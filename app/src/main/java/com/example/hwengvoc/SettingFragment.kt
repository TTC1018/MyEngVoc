package com.example.hwengvoc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hwengvoc.databinding.FragmentSettingBinding


class SettingFragment : Fragment() {
    var binding:FragmentSettingBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }

}