package org.cakk.autotap

// MARKER: autoTapV1
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var store: RuleStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        store = RuleStore(this)

        findViewById<Button>(R.id.btnRules).setOnClickListener {
            startActivity(Intent(this, RulesActivity::class.java))
        }
        findViewById<Button>(R.id.btnPermission).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
    }

    override fun onResume() {
        super.onResume()
        val enabled = isAccessibilityEnabled()
        findViewById<TextView>(R.id.tvPermissionStatus).text =
            if (enabled) getString(R.string.permission_on) else getString(R.string.permission_off)

        val monitoring = findViewById<Switch>(R.id.switchMonitoring)
        monitoring.isChecked = store.monitoringEnabled
        monitoring.isEnabled = enabled
        monitoring.setOnCheckedChangeListener { _, checked -> store.monitoringEnabled = checked }

        val debug = findViewById<Switch>(R.id.switchDebug)
        debug.isChecked = store.debugMode
        debug.setOnCheckedChangeListener { _, checked -> store.debugMode = checked }

        val count = store.getRules().count { it.enabled }
        findViewById<TextView>(R.id.tvRuleCount).text = getString(R.string.rules_active, count)
    }

    private fun isAccessibilityEnabled(): Boolean {
        val expected = "$packageName/${TapService::class.java.canonicalName}"
        val enabled = Settings.Secure.getString(
            contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        ) ?: return false
        return enabled.split(':').any { it.equals(expected, ignoreCase = true) }
    }
}
