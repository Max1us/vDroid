package io.horizontalsystems.bitcoinkit.managers

import io.horizontalsystems.bitcoinkit.RealmFactoryMock
import io.horizontalsystems.bitcoinkit.network.MainNet
import io.horizontalsystems.bitcoinkit.network.RegTest
import io.realm.Realm
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class StateManagerTest {

    private val factory = RealmFactoryMock()
    private lateinit var realm: Realm

    private lateinit var stateManager: StateManager

    @Before
    fun setUp() {
        realm = factory.realmFactory.realm
        realm.executeTransaction { it.deleteAll() }

        stateManager = StateManager(factory.realmFactory, MainNet(), newWallet = false)
    }

    @Test
    fun apiSynced_RegTest() {
        assertFalse(stateManager.restored)

        stateManager = StateManager(factory.realmFactory, RegTest(), newWallet = false)
        assertTrue(stateManager.restored)
    }

    @Test
    fun apiSynced_newWallet() {
        assertFalse(stateManager.restored)

        stateManager = StateManager(factory.realmFactory, MainNet(), newWallet = true)
        assertTrue(stateManager.restored)
    }

    @Test
    fun apiSynced_SetTrue() {
        stateManager.restored = true

        assertTrue(stateManager.restored)
    }

    @Test
    fun apiSynced_NotSet() {
        assertFalse(stateManager.restored)
    }

    @Test
    fun apiSynced_Update() {
        stateManager.restored = true
        assertTrue(stateManager.restored)

        stateManager.restored = false
        assertFalse(stateManager.restored)
    }

}
