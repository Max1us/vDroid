package io.horizontalsystems.bitcoinkit.models

import io.horizontalsystems.bitcoinkit.utils.HashUtils
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.LinkingObjects
import io.realm.annotations.PrimaryKey

/**
 * Block
 *
 *  Size        Field           Description
 *  ====        =====           ===========
 *  80 bytes    Header          Consists of 6 fields that are hashed to calculate the block hash
 *  VarInt      TxCount         Number of transactions in the block
 *  Variable    Transactions    The transactions in the block
 */
open class Block() : RealmObject() {

    @PrimaryKey
    var reversedHeaderHashHex = ""

    var height: Int = 0
    var header: Header? = null
    var headerHash: ByteArray = byteArrayOf()
    var previousBlock: Block? = null
    var stale = false

    @LinkingObjects("block")
    val transactions: RealmResults<Transaction>? = null

    constructor(header: Header, previousBlock: Block) : this() {
        this.header = header
        this.headerHash = header.hash
        this.reversedHeaderHashHex = HashUtils.toHexString(this.headerHash.reversedArray())
        this.previousBlock = previousBlock
        this.height = previousBlock.height + 1
    }

    constructor(header: Header, height: Int) : this() {
        this.header = header
        this.headerHash = header.hash
        this.reversedHeaderHashHex = HashUtils.toHexString(this.headerHash.reversedArray())
        this.height = height
    }

    constructor(headerHash: ByteArray, height: Int) : this() {
        this.headerHash = headerHash
        this.reversedHeaderHashHex = HashUtils.toHexString(this.headerHash.reversedArray())
        this.height = height
    }

}
