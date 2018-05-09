package lifec.mc.a3_2015080;

// Android Libraries

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaRecorder;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

// Java Libraries

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity_A3_2015080 extends AppCompatActivity implements SensorEventListener {

    // Private Variables

    private TextView accTextView;
    private TextView gyroTextView;
    private TextView gpsTextView;
    private TextView networkTextView;
    private TextView wifiTextView;
    private TextView micTextView;

    private String AccelerometerReadings;
    private String GyroscopeReadings;

    // Sensors

    private Sensor accelerometer;
    private Sensor gyroscope;

    private SensorManager sensorManager;

    private String results;

    public static final int RECORD_AUDIO = 0;

    LocationListener locationListener;
    LocationManager locationManager;

    private float ax, ay, az;
    private float gx, gy, gz;

    private DatabaseHelper_A3_2015080 databaseHelper;

    float latitude;
    float longitude;

    private String GPS;
    private String TowerSignal;

    private String wifiID;
    String SoundDB = " dB";
    boolean bit = true;
    TelephonyManager mTelephonyManager;
    MyPhoneStateListener phoneStatelistener;
    WifiManager wifiManager;

    private MediaRecorder soundRecorder;
    private Timer checkTimer;

    private  int minTime = 100;
    private int maxDistance = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main__a3_2015080);

        databaseHelper = new DatabaseHelper_A3_2015080(this);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        verifyPermissions();
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        helperLog("");
        initializeSensors();
        this.networkTextView = findViewById(R.id.networktower);
        this.wifiTextView = findViewById(R.id.wifi);
        this.micTextView = findViewById(R.id.mic);
        helperLog("");
        phoneStatelistener = new MyPhoneStateListener();
        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(phoneStatelistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.startScan();

        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Accelerometer Error!!!", Toast.LENGTH_SHORT).show();
        }

        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            Toast.makeText(this, "Gyroscope Error!!!", Toast.LENGTH_SHORT).show();
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                helperLog("");
                latitude = (float) location.getLatitude();
                longitude = (float) location.getLongitude();
                helperLog("");
                GPS = "Latitude :- " + latitude + "\n" + "Longitude :- " + longitude;
                gpsTextView.setText(GPS);
                helperLog("");
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        helperLog("");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                helperLog("");
                ActivityCompat.requestPermissions(MainActivity_A3_2015080.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, RECORD_AUDIO);
            }
            helperLog("");
        } else {
            locationManager.requestLocationUpdates("gps", this.minTime, this.maxDistance, locationListener);
            helperLog("");
        }
        helperLog("");
        SoundCheck();
        wifi();
    }

    public void initializeSensors() {
        this.accTextView = findViewById(R.id.accelerometer);
        this.gyroTextView = findViewById(R.id.gyroscope);
        this.gpsTextView = findViewById(R.id.gps);
    }

    public void verifyPermissions() {
        String[] PERMISSIONS = {Manifest.permission.RECORD_AUDIO, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.READ_SMS, Manifest.permission.ACCESS_COARSE_LOCATION};
        if (checkForPermissions(this, PERMISSIONS) == false) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, 1);
        }
    }
    public void startStopButtonClicked(View view) {
        if (bit == true) {
            unRegister();
            removeValues();
            bit = false;
        } else {
            register();
            addValues();
            bit = true;
        }
    }

    public void addValues() {
        if (ActivityCompat.checkSelfPermission(MainActivity_A3_2015080.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity_A3_2015080.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        wifiManager.startScan();
        helperLog("");
        locationManager.requestLocationUpdates("gps", this.minTime, this.maxDistance, locationListener);
        mTelephonyManager.listen(phoneStatelistener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
        helperLog("");
    }
    public void register() {
        sensorManager.registerListener(MainActivity_A3_2015080.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(MainActivity_A3_2015080.this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }
    public void unRegister() {
        sensorManager.unregisterListener(MainActivity_A3_2015080.this, accelerometer);
        sensorManager.unregisterListener(MainActivity_A3_2015080.this, gyroscope);
    }

    public void removeValues() {
        locationManager.removeUpdates(locationListener);
        mTelephonyManager.listen(phoneStatelistener, PhoneStateListener.LISTEN_NONE);
        wifiManager.disconnect();
    }

    public void exportToDatabase(View view) {
        startActivity(new Intent(MainActivity_A3_2015080.this, ListActivity_A3_2015080.class));
    }

    public void saveButtonClicked(View view) {
        AddData(this.AccelerometerReadings, this.GyroscopeReadings, this.GPS, this.TowerSignal, this.results, this.SoundDB);
    }

    private String cellID = "LAC: 2098 \nCID: 230432779";

    public void SoundCheck() {
        Context activityContext = MainActivity_A3_2015080.this;
        if (ActivityCompat.checkSelfPermission(activityContext, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity_A3_2015080.this, new String[]{Manifest.permission.RECORD_AUDIO},
                    RECORD_AUDIO);
        } else {
            sound();
        }
    }

    public void sound() {
        this.soundRecorder = new MediaRecorder();
        setSoundValues();
        checkTimer = new Timer();
        checkTimer.scheduleAtFixedRate(new RecorderValueInnerClass(soundRecorder), 0, 500);
        this.soundRecorder.setOutputFile("/dev/null");
        try {
            soundRecorder.prepare();
            soundRecorder.start();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void wifi() {
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        results = "";
        helperLog("");
        if (wifiManager != null) {
            WifiInfo info = wifiManager.getConnectionInfo();
            if (info != null) {
                helperLog("");
                wifiID = info.getSSID();
            }
            List<ScanResult> configurations = this.wifiManager.getScanResults();
            Set<ScanResult> helperSet = new HashSet<>(configurations);
            configurations.clear();
            configurations.addAll(helperSet);
            int count = 0;
            for (ScanResult configuration : configurations) {
                if (count == configurations.size() - 1) {
                    results += configuration.SSID.toString();
                } else {
                    results = results + "\n" + configuration.SSID.toString();
                }
            }
            wifiTextView.append(results);
        } else {
            Toast.makeText(this, "No WIFI Network!!", Toast.LENGTH_SHORT).show();
        }
    }


    public void setSoundValues() {
        helperLog("");
        soundRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        soundRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        soundRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        helperLog("");
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        int xCoordinate = 0;
        int yCoordinate = 1;
        int zCoordinate = 2;
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            helperLog("");
            ax = event.values[xCoordinate];
            ay = event.values[yCoordinate];
            az = event.values[zCoordinate];
            helperLog("");
            AccelerometerReadings = "X:" + ax + "\nY:" + ay + "\nZ:" + az;
            accTextView.setText(AccelerometerReadings);
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gx = event.values[xCoordinate];
            gy = event.values[yCoordinate];
            gz = event.values[zCoordinate];
            GyroscopeReadings = "X:" + gx + "\nY:" + gy + "\nZ:" + gz;
            gyroTextView.setText(GyroscopeReadings);
        }
    }

    private class RecorderValueInnerClass extends TimerTask {
        TextView sound = (TextView) findViewById(R.id.mic);
        private MediaRecorder recorder;

        public RecorderValueInnerClass(MediaRecorder recorder) {
            helperLog("");
            this.recorder = recorder;
        }

        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    helperLog("");
                    int amplitude = recorder.getMaxAmplitude();
                    SoundDB = "" + ((float) (20 * Math.log10((float) Math.abs(amplitude)))) + " dB";
                    sound.setText("" + ((float) (20 * Math.log10((float) Math.abs(amplitude)))));
                }
            });
        }
    }

    class MyPhoneStateListener extends PhoneStateListener {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            super.onSignalStrengthsChanged(signalStrength);
            final TelephonyManager telephoneManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
            if (telephoneManager.getPhoneType() == TelephonyManager.PHONE_TYPE_GSM) {
                if (ActivityCompat.checkSelfPermission(MainActivity_A3_2015080.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity_A3_2015080.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                final GsmCellLocation location = (GsmCellLocation) telephoneManager.getCellLocation();
                helperLog("");
                if (location != null) {
                    helperLog("");
                    networkTextView.setText("LAC: " + location.getLac() + "\n" + "CID: " + location.getCid());
                }
            }
        }
    }

    public void AddData(String accelerometer, String gyroscope, String gps, String tower, String wifi, String sound) {
        helperLog("");
        boolean insertData = databaseHelper.insertData(accelerometer, gyroscope, gps, this.cellID, wifi, sound);
        if (insertData == true) {
            Toast.makeText(this, "Success...", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error!!!", Toast.LENGTH_SHORT).show();
        }
    }

    public void helperLog(String logMsg) {
        Log.d("CHECK", logMsg);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    // Check Permissions Method

    public boolean checkForPermissions(Context context, String... permissions) {
        if (context != null) {
            if (permissions != null) {
                for (String checkPermission : permissions) {
                    helperLog("");
                    if (ActivityCompat.checkSelfPermission(context, checkPermission) != PackageManager.PERMISSION_GRANTED) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

}


