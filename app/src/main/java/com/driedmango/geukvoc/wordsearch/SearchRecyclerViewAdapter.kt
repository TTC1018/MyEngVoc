package com.driedmango.geukvoc.wordsearch

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.driedmango.geukvoc.R
import com.driedmango.geukvoc.data.DicData
import com.driedmango.geukvoc.data.VocData
import java.util.*

class SearchRecyclerViewAdapter(diffCallback: DiffUtil.ItemCallback<VocData>):androidx.recyclerview.widget.ListAdapter<VocData, SearchRecyclerViewAdapter.ViewHolder>(diffCallback){
    var itemClickListener: OnItemClickListener?=null
    interface OnItemClickListener{
        fun OnItemClick(holder: ViewHolder, view:View, data: VocData, position:Int)
    }

//    fun removeItem(pos:Int){
//        vocs.removeAt(pos)
//        notifyItemRemoved(pos)
//        notifyItemRangeRemoved(0, itemCount)
//    }
//
//    fun moveItem(oldPos:Int, newPos:Int){
//        val item = vocs[oldPos]
//        vocs.removeAt(oldPos)
//        vocs.add(newPos, item)
//        notifyItemMoved(oldPos, newPos)
//    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var vocData: VocData
        val layout:LinearLayout = view.findViewById(R.id.searchTextLayout)
        val wordText:TextView = view.findViewById(R.id.wordTextView)
        val meanText:TextView = view.findViewById(R.id.meanTextView)
        init{
            layout.setOnClickListener {
                itemClickListener?.OnItemClick(this, it, vocData, adapterPosition)
            }
        }

        fun bind(vocData: VocData){
            this.vocData = vocData
            wordText.text = this.vocData.word
            meanText.text = this.vocData.meaning
            itemView.animation = AnimationUtils.loadAnimation(itemView.context, R.anim.item_create)
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.search_row, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}