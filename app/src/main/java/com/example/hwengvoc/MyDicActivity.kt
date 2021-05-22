package com.example.hwengvoc

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.hwengvoc.databinding.ActivityMyDicBinding
import java.util.*

class MyDicActivity : AppCompatActivity() {
    lateinit var binding: ActivityMyDicBinding
    lateinit var myDBHelper: MyDBHelper

    var vocData = LinkedList<VocData>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMyDicBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    fun init(){
        val intent = intent
        val DicData = intent.getSerializableExtra("dic") as DicData

        myDBHelper = MyDBHelper(this)
        vocData = myDBHelper.findDic(DicData.dicName.replace(" ", "_"))
        binding.textView3.setText(vocData.size.toString())
    }
}