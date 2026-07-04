package com.example.mcocarenabot

import android.content.SharedPreferences

data class Point(val x: Float, val y: Float) {
    fun isSet() = x > 0f && y > 0f
}

/**
 * Coordenadas calibradas pelo usuário para cada etapa do ciclo de farm.
 * Persistidas em SharedPreferences, já que variam por aparelho/resolução.
 */
data class Coordinates(
    val championSlot: Point,
    val fightButton: Point,
    val attackZone: Point,
    val continueButton1: Point,
    val continueButton2: Point
) {
    fun isCalibrated(): Boolean =
        championSlot.isSet() && fightButton.isSet() && attackZone.isSet() &&
            continueButton1.isSet() && continueButton2.isSet()

    fun save(prefs: SharedPreferences) {
        prefs.edit().apply {
            putFloat("championSlot_x", championSlot.x); putFloat("championSlot_y", championSlot.y)
            putFloat("fightButton_x", fightButton.x); putFloat("fightButton_y", fightButton.y)
            putFloat("attackZone_x", attackZone.x); putFloat("attackZone_y", attackZone.y)
            putFloat("continueButton1_x", continueButton1.x); putFloat("continueButton1_y", continueButton1.y)
            putFloat("continueButton2_x", continueButton2.x); putFloat("continueButton2_y", continueButton2.y)
        }.apply()
    }

    companion object {
        fun load(prefs: SharedPreferences): Coordinates = Coordinates(
            championSlot = Point(prefs.getFloat("championSlot_x", 0f), prefs.getFloat("championSlot_y", 0f)),
            fightButton = Point(prefs.getFloat("fightButton_x", 0f), prefs.getFloat("fightButton_y", 0f)),
            attackZone = Point(prefs.getFloat("attackZone_x", 0f), prefs.getFloat("attackZone_y", 0f)),
            continueButton1 = Point(prefs.getFloat("continueButton1_x", 0f), prefs.getFloat("continueButton1_y", 0f)),
            continueButton2 = Point(prefs.getFloat("continueButton2_x", 0f), prefs.getFloat("continueButton2_y", 0f))
        )

        // key interno -> texto exibido durante a calibração, nesta ordem.
        val STEPS = listOf(
            "championSlot" to "Toque no slot do campeão",
            "fightButton" to "Toque no botão de Lutar",
            "attackZone" to "Toque numa área central da tela de luta (zona de ataque)",
            "continueButton1" to "Toque no botão Continuar (1ª tela pós-luta)",
            "continueButton2" to "Toque no botão Continuar (2ª tela pós-luta)"
        )
    }
}
