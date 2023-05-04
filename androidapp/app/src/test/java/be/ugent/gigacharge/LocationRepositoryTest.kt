package be.ugent.gigacharge

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.test.platform.app.InstrumentationRegistry
import be.ugent.gigacharge.data.LocationRepository
import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.location.QueueState
import be.ugent.gigacharge.model.service.QueueService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import kotlin.coroutines.coroutineContext

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "location")


class LocationRepositoryTest {
    private lateinit var queueService: QueueService
    private lateinit var repo: LocationRepository
    @Before
    fun setup() {
        queueService = mock()
        repo = LocationRepository(queueService,InstrumentationRegistry.getInstrumentation().targetContext)
    }

    @Test(expected = Throwable::class)
    fun getLocationShouldReturnEmptyFlow() = runTest {
        whenever(queueService.locationMap).thenReturn(mutableStateMapOf())
        assertNotNull(repo.getLocation().firstOrNull())
    }

    @Test
    fun getLocationShouldReturnLocation() = runTest {
        val loc = Location("123","naam", QueueState.NotJoined, 0)
        whenever(queueService.locationMap).thenReturn(mutableStateMapOf(Pair("aa", loc)))
        assertEquals(loc, repo.getLocation().first())
    }

    @Test
    fun setLocationShouldChangeOutputOfGetLocation() = runTest {
        val loc1 = Location("1","naam", QueueState.NotJoined, 0)
        val loc2 = Location("2","naam", QueueState.NotJoined, 0)
        whenever(queueService.locationMap).thenReturn(mutableStateMapOf(Pair("1", loc1),Pair("2", loc2)))
        val a = repo.getLocation()
        repo.setLocation(loc1)
        assertEquals(loc1, a.take(1).single())
        repo.setLocation(loc2)
        assertEquals(loc2, a.take(1).last())
        repo.setLocation(loc1)
        assertEquals(loc1, a.take(1).last())
    }

}