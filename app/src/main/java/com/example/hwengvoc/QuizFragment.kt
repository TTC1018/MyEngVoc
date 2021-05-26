package com.example.hwengvoc

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AlertDialog
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
            dicChoiceLayout.setOnClickListener {
                var customDialog = layoutInflater.inflate(R.layout.dialog_set_dic, null)
                var builder = AlertDialog.Builder(requireContext())
                builder.setView(customDialog)
                val dialog = builder.create()

                //custom Dialog 컴포넌트들 선언
                var orderBtn = customDialog.findViewById<Button>(R.id.setDicCancelBtn)
                

                dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
            }

            quizOptionLayout.setOnClickListener {
                var customDialog = layoutInflater.inflate(R.layout.dialog_set_option, null)
                var builder = AlertDialog.Builder(requireContext())
                builder.setView(customDialog)
                val dialog = builder.create()

                //custom Dialog 컴포넌트들 선언
                var orderBtn = customDialog.findViewById<Button>(R.id.optOrderBtn)
                var randomBtn = customDialog.findViewById<Button>(R.id.optRanBtn)

                //각 컴포넌트에 Listener 설정
                orderBtn.setOnClickListener {
                    binding!!.optTextView.text = "순서대로"
                    dialog.dismiss()
                }
                randomBtn.setOnClickListener {
                    binding!!.optTextView.text = "무작위"
                    dialog.dismiss()
                }
                dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
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