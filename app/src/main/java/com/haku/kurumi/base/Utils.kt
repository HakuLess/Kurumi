package com.haku.kurumi.base

import android.content.Context
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import rx.Observable
import rx.Scheduler
import java.io.File

fun Context.toast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun readPngFile(filePath: String): ImageBitmap? {
    Log.d("HaKu", "path is $filePath")
    val file = File(filePath)
    if (!file.exists()) {
        return null
    }
    Log.d("HaKu", "abs Path is ${file.absolutePath}")
    return BitmapFactory.decodeFile(file.absolutePath)?.asImageBitmap()
}

fun runOnThread(scheduler: Scheduler, func: () -> Unit) {
    Observable.just("")
            .subscribeOn(scheduler)
            .observeOn(scheduler)
            .subscribe({
                func()
            }, {
                it.printStackTrace()
            })
}
