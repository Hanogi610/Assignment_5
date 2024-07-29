package com.example.assignment5.core.util

import android.content.Context
import android.content.SharedPreferences

enum class AppStart {
    FIRST_TIME, FIRST_TIME_VERSION, NORMAL
}

//private const val LAST_APP_VERSION = "last_app_version"

//fun checkAppStart(context: Context): AppStart {
//    val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
//    var appStart = AppStart.NORMAL
//    try {
//        val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
//        val lastVersionCode = sharedPreferences.getInt(LAST_APP_VERSION, -1)
//        val currentVersionCode = pInfo.versionCode
//        appStart = checkAppStart(currentVersionCode, lastVersionCode)
//        // Update version in preferences
//        sharedPreferences.edit().putInt(LAST_APP_VERSION, currentVersionCode).apply()
//    } catch (e: PackageManager.NameNotFoundException) {
//        println("Unable to determine current app version from package manager. Defensively assuming normal app start.")
//    }
//    return appStart
//}
//
//fun checkAppStart(currentVersionCode: Int, lastVersionCode: Int): AppStart {
//    return when {
//        lastVersionCode == -1 -> AppStart.FIRST_TIME
//        lastVersionCode < currentVersionCode -> AppStart.FIRST_TIME_VERSION
//        lastVersionCode > currentVersionCode -> {
//            println("Current version code ($currentVersionCode) is less than the one recognized on last startup ($lastVersionCode). Defensively assuming normal app start.")
//            AppStart.NORMAL
//        }
//        else -> AppStart.NORMAL
//    }
//}

fun checkAppStart(context: Context): AppStart {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
    val isFirstLaunch = sharedPreferences.getBoolean("IsFirstLaunch", true)

    return if (isFirstLaunch) {
        // If it's the first launch, update the flag in SharedPreferences
        sharedPreferences.edit().putBoolean("IsFirstLaunch", false).apply()
        AppStart.FIRST_TIME
    } else {
        AppStart.NORMAL
    }
}