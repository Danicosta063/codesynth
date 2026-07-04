package com.example.mcocarenabot

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent

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

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    override fun onUnbind(intent: Intent?): Boolean {
        stopBot()
        instance = null
        return super.onUnbind(intent)
    }

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

    private val runLoop = Runnable { if (isRunning) seriesCycle() }

    private fun seriesCycle() {
        val c = Coordinates.load(prefs)
        if (!c.isCalibrated()) {
            Log.w(TAG, "Coordenadas nao calibradas - calibre pelo widget antes de iniciar.")
            stopBot()
            return
        }

        tap(c.quickSelect)
        delay(SHORT) {
            tap(c.findMatch)
            delay(MATCHMAKING_DELAY_MS) {
                tap(c.selectFight)
                delay(SHORT) {
                    tap(c.continueAfterSelect)
                    delay(SHORT) {
                        tap(c.accept)
                        delay(SHORT) {
                            tap(c.continueBeforeFight)
                            delay(FIGHT_LOAD_DELAY_MS) {
                                runFight(c, FIGHTS_PER_SERIES)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun runFight(c: Coordinates, fightsLeft: Int) {
        if (!isRunning) return

        if (fightsLeft <= 0) {
            delay(REWARDS_LOAD_DELAY_MS) {
                tap(c.nextSeries)
                delay(SHORT) { handler.post(runLoop) }
            }
            return
        }

        if (USE_AUTOPLAY_BUTTON) {
            tap(c.autoplayButton)
            delay(AUTOPLAY_FIGHT_WAIT_MS) { afterFight(c, fightsLeft) }
        } else {
            spamAttacks(c, FIGHT_DURATION_TAPS) { afterFight(c, fightsLeft) }
        }
    }

    private fun afterFight(c: Coordinates, fightsLeft: Int) {
        delay(KO_DELAY_MS) {
            tap(c.attackZone)
            delay(SHORT) {
                tap(c.continueAfterFight)
                fightsCompleted++
                delay(SHORT) { runFight(c, fightsLeft - 1) }
            }
        }
    }

    private fun spamAttacks(c: Coordinates, remaining: Int, onDone: () -> Unit) {
        if (!isRunning) return
        if (remaining <= 0) { onDone(); return }
        tap(c.attackZone)
        handler.postDelayed({ spamAttacks(c, remaining - 1, onDone) }, kotlin.random.Random.nextLong(280, 420))
    }

    private fun delay(ms: Long, action: () -> Unit) {
        handler.postDelayed({ if (isRunning) action() }, ms)
    }

    private fun tap(p: Point) = tap(p.x, p.y)

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

        const val FIGHTS_PER_SERIES = 3
        const val SHORT = 700L
        const val MATCHMAKING_DELAY_MS = 2000L
        const val FIGHT_LOAD_DELAY_MS = 3500L
        const val KO_DELAY_MS = 2000L
        const val REWARDS_LOAD_DELAY_MS = 2000L

        // true = toca 1x no botao de autoplay do jogo e so espera
        // false = fica tocando na zona de ataque, sem defesa nenhuma
        const val USE_AUTOPLAY_BUTTON = false
        const val AUTOPLAY_FIGHT_WAIT_MS = 25000L
        const val FIGHT_DURATION_TAPS = 70
    }
}
