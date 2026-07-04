package com.example.mcocarenabot

import android.content.SharedPreferences

data class CalibPoint(val x: Float, val y: Float, val color: Int) {
    fun isSet() = x > 0f && y > 0f
}

object Coordinates {
    val STEPS = listOf(
        "quickSelect" to "Toque em SELEÇÃO RÁPIDA",
        "findMatch" to "Toque em ENCONTRAR PARTIDA",
        "selectFight" to "Toque na luta de baixo (última opção da lista)",
        "continueAfterSelect" to "Toque em CONTINUAR (após escolher a luta)",
        "accept" to "Toque em ACEITAR",
        "continueBeforeFight" to "Toque em CONTINUAR (antes da luta começar)",
        "attackZone" to "Toque numa área central da tela de luta (ataque)",
        "continueAfterFight" to "Toque em CONTINUAR (depois de cada luta)",
        "nextSeries" to "Toque em PRÓXIMA SÉRIE"
    )

    fun save(prefs: SharedPreferences, key: String, point: CalibPoint) {
        prefs.edit()
            .putFloat("${key}_x", point.x)
            .putFloat("${key}_y", point.y)
            .putInt("${key}_color", point.color)
            .apply()
    }

    fun load(prefs: SharedPreferences, key: String): CalibPoint = CalibPoint(
        x = prefs.getFloat("${key}_x", 0f),
        y = prefs.getFloat("${key}_y", 0f),
        color = prefs.getInt("${key}_color", 0)
    )

    fun isFullyCalibrated(prefs: SharedPreferences): Boolean =
        STEPS.all { (key, _) -> load(prefs, key).isSet() }
}
