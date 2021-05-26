package com.example.hwengvoc

import android.app.UiModeManager
import android.content.Context
import android.content.res.Configuration
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.hwengvoc.databinding.FragmentSettingBinding


class SettingFragment : Fragment() {
    var binding:FragmentSettingBinding?=null
    var slideUp:Animation?=null
    var uiModeManager:UiModeManager?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(layoutInflater)
        slideUp = AnimationUtils.loadAnimation(context, R.anim.slide_up)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uiModeManager = requireActivity().getSystemService(Context.UI_MODE_SERVICE) as UiModeManager?
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
            nightBtn.setOnClickListener {
                if(uiModeManager!!.nightMode == UiModeManager.MODE_NIGHT_NO){
                    uiModeManager!!.nightMode = UiModeManager.MODE_NIGHT_YES
                }
                else{
                    uiModeManager!!.nightMode = UiModeManager.MODE_NIGHT_NO
                }
                val activity = requireActivity() as MainActivity
                for(i in 0 until activity.supportFragmentManager.backStackEntryCount){
                    activity.supportFragmentManager.popBackStack()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }

}