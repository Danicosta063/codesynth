package com.example.mcocarenabot

import android.content.SharedPreferences

data class Point(val x: Float, val y: Float) {
    fun isSet() = x > 0f && y > 0f
}

data class Coordinates(
    val quickSelect: Point,
    val findMatch: Point,
    val selectFight: Point,
    val continueAfterSelect: Point,
    val accept: Point,
    val continueBeforeFight: Point,
    val attackZone: Point,
    val continueAfterFight: Point,
    val nextSeries: Point,
    val autoplayButton: Point
) {
    fun isCalibrated(): Boolean =
        quickSelect.isSet() && findMatch.isSet() && selectFight.isSet() &&
            continueAfterSelect.isSet() && accept.isSet() && continueBeforeFight.isSet() &&
            attackZone.isSet() && continueAfterFight.isSet() && nextSeries.isSet()

    fun save(prefs: SharedPreferences) {
        prefs.edit().apply {
            listOf(
                "quickSelect" to quickSelect, "findMatch" to findMatch, "selectFight" to selectFight,
                "continueAfterSelect" to continueAfterSelect, "accept" to accept,
                "continueBeforeFight" to continueBeforeFight, "attackZone" to attackZone,
                "continueAfterFight" to continueAfterFight, "nextSeries" to nextSeries,
                "autoplayButton" to autoplayButton
            ).forEach { (key, p) ->
                putFloat("${key}_x", p.x); putFloat("${key}_y", p.y)
            }
        }.apply()
    }

    companion object {
        fun load(prefs: SharedPreferences): Coordinates {
            fun p(key: String) = Point(prefs.getFloat("${key}_x", 0f), prefs.getFloat("${key}_y", 0f))
            return Coordinates(
                quickSelect = p("quickSelect"),
                findMatch = p("findMatch"),
                selectFight = p("selectFight"),
                continueAfterSelect = p("continueAfterSelect"),
                accept = p("accept"),
                continueBeforeFight = p("continueBeforeFight"),
                attackZone = p("attackZone"),
                continueAfterFight = p("continueAfterFight"),
                nextSeries = p("nextSeries"),
                autoplayButton = p("autoplayButton")
            )
        }

        val STEPS = listOf(
            "quickSelect" to "Toque em SELEÇÃO RÁPIDA",
            "findMatch" to "Toque em ENCONTRAR PARTIDA",
            "selectFight" to "Toque na luta de baixo (última opção da lista)",
            "continueAfterSelect" to "Toque em CONTINUAR (após escolher a luta)",
            "accept" to "Toque em ACEITAR",
            "continueBeforeFight" to "Toque em CONTINUAR (antes da luta começar)",
            "attackZone" to "Toque numa área central da tela de luta (ataque)",
            "continueAfterFight" to "Toque em CONTINUAR (depois de cada luta)",
            "nextSeries" to "Toque em PRÓXIMA SÉRIE",
            "autoplayButton" to "Se tiver botão de autoplay/luta automática, toque nele agora (senão, toque em qualquer canto vazio)"
        )
    }
}
