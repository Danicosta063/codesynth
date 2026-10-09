// BotEngine.kt
package com.mkbot.app

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Rect
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.ImageReader
import android.media.projection.MediaProjection
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.*
import org.opencv.android.OpenCVLoader
import org.opencv.core.*
import org.opencv.imgproc.Imgproc

class BotEngine(private val context: Context) {
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var imageReader: ImageReader? = null
    private var botJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    
    // Configuração do bot
    data class BotConfig(
        val healthBarRegion: Rect,
        val comboSequence: List<TouchAction>
    )
    
    data class TouchAction(
        val x: Int,
        val y: Int,
        val duration: Long
    )
    
    init {
        // Inicializar OpenCV
        if (!OpenCVLoader.initDebug()) {
            Log.e("BotEngine", "Falha ao inicializar OpenCV")
        }
    }
    
    fun setupMediaProjection(data: Intent) {
        mediaProjection = MediaProjection(
            context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager,
            data
        )
        setupVirtualDisplay()
    }
    
    private fun setupVirtualDisplay() {
        val metrics = context.resources.displayMetrics
        val width = metrics.widthPixels
        val height = metrics.heightPixels
        
        imageReader = ImageReader.newInstance(width, height, PixelFormat.RGBA_8888, 2)
        virtualDisplay = mediaProjection?.createVirtualDisplay(
            "BotScreen",
            width, height, metrics.densityDpi,
            DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
            imageReader?.surface,
            null, null
        )
    }
    
    fun startGameBot(gamePackage: String, config: BotConfig) {
        botJob = scope.launch {
            while (isActive) {
                // 1. Capturar frame
                val frame = captureFrame() ?: continue
                
                // 2. Analisar estado do jogo
                val gameState = analyzeGameFrame(frame, config)
                
                // 3. Tomar decisão
                when (gameState) {
                    is GameState.Combat -> executeCombatActions(gameState)
                    is GameState.Menu -> navigateMenus()
                    is GameState.FinishHim -> executeFatality()
                    else -> Log.d("BotEngine", "Estado: ${gameState::class.simpleName}")
                }
                
                delay(100) // Pequena pausa entre frames
            }
        }
    }
    
    private fun captureFrame(): Bitmap? {
        val image = imageReader?.acquireLatestImage() ?: return null
        val planes = image.planes
        val buffer = planes[0].buffer
        val pixelStride = planes[0].pixelStride
        val rowStride = planes[0].rowStride
        val rowPadding = rowStride - pixelStride * image.width
        
        // Criar bitmap
        val bitmap = Bitmap.createBitmap(
            image.width + rowPadding / pixelStride,
            image.height, Bitmap.Config.ARGB_8888
        )
        bitmap.copyPixelsFromBuffer(buffer)
        image.close()
        
        return bitmap
    }
    
    private fun analyzeGameFrame(frame: Bitmap, config: BotConfig): GameState {
        // Converter Bitmap para Mat do OpenCV
        val mat = Mat()
        org.opencv.android.Utils.bitmapToMat(frame, mat)
        
        // Análise da barra de vida
        val healthAnalysis = analyzeHealthBars(mat, config.healthBarRegion)
        
        // Análise da distância entre personagens
        val distanceAnalysis = analyzeCharacterDistance(mat)
        
        // Decidir estado baseado na análise
        return when {
            healthAnalysis.isFinishHim -> GameState.FinishHim
            healthAnalysis.isMenu -> GameState.Menu
            else -> GameState.Combat(
                healthAnalysis,
                distanceAnalysis,
                shouldBlock = healthAnalysis.playerHealth < 30,
                shouldAttack = distanceAnalysis < 100
            )
        }
    }
    
    private fun analyzeHealthBars(frame: Mat, region: Rect): HealthAnalysis {
        // Extrair região das barras de vida
        val healthRegion = Mat(frame, region)
        
        // Converter para HSV
        val hsv = Mat()
        Imgproc.cvtColor(healthRegion, hsv, Imgproc.COLOR_BGR2HSV)
        
        // Criar máscaras para cores da barra de vida
        val greenMask = Mat()
        Core.inRange(hsv, Scalar(35, 70, 60), Scalar(85, 255, 255), greenMask)
        
        val yellowMask = Mat()
        Core.inRange(hsv, Scalar(15, 70, 60), Scalar(34, 255, 255), yellowMask)
        
        val redMask = Mat()
        Core.inRange(hsv, Scalar(0, 70, 60), Scalar(10, 255, 255), redMask)
        
        // Calcular % de vida
        val totalArea = region.width * region.height
        val greenArea = Core.countNonZero(greenMask)
        val yellowArea = Core.countNonZero(yellowMask)
        val redArea = Core.countNonZero(redMask)
        
        val playerHealth = (greenArea + yellowArea + redArea).toFloat() / totalArea
        
        return HealthAnalysis(
            playerHealth = playerHealth,
            opponentHealth = 1.0f - playerHealth, // Simplificado
            isMenu = false,
            isFinishHim = false
        )
    }
    
    private fun analyzeCharacterDistance(frame: Mat): Float {
        // Implementar detecção de movimento entre frames
        // Simplificado: retorna distância média
        return 50.0f
    }
    
    private suspend fun executeCombatActions(state: GameState.Combat) {
        // Executar combo se estiver perto
        if (state.shouldAttack) {
            for (action in config.comboSequence) {
                TouchService.performTouch(context, action.x, action.y, action.duration)
                delay(100)
            }
        }
        
        // Bloquear se vida baixa
        if (state.shouldBlock) {
            TouchService.performTouch(context, 200, 800, 500) // Botão bloqueio
        }
    }
    
    private suspend fun navigateMenus() {
        // Navegar nos menus do jogo
        TouchService.performTouch(context, 500, 1000, 200)
        delay(500)
    }
    
    private suspend fun executeFatality() {
        // Executar sequência de fatality
        // Implementar com sequências específicas
    }
    
    fun stopGameBot() {
        botJob?.cancel()
        virtualDisplay?.release()
        mediaProjection?.stop()
    }
    
    // Estados do jogo
    sealed class GameState {
        object Menu : GameState()
        object FinishHim : GameState()
        data class Combat(
            val health: HealthAnalysis,
            val distance: Float,
            val shouldBlock: Boolean,
            val shouldAttack: Boolean
        ) : GameState()
    }
    
    data class HealthAnalysis(
        val playerHealth: Float,
        val opponentHealth: Float,
        val isMenu: Boolean,
        val isFinishHim: Boolean
    )
}
