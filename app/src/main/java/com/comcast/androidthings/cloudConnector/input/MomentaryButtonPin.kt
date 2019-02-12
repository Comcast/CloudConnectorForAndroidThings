/**
 * Copyright 2019 Comcast Cable Communications Management, LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * SPDX-License-Identifier: Apache-2.0
 */

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
