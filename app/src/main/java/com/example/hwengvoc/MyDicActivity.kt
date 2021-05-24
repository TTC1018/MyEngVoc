package com.example.hwengvoc

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hwengvoc.databinding.ActivityMyDicBinding
import java.util.*

class MyDicActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyDicBinding
    lateinit var myDBHelper: MyDBHelper
    lateinit var adapter:SearchRecyclerViewAdapter
    lateinit var recyclerView: RecyclerView

    var vocData = LinkedList<VocData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMyDicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    fun init(){
        val intent = intent
        val DicData = intent.getSerializableExtra("dic") as DicData

        myDBHelper = MyDBHelper(this)
        vocData = myDBHelper.findDic(DicData.dicName.replace(" ", "_"))

        recyclerView = binding.mydicRecycler
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = SearchRecyclerViewAdapter(vocData)
        recyclerView.adapter = adapter
        val simpleCallback = object:
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                adapter!!.removeItem(viewHolder.adapterPosition)
            }
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }
        }
        val itemTouchHelper = ItemTouchHelper(simpleCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView!!)
    }

    override fun onBackPressed() {
        val intent = Intent()
        intent.putExtra("count", vocData.size)
        setResult(Activity.RESULT_OK, intent)
        finish()
        super.onBackPressed()
    }
}