package com.example.hwengvoc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class SearchRecyclerViewAdapter(val vocs: LinkedList<VocData>):RecyclerView.Adapter<SearchRecyclerViewAdapter.ViewHolder>(){
    val itemClickListener:OnItemClickListener?=null
    interface OnItemClickListener{
        fun OnItemClick(holder:ViewHolder, view:View, data:VocData, position:Int)
    }

    fun removeItem(pos:Int){
        vocs.removeAt(pos)
        notifyItemRemoved(pos)
        notifyItemRangeRemoved(0, itemCount)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout:LinearLayout = view.findViewById(R.id.searchTextLayout)
        val wordText:TextView = view.findViewById(R.id.wordTextView)
        val meanText:TextView = view.findViewById(R.id.meanTextView)
        init{
            layout.setOnClickListener {
                itemClickListener?.OnItemClick(this, it, vocs[adapterPosition], adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return vocs.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.wordText.text = vocs[position].word
        holder.meanText.text = vocs[position].meaning
    }
}