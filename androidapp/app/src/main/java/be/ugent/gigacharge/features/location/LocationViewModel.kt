package be.ugent.gigacharge.features.location

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import be.ugent.gigacharge.model.Location
import be.ugent.gigacharge.model.service.LogService
import be.ugent.gigacharge.model.service.QueueService
import be.ugent.gigacharge.screens.GigaChargeViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val queueService: QueueService,
    logService: LogService
) : GigaChargeViewModel(logService) {
    var uiState = mutableStateOf(LocationUiState())
        private set

    var locationflow = queueService.getLocations.stateIn(
        scope = viewModelScope,
        initialValue = mutableListOf(),
        started = SharingStarted.Eagerly
    )


    fun refreshLocations(){
        launchCatching {
            queueService.updateLocations()
        }
    }

    fun toggleQueue(loc : Location){
        launchCatching {
            if(!loc.amIJoined){
                queueService.joinQueue(loc)
            }else{
                queueService.leaveQueue(loc)
            }
        }
    }

    fun onStart(){
        /*launchCatching {
            uiState.value.locations.clear()
            uiState.value.locations.addAll(queueService.updateLocations())
            Log.println(Log.INFO, "locations", uiState.value.locations.toString())
        }*/

    }

    /*fun onRegister(openAndPopUp: () -> Unit){
        uiState.value = uiState.value.copy(statusmessage = "nu wordt er geprobeerd om te enablen met u kaartnummber")

        launchCatching {
            accountService.isEnabledObservers.add {
                if (it) {
                    openAndPopUp()
                    uiState.value = uiState.value.copy(statusmessage = "enabling gelukt")
                } else {
                    uiState.value = uiState.value.copy(statusmessage = "enabling niet gelukt")
                }
            }
            accountService.tryEnable(cardnumber)
        }
    }*/
}
