package be.ugent.gigacharge.model.service.module

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "location")


@InstallIn(SingletonComponent::class)
@Module
object DataStoreModule {

    @Provides
    fun provideDataStore(@ApplicationContext context: Context):DataStore<Preferences> = context.dataStore
}