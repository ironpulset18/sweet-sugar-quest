package com.Dadnoto2.Piwwof.core.di

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.Dadnoto2.Piwwof.presentation.app.AppViewModel
import com.Dadnoto2.Piwwof.presentation.collection.CollectionViewModel
import com.Dadnoto2.Piwwof.presentation.game.GameViewModel
import com.Dadnoto2.Piwwof.presentation.gameover.GameOverViewModel
import com.Dadnoto2.Piwwof.presentation.levelmap.LevelMapViewModel
import com.Dadnoto2.Piwwof.presentation.menu.MenuViewModel
import com.Dadnoto2.Piwwof.presentation.missions.MissionsViewModel
import com.Dadnoto2.Piwwof.presentation.rewards.RewardsViewModel
import com.Dadnoto2.Piwwof.presentation.settings.SettingsViewModel
import com.Dadnoto2.Piwwof.presentation.splash.SplashViewModel
import com.Dadnoto2.Piwwof.presentation.tutorial.TutorialViewModel

object AppViewModelFactory {

    val factory: ViewModelProvider.Factory = viewModelFactory {
        initializer {
            val c = ServiceLocator.require()
            AppViewModel(c.settingsRepository, c.progressRepository)
        }
        initializer { SplashViewModel() }
        initializer {
            val c = ServiceLocator.require()
            MenuViewModel(c.progressRepository, c.missionRepository)
        }
        initializer {
            val c = ServiceLocator.require()
            GameViewModel(
                progressRepository = c.progressRepository,
                settingsRepository = c.settingsRepository,
                missionRepository = c.missionRepository,
                generateStage = c.generateStage,
                resolveCluster = c.resolveCluster,
                bloomBurst = c.bloomBurst,
                spreadCrust = c.spreadCrust,
                evaluateStage = c.evaluateStage
            )
        }
        initializer {
            val c = ServiceLocator.require()
            GameOverViewModel(c.progressRepository)
        }
        initializer {
            val c = ServiceLocator.require()
            LevelMapViewModel(c.progressRepository)
        }
        initializer {
            val c = ServiceLocator.require()
            MissionsViewModel(c.progressRepository, c.missionRepository)
        }
        initializer {
            val c = ServiceLocator.require()
            RewardsViewModel(c.progressRepository, c.claimReward)
        }
        initializer {
            val c = ServiceLocator.require()
            CollectionViewModel(c.progressRepository)
        }
        initializer {
            val c = ServiceLocator.require()
            TutorialViewModel(c.progressRepository)
        }
        initializer {
            val c = ServiceLocator.require()
            SettingsViewModel(c.settingsRepository, c.progressRepository, c.missionRepository)
        }
    }
}
