package com.example.letsgogambling

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs

@Suppress("DEPRECATION")
class ShakeDetector(
    context: Context,
    private val onShake: () -> Unit
) : SensorEventListener, LifecycleEventObserver {
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

    private var lastX = 0f
    private var lastY = 0f
    private var lastZ = 0f

    private var lastShakeTime = 0L
    private val shakeThreshold = 12f
    private val shakeCooldown = 12L
    private var isVibrating = false

    private val scope = CoroutineScope(Dispatchers.Default + Job())

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) { }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor?.type != Sensor.TYPE_ACCELEROMETER) return
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastShakeTime < shakeCooldown) return

        val (x, y, z) = event.values

        val deltaX = abs(x - lastX)
        val deltaY = abs(y - lastY)
        val deltaZ = abs(z - lastZ)
        if (deltaX > shakeThreshold || deltaY > shakeThreshold || deltaZ > shakeThreshold) {
            handleShake(currentTime)
        }

        lastX = x
        lastY = y
        lastZ = z
    }

    private fun handleShake(currentTime: Long) {
        onShake()
        lastShakeTime = currentTime
        vibrateDevice()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_RESUME -> registerListener()
            Lifecycle.Event.ON_PAUSE -> unregisterListener()
            Lifecycle.Event.ON_DESTROY -> {
                unregisterListener()
                scope.cancel()
            }
            else -> { }
        }
    }

    private fun registerListener() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    private fun unregisterListener() {
        sensorManager.unregisterListener(this)
    }

    private fun vibrateDevice() {
        if (isVibrating) return

        isVibrating = true
        scope.launch {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    vibrator.vibrate(200)
                }
                delay(200)
            } finally {
                isVibrating = false
            }
        }
    }
}