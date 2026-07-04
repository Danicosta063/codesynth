package com.example.mcocarenabot

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Display
import android.view.accessibility.AccessibilityEvent

class ArenaAccessibilityService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private var isRunning = false
    private var fightsCompleted = 0
    private var statusNote = ""
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
        if (!Coordinates.isFullyCalibrated(prefs)) {
            Log.w(TAG, "Calibre (🎯) antes de iniciar.")
            return
        }
        isRunning = true
        fightsCompleted = 0
        statusNote = ""
        startService(Intent(this, BotForegroundService::class.java))
        runMenuSteps(0) { runFight(FIGHTS_PER_SERIES) }
    }

    fun stopBot() {
        isRunning = false
        handler.removeCallbacksAndMessages(null)
        stopService(Intent(this, BotForegroundService::class.java))
    }

    fun isBotRunning() = isRunning
    fun getFightsCompleted() = fightsCompleted
    fun getStatusNote() = statusNote

    fun samplePixel(x: Int, y: Int, callback: (Int?) -> Unit) {
        try {
            takeScreenshot(Display.DEFAULT_DISPLAY, mainExecutor, object : TakeScreenshotCallback {
                override fun onSuccess(screenshot: ScreenshotResult) {
                    try {
                        val hw = Bitmap.wrapHardwareBuffer(screenshot.hardwareBuffer, screenshot.colorSpace)
                        val bmp = hw?.copy(Bitmap.Config.ARGB_8888, false)
                        screenshot.hardwareBuffer.close()
                        val color = if (bmp != null && x in 0 until bmp.width && y in 0 until bmp.height) {
                            bmp.getPixel(x, y)
                        } else null
                        bmp?.recycle()
                        callback(color)
                    } catch (e: Exception) {
                        Log.w(TAG, "Erro lendo screenshot: ${e.message}")
                        callback(null)
                    }
                }

                override fun onFailure(errorCode: Int) {
                    callback(null)
                }
            })
        } catch (e: Exception) {
            Log.w(TAG, "takeScreenshot falhou: ${e.message}")
            callback(null)
        }
    }

    private fun colorsClose(a: Int, b: Int): Boolean {
        if (b == 0) return false
        return Math.abs(Color.red(a) - Color.red(b)) < COLOR_TOLERANCE &&
            Math.abs(Color.green(a) - Color.green(b)) < COLOR_TOLERANCE &&
            Math.abs(Color.blue(a) - Color.blue(b)) < COLOR_TOLERANCE
    }

    private val menuKeys = listOf(
        "quickSelect", "findMatch", "selectFight",
        "continueAfterSelect", "accept", "continueBeforeFight"
    )

    private fun runMenuSteps(index: Int, onDone: () -> Unit) {
        if (!isRunning) return
        if (index >= menuKeys.size) { onDone(); return }
        val key = menuKeys[index]
        statusNote = key
        waitAndTap(key, System.currentTimeMillis() + STEP_TIMEOUT_MS) {
            runMenuSteps(index + 1, onDone)
        }
    }

    private fun runFight(fightsLeft: Int) {
        if (!isRunning) return
        if (fightsLeft <= 0) {
            statusNote = "recompensas"
            waitAndTap("nextSeries", System.currentTimeMillis() + STEP_TIMEOUT_MS) {
                runMenuSteps(0) { runFight(FIGHTS_PER_SERIES) }
            }
            return
        }
        statusNote = "lutando (${FIGHTS_PER_SERIES - fightsLeft + 1}/$FIGHTS_PER_SERIES)"
        fightTick(System.currentTimeMillis() + FIGHT_TIMEOUT_MS, fightsLeft)
    }

    private fun fightTick(deadline: Long, fightsLeft: Int) {
        if (!isRunning) return
        val cf = Coordinates.load(prefs, "continueAfterFight")
        val az = Coordinates.load(prefs, "attackZone")

        if (System.currentTimeMillis() > deadline) {
            proceedAfterFight(fightsLeft)
            return
        }

        samplePixel(cf.x.toInt(), cf.y.toInt()) { color ->
            if (!isRunning) return@samplePixel
            if (color != null && colorsClose(color, cf.color)) {
                proceedAfterFight(fightsLeft)
            } else {
                tap(az.x, az.y)
                handler.postDelayed({ fightTick(deadline, fightsLeft) }, ATTACK_INTERVAL_MS)
            }
        }
    }

    private fun proceedAfterFight(fightsLeft: Int) {
        fightsCompleted++
        waitAndTap("continueAfterFight", System.currentTimeMillis() + STEP_TIMEOUT_MS) {
            runFight(fightsLeft - 1)
        }
    }

    private fun waitAndTap(key: String, deadline: Long, onDone: () -> Unit) {
        if (!isRunning) return
        val point = Coordinates.load(prefs, key)
        if (!point.isSet()) {
            Log.w(TAG, "Ponto '$key' nao calibrado.")
            stopBot()
            return
        }
        if (System.currentTimeMillis() > deadline) {
            tap(point.x, point.y)
            handler.postDelayed({ onDone() }, SETTLE_MS)
            return
        }
        samplePixel(point.x.toInt(), point.y.toInt()) { color ->
            if (!isRunning) return@samplePixel
            if (color != null && colorsClose(color, point.color)) {
                tap(point.x, point.y)
                handler.postDelayed({ onDone() }, SETTLE_MS)
            } else {
                handler.postDelayed({ waitAndTap(key, deadline, onDone) }, POLL_INTERVAL_MS)
            }
        }
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

        const val FIGHTS_PER_SERIES = 3
        const val COLOR_TOLERANCE = 30
        const val POLL_INTERVAL_MS = 700L
        const val SETTLE_MS = 500L
        const val STEP_TIMEOUT_MS = 20000L
        const val FIGHT_TIMEOUT_MS = 90000L
        const val ATTACK_INTERVAL_MS = 350L
    }
}
