package be.ugent.gigacharge.domain.location

import android.util.Log
import be.ugent.gigacharge.data.LocationRepository
import javax.inject.Inject

class UpdateCurrentLocationUseCase @Inject constructor(
    private val locationRepository: LocationRepository
) {
    suspend operator fun invoke() {
        Log.i("update", "updating location: " + locationRepository.locationIdFlow.value)
        locationRepository.updateLocation(locationRepository.locationIdFlow.value?:"problem")
    }
}