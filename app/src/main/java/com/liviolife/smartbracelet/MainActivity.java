package com.liviolife.smartbracelet;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
/*
Copy and paste the .jar file into app/libs folder of your Android Project.
Then open your gradle.build(Module:app)
and then you can see .jar in dependencies{}
File>Project Structure>App>Dependencies>Add>jar Dependencies>libs --Select both files

Create layouts device list item, pop window and activity main.xml.
Define colors.xml, style.xml and strings.xml

File>New>Folder>Assets Folder and copy JySDK.xml on app->src->main->assets

The targetSdk version should be declared: defaultConfig in the build.gradle file.
Project Structure>App>Flavors>
minSdkVersion="18"
targetSdkVersion="19"

Charts:
https://github.com/PhilJay/MPAndroidChart
*/

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.liviolife.smartbracelet.R;
import com.sxr.sdk.ble.keepfit.aidl.AlarmInfoItem;
import com.sxr.sdk.ble.keepfit.aidl.BleClientOption;
import com.sxr.sdk.ble.keepfit.aidl.DeviceProfile;
import com.sxr.sdk.ble.keepfit.aidl.IRemoteService;
import com.sxr.sdk.ble.keepfit.aidl.IServiceCallback;
import com.sxr.sdk.ble.keepfit.aidl.UserProfile;
import com.sxr.sdk.ble.keepfit.aidl.Weather;
import com.sxr.sdk.ble.keepfit.service.BluetoothLeService;

import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.model.GradientColor;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static java.security.AccessController.getContext;

