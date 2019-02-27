package helpers

import io.horizontalsystems.bitcoinkit.core.hexStringToByteArray
import io.horizontalsystems.bitcoinkit.core.toHexString
import io.horizontalsystems.bitcoinkit.models.*
import io.horizontalsystems.bitcoinkit.transactions.scripts.ScriptType
import io.horizontalsystems.bitcoinkit.utils.HashUtils

object Fixtures {

    val checkpointBlock1
        get() = Block().apply {
            height = 0 // 536256
            header = Header().apply {
                version = 536870912
                prevHash = HashUtils.toBytesAsLE("00000000000000000000943de85f4495f053ff55f27d135edc61c27990c2eec5")
                merkleHash = HashUtils.toBytesAsLE("167bf70981d49388d07881b1a448ff9b79cf2a32716e45c535345823d8cdd541")
                timestamp = 1533980459
                bits = 388763047
                nonce = 1545867530
            }
        }

    val block1
        get () = Block().apply {
            height = 2013 // 538269
            header = Header().apply {
                version = 536870912
                prevHash = HashUtils.toBytesAsLE("00000000000000000011206e641083b68ffc41b7fe6ee1af4a5d69995d1b2d0e")
                merkleHash = HashUtils.toBytesAsLE("5510c0c3d1fd9d2b56a34aab98c29860015caf248fa62a1907b197ddec17c788")
                timestamp = 1535128609
                bits = 388763047
                nonce = 2295801359
            }
            headerHash = header?.hash ?: byteArrayOf()
            reversedHeaderHashHex = HashUtils.toHexString(this.headerHash.reversedArray())
        }

    val block2
        get () = Block().apply {
            height = 2014 // 538270
            header = Header().apply {
                version = 536870912
                prevHash = HashUtils.toBytesAsLE("0000000000000000000a876dbca5804f792afa90b6dc7946dedb5866245d0c55")
                merkleHash = HashUtils.toBytesAsLE("ccf2737e44e435e2e11481755b00d161815a24e605d605a17bf20da49320ad7d")
                timestamp = 1535128839
                bits = 388763047
                nonce = 3401296263
            }
            previousBlock = block1
        }

    val block3
        get() = Block().apply {
            height = 2015 // 538271
            header = Header().apply {
                version = 536870912
                prevHash = HashUtils.toBytesAsLE("000000000000000000124a73e879fd66a1b29d1b4b3f1a81de3cbcbe579e21a8")
                merkleHash = HashUtils.toBytesAsLE("7904930640df999005df3b57f9c6f542088af33c3d773dcec2939f55ced359b8")
                timestamp = 1535129301
                bits = 388763047
                nonce = 59591417
            }
            previousBlock = block2
        }

    val checkpointBlock2
        get() = Block().apply {
            previousBlock = block3
            height = 2016 // 538272
            header = Header().apply {
                version = 536870912
                prevHash = HashUtils.toBytesAsLE("0000000000000000001d9d48d93793aaa85b5f6d17c176d4ef905c7e7112b1cf")
                merkleHash = HashUtils.toBytesAsLE("3ad0fa0e8c100db5831ebea7cabf6addae2c372e6e1d84f6243555df5bbfa351")
                timestamp = 1535129431
                bits = 388618029
                nonce = 2367954839
            }
        }

    //  P2PKH: TestNet tx => 68f297d8a8c9af30cd5a9d6d1eeec5ed3df7be1e4b62f2ced135af6ffe7814c2
    val transactionP2PKH
        get() = Transaction().apply {
            version = 1
            lockTime = 0
            inputs.add(TransactionInput().apply {
                previousOutputHash = "093f5f5c5e57ae2ae9728147547e183e2ef5c9e6e879a78bee6ceb59db2b4797".hexStringToByteArray()
                previousOutputIndex = 1
                sigScript = "473044022018f03676d057a3cb350d9778697ff61da47b813c82fe9fb0f2ea87b231fb865b02200706f5cbbc5ebae6f7bd77e346767bce11c8476aea607671d7321e86a3186ec1012102ce0ef85579f055e2184c935e75e71458db8c4b759cd455b0aa5d91761794eef0".hexStringToByteArray()
                sequence = 4294967295
            })

            outputs.add(TransactionOutput().apply {
                value = 94734191
                index = 0
                lockingScript = "76a91437a9bfe84d9e4883ace248509bbf14c9d72af01788ac".hexStringToByteArray()
                scriptType = ScriptType.UNKNOWN
            })

            outputs.add(TransactionOutput().apply {
                value = 100000
                index = 1
                lockingScript = "76a91437a9bfe84d9e4883ace248509bbf14c9d72af01788ac".hexStringToByteArray()
                scriptType = ScriptType.UNKNOWN
            })

            setHashes()
        }

    //  P2SH: TestNet tx => 761cc7102efe24f4353ae7dc816fbed5e15963d11ca93e36449d521bda21ac4d
    val transactionP2SH
        get() = Transaction().apply {
            version = 1
            lockTime = 0
            inputs.add(TransactionInput().apply {
                previousOutputHash = "b6f0ede9cc38cdbceb91936619f89b648bb912f4c42773567037ea5de164873d".hexStringToByteArray()
                previousOutputIndex = 1
                sigScript = "004830450221008c203a0881f75c731d9a3a2e6d2ffa37da7095b7dde61a9e7a906659219cd0fa02202677097ca7f7e164f73924fe8f84e1e6fc6611450efcda360ce771e98af9f73d0147304402201cba9b641483476f67a4cef08d7280f51de8d7615fcce76642d944dc07132a990220323d13175477bbf67c8c36fb243bec0e4c410bc9173a186d9f8e98ce3445363601475221025b64f7c63e30f315259393f64dcca269d18386997b1cc93da1388c4021e3ea8e210386d42d5d7027ac08ddcbb066e2140575091fe7dc1d202a008eb5e036725e975652ae".hexStringToByteArray()
                sequence = 4294967295
            })

            outputs.add(TransactionOutput().apply {
                value = 617795422
                index = 0
                lockingScript = "a914cdfb2eb01489e9fe8bd9b878ce4a7084dd88776487".hexStringToByteArray()
                scriptType = ScriptType.UNKNOWN
            })

            outputs.add(TransactionOutput().apply {
                value = 1407000
                index = 1
                lockingScript = "a914aed6f804c63da80800892f8fd4cdbad0d3ad6d1287".hexStringToByteArray()
                scriptType = ScriptType.UNKNOWN
            })

            setHashes()
        }

