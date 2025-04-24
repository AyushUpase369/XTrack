package com.example.xtrack

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class CustomMuscleAdapter(
    context: Context,
    private val items: List<MuscleItem>
) : ArrayAdapter<MuscleItem>(context, R.layout.item_muscle_group, items), Filterable {

    private var filteredItems: List<MuscleItem> = items

    override fun getCount(): Int = filteredItems.size

    override fun getItem(position: Int): MuscleItem? = filteredItems[position]

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createCustomView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createCustomView(position, convertView, parent)
    }

    private fun createCustomView(position: Int, convertView: View?, parent: ViewGroup): View {
        val rowView = convertView ?: LayoutInflater.from(context)
            .inflate(R.layout.item_muscle_group, parent, false)

        val item = getItem(position)
        val name = rowView.findViewById<TextView>(R.id.muscleName)
        val icon = rowView.findViewById<ImageView>(R.id.icon)

        if (item != null) {
            name.text = item.name
            icon.setImageResource(item.iconResId)
        }

        return rowView
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase()?.trim()
                val resultList = if (query.isNullOrEmpty()) {
                    items
                } else {
                    items.filter { it.name.lowercase().contains(query) }
                }

                return FilterResults().apply {
                    values = resultList
                    count = resultList.size
                }
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredItems = if (results?.values is List<*>) {
                    results.values as List<MuscleItem>
                } else {
                    emptyList()
                }
                notifyDataSetChanged()
            }
        }
    }
}
