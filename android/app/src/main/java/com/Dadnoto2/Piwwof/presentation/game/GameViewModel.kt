package com.Dadnoto2.Piwwof.presentation.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Dadnoto2.Piwwof.core.config.GameConfig
import com.Dadnoto2.Piwwof.data.sample.SampleData
import com.Dadnoto2.Piwwof.domain.model.Bloom
import com.Dadnoto2.Piwwof.domain.model.Board
import com.Dadnoto2.Piwwof.domain.model.MissionProgress
import com.Dadnoto2.Piwwof.domain.model.RoundStats
import com.Dadnoto2.Piwwof.domain.model.Stage
import com.Dadnoto2.Piwwof.domain.model.StageOutcome
import com.Dadnoto2.Piwwof.domain.model.StageResult
import com.Dadnoto2.Piwwof.domain.repository.MissionRepository
import com.Dadnoto2.Piwwof.domain.repository.ProgressRepository
import com.Dadnoto2.Piwwof.domain.repository.SettingsRepository
import com.Dadnoto2.Piwwof.domain.usecase.BloomBurstUseCase
import com.Dadnoto2.Piwwof.domain.usecase.EvaluateStageUseCase
import com.Dadnoto2.Piwwof.domain.usecase.GenerateStageUseCase
import com.Dadnoto2.Piwwof.domain.usecase.ResolveClusterUseCase
import com.Dadnoto2.Piwwof.domain.usecase.SpreadCrustUseCase
import com.Dadnoto2.Piwwof.domain.usecase.TileIds
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import kotlin.random.Random

