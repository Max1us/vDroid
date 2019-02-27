package io.horizontalsystems.bitcoinkit.transactions

import io.horizontalsystems.bitcoinkit.blocks.IBlockchainDataListener
import io.horizontalsystems.bitcoinkit.core.inTopologicalOrder
import io.horizontalsystems.bitcoinkit.managers.AddressManager
import io.horizontalsystems.bitcoinkit.managers.BloomFilterManager
import io.horizontalsystems.bitcoinkit.models.Block
import io.horizontalsystems.bitcoinkit.models.Transaction
import io.horizontalsystems.bitcoinkit.transactions.scripts.ScriptType
import io.realm.Realm
import java.util.*

class TransactionProcessor(private val extractor: TransactionExtractor, private val linker: TransactionLinker, private val addressManager: AddressManager,
                           private val dataListener: IBlockchainDataListener) {

    fun processOutgoing(transaction: Transaction, realm: Realm) {
        realm.executeTransaction {
            process(transaction, realm)
            val managedTransaction = realm.copyToRealm(transaction)

            dataListener.onTransactionsUpdate(listOf(managedTransaction), listOf())
        }
    }

    private fun process(transaction: Transaction, realm: Realm) {
        extractor.extractOutputs(transaction, realm)
        linker.handle(transaction, realm)

        if (transaction.isMine) {
            extractor.extractAddress(transaction)
            extractor.extractInputs(transaction)
        }
    }

    @Throws(BloomFilterManager.BloomFilterExpired::class)
    fun process(transactions: List<Transaction>, block: Block?, skipCheckBloomFilter: Boolean, realm: Realm) {
        var needToUpdateBloomFilter = false

        val inserted = mutableListOf<Transaction>()
        val updated = mutableListOf<Transaction>()

        for ((index, transaction) in transactions.inTopologicalOrder().withIndex()) {
            val transactionInDB = realm.where(Transaction::class.java).equalTo("hashHexReversed", transaction.hashHexReversed).findFirst()

            if (transactionInDB != null) {
                relay(transactionInDB, index, block)

                updated.add(transactionInDB)
                continue
            }

            process(transaction, realm)

            if (transaction.isMine) {
                relay(transaction, index, block)
                inserted.add(realm.copyToRealm(transaction))

                if (!skipCheckBloomFilter) {
                    needToUpdateBloomFilter = needToUpdateBloomFilter || addressManager.gapShifts(realm) || hasUnspentOutputs(transaction)
                }
            }
        }

        if (inserted.isNotEmpty() || updated.isNotEmpty()) {
            dataListener.onTransactionsUpdate(inserted, updated)
        }

        if (needToUpdateBloomFilter) {
            throw BloomFilterManager.BloomFilterExpired
        }
    }

    private fun relay(transaction: Transaction, order: Int, block: Block?) {
        transaction.block = block
        transaction.status = Transaction.Status.RELAYED
        transaction.timestamp = block?.header?.timestamp ?: (Date().time / 1000)
        transaction.order = order
    }

    private fun hasUnspentOutputs(transaction: Transaction): Boolean {
        return transaction.outputs.any { output ->
            output.publicKey != null && (output.scriptType == ScriptType.P2PK || output.scriptType == ScriptType.P2WPKH)
        }
    }

}
