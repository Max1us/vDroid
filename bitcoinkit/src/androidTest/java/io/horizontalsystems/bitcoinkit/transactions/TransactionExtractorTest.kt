package io.horizontalsystems.bitcoinkit.transactions

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.whenever
import helpers.Fixtures
import io.horizontalsystems.bitcoinkit.RealmFactoryMock
import io.horizontalsystems.bitcoinkit.core.hexStringToByteArray
import io.horizontalsystems.bitcoinkit.core.toHexString
import io.horizontalsystems.bitcoinkit.models.*
import io.horizontalsystems.bitcoinkit.transactions.scripts.ScriptType
import io.horizontalsystems.bitcoinkit.utils.AddressConverter
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.mock

class TransactionExtractorTest {
    private val addressConverter = mock(AddressConverter::class.java)
    private val realmFactoryMock = RealmFactoryMock()
    private val realm = realmFactoryMock.realmFactory.realm

    private lateinit var transactionOutput: TransactionOutput
    private lateinit var transactionInput: TransactionInput
    private lateinit var transaction: Transaction
    private lateinit var extractor: TransactionExtractor

    @Before
    fun setup() {
        transactionOutput = TransactionOutput()
        transactionInput = TransactionInput()
        transaction = Transaction().apply {
            outputs.add(transactionOutput)
            inputs.add(transactionInput)
        }

        extractor = TransactionExtractor(addressConverter)
    }

    //
    // Input
    //

    @Test
    fun extractInputs_P2SH() {
        val address = LegacyAddress("00112233", byteArrayOf(1), AddressType.P2SH)
        val signScript = "004830450221008c203a0881f75c731d9a3a2e6d2ffa37da7095b7dde61a9e7a906659219cd0fa02202677097ca7f7e164f73924fe8f84e1e6fc6611450efcda360ce771e98af9f73d0147304402201cba9b641483476f67a4cef08d7280f51de8d7615fcce76642d944dc07132a990220323d13175477bbf67c8c36fb243bec0e4c410bc9173a186d9f8e98ce3445363601475221025b64f7c63e30f315259393f64dcca269d18386997b1cc93da1388c4021e3ea8e210386d42d5d7027ac08ddcbb066e2140575091fe7dc1d202a008eb5e036725e975652ae"

        whenever(addressConverter.convert(any(), any())).thenReturn(address)

        transactionInput.sigScript = signScript.hexStringToByteArray()
        extractor.extractInputs(transaction)

        assertEquals(address.hash, transaction.inputs[0]?.keyHash)
        assertEquals(address.string, transaction.inputs[0]?.address)
    }

    @Test
    fun extractInputs_P2PKH() {
        val address = LegacyAddress("00112233", byteArrayOf(1), AddressType.P2PKH)
        val signScript = "483045022100907103d70cd2215bc76e27e07cafa39e975cbf4a7f5897402883dbd59b42ed5e022000bbaeb898d2f5c687a420ad51e001080035ee9690b19d6af4bc192f1e0a8b17012103aac540428b6955a53bb01fcae6d4279df45253b2c61684fb993b5545935dac7a"

        whenever(addressConverter.convert(any(), any())).thenReturn(address)

        transactionInput.sigScript = signScript.hexStringToByteArray()
        extractor.extractInputs(transaction)

        assertEquals(address.hash, transaction.inputs[0]?.keyHash)
        assertEquals(address.string, transaction.inputs[0]?.address)
    }

    @Test
    fun extractInputs_P2WPKHSH() {
        val address = LegacyAddress("00112233", byteArrayOf(1), AddressType.P2SH)
        val signScript = "1600148749115073ad59a6f3587f1f9e468adedf01473f"

        whenever(addressConverter.convert(any(), any())).thenReturn(address)

        transactionInput.sigScript = signScript.hexStringToByteArray()
        extractor.extractInputs(transaction)

        assertEquals(address.hash, transaction.inputs[0]?.keyHash)
        assertEquals(address.string, transaction.inputs[0]?.address)
    }

    //
    // Output
    //

    @Test
    fun extractOutputs_P2PKH() {
        assertNull(transaction.outputs[0]?.keyHash)

        val keyHash = "1ec865abcb88cec71c484d4dadec3d7dc0271a7b"
        transactionOutput.lockingScript = "76a914${keyHash}88AC".hexStringToByteArray()
        extractor.extractOutputs(transaction, realm)

        assertEquals(keyHash, transaction.outputs[0]?.keyHash?.toHexString())
        assertEquals(ScriptType.P2PKH, transaction.outputs[0]?.scriptType)
    }

    @Test
    fun extractOutputs_P2PK() {
        assertNull(transaction.outputs[0]?.keyHash)

        val keyHash = "037d56797fbe9aa506fc263751abf23bb46c9770181a6059096808923f0a64cb15"
        transactionOutput.lockingScript = "21${keyHash}AC".hexStringToByteArray()
        extractor.extractOutputs(transaction, realm)

        assertEquals(keyHash, transaction.outputs[0]?.keyHash?.toHexString())
        assertEquals(ScriptType.P2PK, transaction.outputs[0]?.scriptType)
    }

