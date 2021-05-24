package com.example.hwengvoc

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hwengvoc.databinding.FragmentMyDicBinding


class MyDicFragment : Fragment() {
    var recyclerView:RecyclerView?=null
    var adapter:MyDicRecyclerViewAdapter?=null
    var binding:FragmentMyDicBinding?=null
    var dicList = mutableListOf<DicData>()
    var startForResult:ActivityResultLauncher<Intent>?=null

    val MY_DIC_CODE = 100

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
                    startActivityForResult(intent, position)
//                    startForResult!!.launch(intent)
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

//    fun onActivityResult(position:Int, data:DicData){
//        startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
//                result:ActivityResult->
//            if(result.resultCode == Activity.RESULT_OK){
//                val intent = result.data
//                val vocCount = intent!!.getIntExtra("count", -1)
//                dicList.set(position, DicData(dicList.get(position).dicName, vocCount))
//            }
//        }
//    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK){
            val intent = data
            val vocCount = intent!!.getIntExtra("count", -1)
            dicList[requestCode] = DicData(dicList.get(requestCode).dicName, vocCount)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }
}