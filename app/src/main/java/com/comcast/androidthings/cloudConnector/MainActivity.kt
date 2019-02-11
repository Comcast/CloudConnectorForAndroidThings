package com.comcast.androidthings.cloudConnector

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.CompoundButton
import com.comcast.androidthings.cloudConnector.cloud.CloudInformation
import com.comcast.androidthings.cloudConnector.cloud.CloudInformationLiveData
import com.comcast.androidthings.cloudConnector.cloud.CloudInformationUpdater
import com.comcast.androidthings.cloudConnector.cloud.HOME_INFORMATION_ROOT
import com.google.android.things.pio.GpioCallback
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.comcast.androidthings.cloudConnector.input.MomentaryButtonPin
import com.comcast.androidthings.cloudConnector.output.ServoOutputPin
import kotlinx.android.synthetic.main.activity_main.*
import java.io.IOException
import com.google.android.things.update.UpdatePolicy
import com.google.android.things.update.UpdateManager
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {

    // NJD TODO
    // For now, assume a single 'servo' device.. but the cloud function can support multiple device
    // labels... more code would need to be changed here to support multiple devices.
    private val DEVICE_LABEL = "servo"

    // JP8-PIN33 on Pico iMX7d
    private var servoOutputPin: ServoOutputPin = ServoOutputPin("PWM2")

    // JP8-PINXX on Pico iMX7d
    private var servoButtonPin: MomentaryButtonPin = MomentaryButtonPin("GPIO6_IO14")

    // JP8-PIN13 on Pico iMX7d
    //private var solenoidPin: BinaryOutputPin = BinaryOutputPin("GPIO2_IO03")

    // AndroidArchitecture hook between Firebase and LiveData...
    // This allows us to retrieve state changes from the cloud
    private var homeInformationLiveData: CloudInformationLiveData? = null

    // For pushing state changes back up to the cloud
    private var cloudInformationUpdater: CloudInformationUpdater? = null

    private val manager = UpdateManager.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupUpdateManager()

        setupViews()
    }

    private fun setupUpdateManager() {
        Log.e(TAG, "Current UpdateManager status ${manager.status.currentVersionInfo}")
        val policy = UpdatePolicy.Builder()
                .setPolicy(UpdatePolicy.POLICY_APPLY_AND_REBOOT)
                .setApplyDeadline(30L, TimeUnit.MINUTES)
                .build()

        manager.setPolicy(policy)
    }

    override fun onStart() {
        super.onStart()

        // Signs in to Firebase and listens for changes from the cloud
        setupFirebase()

        // Listen for updates from the local UI
        enableButtonViewListener()

        setupLocalPins()
    }

    override fun onStop() {
        super.onStop()

        teardownLocalDevices()
        disableButtonListener()
        teardownFirebase()
    }

    private fun setupLocalPins() {
        // output
        servoOutputPin.open()

        // input
        servoButtonPin
                .open()
                .registerCallback(GpioCallback { gpio ->
                    // We start the local change by pushing it to the cloud first ...
                    // detection of this cloud change locally is what we use to make
                    // changes to our local hardware (e.g. led) ...
                    //
                    // In this way, our remote representation is never out of synch
                    cloudInformationUpdater?.actuateServo(gpio.value)
                    true
                })
    }

    private fun teardownLocalDevices() {
        //solenoidPin.close()
        servoOutputPin.close()
        servoButtonPin.close()
    }

    private fun setupViews() {
        buttonUISwitch.apply { text = "Toggle State" }
        titleTextView.setText("Current CloudButton Build is ${manager.status.currentVersionInfo.buildId}")
    }

    private fun enableButtonViewListener() {
        buttonUISwitch.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener {
            override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
                try {
                    // We start the local change by pushing it to the cloud first ...
                    // detection of this cloud change locally is what we use to make
                    // changes to our local hardware (e.g. led) ...
                    //
                    // In this way, our remote representation is never out of synch
                    cloudInformationUpdater?.actuateServo(isChecked)

                } catch (e: IOException) {
                    Log.e(TAG, "error updating home information in cloud.", e)

                    buttonView.setOnCheckedChangeListener(null)
                    buttonView.isChecked = !isChecked
                    buttonView.setOnCheckedChangeListener(this)
                }
            }
        })
    }

    private fun disableButtonListener() {
        buttonUISwitch.setOnCheckedChangeListener(null)
    }

    private fun setupFirebase() {
        FirebaseAuth.getInstance()
            .signInAnonymously()
            .addOnSuccessListener { cloudInformationUpdater = CloudInformationUpdater(listenForFirebaseChanges()) }
            .addOnFailureListener { Log.e(TAG, "error connecting to firebase:", it) }
    }

    private fun listenForFirebaseChanges(): DatabaseReference {
        val reference = FirebaseDatabase.getInstance().reference.child(HOME_INFORMATION_ROOT)
        homeInformationLiveData = CloudInformationLiveData(reference)
        homeInformationLiveData?.observe(this, homeDataObserver)

        return reference
    }

    // We monitor state changes from server to toggle local hardware
    private val homeDataObserver = Observer<CloudInformation> {
        it?.let {

            // NJD TODO - this needs to be tied into a new value in CloudInformation, but for
            // now both hardware output can be tied to same input
            //solenoidPin.setState(it.servo)

            servoOutputPin.setPosition(if (it.servo)
                                            ServoOutputPin.SERVO_POSITION_IN_DEGREES.ZERO
                                       else
                                            ServoOutputPin.SERVO_POSITION_IN_DEGREES.ONE_SEVENTY
                                      )

            disableButtonListener()
            buttonUISwitch.isChecked = it.servo
            enableButtonViewListener()
        }
    }

    private fun teardownFirebase() {
        homeInformationLiveData?.removeObserver(homeDataObserver)
    }

    companion object {
        private val TAG = MainActivity::class.java!!.getSimpleName()
    }
}
