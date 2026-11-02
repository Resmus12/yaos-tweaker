package com.custom.yaostweaker

import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Toast

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val scroll = ScrollView(this)
        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(48, 48, 48, 48)
        }

        fun addBtn(title: String, onClick: () -> Unit) {
            val btn = Button(this).apply {
                text = title
                isFocusable = true
                setOnClickListener { onClick() }
            }
            layout.addView(btn)
        }

        // 1. Прямой вызов классических настроек AOSP (в обход оболочки YaOS)
        addBtn("1. Корневые Настройки AOSP (Прямой вызов пакета)") {
            launchDirectComponent(
                "com.android.settings",
                "com.android.settings.Settings"
            )
        }

        // 2. Прямой вызов корневого экрана Android TV Settings
        addBtn("2. Главные Настройки Android TV (MainSettings)") {
            launchDirectComponent(
                "com.android.tv.settings",
                "com.android.tv.settings.MainSettings"
            )
        }

        // 3. Принудительный вызов раздела Приложения и Разрешения
        addBtn("3. Все системные приложения (ManageApplications)") {
            launchDirectComponent(
                "com.android.settings",
                "com.android.settings.applications.ManageApplications"
            )
        }

        // 4. Меню разработчика напрямую через компонент
        addBtn("4. Меню Разработчика (Прямой вход)") {
            launchDirectComponent(
                "com.android.tv.settings",
                "com.android.tv.settings.system.development.DevelopmentActivity"
            )
        }

        // 5. Специальные возможности напрямую
        addBtn("5. Специальные возможности (Accessibility)") {
            openByAction(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        }

        // 6. Показать список вообще всех системных экранов настроек в прошивке
        addBtn("6. Просканировать и показать ВСЕ скрытые окна настроек") {
            scanAndLaunchAllSettings(layout)
        }

        scroll.addView(layout)
        setContentView(scroll)
    }

    private fun launchDirectComponent(pkg: String, cls: String) {
        try {
            val intent = Intent().apply {
                component = ComponentName(pkg, cls)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "Пакет $cls вырезан, пробую запасной метод...", Toast.LENGTH_SHORT).show()
            openByAction(Settings.ACTION_SETTINGS)
        }
    }

    private fun openByAction(action: String) {
        try {
            startActivity(Intent(action).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    // Встроенный аналог Activity Launcher: находит все экраны внутри пакетов настроек ТВ
   private fun scanAndLaunchAllSettings(layout: LinearLayout) {
        val pkgs = packageManager.getInstalledPackages(android.content.pm.PackageManager.GET_ACTIVITIES)
        var count = 0
        for (pkgInfo in pkgs) {
            val pkgName = pkgInfo.packageName
            // Ищем во всех системных, заводских и яндексовских пакетах, кроме самого твикера
            if (pkgName != packageName) {
                pkgInfo.activities?.forEach { act ->
                    if (act.exported && (act.name.contains("Setting", true) ||
                                         act.name.contains("Debug", true) ||
                                         act.name.contains("Factory", true) ||
                                         act.name.contains("Menu", true) ||
                                         act.name.contains("Launcher", true) ||
                                         act.name.contains("Adb", true))) {
                        count++
                        val shortName = act.name.substringAfterLast(".")
                        val btn = Button(this).apply {
                            text = "[$pkgName] $shortName"
                            setOnClickListener {
                                launchDirectComponent(pkgName, act.name)
                            }
                        }
                        layout.addView(btn)
                    }
                }
            }
        }
        Toast.makeText(this, "Найдено скрытых экранов во всей прошивке: $count", Toast.LENGTH_LONG).show()
    }
}
