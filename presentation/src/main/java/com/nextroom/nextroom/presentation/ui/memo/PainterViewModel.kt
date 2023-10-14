package com.nextroom.nextroom.presentation.ui.memo

import android.app.Application
import android.graphics.Path
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PainterViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {
    private var _paths: MutableList<Path> = mutableListOf()
    val paths: List<Path> get() = _paths

    fun savePaths(paths: List<Path>) {
        _paths = paths.toMutableList()
    }
}
