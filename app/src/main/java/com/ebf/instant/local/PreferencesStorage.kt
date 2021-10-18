package com.ebf.instant.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.ebf.instant.local.DataStorePreferencesStorage.PreferencesKeys.PREF_USER_ID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface PreferencesStorage {
    suspend fun setUserId(userId: String)
    val userId: Flow<String?>
}

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DataStorePreferencesStorage.PREFS_NAME)

class DataStorePreferencesStorage(
    private val dataStore: DataStore<Preferences>
) : PreferencesStorage {
    companion object {
        const val PREFS_NAME = "instant"
    }

    object PreferencesKeys {
        val PREF_USER_ID = stringPreferencesKey("pref_user_id")
    }

    override suspend fun setUserId(userId: String) {
        dataStore.edit {
            it[PREF_USER_ID] = userId
        }
    }

    override val userId: Flow<String?> =
        dataStore.data.map { it[PREF_USER_ID] }
}
