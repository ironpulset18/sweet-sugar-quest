package com.Dadnoto2.Piwwof.core.config

object GameConfig {
    const val LOADER_DURATION_MS = 8000L
    const val COLS = 7
    const val ROWS = 8
    const val BOARD_MAX_WIDTH_DP = 380
    const val BOARD_SIDE_MARGIN_DP = 32
    const val BOARD_PADDING_DP = 6
    const val BOARD_BORDER_DP = 2
    const val BOARD_FRAME_DP = BOARD_PADDING_DP + BOARD_BORDER_DP
    const val METER_MAX = 20
    const val METER_MIN_CLUSTER = 4
    const val WILT_LIMIT = 3
    const val CRUST_SPREAD_EVERY = 5
    const val REROLL_LIMIT = 2
    const val STAGE_COUNT = 12
    const val BLOOM_SLOTS = 15
    const val MISSION_SLOTS = 3
    const val GARDEN_TIER_STEP = 5
    const val SELECT_MS = 90L
    const val BURST_MS = 220L
    const val SETTLE_MS = 70L
    const val SHAKE_MS = 180L
    const val WAVE_MS = 420L
    const val HINT_IDLE_MS = 5000L
    const val GENERATION_ATTEMPTS = 20
    const val MIN_GROUPS_IN_BOARD = 4
    const val MIN_CELLS_PER_BLOOM = 8
    const val MAX_CRUST_CELLS = 9
    const val CLUSTER_SCORE_FACTOR = 10
    const val BURST_SCORE_FACTOR = 15
    const val SEED_STAGE_PRIME = 7919L
    const val SEED_ATTEMPT_PRIME = 104729L
}
