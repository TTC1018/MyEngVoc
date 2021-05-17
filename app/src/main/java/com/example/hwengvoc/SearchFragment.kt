package com.example.hwengvoc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.hwengvoc.databinding.FragmentSearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jsoup.Jsoup


class SearchFragment : Fragment() {
    var recyclerView:RecyclerView?=null
    var adapter:SearchRecyclerViewAdapter?=null
    var binding: FragmentSearchBinding?=null
    val scope = CoroutineScope(Dispatchers.IO)
    val url = "https://dictionary.cambridge.org/ko/%EC%82%AC%EC%A0%84/%EC%98%81%EC%96%B4-%ED%95%9C%EA%B5%AD%EC%96%B4/"
    var searchedList = mutableListOf<VocData>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding!!.apply {
            wordSearchBtn.setOnClickListener {
                getNews()
            }

            recyclerView = recyclerSearch
            recyclerView!!.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = SearchRecyclerViewAdapter(searchedList)
            recyclerView!!.adapter = adapter
        }
    }

    private fun getNews(){
        val engMatch = "^[a-zA-Z]*\$".toRegex()
        var word = binding!!.searchEditText.text.toString()
        var meaning:String

        if(!engMatch.matches(word)){
            Toast.makeText(context, "영어를 입력하세요", Toast.LENGTH_SHORT).show()
            binding!!.searchEditText.text.clear()
            return
        }

        scope.launch {
            val doc = Jsoup.connect(url+word).get()
            try{
                meaning = doc.select("div.def-body.ddef_b span.trans.dtrans.dtrans-se ").first().text()
                println(word + "=" + meaning)
                searchedList.add(VocData(word, meaning))
                activity!!.runOnUiThread {
                    adapter!!.notifyDataSetChanged()
                }
            }catch(e:NullPointerException){
                activity!!.runOnUiThread {
                    Toast.makeText(context, "찾는 단어가 없습니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding!!.searchEditText.text.clear()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding=null
    }
}