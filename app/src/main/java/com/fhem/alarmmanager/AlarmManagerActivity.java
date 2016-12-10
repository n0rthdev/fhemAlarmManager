package com.fhem.alarmmanager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.chiralcode.colorpicker.ColorPickerDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AlarmManagerActivity extends Activity {

    public static final String FHEM_COMMAND_URL = "http://192.168.1.3:8083/fhem?cmd=";

    final static String[] device_tags = new String[]{"Bed", "Desk", "Window", "Left", "Right", "Doors", "All"};
    final static String[] devices = new String[]{"Bed", "Desk", "Window", "LockerLeft", "LockerRight", "LockerDoors", "LED"};

    final static String[] on_off_device_tags = new String[]{"Led", "Hal", "Ufo", "Fan", "Sun"};
    final static String[] on_off_devices = new String[]{"F_Led", "F_Hal", "UFO", "FAN", "SUN"};

    final static String[] on_device_tags = new String[]{"WolServer", "WolXeon", "NightAll", "Night", "RedBlue", "GreenBlue", "RedBluePink"};
    final static String[] on_devices = new String[]{"WOL_Server", "WOL_Xeon", "NIGHT_ALL", "NIGHT", "RED_BLUE", "GREEN_BLUE", "RED_BLUE_PINK"};

    final static String colors0 = "FF0000";
    final static String colors1 = "00FF00";
    final static String colors2 = "0000FF";
    final static String colors3 = "ff3c00";
    final static String colors4 = "ff6f21";
    //final static String colors5 = "00FFFF";

    final static String comOn_tag = "On";
    final static String comOff_tag = "Off";

    final static String comOn = "on";
    final static String comOff = "off";

    final static String[] value_tags = new String[]{comOn_tag, "0", "1", "2", "3", "4", "5", comOff_tag};
    final static String[] rgb_values = new String[]{comOn, "RGB " + colors0, "RGB " + colors1, "RGB " + colors2, "RGB " + colors3, "RGB " + colors4, "RGB ", comOff};

    final static String[] on_off_value_tags = new String[]{comOn_tag, comOff_tag};
    final static String[] on_off_values = new String[]{comOn, comOff};

    private AlarmManagerBroadcastReceiver alarm;

    private Map<String, String> commands = new HashMap<String, String>();
    private Map<Button, Integer> lastColor = new HashMap<Button, Integer>();
    private ArrayList<Button> colBtns = new ArrayList<Button>();

    int[] colBtnsIds = new int[]{
            R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6,
            R.id.button8, R.id.button9, R.id.button10, R.id.button11, R.id.button12, R.id.button13,
            R.id.button16, R.id.button17, R.id.button18, R.id.button19, R.id.button20, R.id.button21,
            R.id.button24, R.id.button25, R.id.button26, R.id.button27, R.id.button28, R.id.button29,
            R.id.button32, R.id.button33, R.id.button34, R.id.button35, R.id.button36, R.id.button37,
            R.id.button40, R.id.button41, R.id.button42, R.id.button43, R.id.button44, R.id.button45,
            R.id.button48, R.id.button49, R.id.button50, R.id.button51, R.id.button52, R.id.button53,
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_manager);
        alarm = new AlarmManagerBroadcastReceiver();

        commands.clear();
        for (int i = 0; i < devices.length; i++) {
            for (int j = 0; j < rgb_values.length; j++) {
                commands.put(device_tags[i] + value_tags[j], devices[i] + " " + rgb_values[j]);
            }
        }

        for (int i = 0; i < on_off_device_tags.length; i++) {
            for (int j = 0; j < on_off_value_tags.length; j++) {
                commands.put(on_off_device_tags[i] + on_off_value_tags[j], on_off_devices[i] + " " + on_off_values[j]);
            }
        }

        for (int i = 0; i < on_device_tags.length; i++) {
            commands.put(on_device_tags[i], on_devices[i] + " " + comOn);
        }
        colBtns.clear();

        for (int p : colBtnsIds) {
            Button btn = (Button) findViewById(p);
            String cmd = commands.get(btn.getTag());

            String[] spl = cmd.split(" ");
            if (spl.length == 3) {
                int col = Color.parseColor("#" + spl[2]);

                btn.getBackground().setColorFilter(col, PorterDuff.Mode.SRC_ATOP);//.setcolor .setBackgroundColor(col);
                colBtns.add(btn);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent i = getIntent();
        if (i != null) {
            Uri data = i.getData();
            if (data != null) {
                String cmd = data.getQueryParameter("cmd");
                if (cmd != null)
                    alarm.runFhemCmd(cmd);
            }
        }
    }

    public void startRepeatingTimer(View view) {
        Context context = this.getApplicationContext();
        if (alarm != null) {
            alarm.startAlarmAutoUpdate(context, this);
        } else {
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }

    public void cancelRepeatingTimer(View view) {
        Context context = this.getApplicationContext();
        if (alarm != null) {
            alarm.cancelAlarmAutoUpdate(context);
        } else {
            Toast.makeText(context, "Alarm is null", Toast.LENGTH_SHORT).show();
        }
    }

    public void manualTimerUpdate(View view) {
        String corr = alarm.sendAlarmUpdate(view.getContext(), true);
        Button timebtn = (Button) findViewById(R.id.alarmTimeButton);
        timebtn.setText(corr);
    }

    public void colorPickerClick(View view) {
        final Button b = (Button) view;

        Integer c1 = lastColor.get(b);
        int c2 = (c1 == null) ? Color.BLACK : (0xFFFFFF & c1);

        final String buttonTag = b.getTag().toString();
        final String command = "set " + commands.get(buttonTag);

        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this, c2, new ColorPickerDialog.OnColorSelectedListener() {

            @Override
            public void onColorSelected(int color) {
                String strColor = String.format("%06X", 0xFFFFFF & color);
                alarm.runFhemCmd(command + strColor);
                lastColor.put(b, color);
            }

        });
        colorPickerDialog.show();
    }

    public void webButtonClick(View view) {

        Button b = (Button) view;

        String buttonTag = b.getTag().toString();
        String cmd = commands.get(buttonTag);
        String command = "set " + cmd;

        alarm.runFhemCmd(command);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_widget_alarm_manager, menu);
        return true;
    }
}
