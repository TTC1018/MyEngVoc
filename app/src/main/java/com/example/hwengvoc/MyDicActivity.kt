package com.example.hwengvoc

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hwengvoc.databinding.ActivityMyDicBinding
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class MyDicActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyDicBinding
    lateinit var myDBHelper: MyDBHelper
    lateinit var adapter:SearchRecyclerViewAdapter
    lateinit var recyclerView: RecyclerView
    lateinit var dicData:DicData
    lateinit var dicName:String
    var vocData = LinkedList<VocData>()
    var wordFlag:Boolean=false
    var meanFlag:Boolean=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMyDicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    fun init(){
        val intent = intent
        dicData = intent.getSerializableExtra("dic") as DicData
        dicName = dicData.dicName.replace(" ", "_")

        //DBHelper 생성 및 단어 리스트 Activty 변수에 담기
        myDBHelper = MyDBHelper(this)
        vocData = myDBHelper.findDic(dicName)

        //RecyclerView 관련 설정
        recyclerView = binding.showVocRecycler
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapter = SearchRecyclerViewAdapter(vocData)
        recyclerView.adapter = adapter
        val simpleCallback = object:
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                myDBHelper!!.deleteVoc(vocData[position].word, dicName)
                adapter!!.removeItem(position)
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

        //단어 추가 버튼 Listener 설정
        binding.showDicAddBtn.setOnClickListener {
            var customDialog = layoutInflater.inflate(R.layout.dialog_add_voc, null)
            var builder = AlertDialog.Builder(this)
            builder.setView(customDialog)
            val dialog = builder.create()
            dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            //custom Dialog 컴포넌트들 선언
            var cancelBtn = customDialog.findViewById<Button>(R.id.vocRegiCancelBtn)
            var okBtn = customDialog.findViewById<Button>(R.id.vocRegiOKBtn)
            var wordText = customDialog.findViewById<TextInputLayout>(R.id.vocWordEditText)
            var meanText = customDialog.findViewById<TextInputLayout>(R.id.vocMeanEditText)

            //각 컴포넌트에 ClickListener 선언
            cancelBtn.setOnClickListener {
                dialog.dismiss()
            }
            okBtn.setOnClickListener {
                val newWord = wordText.editText!!.text.toString()
                val newMean = meanText.editText!!.text.toString()
                myDBHelper!!.insertVoc(VocData(0, newWord, newMean), dicName)
                vocData.add(VocData(vocData.size, newWord, newMean))
                adapter!!.notifyDataSetChanged()
                wordText.editText!!.text.clear()
                meanText.editText!!.text.clear()
                Toast.makeText(this, "단어가 추가되었습니다", Toast.LENGTH_SHORT).show()
                wordText.editText!!.requestFocus()
            }

            //TextWatcher 설정
            okBtn.isClickable = false
            okBtn.setBackgroundColor(Color.GRAY)
            wordText.editText!!.addTextChangedListener(makeTextWatcher(okBtn, wordText.editText!!, 1))
            meanText.editText!!.addTextChangedListener(makeTextWatcher(okBtn, meanText.editText!!, 2))
            wordText.requestFocus()
        }
    }

    private fun makeTextWatcher(btn:Button, text:EditText, type:Int):TextWatcher{
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {}
            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
                val originColor = btn.solidColor

                when(type){ //1:단어 2:뜻
                    1-> {
                        wordFlag = s!!.matches(Regex("^[a-z|A-Z| ]+\$"))
                        btn.isClickable = wordFlag && meanFlag
                        if(!wordFlag){ // 영어 아닐 때
                            btn.setBackgroundColor(Color.GRAY)
                            if(s!!.isEmpty()){
                                text.error = "단어를 입력해주세요"
                            }
                            else{
                                text.error = "영어만 입력 가능합니다"
                            }
                        }
                        else { //한글 숫자 영어 일때
                            if(vocListHas(s.toString())){
                                btn.setBackgroundColor(Color.GRAY)
                                text.error = "같은 이름의 단어가 존재합니다."
                            }
                            else{
                                text.error = null
                            }
                        }
                    }
                    2-> {
                        meanFlag = s!!.matches(Regex("^[ㄱ-ㅎ|가-힣|0-9|()?,~\\-\\/ ]+\$"))
                        btn.isClickable = wordFlag && meanFlag
                        if(!meanFlag){ // 한글 아닐 때
                            btn.setBackgroundColor(Color.GRAY)
                            if(s!!.isEmpty()){
                                text.error = "뜻을 입력해주세요"
                            }
                            else{
                                text.error = "한글, 숫자, 특정 기호만 입력 가능합니다"
                            }
                        }
                        else { //한글 일 때
                            text.error = null
                        }
                    }
                }

                if(wordFlag && meanFlag)
                    btn.setBackgroundColor(originColor)
            }
        }
    }

    private fun vocListHas(editText: String): Boolean {
        for(i in 0 until vocData.size){
            if(vocData.get(i).word.equals(editText)){
                return true
            }
        }
        return false
    }
}