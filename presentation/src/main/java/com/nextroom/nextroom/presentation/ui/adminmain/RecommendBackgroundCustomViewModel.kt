package com.nextroom.nextroom.presentation.ui.adminmain

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecommendBackgroundCustomViewModel @Inject constructor() : ViewModel() {

    fun onDismissClicked() {
        //TODO : 일주일간 보지 않기
    }
}
