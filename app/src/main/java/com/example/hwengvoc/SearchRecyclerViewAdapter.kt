package com.example.hwengvoc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class SearchRecyclerViewAdapter(val values:List<VocData>):RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder>(){
//    var itemClickListener:OnItemClickListener?=null
//    interface OnItemClickListener{
//        fun OnItemClick(holder:ViewHolder, view:View, data:VocData, position:Int)
//    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val wordText:TextView = view.findViewById(R.id.wordTextView)
        val meanText:TextView = view.findViewById(R.id.meanTextView)
    }

    override fun getItemCount(): Int {
        return values.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.wordText.text = values[position].word
        holder.meanText.text = values[position].meaning
    }
}