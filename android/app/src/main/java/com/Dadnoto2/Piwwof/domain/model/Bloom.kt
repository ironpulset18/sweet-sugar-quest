package com.Dadnoto2.Piwwof.domain.model

enum class BloomBadge { Circle, Triangle, Square, Diamond, Star }

enum class Bloom(val argb: Long, val badge: BloomBadge, val spriteSlot: Int) {
    Strawberry(0xFFFF4D6DL, BloomBadge.Circle, 1),
    Lemon(0xFFFFC93CL, BloomBadge.Triangle, 2),
    Mint(0xFF3FD6A6L, BloomBadge.Square, 3),
    Blueberry(0xFF4D8CFFL, BloomBadge.Diamond, 4),
    Grape(0xFFA35BFFL, BloomBadge.Star, 5)
}
