package io.horizontalsystems.bitcoinkit.utils

import io.horizontalsystems.bitcoinkit.core.hexStringToByteArray
import io.horizontalsystems.bitcoinkit.models.Address
import io.horizontalsystems.bitcoinkit.models.AddressType
import io.horizontalsystems.bitcoinkit.transactions.scripts.ScriptType
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test

class SegwitAddressConverterTest {
    private lateinit var converter: SegwitAddressConverter
    private lateinit var bytes: ByteArray
    private lateinit var program: ByteArray
    private lateinit var addressString: String
    private lateinit var address: Address

    @Test
    fun convert_P2WPKH() {
        addressString = "bc1qw508d6qejxtdg4y5r3zarvary0c5xw7kv8f3t4"
        program = "751e76e8199196d454941c45d1b3a323f1433bd6".hexStringToByteArray()
        bytes = "0014".hexStringToByteArray() + program

        converter = SegwitAddressConverter()
        address = converter.convert("bc", bytes, ScriptType.P2WPKH)

        assertEquals(AddressType.WITNESS, address.type)
        assertEquals(addressString, address.string)
        assertArrayEquals(program, address.hash)
    }

    @Test
    fun convert_P2WSH() {
        addressString = "tb1qrp33g0q5c5txsp9arysrx4k6zdkfs4nce4xj0gdcccefvpysxf3q0sl5k7"
        program = "1863143c14c5166804bd19203356da136c985678cd4d27a1b8c6329604903262".hexStringToByteArray()
        bytes = "00201863143c14c5166804bd19203356da136c985678cd4d27a1b8c6329604903262".hexStringToByteArray()

        converter = SegwitAddressConverter()
        address = converter.convert("tb", bytes, ScriptType.P2WSH)

        assertEquals(AddressType.WITNESS, address.type)
        assertEquals(addressString, address.string)
        assertArrayEquals(program, address.hash)
    }

    @Test
    fun convert_witness1() {
        addressString = "bc1pw508d6qejxtdg4y5r3zarvary0c5xw7kw508d6qejxtdg4y5r3zarvary0c5xw7k7grplx"
        program = "751e76e8199196d454941c45d1b3a323f1433bd6751e76e8199196d454941c45d1b3a323f1433bd6".hexStringToByteArray()
        bytes = "5128751e76e8199196d454941c45d1b3a323f1433bd6751e76e8199196d454941c45d1b3a323f1433bd6".hexStringToByteArray()

        converter = SegwitAddressConverter()
        address = converter.convert("bc", bytes, ScriptType.P2WPKH)

        assertEquals(AddressType.WITNESS, address.type)
        assertEquals(addressString, address.string)
        assertArrayEquals(program, address.hash)
    }

}
