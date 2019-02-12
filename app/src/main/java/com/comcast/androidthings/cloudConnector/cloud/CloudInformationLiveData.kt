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
