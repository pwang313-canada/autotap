package org.cakk.autotap

// MARKER: autoTapV1
import android.accessibilityservice.AccessibilityService
import android.os.SystemClock
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

/**
 * Watches the target app (default: YouTube) and auto-taps any UI node whose
 * text or content-description matches an enabled TapRule keyword.
 */
class TapService : AccessibilityService() {

    private lateinit var store: RuleStore
    private val lastTapAt = mutableMapOf<String, Long>()

    override fun onServiceConnected() {
        store = RuleStore(this)
        Log.i(TAG, "AutoTap service connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (!::store.isInitialized) store = RuleStore(this)
        if (!store.monitoringEnabled) return

        val pkg = event.packageName?.toString() ?: return
        val type = event.eventType
        if (type != AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
            type != AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED
        ) return

        val rules = store.getRules().filter { it.enabled && it.targetPackage == pkg }
        if (rules.isEmpty()) return

        val root = rootInActiveWindow ?: return
        try {
            if (store.debugMode) logVisibleText(root)
            for (rule in rules) {
                val now = SystemClock.uptimeMillis()
                if (now - (lastTapAt[rule.id] ?: 0L) < rule.cooldownMs) continue
                if (findAndTap(root, rule)) {
                    lastTapAt[rule.id] = now
                    Log.i(TAG, "Tapped '${rule.keyword}' in $pkg")
                }
            }
        } finally {
            root.recycle()
        }
    }

    /** BFS over the node tree; taps the first node matching the rule. */
    private fun findAndTap(root: AccessibilityNodeInfo, rule: TapRule): Boolean {
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(root)
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            val text = node.text?.toString().orEmpty()
            val desc = node.contentDescription?.toString().orEmpty()
            val hit = when (rule.matchMode) {
                MatchMode.CONTAINS -> text.contains(rule.keyword, ignoreCase = true) ||
                        desc.contains(rule.keyword, ignoreCase = true)
                MatchMode.EXACT -> text.equals(rule.keyword, ignoreCase = true) ||
                        desc.equals(rule.keyword, ignoreCase = true)
            }
            if (hit) {
                // Walk up to the nearest clickable ancestor.
                var target: AccessibilityNodeInfo? = node
                while (target != null && !target.isClickable) target = target.parent
                return target?.performAction(AccessibilityNodeInfo.ACTION_CLICK) ?: false
            }
            for (i in 0 until node.childCount) {
                node.getChild(i)?.let { queue.add(it) }
            }
        }
        return false
    }

    /** Debug helper: dumps all visible text on screen to logcat. */
    private fun logVisibleText(root: AccessibilityNodeInfo) {
        val sb = StringBuilder()
        val queue = ArrayDeque<AccessibilityNodeInfo>()
        queue.add(root)
        while (queue.isNotEmpty()) {
            val node = queue.removeFirst()
            node.text?.toString()?.takeIf { it.isNotBlank() }?.let { sb.append('[').append(it).append("] ") }
            node.contentDescription?.toString()?.takeIf { it.isNotBlank() }?.let { sb.append('{').append(it).append("} ") }
            for (i in 0 until node.childCount) node.getChild(i)?.let { queue.add(it) }
        }
        Log.d(TAG, "SCREEN TEXT: $sb")
    }

    override fun onInterrupt() {}

    companion object {
        const val TAG = "AutoTap"
    }
}
