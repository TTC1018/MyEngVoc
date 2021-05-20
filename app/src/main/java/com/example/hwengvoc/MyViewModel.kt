package com.example.hwengvoc

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel: ViewModel() {
    val selectdnum = MutableLiveData<Int>()
    fun setLiveData(num:Int){
        selectdnum.value = num
    }
}