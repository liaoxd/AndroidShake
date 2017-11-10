package com.kiplening.shake;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    private static  String TAG = "tag---";
    SensorManager sensorManager;
    MySensorListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        listener = new MySensorListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);
    }

    private static final long MIN_TIME_BETWEEN_SAMPLES_NS =
            TimeUnit.NANOSECONDS.convert(20, TimeUnit.MILLISECONDS);
    private static final float SHAKING_TIME_WINDOW =
            TimeUnit.NANOSECONDS.convert(1, TimeUnit.SECONDS);

    class MySensorListener implements SensorEventListener{

        float lastAx = 0, lastAy = 0, lastAz = 0;
        float tlastAx = 0, tlastAy = 0, tlastAz = 0;
        float SENSOR_VALUE = (float) (SensorManager.GRAVITY_EARTH * 1.33f);
        private long mLastTimestamp;  //上一次检测的时间
        int count = 0;
        private int numShakes;
        private long lastShakeTimestamp;

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            if (sensorEvent.timestamp - mLastTimestamp < MIN_TIME_BETWEEN_SAMPLES_NS) {
                return;
            }
            float ax = sensorEvent.values[0];
            float ay = sensorEvent.values[1];
            float az = sensorEvent.values[2] - SensorManager.GRAVITY_EARTH;


            if ((Math.abs(sensorEvent.values[0]) > SENSOR_VALUE || Math.abs(sensorEvent.values[1]) > SENSOR_VALUE || Math.abs(sensorEvent.values[2]) > SENSOR_VALUE))   {
                //if (lastAy != 0 || lastAx != 0 || lastAz != 0){
                if (lastAz == 0 && lastAx == 0 && lastAy == 0){
                    lastAy += ay;
                    lastAz += az;
                    lastAx += ax;
                    return;
                }
                float ji = ax*lastAx + ay*lastAy + az*lastAz;
                float length = (float) (Math.sqrt(ax*ax + ay*ay + az*az) * Math.sqrt(lastAx*lastAx + lastAy*lastAy + lastAz*lastAz));
                Log.e(TAG, "cos: "+ ji/length);
                if(ji/length < -0.9){
                    lastShakeTimestamp = sensorEvent.timestamp;
                    Log.e(TAG, "cos: "+ ji/length);
                    Log.e(TAG, "ACCELEROMETER: " + Math.sqrt(ax*ax + ay*ay + az*az));
                    Log.e(TAG, "ACCELEROMETER: " + ax +"+++"+ ay+"+++"+az);
//                    float ji = ax*lastAx + ay*lastAy + az*lastAz;
//                    float length = (float) (Math.sqrt(ax*ax + ay*ay + az*az) * Math.sqrt(lastAx*lastAx + lastAy*lastAy + lastAz*lastAz));
//                    Log.e(TAG, "cos: "+ ji/length);
//                    lastAz = az;
//                    lastAy = ay;
//                    lastAx = ax;
                    reset();
                }else if (ji/length > 0.9){
                    lastAy += ay;
                    lastAz += az;
                    lastAx += ax;
                }
            }
            if (sensorEvent.timestamp - lastShakeTimestamp > SHAKING_TIME_WINDOW){
                reset();
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }

        private void reset() {
            numShakes = 0;
            lastAy = 0;
            lastAz = 0;
            lastAx = 0;
        }

        private void maybeDispatchShake(long currentTimestamp) {
            if (numShakes >= 6 ) {
                reset();
                Log.e(TAG, "maybeDispatchShake: +++++++++");
            }

//            if (currentTimestamp - mLastShakeTimestamp > SHAKING_WINDOW_NS) {
//                reset();
//            }
        }
    }
}
