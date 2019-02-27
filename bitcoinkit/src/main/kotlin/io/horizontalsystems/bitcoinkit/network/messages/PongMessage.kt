package io.horizontalsystems.bitcoinkit.network.messages

import io.horizontalsystems.bitcoinkit.io.BitcoinInput
import io.horizontalsystems.bitcoinkit.io.BitcoinOutput
import java.io.ByteArrayInputStream
import java.io.IOException

/**
 * Pong Message
 *
 *  Size        Field   Description
 *  ====        =====   ===========
 *  8 bytes     Nonce   Random value
 */
class PongMessage : Message {

    var nonce: Long = 0

    constructor(nonce: Long) : super("pong") {
        this.nonce = nonce
    }

    @Throws(IOException::class)
    constructor(payload: ByteArray) : super("pong") {
        BitcoinInput(ByteArrayInputStream(payload)).use { input ->
            nonce = input.readLong()
        }
    }

    override fun getPayload(): ByteArray {
        return BitcoinOutput().writeLong(nonce).toByteArray()
    }

    override fun toString(): String {
        return "PongMessage(nonce=$nonce)"
    }
}
