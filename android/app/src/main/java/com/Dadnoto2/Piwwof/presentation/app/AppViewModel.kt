package com.Dadnoto2.Piwwof.presentation.app

import androidx.lifecycle.ViewModel
import com.Dadnoto2.Piwwof.core.navigation.Screen
import com.Dadnoto2.Piwwof.domain.model.GameSettings
import com.Dadnoto2.Piwwof.domain.model.StageResult
import com.Dadnoto2.Piwwof.domain.repository.ProgressRepository
import com.Dadnoto2.Piwwof.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppViewModel(
    settingsRepository: SettingsRepository,
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val backStack = ArrayDeque<Screen>()
    private var attempt = 0

    private val screenState = MutableStateFlow<Screen>(Screen.Splash)
    val screen: StateFlow<Screen> = screenState.asStateFlow()

    val settings: StateFlow<GameSettings> = settingsRepository.settings

    fun openFromMenu(target: Screen) {
        backStack.addLast(screenState.value)
        screenState.value = target
    }

    fun leaveSplash() {
        backStack.clear()
        screenState.value = Screen.Menu
    }

    fun playStage(stageIndex: Int) {
        attempt += 1
        backStack.addLast(screenState.value)
        screenState.value = Screen.Game(stageIndex, attempt)
    }

    fun playCurrentStage() {
        playStage(progressRepository.progress.value.unlockedStage)
    }

    fun replayStage(stageIndex: Int) {
        attempt += 1
        screenState.value = Screen.Game(stageIndex, attempt)
    }

    fun finishRound(result: StageResult) {
        screenState.value = Screen.GameOver(result)
    }

    fun goToMenu() {
        backStack.clear()
        screenState.value = Screen.Menu
    }

    fun back() {
        val previous = backStack.removeLastOrNull()
        screenState.value = previous ?: Screen.Menu
    }

    fun canGoBack(): Boolean =
        screenState.value !is Screen.Splash && screenState.value !is Screen.Menu
}
