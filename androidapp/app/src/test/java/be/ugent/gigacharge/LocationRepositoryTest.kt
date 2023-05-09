package be.ugent.gigacharge

import android.content.Context
import androidx.compose.runtime.mutableStateMapOf
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
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
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

private val Context.dataStore: DataStore<Preferences>
    get() = mock()

class LocationRepositoryTest {
    private var queueService: QueueService = mock()
    private lateinit var repo: LocationRepository
    private var dataStore: DataStore<Preferences> = mock()
    private var preferences: Preferences =  mock {
        this.on { it[any<Preferences.Key<String>>()] }.thenReturn("roeselare")
    }
    private var accountService: AccountService = mock()
    @Before
    fun setup() {
        whenever(dataStore.data).thenReturn(flowOf(preferences))
        runBlocking { whenever(dataStore.edit { any() }).thenReturn(null)}
        val mockContext = mock<Context> {
        }
        val dataStoreField = Context::class.java.getDeclaredField("dataStore")
        dataStoreField.isAccessible = true
        dataStoreField.set(mockContext, dataStore)
        repo = LocationRepository(queueService, accountService, mockContext)
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

}
