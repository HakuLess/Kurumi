package com.haku.kurumi.base

import android.util.Log
import rx.schedulers.Schedulers
import java.io.File
import kotlin.system.measureTimeMillis


/**
 * 执行adb命令
 *
 * @param cmd 命令具体内容 如 "input swipe 500 1800 500 300 1000"
 * */
fun exec(cmd: String) {
    try {
        Runtime.getRuntime().exec(cmd)
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

var process: Process? = null

/**
 * 执行拍摄屏幕
 *
 * @param filename 外部传入文件名称
 * */
fun takeScreenshot(filename: String): String {
    try {
//        val path = KurumiApplication.instance.filesDir
        val path = "/sdcard"
        val file: File = File(path, "/$filename")
        if (!file.exists()) {
            file.createNewFile()
        }

//        val process = Runtime.getRuntime().exec("screencap -p ${file.absolutePath}")

        runOnThread(scheduler = Schedulers.io()) {
            measureTimeMillis {
//            val process = Runtime.getRuntime().exec("screencap -p ${file.absolutePath}")
//            process.waitFor()
//                if (process == null) {
                val process = Runtime.getRuntime().exec("su", null, null)
//                }

                val os = process.outputStream
//                val str = "/system/bin/screencap -p /sdcard/img_${System.currentTimeMillis()}.png"
                val str = "/system/bin/screencap -p /sdcard/${filename}"

                os.write(str.toByteArray(charset("ASCII")))
                Log.d("HaKu", "exec $str")
                os.flush()
                os.close()
                process.waitFor()
            }.also {
                Log.d("HaKu", "run cost $it")
            }
//        val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            Log.d("HaKu", "path: ${file.absolutePath} ${file.exists()}")
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}