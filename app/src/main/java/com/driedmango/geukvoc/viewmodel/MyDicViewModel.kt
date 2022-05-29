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
        dicList.postValue(_dicList)
    }

    fun addDic(dicData: DicData){
        _dicList.add(dicData)
        dicList.postValue(_dicList)
    }

    fun setwordCount(count:Int, position:Int){
        _dicList[position].wordCount = count
        dicList.postValue(_dicList)
    }

    fun updateInfo(myDBHelper: MyDBHelper){
        if(_dicList.isNotEmpty()){
            for(i in _dicList.indices){
                for(TABLE_NAME in MyDBHelper.TABLE_NAMES){
                    if(_dicList[i].dicName == TABLE_NAME.replace("_", " ")){
                        _dicList[i].wordCount = myDBHelper.countVoc(TABLE_NAME)
                    }
                }
            }
        }
        dicList.postValue(_dicList)
    }
}