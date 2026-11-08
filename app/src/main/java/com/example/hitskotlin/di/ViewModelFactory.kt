package com.example.hitskotlin.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ViewModelFactory(private val createViewModel: () -> ViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val viewModel = createViewModel()
        require(modelClass.isInstance(viewModel)) { "Фабрика создала другую ViewModel" }
        return modelClass.cast(viewModel)!!
    }
}
