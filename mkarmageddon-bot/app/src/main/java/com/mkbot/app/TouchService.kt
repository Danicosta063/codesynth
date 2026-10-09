// TouchService.kt - AccessibilityService para simular toques
package com.mkbot.app

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.graphics.Path
import android.view.accessibility.AccessibilityEvent

class TouchService : AccessibilityService() {
    
    companion object {
        fun performTouch(context: android.content.Context, x: Int, y: Int, duration: Long) {
            val intent = Intent(context, TouchService::class.java)
            intent.putExtra("x", x)
            intent.putExtra("y", y)
            intent.putExtra("duration", duration)
            context.startService(intent)
        }
    }
    
    override fun onServiceConnected() {
        super.onServiceConnected()
        // Configurar filtros de eventos
    }
    
    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Processar eventos de acessibilidade
    }
    
    override fun onInterrupt() {
        // Limpar recursos
    }
    
    override fun onStartCommand(intent: android.content.Intent?, flags: Int, startId: Int): Int {
        val x = intent?.getIntExtra("x", 0) ?: 0
        val y = intent?.getIntExtra("y", 0) ?: 0
        val duration = intent?.getLongExtra("duration", 100) ?: 100
        
        performClick(x, y, duration)
        return START_NOT_STICKY
    }
    
    private fun performClick(x: Int, y: Int, duration: Long) {
        val path = Path()
        path.moveTo(x.toFloat(), y.toFloat())
        
        val gesture = GestureDescription.Builder()
            .addStroke(GestureDescription.StrokeDescription(path, 0, duration))
            .build()
        
        dispatchGesture(gesture, null, null)
    }
}
