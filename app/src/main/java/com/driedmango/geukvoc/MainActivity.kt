package com.driedmango.geukvoc


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.driedmango.geukvoc.databinding.ActivityMainBinding
import com.driedmango.geukvoc.myengvoc.MyDicFragment
import com.driedmango.geukvoc.settings.SettingFragment
import com.driedmango.geukvoc.vocquiz.QuizFragment
import com.driedmango.geukvoc.wordsearch.SearchFragment
import java.util.*

class MainActivity : AppCompatActivity() {
    private val dicFragment by lazy { MyDicFragment() }
    private val searchFragment by lazy { SearchFragment() }
    private val quizFragment by lazy { QuizFragment() }
    private val manageFragment by lazy { SettingFragment() }

    lateinit var binding: ActivityMainBinding
    lateinit var myDBHelper: MyDBHelper

    var fragNum = Stack<Int>()
    var backStackCount:Int = 0
    var finishFlag:Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    override fun onResume() {
        super.onResume()
        dicFragment.recyclerView!!.adapter!!.notifyDataSetChanged()
    }

    private fun init() {
        binding.apply{
            bottomNavi.setOnNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.searchBtn -> {
                        changeFragment(searchFragment, "search")
                    }
                    R.id.myDicBtn -> {
                        changeFragment(dicFragment, "mydic")
                    }
                    R.id.quizBtn -> {
                        changeFragment(quizFragment, "quiz")
                    }
                    R.id.manageBtn -> {
                        changeFragment(manageFragment, "manage")
                    }
                }
                true
            }
            bottomNavi.selectedItemId = R.id.myDicBtn
            bottomNavi.setItemIconTintList(null)
        }

        //BackStack과 BottomNavigation 동기화 코드
        supportFragmentManager.addOnBackStackChangedListener {
            val entryCount = supportFragmentManager.backStackEntryCount
            if(entryCount<backStackCount){
                backStackCount--
                when(fragNum.pop()){
                    0-> binding.bottomNavi.menu.getItem(0).setChecked(true)
                    1-> binding.bottomNavi.menu.getItem(1).setChecked(true)
                    2-> binding.bottomNavi.menu.getItem(2).setChecked(true)
                    3-> binding.bottomNavi.menu.getItem(3).setChecked(true)
                }
            }
        }

        myDBHelper= MyDBHelper(this)
    }

    private fun changeFragment(fragment: Fragment, tag:String) {
        updateFragNum()
        supportFragmentManager
            .beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, R.anim.fade_in, R.anim.fade_out)
            .addToBackStack(tag)
            .replace(R.id.fragContainer, fragment, tag)
            .commit()

        backStackCount++
        finishFlag=false
//        Toast.makeText(this, backStackCount.toString(), Toast.LENGTH_SHORT).show()
    }

    private fun updateFragNum(){
        //BackStack과 BottomNavigation을 동기화 하기 위한 코드
        if(supportFragmentManager.backStackEntryCount > 0){
            val backStack = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount-1)
            when(backStack.name){
                "search"->fragNum.push(0)
                "mydic"->fragNum.push(1)
                "quiz"->fragNum.push(2)
                "manage"->fragNum.push(3)
            }
        }
    }

    private fun showCloseDialog(){
        val alBuilder = AlertDialog.Builder(this, R.style.DefaultDialogStyle);
        alBuilder.setMessage("종료 할까요?");
        alBuilder.setPositiveButton("종료") { _, _ ->
            finish()
        }
        alBuilder.setNegativeButton("취소") {_, _ ->

        }
        alBuilder.setTitle("나만의 단어장")
        alBuilder.show()
    }

    override fun onBackPressed() {
        if(binding.bottomNavi.selectedItemId == R.id.myDicBtn){
            if(dicFragment.editFlag){
                dicFragment.binding!!.editBtn.performClick()
                return
            }
        }

        if(backStackCount==1){
            finishFlag=true
            if(finishFlag){
                showCloseDialog()
            }
            return
        }
        super.onBackPressed()
    }
}