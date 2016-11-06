# fhemAlarmManager
This is a very small (and dirty) application that is able to read the next alarm time of android and post it to your FHEM server to trigger an event.
For this implementation the FHEM server runs the on IP 192.168.1.2.
The trigger time sent to FHEM is exactly one hour ahead of your alarm.
I also integrated some remote controll options for my setup.

The app uses the text displayed in the status-bar or start screen,
so it is able to also read the alarms of third party apps like the Timely Alarm Clock App.

[Demo Video on YouTube](https://www.youtube.com/watch?v=SzFoZILu9mY)

[Timely Alarm Clock - Goolge Play](https://play.google.com/store/apps/details?id=ch.bitspin.timely)

## FHEM Setup

FHEM is installed on a RaspberryPi.

The Android application was mainly developed to get the alarm time of the Android phone that I use as alarm clock. Timely is my preffered alarm clock, but the application reads the system variable of the text android displays (lockscreen, etc.) for the next alarm.
Another advantage is that it is more handy to use than the web-interface in a mobile browser. Also it enables me to trigger light settings by scanning NFC-tags.

The [Power-Sockets](http://amzn.to/2dC6brg) are connected via USB to the RaspberryPi. 

[WIFI-RGB-Controller #1](http://amzn.to/2dDWusQ) (3 devices)

[WIFI-RGB-Controller #2](http://amzn.to/2e24NS3) (2 devices)

The old models of #1 works perfect even better than #2. With controllers of the current version I had troubles connecting them to my Wifi-Network. All these models look similar and have the same name, although they have completely different firmware.

1. setting custom IP and changing the admin password

2. can only use IP from DHCP, which makes it harder to use with FHEM.

I set up a DHCP server and configured it so that the MAC-Addresses of the RGB-Wifi Controllers always get a specified IP. The DHCP server is installed on the same RaspberryPi as FHEM.

FHEM: http://www.fhemwiki.de/wiki/WifiLight

FHEM- WifiLight-Module: http://www.fhemwiki.de/wiki/WifiLight
