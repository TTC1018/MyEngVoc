package com.example.hwengvoc

import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyDicRecyclerViewAdapter(val dicList:List<DicData>): RecyclerView.Adapter<MyDicRecyclerViewAdapter.ViewHolder>() {
    var itemClickListener:OnItemClickListener?=null
    interface OnItemClickListener{
        fun OnItemClick(holder:ViewHolder, view:View, data:DicData, position:Int)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val btnLayout:LinearLayout = view.findViewById(R.id.dicBtnLayout)
        val dicNameText: TextView = view.findViewById(R.id.dicNameText)
        val wordCountText: TextView = view.findViewById(R.id.wordCountText)
        init{
            btnLayout.setOnClickListener {
                itemClickListener?.OnItemClick(this, it, dicList[adapterPosition], adapterPosition)
            }
        }
    }

    override fun getItemCount(): Int {
        return dicList.size
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyDicRecyclerViewAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mydic_square, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyDicRecyclerViewAdapter.ViewHolder, position: Int) {
        holder.dicNameText.text = dicList[position].dicName
        holder.wordCountText.text = "단어 "+dicList[position].wordCount.toString()+"개"
    }
}