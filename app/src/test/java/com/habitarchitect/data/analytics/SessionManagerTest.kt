package com.habitarchitect.data.analytics

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
class SessionManagerTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var testDataStore: DataStore<Preferences>
    private lateinit var context: Context
    private lateinit var sessionManager: SessionManager

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)

        // Create a test DataStore
        val testFile = File(tempFolder.root, "test_session.preferences_pb")
        testDataStore = PreferenceDataStoreFactory.create(
            scope = CoroutineScope(testDispatcher + SupervisorJob()),
            produceFile = { testFile }
        )

        context = mockk(relaxed = true)
        sessionManager = SessionManager(context)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getSessionId returns new session ID on first call`() = runTest(testDispatcher) {
        // Note: This test would need a proper test setup with DataStore
        // For now, we test the basic contract
        val sessionId = sessionManager.getSessionId()
        assertNotNull(sessionId)
        assertTrue(sessionId.isNotBlank())
    }

    @Test
    fun `getSessionId returns same ID on consecutive calls`() = runTest(testDispatcher) {
        val firstId = sessionManager.getSessionId()
        val secondId = sessionManager.getSessionId()
        assertEquals(firstId, secondId)
    }

    @Test
    fun `startNewSession creates different session ID`() = runTest(testDispatcher) {
        val firstId = sessionManager.getSessionId()
        val secondId = sessionManager.startNewSession()
        assertNotEquals(firstId, secondId)
    }

    @Test
    fun `getSessionDuration returns positive value`() = runTest(testDispatcher) {
        sessionManager.getSessionId() // Initialize session
        val duration = sessionManager.getSessionDuration()
        assertTrue(duration >= 0)
    }

    @Test
    fun `SESSION_TIMEOUT_MS is 30 minutes`() {
        assertEquals(30 * 60 * 1000L, SessionManager.SESSION_TIMEOUT_MS)
    }

    @Test
    fun `isSessionExpired returns false for recent activity`() = runTest(testDispatcher) {
        sessionManager.getSessionId()
        sessionManager.recordActivity()
        val isExpired = sessionManager.isSessionExpired()
        assertTrue(!isExpired)
    }

    @Test
    fun `isSessionExpired returns true for old activity`() = runTest(testDispatcher) {
        sessionManager.getSessionId()
        // Simulate time passing beyond timeout
        val futureTime = System.currentTimeMillis() + SessionManager.SESSION_TIMEOUT_MS + 1000
        val isExpired = sessionManager.isSessionExpired(futureTime)
        assertTrue(isExpired)
    }
}
