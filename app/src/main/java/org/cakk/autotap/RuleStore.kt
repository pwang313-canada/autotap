package org.cakk.autotap

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import java.util.UUID

/** Simple SharedPreferences-backed store (synchronous reads — safe to use inside the service). */
class RuleStore(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("autotap", Context.MODE_PRIVATE)

    fun getRules(): List<TapRule> {
        val raw = prefs.getString("rules", null) ?: return emptyList()
        return try {
            val arr = JSONArray(raw)
            List(arr.length()) { TapRule.fromJson(arr.getJSONObject(it)) }
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun saveRules(rules: List<TapRule>) {
        val arr = JSONArray()
        rules.forEach { arr.put(it.toJson()) }
        prefs.edit().putString("rules", arr.toString()).apply()
    }

    fun newId(): String = UUID.randomUUID().toString()

    var monitoringEnabled: Boolean
        get() = prefs.getBoolean("monitoring_enabled", false)
        set(v) = prefs.edit().putBoolean("monitoring_enabled", v).apply()

    var debugMode: Boolean
        get() = prefs.getBoolean("debug_mode", false)
        set(v) = prefs.edit().putBoolean("debug_mode", v).apply()
}
