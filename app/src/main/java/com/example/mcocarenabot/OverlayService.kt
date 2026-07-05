package com.example.mcocarenabot

import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView

class OverlayService : Service() {

    private lateinit var windowManager: WindowManager
    private lateinit var widgetView: View
    private lateinit var params: WindowManager.LayoutParams

    override fun onCreate() {
        super.onCreate()

        if (!Settings.canDrawOverlays(this)) {
            stopSelf()
            return
        }

        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        widgetView = LayoutInflater.from(this).inflate(R.layout.overlay_widget, null)

        val overlayType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else
            @Suppress("DEPRECATION") WindowManager.LayoutParams.TYPE_PHONE

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 40
        params.y = 200

        windowManager.addView(widgetView, params)
        setupDrag()
        setupButtons()
        updateStatusText()
    }

    private fun setupDrag() {
        val dragHandle = widgetView.findViewById<View>(R.id.dragHandle)
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f

        dragHandle.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX + (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(widgetView, params)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupButtons() {
        widgetView.findViewById<View>(R.id.btnStart).setOnClickListener {
            ArenaAccessibilityService.instance?.startBot()
            updateStatusText()
        }
        widgetView.findViewById<View>(R.id.btnStop).setOnClickListener {
            ArenaAccessibilityService.instance?.stopBot()
            updateStatusText()
        }
        widgetView.findViewById<View>(R.id.btnClose).setOnClickListener {
            ArenaAccessibilityService.instance?.stopBot()
            stopSelf()
        }
    }

    private fun updateStatusText() {
        val svc = ArenaAccessibilityService.instance
        val running = svc?.isBotRunning() ?: false
        val fights = svc?.getFightsCompleted() ?: 0
        val note = svc?.getStatusNote() ?: ""
        widgetView.findViewById<TextView>(R.id.statusLabel).text =
            if (running) "Rodando • $fights lutas • $note" else "Parado"
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::widgetView.isInitialized) windowManager.removeView(widgetView)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
