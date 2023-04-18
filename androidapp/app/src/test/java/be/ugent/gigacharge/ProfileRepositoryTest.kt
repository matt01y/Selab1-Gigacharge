package be.ugent.gigacharge

import be.ugent.gigacharge.data.ProfileRepository
import be.ugent.gigacharge.data.local.models.ProfileState
import be.ugent.gigacharge.model.service.QueueService
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock

class ProfileRepositoryTest {
    private lateinit var queueService: QueueService
    private lateinit var repo: ProfileRepository

    @Before
    fun setup() {
        queueService = mock()
        repo = ProfileRepository(queueService)
    }

    @Test(expected = Throwable::class)
    fun getProfileShouldReturnEmptyFlow() = runTest {
        assertNotNull(repo.getProfile().firstOrNull())
    }

    @Test
    fun getProfileShouldReturnHiddenProfileState() = runTest {
        assertEquals(ProfileState.Hidden, repo.getProfile().take(1).single())
    }

    @Test
    fun getProfileShouldReturnShownProfile() = runTest {
        repo.toggleProfile()
        assertTrue(when (repo.getProfile().take(1).single()) {
            is ProfileState.Hidden -> false
            else -> true
        })
    }
}