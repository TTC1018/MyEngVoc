package com.driedmango.geukvoc.vocquiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import com.driedmango.geukvoc.MyDBHelper
import com.driedmango.geukvoc.R
import com.driedmango.geukvoc.data.VocData
import com.driedmango.geukvoc.databinding.ActivitySpellQuizBinding

class SpellQuizActivity : AppCompatActivity() {
    lateinit var binding: ActivitySpellQuizBinding
    lateinit var myDBHelper: MyDBHelper
    lateinit var dicName:String
    lateinit var option:String
    var vocList = mutableListOf<VocData>()
    var vocCounter = 0
    var chanceCounter = 0
    var cycleCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySpellQuizBinding.inflate(layoutInflater)
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
            binding.sQuizMeanText.text = "단어 없음"
            binding.sQuizHintLay.visibility = View.GONE
            binding.sQuizNextBtn.visibility = View.GONE
            return
        }

        val fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in)
        val fadeInAndOut = AnimationUtils.loadAnimation(this, R.anim.fade_inandout)
        binding.apply {
            sQuizMeanText.text = vocList.first().meaning
            sQuizFirstSpell.startAnimation(fadeInAndOut)
            sQuizSpellCount.startAnimation(fadeInAndOut)

            sQuizFirstSpell.setOnClickListener {
                sQuizFirstSpell.isClickable = false
                sQuizFirstSpell.text = vocList[vocCounter].word.slice(0 until 1)
                sQuizFirstSpell.startAnimation(fadeIn)
            }

            sQuizSpellCount.setOnClickListener {
                sQuizSpellCount.isClickable = false
                sQuizSpellCount.text = vocList[vocCounter].word.length.toString()
                sQuizSpellCount.startAnimation(fadeIn)
            }

            sQuizNextBtn.setOnClickListener {
                chanceCounter++
                //정답
                if(sQuizSpellInput.text.toString().equals(vocList[vocCounter].word)){
                    sQuizFirstSpell.isClickable = true
                    sQuizSpellCount.isClickable = true
                    chanceCounter=0
                    vocCounter++
                    if(vocCounter==vocList.size){
                        noOverlapShuffle()
                        vocCounter=0
                        cycleCounter++
                        sQuizCycleNum.text = cycleCounter.toString()
                    }
                    sQuizMeanText.text = vocList[vocCounter].meaning
                    sQuizFirstSpell.text = "확인"
                    sQuizSpellCount.text = "확인"

                    sQuizFirstSpell.startAnimation(fadeInAndOut)
                    sQuizSpellCount.startAnimation(fadeInAndOut)
                    sQuizMeanText.startAnimation(fadeIn)
                    sQuizHintLay.startAnimation(fadeIn)
                    Toast.makeText(applicationContext, "정답!", Toast.LENGTH_SHORT).show()
                }
                //오답
                else{
                    if(chanceCounter==4){
                        Toast.makeText(applicationContext, "한번 더 틀리면 정답이 공개됩니다", Toast.LENGTH_SHORT).show()
                    }
                    else if(chanceCounter==5){
                        sQuizFirstSpell.isClickable = true
                        sQuizSpellCount.isClickable = true
                        chanceCounter = 0
                        val answer = vocList[vocCounter].word
                        val meaning = vocList[vocCounter].meaning
                        vocCounter++
                        if(vocCounter==vocList.size){
                            noOverlapShuffle()
                            vocCounter=0
                            cycleCounter++
                            sQuizCycleNum.text = cycleCounter.toString()
                        }
                        sQuizMeanText.text = vocList[vocCounter].meaning
                        sQuizFirstSpell.text = "확인"
                        sQuizSpellCount.text = "확인"

                        sQuizFirstSpell.startAnimation(fadeInAndOut)
                        sQuizSpellCount.startAnimation(fadeInAndOut)
                        sQuizMeanText.startAnimation(fadeIn)
                        sQuizHintLay.startAnimation(fadeIn)
                        Toast.makeText(applicationContext, "$meaning: $answer", Toast.LENGTH_LONG).show()
                    }
                    else{
                        Toast.makeText(applicationContext, "오답입니다. 조금 더 고민해보세요", Toast.LENGTH_SHORT).show()
                    }
                }
                sQuizSpellInput.text.clear()
            }

            // 답 버튼
            sQuizSkipBtn.setOnClickListener {
                sQuizFirstSpell.isClickable = true
                sQuizSpellCount.isClickable = true
                chanceCounter = 0
                val answer = vocList[vocCounter].word
                val meaning = vocList[vocCounter].meaning
                vocCounter++
                if(vocCounter==vocList.size){
                    noOverlapShuffle()
                    vocCounter=0
                    cycleCounter++
                    sQuizCycleNum.text = cycleCounter.toString()
                }
                sQuizMeanText.text = vocList[vocCounter].meaning
                sQuizFirstSpell.text = "확인"
                sQuizSpellCount.text = "확인"

                sQuizFirstSpell.startAnimation(fadeInAndOut)
                sQuizSpellCount.startAnimation(fadeInAndOut)
                sQuizMeanText.startAnimation(fadeIn)
                sQuizHintLay.startAnimation(fadeIn)
                Toast.makeText(applicationContext, "$meaning = $answer", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun noOverlapShuffle(){
        if(option.equals("무작위")){
            val prevVoc = vocList.last().meaning
            do{
                vocList.shuffle()
            }while(prevVoc.equals(vocList.get(0).meaning))
        }
    }
}