package com.nextroom.nextroom.presentation.ui.theme_select

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nextroom.nextroom.domain.repository.DataStoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecommendBackgroundCustomViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
) : ViewModel() {

    fun onDismissClicked() {
        val oneWeekLater = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000) // 일주일 밀리세컨드 표시
        viewModelScope.launch {
            dataStoreRepository.setRecommendBackgroundCustomDialogHidden(oneWeekLater)
        }
    }
}
