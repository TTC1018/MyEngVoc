package com.driedmango.geukvoc.myengvoc

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.driedmango.geukvoc.*
import com.driedmango.geukvoc.data.DicData
import com.driedmango.geukvoc.data.VocData
import com.driedmango.geukvoc.databinding.ActivityMyDicBinding
import com.driedmango.geukvoc.wordsearch.SearchRecyclerViewAdapter
import com.google.android.material.textfield.TextInputLayout
import java.util.*

class MyDicActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyDicBinding
    lateinit var myDBHelper: MyDBHelper
    private var adapter: SearchRecyclerViewAdapter = SearchRecyclerViewAdapter(diffUtil)
    lateinit var recyclerView: RecyclerView
    lateinit var dicData: DicData
    lateinit var dicName:String
    var wordFlag:Boolean=false
    var meanFlag:Boolean=false
    var alignFlag:Int=2

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

        //RecyclerView 관련 설정
        recyclerView = binding.showVocRecycler
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        adapter.submitList(myDBHelper.findDic(dicName).toMutableList())
        recyclerView.adapter = adapter
        val simpleCallback = object:
            ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT){
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val currentList = adapter.currentList.toMutableList()
                myDBHelper.deleteVoc(currentList[position].word, dicName)
                currentList.removeAt(position)
                adapter.submitList(currentList)
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
        itemTouchHelper.attachToRecyclerView(recyclerView)
        recyclerView.scrollToPosition(adapter.itemCount - 1)

            //단어 추가 버튼 Listener 설정
            binding.showDicAddBtn.setOnClickListener {
                val customDialog = layoutInflater.inflate(R.layout.dialog_add_voc, null)
                val builder = AlertDialog.Builder(this)
                builder.setView(customDialog)
                val dialog = builder.create()
                dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()

                //custom Dialog 컴포넌트들 선언
                val cancelBtn = customDialog.findViewById<Button>(R.id.vocRegiCancelBtn)
                val okBtn = customDialog.findViewById<Button>(R.id.vocRegiOKBtn)
                val wordText = customDialog.findViewById<TextInputLayout>(R.id.vocWordEditText)
                val meanText = customDialog.findViewById<TextInputLayout>(R.id.vocMeanEditText)

                //각 컴포넌트에 ClickListener 선언
                cancelBtn.setOnClickListener {
                    dialog.dismiss()
                }
                okBtn.setOnClickListener {
                    val newWord = wordText.editText!!.text.toString()
                    val newMean = meanText.editText!!.text.toString()
                    myDBHelper.insertVoc(VocData(0, newWord, newMean), dicName)

                    val currentList = adapter.currentList.toMutableList()
                    currentList.add(VocData(adapter.itemCount, newWord, newMean))
                    adapter.submitList(currentList)
                    wordText.editText!!.text.clear()
                    meanText.editText!!.text.clear()
                    Toast.makeText(this, "단어가 추가되었습니다", Toast.LENGTH_SHORT).show()
                    wordText.editText!!.requestFocus()
                    recyclerView.scrollToPosition(adapter.itemCount-1)
                }

                //TextWatcher 설정
                okBtn.isClickable = false
                okBtn.setBackgroundColor(Color.GRAY)
                wordText.editText!!.addTextChangedListener(makeTextWatcher(okBtn, wordText.editText!!, 1))
                meanText.editText!!.addTextChangedListener(makeTextWatcher(okBtn, meanText.editText!!, 2))
                wordText.requestFocus()
            }

            //정렬 버튼
            val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
            val fadeInAndOut = AnimationUtils.loadAnimation(this, R.anim.fade_inandout)
            binding.alignView.startAnimation(fadeInAndOut)
            binding.alignView.setOnClickListener {
                val currentList = adapter.currentList.toMutableList()
                when(alignFlag){
                    1->{
                        currentList.sortBy {
                            it.word
                        }
                        adapter.submitList(currentList)
                        recyclerView.startAnimation(fadeIn)
                        alignFlag = 2;
                    }
                    2->{
                        currentList.sortByDescending {
                            it.word
                        }
                        adapter.submitList(currentList)
                        recyclerView.startAnimation(fadeIn)
                        alignFlag = 1;
                    }
                }
            }

            // 단어 검색 EditText
            binding.vocSearchEditText.addTextChangedListener(object:TextWatcher{
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) { }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val target = s.toString()

                    var currentList = adapter.currentList.toMutableList()
                    if(count != 0){

                        if(target.matches(Regex("^[a-z|A-Z|\\s]+\$"))){
                            currentList = currentList.filter {
                                it.word.startsWith(target)
                            }.toMutableList()
                        }
                        else if(target.matches(Regex("^[가-힣|0-9|()?,~\\-\\/\\s]+\$"))){
                            currentList = currentList.filter {
                                it.meaning.matches(Regex(target))
                            }.toMutableList()
                        }
                        binding.vocSearchEditText.post {
                            binding.vocSearchEditText.requestFocus()
                        }
                    }
                    else{
                        currentList = myDBHelper.findDic(dicName)
                    }
                    adapter.submitList(currentList)
                }

                override fun afterTextChanged(s: Editable?) { }
            })


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
                when(type){ //1:단어 2:뜻
                    1-> {
                        wordFlag = s!!.matches(Regex("^[\\p{Graph}|\\s]+\$")) && s.isNotEmpty() && !s.startsWith(" ")
                        btn.isClickable = wordFlag && meanFlag
                        if(!wordFlag){ // 영어 아닐 때
                            btn.setBackgroundColor(Color.GRAY)
                            if(s.isEmpty()){
                                text.error = "단어를 입력해주세요"
                            }
                            else if(s.startsWith(" ")){
                                text.error = "공백으로 시작할 수 없습니다"
                            }
                            else{
                                text.error = "올바른 문자를 입력해주세요"
                            }
                        }
                        else { //한글 숫자 영어 일때
                            if(vocListHas(s.toString())){
                                btn.setBackgroundColor(Color.GRAY)
                                text.error = "이미 등록된 단어입니다"
                            }
                            else{
                                text.error = null
                            }
                        }
                    }
                    2-> {
                        meanFlag = s!!.matches(Regex("^[\\p{Graph}|\\s]+\$")) && s.isNotEmpty() && !s.startsWith(" ")
                        btn.isClickable = wordFlag && meanFlag
                        if(!meanFlag){ // 한글 아닐 때
                            btn.setBackgroundColor(Color.GRAY)
                            if(s.isEmpty()){
                                text.error = "뜻을 입력해주세요"
                            }
                            else if(s.startsWith(" ")){
                                text.error = "공백으로 시작할 수 없습니다"
                            }
                            else{
                                text.error = "올바른 문자를 입력해주세요"
                            }
                        }
                        else { //한글 일 때
                            text.error = null
                        }
                    }
                }

                if(wordFlag && meanFlag)
                    btn.setBackgroundColor(Color.BLACK)
            }
        }
    }

    private fun vocListHas(editText: String): Boolean {
        val currentList = adapter.currentList
        for(i in 0 until currentList.size){
            if(currentList[i].word == editText){
                return true
            }
        }
        return false
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<VocData>() {
            override fun areItemsTheSame(oldItem: VocData, newItem: VocData): Boolean {
                return oldItem.vid == newItem.vid
            }

            override fun areContentsTheSame(oldItem: VocData, newItem: VocData): Boolean {
                return oldItem == newItem
            }
        }
    }
}