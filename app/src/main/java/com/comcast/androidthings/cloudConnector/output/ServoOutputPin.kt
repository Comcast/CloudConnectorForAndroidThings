package com.comcast.androidthings.cloudConnector.output

import android.util.Log
import com.google.android.things.pio.PeripheralManager
import com.google.android.things.pio.Pwm

class ServoOutputPin(private val portName: String) {

    private var pwm: Pwm? = null

    fun open() {
        close()
        pwm = PeripheralManager.getInstance().openPwm(portName)
        pwm?.setPwmFrequencyHz(50.0)
        setPosition(SERVO_POSITION_IN_DEGREES.NINETY)
        pwm?.setEnabled(true)

        Log.d("ServoOutputPin", "connected.")
    }

    fun close() {
        pwm?.close().also {
            pwm = null
        }
    }

    fun setPosition(position: SERVO_POSITION_IN_DEGREES) {
        Log.e("ServoOutputPin", "setting DutyCycle to ${position.dutyCyclePercentage}.")
        try {
            pwm?.setPwmDutyCycle(position.dutyCyclePercentage)
        } catch (ex: Exception) {
            Log.e("ServoOutputPin", "Exception while setting position.", ex)
        }
    }

    enum class SERVO_POSITION_IN_DEGREES(val dutyCyclePercentage: Double) {
        ZERO (3.0),
        //THIRTY (.9),
        //SIXTY (1.2),
        NINETY (5.0),
        //ONE_TWENTY (1.8),
        //ONE_FIFTY (2.1);
        ONE_SEVENTY (7.0),
        ONE_EIGHTY (10.0),
        TWO_SEVENTY (12.0);
    }
}
