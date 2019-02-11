package com.comcast.androidthings.cloudConnector.cloud


import android.arch.lifecycle.LiveData
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener

class CloudInformationLiveData(private val databaseReference: DatabaseReference) : LiveData<CloudInformation>() {

    private val valueEventListener = object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            val newValue = snapshot.getValue(CloudInformation::class.java)
            Log.d("onDataChange()", "New data received! $newValue")
            value = newValue
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("onCanceled", "onCancelled", error.toException())
        }
    }

    override fun onActive() {
        databaseReference.addValueEventListener(valueEventListener)
    }

    override fun onInactive() {
        databaseReference.removeEventListener(valueEventListener)
    }
}
