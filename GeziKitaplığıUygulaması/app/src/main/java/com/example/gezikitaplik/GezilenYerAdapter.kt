package com.example.gezikitaplik

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.gezikitaplik.model.GezilenYer

class GezilenYerAdapter(

    private val context: Context,
    private val gezilenYerler: List<GezilenYer>
) : BaseAdapter(), Filterable {

    private var filteredGezilenYerler: List<GezilenYer> = gezilenYerler

    override fun getCount(): Int = filteredGezilenYerler.size

    override fun getItem(position: Int): Any = filteredGezilenYerler[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.gezilen_yer_item, parent, false)

        val gezilenYer = getItem(position) as GezilenYer

        val gezilenYerTextView = view.findViewById<TextView>(R.id.gezilenYerTextView)
        val aciklamaTextView = view.findViewById<TextView>(R.id.aciklamaTextView)
        val resimImageView = view.findViewById<ImageView>(R.id.resimImageView)

        gezilenYerTextView.text = gezilenYer.gezilenYer
        aciklamaTextView.text = gezilenYer.aciklama

        // Resim URI kontrolü
        if (!gezilenYer.resimUri.isNullOrEmpty()) {
            Glide.with(context)
                .load(gezilenYer.resimUri)
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .into(resimImageView)
        } else {
            resimImageView.setImageResource(R.drawable.placeholder_image)
        }

        return view
    }

    // Filtreleme işlevi
    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val query = constraint?.toString()?.lowercase() ?: ""
                val results = FilterResults()

                // Eğer arama boşsa tüm listeyi döndür
                results.values = if (query.isEmpty()) {
                    gezilenYerler
                } else {
                    gezilenYerler.filter {
                        // Gezilen yer adını küçük harflerle karşılaştırarak filtrele
                        it.gezilenYer.lowercase().contains(query)
                    }
                }
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                // Filtrelenmiş verileri güncelle
                filteredGezilenYerler = results?.values as List<GezilenYer>
                notifyDataSetChanged() // Listeyi güncelleyip adaptörü yenile
            }
        }
    }
}