    //  P2PK: TestNet tx => 75b84cb54351866cb5248158735e801d9b2c56592633157ba10d08affa2ffbab
    val transactionP2PK
        get() = Transaction().apply {
            version = 1
            lockTime = 0
            inputs.add(TransactionInput().apply {
                previousOutputHash = "978530798f3979322351c190856d17b9e9e7e470c5be4ce87a60bd7a9f7756ac".hexStringToByteArray()
                previousOutputIndex = 0
                sigScript = "473044022003f9d150b4e291de2825af19dbe1846cc80caf3535d7e9fa03743b2ad019cc47022073294e520c508f702e3ad7a085ecce4a4b311d43faa1e6eb685ec78c002e795d01".hexStringToByteArray()
                sequence = 4294967295
            })

            outputs.add(TransactionOutput().apply {
                value = 1000000000
                index = 0
                lockingScript = "4104ae1a62fe09c5f51b13905f07f06b99a2f7159b2225f374cd378d71302fa28414e7aab37397f554a7df5f142c21c1b7303b8a0626f1baded5c72a704f7e6cd84cac".hexStringToByteArray()
                scriptType = ScriptType.UNKNOWN
            })

            outputs.add(TransactionOutput().apply {
                value = 4000000000
                index = 1
                lockingScript = "410411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3ac".hexStringToByteArray()
                scriptType = ScriptType.UNKNOWN
            })

            setHashes()
        }

    val transactionP2WPKH
        get() = Transaction().apply {
            version = 1
            inputs.add(TransactionInput().apply {
                previousOutputHash = "a6d1ce683f38a84cfd88a9d48b0ba2d7a8def00f8517e3da02c86fce6c7863d7".hexStringToByteArray()
                previousOutputHexReversed = previousOutputHash.reversedArray().toHexString()
                previousOutputIndex = 0
                sigScript = "4730440220302e597d74aebcb0bf7f372be156252017af190bd586466104b079fba4b7efa7022037ebbf84e096ef3d966123a93a83586012353c1d2c11c967d21acf1c94c45df001210347235e12207d21b6093d9fd93a0df4d589a0d44252b98b2e934a8da5ab1d1654".hexStringToByteArray()
                sequence = 4294967295
            })
            outputs.add(TransactionOutput().apply {
                value = 10792000
                index = 0
                lockingScript = "00148749115073ad59a6f3587f1f9e468adedf01473f".hexStringToByteArray()
                scriptType = ScriptType.P2WPKH
                keyHash = byteArrayOf()
            })
            outputs.add(TransactionOutput().apply {
                value = 0
                index = 0
                lockingScript = "6a4c500000b919000189658af37cd16dbd16e4186ea13c5d8e1f40c5b5a0958326067dd923b8fc8f0767f62eb9a7fd57df4f3e775a96ca5b5eabf5057dff98997a3bbd011366703f5e45075f397f7f3c8465da".hexStringToByteArray()
                scriptType = ScriptType.P2PK
                keyHash = byteArrayOf()
            })
            lockTime = 0
            setHashes()
        }

    //  test public key
    val publicKey = PublicKey().apply {
        this.publicKey = "037d56797fbe9aa506fc263751abf23bb46c9770181a6059096808923f0a64cb15".hexStringToByteArray()
        this.publicKeyHash = "e4de5d630c5cacd7af96418a8f35c411c8ff3c06".hexStringToByteArray()
        this.publicKeyHex = "e4de5d630c5cacd7af96418a8f35c411c8ff3c06"
    }

    //  transaction in regtest
    val transactionP2PKH_ForSignatureTest: Transaction
        get() {
            val ownPubKey = publicKey

            val previousOutput = TransactionOutput().apply {
                value = 4_999_900_000
                index = 0
                lockingScript = "76a914e4de5d630c5cacd7af96418a8f35c411c8ff3c0688ac".hexStringToByteArray()
                scriptType = ScriptType.P2PKH
                this.publicKey = ownPubKey
                this.keyHash = ownPubKey.publicKeyHash
            }

            val transaction = Transaction(1, 0)

            val payInput = TransactionInput().apply {
                previousOutputHexReversed = "f296d7192200cd926369d1a8a88c0339c140149602651c2cc2ed5116368eb79c"
                previousOutputHash = "f296d7192200cd926369d1a8a88c0339c140149602651c2cc2ed5116368eb79c".hexStringToByteArray().reversedArray()
                this.previousOutput = previousOutput
                sequence = 4294967295
            }

            val payOutput = TransactionOutput().apply {
                value = 4_999_800_000
                index = 0
                lockingScript = "76a914e4de5d630c5cacd7af96418a8f35c411c8ff3c0688ac".hexStringToByteArray()
                scriptType = ScriptType.P2PKH
            }

            transaction.inputs.add(payInput)
            transaction.outputs.add(payOutput)

            return transaction
        }

}
