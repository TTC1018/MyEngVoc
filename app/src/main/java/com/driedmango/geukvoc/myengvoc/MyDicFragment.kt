package com.driedmango.geukvoc.myengvoc

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.driedmango.geukvoc.*
import com.driedmango.geukvoc.data.DicData
import com.driedmango.geukvoc.databinding.FragmentMyDicBinding
import com.driedmango.geukvoc.viewmodel.MyDicViewModel
import com.google.android.material.textfield.TextInputLayout


class MyDicFragment : BaseFragment<FragmentMyDicBinding>(R.layout.fragment_my_dic) {
    private val viewModel by activityViewModels<MyDicViewModel>()
    private lateinit var recyclerView:RecyclerView
    val adapter: MyDicRecyclerViewAdapter = MyDicRecyclerViewAdapter(diffUtil)
    var dbHelper: MyDBHelper?=null
    var editFlag:Boolean = false

    override fun onResume() {
        super.onResume()
        getCurrentList()?.let {
            if(it.size != 0){
                for(i in 0 until it.size){
                    for(TABLE_NAME in MyDBHelper.TABLE_NAMES){
                        if(it[i].dicName == TABLE_NAME.replace("_", " ")){
                            it[i].wordCount = dbHelper!!.countVoc(TABLE_NAME)
                        }
                    }
                }
            }

            if(it.size==0 && MyDBHelper.TABLE_NAMES.size!=0){
                for(TABLE_NAME in MyDBHelper.TABLE_NAMES){
                    it.add(DicData(TABLE_NAME.replace("_", " "), dbHelper!!.countVoc(TABLE_NAME)))
                }
                adapter.submitList(it.toMutableList())
            }
        }
        val activity = requireActivity() as MainActivity
        activity.binding.bottomNavi.menu.getItem(1).isChecked = true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        //어플 최조 실행을 확인 해주는 변수 (기본 단어장 추가에 활용)
        val pref: SharedPreferences = requireContext().getSharedPreferences("isFirst", Activity.MODE_PRIVATE)
        val first:Boolean = pref.getBoolean("isFirst", true)

        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        // 데이터베이스 불러오기
        val activity = requireActivity() as MainActivity
        dbHelper = activity.myDBHelper
        dbHelper?.let { helper ->
            defaultDicGenerate(helper, first, pref)
            if(MyDBHelper.TABLE_NAMES.size==0){
                helper.initalizeDB()
                viewModel.loadDB(helper)
                adapter.submitList(viewModel.dicList.value?.toMutableList())
            }
        }
        binding.apply {

            //변수들 초기화
            recyclerView = recyclerDic
            adapter.itemClickListener = defItemClickListener()
            recyclerView.layoutManager = GridLayoutManager(context, 2)
            recyclerView.adapter = adapter



            //단어장 수정 버튼 ClickListener
            editBtn.setOnClickListener {
                val fadeIn = AnimationUtils.loadAnimation(context, R.anim.fade_in)
                val fadeOut = AnimationUtils.loadAnimation(context, R.anim.fade_out)

                if(recyclerView.layoutManager is GridLayoutManager){
                    recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                    recyclerView.startAnimation(fadeIn)
                    adapter.setOnItemClickListener(editItemClickListener())
                    editFlag = true
                }
                else{
                    recyclerView.layoutManager = GridLayoutManager(context, 2)
                    recyclerView.startAnimation(fadeIn)
                    adapter.setOnItemClickListener(defItemClickListener())
                    editFlag = false
                }

                val editShowText:TextView = binding.editShowText
                val addBtn: ImageButton = binding.addBtn
                val weightView:View = binding.weightView
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
                val customDialog = layoutInflater.inflate(R.layout.dialog_add_my_dic, null)
                val builder = AlertDialog.Builder(requireContext())
                builder.setView(customDialog)
                val dialog = builder.create()
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()

                val cancelBtn = customDialog.findViewById<Button>(R.id.diaAddCancelBtn)
                val okBtn = customDialog.findViewById<Button>(R.id.diaAddOKBtn)
                val dicText = customDialog.findViewById<TextInputLayout>(R.id.dicAddEditText)

                dicText.editText!!.addTextChangedListener(makeTextWatcher(okBtn, dicText))
                cancelBtn.setOnClickListener {
                    dialog.dismiss()
                }
                okBtn.setOnClickListener {
                    val newDicName = dicText.editText!!.text.toString()
                    dbHelper!!.createTable(newDicName)
                    getCurrentList()?.let {
                        it.add(DicData(newDicName, dbHelper!!.countVoc(newDicName.replace(" ", "_"))))
                        adapter.submitList(it.toMutableList())
                    }
                    dialog.dismiss()
                }
                okBtn.isClickable = false
                okBtn.setBackgroundColor(Color.GRAY)
                dicText.requestFocus()
            }
        }
    }

    private fun defaultDicGenerate(dbHelper: MyDBHelper, first:Boolean, pref:SharedPreferences){
        if(first){ //첫 실행
            MyDBHelper.TABLE_NAMES.add("기본_단어장")
            dbHelper.readDefaultDic()
            val editor:SharedPreferences.Editor = pref.edit()
            editor.putBoolean("isFirst", false)
            editor.apply()
        }
    }

