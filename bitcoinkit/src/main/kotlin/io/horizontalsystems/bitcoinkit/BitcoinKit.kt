package io.horizontalsystems.bitcoinkit

import android.content.Context
import io.horizontalsystems.bitcoinkit.blocks.BlockSyncer
import io.horizontalsystems.bitcoinkit.blocks.Blockchain
import io.horizontalsystems.bitcoinkit.core.DataProvider
import io.horizontalsystems.bitcoinkit.core.KitStateProvider
import io.horizontalsystems.bitcoinkit.core.RealmFactory
import io.horizontalsystems.bitcoinkit.core.toHexString
import io.horizontalsystems.bitcoinkit.managers.*
import io.horizontalsystems.bitcoinkit.models.BitcoinPaymentData
import io.horizontalsystems.bitcoinkit.models.BlockInfo
import io.horizontalsystems.bitcoinkit.models.PublicKey
import io.horizontalsystems.bitcoinkit.models.TransactionInfo
import io.horizontalsystems.bitcoinkit.network.*
import io.horizontalsystems.bitcoinkit.network.peer.PeerGroup
import io.horizontalsystems.bitcoinkit.network.peer.PeerHostManager
import io.horizontalsystems.bitcoinkit.transactions.*
import io.horizontalsystems.bitcoinkit.transactions.builder.TransactionBuilder
import io.horizontalsystems.bitcoinkit.transactions.scripts.OpCodes
import io.horizontalsystems.bitcoinkit.transactions.scripts.ScriptType
import io.horizontalsystems.bitcoinkit.utils.AddressConverter
import io.horizontalsystems.bitcoinkit.utils.PaymentAddressParser
import io.horizontalsystems.hdwalletkit.HDWallet
import io.horizontalsystems.hdwalletkit.Mnemonic
import io.reactivex.Single
import io.realm.Realm
import io.realm.annotations.RealmModule
import java.util.concurrent.Executor
import java.util.concurrent.Executors

@RealmModule(library = true, allClasses = true)
class BitcoinKitModule

class BitcoinKit(seed: ByteArray, networkType: NetworkType, walletId: String? = null, peerSize: Int = 10, newWallet: Boolean = false, confirmationsThreshold: Int = 6) : KitStateProvider.Listener, DataProvider.Listener {

    interface Listener {
        fun onTransactionsUpdate(bitcoinKit: BitcoinKit, inserted: List<TransactionInfo>, updated: List<TransactionInfo>)
        fun onTransactionsDelete(hashes: List<String>)
        fun onBalanceUpdate(bitcoinKit: BitcoinKit, balance: Long)
        fun onLastBlockInfoUpdate(bitcoinKit: BitcoinKit, blockInfo: BlockInfo)
        fun onKitStateUpdate(bitcoinKit: BitcoinKit, state: KitState)
    }

    var listener: Listener? = null
    var listenerExecutor: Executor = Executors.newSingleThreadExecutor()

    //  DataProvider getters
    val balance get() = dataProvider.balance
    val lastBlockInfo get() = dataProvider.lastBlockInfo

    private val peerGroup: PeerGroup
    private val initialSyncer: InitialSyncer
    private val feeRateSyncer: FeeRateSyncer
    private val addressManager: AddressManager
    private val addressConverter: AddressConverter
    private val paymentAddressParser: PaymentAddressParser
    private val transactionCreator: TransactionCreator
    private val transactionBuilder: TransactionBuilder
    private val dataProvider: DataProvider
    private val unspentOutputProvider: UnspentOutputProvider
    private val realmFactory = RealmFactory("bitcoinkit-${networkType.name}-$walletId")

    private val network = when (networkType) {
        NetworkType.MainNet -> MainNet()
        NetworkType.MainNetBitCash -> MainNetBitcoinCash()
        NetworkType.TestNet -> TestNet()
        NetworkType.TestNetBitCash -> TestNetBitcoinCash()
        NetworkType.RegTest -> RegTest()
    }

    constructor(words: List<String>, networkType: NetworkType, walletId: String? = null, peerSize: Int = 10, newWallet: Boolean = false, confirmationsThreshold: Int = 6) :
            this(Mnemonic().toSeed(words), networkType, walletId, peerSize, newWallet, confirmationsThreshold)

