package com.example.hwengvoc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.activity.viewModels
import androidx.core.graphics.component2
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.hwengvoc.databinding.ActivityMainBinding
import com.example.hwengvoc.databinding.FragmentMyDicBinding
import com.example.hwengvoc.databinding.FragmentSearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup

class MainActivity : AppCompatActivity() {
    private val dicFragment by lazy {MyDicFragment()}
    private val searchFragment by lazy {SearchFragment()}
    private val quizFragment by lazy {QuizFragment()}
    private val manageFragment by lazy {SettingFragment()}

    lateinit var binding: ActivityMainBinding
    lateinit var myDBHelper: MyDBHelper
    lateinit var fragmentManager: FragmentManager
    val myViewModel: MyViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        fragmentManager = supportFragmentManager
        setContentView(binding.root)
        init()
    }

    private fun init() {
        binding.apply{
            bottomNavi.setOnNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.searchBtn -> {
                        changeFragment(searchFragment)
                    }
                    R.id.myDicBtn -> {
                        changeFragment(dicFragment)
                    }
                    R.id.quizBtn -> {
                        changeFragment(quizFragment)
                    }
                    R.id.manageBtn -> {
                        changeFragment(manageFragment)
                    }
                }
                true
            }
            binding!!.bottomNavi.selectedItemId = R.id.myDicBtn
        }

        myDBHelper= MyDBHelper(this)
//        myDBHelper.initDB()
    }

    private fun changeFragment(fragment: Fragment) {
        fragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            .addToBackStack(null)
            .replace(R.id.fragContainer, fragment)
            .commit()
    }
}