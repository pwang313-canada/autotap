package org.cakk.autotap

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class RulesActivity : AppCompatActivity() {
    private lateinit var store: RuleStore
    private lateinit var adapter: RulesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rules)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        store = RuleStore(this)

        adapter = RulesAdapter(
            emptyList(),
            onToggle = { rule, enabled ->
                val updated = store.getRules().map {
                    if (it.id == rule.id) it.copy(enabled = enabled) else it
                }
                store.saveRules(updated)
            },
            onClick = { rule ->
                startActivity(
                    Intent(this, RuleEditActivity::class.java).putExtra("rule_id", rule.id)
                )
            }
        )

        findViewById<RecyclerView>(R.id.recyclerRules).apply {
            layoutManager = LinearLayoutManager(this@RulesActivity)
            adapter = this@RulesActivity.adapter
        }
        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(this, RuleEditActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        adapter.update(store.getRules())
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
