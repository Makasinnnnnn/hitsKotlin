package com.example.hitskotlin.ui

import android.app.Activity
import android.content.Intent
import com.example.hitskotlin.CatalogActivity
import com.example.hitskotlin.LoginOrRegActivity

fun Activity.openCatalog() {
    startActivity(Intent(this, CatalogActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    })
    finish()
}

fun Activity.openWelcome() {
    startActivity(Intent(this, LoginOrRegActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    })
    finish()
}
