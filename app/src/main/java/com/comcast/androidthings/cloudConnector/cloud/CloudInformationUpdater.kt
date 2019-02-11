package com.comcast.androidthings.cloudConnector.cloud

import com.google.firebase.database.DatabaseReference

// Helper class to update our specific data structure in
// the Firebase database.. The root database node is assumed to be
// 'home' here.
class CloudInformationUpdater(private val reference: DatabaseReference) {

    fun actuateServo(actuate: Boolean) {
        reference.child("servo").setValue(actuate)
    }
}