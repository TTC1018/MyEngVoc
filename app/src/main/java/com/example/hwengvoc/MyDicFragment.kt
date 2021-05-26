package com.example.hwengvoc

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hwengvoc.databinding.FragmentMyDicBinding
import com.google.android.material.textfield.TextInputLayout


class MyDicFragment : Fragment() {
    var recyclerView:RecyclerView?=null
    var adapter:MyDicRecyclerViewAdapter?=null
    var binding:FragmentMyDicBinding?=null
    var dicList = mutableListOf<DicData>()
    var dbHelper:MyDBHelper?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMyDicBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onResume() {
        super.onResume()
        if(dicList.size != 0){
            for(i in 0 until dicList.size){
                for(TABLE_NAME in MyDBHelper.TABLE_NAMES){
                    if(dicList.get(i).dicName.equals(TABLE_NAME.replace("_", " "))){
                        dicList.get(i).wordCount = dbHelper!!.countVoc(TABLE_NAME)
                    }
                }
            }
        }

        if(dicList.size==0 && MyDBHelper.TABLE_NAMES.size!=0){
            for(TABLE_NAME in MyDBHelper.TABLE_NAMES){
                dicList.add(DicData(TABLE_NAME.replace("_", " "), dbHelper!!.countVoc(TABLE_NAME)))
            }
            adapter!!.notifyDataSetChanged()
        }
        val activity = requireActivity() as MainActivity
        activity.binding.bottomNavi.menu.getItem(1).setChecked(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        //어플 최조 실행을 확인 해주는 변수 (기본 단어장 추가에 활용)
        var pref: SharedPreferences = context!!.getSharedPreferences("isFirst", Activity.MODE_PRIVATE)
        var first:Boolean = pref.getBoolean("isFirst", true)

        super.onViewCreated(view, savedInstanceState)
        binding!!.apply {

            //변수들 초기화
            recyclerView = recyclerDic
            recyclerView!!.layoutManager = GridLayoutManager(context, 2)
            adapter = MyDicRecyclerViewAdapter(dicList)
            adapter!!.itemClickListener = defItemClickListener()
            recyclerView!!.adapter = adapter

            //단어장 수정 버튼 ClickListener
            editBtn.setOnClickListener {
                val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                val fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)

                if(recyclerView!!.layoutManager is GridLayoutManager){
                    recyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    recyclerView!!.startAnimation(fadeIn)
                    adapter!!.setOnItemClickListener(editItemClickListener())
                }
                else{
                    recyclerView!!.layoutManager = GridLayoutManager(context, 2)
                    recyclerView!!.startAnimation(fadeIn)
                    adapter!!.setOnItemClickListener(defItemClickListener())
                }

                val editShowText:TextView = binding!!.editShowText
                val addBtn: ImageButton = binding!!.addBtn
                val weightView:View = binding!!.weightView
                if(editShowText.visibility==View.GONE){
                    weightView.visibility=View.GONE
                    editShowText.visibility=View.VISIBLE
                    addBtn.visibility=View.VISIBLE
                    editShowText.startAnimation(fadeIn)
                    addBtn.startAnimation(fadeIn)
                }
                else{
                    editShowText.startAnimation(fadeOut)
                    addBtn.startAnimation(fadeOut)
                    editShowText.visibility=View.GONE
                    addBtn.visibility=View.GONE
                    weightView.visibility=View.VISIBLE
                }
            }

            //단어장 추가 버튼 ClickListener
            addBtn.setOnClickListener {
                var customDialog = layoutInflater.inflate(R.layout.dialog_add_my_dic, null)
                var builder = AlertDialog.Builder(requireContext())
                builder.setView(customDialog)
                val dialog = builder.create()
                dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()

                var cancelBtn = customDialog.findViewById<Button>(R.id.diaAddCancelBtn)
                var okBtn = customDialog.findViewById<Button>(R.id.diaAddOKBtn)
                var dicText = customDialog.findViewById<TextInputLayout>(R.id.dicAddEditText)

                dicText.editText!!.addTextChangedListener(makeTextWatcher(okBtn, dicText))
                cancelBtn.setOnClickListener {
                    dialog.dismiss()
                }
                okBtn.setOnClickListener {
                    val newDicName = dicText.editText!!.text.toString()
                    dbHelper!!.createTable(newDicName)
                    dicList.add(DicData(newDicName, dbHelper!!.countVoc(newDicName.replace(" ", "_"))))
                    adapter!!.notifyDataSetChanged()
                    dialog.dismiss()
                }
                okBtn.isClickable = false
                okBtn.setBackgroundColor(Color.GRAY)
                dicText.requestFocus()
            }
        }

        val activity = requireActivity() as MainActivity
        dbHelper = activity.myDBHelper
        defaultDicGenerate(dbHelper!!, first, pref)
        if(MyDBHelper.TABLE_NAMES.size==0){
            dbHelper!!.initalizeDB()
            for(i:Int in 0 until MyDBHelper.TABLE_NAMES.size){
                dicList.add(DicData(MyDBHelper.TABLE_NAMES.get(i).replace("_", " "), dbHelper!!.countVoc(MyDBHelper.TABLE_NAMES.get(i))))
            }
            adapter!!.notifyDataSetChanged()
        }
    }

