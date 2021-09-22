package com.driedmango.geukvoc.settings

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import com.driedmango.geukvoc.MainActivity
import com.driedmango.geukvoc.R
import com.driedmango.geukvoc.databinding.FragmentSettingBinding


class SettingFragment : Fragment() {
    var binding:FragmentSettingBinding?=null
    var slideUp:Animation?=null
    var uiModeManager:UiModeManager?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(layoutInflater)
        slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.apply {
            appInfoLayout.setOnClickListener {
                if(appInfoTextLayout.visibility==View.GONE){
                    appInfoTextLayout.visibility=View.VISIBLE
                    appInfoTextLayout.startAnimation(slideUp)
                }
                else{
                    appInfoTextLayout.visibility=View.GONE
                }
            }

            val fadeInOut = AnimationUtils.loadAnimation(context, R.anim.fade_inandout)
            nightBtn.startAnimation(fadeInOut)
            if(Build.VERSION.SDK_INT >= 28){
                when(Build.VERSION.SDK_INT){
                    28 -> {
                        nightBtn.setOnClickListener {
                            val curNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                            when(curNightMode){
                                Configuration.UI_MODE_NIGHT_NO->{
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                                }
                                Configuration.UI_MODE_NIGHT_YES->{
                                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                                }
                            }
                            val activity = requireActivity() as MainActivity
                            for(i in 0 until activity.supportFragmentManager.backStackEntryCount){
                                activity.supportFragmentManager.popBackStack()
                            }
                        }
                    }
                    else -> {
                        nightBtn.setOnClickListener {
                            Toast.makeText(context, "기기의 다크 모드 전환 기능을 이용해주세요", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }
                }

            }
            else{
                nightBtn.setOnClickListener {
                    Toast.makeText(context, "다크 모드를 미지원하는 안드로이드 입니다", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }

}