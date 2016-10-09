## About ##
This is my submission for MHACKS 8. I built a simple VR shooter with motion tracking. I am not even close to being able to afford a real VR setup, so I wanted to experience a tiny bit of the new VR tech.

Here is the game:
![phone](https://raw.githubusercontent.com/jtjj222/mhacks-8-space-shooter/master/pictures/phone.png)

Here is the desktop portion:
![desktop](https://raw.githubusercontent.com/jtjj222/mhacks-8-space-shooter/master/pictures/desktop.png)

When you move outside of the field of view of the head tracker, the game is paused and you are shown the camera feed to re-orient yourself:
![camera](https://raw.githubusercontent.com/jtjj222/mhacks-8-space-shooter/master/pictures/camera.png)

## How to Play##
Download the source code and open the gradle project in android studio. Tether to your phone using bluetooth or wifi, and make sure that you can ping/contact your phone. I used a static ip on a different subnet mask from the wifi so that I could keep my wifi connection on my laptop, as well as a connection to my phone, as shown in the picture above.

Print out one of the trackers in trackers.pdf, and place it on your cardboard.

Deploy the android project, and then start the desktop-launcher project. The phone will vibrate and print out a log message in adb when it connects to the laptop. Put on the cardboard, and face the webcam.

Move your head without rotating to control the position of your ship, and press the fire button on the cardboard to shoot. You can look around, but if you rotate too far, it will lose the track. If the track is lost, it will pause the game and show you the camera feed so you can re-orient yourself.


## Game Objects ##

You can shoot enemies (the red circle things) and meteors. If you get hit, the cardboard will vibrate. You must dodge the enemy fire (the red crosses), and the electric fields.

The green pills give you a life only when you collide with them. If you shoot them, they disappear, so be sure not to just spam the trigger. When you collect a pill or press fire, the phone will vibrate quicker than when you get hit.

Your points are shown below, and lives above. When you get game-over, you must restart the application.

## How it works ##
The desktop application accepts a tcp connection from the phone, and sends the position of your head. It uses boofcv to track you. When the outline is red, it is using the fiducial tracker (boofcv's name for the black and white tracker). When it is green, it has lost the track and is looking at the texture to guess where you are until it finds the fiducial again. If it does not find a fiducial within 1 second, it pauses the game and shows you the camera.

On the phone, I used Libgdx to handle the graphics. While I still use mostly opengl calls, it handles things like loading textures. I decided that I wanted to use a lower-level library so I could learn more about opengl.

I used an ancient version of the Cardboard library, before google hid the source code, because I want to be able to learn from it.

## Challenges and Things I Learned ##
- Getting libgdx and the cardboard libraries to work with each other and with gradle was a pain. I had to modify both of the open-source projects that I used to get it to work.
- Getting the head tracking to work when you move quickly was a bit tricky. I outlined my approach above.
- I am not good at game design, and even though I've worked with cardboard and libgdx before, this is by far the most complete game I've ever built.
- The code is a mess. Sorry.
- I learned so much about opengl (especially why my alpha blending wasn't working) and android surface textures (to view the camera stream)
- I tried doing the motion tracking on the phone with boofcv, but it was far too slow.

## What's next? ##
- I want to incorporate the head tracking with an old game like DOOM
- I originally wanted to make a wireless controller with the ESP8266 that would shock you when you got hit. I ordered a joke buzzer on Amazon, but it didn't come in on time :(

## Attribution ##
- Art used from http://opengameart.org/content/space-shooter-redux
- Libgdx, a game library handling things like textures, model loading and shader compiling. 
- https://github.com/rsanchezsaez/cardboard-java/ 
- https://github.com/yangweigbh/Libgdx-CardBoard-Extension
- A big thanks to Google for giving me a Google Cardboard v2! You totally made my month!

Thanks to the organizers and sponsors of MHACKS 8!

Regards,
Justin

