package be.ugent.gigacharge

import be.ugent.gigacharge.data.ProfileRepository
import be.ugent.gigacharge.model.service.AccountService
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify

class ProfileRepositoryTest {
    private lateinit var repo: ProfileRepository
    private lateinit var accountService: AccountService

    @Before
    fun setup() {
        accountService = mock()
        repo = ProfileRepository(accountService)
    }

    @Test
    fun deleteProfileShouldCallAccountServiceDOTDeleteProfile() = runTest {
        repo.deleteProfile()
        verify(accountService).deleteProfile()
    }
    @Test
    fun isVisibleFlowShouldBeFalseAtStart() = runTest {
        assertFalse(repo.getIsVisibleFlow().first())
    }

    @Test
    fun isVisibleFlowShouldChangeToTrueWhenToggleProfileIsCalled() = runTest {
        repo.toggleProfile()
        assertTrue(repo.getIsVisibleFlow().value)
    }

    @Test
    fun isVisibleFlowShouldChangeBackWhenCalledTwice() = runTest {
        runBlocking { repo.toggleProfile() }
        runBlocking { repo.toggleProfile() }
        assertFalse(repo.getIsVisibleFlow().value)
    }

    @Test
    fun passedActionShouldBeCalledIfAccountServiceDOTIsNeabledObserversDOTAddPassesTrue() = runTest {
        val action: ()->Unit = mock()
        repo.registerUser(action, "")
        verify(action, times(1))
    }
//    @Test
//    fun registerUserShouldCallAccountSerciceDotSendTokenIfTokenIsNotNull() = runTest {
//        repo.registerUser({}, "")
//        verify(accountService, times(0)).sendToken(any())
//    }

    @Test
    fun registerUserShouldCallAccountServiceDotTryEnableTestA() = runTest {
        repo.registerUser({}, "")
        verify(accountService).tryEnable("")
    }

    @Test
    fun registerUserShouldCallAccountServiceDotTryEnableTestB() = runTest {
        repo.registerUser({}, "b")
        verify(accountService).tryEnable("b")
    }
}