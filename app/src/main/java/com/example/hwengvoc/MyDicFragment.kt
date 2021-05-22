package com.example.hwengvoc

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hwengvoc.databinding.FragmentMyDicBinding


class MyDicFragment : Fragment() {
    var recyclerView:RecyclerView?=null
    var adapter:MyDicRecyclerViewAdapter?=null
    var binding:FragmentMyDicBinding?=null
    var dicList = mutableListOf<DicData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyDicBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.apply {

            recyclerView = recyclerDic
            recyclerView!!.layoutManager = GridLayoutManager(context, 2)
            adapter = MyDicRecyclerViewAdapter(dicList)
            recyclerView!!.adapter = adapter
            adapter!!.itemClickListener = object:MyDicRecyclerViewAdapter.OnItemClickListener{
                override fun OnItemClick(
                    holder: MyDicRecyclerViewAdapter.ViewHolder,
                    view: View,
                    data: DicData,
                    position: Int
                ) {
                    val intent = Intent(context, MyDicActivity::class.java)
                    intent.putExtra("dic", data)
                    startActivity(intent)
                }
            }


            editBtn.setOnClickListener {
                if(recyclerView!!.layoutManager is GridLayoutManager)
                    recyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                else
                    recyclerView!!.layoutManager = GridLayoutManager(context, 2)
            }
        }

        val activity = requireActivity() as MainActivity
        val dbHelper = activity.myDBHelper
        dbHelper.readDefaultDic()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }
}