// MainActivity.kt
package com.mkbot.app

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView

class MainActivity : Activity() {
    private lateinit var botEngine: BotEngine
    private var screenCapturePermission: Int = 0
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        botEngine = BotEngine(this)
        
        // Botão para iniciar o bot
        findViewById<Button>(R.id.btnStartBot).setOnClickListener {
            startBot()
        }
        
        // Botão para parar o bot
        findViewById<Button>(R.id.btnStopBot).setOnClickListener {
            stopBot()
        }
        
        // Solicitar permissão de captura de tela
        requestScreenCapturePermission()
    }
    
    private fun requestScreenCapturePermission() {
        val mediaProjectionManager = 
            getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        val captureIntent = mediaProjectionManager.createScreenCaptureIntent()
        startActivityForResult(captureIntent, 1000)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            screenCapturePermission = resultCode
            botEngine.setupMediaProjection(data)
        }
    }
    
    private fun startBot() {
        botEngine.startGameBot(
            gamePackage = "com.nethersx2.emulator", // Package do NetherSX2
            config = BotConfig(
                healthBarRegion = Rect(100, 50, 400, 30), // Área barra vida
                comboSequence = listOf(
                    TouchAction(200, 500, 100),  // Quadrado
                    TouchAction(200, 500, 100),  // Quadrado
                    TouchAction(300, 500, 150)   // Triângulo
                )
            )
        )
    }
    
    private fun stopBot() {
        botEngine.stopGameBot()
    }
}
