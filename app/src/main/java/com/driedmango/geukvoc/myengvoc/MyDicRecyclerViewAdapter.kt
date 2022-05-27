package com.driedmango.geukvoc.myengvoc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.driedmango.geukvoc.R
import com.driedmango.geukvoc.data.DicData

class MyDicRecyclerViewAdapter(diffCallback: DiffUtil.ItemCallback<DicData>): ListAdapter<DicData, MyDicRecyclerViewAdapter.ViewHolder>(diffCallback) {
    var itemClickListener: OnItemClickListener?=null
    interface OnItemClickListener{
        fun OnItemClick(holder: ViewHolder, view:View, data: DicData, position:Int)
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var dicData:DicData
        val btnLayout:LinearLayout = view.findViewById(R.id.dicBtnLayout)
        val dicNameText: TextView = view.findViewById(R.id.dicNameText)
        val wordCountText: TextView = view.findViewById(R.id.wordCountText)
        init{
            btnLayout.setOnClickListener {
                itemClickListener?.OnItemClick(this, it, dicData, adapterPosition)
            }
        }

        fun bind(dicData: DicData){
            this.dicData = dicData
            dicNameText.text = this.dicData.dicName
            wordCountText.text = "단어 ${this.dicData.wordCount}개"
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.mydic_square, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    fun setOnItemClickListener(itemClickListener: OnItemClickListener){
        this.itemClickListener = itemClickListener
    }
}