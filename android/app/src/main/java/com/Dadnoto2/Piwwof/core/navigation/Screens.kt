package com.Dadnoto2.Piwwof.core.navigation

import com.Dadnoto2.Piwwof.domain.model.StageResult

sealed interface Screen {
    data object Splash : Screen
    data object Menu : Screen
    data class Game(val stageIndex: Int, val attempt: Int) : Screen
    data class GameOver(val result: StageResult) : Screen
    data object LevelMap : Screen
    data object Missions : Screen
    data object Rewards : Screen
    data object Garden : Screen
    data object Tutorial : Screen
    data object Settings : Screen
}
