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
const functions = require('firebase-functions');
var firebase = require('firebase-admin');

firebase.initializeApp({
    databaseURL: 'https://cloudbuttonathome.firebaseio.com'
});

exports.testHardware = functions.https.onRequest((request, response) => {

    const deviceLabel = request.query.deviceLabel;
    const actuate = request.query.actuate;

    if (deviceLabel !== undefined && actuate !== undefined) {
        firebase.database().ref('/home/' + deviceLabel).set(actuate === 'true')
            .then( function() {
                return response.send("CloudButtonUpdateRequest success: (deviceLabel:" + deviceLabel + ", isActuated:)" + actuate + ")");
            })
            .catch(function(error) {
                return response.send("CloudButtonUpdateRequest failed: " + error);
            });
    } else if (deviceLabel !== undefined) {
        return firebase.database().ref('/home/' + deviceLabel).once('value').then((snapshot) => {
            return response.send("CloudButtonState: (deviceLabel:" + deviceLabel + ", isActuated:" + snapshot.val() + ")");
        });
    } else {
        return response.send("Parameter 'deviceLabel' is required.");
    }
});
