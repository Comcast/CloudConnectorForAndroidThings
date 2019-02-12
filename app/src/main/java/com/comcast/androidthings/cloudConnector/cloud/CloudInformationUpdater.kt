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

import com.google.firebase.database.DatabaseReference

// Helper class to update our specific data structure in
// the Firebase database.. The root database node is assumed to be
// 'home' here.
class CloudInformationUpdater(private val reference: DatabaseReference) {

    fun actuateServo(actuate: Boolean) {
        reference.child("servo").setValue(actuate)
    }
}