package com.Dadnoto2.Piwwof.presentation.tutorial

import androidx.lifecycle.ViewModel
import com.Dadnoto2.Piwwof.R
import com.Dadnoto2.Piwwof.domain.model.Bloom
import com.Dadnoto2.Piwwof.domain.repository.ProgressRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MiniCell(
    val bloom: Bloom,
    val crustLayers: Int = 0,
    val highlighted: Boolean = false
)

data class TutorialStep(
    val titleRes: Int,
    val bodyRes: Int,
    val cells: List<MiniCell>
)

data class TutorialUiState(
    val step: Int = 0,
    val steps: List<TutorialStep> = emptyList()
) {
    val total: Int get() = steps.size
    val current: TutorialStep? get() = steps.getOrNull(step)
    val isLast: Boolean get() = step >= steps.size - 1
}

class TutorialViewModel(
    private val progressRepository: ProgressRepository
) : ViewModel() {

    private val uiState = MutableStateFlow(TutorialUiState(step = 0, steps = buildSteps()))
    val state: StateFlow<TutorialUiState> = uiState.asStateFlow()

    fun next() {
        val current = uiState.value
        if (current.step < current.steps.size - 1) {
            uiState.value = current.copy(step = current.step + 1)
        }
    }

    fun back() {
        val current = uiState.value
        if (current.step > 0) uiState.value = current.copy(step = current.step - 1)
    }

    fun markSeen() {
        progressRepository.markTutorialSeen()
    }

    private fun buildSteps(): List<TutorialStep> {
        val s = Bloom.Strawberry
        val l = Bloom.Lemon
        val m = Bloom.Mint
        val b = Bloom.Blueberry
        val g = Bloom.Grape
        return listOf(
            TutorialStep(
                titleRes = R.string.tutorial_1_title,
                bodyRes = R.string.tutorial_1_body,
                cells = listOf(
                    MiniCell(b), MiniCell(m, highlighted = true), MiniCell(l),
                    MiniCell(s), MiniCell(m, highlighted = true), MiniCell(g),
                    MiniCell(l), MiniCell(m, highlighted = true), MiniCell(b)
                )
            ),
            TutorialStep(
                titleRes = R.string.tutorial_2_title,
                bodyRes = R.string.tutorial_2_body,
                cells = listOf(
                    MiniCell(s, highlighted = true), MiniCell(s, highlighted = true), MiniCell(l),
                    MiniCell(s, highlighted = true), MiniCell(s, highlighted = true), MiniCell(g),
                    MiniCell(m), MiniCell(b), MiniCell(l)
                )
            ),
            TutorialStep(
                titleRes = R.string.tutorial_3_title,
                bodyRes = R.string.tutorial_3_body,
                cells = listOf(
                    MiniCell(l), MiniCell(b, crustLayers = 2), MiniCell(m),
                    MiniCell(l, highlighted = true), MiniCell(l, highlighted = true), MiniCell(g),
                    MiniCell(s), MiniCell(m), MiniCell(b)
                )
            ),
            TutorialStep(
                titleRes = R.string.tutorial_4_title,
                bodyRes = R.string.tutorial_4_body,
                cells = listOf(
                    MiniCell(g, highlighted = true), MiniCell(l), MiniCell(g, highlighted = true),
                    MiniCell(m), MiniCell(g, highlighted = true), MiniCell(b),
                    MiniCell(g, highlighted = true), MiniCell(s), MiniCell(g, highlighted = true)
                )
            )
        )
    }
}
