package com.example.hwengvoc

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        // Inflate the layout for this fragment
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
        var word = binding!!.searchEditText.text.toString()
        var meaning:String
        scope.launch {
            val doc = Jsoup.connect(url+word).get()
            meaning = doc.select("div.def-body.ddef_b span").text()
            println(word + "=" + meaning)
            searchedList.add(VocData(word, meaning))
            activity!!.runOnUiThread {
                adapter!!.notifyDataSetChanged()
            }
        }
        binding!!.searchEditText.text.clear()
    }
}