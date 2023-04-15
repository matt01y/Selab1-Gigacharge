package be.ugent.gigacharge.data.local.models

sealed interface ProfileState {
    object Hidden: ProfileState
    data class Shown(val profile: Profile): ProfileState
}