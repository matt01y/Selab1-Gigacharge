package be.ugent.gigacharge

import be.ugent.gigacharge.data.ProfileRepository
import be.ugent.gigacharge.model.service.AccountService
import org.junit.Before
import org.mockito.kotlin.mock

class ProfileRepositoryTest {
    private lateinit var repo: ProfileRepository
    private lateinit var accountService: AccountService

    @Before
    fun setup() {
        accountService = mock()
        repo = ProfileRepository(accountService)
    }

//    @Test
//    fun getProfileShouldReturnHiddenProfileState() = runTest {
//        assertEquals(ProfileState.Hidden, repo.getProfile().take(1).single())
//    }
//
//    @Test
//    fun getProfileShouldReturnShownProfile() = runTest {
//        repo.toggleProfile()
//        assertTrue(when (repo.getProfile().take(1).single()) {
//            is ProfileState.Hidden -> false
//            else -> true
//        })
//    }
}