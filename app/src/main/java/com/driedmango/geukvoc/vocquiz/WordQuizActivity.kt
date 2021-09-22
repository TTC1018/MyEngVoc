package com.driedmango.geukvoc.vocquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.driedmango.geukvoc.MyDBHelper
import com.driedmango.geukvoc.R
import com.driedmango.geukvoc.data.VocData
import com.driedmango.geukvoc.databinding.ActivityWordQuizBinding

class WordQuizActivity : AppCompatActivity() {
    lateinit var binding:ActivityWordQuizBinding
    lateinit var myDBHelper: MyDBHelper
    lateinit var dicName:String
    lateinit var option:String
    var vocList = mutableListOf<VocData>()
    var vocCounter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWordQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)
        myDBHelper = MyDBHelper(this)
        loadVocs()
        init()
    }

    private fun loadVocs() {
        val intent = intent
        dicName = intent.getStringExtra("dicName")!!.replace(" ", "_")
        option = intent.getStringExtra("option") as String

        vocList = myDBHelper.findDic(dicName)
        if(option.equals("무작위")){
            vocList.shuffle()
        }
    }

    private fun init(){
        if(vocList.isEmpty()){
            binding.wQuizMeanText.text = "단어 없음"
            binding.wQuizAnswerText.visibility = View.GONE
            binding.wQuizNextBtn.visibility = View.GONE
            return
        }

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val fadeInAndOut = AnimationUtils.loadAnimation(this, R.anim.fade_inandout)
        binding.apply {
            wQuizMeanText.text = vocList.first().meaning
            wQuizAnswerText.startAnimation(fadeInAndOut)
            wQuizAnswerText.setOnClickListener {
                wQuizAnswerText.isClickable = false
                wQuizAnswerText.text = vocList[vocCounter].word
                wQuizAnswerText.startAnimation(fadeIn)
            }

            wQuizNextBtn.setOnClickListener {
                if(wQuizAnswerText.isClickable){
                    wQuizAnswerText.performClick()
                }
                else {
                    wQuizAnswerText.isClickable = true
                    vocCounter++
                    if(vocCounter==vocList.size){
                        if(option.equals("무작위")){
                            val prevVoc = vocList.last().meaning
                            do{
                                vocList.shuffle()
                            }while(prevVoc.equals(vocList.get(0).meaning))
                        }
                        vocCounter=0
                    }
                    wQuizMeanText.text = vocList[vocCounter].meaning
                    wQuizAnswerText.text = "정답 확인"
                    wQuizMeanText.startAnimation(fadeIn)
                    wQuizAnswerText.startAnimation(fadeInAndOut)
                }
            }
        }
    }
}