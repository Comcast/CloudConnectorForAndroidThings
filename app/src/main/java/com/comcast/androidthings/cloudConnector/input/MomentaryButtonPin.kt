package com.comcast.androidthings.cloudConnector.input

import com.google.android.things.pio.Gpio
import com.google.android.things.pio.GpioCallback
import com.google.android.things.pio.PeripheralManager

// To trigger, should be pulled to VCC with 1K resistor
class MomentaryButtonPin(private val portName: String) {

    private var gpio: Gpio? = null
    private var gpioCallback: GpioCallback? = null

    fun registerCallback(gpioCallback: GpioCallback) {
        this.gpioCallback = gpioCallback.apply { gpio?.registerGpioCallback(this) }
    }

    fun open(): MomentaryButtonPin {
        close()
        gpio = PeripheralManager.getInstance().openGpio(portName)
        gpio?.setDirection(Gpio.DIRECTION_IN)
        gpio?.setEdgeTriggerType(Gpio.EDGE_FALLING)

        gpio?.setActiveType(Gpio.ACTIVE_LOW)

        return this
    }

    fun close() {
        gpio?.unregisterGpioCallback(gpioCallback)
        gpio?.close().also {
            gpio = null
        }
    }
}
