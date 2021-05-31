package user.example.accball

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), SensorEventListener
                        , SurfaceHolder.Callback {
    private var surfaceWidth: Int = 0       // サーフェスビューの幅
    private var surfaceHeight: Int = 0      // サーフェスビューの高さ

    private val radius = 50.0f              // ボールの半径を表す定数
    private val coef = 1000.0f              // ボールの移動量を調整するための計数

    private var ballX: Float = 0f           // ボールの現在のx座標
    private var ballY: Float = 0f           // ボールの現在のy座標
    private var vx: Float = 0f              // ボールのx方向への加速度
    private var vy: Float = 0f              // ボールのy方向への加速度
    private var time: Long = 0L             // 前回時間の保持

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)
        val holder = surfaceView.holder
        holder.addCallback(this)
    }

    override fun onResume() {
        super.onResume()
        val sensorManager = getSystemService(Context.SENSOR_SERVICE)
                as SensorManager
        val accSensor = sensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(
            this, accSensor,
            SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        val sensorManager = getSystemService(Context.SENSOR_SERVICE)
                as SensorManager
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        if (time == 0L) time = System.currentTimeMillis()
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val x = -event.values[0]
            val y = event.values[1]

            var t = (System.currentTimeMillis() - time).toFloat()
            time = System.currentTimeMillis()
            t /= 1000.0f

            val dx = vx * t + x * t * t / 2.0f
            val dy = vy * t + y * t * t / 2.0f
            ballX += dx * coef
            ballY += dy * coef
            vx += x * t
            vy += y * t

            if (ballX - radius < 0 && vx < 0) {
                vx = -vx / 1.5f
                ballX = radius
            } else if (ballX + radius > surfaceWidth && vx > 0) {
                vx = -vx / 1.5f
                ballX = surfaceWidth - radius
            }
            if (ballY - radius < 0 && vy < 0) {
                vy = -vy / 1.5f
                ballY = radius
            } else if (ballY + radius > surfaceHeight && vy > 0) {
                vy = -vy / 1.5f
                ballY = surfaceHeight - radius
            }
            drawCanvas()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE)
                    as SensorManager
        val accSensor = sensorManager.getDefaultSensor(
            Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(
            this, accSensor,
            SensorManager.SENSOR_DELAY_GAME)
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        surfaceWidth = width
        surfaceHeight = height
        ballX = (width / 2).toFloat()
        ballY = (height / 2).toFloat()
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        val sensorManager = getSystemService(Context.SENSOR_SERVICE)
                    as SensorManager
        sensorManager.unregisterListener(this)
    }

    private fun drawCanvas() {
        val canvas = surfaceView.holder.lockCanvas()
        canvas.drawColor(Color.YELLOW)
        canvas.drawCircle(ballX, ballY, radius, Paint().apply {
            color = Color.MAGENTA
        })
        surfaceView.holder.unlockCanvasAndPost(canvas)
    }
}