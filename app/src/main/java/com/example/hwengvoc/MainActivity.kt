package com.example.hwengvoc

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.graphics.component2
import androidx.core.view.get
import androidx.fragment.app.Fragment
import com.example.hwengvoc.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val dicFragment by lazy {MyDicFragment()}
    private val searchFragment by lazy {SearchFragment()}

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init() {
        with(binding){
            bottomNavi.setOnNavigationItemSelectedListener {
                when(it.itemId){
                    R.id.myDicBtn -> {
                        changeFragment(dicFragment)
                    }
                    R.id.searchBtn -> {
                        changeFragment(searchFragment)
                    }
                }
                true
            }
            bottomNavi.selectedItemId = R.id.myDicBtn
        }
    }

    private fun changeFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragContainer, fragment)
            .commit()
    }

}