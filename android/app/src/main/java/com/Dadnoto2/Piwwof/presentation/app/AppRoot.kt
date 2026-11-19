package com.Dadnoto2.Piwwof.presentation.app

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Dadnoto2.Piwwof.core.di.AppViewModelFactory
import com.Dadnoto2.Piwwof.core.navigation.Screen
import com.Dadnoto2.Piwwof.core.theme.BlushBase
import com.Dadnoto2.Piwwof.presentation.collection.CollectionRoute
import com.Dadnoto2.Piwwof.presentation.game.GameRoute
import com.Dadnoto2.Piwwof.presentation.gameover.GameOverRoute
import com.Dadnoto2.Piwwof.presentation.levelmap.LevelMapRoute
import com.Dadnoto2.Piwwof.presentation.menu.MenuRoute
import com.Dadnoto2.Piwwof.presentation.missions.MissionsRoute
import com.Dadnoto2.Piwwof.presentation.rewards.RewardsRoute
import com.Dadnoto2.Piwwof.presentation.settings.SettingsRoute
import com.Dadnoto2.Piwwof.presentation.splash.SplashRoute
import com.Dadnoto2.Piwwof.presentation.tutorial.TutorialRoute

@Composable
fun AppRoot() {
    val appViewModel: AppViewModel = viewModel(factory = AppViewModelFactory.factory)
    val screen by appViewModel.screen.collectAsStateWithLifecycle()
    val options by appViewModel.settings.collectAsStateWithLifecycle()
    val reducedMotion = options.reducedMotion
    val enterMs = if (reducedMotion) 120 else 260
    val exitMs = if (reducedMotion) 120 else 200

    BackHandler(enabled = screen !is Screen.Splash && screen !is Screen.Menu) {
        appViewModel.back()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BlushBase)
    ) {
        AnimatedContent(
            targetState = screen,
            transitionSpec = { fadeIn(tween(enterMs)) togetherWith fadeOut(tween(exitMs)) },
            label = "screenHost"
        ) { current ->
            when (current) {
                is Screen.Splash -> SplashRoute(
                    onFinished = appViewModel::leaveSplash
                )

                is Screen.Menu -> MenuRoute(
                    reducedMotion = reducedMotion,
                    onPlay = appViewModel::playStage,
                    onLevels = { appViewModel.openFromMenu(Screen.LevelMap) },
                    onMissions = { appViewModel.openFromMenu(Screen.Missions) },
                    onCollection = { appViewModel.openFromMenu(Screen.Garden) },
                    onRewards = { appViewModel.openFromMenu(Screen.Rewards) },
                    onTutorial = { appViewModel.openFromMenu(Screen.Tutorial) },
                    onSettings = { appViewModel.openFromMenu(Screen.Settings) }
                )

                is Screen.Game -> GameRoute(
                    stageIndex = current.stageIndex,
                    attempt = current.attempt,
                    onFinished = appViewModel::finishRound,
                    onMenu = appViewModel::goToMenu,
                    onRestart = { appViewModel.replayStage(current.stageIndex) }
                )

                is Screen.GameOver -> GameOverRoute(
                    result = current.result,
                    reducedMotion = reducedMotion,
                    onPlayAgain = { appViewModel.replayStage(current.result.stageIndex) },
                    onLevels = { appViewModel.openFromMenu(Screen.LevelMap) },
                    onMenu = appViewModel::goToMenu
                )

                is Screen.LevelMap -> LevelMapRoute(
                    reducedMotion = reducedMotion,
                    onPlayStage = appViewModel::playStage,
                    onMenu = appViewModel::goToMenu
                )

                is Screen.Missions -> MissionsRoute(
                    reducedMotion = reducedMotion,
                    onMenu = appViewModel::goToMenu
                )

                is Screen.Rewards -> RewardsRoute(
                    reducedMotion = reducedMotion,
                    onMenu = appViewModel::goToMenu
                )

                is Screen.Garden -> CollectionRoute(
                    reducedMotion = reducedMotion,
                    onMenu = appViewModel::goToMenu
                )

                is Screen.Tutorial -> TutorialRoute(
                    reducedMotion = reducedMotion,
                    onPlayNow = appViewModel::playCurrentStage,
                    onMenu = appViewModel::goToMenu
                )

                is Screen.Settings -> SettingsRoute(
                    reducedMotion = reducedMotion,
                    onMenu = appViewModel::goToMenu
                )
            }
        }
    }
}
