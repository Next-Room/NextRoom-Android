package com.nextroom.nextroom.presentation.ui.memo

import android.graphics.Path
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PainterViewModel @Inject constructor() : ViewModel() {
    private var _paths: MutableList<Path> = mutableListOf() // TODO JSON 으로 저장하도록 리팩터링 필요 (안드로이드 의존성<Path> 제거)
    val paths: List<Path> get() = _paths

    fun savePaths(paths: List<Path>) {
        _paths = paths.toMutableList()
    }

    fun clear() {
        _paths.clear()
    }
}
