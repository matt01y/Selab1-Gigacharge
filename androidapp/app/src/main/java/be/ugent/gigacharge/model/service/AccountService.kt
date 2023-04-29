/*
Copyright 2022 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package be.ugent.gigacharge.model.service

import be.ugent.gigacharge.model.AuthenticationError
import be.ugent.gigacharge.model.User
import kotlinx.coroutines.flow.Flow

interface AccountService {


    suspend fun createAnonymousAccount()
    suspend fun deleteAccount()
    suspend fun signOut()

    val currentUserId: String
    val currentUser: Flow<User>
    val hasUser: Boolean
    suspend fun tryEnable(cardNumber: String)
    suspend fun isEnabled(): Boolean
    suspend fun init()
    fun sendToken(token: String)

    val authError: Flow<AuthenticationError>

    val isEnabledObservers: MutableList<((Boolean) -> Unit)>
}
