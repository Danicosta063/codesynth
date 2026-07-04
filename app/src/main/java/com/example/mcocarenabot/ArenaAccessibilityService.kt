package com.example.mcocarenabot

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import kotlin.random.Random

/**
 * Serviço de Acessibilidade responsável por simular os toques do farm de Arena.
 *
 * Importante: o MCOC (como a maioria dos jogos) renderiza tudo numa superfície
 * gráfica única (OpenGL/Canvas), então NÃO expõe uma árvore de UI acessível.
 * Por isso este serviço não lê o conteúdo da tela — ele só despacha gestos
 * (toques) em coordenadas pré-calibradas, com timing fixo.
 */
class ArenaAccessibilityService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private var fightsCompleted = 0
    private lateinit var prefs: android.content.SharedPreferences

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        Log.i(TAG, "Accessibility service conectado")
        startService(Intent(this, OverlayService::class.java))
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Sem uso — mantido apenas porque a classe base exige a implementação.
    }

    override fun onInterrupt() {}

    override fun onUnbind(intent: Intent?): Boolean {
        stopBot()
        instance = null
        return super.onUnbind(intent)
    }

    // ---------- Controle público (chamado pelo OverlayService) ----------

    fun startBot() {
        if (isRunning) return
        isRunning = true
        fightsCompleted = 0
        startService(Intent(this, BotForegroundService::class.java))
        handler.post(runLoop)
    }

    fun stopBot() {
        isRunning = false
        handler.removeCallbacksAndMessages(null)
        stopService(Intent(this, BotForegroundService::class.java))
    }

    fun isBotRunning() = isRunning
    fun getFightsCompleted() = fightsCompleted

    // ---------- Máquina de estados do farm de Arena ----------

    private val runLoop = Runnable { if (isRunning) farmCycle() }

    private fun farmCycle() {
        val coords = Coordinates.load(prefs)
        if (!coords.isCalibrated()) {
            Log.w(TAG, "Coordenadas não calibradas — calibre pelo widget flutuante antes de iniciar.")
            stopBot()
            return
        }

        tap(coords.championSlot.x, coords.championSlot.y)          // 1. seleciona campeão
        delay(600) {
            tap(coords.fightButton.x, coords.fightButton.y)         // 2. inicia a luta
            delay(FIGHT_LOAD_DELAY_MS) {
                spamAttacks(coords, FIGHT_DURATION_TAPS) {           // 3. combate
                    delay(POST_FIGHT_DELAY_MS) {
                        tap(coords.continueButton1.x, coords.continueButton1.y)  // 4. telas pós-luta
                        delay(800) {
                            tap(coords.continueButton2.x, coords.continueButton2.y)
                            fightsCompleted++
                            delay(700) { handler.post(runLoop) }      // 5. repete
                        }
                    }
                }
            }
        }
    }

    private fun spamAttacks(coords: Coordinates, remaining: Int, onDone: () -> Unit) {
        if (!isRunning) return
        if (remaining <= 0) { onDone(); return }
        tap(coords.attackZone.x, coords.attackZone.y)
        handler.postDelayed({ spamAttacks(coords, remaining - 1, onDone) }, Random.nextLong(280, 420))
    }

    private fun delay(ms: Long, action: () -> Unit) {
        handler.postDelayed({ if (isRunning) action() }, ms)
    }

    private fun tap(x: Float, y: Float) {
        val path = Path().apply { moveTo(x, y) }
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, 60))
            .build()
        dispatchGesture(gesture, null, null)
    }

    companion object {
        var instance: ArenaAccessibilityService? = null
            private set

        private const val TAG = "ArenaBot"
        const val PREFS_NAME = "mcoc_bot"

        // Ajuste estes valores conforme a duração real das suas lutas de Arena.
        const val FIGHT_DURATION_TAPS = 40      // nº de toques durante o combate
        const val FIGHT_LOAD_DELAY_MS = 3500L   // espera a luta carregar
        const val POST_FIGHT_DELAY_MS = 1800L   // espera a animação de vitória
    }
}
