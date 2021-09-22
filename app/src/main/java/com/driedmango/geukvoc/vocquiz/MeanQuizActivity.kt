package com.driedmango.geukvoc.vocquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import com.driedmango.geukvoc.MyDBHelper
import com.driedmango.geukvoc.R
import com.driedmango.geukvoc.data.VocData
import com.driedmango.geukvoc.databinding.ActivityMeanQuizBinding

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
        val fadeInAndOut = AnimationUtils.loadAnimation(this, R.anim.fade_inandout)
        binding.apply {
            mQuizWordText.text = vocList.first().word
            mQuizAnswerText.startAnimation(fadeInAndOut)
            mQuizAnswerText.setOnClickListener {
                mQuizAnswerText.isClickable = false
                mQuizAnswerText.text = vocList[vocCounter].meaning
                mQuizAnswerText.startAnimation(fadeIn)
            }

            mQuizNextBtn.setOnClickListener {
                if(mQuizAnswerText.isClickable){
                    mQuizAnswerText.performClick()
                }
                else{
                    mQuizAnswerText.isClickable = true
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
                    mQuizAnswerText.startAnimation(fadeInAndOut)
                }
            }
        }
    }
}