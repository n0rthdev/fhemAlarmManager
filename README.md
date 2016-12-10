# fhemAlarmManager
This is a very small (and dirty) application that is able to read the next alarm time of android and post it to your FHEM server to trigger an event.
For this implementation the FHEM server runs the on IP 192.168.1.3.
The trigger time sent to FHEM is exactly one hour ahead of your alarm.
I also integrated some remote controll options for my setup.

The app uses the text displayed in the status-bar or start screen,
so it is able to also read the alarms of third party apps like the Timely Alarm Clock App.

[Demo Video on YouTube](https://www.youtube.com/watch?v=SzFoZILu9mY)

[Timely Alarm Clock - Goolge Play](https://play.google.com/store/apps/details?id=ch.bitspin.timely)

![Screenshot of the apps main screen](https://github.com/napster2202/fhemAlarmManager/raw/master/emulatorshot.PNG "Screenshot of the apps main screen")

## FHEM Config parts

Variables for saving the state, and code for sunrise simulation
```perl
define AlarmtimeAndroid dummy
attr AlarmtimeAndroid setList state:time
attr AlarmtimeAndroid webCmd state

define AlarmDayAndroid dummy
attr AlarmDayAndroid setList state:0,1,2,3,4,5,6
attr AlarmDayAndroid webCmd state

# AlarmAndroid
define AlarmAndroid at *{ReadingsVal("AlarmtimeAndroid","state","05:30:00")} {\
    if ($wday eq Value("AlarmDayAndroid")) {\
     {fhem("set Wake on")}\
    }\
}


define AlarmChange notify (AlarmtimeAndroid|global:INITIALIZED|global:REREADCFG).* \
   modify AlarmAndroid *{ReadingsVal("AlarmtimeAndroid","state","05:30:00")}

define Wake dummy
attr Wake setList state:on,off
attr Wake webCmd state

define Wake.ntfy notify Wake.* {\
 if ("$EVENT" ne "off") {\
{fhem("define on0At at +00:35:00 { if (Value(\"Wake\") eq \"on\") {{fhem(\"set LED on\")}{fhem(\"set LED RGB 000021\")}}{fhem(\"delete on0At\")}}")}\
{fhem("define on7At at +00:40:00 { if (Value(\"Wake\") eq \"on\") {{fhem(\"set FAN on\")}}{fhem(\"delete on7At\")}}")}\
{fhem("define on1At at +00:40:00 { if (Value(\"Wake\") eq \"on\") {{fhem(\"set LED RGB 661700\")}}{fhem(\"delete on1At\")}}")}\
{fhem("define on2At at +00:43:00 { if (Value(\"Wake\") eq \"on\") {{fhem(\"set LED RGB FF3C00\")}}{fhem(\"delete on2At\")}}")}\
{fhem("define on6At at +00:45:00 { if (Value(\"Wake\") eq \"on\") {{fhem(\"set WOL_X301_2 on\")}}{fhem(\"delete on6At\")}}")}\
{fhem("define on3At at +00:49:00 { if (Value(\"Wake\") eq \"on\") {{fhem(\"set F_Hal on\")}}{fhem(\"delete on3At\")}}")}\
{fhem("define on4At at +00:53:00 { if (Value(\"Wake\") eq \"on\") {{fhem(\"set UFO on\")}}{fhem(\"delete on4At\")}}")}\
{fhem("define on5At at +00:55:00 { if (Value(\"Wake\") eq \"on\") {{fhem(\"set F_Led on\")}}{fhem(\"delete on5At\")}}")}\
{fhem("define offWake at +01:35:00 { if (Value(\"Wake\") eq \"on\") {{fhem(\"set LED off\")}{fhem(\"set F_Hal off\")}{fhem(\"set UFO off\")}{fhem(\"set F_Led off\")}{fhem(\"set FAN off\")}{fhem(\"set Wake off\")}}{fhem(\"delete offWake\")}}")}\
 }\
 else {\
 #{fhem(\"set LED off\")}\
 #{fhem(\"set F_Hal off\")}\
 #{fhem(\"set UFO off\")}\
 #{fhem(\"set F_Led off\")}\
 #{fhem(\"set Wake off\")}\
 #{fhem(\"set FAN off\")}\
 }\
}
```


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
