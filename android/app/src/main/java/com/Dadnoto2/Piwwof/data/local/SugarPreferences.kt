package com.Dadnoto2.Piwwof.data.local

import android.content.Context
import android.content.SharedPreferences

class SugarPreferences(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("sugar_bloom_garden", Context.MODE_PRIVATE)

    fun int(key: String, fallback: Int): Int = runCatching { prefs.getInt(key, fallback) }.getOrDefault(fallback)

    fun bool(key: String, fallback: Boolean): Boolean = runCatching { prefs.getBoolean(key, fallback) }.getOrDefault(fallback)

    fun text(key: String, fallback: String): String = runCatching { prefs.getString(key, fallback) ?: fallback }.getOrDefault(fallback)

    fun putInt(key: String, value: Int) {
        prefs.edit().putInt(key, value).apply()
    }

    fun putBool(key: String, value: Boolean) {
        prefs.edit().putBoolean(key, value).apply()
    }

    fun putText(key: String, value: String) {
        prefs.edit().putString(key, value).apply()
    }

    fun clearAll() {
        prefs.edit().clear().apply()
    }

    fun isCorrupted(): Boolean {
        val coins = int(KEY_COINS, 0)
        val stage = int(KEY_UNLOCKED, 1)
        return coins < 0 || stage < 1
    }

    companion object {
        const val KEY_COINS = "coins"
        const val KEY_UNLOCKED = "unlocked_stage"
        const val KEY_BEST = "best_score"
        const val KEY_OWNED = "owned_blooms"
        const val KEY_TUTORIAL = "tutorial_seen"
        const val KEY_MISSION_SET = "mission_set"
        const val KEY_COLOURBLIND = "opt_colourblind"
        const val KEY_MOTION = "opt_reduced_motion"
        const val KEY_HINT = "opt_hint_glow"
        fun starsKey(stage: Int): String = "stars_$stage"
        fun bestKey(stage: Int): String = "best_$stage"
        fun missionProgressKey(id: Int): String = "mission_p_$id"
        fun missionClaimedKey(id: Int): String = "mission_c_$id"
    }
}
