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
    private var lastCaptureOk = true
    private var lastErrorCode: Int? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
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
        runMenuSteps { runFight(FIGHTS_PER_SERIES) }
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
        var responded = false
        val timeoutRunnable = Runnable {
            if (!responded) {
                responded = true
                lastErrorCode = -1
                Log.w(TAG, "takeScreenshot sem resposta em ${CAPTURE_TIMEOUT_MS}ms - desistindo desta tentativa")
                lastCaptureOk = false
                callback(null)
            }
        }
        handler.postDelayed(timeoutRunnable, CAPTURE_TIMEOUT_MS)

        try {
            takeScreenshot(Display.DEFAULT_DISPLAY, mainExecutor, object : TakeScreenshotCallback {
                override fun onSuccess(screenshot: ScreenshotResult) {
                    if (responded) return
                    responded = true
                    handler.removeCallbacks(timeoutRunnable)
                    try {
                        val hw = Bitmap.wrapHardwareBuffer(screenshot.hardwareBuffer, screenshot.colorSpace)
                        val bmp = hw?.copy(Bitmap.Config.ARGB_8888, false)
                        screenshot.hardwareBuffer.close()
                        lastCaptureOk = bmp != null
                        callback(bmp)
                    } catch (e: Exception) {
                        Log.w(TAG, "Erro lendo screenshot: ${e.message}")
                        lastCaptureOk = false
                        callback(null)
                    }
                }

                override fun onFailure(errorCode: Int) {
                    if (responded) return
                    responded = true
                    handler.removeCallbacks(timeoutRunnable)
                    lastErrorCode = errorCode
                    Log.w(TAG, "takeScreenshot onFailure codigo=$errorCode")
                    lastCaptureOk = false
                    callback(null)
                }
            })
        } catch (e: Exception) {
            if (!responded) {
                responded = true
                handler.removeCallbacks(timeoutRunnable)
                Log.w(TAG, "takeScreenshot lancou excecao: ${e.message}")
                lastCaptureOk = false
                callback(null)
            }
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

    private fun defaultAttackPoint(): Pair<Float, Float> {
        val metrics = resources.displayMetrics
        return (metrics.widthPixels / 2f) to (metrics.heightPixels * 0.55f)
    }

    private fun waitAndTapFixed(
        label: String,
        texts: List<String>,
        point: Pair<Float, Float>,
        deadline: Long,
        attempt: Int = 1,
        onDone: () -> Unit
    ) {
        if (!isRunning) return
        if (System.currentTimeMillis() > deadline) {
            statusNote = "$label: tempo esgotado, tocando assim mesmo"
            tap(point.first, point.second)
            handler.postDelayed({ onDone() }, SETTLE_MS)
            return
        }
        statusNote = "$label (tentativa $attempt)"
        findTextBoxes(texts) { boxes ->
            if (!isRunning) return@findTextBoxes
            if (boxes.isNotEmpty()) {
                tap(point.first, point.second)
                handler.postDelayed({ onDone() }, SETTLE_MS)
            } else {
                statusNote = "$label (tentativa $attempt, print ${if (lastCaptureOk) "ok, texto nao achado" else "FALHOU cod=$lastErrorCode"})"
                handler.postDelayed(
                    { waitAndTapFixed(label, texts, point, deadline, attempt + 1, onDone) },
                    OCR_POLL_INTERVAL_MS
                )
            }
        }
    }

    private fun runMenuSteps(onDone: () -> Unit) {
        if (!isRunning) return
        val deadline = { System.currentTimeMillis() + STEP_TIMEOUT_MS }
        waitAndTapFixed("selecionando campeoes", listOf("SELECAO"), Coordinates.quickSelect, deadline()) {
            waitAndTapFixed("procurando partida", listOf("ENCONTRAR"), Coordinates.findMatch, deadline()) {
                waitAndTapFixed("escolhendo luta", listOf("FACIL"), Coordinates.selectFight, deadline()) {
                    waitAndTapFixed("confirmando luta", listOf("CONTINUAR"), Coordinates.continueAfterSelect, deadline()) {
                        waitAndTapFixed("aceitando", listOf("ACEITAR"), Coordinates.accept, deadline()) {
                            waitAndTapFixed("iniciando luta", listOf("CONTINUAR"), Coordinates.continueBeforeFight, deadline()) {
                                onDone()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun runFight(fightsLeft: Int) {
        if (!isRunning) return
        if (fightsLeft <= 0) {
            waitAndTapFixed("recompensas", listOf("CONTINUAR"), Coordinates.continueAfterLastFight, System.currentTimeMillis() + STEP_TIMEOUT_MS) {
                waitAndTapFixed("proxima serie", listOf("PROXIMA"), Coordinates.nextSeries, System.currentTimeMillis() + STEP_TIMEOUT_MS) {
                    runMenuSteps { runFight(FIGHTS_PER_SERIES) }
                }
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
        waitAndTapFixed("continuar pos-luta", listOf("CONTINUAR"), Coordinates.continueAfterFight, System.currentTimeMillis() + STEP_TIMEOUT_MS) {
            runFight(fightsLeft - 1)
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

        const val FIGHTS_PER_SERIES = 3
        const val OCR_POLL_INTERVAL_MS = 900L
        const val CAPTURE_TIMEOUT_MS = 5000L
        const val SETTLE_MS = 500L
        const val STEP_TIMEOUT_MS = 20000L
        const val FIGHT_TIMEOUT_MS = 90000L
        const val ATTACK_INTERVAL_MS = 450L
    }
}