    @Test
    fun extractOutputs_P2SH() {
        assertNull(transaction.outputs[0]?.keyHash)

        val keyHash = "bd82ef4973ebfcbc8f7cb1d540ef0503a791970b"
        transactionOutput.lockingScript = "A914${keyHash}87".hexStringToByteArray()
        extractor.extractOutputs(transaction, realm)

        assertEquals(keyHash, transaction.outputs[0]?.keyHash?.toHexString())
        assertEquals(ScriptType.P2SH, transaction.outputs[0]?.scriptType)
    }

    @Test
    fun extractOutputs_P2WPKH() {
        assertNull(transaction.outputs[0]?.keyHash)

        val keyHash = "00148749115073ad59a6f3587f1f9e468adedf01473f".hexStringToByteArray()
        transactionOutput.lockingScript = keyHash
        extractor.extractOutputs(transaction, realm)

        assertEquals(keyHash, transaction.outputs[0]?.keyHash)
        assertEquals(ScriptType.P2WPKH, transaction.outputs[0]?.scriptType)
    }

    //
    // Old e2e tests
    //
    @Test
    fun extractP2PKH() {
        transaction = Fixtures.transactionP2PKH

        assertNull(transaction.inputs[0]?.keyHash)
        assertNull(transaction.outputs[0]?.keyHash)
        assertNull(transaction.outputs[1]?.keyHash)

        extractor.extractOutputs(transaction, realm)

        // output
        assertEquals(ScriptType.P2PKH, transaction.outputs[0]?.scriptType)
        assertEquals(ScriptType.P2PKH, transaction.outputs[1]?.scriptType)
        assertEquals("37a9bfe84d9e4883ace248509bbf14c9d72af017", transaction.outputs[0]?.keyHash?.toHexString())
        assertEquals("37a9bfe84d9e4883ace248509bbf14c9d72af017", transaction.outputs[1]?.keyHash?.toHexString())

        // // input
        // assertEquals("f6889a22593e9156ef80bdcda0e1b355e8949e05", transaction.inputs[0]?.keyHash?.toHexString())
        // // address
        // assertEquals("n3zWAXKu6LBa8qYGEuTEfg9RXeijRHj5rE", transaction.inputs[0]?.address)
        // assertEquals("mkbGp1uE1jRfdNxtWAUTGWKc9r2pRsLiUi", transaction.outputs[0]?.address)
        // assertEquals("mkbGp1uE1jRfdNxtWAUTGWKc9r2pRsLiUi", transaction.outputs[1]?.address)
    }

    @Test
    fun extractP2SH() {
        transaction = Fixtures.transactionP2SH

        assertNull(transaction.inputs[0]?.keyHash)
        assertNull(transaction.outputs[0]?.keyHash)
        assertNull(transaction.outputs[1]?.keyHash)

        extractor.extractOutputs(transaction, realm)

        // output
        assertEquals(ScriptType.P2SH, transaction.outputs[0]?.scriptType)
        assertEquals(ScriptType.P2SH, transaction.outputs[1]?.scriptType)
        assertEquals("cdfb2eb01489e9fe8bd9b878ce4a7084dd887764", transaction.outputs[0]?.keyHash?.toHexString())
        assertEquals("aed6f804c63da80800892f8fd4cdbad0d3ad6d12", transaction.outputs[1]?.keyHash?.toHexString())

        // // input
        // assertEquals("aed6f804c63da80800892f8fd4cdbad0d3ad6d12", transaction.inputs[0]?.keyHash?.toHexString())
        // // address
        // assertEquals("2N9Bh5xXL1CdQohpcqPiphdqtQGuAquWuaG", transaction.inputs[0]?.address)
        // assertEquals("2NC2MR4p1VsHCgAAo8C5KPmyKhuY6rb6SGN", transaction.outputs[0]?.address)
        // assertEquals("2N9Bh5xXL1CdQohpcqPiphdqtQGuAquWuaG", transaction.outputs[1]?.address)
    }

    @Test
    fun extractP2PK() {
        transaction = Fixtures.transactionP2PK

        assertNull(transaction.inputs[0]?.keyHash)
        assertNull(transaction.outputs[0]?.keyHash)
        assertNull(transaction.outputs[1]?.keyHash)

        extractor.extractOutputs(transaction, realm)

        assertEquals(ScriptType.P2PK, transaction.outputs[0]?.scriptType)
        assertEquals("04ae1a62fe09c5f51b13905f07f06b99a2f7159b2225f374cd378d71302fa28414e7aab37397f554a7df5f142c21c1b7303b8a0626f1baded5c72a704f7e6cd84c", transaction.outputs[0]?.keyHash?.toHexString())
        assertEquals("0411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3", transaction.outputs[1]?.keyHash?.toHexString())

        // // address
        // assertEquals("", transaction.inputs[0]?.address)
        // assertEquals("n4YQoLK25P4RsJ2wJEpKnT6q2WGxt149rs", transaction.outputs[0]?.address)
        // assertEquals("mh8YhPYEAYs3E7EVyKtB5xrcfMExkkdEMF", transaction.outputs[1]?.address)
    }
}
