package com.example.hwengvoc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
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
            //test
            dicList.add(DicData("나의 단어장", 5))

            recyclerView = recyclerDic
            recyclerView!!.layoutManager = GridLayoutManager(context, 2)
            adapter = MyDicRecyclerViewAdapter(dicList)
            recyclerView!!.adapter = adapter
        }
    }
}