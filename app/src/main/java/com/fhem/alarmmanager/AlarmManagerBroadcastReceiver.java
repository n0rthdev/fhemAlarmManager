package com.fhem.alarmmanager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class AlarmManagerBroadcastReceiver extends BroadcastReceiver {

    final public static String ONE_TIME = "onetime";

    int counter = 0;
    private AlarmManagerActivity alarmManagerActivity;

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YOUR TAG");
        //Acquire the lock
        wl.acquire();

        sendAlarmUpdate(context, false);

        //Release the lock
        wl.release();

    }

    public String sendAlarmUpdate(Context context, boolean toast) {
        try {
            String nextAlarm = Settings.System.getString(context.getContentResolver(), Settings.System.NEXT_ALARM_FORMATTED);

            String[] spl = nextAlarm.split(" ");
            int day = -1, hour = -1, min = -1;
            boolean done = false;
            if (spl.length == 3) {
                if (spl[0].equals("Mon")) {
                    day = 0;
                } else if (spl[0].equals("Tue")) {
                    day = 1;
                } else if (spl[0].equals("Wed")) {
                    day = 2;
                } else if (spl[0].equals("Thu")) {
                    day = 3;
                } else if (spl[0].equals("Fri")) {
                    day = 4;
                } else if (spl[0].equals("Sat")) {
                    day = 5;
                } else if (spl[0].equals("Sun")) {
                    day = 6;
                }
                String[] spt = spl[1].split(":");
                hour = Integer.parseInt(spt[0]);
                min = Integer.parseInt(spt[1]);
                if (spl[2] == "PM" && hour != 12)
                    hour += 12;
                else if (spl[2] == "AM" && hour == 12)
                    hour = 0;
                done = true;
            }
            spl = nextAlarm.split(",");
            if (!done && spl.length == 2) {
                spl[0] = spl[0].substring(0, 2);
                spl[1] = spl[1].substring(1);
                if (spl[0].equals("Mo")) {
                    day = 0;
                } else if (spl[0].equals("Di")) {
                    day = 1;
                } else if (spl[0].equals("Mi")) {
                    day = 2;
                } else if (spl[0].equals("Do")) {
                    day = 3;
                } else if (spl[0].equals("Fr")) {
                    day = 4;
                } else if (spl[0].equals("Sa")) {
                    day = 5;
                } else if (spl[0].equals("So")) {
                    day = 6;
                }
                String[] spt = spl[1].split(":");
                hour = Integer.parseInt(spt[0]);
                min = Integer.parseInt(spt[1]);
                done = true;
            }

            int day1hbefore = (hour == 0) ? (day + 6) % 7 : day;
            int hour1hbefore = (hour == 0) ? 23 : hour - 1;

            int perlday1hbefore = (day1hbefore + 1) % 7;

            String timestr = String.format("%02d", hour1hbefore) + ":" + String.format("%02d", min);

            String corr = (perlday1hbefore < 0 || hour1hbefore < 0) ? "None" : (perlday1hbefore + "/" + timestr);

            if (toast) {
                Toast.makeText(context, "Your Alarm: " + nextAlarm + ", Fhem Trigger: " + corr, Toast.LENGTH_LONG).show();
            }

            counter++;

            runFhemCmd("set AlarmDayAndroid " + perlday1hbefore);
            runFhemCmd("set AlarmtimeAndroid " + timestr);

            return corr;

        } catch (Exception e) {
            e.printStackTrace();
            return "ERROR";
        }
    }

    private class FhemRequest extends AsyncTask<String, Integer, Long> {
        protected Long doInBackground(String... s) {

            try {
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                HttpClient client = new DefaultHttpClient();
                String getURL = null;

                String padcmd = s[0].replace(" ", "%20");
                getURL = alarmManagerActivity.FHEM_COMMAND_URL + padcmd;
                HttpGet get = new HttpGet(getURL);
                HttpResponse responseGet = client.execute(get);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return new Long(0);
        }

        protected void onProgressUpdate(Integer... progress) {

        }

        protected void onPostExecute(Long result) {
        }
    }

    public void runFhemCmd(String cmd) {
        new FhemRequest().execute(cmd);

    }

    public void startAlarmAutoUpdate(Context context, AlarmManagerActivity alarmManagerActivity) {
        this.alarmManagerActivity = alarmManagerActivity;
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.FALSE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 15, pi);
    }

    public void cancelAlarmAutoUpdate(Context context) {
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }

    public void setOnetimeTimer(Context context) {
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmManagerBroadcastReceiver.class);
        intent.putExtra(ONE_TIME, Boolean.TRUE);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, intent, 0);
        am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pi);
    }
}