    init {
        val wallet = HDWallet(seed, network.coinType)

        unspentOutputProvider = UnspentOutputProvider(realmFactory, confirmationsThreshold)
        dataProvider = DataProvider(realmFactory, this, unspentOutputProvider)
        addressConverter = AddressConverter(network)
        addressManager = AddressManager(realmFactory, wallet, addressConverter)

        val kitStateProvider = KitStateProvider(this)
        val peerHostManager = PeerHostManager(network, realmFactory)
        val transactionLinker = TransactionLinker()
        val transactionExtractor = TransactionExtractor(addressConverter)
        val transactionProcessor = TransactionProcessor(transactionExtractor, transactionLinker, addressManager, dataProvider)
        val bloomFilterManager = BloomFilterManager(realmFactory)
        val addressSelector: IAddressSelector

        peerGroup = PeerGroup(peerHostManager, bloomFilterManager, network, kitStateProvider, peerSize)
        peerGroup.blockSyncer = BlockSyncer(realmFactory, Blockchain(network, dataProvider), transactionProcessor, addressManager, bloomFilterManager, kitStateProvider, network)
        peerGroup.transactionSyncer = TransactionSyncer(realmFactory, transactionProcessor, addressManager, bloomFilterManager)
        peerGroup.connectionManager = connectionManager

        when (networkType) {
            NetworkType.MainNet,
            NetworkType.TestNet,
            NetworkType.RegTest -> {
                addressSelector = BitcoinAddressSelector(addressConverter)
                paymentAddressParser = PaymentAddressParser("bitcoin", removeScheme = true)
            }
            NetworkType.MainNetBitCash,
            NetworkType.TestNetBitCash -> {
                addressSelector = BitcoinCashAddressSelector(addressConverter)
                paymentAddressParser = PaymentAddressParser("bitcoincash", removeScheme = false)
            }
        }

        val stateManager = StateManager(realmFactory, network, newWallet)
        val initialSyncerApi = InitialSyncerApi(wallet, addressSelector, network)

        feeRateSyncer = FeeRateSyncer(realmFactory, ApiFeeRate(networkType), connectionManager)
        initialSyncer = InitialSyncer(realmFactory, initialSyncerApi, stateManager, addressManager, peerGroup, kitStateProvider)
        transactionBuilder = TransactionBuilder(realmFactory, addressConverter, wallet, network, addressManager, unspentOutputProvider)
        transactionCreator = TransactionCreator(realmFactory, transactionBuilder, transactionProcessor, peerGroup)
    }

    //
    // API methods
    //
    fun start() {
        initialSyncer.sync()
        feeRateSyncer.start()
    }

    fun stop() {
        initialSyncer.stop()
        feeRateSyncer.stop()
        dataProvider.clear()
    }

    fun clear() {
        stop()
        realmFactory.realm.use { realm ->
            realm.executeTransaction { it.deleteAll() }
        }
    }

    fun refresh() {
        start()
    }

    fun transactions(fromHash: String? = null, limit: Int? = null): Single<List<TransactionInfo>> {
        return dataProvider.transactions(fromHash, limit)
    }

    fun fee(value: Long, address: String? = null, senderPay: Boolean = true): Long {
        return transactionBuilder.fee(value, dataProvider.feeRate.medium, senderPay, address)
    }

    fun send(address: String, value: Long, senderPay: Boolean = true) {
        transactionCreator.create(address, value, dataProvider.feeRate.medium, senderPay)
    }

    fun receiveAddress(): String {
        return addressManager.receiveAddress()
    }

    fun validateAddress(address: String) {
        addressConverter.convert(address)
    }

    fun parsePaymentAddress(paymentAddress: String): BitcoinPaymentData {
        return paymentAddressParser.parse(paymentAddress)
    }

    fun showDebugInfo() {
        addressManager.fillGap()
        realmFactory.realm.use { realm ->
            realm.where(PublicKey::class.java).findAll().forEach { pubKey ->
                try {
                    val scriptType = if (network is MainNetBitcoinCash || network is TestNetBitcoinCash)
                        ScriptType.P2PKH else
                        ScriptType.P2WPKH

                    val legacy = addressConverter.convert(pubKey.publicKeyHash, ScriptType.P2PKH).string
                    val wpkh = addressConverter.convert(pubKey.scriptHashP2WPKH, ScriptType.P2SH).string
                    val bechAddress = try {
                        addressConverter.convert(OpCodes.push(0) + OpCodes.push(pubKey.publicKeyHash), scriptType).string
                    } catch (e: Exception) {
                        ""
                    }
                    println("${pubKey.index} --- extrnl: ${pubKey.external} --- hash: ${pubKey.publicKeyHex} --- p2wkph(SH) hash: ${pubKey.scriptHashP2WPKH.toHexString()}")
                    println("legacy: $legacy --- bech32: $bechAddress --- SH(WPKH): $wpkh")
                } catch (e: Exception) {
                    println(e.message)
                }
            }
        }
    }

    //
    // DataProvider Listener implementations
    //
    override fun onTransactionsUpdate(inserted: List<TransactionInfo>, updated: List<TransactionInfo>) {
        listenerExecutor.execute {
            listener?.onTransactionsUpdate(this, inserted, updated)
        }
    }

    override fun onTransactionsDelete(hashes: List<String>) {
        listenerExecutor.execute {
            listener?.onTransactionsDelete(hashes)
        }
    }

    override fun onBalanceUpdate(balance: Long) {
        listenerExecutor.execute {
            listener?.onBalanceUpdate(this, balance)
        }
    }

    override fun onLastBlockInfoUpdate(blockInfo: BlockInfo) {
        listenerExecutor.execute {
            listener?.onLastBlockInfoUpdate(this, blockInfo)
        }
    }

    //
    // KitStateProvider Listener implementations
    //
    override fun onKitStateUpdate(state: KitState) {
        listenerExecutor.execute {
            listener?.onKitStateUpdate(this, state)
        }
    }

    enum class NetworkType {
        MainNet,
        TestNet,
        RegTest,
        MainNetBitCash,
        TestNetBitCash
    }

    sealed class KitState {
        object Synced : KitState()
        object NotSynced : KitState()
        class Syncing(val progress: Double) : KitState()
    }

    companion object {
        private var connectionManager: ConnectionManager? = null

        fun init(context: Context) {
            connectionManager = ConnectionManager(context)
            Realm.init(context)
        }
    }
}
