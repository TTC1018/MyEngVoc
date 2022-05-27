package com.driedmango.geukvoc.wordsearch

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.driedmango.geukvoc.*
import com.driedmango.geukvoc.data.VocData
import com.driedmango.geukvoc.databinding.FragmentSearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import java.util.*


class SearchFragment : Fragment() {
    var recyclerView:RecyclerView?=null
    var regiRecyclerView:RecyclerView?=null
    private var adapter: SearchRecyclerViewAdapter = SearchRecyclerViewAdapter(diffUtil)
    var regiAdapter: RegiVocRecyclerViewAdapter?=null
    var binding: FragmentSearchBinding?=null
    val scope = CoroutineScope(Dispatchers.IO)
    val url = "https://dictionary.cambridge.org/ko/%EC%82%AC%EC%A0%84/%EC%98%81%EC%96%B4-%ED%95%9C%EA%B5%AD%EC%96%B4/"
    var searchedList = LinkedList<VocData>()
    var targetDics = LinkedList<String>()
    var dbHelper: MyDBHelper?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        recyclerView = binding!!.recyclerSearch
        recyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapter.itemClickListener = defItemClickListener()
        adapter.submitList(searchedList.toMutableList())
        recyclerView!!.adapter = adapter
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = MyDBHelper(requireContext())
        binding!!.apply {
            wordSearchBtn.setOnClickListener {
                progressBar.visibility=View.VISIBLE
                getNews()
            }

            searchEditText.setOnKeyListener { _, keyCode, event ->
                if(event.action==KeyEvent.ACTION_DOWN && keyCode==KeyEvent.KEYCODE_ENTER){
                    wordSearchBtn.performClick()
                }
                true
            }

            val simpleCallback = object:ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN, ItemTouchHelper.RIGHT){
                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    searchedList.removeAt(position)
                    adapter.submitList(searchedList.toMutableList())
                }
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    val oldPos = viewHolder.adapterPosition
                    val newPos = target.adapterPosition
                    val item = searchedList[oldPos]
                    searchedList.removeAt(oldPos)
                    searchedList.add(newPos, item)
                    adapter.submitList(searchedList.toMutableList())
                    return true
                }
            }
            val itemTouchHelper = ItemTouchHelper(simpleCallback)
            itemTouchHelper.attachToRecyclerView(recyclerView!!)

            regiAdapter = RegiVocRecyclerViewAdapter(targetDics)
            if(targetDics.size!= MyDBHelper.TABLE_NAMES.size){
                for(TABLE_NAME in MyDBHelper.TABLE_NAMES){
                    if(!targetDics.contains(TABLE_NAME.replace("_", " ")))
                        targetDics.add(TABLE_NAME.replace("_", " "))
                }
                regiAdapter!!.notifyDataSetChanged()
            }
        }
    }

    private fun getNews(){
        val engMatch = "^[a-zA-Z]*\$".toRegex()
        var word = binding!!.searchEditText.text.toString()
        val word_unit = word.split(" ")
        var meaning:String

        for(unit in word_unit){
            if(!engMatch.matches(unit)){
                Toast.makeText(context, "영어를 입력하세요", Toast.LENGTH_SHORT).show()
                binding!!.searchEditText.text.clear()
                binding!!.progressBar.visibility = View.GONE
                return
            }
        }

        binding!!.searchEditText.text.clear()
        scope.launch {
            val doc = Jsoup.connect(url+word).get()
            try{
                meaning = doc.select("div.def-body.ddef_b span.trans.dtrans.dtrans-se ").first().text()
                println(word + "=" + meaning)
                searchedList.push(VocData(searchedList.size, word, meaning))
                requireActivity().runOnUiThread {
                    adapter.submitList(searchedList.toMutableList())
                    binding!!.progressBar.visibility = View.GONE
                    //키보드 숨기기 코드
                    val iMM = requireActivity().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
                    iMM.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken , InputMethodManager.HIDE_NOT_ALWAYS)
                    recyclerView!!.layoutManager!!.scrollToPosition(0)
                    recyclerView!!.post {
                        recyclerView!!.requestFocus()
                    }
                }
            }catch(e:Exception){
                requireActivity().runOnUiThread {
                    binding!!.progressBar.visibility = View.GONE
                    Toast.makeText(context, "검색된 단어가 없습니다", Toast.LENGTH_SHORT).show()
                    Log.e("getNews", e.toString())
                }
            }
        }
    }

    private fun defItemClickListener(): SearchRecyclerViewAdapter.OnItemClickListener {
        return object: SearchRecyclerViewAdapter.OnItemClickListener {
            override fun OnItemClick(
                holder: SearchRecyclerViewAdapter.ViewHolder,
                view: View,
                data: VocData,
                position: Int
            ) {
                val customDialog = layoutInflater.inflate(R.layout.dialog_add_to_dic, null)
                val builder = AlertDialog.Builder(requireContext())
                builder.setView(customDialog)
                val dialog = builder.create()

                //custom Dialog 컴포넌트들 선언
                val cancelBtn = customDialog.findViewById<Button>(R.id.vocRegiCancelBtn)
                regiRecyclerView = customDialog.findViewById<RecyclerView>(R.id.vocRegiRecyclerView)
                regiRecyclerView!!.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
                regiAdapter!!.itemClickListener=regiItemClickListener(data, dialog)
                regiRecyclerView!!.adapter = regiAdapter

                //각 컴포넌트에 Listener 선언
                cancelBtn.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                dialog.show()
            }
        }
    }

    private fun regiItemClickListener(data: VocData, dialog: AlertDialog): RegiVocRecyclerViewAdapter.OnItemClickListener {
        return object: RegiVocRecyclerViewAdapter.OnItemClickListener {
            override fun OnItemClick(
                holder: RegiVocRecyclerViewAdapter.ViewHolder,
                view: View,
                tableName: String,
                position: Int
            ) {
                val resultFlag = dbHelper!!.insertVoc(data, tableName.replace(" ", "_"))
                if(resultFlag){
                    Toast.makeText(requireContext(), "$tableName"+"에 단어가 추가되었습니다", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                else{
                    Toast.makeText(requireContext(), "단어 추가 실패. 중복 단어인지 확인하세요", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<VocData>() {
            override fun areItemsTheSame(oldItem: VocData, newItem: VocData): Boolean {
                return oldItem.vid == newItem.vid
            }

            override fun areContentsTheSame(oldItem: VocData, newItem: VocData): Boolean {
                return oldItem == newItem
            }
        }

        fun newInstance() = SearchFragment()
    }
}