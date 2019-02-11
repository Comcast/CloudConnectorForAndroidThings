package com.comcast.androidthings.cloudConnector.output

import android.util.Log
import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager

class BinaryOutputPin(private val portName: String) {

    private var gpio: Gpio? = null

    fun open() {
        close()
        gpio = PeripheralManager.getInstance().openGpio(portName)
        gpio?.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)

        Log.d("BinaryOutputPin", "connected.")
    }

    fun close() {
        gpio?.close().also {
            gpio = null
        }
    }

    fun setState(state: Boolean) {
        gpio?.value = state
    }

}
