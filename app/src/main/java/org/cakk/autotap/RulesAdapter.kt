package org.cakk.autotap

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RulesAdapter(
    private var rules: List<TapRule>,
    private val onToggle: (TapRule, Boolean) -> Unit,
    private val onClick: (TapRule) -> Unit
) : RecyclerView.Adapter<RulesAdapter.VH>() {

    class VH(v: View) : RecyclerView.ViewHolder(v) {
        val tvKeyword: TextView = v.findViewById(R.id.tvKeyword)
        val tvDetail: TextView = v.findViewById(R.id.tvDetail)
        val swEnabled: Switch = v.findViewById(R.id.swEnabled)
    }

    override fun onCreateViewHolder(p: ViewGroup, vt: Int): VH =
        VH(LayoutInflater.from(p.context).inflate(R.layout.item_rule, p, false))

    override fun onBindViewHolder(h: VH, pos: Int) {
        val rule = rules[pos]
        h.tvKeyword.text = rule.keyword
        h.tvDetail.text = "${rule.matchMode.name} · ${rule.cooldownMs / 1000}s cooldown"
        h.swEnabled.setOnCheckedChangeListener(null)
        h.swEnabled.isChecked = rule.enabled
        h.swEnabled.setOnCheckedChangeListener { _, checked -> onToggle(rule, checked) }
        h.itemView.setOnClickListener { onClick(rule) }
    }

    override fun getItemCount() = rules.size

    fun update(newRules: List<TapRule>) {
        rules = newRules
        notifyDataSetChanged()
    }
}
