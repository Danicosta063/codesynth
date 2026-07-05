package com.example.mcocarenabot

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Path
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Display
import android.view.accessibility.AccessibilityEvent
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.text.Normalizer

class ArenaAccessibilityService : AccessibilityService() {

    private val handler = Handler(Looper.getMainLooper())
    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
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
        recognizer.close()
        instance = null
        return super.onUnbind(intent)
    }

    fun startBot() {
        if (isRunning) return
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

    private fun captureBitmap(callback: (Bitmap?) -> Unit) {
        try {
            takeScreenshot(Display.DEFAULT_DISPLAY, mainExecutor, object : TakeScreenshotCallback {
                override fun onSuccess(screenshot: ScreenshotResult) {
                    try {
                        val hw = Bitmap.wrapHardwareBuffer(screenshot.hardwareBuffer, screenshot.colorSpace)
                        val bmp = hw?.copy(Bitmap.Config.ARGB_8888, false)
                        screenshot.hardwareBuffer.close()
                        callback(bmp)
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

    private fun findTextBoxes(targets: List<String>, callback: (List<Rect>) -> Unit) {
        captureBitmap { bitmap ->
            if (bitmap == null) {
                callback(emptyList())
                return@captureBitmap
            }
            val normTargets = targets.map { normalize(it) }
            val image = InputImage.fromBitmap(bitmap, 0)
            recognizer.process(image)
                .addOnSuccessListener { visionText ->
                    val boxes = visionText.textBlocks
                        .filter { block -> normTargets.any { normalize(block.text).contains(it) } }
                        .mapNotNull { it.boundingBox }
                    bitmap.recycle()
                    callback(boxes)
                }
                .addOnFailureListener {
                    bitmap.recycle()
                    callback(emptyList())
                }
        }
    }

    private fun normalize(s: String): String {
        val nfd = Normalizer.normalize(s, Normalizer.Form.NFD)
        return nfd.replace(Regex("\\p{M}"), "").uppercase()
    }

    fun samplePixel(x: Int, y: Int, callback: (Int?) -> Unit) {
        captureBitmap { bitmap ->
            val color = if (bitmap != null && x in 0 until bitmap.width && y in 0 until bitmap.height) {
                bitmap.getPixel(x, y)
            } else null
            bitmap?.recycle()
            callback(color)
        }
    }

    private fun defaultAttackPoint(): Pair<Float, Float> {
        val calibrated = Coordinates.load(prefs, "attackZone")
        if (calibrated.isSet()) return calibrated.x to calibrated.y
        val metrics = resources.displayMetrics
        return (metrics.widthPixels / 2f) to (metrics.heightPixels * 0.55f)
    }

    private data class Step(val key: String, val texts: List<String>, val pickBottom: Boolean = false)

    private val menuSteps = listOf(
        Step("quickSelect", listOf("SELECAO RAPIDA", "SELECAO")),
        Step("findMatch", listOf("ENCONTRAR PARTIDA", "ENCONTRAR")),
        Step("selectFight", listOf("FACIL"), pickBottom = true),
        Step("continueAfterSelect", listOf("CONTINUAR")),
        Step("accept", listOf("ACEITAR")),
        Step("continueBeforeFight", listOf("CONTINUAR"))
    )

    private fun runMenuSteps(index: Int, onDone: () -> Unit) {
        if (!isRunning) return
        if (index >= menuSteps.size) { onDone(); return }
        val step = menuSteps[index]
        statusNote = step.key
        waitAndTapText(step, System.currentTimeMillis() + STEP_TIMEOUT_MS) {
            runMenuSteps(index + 1, onDone)
        }
    }

    private fun runFight(fightsLeft: Int) {
        if (!isRunning) return
        if (fightsLeft <= 0) {
            statusNote = "recompensas"
            waitAndTapText(Step("nextSeries", listOf("PROXIMA SERIE", "PROXIMA")), System.currentTimeMillis() + STEP_TIMEOUT_MS) {
                runMenuSteps(0) { runFight(FIGHTS_PER_SERIES) }
            }
            return
        }
        statusNote = "lutando (${FIGHTS_PER_SERIES - fightsLeft + 1}/$FIGHTS_PER_SERIES)"
        fightTick(System.currentTimeMillis() + FIGHT_TIMEOUT_MS, fightsLeft)
    }

    private fun fightTick(deadline: Long, fightsLeft: Int) {
        if (!isRunning) return
        if (System.currentTimeMillis() > deadline) {
            proceedAfterFight(fightsLeft)
            return
        }
        findTextBoxes(listOf("CONTINUAR")) { boxes ->
            if (!isRunning) return@findTextBoxes
            if (boxes.isNotEmpty()) {
                proceedAfterFight(fightsLeft)
            } else {
                val (ax, ay) = defaultAttackPoint()
                tap(ax, ay)
                handler.postDelayed({ fightTick(deadline, fightsLeft) }, ATTACK_INTERVAL_MS)
            }
        }
    }

    private fun proceedAfterFight(fightsLeft: Int) {
        fightsCompleted++
        waitAndTapText(Step("continueAfterFight", listOf("CONTINUAR")), System.currentTimeMillis() + STEP_TIMEOUT_MS) {
            runFight(fightsLeft - 1)
        }
    }

    private fun waitAndTapText(step: Step, deadline: Long, onDone: () -> Unit) {
        if (!isRunning) return
        if (System.currentTimeMillis() > deadline) {
            val fallback = Coordinates.load(prefs, step.key)
            if (fallback.isSet()) tap(fallback.x, fallback.y)
            handler.postDelayed({ onDone() }, SETTLE_MS)
            return
        }
        findTextBoxes(step.texts) { boxes ->
            if (!isRunning) return@findTextBoxes
            val target = if (step.pickBottom) boxes.maxByOrNull { it.centerY() } else boxes.firstOrNull()
            if (target != null) {
                tap(target.exactCenterX(), target.exactCenterY())
                handler.postDelayed({ onDone() }, SETTLE_MS)
            } else {
                handler.postDelayed({ waitAndTapText(step, deadline, onDone) }, OCR_POLL_INTERVAL_MS)
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
        const val OCR_POLL_INTERVAL_MS = 900L
        const val SETTLE_MS = 500L
        const val STEP_TIMEOUT_MS = 20000L
        const val FIGHT_TIMEOUT_MS = 90000L
        const val ATTACK_INTERVAL_MS = 450L
    }
}