public class MainActivity extends Activity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent gattServiceIntent = new Intent(this, SampleBleService.class);

        gattServiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startService(gattServiceIntent);
        llConnect = (LinearLayout)findViewById(R.id.llConnect);
        device_info_text = (TextView)findViewById(R.id.device_info);
        data_text = (TextView)findViewById(R.id.data_text);
        oxygen_data = (TextView)findViewById(R.id.oxygen_data);
        fatigue_data = (TextView)findViewById(R.id.fatigue_data);
        bloodpressure_data = (TextView)findViewById(R.id.bloodpressure_data);
        last_sync = (TextView)findViewById(R.id.last_sync);
        currenttotal_steps = (TextView)findViewById(R.id.currenttotal_steps);
        currenttotal_distance = (TextView)findViewById(R.id.currenttotal_distance);
        currenttotal_calories = (TextView)findViewById(R.id.currenttotal_calories);
        currenttotal_sleeping = (TextView)findViewById(R.id.currenttotal_sleeping);
        currenttotal_exercise = (TextView)findViewById(R.id.currenttotal_exercise);
        stepsChart_date = (TextView)findViewById(R.id.stepsChart_date);
        btBind = (Button)findViewById(R.id.bind);
        btUnbind = (Button)findViewById(R.id.unbind);
        btScan = (Button)findViewById(R.id.scan);
        btConnect = (Button)findViewById(R.id.connect);
        btDisconnect = (Button)findViewById(R.id.disconnect);
        btReadFw = (Button)findViewById(R.id.read_fw);
        btSync = (Button)findViewById(R.id.sync);
        btSyncPersonalInfo = (Button)findViewById(R.id.set_alarm);
        set_time = (Button)findViewById(R.id.set_time);
        getcursportdata = (Button)findViewById(R.id.getcursportdata);
        set_parameters = (Button)findViewById(R.id.set_parameters);
        tvSync = (TextView) findViewById(R.id.tvSync);
        bNotify = (Button)findViewById(R.id.bNotify);

        data_heartrate = (TextView) findViewById(R.id.data_heartrate);
        set_userinfo = (Button)findViewById(R.id.set_userinfo);
        set_userinfo.setOnClickListener(this);
        set_vir = (Button)findViewById(R.id.set_vir);
        set_vir.setOnClickListener(this);
        set_photo = (Button)findViewById(R.id.set_photo);
        set_photo.setOnClickListener(this);
        set_idletime = (Button)findViewById(R.id.set_idletime);
        set_idletime.setOnClickListener(this);
        set_sleep = (Button)findViewById(R.id.set_sleep);
        set_sleep.setOnClickListener(this);
        read_battery = (Button)findViewById(R.id.read_battery);
        read_battery.setOnClickListener(this);
        read_fw = (Button)findViewById(R.id.read_fw);
        read_fw.setOnClickListener(this);
        set_alarm = (Button)findViewById(R.id.set_alarm);
        set_alarm.setOnClickListener(this);
        set_autoheart = (Button)findViewById(R.id.set_autoheart);
        set_autoheart.setOnClickListener(this);
        set_fuzhu = (Button)findViewById(R.id.set_fuzhu);
        set_fuzhu.setOnClickListener(this);
        set_showmode = (Button)findViewById(R.id.set_showmode);
        set_showmode.setOnClickListener(this);
        openheart = (Button)findViewById(R.id.openheart);
        openheart.setOnClickListener(this);
        closeheart = (Button)findViewById(R.id.closeheart);
        closeheart.setOnClickListener(this);
        getStepsData = (Button)findViewById(R.id.getStepsData);
        getStepsData.setOnClickListener(this);
        setLanguage = (Button)findViewById(R.id.setLanguage);
        setLanguage.setOnClickListener(this);
        send_weather = (Button)findViewById(R.id.send_weather);
        send_weather.setOnClickListener(this);
        bt_getmutipleSportData = (Button)findViewById(R.id.bt_getmutipleSportData);
        bt_getmutipleSportData.setOnClickListener(this);
        bt_open_blood = (Button)findViewById(R.id.bt_open_blood);
        bt_open_blood.setOnClickListener(this);
        bt_close_blood = (Button)findViewById(R.id.bt_close_blood);
        bt_close_blood.setOnClickListener(this);
        bt_setgoalstep = (Button)findViewById(R.id.bt_setgoalstep);
        bt_setgoalstep.setOnClickListener(this);
        bt_setHeartRateArea = (Button)findViewById(R.id.bt_setHeartRateArea);
        bt_setHeartRateArea.setOnClickListener(this);

        previous_daySteps = (ImageButton)findViewById(R.id.previous_daySteps);
        previous_daySteps.setOnClickListener(this);
        next_daySteps = (ImageButton)findViewById(R.id.next_daySteps);
        next_daySteps.setOnClickListener(this);
        Calendar date = Calendar.getInstance();
        stepsChart_date.setText(new SimpleDateFormat("MMMM dd, yyyy EEEE").format(date.getTime()) + " (-" + dayNumberFromToday + ") *Saved locally");

        bNotify.setOnClickListener(this);
        findViewById(R.id.bind).setOnClickListener(this);
        findViewById(R.id.unbind).setOnClickListener(this);
        findViewById(R.id.scan).setOnClickListener(this);
        findViewById(R.id.connect).setOnClickListener(this);
        findViewById(R.id.disconnect).setOnClickListener(this);
        findViewById(R.id.read_fw).setOnClickListener(this);
        findViewById(R.id.sync).setOnClickListener(this);
        findViewById(R.id.set_alarm).setOnClickListener(this);
        getcursportdata.setOnClickListener(this);
        set_time.setOnClickListener(this);
        set_parameters.setOnClickListener(this);

        radioGroupNotification1 = (RadioGroup) findViewById(R.id.rgNotification1);
        radioGroupNotification2 = (RadioGroup) findViewById(R.id.rgNotification2);
        radioGroupNotification1.clearCheck(); // this is so we can start fresh, with no selection on both RadioGroups
        radioGroupNotification2.clearCheck();
        radioGroupNotification1.setOnCheckedChangeListener(radioGroupNotificationListener1);
        radioGroupNotification2.setOnCheckedChangeListener(radioGroupNotificationListener2);
        progressBarGettingSteps = (ProgressBar)findViewById(R.id.progressBarGettingSteps);

        is24HourFormat = DateFormat.is24HourFormat(this);


        DataBaseSQLite = new DataBaseHelper(this);
        //DataBaseSQLite.deleteAllStoredData();
        //DataBaseSQLite.getAllStoredData();
        DrawDataByDayStepsChart("2019-01-09");
    }

    private static final String TAG = MainActivity.class.getSimpleName();

    private IRemoteService mService;
    private boolean mIsBound = false;
    private String DEMO_APPID = "AAL963FC51C5VPM";
    private String DEMO_SECRET = "IrvotlO3t5UVIL15qewGke56GV6jNxPUJSpsYteb";
    private String DEMO_VID = "000017001202";

    private int countStep = 0;
    private int dayNumberFromToday = 0;
    private String data = "";
    private LinearLayout llConnect;

    private int sleepcount = 0;
    private int stepcount = 0;
    private boolean bStart = false;

    private PopupWindow window;

    private ArrayList<BleDeviceItem> nearbyItemList;
    public class BleDeviceItem {
        private String bleDeviceName;
        private String bleDeviceAddress;
        private String nickname;
        private String bindedDate;
        private int rssi;
        private String type;

        public BleDeviceItem() {
        }

        public BleDeviceItem(String deviceName, String deviceAddress, String nickname, String bindedDate, int rssi, String type) {
            setBleDeviceName(deviceName);
            setBleDeviceAddress(deviceAddress);
            setNickname(nickname);
            setBindedDate(bindedDate);
            setRssi(rssi);
            setType(type);
        }

        public String getBleDeviceName() {
            return bleDeviceName;
        }
        public void setBleDeviceName(String bleDeviceName) {
            this.bleDeviceName = bleDeviceName;
        }

        public String getBleDeviceAddress() {
            return bleDeviceAddress;
        }

        public void setBleDeviceAddress(String bleDeviceAddress) {
            this.bleDeviceAddress = bleDeviceAddress;
        }

        public int getRssi() {
            return rssi;
        }

        public void setRssi(int rssi) {
            this.rssi = rssi;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getBindedDate() {
            return bindedDate;
        }

        public void setBindedDate(String bindedDate) {
            this.bindedDate = bindedDate;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

    }

    private listDeviceViewAdapter nearbyListAdapter;

    /*Interface*/
    private Button btBind;
    private Button btUnbind;
    private Button btScan;
    private Button btConnect;
    private Button btDisconnect;
    private Button btReadFw;
    private Button btSync;
    private TextView tvSync;
    private Button btSyncPersonalInfo;
    private Button bNotify;
    private Button set_time, getcursportdata, set_parameters;
    private ImageButton previous_daySteps,next_daySteps;
    private TextView device_info_text,data_text,data_heartrate,oxygen_data,fatigue_data,bloodpressure_data,stepsChart_date;
    private TextView last_sync,currenttotal_steps,currenttotal_distance,currenttotal_calories,currenttotal_sleeping,currenttotal_exercise;
    private Button set_userinfo,set_vir,set_photo,set_idletime,set_sleep,read_battery,read_fw,set_alarm,set_autoheart,set_fuzhu,set_showmode,openheart,closeheart,getStepsData;
    private Button setLanguage,send_weather,bt_getmutipleSportData,bt_open_blood,bt_close_blood,bt_setgoalstep,bt_setHeartRateArea;
    private RadioGroup radioGroupNotification1,radioGroupNotification2;
    private ProgressBar progressBarGettingSteps;
    private boolean is24HourFormat = false;
    DataBaseHelper DataBaseSQLite;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Toast.makeText(MainActivity.this, "Service connected", Toast.LENGTH_SHORT).show();

            mService = IRemoteService.Stub.asInterface(service);
            try {
                //mService.openSDKLog(true);
                mService.registerCallback(mServiceCallback);
                callRemoteAuthrize(DEMO_VID, DEMO_APPID, DEMO_SECRET);

                boolean isConnected = callRemoteIsConnected();

                if(isConnected == false) {
                    btBind.setEnabled(false);
                    btUnbind.setEnabled(true);
                    btScan.setEnabled(true);
                    btConnect.setEnabled(false);
                    btDisconnect.setEnabled(false);
                    llConnect.setVisibility(View.GONE);
                }
                else {
                    int authrize = callRemoteIsAuthrize();
                    if(authrize == 200) {
                        String curMac = callRemoteGetConnectedDevice();

                        btBind.setEnabled(false);
                        btUnbind.setEnabled(true);
                        btScan.setEnabled(true);
                        btConnect.setEnabled(false);
                        btDisconnect.setEnabled(true);
                        llConnect.setVisibility(View.VISIBLE);
                    }
                }

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(MainActivity.this, "Service disconnected", Toast.LENGTH_SHORT).show();

            btBind.setEnabled(true);
            btUnbind.setEnabled(false);
            btScan.setEnabled(false);
            btConnect.setEnabled(false);
            btDisconnect.setEnabled(false);
            llConnect.setVisibility(View.GONE);
            mService = null;
        }
    };

    private IServiceCallback mServiceCallback = new IServiceCallback.Stub() {
        @Override
        public void onConnectStateChanged(int state) throws RemoteException {
            Log.i(TAG, String.format("onConnectStateChanged [%1$d]", state));
            if(state == 2) {
                showToast("connect_state", curMac);
            }
            else
                showToast("connect_state", "" + state);

            updateConnectState(state);
        }


        @Override
        public void onScanCallback(String deviceName, String deviceMacAddress, int rssi)
                throws RemoteException {
            Log.i(TAG, String.format("onScanCallback [%1$s][%2$s](%3$d)", deviceName, deviceMacAddress, rssi));

            BleDeviceItem item = null;

            Iterator<BleDeviceItem> iter = nearbyItemList.iterator();

            boolean bExist = false;
            while (iter.hasNext()) {
                item = (BleDeviceItem) iter.next();
                if(item.getBleDeviceAddress().equalsIgnoreCase(deviceMacAddress) == true) {
                    bExist = true;
                    item.setRssi(rssi);
                    break;
                }
            }

            if(bExist == false) {
                item = new BleDeviceItem(deviceName, deviceMacAddress, "", "", rssi, "");
                nearbyItemList.add(item);
                Collections.sort(nearbyItemList, new ComparatorBleDeviceItem());
            }

            Message msg = new Message();
            scanDeviceHandler.sendMessage(msg);
        }



        @Override
        public void onSetNotify(int result) throws RemoteException {
            showToast("onSetNotify", String.valueOf(result));
        }

        @Override
        public void onSetUserInfo(int result) throws RemoteException {
            Log.i("mytest", "onSetUserInfo" + " result : " + result);
            showToast("onSetUserInfo", "" + result);
        }

        @Override
        public void onAuthSdkResult(int errorCode) throws RemoteException {
            showToast("onAuthSdkResult",  errorCode + "");
        }

        @Override
        public void onGetDeviceTime(int result, String time) throws RemoteException {
            showToast("onGetDeviceTime", String.valueOf(time));
        }

        /**
         *
         * Callback interface
         * @param result: 1 success 0 failed
         * @throws RemoteException
         */
        @Override
        public void onSetDeviceTime(int result) throws RemoteException {
            showToast("onSetDeviceTime",  result + "");
        }

        @Override
        public void onSetDeviceInfo(int arg0) throws RemoteException {
            showToast("onSetDeviceInfo",  arg0 + "");
        }



        @Override
        public void onAuthDeviceResult(int arg0) throws RemoteException {
            showToast("onAuthDeviceResult",  arg0 + "");
        }

        @Override
        public void onSetAlarm(int arg0) throws RemoteException {
            showToast("onSetAlarm",  arg0 + "");
            Log.i("onSetAlarm result", arg0+"--------===");
        }

        @Override
        public void onSendVibrationSignal(int arg0) throws RemoteException {
            Log.i("mytest", "onSendVibrationSignal result:" + arg0);
        }

        /**
         *
         * @param battery: 0-100 percent
         * @param status: 0 is not charging, 1 is charging
         */
        @Override
        public void onGetDeviceBatery(int battery,int status) throws RemoteException {
            Log.i("mytest", "onGetDeviceBattery battery: " + battery  + "%, status: " + status);
            /*0 is not charging, 1 is charging*/
            final int fStatus = status;
            final int fBattery = battery;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    chargingBatteryAnimationEnable(fStatus,fBattery);
                }
            });

        }


        @Override
        public void onSetDeviceMode(int arg0) throws RemoteException {
            // TODO Auto-generated method stub
            Log.i("mytest", "onSetDeviceMode result:" + arg0);
        }

        @Override
        public void onSetHourFormat(int arg0) throws RemoteException {
            Log.i("mytest", "onSetHourFormat result:" + arg0);

        }

        @Override
        public void setAutoHeartMode(int arg0) throws RemoteException {
            // TODO Auto-generated method stub
            Log.i("mytest", "setAutoHeartMode result:" + arg0);
        }

        /**
         * Callback
         * @param type: Valid return values ​​are based on type
         *          Type = 0 current motion information
         *              The valid values ​​returned are:
         *              current timestamp of the bracelet, in seconds,
         *              current step,
         *              current distance (meter),
         *              current calorie (large card),
         *              current sleep time (seconds)
         *          Type = 1 current total exercise time information
         * @param timestamp: current timestamp, in seconds
         * @param step: current step
         * @param distance: current distance (meters)
         * @param cal: current calories (KCal)
         * @param cursleeptime: current sleep time (seconds)
         * @param totalexercisetime: current total exercise time
         * @param steptime: current step time
         * @throws RemoteException
         */
        @Override
        public void onGetCurSportData(int type, long timestamp, int step, int distance,
                                      int cal, int cursleeptime, int totalexercisetime,int steptime) throws RemoteException {
            Date date = new Date(timestamp * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String time = sdf.format(date);
            Log.i("mytest", "type : " + type + " , time :" + time + " , step: " + step + ", distance :" + distance + ", cal :" + cal + ", cursleeptime :" + cursleeptime + ", totalexercisetime:" + totalexercisetime);
            last_sync.setText("Sync: "+time);
            if(type == 0) {
                currenttotal_steps.setText("Steps: " + step);
                currenttotal_distance.setText("Distance: " + distance);
                currenttotal_calories.setText("Calories: " + cal);
                currenttotal_sleeping.setText("Sleeping: " + cursleeptime);

                stepcount = step+10;
                bt_setgoalstep.setText("Set target steps to: "+ stepcount);
                bt_setgoalstep.setEnabled(true);
            }else {
                currenttotal_exercise.setText("Total exercise time: " + totalexercisetime);
            }
        }

        /**
         *
         * @param type: 1 Motion information, 2 Sleep information, 3 Heart rate information
         * @param timestamp: Timestamp (seconds)
         * @param stepsOrSleepQlty:
         *      When type = 1 step is the number of steps,
         *                        heart rate is 0 (invalid)
         *      When type = 2 step is sleep quality data.
         *                        If  0 indicates no record or no sleep,
         *                        the range of values when recording is (1-100).
         *                        100 for the best sleep quality, 1 for the worst sleep quality)
         *                        Heart rate 0 (invalid)
         *      When type = 3, step is 0 (invalid),
         *                       heartrate is the heart rate value
         * @param heartrate: heart rate
         * @throws RemoteException
         */
        @Override
        public void onGetDataByDay(int type,long timestamp,int stepsOrSleepQlty,int heartrate)
                throws RemoteException {
            Date date = new Date(timestamp * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String recorddate = sdf.format(date);
            if(type == 1){
                Log.i("mytest", "onGetDataByDay type:" + type + ",time::" + recorddate + ",step:" + stepsOrSleepQlty + ",heartrate invalid:" +heartrate );
                DataBaseSQLite.insertBraceletInfo(stepsOrSleepQlty+"",0+"",heartrate+"",timestamp+"",recorddate+"");
            }
            else if(type == 2){
                Log.i("mytest", "onGetDataByDay type:" + type + ",time::" + recorddate + ",sleepQuality (0:NoData, 1:Bad-100:Great):" + stepsOrSleepQlty + ",heartrate invalid:" +heartrate );
                DataBaseSQLite.insertBraceletInfo(0+"",stepsOrSleepQlty+"",heartrate+"",timestamp+"",recorddate+"");
                sleepcount++;
            }else if(type == 3){
                Log.i("mytest", "onGetDataByDay type:" + type + ",time::" + recorddate + ",step:" + stepsOrSleepQlty + ",heartrate:" +heartrate );
                DataBaseSQLite.insertBraceletInfo(stepsOrSleepQlty+"",0+"",heartrate+"",timestamp+"",recorddate+"");
            }
        }

        @Override
        public void onGetDataByDayEnd(int type ,long timestamp) throws RemoteException {
            Date date = new Date(timestamp * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String recorddate = sdf.format(date);

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DATE, -dayNumberFromToday);
            final String dateToPlot = new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());

            Log.i("mytest", "onGetDataByDayEnd time::" + recorddate+ " :dateToPlot: "+dateToPlot   +",sleepcount:" + sleepcount);
            sleepcount = 0;
            DataBaseSQLite.getAllStoredData();//Just Show all the data we have

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    progressBarGettingSteps.setVisibility(View.GONE);
                    getStepsData.setVisibility(View.VISIBLE);
                    DrawDataByDayStepsChart(dateToPlot);//Plot the data at the day we just got from the bracelet Ex"2019-01-04"
                }
            });
        }

        @Override
        public void onSetPhontMode(int arg0) throws RemoteException {
            // TODO Auto-generated method stub
            Log.i("mytest", "onSetPhontMode result::" + arg0  );
        }


        @Override
        public void onSetSleepTime(int arg0) throws RemoteException {
            // TODO Auto-generated method stub
            Log.i("mytest", "onSetSleepTime result::" + arg0  );
        }


        @Override
        public void onSetIdleTime(int arg0) throws RemoteException {
            // TODO Auto-generated method stub
            Log.i("mytest", "onSetIdleTime result::" + arg0  );
        }


        @Override
        public void onGetDeviceInfo(int version, String macaddress, String vendorCode,
                                    String productCode,int result) throws RemoteException {
            Log.i("mytest", "onGetDeviceInfo  version :" + version  + ",macaddress : " + macaddress  + ",vendorCode : "+ vendorCode + ",productCode :" + productCode + " , CRCresult :" + result );

            final String fMacaddress = macaddress;
            final int fVersion = version;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    device_info_text.setText("Mac: " + fMacaddress.replaceAll("(.{2})", "$1"+":").substring(0,17)+"   Ver.:" + fVersion );
                    device_info_text.setTextColor(Color.rgb( (int) (Math.random() * 255),  (int) (Math.random() * 255), (int) (Math.random() * 255)));
                }
            });
        }

        @Override
        public void onGetDeviceAction(int type) throws RemoteException {
            // TODO Auto-generated method stub
            Log.i("mytest", "onGetDeviceAction type:" + type );
        }


        @Override
        public void onGetBandFunction(int result,boolean[] results) throws RemoteException {
            // TODO Auto-generated method stub
            for(int i = 0; i < results.length; i ++){
                Log.i("mytest", "onGetBandFunction result : " + result + ", results :" + i +  " : " + results[i]);

            }
        }


        @Override
        public void onSetLanguage(int arg0) throws RemoteException {
            // TODO Auto-generated method stub
            Log.i("mytest", "onSetLanguage result:" + arg0 );
        }


        @Override
        public void onSendWeather(int arg0) throws RemoteException {
            Log.i("mytest", "onSendWeather result:" + arg0 );
        }


        @Override
        public void onSetAntiLost(int arg0) throws RemoteException {
            Log.i("mytest", "onSetAntiLost result:" + arg0 );

        }


        @Override
        public void onReceiveSensorData(int heartrate, int Systolicpressure, int Diastolicpressure, int Oxygen, int Fatiguevalue) throws RemoteException {
            // TODO Auto-generated method stub
            Log.i("mytest", "onReceiveSensorData result: \nHeart Rate: " + heartrate + " ,\nsystolic pressure/diastolic pressure: " +  Systolicpressure + " / " + Diastolicpressure + " millimeters of mercury (mm Hg),\nOxygen: " +  Oxygen + " ,\nFatigue: " +  Fatiguevalue);
            if(heartrate != 0)
                data_heartrate.setText("Heart Rate: " + heartrate);
            if(Oxygen != 0)
                oxygen_data.setText("Oxygen: " +  Oxygen);
            if(Fatiguevalue != 0)
                fatigue_data.setText("Fatigue: " +  Fatiguevalue);
            if(Systolicpressure != 0 || Diastolicpressure != 0)
                bloodpressure_data.setText( Systolicpressure + " / " + Diastolicpressure + " [mm Hg]");
        }
        /**
         * Callback
         * Note that after successfully entering the heart rate mode, the bracelet will send this message to the mobile app at a certain interval.
         * @param result: return start\close result
         * @param timestamp: current wrist time
         * @param heartrate: heart rate value
         * @param sleepstatus: current sleep state 0x00-0x03, 0 no sleep 3 is the best sleep quality, 1 is the worst sleep quality
         * @throws RemoteException
         */
        @Override
        public void onGetSenserData(int result, long timestamp, int heartrate, int sleepstatus)
                throws RemoteException {
            Date date = new Date(timestamp * 1000);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            String time = sdf.format(date);
            Log.i("mytest", "onGetSenserData result: " +  result + ",time:" + time + ",heartrate:" + heartrate +",sleepstatus:" + sleepstatus);

            last_sync.setText("Sync: "+time);
            if(heartrate != 0)
                data_heartrate.setText("Heart Rate: " + heartrate);
        }

        @Override
        public void onSetBloodPressureMode(int arg0) throws RemoteException {
            // TODO Auto-generated method stub
            Log.i("mytest", "onSetBloodPressureMode result:" + arg0 );
        }


        @Override
        public void onGetMultipleSportData(int flag, String recorddate,int mode, int value)
                throws RemoteException {
            // TODO Auto-generated method stub
//            Date date = new Date(timestamp * 1000);
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
//            String recorddate = sdf.format(date);
            Log.i("mytest", "onGetMultipleSportData flag:" + flag + " , mode :" + mode + " recorddate:" + recorddate +  " , value :" + value );
        }


        @Override
        public void onSetGoalStep(int result) throws RemoteException {
            // TODO Auto-generated method stub
            Log.i("mytest", "onSetGoalStep result:" + result );
        }


        @Override
        public void onSetDeviceHeartRateArea(int result) throws RemoteException {
            // TODO Auto-generated method stub
            Log.i("mytest", "onSetDeviceHeartRateArea result:" + result );
        }

        /**
         * Callback
         * @param type: 1: heart rate 2: blood pressure oximetry
         * @param state:1: open 0: off
         * @throws RemoteException
         */
        @Override
        public void onSensorStateChange(int type, int state)
                throws RemoteException {
            // TODO Auto-generated method stub
            Log.i("mytest", "onSensorStateChange type:" + type + " , state : " +  state);
            final int fType = type;
            final int fState = state;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(fType == 2){
                        if(fState == 1) {
                            bigButtonHeartAnimationEnable(true);
                        }
                        else {
                            bigButtonHeartAnimationEnable(false);
                        }
                    }else if(fType == 1){
                        if(fState == 1)
                            smallButtonHeartAnimationEnable(true);
                        else {
                            smallButtonHeartAnimationEnable(false);
                        }
                    }
                }
            });

        }


        @Override
        public void onReadCurrentSportData(int mode, String time, int step,
                                           int cal) throws RemoteException {
            // TODO Auto-generated method stub
            Log.i("mytest", "onReadCurrentSportData mode:" + mode + " , time : " +  time + " , step : " + step + " cal :" + cal);
        }


    };

    Handler updateConnectStateHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //super.handleMessage(msg);
            Bundle data = msg.getData();
            int state = data.getInt("state");

            if(state == 2) {
                btBind.setEnabled(false);
                btUnbind.setEnabled(true);
                btScan.setEnabled(true);
                btConnect.setEnabled(false);
                btDisconnect.setEnabled(true);
                llConnect.setVisibility(View.VISIBLE);
            }
            else {
                btBind.setEnabled(false);
                btUnbind.setEnabled(true);
                btScan.setEnabled(true);
                btConnect.setEnabled(false);
                btDisconnect.setEnabled(false);
                llConnect.setVisibility(View.GONE);
            }
            return true;
        }
    });

    protected void updateConnectState(int state) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putInt("state", state);
        msg.setData(data);
        updateConnectStateHandler.sendMessage(msg);
    }



    protected void showToast(String title, String content) {
        Message msg = new Message();
        Bundle data = new Bundle();
        data.putString("title", title);
        data.putString("content", content);
        msg.setData(data);
        messageHandler.sendMessage(msg);
    }

    Handler messageHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Bundle data = msg.getData();
            String title = data.getString("title");
            String content = data.getString("content");

            Log.i(TAG, title + ": " + content);
            switch (title) {
                case "onGetStep":
                    if(countStep >= 10){
                        Toast.makeText(MainActivity.this, title + ": " + content, Toast.LENGTH_SHORT).show();
                        data_text.setText(title + ": " + content);
                        countStep = 0;
                    }
                    countStep ++;
                    break;
                case "onGetSportDataStart":
                    llConnect.setVisibility(View.GONE);
                    tvSync.setText(tvSync.getText().toString() + "\n" + content);
                    break;
                case "onGetSportData":
                    tvSync.setText(tvSync.getText().toString() + "\n" + content);
                    break;
                case "onGetSportDataEnd":
                    tvSync.setText(tvSync.getText().toString() + "\n" + content);
                    break;
                default:
                    Toast.makeText(MainActivity.this, title + ": " + content, Toast.LENGTH_SHORT).show();
                    data_text.setText(title + ": " + content);
                    break;
            }
            return true;
        }
    });

    Handler scanDeviceHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            //super.handleMessage(msg);
            Bundle data = msg.getData();
            String result = data.getString("result");
            nearbyListAdapter.notifyDataSetChanged();
            return true;
        }
    });

    protected String curMac;

    private void callRemoteScanDevice() {
        if(nearbyItemList != null)
            nearbyItemList.clear();

        if (mService != null) {
            try {
                popWindow(findViewById(R.id.scan), R.layout.popwindow_devicelist);
                bStart = !bStart;
                mService.scanDevice(bStart);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean callRemoteIsConnected() {
        boolean isConnected = false;
        if (mService != null) {
            try {
                isConnected = mService.isConnectBt();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }

        return isConnected;
    }

    private String callRemoteGetConnectedDevice() {
        String deviceMac = "";
        if (mService != null) {
            try {
                deviceMac = mService.getConnectedDevice();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }

        return deviceMac;
    }

    private void callRemoteConnect(String name, String mac) {
        if(mac == null || mac.length() == 0) {
            Toast.makeText(this, "ble device mac address is not correctly!", Toast.LENGTH_SHORT).show();
            return ;
        }

        if (mService != null) {
            try {
                mService.connectBt(name, mac);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }

    private void callRemoteDisconnect() {

        if (mService != null) {
            try {
                mService.disconnectBt(true);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }

    private void callRemoteAuthrize(String vid, String appId, String appSecret) {
/*        if (mService != null) {
            try {
            	mService.authrize(vid, appId, appSecret);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }*/
    }

    private int callRemoteIsAuthrize() {
        int isAuthrize = 0;
        if (mService != null) {
            try {
                isAuthrize = mService.isAuthrize();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }

        return isAuthrize;
    }

    private int callRemoteSetOption(BleClientOption opt) {
        int result = 0;
        if (mService != null) {
            try {
                result = mService.setOption(opt);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    private int callRemoteGetFw() {
        int result = 0;
        if (mService != null) {
//            try {
//            	result = mService.getDeviceVer();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
//            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    private int callRemoteSync() {
        int result = 0;
        if (mService != null) {
//            try {
//            	EditText etStart = (EditText) findViewById(R.id.etStart);
////		        result = mService.getSportData(true, Long.parseLong(etStart.getText().toString()));
//            } catch (RemoteException e) {
//                e.printStackTrace();
//                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
//            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }

        return result;
    }

    private int callRemoteSetUserInfo() {
        int result = 0;
        UserProfile userProfile = new UserProfile(10000, 170, 60, 50, 0, 1, 24);
        BleClientOption opt = new BleClientOption(userProfile, null, null);
        result = callRemoteSetOption(opt);
        if (mService != null) {
            try {
                /*Set user information. First set UserProfile and setOption.*/
                result = mService.setUserInfo();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }

        return result;
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if(keyCode == KeyEvent.KEYCODE_BACK){
            android.os.Process.killProcess(android.os.Process.myPid());
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsBound) {
            unbindService(mServiceConnection);
        }

    }

    @Override
    public void onClick(View v) {
        Calendar date = Calendar.getInstance();
        switch (v.getId()) {
            case R.id.bind:
                Intent intent = new Intent(IRemoteService.class.getName());
                //intent.setClassName("com.sxr.sdk.ble.keepfit.client", "com.sxr.sdk.ble.keepfit.client.SampleBleService");
                intent.setClassName("com.liviolife.smartbracelet", "com.liviolife.smartbracelet.SampleBleService");

                bindService(intent, mServiceConnection, BIND_AUTO_CREATE);
                mIsBound = true;
                break;
            case R.id.unbind:
                if (mIsBound) {
                    btBind.setEnabled(true);
                    btUnbind.setEnabled(false);
                    btScan.setEnabled(false);
                    btConnect.setEnabled(false);
                    btDisconnect.setEnabled(false);
                    btReadFw.setEnabled(false);
                    btSync.setEnabled(false);

                    btConnect.setText(R.string.connect);
                    btDisconnect.setText(R.string.disconnect);
                    btReadFw.setText(R.string.read_fw);
                    btSync.setText(R.string.sync);

                    btSyncPersonalInfo.setEnabled(false);
                    getcursportdata.setEnabled(false);
                    set_time.setEnabled(false);
                    set_parameters.setEnabled(false);

                    try {
                        mService.unregisterCallback(mServiceCallback);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                    unbindService(mServiceConnection);
                    mIsBound = false;
                }
                break;
            case R.id.scan:
                callRemoteScanDevice();
                break;
            case R.id.disconnect:
                callRemoteDisconnect();
                break;
            case R.id.sync:
                callRemoteSync();
                break;
            case R.id.bNotify:
                callNotify();
                break;
            case R.id.set_parameters:
                callSetParameters();
                break;

//            	set_photo,set_idletime,set_sleep,read_battery,read_fw,set_alarm,set_autoheart,set_fuzhu,set_showmode
            case R.id.set_time:
                callSetDeviceTime();
                break;
            case R.id.set_userinfo:
                callRemoteSetUserInfo();
                break;
            case R.id.getcursportdata:
                callgetCurSportData();
                break;
            case R.id.set_vir:
                callSet_vir();
                break;
            case R.id.set_photo:
                callRemoteSetphoto();
                break;
            case R.id.set_idletime:
                callRemoteSetIdletime();
                break;
            case R.id.set_sleep:
                callRemoteSetSleepTime();
                break;
            case R.id.read_battery:
                callRemoteGetDeviceBattery();
                break;
            case R.id.read_fw:
                callRemoteGetDeviceInfo();
                break;
            case R.id.set_alarm:
                callSetAlarm();
                break;
            case R.id.set_autoheart:
                callRemoteSetAutoHeartMode(true);
                break;
            case R.id.set_fuzhu:
                DeviceProfile deviceProfile = new DeviceProfile(true, true, false, 18, 20, 00, 00);
                BleClientOption opt2 = new BleClientOption(null, deviceProfile, null);
                int result2 = callRemoteSetOption(opt2);
                callRemoteSetDeviceMode();
                break;
            case R.id.set_showmode:
                callRemoteSetHourFormat();
                break;
            case R.id.openheart:
                callRemoteSetHeartRateMode(true);
                break;
            case R.id.closeheart:
                callRemoteSetHeartRateMode(false);
                break;
            case R.id.getStepsData:
                callRemoteGetData(1,dayNumberFromToday);//day:0 today, 3: 3days ago
                break;
            case R.id.setLanguage:
                callRemoteSetLanguage();
                break;
            case R.id.send_weather:
//            	Weather weather = new Weather();
                Weather weather = new Weather((int) (System.currentTimeMillis()/1000), 300, 400, 7, 28, 2, 0, 0, 0, -20); //时间 白天、晚上天气 、最低最高温 空气质量、PM2.5 UV AQI 当前温度
                BleClientOption opt3 = new BleClientOption(null, null, null,weather);
                callRemoteSetOption(opt3);
                callRemoteSetWeather();
                break;
            case R.id.bt_getmutipleSportData:
                callRemoteGetMutipleData(0);
                break;
            case R.id.bt_open_blood:
                callRemoteOpenBlood(true);
                break;
            case R.id.bt_close_blood:
                callRemoteOpenBlood(false);
                break;
            case R.id.bt_setgoalstep:
                callRemoteSetGoalStep(stepcount);
                break;
            case R.id.bt_setHeartRateArea:
                callRemoteSetHeartRateArea(true,150,80);
                break;
            case R.id.previous_daySteps:
                dayNumberFromToday += 1;
                date.add(Calendar.DATE, -dayNumberFromToday);
                stepsChart_date.setText(new SimpleDateFormat("MMMM dd, yyyy EEEE").format(date.getTime())+" (-"+dayNumberFromToday+") *Saved locally");
                DrawDataByDayStepsChart(new SimpleDateFormat("yyyy-MM-dd").format(date.getTime()));
                //callRemoteGetData(1,dayNumberFromToday);//day:0 today, 3: 3days ago
                break;
            case R.id.next_daySteps:
                if(dayNumberFromToday > 0) {
                    dayNumberFromToday -= 1;
                    date.add(Calendar.DATE, -dayNumberFromToday);
                    stepsChart_date.setText(new SimpleDateFormat("MMMM dd, yyyy EEEE").format(date.getTime()) + " (-" + dayNumberFromToday + ") *Saved locally");
                    DrawDataByDayStepsChart(new SimpleDateFormat("yyyy-MM-dd").format(date.getTime()));
                    //callRemoteGetData(1, dayNumberFromToday);//day:0 today, 3: 3days ago
                }
                break;
        }
    }

    private void callSetParameters(){
        int result;
        if (mService != null) {
            try {
                DeviceProfile deviceProfile = new DeviceProfile(true, true, false, 1, 2, 00, 00);
                BleClientOption opt2 = new BleClientOption(null, deviceProfile, null);
                int result2 = callRemoteSetOption(opt2);
                result = mService.setDeviceInfo();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }

    private void callSetAlarm() {
        boolean result;
        if (mService != null) {
            try {
                Calendar rightNow = Calendar.getInstance();
                rightNow.add(Calendar.MINUTE, 1);
                int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)
                int currentMinutePlusOne = rightNow.get(Calendar.MINUTE);
                Log.i("mytest", "Alarm: " + currentHourIn24Format +":"+currentMinutePlusOne);
                set_alarm.setText("Alarm set for 1 minute: " + currentHourIn24Format +":"+currentMinutePlusOne);

                ArrayList<AlarmInfoItem> lAlarmInfo = new ArrayList<AlarmInfoItem>();
                AlarmInfoItem item = new AlarmInfoItem(1, 1, currentHourIn24Format, currentMinutePlusOne, 1, 1, 1, 1, 1, 1, 1, "App Alarm",false);
                lAlarmInfo.add(item);
                BleClientOption bco = new BleClientOption(null, null, lAlarmInfo);
                mService.setOption(bco);
                mService.setAlarm();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }

    private RadioGroup.OnCheckedChangeListener radioGroupNotificationListener1 = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                radioGroupNotification2.setOnCheckedChangeListener(null); // remove the listener before clearing
                radioGroupNotification2.clearCheck(); // clear the second RadioGroup!
                radioGroupNotification2.setOnCheckedChangeListener(radioGroupNotificationListener2); //reset the listener
            }
        }
    };
    private RadioGroup.OnCheckedChangeListener radioGroupNotificationListener2 = new RadioGroup.OnCheckedChangeListener() {

        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            if (checkedId != -1) {
                radioGroupNotification1.setOnCheckedChangeListener(null);
                radioGroupNotification1.clearCheck();
                radioGroupNotification1.setOnCheckedChangeListener(radioGroupNotificationListener1);
            }
        }
    };
    private void callNotify() {
        boolean result;
        if (mService != null) {
            try {
                /*Get Radio Groups 1 and 2*/
                int chkId1 = radioGroupNotification1.getCheckedRadioButtonId();
                int chkId2 = radioGroupNotification2.getCheckedRadioButtonId();
                int realCheckNotification = chkId1 == -1 ? chkId2 : chkId1;
                int type = 0;
                switch (realCheckNotification) {
                    case R.id.callNotification:
                        type = 0;//phone = 0
                        break;
                    case R.id.messageNotification:
                        type = 1;//SMS = 1
                        break;
                    case R.id.wechatNotification:
                        type = 2;//WeChat = 2
                        break;
                    case R.id.qqNotification:
                        type = 3;//qq = 3
                        break;
                    case R.id.facebookNotification:
                        type = 4;//facebook= 4
                        break;
                    case R.id.skypeNotification:
                        type = 5;// skype = 5
                        break;
                    case R.id.twiterNotification:
                        type = 6;//twitter = 6
                        break;
                    case R.id.whatsappNotification:
                        type = 7;//what_is_app = 7
                        break;
                    case R.id.lineNotification:
                        type = 8;//LINE=8
                        break;
                    case R.id.talkNotification:
                        type = 9;//TALK=9
                        break;
                }
                String name = ((EditText)findViewById(R.id.etName)).getText().toString();
                String content = ((EditText)findViewById(R.id.etContent)).getText().toString();
                result = mService.setNotify(System.currentTimeMillis()+"",type, name, content);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }

    private void callSetDeviceTime() {
        int result;
        if (mService != null) {
            try {
                result = mService.setDeviceTime();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }
    private void callgetCurSportData() {
        int result;
        if (mService != null) {
            try {
                result = mService.getCurSportData();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }
    private void callSet_vir() {
        int result;
        if (mService != null) {
            try {
                result = mService.sendVibrationSignal(4); //Shake 4 times
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }
    private void callRemoteSetphoto() {
        int result;
        if (mService != null) {
            try {
                result = mService.setPhontMode(true);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }
    private void callRemoteSetIdletime() {
        int result;
        if (mService != null) {
            try {
                Calendar rightNow = Calendar.getInstance();
                int currentHourIn24Format = rightNow.get(Calendar.HOUR_OF_DAY); // return the hour in 24 hrs format (ranging from 0-23)
                int currentMinute = rightNow.get(Calendar.MINUTE);
                rightNow.add(Calendar.MINUTE, 1);
                int currentMinutePlusOne = rightNow.get(Calendar.MINUTE);
                Log.i("mytest", "Sedentary Alarm: " + currentHourIn24Format+":"+currentMinute+"-"+ currentHourIn24Format +":"+currentMinutePlusOne);
                set_idletime.setText("Set sedentary reminder alarm every 10 sec for one minute " + currentHourIn24Format+":"+currentMinute+"-"+ currentHourIn24Format +":"+currentMinutePlusOne);

                result = mService.setIdleTime(10, currentHourIn24Format, currentMinute, currentHourIn24Format, currentMinutePlusOne);//Alarm Every 10seconds
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }
    private void callRemoteSetSleepTime() {
        int result;
        if (mService != null) {
            try {
                result = mService.setSleepTime(12, 00, 14, 00, 22, 00, 8, 00);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }
    private void callRemoteGetDeviceBattery() {
        int result;
        if (mService != null) {
            try {
                result = mService.getDeviceBatery();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }
    private void callRemoteGetDeviceInfo() {
        int result;
        if (mService != null) {
            try {
                result = mService.getDeviceInfo();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }
    private void callRemoteSetDeviceMode() {
        int result;
        if (mService != null) {
            try {
                result = mService.setDeviceMode(3);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }

    private void callRemoteSetHourFormat() {
        int result;
        if (mService != null) {
            try {
                is24HourFormat = !is24HourFormat;
                result = mService.setHourFormat(is24HourFormat == true ? 0 : 1);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }
    private void callRemoteSetHeartRateMode(boolean enable) {
        int result;
        if (mService != null) {
            try {
                result = mService.setHeartRateMode(enable,60);
                smallButtonHeartAnimationEnable(enable);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
                smallButtonHeartAnimationEnable(false);
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }
    private void callRemoteSetAutoHeartMode(boolean enable) {
        int result;
        if (mService != null) {
            try {
                result = mService.setAutoHeartMode(enable,18,00,19,00,15,2); //18:00 - 19:00  15min 2min
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }
    private void callRemoteGetData(int type,int day) {
        Log.i("mytest", "callRemoteGetData");
        int result;
        if (mService != null) {
            try {
                /*
                Type 1: Get detailed sports information for the day, range 0-27
                    (for example, 0: day, 1: previous day)
                Type 2: Get detailed heart rate data for the day, range 0-1
                    (0: same day, 1: previous day)
                */
                result = mService.getDataByDay(type, day);
                progressBarGettingSteps.setVisibility(View.VISIBLE);
                getStepsData.setVisibility(View.GONE);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }
    //    private void callRemoteGetFunction() {
//    	Log.i("mytest", "callRemoteGetFunction");
//    	int result;
//    	if (mService != null) {
//            try {
//                result = mService.getBandFunction();
//            } catch (RemoteException e) {
//                e.printStackTrace();
//                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
//        }
//	}
    private void callRemoteSetLanguage() {
        Log.i("mytest", "callRemoteSetLanguage");
        int result;
        if (mService != null) {
            try {
                result = mService.setLanguage();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }
    private void callRemoteSetWeather() {
        Log.i("mytest", "callRemoteSetWeather");
        int result;
        if (mService != null) {
            try {
                result = mService.sendWeather();
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }
    private void callRemoteGetMutipleData(int day) {
        Log.i("mytest", "callRemoteGetMutipleData");
        int result;
        if (mService != null) {
            try {
                result = mService.getMultipleSportData(day);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }
    private void callRemoteOpenBlood(boolean enable) {
        Log.i("mytest", "callRemoteOpenBlood");
        int result;
        if (mService != null) {
            try {
                result = mService.setBloodPressureMode(enable);
                bigButtonHeartAnimationEnable(enable);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
                bigButtonHeartAnimationEnable(false);
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }
    private void callRemoteSetGoalStep(int step) {
        Log.i("mytest", "callRemoteSetGoalStep");
        int result;
        if (mService != null) {
            try {
                result = mService.setGoalStep(step);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }
    private void callRemoteSetHeartRateArea(boolean enable ,int max, int min) {
        Log.i("mytest", "callRemoteOpenBlood");
        int result;
        if (mService != null) {
            try {
                result = mService.setDeviceHeartRateArea(enable,max,min);
            } catch (RemoteException e) {
                e.printStackTrace();
                Toast.makeText(this, "Remote call error!", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Service is not available yet!", Toast.LENGTH_SHORT).show();
        }
    }

    /*******************************************************************************/
    AnimationDrawable bigButtonHeartFrameAnimation = null;
    private void bigButtonHeartAnimationEnable(boolean enable) {
        if(enable) {
            int drawableIconImage = R.drawable.big_heart_animation;
            bt_open_blood.setCompoundDrawablesWithIntrinsicBounds(0, drawableIconImage, 0, 0);
            // Get the background, which has been compiled to an AnimationDrawable object.
            bigButtonHeartFrameAnimation = (AnimationDrawable) bt_open_blood.getCompoundDrawables()[1];

            // Start the animation (looped playback by default).
            bigButtonHeartFrameAnimation.start();
        }else{
            if(bigButtonHeartFrameAnimation != null)
                bigButtonHeartFrameAnimation.stop();
            int drawableIconImage = R.drawable.blood_pressure;
            bt_open_blood.setCompoundDrawablesWithIntrinsicBounds(0, drawableIconImage, 0, 0);
        }
    }

    /*******************************************************************************/
    /*******************************************************************************/
    AnimationDrawable smallButtonHeartFrameAnimation = null;
    private void smallButtonHeartAnimationEnable(boolean enable) {
        if(enable) {
            int drawableIconImage = R.drawable.small_heart_animation;
            openheart.setCompoundDrawablesWithIntrinsicBounds(drawableIconImage, 0, 0, 0);
            // Get the background, which has been compiled to an AnimationDrawable object.
            smallButtonHeartFrameAnimation = (AnimationDrawable) openheart.getCompoundDrawables()[0];

            // Start the animation (looped playback by default).
            smallButtonHeartFrameAnimation.start();
        }else{
            if(smallButtonHeartFrameAnimation != null)
                smallButtonHeartFrameAnimation.stop();
            int drawableIconImage = R.drawable.cardiogram;
            openheart.setCompoundDrawablesWithIntrinsicBounds(drawableIconImage, 0, 0, 0);
        }
    }
    /*******************************************************************************/
    /*******************************************************************************/
    AnimationDrawable chargingBatteryAnimation = null;
    public void chargingBatteryAnimationEnable(int enable,int battery) {
        if(enable == 0){
            int drawableIconImage = R.drawable.battery100;
            if(battery <= 30)
                drawableIconImage = R.drawable.battery25;
            else if(battery <= 70)
                drawableIconImage = R.drawable.battery50;
            else if(battery <= 90)
                drawableIconImage = R.drawable.battery75;

            if(chargingBatteryAnimation != null)
                chargingBatteryAnimation.stop();
            read_battery.setCompoundDrawablesWithIntrinsicBounds(0,drawableIconImage,  0, 0);

        }else{
            int drawableIconImage = R.drawable.charging_battery_animation;
            read_battery.setCompoundDrawablesWithIntrinsicBounds(0, drawableIconImage, 0, 0);

            // Get the background, which has been compiled to an AnimationDrawable object.
            chargingBatteryAnimation = (AnimationDrawable) read_battery.getCompoundDrawables()[1];
            // Start the animation (looped playback by default).
            chargingBatteryAnimation.start();
        }
        read_battery.setText("Read battery ("+ battery+"%)");
        /*
        */
    }
    public boolean dismissPopWindow() {
        if (window != null) {
            window.dismiss();
            window = null;

            return true;
        }

        return false;
    }

    public void popWindow(View parent, int windowRes) {
        if (window == null) {
            LayoutInflater lay = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View popView = lay.inflate(windowRes, null);
//			View llHeader = popView.findViewById(R.id.llHeader);
//			llHeader.setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View arg0) {
//					window.dismiss();
//					window = null;
//				}
//
//			});

            nearbyItemList = new ArrayList<BleDeviceItem>();

            ListView nearbyListView = (ListView) popView.findViewById(R.id.nearby_device_listView);

            nearbyListAdapter = new listDeviceViewAdapter(this, nearbyItemList);
            nearbyListAdapter.setType(listDeviceViewAdapter.DEVICE_NEARBY);
            nearbyListView.setAdapter(nearbyListAdapter);

            nearbyListView.setOnItemClickListener(new OnItemClickListener()
            {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                {
                    try {
                        ViewHolder holder = (ViewHolder) view.getTag();
                        callRemoteScanDevice();
                        callRemoteDisconnect();
                        curMac = holder.mac;
                        callRemoteConnect(holder.name, holder.mac);

                        dismissPopWindow();
                    }
                    catch(Exception e) {
                        e.printStackTrace();
                        Log.e("BLE service", "ble connect ble device: excption");
                    }
                }
            });

            popView.setOnKeyListener(new OnKeyListener() {

                @Override
                public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                    window.dismiss();
                    window = null;
                    return false;
                }

            });

            window = new PopupWindow(popView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, true);
            window.setOutsideTouchable(true);
            window.setFocusable(true);
            window.update();
            window.showAtLocation(parent, Gravity.CENTER_VERTICAL, 0, 0);
        }
    }

    private class ViewHolder {
        TextView tvName;
        TextView address;
        TextView rssi;
        String name;
        String mac;
    }

    class listDeviceViewAdapter extends BaseAdapter implements
            OnItemSelectedListener {

        private static final int DEVICE_NEARBY = 0;
        int count = 0;
        private LayoutInflater layoutInflater;
        Context local_context;
        float xDown = 0, yDown = 0, xUp = 0, yUp = 0;
        private List<BleDeviceItem> itemList;
        private int type;
        protected AnimationDrawable adCallBand;

        public listDeviceViewAdapter(Context context, List<BleDeviceItem> list) {
            layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //layoutInflater = LayoutInflater.from(context);
            local_context = context;
            itemList = list;
        }

        public int getCount() {
            return itemList.size();
        }

        public Object getItem(int pos) {
            return pos;
        }

        public long getItemId(int pos) {
            return pos;
        }

        public View getView(int pos, View v, ViewGroup p) {
            View view;
            ViewHolder viewHolder;

            BleDeviceItem item = itemList.get(pos);

            view = layoutInflater.inflate(R.layout.device_listitem_text, null);
            viewHolder = new ViewHolder();

            view.setTag(viewHolder);
            viewHolder.tvName = (TextView) view.findViewById(R.id.ItemTitle);
            viewHolder.address = (TextView) view.findViewById(R.id.ItemDate);
            viewHolder.rssi = (TextView) view.findViewById(R.id.ItemRssi);

            viewHolder.tvName.setText(item.getBleDeviceName());
            viewHolder.address.setText(item.getBleDeviceAddress());
            int rssi = item.getRssi();
            viewHolder.rssi.setText(String.valueOf(rssi));
            viewHolder.name = item.getBleDeviceName();
            viewHolder.mac = item.getBleDeviceAddress();

            return view;
        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position,
                                   long id) {
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

    }

    public class ComparatorBleDeviceItem implements Comparator<BleDeviceItem> {

        @Override
        public int compare(BleDeviceItem arg0, BleDeviceItem arg1) {
            int rssi0 = arg0.getRssi();
            int rssi1 = arg1.getRssi();
            int result = 0;
            if(rssi0 < rssi1)
            {
                result=1;
            }
            if(rssi0 > rssi1)
            {
                result=-1;
            }

            return result;
        }
    }

    /*
    {
        "2019-01-04": [
          {"02:30:00": {"step": "13","sleepQuality": "80","heartrate": "60"}},
          {"02:31:00": {"step": "14","sleepQuality": "81","heartrate": "61"}}
        ],
        "2019-01-05": [
          {"02:30:00": {"step": "13","sleepQuality": "80","heartrate": "60"}},
          {"02:31:00": {"step": "14","sleepQuality": "81","heartrate": "61"}}
        ]
    }
    */

    public class DataBaseHelper extends SQLiteOpenHelper {

        // If you change the database schema, you must increment the database version.
        public static final int DATABASE_VERSION = 2;
        public static final String DATABASE_NAME = "BraceletInfo.db";
        public static final String TABLE_NAME = "bracelet_info_tb";
        private final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + TABLE_NAME;
        public static final String KEY_DATE = "sample_date";
        public static final String KEY_TIMESTAMP = "sample_timestamp";
        public static final String KEY_STEPS = "steps";
        public static final String KEY_HEART_RATE = "heart_rate";
        public static final String KEY_SLEEP_QLTY = "sleep_qlty";
        public static final String KEY_ROWID = "_id";

        private final String DATABASE_CREATE =
                "create table "+TABLE_NAME+" (" + KEY_ROWID + " integer primary key autoincrement, "
                        + KEY_STEPS + " text not null, " + KEY_HEART_RATE + " text not null, "  + KEY_SLEEP_QLTY + " text not null, "+ KEY_TIMESTAMP +" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"+KEY_DATE+" text);";


        public DataBaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over
            db.execSQL(SQL_DELETE_ENTRIES);
            onCreate(db);
        }
        @Override
        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }
        public boolean insertBraceletInfo (String steps, String sleepQlty, String heartRate, String timestamp,String date) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put(KEY_STEPS, steps);
            contentValues.put(KEY_HEART_RATE, heartRate);
            contentValues.put(KEY_SLEEP_QLTY, sleepQlty);
            contentValues.put(KEY_TIMESTAMP, timestamp);
            contentValues.put(KEY_DATE, date);
            long result = db.insert(TABLE_NAME, null, contentValues);
            if(result == -1)
                return false;
            else
                return true;
        }
        //public ArrayList<BarEntry> getBraceletDataByHour(String start_date, String end_date)//"2019-01-04 00:00:00","2019-01-04 23:59:59"
        public ArrayList<BarEntry> getBraceletStepsDataByHour(String date)//"2019-01-04"
        {
            ArrayList<BarEntry> values = new ArrayList<>();
            SQLiteDatabase db = this.getReadableDatabase();
            //https://www.sqlite.org/lang_datefunc.html
            //Cursor mCursor = db.rawQuery("SELECT strftime('%H', "+KEY_DATE+") as valHour, SUM("+KEY_STEPS+") as totalSteps FROM "+TABLE_NAME+" WHERE "+KEY_DATE+" >= '"+start_date+"' AND "+KEY_DATE+" <= '"+end_date+"' GROUP BY valHour ", null);
            Cursor mCursor = db.rawQuery("SELECT strftime('%H', "+KEY_DATE+") as valHour, SUM("+KEY_STEPS+") as totalSteps FROM "+TABLE_NAME+" WHERE date("+KEY_DATE+") == '"+date+"' GROUP BY valHour ", null);
            if (mCursor != null) {
                mCursor.moveToFirst();
            }

            while(mCursor.isAfterLast() == false){
                int hourOfTheDay = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("valHour")));
                int totalStepAtThisHour = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex("totalSteps")));
                Log.i("mytest", "Reading from DB ByHour: valHour::" + hourOfTheDay +": " + ",totalStep:" + totalStepAtThisHour);
                if (totalStepAtThisHour > 400) {
                    values.add(new BarEntry(hourOfTheDay, totalStepAtThisHour, getResources().getDrawable(R.drawable.award)));
                } else {
                    values.add(new BarEntry(hourOfTheDay, totalStepAtThisHour));
                }
                mCursor.moveToNext();
            }
            return values;
        }
        public sleepQltyDataResult getBraceletSleepQltyDataByHour(String date)//"2019-01-04"
        {
            ArrayList<Entry> values = new ArrayList<>();
            List<String> hourLabels = new ArrayList<String>();
            SQLiteDatabase db = this.getReadableDatabase();
            //https://www.sqlite.org/lang_datefunc.html
            Cursor mCursor = db.rawQuery("SELECT strftime('%H:%M',"+KEY_DATE+") as sleepTime,"+KEY_SLEEP_QLTY+" FROM "+TABLE_NAME+" WHERE date("+KEY_DATE+") == '"+date+"'", null);
            if (mCursor != null) {
                mCursor.moveToFirst();
            }
            int previousValue = 0,cont=0;
            while(mCursor.isAfterLast() == false){
                String sampleTime = mCursor.getString(mCursor.getColumnIndex("sleepTime"));
                int sleepQlty = Integer.parseInt(mCursor.getString(mCursor.getColumnIndex(KEY_SLEEP_QLTY)));
                Log.i("mytest", "Reading from DB ByHour: Hour::" + sampleTime +": " + ",SleepQlty:" + sleepQlty);
                //Don't show the same value more than once in a row
                if (previousValue != sleepQlty){
                    values.add(new Entry(cont,sleepQlty));
                    hourLabels.add(sampleTime);
                    cont++;
                }
                previousValue = sleepQlty;
                mCursor.moveToNext();
            }
            Log.i("mytest", "Reading from DB ByHour: Hour::" + hourLabels.size() +": " + ",SleepQlty:" + values.size());
            return new sleepQltyDataResult(hourLabels,values);
        }
        public void getAllStoredData() {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res =  db.rawQuery( "select * from "+TABLE_NAME, null );
            res.moveToFirst();

            while(res.isAfterLast() == false){
                Log.i("mytest", "Reading from dB: time::" + res.getString(res.getColumnIndex(KEY_TIMESTAMP))+": "+res.getString(res.getColumnIndex(KEY_DATE)) + ",step:" + res.getString(res.getColumnIndex(KEY_STEPS))+", sleepQlty: "+res.getString(res.getColumnIndex(KEY_SLEEP_QLTY)) + ",heartrate:" +res.getString(res.getColumnIndex(KEY_HEART_RATE)) );
                res.moveToNext();
            }
        }
        public void deleteAllStoredData(){
            SQLiteDatabase db = this.getWritableDatabase();
            db.execSQL("delete from " + TABLE_NAME);
        }

        final class sleepQltyDataResult {
            private final ArrayList<Entry> yValues;
            private final List<String> xHourLabels;
            public sleepQltyDataResult(List<String> xHourLabels,ArrayList<Entry> yValues) {
                this.xHourLabels = xHourLabels;
                this.yValues = yValues;
            }
            public List<String> xHourLabels() {
                return xHourLabels;
            }
            public ArrayList<Entry> yValues() {
                return yValues;
            }
        }
    }

    public void DrawDataByDayStepsChart(String date){

        /*********************************************/
        //https://github.com/PhilJay/MPAndroidChart/blob/master/MPChartExample/src/main/java/com/xxmassdeveloper/mpchartexample/BarChartActivity.java
        ArrayList<BarEntry> values = DataBaseSQLite.getBraceletStepsDataByHour(date);
        /*
        for (int i = 0; i < 24; i++) {
            float val = (float) (Math.random() * (15000 + 1));

            if (val > 4000) {
                values.add(new BarEntry(i, val, getResources().getDrawable(R.drawable.award)));
            } else {
                values.add(new BarEntry(i, val));
            }
        }*/
        BarChart chart = findViewById(R.id.stepsBarChart);
        BarDataSet set1 = new BarDataSet(values, "Steps");
        set1.setDrawIcons(true);
        set1.setColor(Color.WHITE);
        set1.setIconsOffset(new MPPointF(0, -15));
        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);
        BarData data = new BarData(dataSets);
        data.setValueTextSize(5f);
        data.setValueTextColor(Color.WHITE);
        data.setDrawValues(true);
        data.setBarWidth(0.5f);
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.animateY(2000);
        chart.getXAxis().setTextColor(Color.WHITE);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setData(data);
        DrawDataByDaySleepQltyChart(date);
    }
    public void DrawDataByDaySleepQltyChart(String date){
        DataBaseHelper.sleepQltyDataResult sleepQltyDataResult = DataBaseSQLite.getBraceletSleepQltyDataByHour(date);
        ArrayList<Entry> values = sleepQltyDataResult.yValues();
        final List<String> xAxisLabels = sleepQltyDataResult.xHourLabels();

        LineChart chart = findViewById(R.id.sleepQltyLineChart);

        LineDataSet set1 = new LineDataSet(values, "SleepQlty");
        set1.setDrawFilled(true);
        set1.setDrawIcons(true);
        set1.setColor(Color.WHITE);
        set1.setIconsOffset(new MPPointF(0, -15));
        /*********Set xAxis labels as hours instead of just numbers*********/
        IAxisValueFormatter formatterLabelsXaxis = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return xAxisLabels.get((int) value);
            }
        };
        // use the interface ILineDataSet
        List<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1);
        LineData data = new LineData( dataSets);
        data.setValueTextSize(5f);
        data.setValueTextColor(Color.WHITE);
        data.setDrawValues(true);
        set1.setDrawCircleHole(true);
        set1.setDrawCircles(true);
        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawGridLines(false);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
        xAxis.setValueFormatter(formatterLabelsXaxis);
        chart.animateY(2000);
        chart.getXAxis().setTextColor(Color.WHITE);
        chart.getAxisLeft().setTextColor(Color.WHITE);
        chart.getLegend().setTextColor(Color.WHITE);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisRight().setEnabled(false);
        chart.getAxisLeft().setAxisMinimum(0f);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);
        chart.setData(data);
    }
}
