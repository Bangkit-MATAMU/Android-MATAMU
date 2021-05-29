package com.akih.matarak.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.akih.matarak.hospital.MapsViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelProviderFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        when {
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                return MapsViewModel() as T
            }
            else -> throw Throwable("Unknown ViewModel Type")
        }
    }
}