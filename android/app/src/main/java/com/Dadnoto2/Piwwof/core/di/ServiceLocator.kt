package com.Dadnoto2.Piwwof.core.di

import android.content.Context
import com.Dadnoto2.Piwwof.data.local.SugarPreferences
import com.Dadnoto2.Piwwof.data.repository.MissionRepositoryImpl
import com.Dadnoto2.Piwwof.data.repository.ProgressRepositoryImpl
import com.Dadnoto2.Piwwof.data.repository.SettingsRepositoryImpl
import com.Dadnoto2.Piwwof.data.sample.SampleData
import com.Dadnoto2.Piwwof.domain.repository.MissionRepository
import com.Dadnoto2.Piwwof.domain.repository.ProgressRepository
import com.Dadnoto2.Piwwof.domain.repository.SettingsRepository
import com.Dadnoto2.Piwwof.domain.usecase.BloomBurstUseCase
import com.Dadnoto2.Piwwof.domain.usecase.ClaimRewardUseCase
import com.Dadnoto2.Piwwof.domain.usecase.EvaluateStageUseCase
import com.Dadnoto2.Piwwof.domain.usecase.GenerateStageUseCase
import com.Dadnoto2.Piwwof.domain.usecase.ResolveClusterUseCase
import com.Dadnoto2.Piwwof.domain.usecase.SpreadCrustUseCase
import com.Dadnoto2.Piwwof.domain.usecase.UpdateMissionsUseCase

class AppContainer(context: Context) {
    private val preferences = SugarPreferences(context)

    val updateMissions = UpdateMissionsUseCase()
    val generateStage = GenerateStageUseCase { stage, ids -> SampleData.fallbackBoard(stage, ids) }
    val resolveCluster = ResolveClusterUseCase()
    val bloomBurst = BloomBurstUseCase()
    val spreadCrust = SpreadCrustUseCase()
    val evaluateStage = EvaluateStageUseCase()
    val claimReward = ClaimRewardUseCase()

    val progressRepository: ProgressRepository = ProgressRepositoryImpl(preferences)
    val settingsRepository: SettingsRepository = SettingsRepositoryImpl(preferences)
    val missionRepository: MissionRepository = MissionRepositoryImpl(preferences, updateMissions)
}

object ServiceLocator {

    private var container: AppContainer? = null

    fun init(context: Context) {
        if (container == null) container = AppContainer(context.applicationContext)
    }

    fun require(): AppContainer = checkNotNull(container) { "ServiceLocator was not initialised" }
}
