package com.haku.kurumi

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.PixelFormat
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.haku.kurumi.base.TAG
import com.haku.kurumi.base.exec
import com.haku.kurumi.base.readPngFile
import com.haku.kurumi.base.takeScreenshot
import com.haku.kurumi.base.toast
import com.haku.kurumi.ui.theme.KurumiTheme
import org.opencv.core.CvType
import org.opencv.core.Mat
import org.opencv.osgi.OpenCVNativeLoader
import java.nio.ByteBuffer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        exec("su")

        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            result.forEach { s, b ->
                Log.d("HaKu", "$s with $b")
            }
//            if (isGranted) {
//                // 用户授予了权限，执行写文件操作
//                toast("take test1")
//                takeScreenshot("test1.png")
//            } else {
//                // 用户拒绝了权限，处理相应逻辑
//            }
        }

//        val lp = WindowManager.LayoutParams().apply {
//            // 设置大小 自适应
//            width = 40
//            height = 40
//            flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
//        }
//
//        // 将悬浮窗控件添加到WindowManager
//        windowManager.addView(fab, lp)

        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.SYSTEM_ALERT_WINDOW
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.SYSTEM_ALERT_WINDOW),
                    2
            )
        } else {
        }

//        showFab()

        setContent {
            KurumiTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting(this)
                }
            }
        }
    }

    private fun showFab() {
        val windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
//        val outMetrics = DisplayMetrics()
//        windowManager.defaultDisplay.getMetrics(outMetrics)
        val layoutParam = WindowManager.LayoutParams().apply {
            type = WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
            format = PixelFormat.RGBA_8888
            flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            // 位置大小设置
            width = WRAP_CONTENT
            height = WRAP_CONTENT
            gravity = Gravity.LEFT or Gravity.TOP
            // 设置剧中屏幕显示
//            x = outMetrics.widthPixels / 2 - width / 2
//            y = outMetrics.heightPixels / 2 - height / 2
        }
        // 新建悬浮窗控件
        val fab = View(this)

        // 将悬浮窗控件添加到WindowManager
        windowManager.addView(fab, layoutParam)
    }
}

@Composable
fun Greeting(context: Activity) {

    val a = remember { mutableStateOf("") }

    Column {
        Button(onClick = {
            context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            Log.d(TAG, "123123")
        }) {

        }
        Text(
                text = "Hello!",
                fontSize = 30f.sp,
                modifier = Modifier.clickable {
                    Log.d("HaKu", "Before Click")
                    exec("input swipe 500 1800 500 300 1000")
                    Log.d("HaKu", "After Click")

                    if (ContextCompat.checkSelfPermission(
                                    context,
                                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        ActivityCompat.requestPermissions(
                                context,
                                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                                1
                        )
                    } else {
                        context.toast("take test")
                        takeScreenshot("${System.currentTimeMillis()}.png", a)
                    }
                },
        )

//        LocalImage("/sdcard/test.png")
        LocalImage(a.value)
//        val l = ArrayList<String>()
//        repeat(100) {
//            l.add(it.toString())
//        }
//        MyList(data = l)
    }
}

@Composable
fun LocalImage(filePath: String) {
    val bitmap: Bitmap = readPngFile(filePath) ?: return
    val imageBitmap = bitmap.asImageBitmap()
    // OpenCV提供的加载libopencv_java4.so的封装类
    val openCVNativeLoader = OpenCVNativeLoader()
    openCVNativeLoader.init()
    val buffer = ByteBuffer.allocateDirect(bitmap.byteCount)
    bitmap.copyPixelsToBuffer(buffer);
    val mat = Mat(bitmap.height, bitmap.width, CvType.CV_8UC4, buffer)
    Log.d("HaKu", "mat channels:" + mat.channels() + ", cols:" + mat.cols() + ", rows:" + mat.rows())

    imageBitmap?.let {
        Image(
                bitmap = it,
                contentDescription = null // 可选的内容描述
        )
    }
}


@Composable
fun MyList(data: List<String>) {
    LazyColumn {
        items(data.size) { item ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(text = data[item])
            }
        }
    }
}