package org.cakk.autotap

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class RuleEditActivity : AppCompatActivity() {
    private lateinit var store: RuleStore
    private var ruleId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rule_edit)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        store = RuleStore(this)

        ruleId = intent.getStringExtra("rule_id")
        val existing = ruleId?.let { id -> store.getRules().find { it.id == id } }

        val etKeyword = findViewById<EditText>(R.id.etKeyword)
        val etPackage = findViewById<EditText>(R.id.etPackage)
        val rgMode = findViewById<RadioGroup>(R.id.rgMode)
        val etCooldown = findViewById<EditText>(R.id.etCooldown)

        if (existing != null) {
            etKeyword.setText(existing.keyword)
            etPackage.setText(existing.targetPackage)
            etCooldown.setText((existing.cooldownMs / 1000).toString())
            if (existing.matchMode == MatchMode.EXACT) rgMode.check(R.id.rbExact)
        } else {
            etPackage.setText("com.google.android.youtube")
            etCooldown.setText("5")
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val keyword = etKeyword.text.toString().trim()
            if (keyword.isEmpty()) {
                etKeyword.error = getString(R.string.keyword_required)
                return@setOnClickListener
            }
            val rule = TapRule(
                id = existing?.id ?: store.newId(),
                keyword = keyword,
                matchMode = if (rgMode.checkedRadioButtonId == R.id.rbExact)
                    MatchMode.EXACT else MatchMode.CONTAINS,
                targetPackage = etPackage.text.toString().trim()
                    .ifEmpty { "com.google.android.youtube" },
                cooldownMs = (etCooldown.text.toString().toLongOrNull() ?: 5) * 1000,
                enabled = existing?.enabled ?: true
            )
            val rules = store.getRules().toMutableList()
            val idx = rules.indexOfFirst { it.id == rule.id }
            if (idx >= 0) rules[idx] = rule else rules.add(rule)
            store.saveRules(rules)
            finish()
        }

        val btnDelete = findViewById<Button>(R.id.btnDelete)
        if (existing == null) {
            btnDelete.visibility = View.GONE
        } else {
            btnDelete.setOnClickListener {
                store.saveRules(store.getRules().filter { it.id != existing.id })
                finish()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
