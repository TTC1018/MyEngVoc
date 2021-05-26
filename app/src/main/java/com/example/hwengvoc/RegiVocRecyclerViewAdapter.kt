package com.example.hwengvoc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.util.*

class RegiVocRecyclerViewAdapter(val dics: LinkedList<String>): RecyclerView.Adapter<RegiVocRecyclerViewAdapter.ViewHolder>(){
    var itemClickListener:OnItemClickListener?=null
    interface OnItemClickListener{
        fun OnItemClick(holder:ViewHolder, view: View, tableName:String, position:Int)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val rowLayout:LinearLayout = view.findViewById(R.id.targetDicLayout)
        val dicNameText: TextView = view.findViewById(R.id.targetDicNameTextView)
        init{
            rowLayout.setOnClickListener {
                itemClickListener?.OnItemClick(this, it, dics[adapterPosition], adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return dics.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.target_dic_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.dicNameText.text = dics[position]
    }
}