    private fun defItemClickListener(): MyDicRecyclerViewAdapter.OnItemClickListener {
        return object: MyDicRecyclerViewAdapter.OnItemClickListener {
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

    private fun editItemClickListener(): MyDicRecyclerViewAdapter.OnItemClickListener {
        return object: MyDicRecyclerViewAdapter.OnItemClickListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun OnItemClick(
                holder: MyDicRecyclerViewAdapter.ViewHolder,
                view: View,
                data: DicData,
                position: Int
            ) {
                val customDialog = layoutInflater.inflate(R.layout.dialog_edit_my_dic, null)
                val builder = AlertDialog.Builder(context!!)
                builder.setView(customDialog)
                val dialog = builder.create()
                dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()

                //custom Dialog 컴포넌트들 선언
                val cancelBtn = customDialog.findViewById<Button>(R.id.diaCancelBtn)
                val okBtn = customDialog.findViewById<Button>(R.id.diaOKBtn)
                val removeBtn = customDialog.findViewById<Button>(R.id.diaRemoveBtn)
                val dicText = customDialog.findViewById<TextInputLayout>(R.id.dicNameEditText)

                //각 컴포넌트에 ClickListener 선언
                cancelBtn.setOnClickListener {
                    dialog.dismiss()
                }
                okBtn.setOnClickListener {
                    val newDicName = dicText.editText!!.text.toString()
                    dbHelper!!.updateTable(data.dicName, newDicName)
                    val currentList = viewModel.dicList.value?.toMutableList()
                    currentList?.let {
                        it[position].dicName = newDicName
                        adapter.submitList(it.toMutableList())
                    }
                    dialog.dismiss()
                }
                removeBtn.setOnClickListener {
                    showRemoveDialog(position, data, dialog)
                }

                //TextWatcher 설정
                dicText.editText!!.setText(getCurrentList()!![position].dicName)
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
                        okBtn.isClickable = s!!.matches(Regex("^[\\p{Alnum}|\\s]+\$")) && s.isNotEmpty() && !s.startsWith(" ") && !s.endsWith(" ")
                        if(!okBtn.isClickable){ // 한글 숫자 영어 아닐 때
                            okBtn.setBackgroundColor(Color.GRAY)
                            if(s.isEmpty()){
                                dicText.error = "글자를 입력해주세요"
                            }
                            else if(s.startsWith(" ")){
                                dicText.error = "공백만 입력할 수는 없습니다"
                            }
                            else if(s.endsWith(" ")){
                                dicText.error = "공백으로 끝날 수 없습니다"
                            }
                            else{
                                dicText.error = "올바른 문자를 입력해주세요"
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

    private fun showRemoveDialog(position:Int, data: DicData, prevDialog: AlertDialog){
        val alBuilder = AlertDialog.Builder(requireActivity(), R.style.DefaultDialogStyle);
        alBuilder.setMessage("삭제 버튼을 누르면 단어장이 삭제됩니다.");
        alBuilder.setPositiveButton("삭제") { _, _ ->
            dbHelper!!.deleteTable(data.dicName)
            val currentList = viewModel.dicList.value?.toMutableList()
            currentList?.let {
                it.removeAt(position)
                adapter.submitList(it.toMutableList())
            }
            prevDialog.dismiss()
        }
        alBuilder.setNegativeButton("취소") {_, _ ->

        }
        alBuilder.setTitle("단어장 삭제")
        alBuilder.show()
    }

    private fun dicListHas(dicName:String, position: Int):Boolean{
        getCurrentList()?.let {
            for(i in 0 until it.size){
                if(it[i].dicName == dicName && i!=position){
                    return true
                }
            }
        }
        return false
    }

    private fun dicListJustHas(dicName:String):Boolean{
        getCurrentList()?.let {
            for(i in 0 until it.size){
                if(it[i].dicName == dicName){
                    return true
                }
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
                okBtn.isClickable = s!!.matches(Regex("^[\\p{Alnum}|\\s]+\$")) && s.isNotEmpty() && !s.startsWith(" ") && !s.endsWith(" ")
                if(!okBtn.isClickable){ // 한글 숫자 영어 아닐 때
                    okBtn.setBackgroundColor(Color.GRAY)
                    if(s.isEmpty()){
                        dicText.error = "글자를 입력해주세요"
                    }
                    else if(s.startsWith(" ")){
                        dicText.error = "공백으로 시작할 수 없습니다"
                    }
                    else if(s.endsWith(" ")){
                        dicText.error = "공백으로 끝날 수 없습니다"
                    }
                    else{
                        dicText.error = "올바른 문자를 입력해주세요"
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

    fun getCurrentList(): MutableList<DicData>? = viewModel.dicList.value?.toMutableList()




    companion object {
        fun newInstance() = MyDicFragment()
        val diffUtil = object : DiffUtil.ItemCallback<DicData>() {
            override fun areItemsTheSame(oldItem: DicData, newItem: DicData): Boolean {
                return oldItem.dicName == newItem.dicName
            }

            override fun areContentsTheSame(oldItem: DicData, newItem: DicData): Boolean {
                return oldItem == newItem
            }
        }
    }
}