package com.example.hwengvoc

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.hwengvoc.databinding.FragmentQuizBinding


class QuizFragment : Fragment() {
    var binding:FragmentQuizBinding?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuizBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.apply {
            quizOptionLayout.setOnClickListener {

            }

            meanQuizLayout.setOnClickListener {
                val intent = Intent(requireContext(), MeanQuizActivity::class.java)
                startActivity(intent)
            }
            wordQuizLayout.setOnClickListener {
                val intent = Intent(requireContext(), WordQuizActivity::class.java)
                startActivity(intent)
            }
            spellQuizLayout.setOnClickListener {
                val intent = Intent(requireContext(), SpellQuizActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }

}