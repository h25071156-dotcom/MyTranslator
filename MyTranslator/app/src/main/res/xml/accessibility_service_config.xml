package com.ahfei.translator

import android.accessibilityservice.AccessibilityService
import android.graphics.PixelFormat
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatOverlayService : AccessibilityService() {

    private lateinit var windowManager: WindowManager
    private val translatedNodes = mutableSetOf<String>()
    private val serviceScope = CoroutineScope(Dispatchers.Main)
    private val targetPackages = setOf("com.facebook.orca", "com.whatsapp")

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        if (event == null) return
        val packageName = event.packageName?.toString() ?: return
        if (!targetPackages.contains(packageName)) return

        val rootNode: AccessibilityNodeInfo = rootInActiveWindow ?: return
        scanAndTranslateNodes(rootNode)
        rootNode.recycle()
    }

    private fun scanAndTranslateNodes(node: AccessibilityNodeInfo) {
        for (i in 0 until node.childCount) {
            val child = node.getChild(i) ?: continue
            val text = child.text?.toString()

            if (!text.isNullOrBlank() && text.length > 1 && !translatedNodes.contains(text)) {
                translatedNodes.add(text)
                serviceScope.launch {
                    val translatedText = TranslateEngine.translateToTraditionalChinese(text)
                    if (translatedText != text) {
                        showTranslatedOverlay(translatedText, child)
                    }
                }
            }
            scanAndTranslateNodes(child)
            child.recycle()
        }
    }

    private fun showTranslatedOverlay(translatedText: String, node: AccessibilityNodeInfo) {
        try {
            val bounds = android.graphics.Rect()
            node.getBoundsInScreen(bounds)

            val params = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                PixelFormat.TRANSLUCENT
            ).apply {
                gravity = Gravity.TOP or Gravity.START
                x = bounds.left
                y = bounds.bottom + 5 // 完美顯示在訊息下方，不覆蓋原字體
            }

            val inflater = LayoutInflater.from(this)
            val translatedView = inflater.inflate(android.R.layout.simple_list_item_1, null) as TextView
            translatedView.text = translatedText
            translatedView.setTextColor(android.graphics.Color.YELLOW)
            translatedView.setBackgroundColor(android.graphics.Color.parseColor("#99000000"))

            windowManager.addView(translatedView, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onInterrupt() {
        translatedNodes.clear()
    }
}