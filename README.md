# CloudConnectorForAndroidThings
## an AndroidThings Playground

[AndroidThings](https://developer.android.com/things/) is a fairly new Internet of Things (IoT) rapid-prototyping 
platform developed by Google.
[Firebase](https://firebase.google.com/?gclid=CjwKCAjwyrvaBRACEiwAcyuzRIwMz2KvY1kx5J916mFPfZKyebCI7y7FPa8lddtm36FvgI3HH5y9ERoCIokQAvD_BwE) 
is a Cloud Platform by Google providing, among other things, cloud persistence and functions.

Combining these two technologies is a great way to rapidly prototype new ideas. 


The motivation for this project is to provide a simple but solid platform to start experimenting with AndroidThings,
 Firebase Database and Cloud Functions.

Please note that AndroidThings, Firebase Database and Firebase Functions are free when used for 
experimentation as described on the 
[Firebase Pricing Guide](https://firebase.google.com/pricing/).  This project will describe how to 
create such a test account within the Firebase Cloud.

The 'use case' for this project is to have an AndroidThings platform (e.g. PICO PI) connected up to 
a PWM-controlled Servo.  This servo can be used to actuate a real world object (e.g. unlock a door).  


The FirebaseDatabase and Firebase Function code included in this project provide a webhook so one can 
easily actuate this Servo from any web client.


## Credits


Many thanks to James Coggan and his AndroidThings Workshop (https://github.com/jamescoggan/AndroidThingsWorkshop).  
He's a great inspiration and this project borrows from his work!

Also, thanks to Unsplash.com for their Philly 'Love' photo.

## Let's get started!

**Step 1:  Hardware Setup**


The idea behind AndroidThings is you start with a core Android system hardware module which provides the Android runtime.  
That plugs into a prototype board which supports many IO standards (e.g. SPI, GPIO, etc.) and has many headers for making quick connections.  
You build your prototype on this board and then eventually you get a more precise board fabricated.  This new, smaller board can use the 
same core Android module.  There are a number of purchase options for this 'prototype board'.

I purchased the ['Pico Pi' AndroidThings starter kit from TechNexion](https://shop.technexion.com/pico-pi-imx7-startkit-rainbow-hat.html). 
This is based on the NXP i.MX7 processor, but that doesn't really matter as you're going to be pushing stock 
Android code anyway.  This board comes with a nice 5" color touchscreen, build-in WiFi w/ antenna, and a camera.

This board comes in a really nice shipping carton, which, with a little duct tape, can be converted to a holder for 
your new board, display, and camera:
 
![AndroidThingsInShippingBox](https://github.com/ndipatri/FirebaseWithAndroidThings/blob/master/media/androidThingsInShippingBox.jpg "AndroidThings in it's shipping box")

This project uses this board to actuate a Servo.  A Servo is a DC motor with a gear on the end that's 
been geared-down so it has some torque to move things.  
The Servo can be instructed to 'rotate to the X degree position' over an analog interface called PWM (Pulse Width Modulation) 
on the AndroidThings platform   

As shown in the picture above, typically, you attach an arm to the servo as a means of moving something. 

I bought the [TowerPro SG92R Servo](http://www.towerpro.com.tw/product/sg92r-7/).  I recommend using the same Servo 
as the code in this project is tuned for the control signals expected by this Servo.

The manual that comes with AndroidThings will help you assemble the board and attach all the parts.  
Follow these steps to configure your board:

1. [Load the board with the latest firmware](https://developer.android.com/things/hardware/imx7d.html)

2. [Configure the board to connect to WiFi](https://developer.android.com/things/hardware/wifi-adb.html)


After you've assembled and configure the Pico PI AndroidThings board, you can wire up the Servo to the 40-pin 
expansion header near the top of the board.  [Here are the pinouts](https://github.com/ndipatri/FirebaseWithAndroidThings/blob/master/media/pi3_and_mxp7_pinout.png) 
for this expansion header.

Wiring up a Servo requires three connections:

**Brown Wire (Servo)** - connects to PIN 6 (Ground) on the Pico Pi

**Red Wire (Servo)** - connects to PIN 1 (3.3V) on the Pico Pi

**Yellow Wire (Servo)** - connects to PIN 33 (PWM2) on the Pico Pi

The 'PWM2' PIN on the Pico Pi is a 'Pulse Width Modulation' analog output pin.  It's the control 
signal that is sent to the servo and is controlled by the code in this project.


**Step 2: Firebase Setup**

In addition to controlling the locally connected Servo, this AndroidThings project also synchronizes 
the 'on/off' state of the Servo with an instance of [Firebase Realtime Database](https://firebase.google.com/docs/database/?gclid=CjwKCAjwhLHaBRAGEiwAHCgG3ok1YgazBaUdc0II7bOTkSKtupTszjnhDF-tvjb3HSc9VEkj51NjvhoCcFcQAvD_BwE).  
This database stores the current 'on/off' state of the Servo.
 

Inside the 'firebaseFunction' directory of this project is a [Firebase Function](https://firebase.google.com/products/functions/?gclid=CjwKCAjwhLHaBRAGEiwAHCgG3tVvRmlWfTwDDLAFtKaV1L4-73e40EPyvS0MUPg43FbSX7wB_3VeLRoCuo0QAvD_BwE) 
which, when deployed to the web, can change the values stored in the Firebase Realtime Database 
from any web browser. By calling this Firebase Function 'web hook', you can remotely affect a change in the Servo state. 


**Step 2.1 Create Firebase Project**

You will be creating a Firebase Project strictly for experimentation here which is free of charge.  
To start you will need a Google Account. Navigate to the [Firebase Console](https://console.firebase.google.com/u/0/?pli=1) 
and click on 'Add Project'. Once you create this project, you need the 'Project Id'.  This can be found 
by going into 'settings' on the Firebase Console.

**Step 2.2 Configure Firebase Project for Anonymous 'sign-in'**

Navigate to the [Firebase Console](https://console.firebase.google.com/u/0/?pli=1) again and click on 
'Authentication' tab, then click on 'Sign-in'method.  Enabled the 'Anonymous' method.

Next you need to setup this project to use a Firebase Database.  

**Step 2.3 Create Firebase Realtime Database**

Enter your project in the Firebase Console and click on 'Develop' then 'Database'.  Scroll down to 
'Realtime Database' and click 'create database' and select 'start in test mode' and click 'enable'.  
Please be aware that this is creating a completely 'open' database, which is acceptable for this 
simple experiment. Anyone with your web-hook can change the state of your Servo.  

The 'schema' of this database is configured by this project itself so your database is all ready to 
be used!  The next step is to setup the Firebase Function.


**Step 2.4 Configure AndroidThings project to talk to your new Firebase Project in the cloud.**

There's a lot of 'magic' that happens when deploying Firebase Functions and the Android Studio helps 
to configure this magic.  Launch AndroidStudio and login to your Google Account using the 'User' icon 
in the upper right corner of the IDE.  Once you are logged into your Google Account, Android Studio 
will be able to find your new Firebae Project.  To complete this step, navigate to 'Tools-->Firebase', 
then scroll down to 'Realtime Database' and select 'save and retrieve data'.  This will launch a dialog 
and from here you can select your new Firebase Project.  You will notice that this adds the 
'app/google-services.json' file to this project.  Now you are ready to configure your project for 
Firebase Functions.

**Step 2.5 Configure and Deploy Firebase Function**

In general, you write, compile and test a Firebase Function using a local Node.js instance.  Once 
the function is completed, you can deploy it to your Firebase Project created above.  This project 
already includes the Firebase Function necessary to control our Servo in the 
**'firebaseFunction/functions/index.js'** project file.  We need to configure this project to talk 
to your Firebase Project in order to execute Firebase Functions.

Do all of the following from the main project directory:

First install [HomeBrew](https://brew.sh/) and Node.js.

```
>brew upgrade
>brew install node
>brew install npm
```

Then you need to configure your local **'firebaseFunction/** directory to be able to deploy 
Firebase Functions: 

```
> cd firebaseFunction
> npm install -g firebase-tools
> firebase login (this will launch a browser and allow you to login to your Google account)
> firebase init (this may redirect you to a web login for your Google Account, which you should do)
```
Use arrow keys to scroll down to 'Functions'.  Press 'space' to select and 'enter' to finish.  
Choose the Firebase Project you created earlier to associate with this function. Choose 'Javascript'.  
Chose to NOT overwrite the 'index.js' file.  This is our existing Firebase Function that will talk to 
our Android Things!  Choose YES to install NPM dependencies.

Before we can deploy our function, we need to modify it slightly.  Open the 'index.js' file and 
change the **databaseURL** so that it includes YOUR Firebase ProjectId.  You can get this by navigating 
to your project in the Firebase Console (https://console.firebase.google.com/), then navigate to 
the 'Database' tab:

```
firebase.initializeApp({
    databaseURL: 'https://<yourProjectIdHere>.firebaseio.com'
});
```

Now you are ready to deploy this Firebase Function to your Firebase Cloud project:

```
>firebase deploy --only functions
```

This will compile and upload your Firebase Function and will give you a **Function URL** which is how 
you can call this function from any browser to actuate your AndroidThings Servo!

**Step 3: Deploy to AndroidThings**

Primarily, this is an AndroidThings project.  You load this project into AndroidStudio, connect your 
AndroidThings device to the computer, and push the build as you would push a build to an Android phone.  
Once deployed, this project will present a toggle switch on the LCD display which can be used to set 
the Servo to one of two positions (currently ZERO and ONE_SEVENTY degrees).


**Step 4: Use webhook to control Servo**

Below we combine the Firebase URL with the required parameter.  A **testHardware** value of 'true' 
will set the Servo to ONE_SEVENTY degrees and a value of 'false' will set the Servo to ZERO degrees.

```
Example Function URL with required Servo parameter:

https://<someHostnameWhichContainsYourProjectId>.cloudfunctions.net/testHardware?actuateServo=false
```

You should go ahead, and test the above URL now to make sure it responds with OK.

**NOTE**:  It's possible the particular servo you have won't work with this code.  If this happens, 
this is probably due to the 'DutyCyclePercentage' value used in ServoOutputPin.kt.  You can 
recognize this failure if the servo is just making a buzzing sound instead rotating.  You can
change the 'DutyCyclePercentage' using a breakpoint and experiment with values for ZERO and
ONE_SEVENTY.






 




 

