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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hwengvoc.databinding.FragmentQuizBinding
import java.util.*


class QuizFragment : Fragment() {
    var binding:FragmentQuizBinding?=null
    var dbHelper:MyDBHelper?=null
    var targetDicAdapter:RegiVocRecyclerViewAdapter?=null
    var targetRecyclerView:RecyclerView?=null
    var dicList = LinkedList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuizBinding.inflate(layoutInflater)
        return binding!!.root
    }

    override fun onResume() {
        super.onResume()
        dicList.clear()
        if(dicList.size!=MyDBHelper.TABLE_NAMES.size){
            for(TABLE_NAME in MyDBHelper.TABLE_NAMES){
                if(!dicList.contains(TABLE_NAME.replace("_", " ")))
                    dicList.add(TABLE_NAME.replace("_", " "))
            }
            targetDicAdapter!!.notifyDataSetChanged()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = MyDBHelper(requireContext())
        targetDicAdapter = RegiVocRecyclerViewAdapter(dicList)
        if(dicList.size!=MyDBHelper.TABLE_NAMES.size){
            for(TABLE_NAME in MyDBHelper.TABLE_NAMES){
                if(!dicList.contains(TABLE_NAME.replace("_", " ")))
                    dicList.add(TABLE_NAME.replace("_", " "))
            }
            targetDicAdapter!!.notifyDataSetChanged()
        }


        binding!!.apply {
            dicChoiceLayout.setOnClickListener {
                var customDialog = layoutInflater.inflate(R.layout.dialog_set_dic, null)
                var builder = AlertDialog.Builder(requireContext())
                builder.setView(customDialog)
                val dialog = builder.create()

                //custom Dialog 컴포넌트들 선언
                var cancelBtn = customDialog.findViewById<Button>(R.id.setDicCancelBtn)
                targetDicAdapter!!.itemClickListener=tarItemClickListener(dialog)
                targetRecyclerView = customDialog.findViewById(R.id.setDicRecyclerView)
                targetRecyclerView!!.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                targetRecyclerView!!.adapter = targetDicAdapter

                cancelBtn.setOnClickListener {
                    dialog.dismiss()
                }

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
                if(selDicTextView.length()==0){
                    Toast.makeText(requireContext(), "단어장을 선택하세요", Toast.LENGTH_SHORT).show()
                }
                else{
                    val intent = Intent(requireContext(), MeanQuizActivity::class.java)
                    intent.putExtra("dicName", selDicTextView.text.toString())
                    intent.putExtra("option", optTextView.text.toString())
                    startActivity(intent)
                }
            }
            wordQuizLayout.setOnClickListener {
                if(selDicTextView.length()==0){
                    Toast.makeText(requireContext(), "단어장을 선택하세요", Toast.LENGTH_SHORT).show()
                }
                else{
                    val intent = Intent(requireContext(), WordQuizActivity::class.java)
                    intent.putExtra("dicName", selDicTextView.text.toString())
                    intent.putExtra("option", optTextView.text.toString())
                    startActivity(intent)
                }
            }
            spellQuizLayout.setOnClickListener {
                if(selDicTextView.length()==0){
                    Toast.makeText(requireContext(), "단어장을 선택하세요", Toast.LENGTH_SHORT).show()
                }
                else{
                    val intent = Intent(requireContext(), SpellQuizActivity::class.java)
                    intent.putExtra("dicName", selDicTextView.text.toString())
                    intent.putExtra("option", optTextView.text.toString())
                    startActivity(intent)
                }
            }
        }
    }

    private fun tarItemClickListener(dialog: AlertDialog):RegiVocRecyclerViewAdapter.OnItemClickListener{
        return object:RegiVocRecyclerViewAdapter.OnItemClickListener{
            override fun OnItemClick(
                holder: RegiVocRecyclerViewAdapter.ViewHolder,
                view: View,
                tableName: String,
                position: Int
            ) {
                binding!!.selDicTextView.text = tableName
                dialog.dismiss()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }

}