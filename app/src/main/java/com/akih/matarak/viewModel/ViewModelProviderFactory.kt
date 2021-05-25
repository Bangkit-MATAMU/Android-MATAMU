package com.akih.matarak.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

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