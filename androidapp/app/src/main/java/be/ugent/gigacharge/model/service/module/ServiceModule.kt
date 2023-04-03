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

package be.ugent.gigacharge.model.service.module

import be.ugent.gigacharge.model.service.AccountService
import be.ugent.gigacharge.model.service.ConfigurationService
import be.ugent.gigacharge.model.service.LogService
import be.ugent.gigacharge.model.service.StorageService
import be.ugent.gigacharge.model.service.impl.AccountServiceImpl
import be.ugent.gigacharge.model.service.impl.ConfigurationServiceImpl
import be.ugent.gigacharge.model.service.impl.LogServiceImpl
import be.ugent.gigacharge.model.service.impl.StorageServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class ServiceModule {
  @Binds abstract fun provideAccountService(impl: AccountServiceImpl): AccountService

  @Binds abstract fun provideLogService(impl: LogServiceImpl): LogService

  @Binds abstract fun provideStorageService(impl: StorageServiceImpl): StorageService

  @Binds
  abstract fun provideConfigurationService(impl: ConfigurationServiceImpl): ConfigurationService
}
