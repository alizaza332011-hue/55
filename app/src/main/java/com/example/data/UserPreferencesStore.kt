package com.example.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "rpg_system_prefs")

data class SystemPreferences(
    val soundMuted: Boolean = false,
    val musicEnabled: Boolean = false,
    val volume: Float = 0.65f,
    val bedtimeReminderEnabled: Boolean = true,
    val bedtimeHour: Int = 22,
    val bedtimeMinute: Int = 30
)

class UserPreferencesStore(private val context: Context) {
    private object Keys {
        val SOUND_MUTED = booleanPreferencesKey("sound_muted")
        val MUSIC_ENABLED = booleanPreferencesKey("music_enabled")
        val VOLUME = floatPreferencesKey("volume")
        val BEDTIME_REMINDER_ENABLED = booleanPreferencesKey("bedtime_reminder_enabled")
        val BEDTIME_HOUR = intPreferencesKey("bedtime_hour")
        val BEDTIME_MINUTE = intPreferencesKey("bedtime_minute")
    }

    val preferencesFlow: Flow<SystemPreferences> = context.dataStore.data.map { prefs ->
        SystemPreferences(
            soundMuted = prefs[Keys.SOUND_MUTED] ?: false,
            musicEnabled = prefs[Keys.MUSIC_ENABLED] ?: false,
            volume = prefs[Keys.VOLUME] ?: 0.65f,
            bedtimeReminderEnabled = prefs[Keys.BEDTIME_REMINDER_ENABLED] ?: true,
            bedtimeHour = prefs[Keys.BEDTIME_HOUR] ?: 22,
            bedtimeMinute = prefs[Keys.BEDTIME_MINUTE] ?: 30
        )
    }

    suspend fun setSoundMuted(muted: Boolean) {
        context.dataStore.edit { it[Keys.SOUND_MUTED] = muted }
    }

    suspend fun setMusicEnabled(enabled: Boolean) {
        context.dataStore.edit { it[Keys.MUSIC_ENABLED] = enabled }
    }

    suspend fun setVolume(volume: Float) {
        context.dataStore.edit { it[Keys.VOLUME] = volume.coerceIn(0f, 1f) }
    }

    suspend fun setBedtimeReminder(enabled: Boolean, hour: Int, minute: Int) {
        context.dataStore.edit {
            it[Keys.BEDTIME_REMINDER_ENABLED] = enabled
            it[Keys.BEDTIME_HOUR] = hour
            it[Keys.BEDTIME_MINUTE] = minute
        }
    }

    suspend fun resetDefaults() {
        context.dataStore.edit { it.clear() }
    }
}
