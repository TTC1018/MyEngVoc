package com.driedmango.geukvoc

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment

abstract class BaseFragment<T:ViewDataBinding>(@LayoutRes private val layoutId:Int):Fragment() {

    private var _binding:T? = null
    val binding
        get() = checkNotNull(_binding)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        _binding?.lifecycleOwner = viewLifecycleOwner
        return _binding?.also { it.lifecycleOwner = viewLifecycleOwner }?.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    protected fun bind(block:T.() -> Unit){
        block(binding)
    }
}