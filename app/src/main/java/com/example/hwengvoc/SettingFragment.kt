package com.example.hwengvoc

import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.hwengvoc.databinding.FragmentSettingBinding


class SettingFragment : Fragment() {
    var binding:FragmentSettingBinding?=null
    var slideDown:Animation?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(layoutInflater)
        slideDown = AnimationUtils.loadAnimation(context, R.anim.slide_down)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.apply {

            appInfoLayout.setOnClickListener {
                        if(appInfoTextLayout.visibility==View.GONE){
                            appInfoTextLayout.visibility=View.VISIBLE
                            appInfoTextLayout.startAnimation(slideDown)
                        }
                        else{
                            appInfoTextLayout.visibility=View.GONE
                        }

            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }

}