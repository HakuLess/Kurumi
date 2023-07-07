package com.haku.kurumi.base

import android.util.Log
import androidx.compose.runtime.MutableState
import rx.schedulers.Schedulers
import java.io.File
import kotlin.system.measureTimeMillis


// 本APP存储专用地址
const val dir = "/sdcard/kurumi/"

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
fun takeScreenshot(filename: String, a: MutableState<String>? = null): String {
    try {
//        val path = KurumiApplication.instance.filesDir
        val file: File = File(dir, "/$filename")
        if (!file.exists()) {
            if (!file.parentFile.exists()) {
                file.parentFile.mkdirs()
            }
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
                val str = "/system/bin/screencap -p ${dir}/${filename}"

                os.write(str.toByteArray(charset("ASCII")))
                Log.d("HaKu", "exec $str")
                os.flush()
                os.close()
                process.waitFor()

                if (a != null) {
                    runOnMainThread {
                        Log.d("HaKu", "set value before")
                        a.value = "/${dir}/${filename}"
                        Log.d("HaKu", "set value after")
                    }
                }
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