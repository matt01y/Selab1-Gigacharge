package be.ugent.gigacharge

import androidx.compose.runtime.mutableStateMapOf
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import be.ugent.gigacharge.data.LocationRepository
import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.location.LocationStatus
import be.ugent.gigacharge.model.location.QueueState
import be.ugent.gigacharge.model.service.AccountService
import be.ugent.gigacharge.model.service.QueueService
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

const val STARTID = "roeselare"
class LocationRepositoryTest {
    private var queueService: QueueService = mock()
    private lateinit var repo: LocationRepository
    private var dataStore: DataStore<Preferences> = mock()
    private var preferences: Preferences =  mock {
        this.on { it[any<Preferences.Key<String>>()] }.thenReturn(STARTID)
    }
    private var accountService: AccountService = mock()
    @Before
    fun setup() {
        whenever(dataStore.data).thenReturn(flowOf(preferences))
        runBlocking { whenever(dataStore.edit { any() }).thenReturn(null)}
        repo = LocationRepository(queueService, accountService, dataStore)
    }

    @Test(expected = Throwable::class)
    fun getLocationShouldReturnEmptyFlow() = runTest {
        whenever(queueService.locationMap).thenReturn(mutableStateMapOf())
        assertNotNull(repo.getLocation().firstOrNull())
    }

    @Test
    fun getLocationShouldReturnLocation() = runTest {
        val loc = Location("123","naam", QueueState.NotJoined, LocationStatus.FREE, 0, listOf())
        whenever(queueService.locationMap).thenReturn(mutableStateMapOf(Pair("aa", loc)))
        assertEquals(loc, repo.getLocation().first())
    }

    @Test
    fun locationIdFlowShouldContainStartIDInTheStart() = runTest {
        assertEquals(STARTID, repo.locationIdFlow.value)
    }

    @Test
    fun locationIDFlowShouldChangeWithSetLocation() = runTest {
        val loc = Location("123","naam", QueueState.NotJoined, LocationStatus.FREE, 0, listOf())
        whenever(queueService.locationMap).thenReturn(mutableStateMapOf(Pair("aa", loc)))
        repo.setLocation(loc)
        assertEquals(loc.id, repo.locationIdFlow.value)
    }

    @Test
    fun locationIDFlowShouldChangeWithEverySetLocation() = runTest {
        val loc1 = Location("1","naam", QueueState.NotJoined, LocationStatus.FREE, 0, listOf())
        val loc2 = Location("2","naam", QueueState.NotJoined, LocationStatus.FREE, 0, listOf())
        whenever(queueService.locationMap).thenReturn(mutableStateMapOf(Pair("1", loc1),Pair("2", loc2)))
        repo.setLocation(loc1)
        assertEquals(loc1.id, repo.locationIdFlow.value)
        repo.setLocation(loc2)
        assertEquals(loc2.id, repo.locationIdFlow.value)
        repo.setLocation(loc1)
        assertEquals(loc1.id, repo.locationIdFlow.value)
    }

    @Test
    fun setLocationShouldChangeOutputOfGetLocation() = runTest {
        val loc1 = Location("1","naam", QueueState.NotJoined, LocationStatus.FREE, 0, listOf())
        val loc2 = Location("2","naam", QueueState.NotJoined, LocationStatus.FREE, 0, listOf())
        whenever(queueService.locationMap).thenReturn(mutableStateMapOf(Pair("1", loc1),Pair("2", loc2)))
        val a = repo.getLocation()
        repo.setLocation(loc1)
        assertEquals(loc1, a.take(1).single())
        repo.setLocation(loc2)
        assertEquals(loc2, a.take(1).last())
        repo.setLocation(loc1)
        assertEquals(loc1, a.take(1).last())
    }

//    @Test
//    fun setLocationShouldWriteNewLocationToDataStore() = runTest {
//        val loc = Location("123","naam", QueueState.NotJoined, LocationStatus.FREE, 0, listOf())
//        repo.setLocation(loc)
//        verify(dataStore, times(1)).data
//        verify(dataStore, times(1)).edit { any() }
//    }

    @Test
    fun updateLocationsShouldCallQueueServiceDotUpdateLocationsWhenAccountServiceEnabled() = runTest {
        whenever(accountService.isEnabled()).thenReturn(true)
        repo.updateLocations()
        verify(queueService).updateLocations()
    }

    @Test
    fun updateLocationsShouldNotCallQueueServiceDotUpdateLocationsWhenAccountServiceNotEnabled() = runTest {
        whenever(accountService.isEnabled()).thenReturn(false)
        repo.updateLocations()
        verify(queueService, times(0)).updateLocations()
    }

    @Test
    fun updateLocationShouldCallQueueServiceDotUpdateLocationWhenAccountServiceEnabled() = runTest {
        whenever(accountService.isEnabled()).thenReturn(true)
        repo.updateLocation("")
        verify(queueService).updateLocation(String())
    }

    @Test
    fun updateLocationShouldNotCallQueueServiceDotUpdateLocationWhenAccountServiceNotEnabled() = runTest {
        whenever(accountService.isEnabled()).thenReturn(false)
        repo.updateLocation("")
        verify(queueService, times(0)).updateLocation(String())
    }
}
