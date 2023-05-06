package be.ugent.gigacharge.data

import android.content.Context
import androidx.compose.runtime.snapshotFlow
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import be.ugent.gigacharge.model.location.Location
import be.ugent.gigacharge.model.service.AccountService
import be.ugent.gigacharge.model.service.QueueService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "location")

@Singleton
class LocationRepository @Inject constructor(
    private val queueService: QueueService,
    private val accountService: AccountService,
    @ApplicationContext private val context: Context
) {
    private val sTARTID = stringPreferencesKey("startID")
    private var startID = runBlocking {context.dataStore.data.map { it[sTARTID] }.first()}

    val locationIdFlow: MutableStateFlow<String?> = MutableStateFlow(startID)
    private val locations: Flow<Map<String, Location>> =
        snapshotFlow { queueService.locationMap.toMap() }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getLocation(): Flow<Location> = locations.flatMapLatest { map ->
        if (map.isEmpty()) {
            emptyFlow()
        } else {
            locationIdFlow.transform { id ->
                if (id == null || !map.containsKey(id)) {
                    emit(map.values.first())
                } else {
                    emit(map[id]!!)
                }
            }
        }
    }

    fun getLocations(): Flow<List<Location>> {
        return locations.map { it.values.toList() }
    }

    fun setLocation(location: Location) {
        locationIdFlow.value = location.id
        runBlocking { context.dataStore.edit {it[sTARTID] = location.id } }
    }

    suspend fun updateLocations() {
        if (accountService.isEnabled()) {
            queueService.updateLocations()
        }
    }

    suspend fun updateLocation(locid : String){
        if (accountService.isEnabled()) {
            queueService.updateLocation(locid)
        }
    }
}