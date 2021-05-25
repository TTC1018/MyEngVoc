package com.example.hwengvoc

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hwengvoc.databinding.DialogEditMyDicBinding
import com.example.hwengvoc.databinding.FragmentMyDicBinding


class MyDicFragment : Fragment() {
    var recyclerView:RecyclerView?=null
    var adapter:MyDicRecyclerViewAdapter?=null
    var binding:FragmentMyDicBinding?=null
    var dicList = mutableListOf<DicData>()

    val MY_DIC_CODE = 100

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyDicBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //어플 최조 실행을 확인 해주는 변수 (기본 단어장 추가에 활용)
        var pref: SharedPreferences = context!!.getSharedPreferences("isFirst", Activity.MODE_PRIVATE)
        var first:Boolean = pref.getBoolean("isFirst", false)

        super.onViewCreated(view, savedInstanceState)
        binding!!.apply {

            //변수들 초기화
            recyclerView = recyclerDic
            recyclerView!!.layoutManager = GridLayoutManager(context, 2)
            adapter = MyDicRecyclerViewAdapter(dicList)
            recyclerView!!.adapter = adapter
            adapter!!.itemClickListener = editItemClickListener()

            //단어장 수정 버튼 ClickListener
            editBtn.setOnClickListener {
                val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                val fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)

                if(recyclerView!!.layoutManager is GridLayoutManager){
                    recyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    recyclerView!!.startAnimation(fadeIn)
                    adapter!!.itemClickListener = defItemClickListener()
                }
                else{
                    recyclerView!!.layoutManager = GridLayoutManager(context, 2)
                    recyclerView!!.startAnimation(fadeIn)
                    adapter!!.itemClickListener = editItemClickListener()
                }

                val editShowText:TextView = binding!!.editShowText
                if(editShowText.visibility==View.GONE){
                    editShowText.visibility=View.VISIBLE
                    editShowText.startAnimation(fadeIn)
                }
                else{
                    editShowText.startAnimation(fadeOut)
                    editShowText.visibility=View.GONE
                }
            }
        }

        val activity = requireActivity() as MainActivity
        val dbHelper = activity.myDBHelper
        defaultDicGenerate(dbHelper, first, pref)
    }

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

    private fun defaultDicGenerate(dbHelper: MyDBHelper, first:Boolean, pref:SharedPreferences){
        if(!first){
            dbHelper.readDefaultDic()
            val editor:SharedPreferences.Editor = pref.edit()
            editor.putBoolean("isFirst", true)
            editor.commit()
        }
        else{
            if(dbHelper.checkTableMade(MyDBHelper.TABLE_NAMES.get(0))){
                if(dicList.size==0){
                    dicList.add(DicData(MyDBHelper.TABLE_NAMES.get(0).replace("_", " "), dbHelper.countVoc("기본_단어장")))
                    adapter!!.notifyDataSetChanged()
                }
            }
        }
    }

    private fun defItemClickListener():MyDicRecyclerViewAdapter.OnItemClickListener{
        return object:MyDicRecyclerViewAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: MyDicRecyclerViewAdapter.ViewHolder,
                view: View,
                data: DicData,
                position: Int
            ) {
                val intent = Intent(context, MyDicActivity::class.java)
                intent.putExtra("dic", data)
                startActivityForResult(intent, position)
            }
        }
    }

    private fun editItemClickListener():MyDicRecyclerViewAdapter.OnItemClickListener{
        return object:MyDicRecyclerViewAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: MyDicRecyclerViewAdapter.ViewHolder,
                view: View,
                data: DicData,
                position: Int
            ) {
                var customDialog = layoutInflater.inflate(R.layout.dialog_edit_my_dic, null)
                var builder = AlertDialog.Builder(context!!)
                builder.setView(customDialog)
                val dialog = builder.create()
                dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()

                var cancelBtn = customDialog.findViewById<Button>(R.id.diaCancelBtn)
                var okBtn = customDialog.findViewById<Button>(R.id.diaOKBtn)
                cancelBtn.setOnClickListener {
                    dialog.dismiss()
                }
                okBtn.setOnClickListener {

                }
                var dicText = customDialog.findViewById<EditText>(R.id.dicNameEditText)
                dicText.requestFocus()
            }
        }
    }


}