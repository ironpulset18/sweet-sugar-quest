package com.Dadnoto2.Piwwof.core.ui

fun groupedNumber(value: Int): String {
    val digits = value.toString()
    if (digits.length <= 3) return digits
    val out = StringBuilder()
    var countdown = digits.length
    for (ch in digits) {
        out.append(ch)
        countdown -= 1
        if (countdown > 0 && countdown % 3 == 0) out.append(',')
    }
    return out.toString()
}

fun percentOf(value: Int, total: Int): Int =
    if (total <= 0) 0 else ((value.toFloat() / total.toFloat()) * 100f).toInt().coerceIn(0, 100)
