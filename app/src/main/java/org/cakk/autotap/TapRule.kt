package org.cakk.autotap

// MARKER: autoTapV1
import org.json.JSONObject

enum class MatchMode { CONTAINS, EXACT }

data class TapRule(
    val id: String,
    val keyword: String,
    val matchMode: MatchMode = MatchMode.CONTAINS,
    val targetPackage: String = "com.google.android.youtube",
    val cooldownMs: Long = 5000,
    val enabled: Boolean = true
) {
    fun toJson(): JSONObject = JSONObject()
        .put("id", id)
        .put("keyword", keyword)
        .put("matchMode", matchMode.name)
        .put("targetPackage", targetPackage)
        .put("cooldownMs", cooldownMs)
        .put("enabled", enabled)

    companion object {
        fun fromJson(o: JSONObject): TapRule = TapRule(
            id = o.getString("id"),
            keyword = o.getString("keyword"),
            matchMode = try {
                MatchMode.valueOf(o.optString("matchMode", "CONTAINS"))
            } catch (_: Exception) {
                MatchMode.CONTAINS
            },
            targetPackage = o.optString("targetPackage", "com.google.android.youtube"),
            cooldownMs = o.optLong("cooldownMs", 5000),
            enabled = o.optBoolean("enabled", true)
        )
    }
}
