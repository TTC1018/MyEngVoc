package com.example.hwengvoc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.example.hwengvoc.databinding.ActivityMeanQuizBinding

class MeanQuizActivity : AppCompatActivity() {
    lateinit var binding: ActivityMeanQuizBinding
    lateinit var myDBHelper: MyDBHelper
    lateinit var dicName:String
    lateinit var option:String
    var vocList = mutableListOf<VocData>()
    var vocCounter = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMeanQuizBinding.inflate(layoutInflater)
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
            binding.mQuizWordText.text = "단어 없음"
            binding.mQuizAnswerText.visibility = View.GONE
            binding.mQuizNextBtn.visibility = View.GONE
            return
        }

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        binding.apply {
            mQuizWordText.text = vocList.first().word
            mQuizAnswerText.setOnClickListener {
                mQuizAnswerText.text = vocList[vocCounter].meaning
                mQuizAnswerText.startAnimation(fadeIn)
            }

            mQuizNextBtn.setOnClickListener {
                vocCounter++
                if(vocCounter==vocList.size){
                    if(option.equals("무작위")){
                        val prevVoc = vocList.last().word
                        do{
                            vocList.shuffle()
                        }while(prevVoc.equals(vocList.get(0).word))
                    }
                    vocCounter=0
                }
                mQuizWordText.text = vocList[vocCounter].word
                mQuizAnswerText.text = "정답 확인"
                mQuizWordText.startAnimation(fadeIn)
                mQuizAnswerText.startAnimation(fadeIn)
            }
        }
    }
}