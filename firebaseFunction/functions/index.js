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