class GameViewModel(
    private val progressRepository: ProgressRepository,
    settingsRepository: SettingsRepository,
    private val missionRepository: MissionRepository,
    private val generateStage: GenerateStageUseCase,
    private val resolveCluster: ResolveClusterUseCase,
    private val bloomBurst: BloomBurstUseCase,
    private val spreadCrust: SpreadCrustUseCase,
    private val evaluateStage: EvaluateStageUseCase
) : ViewModel() {

    private val uiState = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = uiState.asStateFlow()

    private val finishedRounds = Channel<StageResult>(Channel.BUFFERED)
    val roundFinished = finishedRounds.receiveAsFlow()

    private val ids = TileIds()
    private var board: Board = Board(GameConfig.COLS, GameConfig.ROWS, emptyList())
    private var stage: Stage = SampleData.stages.first()
    private var random: Random = Random(1)
    private var moveBudget = 0
    private var movesUsed = 0
    private var score = 0
    private var meter = 0
    private var wiltStrikes = 0
    private var rerollsUsed = 0
    private var bloomsBurst = 0
    private var crustCracked = 0
    private var sweeps = 0
    private var largestCluster = 0
    private val burstByBloom = HashMap<Bloom, Int>()
    private var busy = false
    private var roundOver = false
    private var sessionKey = ""
    private var hintJob: Job? = null
    private var hintEnabled = true

    init {
        viewModelScope.launch {
            settingsRepository.settings.collect { options ->
                hintEnabled = options.hintGlow
                uiState.value = uiState.value.copy(
                    colourBlindMarkers = options.colourBlindMarkers,
                    reducedMotion = options.reducedMotion
                )
                if (!options.hintGlow) {
                    hintJob?.cancel()
                    uiState.value = uiState.value.copy(hinted = emptySet())
                }
            }
        }
    }

    fun startStage(stageIndex: Int, attempt: Int) {
        val key = stageIndex.toString() + "-" + attempt.toString()
        if (key == sessionKey) return
        sessionKey = key
        hintJob?.cancel()
        busy = false
        roundOver = false
        movesUsed = 0
        score = 0
        meter = 0
        wiltStrikes = 0
        rerollsUsed = 0
        bloomsBurst = 0
        crustCracked = 0
        sweeps = 0
        largestCluster = 0
        burstByBloom.clear()
        stage = SampleData.stageAt(stageIndex)
        moveBudget = stage.moveBudget + progressRepository.progress.value.gardenTier
        uiState.value = uiState.value.copy(
            loading = true,
            stageIndex = stage.index,
            stageNameRes = stage.nameRes,
            tiles = emptyList(),
            goals = stage.goals.map { GoalUi(it.bloom, 0, it.target) },
            crustDone = 0,
            crustTarget = stage.crustGoal,
            movesLeft = moveBudget,
            moveBudget = moveBudget,
            score = 0,
            meter = 0,
            wilt = 0,
            rerollsLeft = GameConfig.REROLL_LIMIT,
            selected = emptySet(),
            bursting = emptySet(),
            hinted = emptySet(),
            wiltCell = -1,
            paused = false
        )
        viewModelScope.launch {
            yield()
            val generated = generateStage(stage, attempt, ids)
            board = generated.board
            random = Random(generated.seed + 977L)
            uiState.value = uiState.value.copy(
                loading = false,
                usedFallback = generated.usedFallback,
                seed = generated.seed
            )
            publish()
            armHint()
        }
    }

    fun pressCell(index: Int) {
        val current = uiState.value
        if (busy || roundOver || current.paused || current.loading) return
        if (index !in board.cells.indices) return
        hintJob?.cancel()
        val group = resolveCluster.preview(board, index)
        if (group.isEmpty()) {
            registerWilt(index)
            return
        }
        busy = true
        viewModelScope.launch {
            uiState.value = uiState.value.copy(selected = group, hinted = emptySet())
            delay(GameConfig.SELECT_MS)
            uiState.value = uiState.value.copy(selected = emptySet(), bursting = group)
            delay(GameConfig.BURST_MS)
            val outcome = resolveCluster(board, stage, index, random, ids)
            board = outcome.board
            movesUsed += 1
            score += outcome.score
            meter = minOf(GameConfig.METER_MAX, meter + outcome.meterGain)
            bloomsBurst += outcome.cleared
            crustCracked += outcome.crustCracked
            if (outcome.cleared > largestCluster) largestCluster = outcome.cleared
            outcome.bloom?.let { burstByBloom[it] = (burstByBloom[it] ?: 0) + outcome.cleared }
            maybeSpreadCrust()
            uiState.value = uiState.value.copy(bursting = emptySet())
            publish()
            delay(GameConfig.SETTLE_MS)
            busy = false
            evaluateRound()
        }
    }

    fun sweep() {
        val current = uiState.value
        if (busy || roundOver || current.paused || current.loading) return
        hintJob?.cancel()
        val charged = meter >= GameConfig.METER_MAX
        val outcome = if (charged) {
            bloomBurst(board, stage, goalPriority(), random, ids)
        } else {
            bloomBurst.shower(board, stage, random, ids)
        }
        if (outcome.cleared == 0) return
        busy = true
        viewModelScope.launch {
            uiState.value = uiState.value.copy(bursting = outcome.group, hinted = emptySet())
            delay(if (charged) GameConfig.WAVE_MS else GameConfig.BURST_MS)
            board = outcome.board
            score += outcome.score
            bloomsBurst += outcome.cleared
            crustCracked += outcome.crustCracked
            outcome.clearedByBloom.forEach { (bloom, count) ->
                burstByBloom[bloom] = (burstByBloom[bloom] ?: 0) + count
            }
            if (charged) {
                meter = 0
                sweeps += 1
            } else {
                movesUsed += 1
                maybeSpreadCrust()
            }
            uiState.value = uiState.value.copy(bursting = emptySet())
            publish()
            delay(GameConfig.SETTLE_MS)
            busy = false
            evaluateRound()
        }
    }

    fun reroll() {
        val current = uiState.value
        if (busy || roundOver || current.paused || current.loading) return
        if (rerollsUsed >= GameConfig.REROLL_LIMIT) return
        hintJob?.cancel()
        busy = true
        viewModelScope.launch {
            board = resolveCluster.reroll(board, stage, random, ids)
            rerollsUsed += 1
            movesUsed += 1
            maybeSpreadCrust()
            publish()
            delay(GameConfig.SETTLE_MS)
            busy = false
            evaluateRound()
        }
    }

    fun pause() {
        hintJob?.cancel()
    }

    fun requestPause() {
        hintJob?.cancel()
        if (roundOver) return
        uiState.value = uiState.value.copy(paused = true, hinted = emptySet())
    }

    fun resume() {
        if (roundOver) return
        uiState.value = uiState.value.copy(paused = false)
        armHint()
    }

    fun abandon() {
        hintJob?.cancel()
        roundOver = true
        sessionKey = ""
    }

    private fun goalPriority(): List<Bloom> = stage.goals
        .map { it.bloom to it.target - (burstByBloom[it.bloom] ?: 0) }
        .filter { it.second > 0 }
        .sortedByDescending { it.second }
        .map { it.first }

    private fun maybeSpreadCrust() {
        if (movesUsed > 0 && movesUsed % GameConfig.CRUST_SPREAD_EVERY == 0) {
            val spread = spreadCrust(board, random)
            board = spread.first
        }
    }

    private fun registerWilt(index: Int) {
        wiltStrikes += 1
        uiState.value = uiState.value.copy(
            wilt = wiltStrikes,
            wiltCell = index,
            hinted = emptySet(),
            shakeToken = uiState.value.shakeToken + 1
        )
        viewModelScope.launch {
            delay(GameConfig.SHAKE_MS)
            uiState.value = uiState.value.copy(wiltCell = -1)
            if (wiltStrikes >= GameConfig.WILT_LIMIT) {
                finishRound(StageOutcome.Crushed)
            } else {
                armHint()
            }
        }
    }

    private fun armHint() {
        hintJob?.cancel()
        if (!hintEnabled || roundOver) return
        hintJob = viewModelScope.launch {
            delay(GameConfig.HINT_IDLE_MS)
            uiState.value = uiState.value.copy(hinted = board.largestCluster())
        }
    }

    private fun publish() {
        uiState.value = uiState.value.copy(
            tiles = board.cells.map { TileUi(it.id, it.bloom, it.crustLayers) },
            goals = stage.goals.map { GoalUi(it.bloom, burstByBloom[it.bloom] ?: 0, it.target) },
            crustDone = crustCracked,
            movesLeft = (moveBudget - movesUsed).coerceAtLeast(0),
            score = score,
            meter = meter,
            wilt = wiltStrikes,
            rerollsLeft = GameConfig.REROLL_LIMIT - rerollsUsed
        )
    }

    private fun evaluateRound() {
        if (roundOver) return
        if (evaluateStage.goalsMet(stage, burstByBloom, crustCracked)) {
            finishRound(StageOutcome.Win)
            return
        }
        if (moveBudget - movesUsed <= 0) {
            finishRound(StageOutcome.OutOfMoves)
            return
        }
        armHint()
    }

    private fun finishRound(outcome: StageOutcome) {
        if (roundOver) return
        roundOver = true
        hintJob?.cancel()
        val movesLeft = (moveBudget - movesUsed).coerceAtLeast(0)
        val stats = RoundStats(
            stageIndex = stage.index,
            bloomsBurst = bloomsBurst,
            crustCracked = crustCracked,
            movesLeft = movesLeft,
            bloomBursts = sweeps,
            wiltStrikes = wiltStrikes,
            largestCluster = largestCluster,
            burstByBloom = HashMap(burstByBloom),
            won = outcome == StageOutcome.Win
        )
        val before = missionRepository.entries.value
        missionRepository.recordRound(stats)
        val after = missionRepository.entries.value
        val missionTitle = firstCompletedMission(before, after)
        val result = evaluateStage(
            stage = stage,
            outcome = outcome,
            score = score,
            bloomsBurst = bloomsBurst,
            crustCracked = crustCracked,
            movesLeft = movesLeft,
            missionTitleRes = missionTitle
        )
        progressRepository.applyResult(result)
        viewModelScope.launch { finishedRounds.send(result) }
    }

    private fun firstCompletedMission(
        before: List<MissionProgress>,
        after: List<MissionProgress>
    ): Int? {
        val active = SampleData.missionsFor(missionRepository.setIndex.value)
        for (mission in active) {
            val was = before.firstOrNull { it.missionId == mission.id }?.progress ?: 0
            val now = after.firstOrNull { it.missionId == mission.id }?.progress ?: 0
            if (was < mission.target && now >= mission.target) return mission.titleRes
        }
        return null
    }
}
