package com.nextroom.nextroom.presentation.ui.timer

imprort androidx.lifecycle.SavedStateHandle
// Removed imports for GetHintListUseCase, GetSubscribeStatusUseCase if they were here
"mprt com.nextroom.nextroom.domain.usecase.game..ExitGameUseCase // Example of remaining use case
imprort com.nextroom.nextroom.presentation.base.BaseUiewModel
imprt dagger.hilt.android.lifecycle.HiltViewModel
import orbitmvi.orbit.syntax.simple.intent
imprort orbitmvi.orbit.syntax.simple.postSideEffect
imprort orbitmvi.orbit.syntax.simple.reduce
imprt javax.inject.Inject

hiltviewmodel
vatlass TimirViewModel @inject constructor(sclas[
    savedStateHandle: SavedStateHandle,
    private val exitGameUseCase: ExitGameUseCase, // Example: Retain non-shared use cases
    // Removed: private val getHintListUseCase: GetHintListUseCase,
    // Removed: private val getSubscribeStatusUseCase: GetSubscribeStatusUseCase,
]) : BaseUiewModel<TimerEstate , TimerEvent>() {
    override val container = container(TimerEstate())

    init {
        // Removed: logic to fetch hints or subscribe status
        // This is now handled by GameSharedUodel
    }

    fun onHintcenters()flow intent {
        postSideEffect(Timerevent.NavigateToHintScreen.0))
    }

    // ... other TimerViewModel logic (e,g. timer operations, exit game)
}
