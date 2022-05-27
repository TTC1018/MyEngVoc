package com.driedmango.geukvoc.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.driedmango.geukvoc.MyDBHelper
import com.driedmango.geukvoc.data.DicData

class MyDicViewModel:ViewModel() {
    private val _dicList = mutableListOf<DicData>()
    val dicList = MutableLiveData<List<DicData>>()

    init {
        dicList.value = _dicList
    }

    fun loadDB(myDBHelper: MyDBHelper){
        for(i in 0 until MyDBHelper.TABLE_NAMES.size){
            _dicList.add(
                DicData(
                    MyDBHelper.TABLE_NAMES[i].replace("_", " "), myDBHelper.countVoc(
                        MyDBHelper.TABLE_NAMES[i]
                    ))
            )
        }
        dicList.value = _dicList
    }
}