    private fun defaultDicGenerate(dbHelper: MyDBHelper, first:Boolean, pref:SharedPreferences){
        if(first){ //첫 실행
            MyDBHelper.TABLE_NAMES.add("기본_단어장")
            dbHelper.readDefaultDic()
            val editor:SharedPreferences.Editor = pref.edit()
            editor.putBoolean("isFirst", false)
            editor.commit()
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
                startActivity(intent)
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

                //custom Dialog 컴포넌트들 선언
                var cancelBtn = customDialog.findViewById<Button>(R.id.diaCancelBtn)
                var okBtn = customDialog.findViewById<Button>(R.id.diaOKBtn)
                var removeBtn = customDialog.findViewById<Button>(R.id.diaRemoveBtn)
                var dicText = customDialog.findViewById<TextInputLayout>(R.id.dicNameEditText)

                //각 컴포넌트에 ClickListener 선언
                cancelBtn.setOnClickListener {
                    dialog.dismiss()
                }
                okBtn.setOnClickListener {
                    val newDicName = dicText.editText!!.text.toString()
                    dbHelper!!.updateTable(data.dicName, newDicName)
                    dicList.get(position).dicName = newDicName
                    adapter!!.notifyDataSetChanged()
                    dialog.dismiss()
                }
                removeBtn.setOnClickListener {
                    showRemoveDialog(position, data, dialog)
                }

                //TextWatcher 설정
                dicText.editText!!.setText(dicList.get(position).dicName)
                okBtn.isClickable = false
                okBtn.setBackgroundColor(Color.GRAY)
                dicText.editText!!.addTextChangedListener(object :TextWatcher{
                    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                    override fun afterTextChanged(s: Editable?) {}
                    override fun onTextChanged(
                        s: CharSequence?,
                        start: Int,
                        before: Int,
                        count: Int
                    ) {
                        okBtn.isClickable = s!!.matches(Regex("^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9| ]+\$")) && count!=0 && !s.startsWith(" ")
                        if(!okBtn.isClickable){ // 한글 숫자 영어 아닐 때
                            okBtn.setBackgroundColor(Color.GRAY)
                            if(s.isEmpty()){
                                dicText.error = "글자를 입력해주세요"
                            }
                            else if(s.startsWith(" ")){
                                dicText.error = "공백만 입력할 수는 없습니다"
                            }
                            else{
                                dicText.error = "특수문자는 입력 불가능합니다"
                            }
                        }
                        else { //한글 숫자 영어 일때
                            if(dicListHas(s.toString(), position)){
                                okBtn.setBackgroundColor(Color.GRAY)
                                dicText.error = "같은 이름의 단어장이 존재합니다."
                            }
                            else if(data.dicName.equals(s.toString())){
                                okBtn.setBackgroundColor(Color.GRAY)
                                dicText.error = "동일한 이름으로 수정 불가능합니다."
                            }
                            else{
                                okBtn.setBackgroundColor(Color.BLACK)
                                dicText.error = null
                            }
                        }
                    }
                })
                 dicText.editText!!.requestFocus()

                }
            }
        }

    private fun showRemoveDialog(position:Int, data:DicData, prevDialog: AlertDialog){
        val alBuilder = AlertDialog.Builder(requireActivity(), R.style.DefaultDialogStyle);
        alBuilder.setMessage("삭제 버튼을 누르면 단어장이 삭제됩니다.");
        alBuilder.setPositiveButton("삭제") { _, _ ->
            dbHelper!!.deleteTable(data.dicName)
            adapter!!.notifyDataSetChanged()
            dicList.removeAt(position)
            prevDialog.dismiss()
        }
        alBuilder.setNegativeButton("취소") {_, _ ->

        }
        alBuilder.setTitle("단어장 삭제")
        alBuilder.show()
    }

    private fun dicListHas(dicName:String, position: Int):Boolean{
        for(i in 0 until dicList.size){
            if(dicList.get(i).dicName.equals(dicName) && i!=position){
                return true
            }
        }
        return false
    }

    private fun dicListJustHas(dicName:String):Boolean{
        for(i in 0 until dicList.size){
            if(dicList.get(i).dicName.equals(dicName)){
                return true
            }
        }
        return false
    }

    private fun makeTextWatcher(okBtn:Button, dicText:TextInputLayout):TextWatcher{
        return object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                okBtn.isClickable = s!!.matches(Regex("^[ㄱ-ㅎ|가-힣|a-z|A-Z|0-9| ]+\$")) && count!=0 && !s.startsWith(" ")
                if(!okBtn.isClickable){ // 한글 숫자 영어 아닐 때
                    okBtn.setBackgroundColor(Color.GRAY)
                    if(s.isEmpty()){
                        dicText.error = "글자를 입력해주세요"
                    }
                    else if(s.startsWith(" ")){
                        dicText.error = "공백만 입력할 수는 없습니다"
                    }
                    else{
                        dicText.error = "특수문자는 입력 불가능합니다"
                    }
                }
                else { //한글 숫자 영어 일때
                    if(dicListJustHas(s.toString())){
                        okBtn.setBackgroundColor(Color.GRAY)
                        dicText.error = "같은 이름의 단어장이 존재합니다."
                    }
                    else{
                        okBtn.setBackgroundColor(Color.BLACK)
                        dicText.error = null
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }
}