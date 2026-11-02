package com.custom.yaostweaker

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(64, 64, 64, 64)
        }

        val btnApplyTweaks = Button(this).apply {
            text = "1. Применить быстрые твики (Без анимаций + Сеть)"
            isFocusable = true
            setOnClickListener { applySystemTweaks() }
        }

        val btnOpenSettings = Button(this).apply {
            text = "2. Открыть скрытые Настройки Android"
            isFocusable = true
            setOnClickListener { openHiddenActivity(Settings.ACTION_SETTINGS) }
        }

        val btnOpenDev = Button(this).apply {
            text = "3. Открыть меню Для разработчиков"
            isFocusable = true
            setOnClickListener { openHiddenActivity(Settings.ACTION_APPLICATION_DEVELOPMENT_SETTINGS) }
        }

        val btnOpenApps = Button(this).apply {
            text = "4. Открыть системный список приложений"
            isFocusable = true
            setOnClickListener { openHiddenActivity(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS) }
        }

        val btnOpenAccessibility = Button(this).apply {
            text = "5. Открыть Специальные возможности"
            isFocusable = true
            setOnClickListener { openHiddenActivity(Settings.ACTION_ACCESSIBILITY_SETTINGS) }
        }

        layout.addView(btnApplyTweaks)
        layout.addView(btnOpenSettings)
        layout.addView(btnOpenDev)
        layout.addView(btnOpenApps)
        layout.addView(btnOpenAccessibility)

        setContentView(layout)
        btnApplyTweaks.requestFocus()
    }

    private fun applySystemTweaks() {
        try {
            val cr = contentResolver
            Settings.Global.putFloat(cr, Settings.Global.WINDOW_ANIMATION_SCALE, 0.0f)
            Settings.Global.putFloat(cr, Settings.Global.TRANSITION_ANIMATION_SCALE, 0.0f)
            Settings.Global.putFloat(cr, Settings.Global.ANIMATOR_DURATION_SCALE, 0.0f)
            Settings.Global.putInt(cr, Settings.Global.WIFI_SLEEP_POLICY, Settings.Global.WIFI_SLEEP_POLICY_NEVER)
            Settings.Secure.putInt(cr, Settings.Secure.INSTALL_NON_MARKET_APPS, 1)

            Toast.makeText(this, "Параметры успешно применены!", Toast.LENGTH_LONG).show()
        } catch (e: SecurityException) {
            Toast.makeText(
                this,
                "Нужны права! Выполните с ПК: adb shell pm grant com.custom.yaostweaker android.permission.WRITE_SECURE_SETTINGS",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun openHiddenActivity(action: String) {
        try {
            val intent = Intent(action).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            try {
                val fallback = Intent().apply {
                    component = ComponentName("com.android.settings", "com.android.settings.Settings")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(fallback)
            } catch (ex: Exception) {
                Toast.makeText(this, "Ошибка: ${ex.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
