package vn.viethoang.truong.smartclass.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import vn.viethoang.truong.smartclass.R;
import vn.viethoang.truong.smartclass.View.Home.HomeActivity;


public class ListenerService extends Service {

    public final String TAG = "HelloService";
//    public String preferencesName= "data";
//    public SharedPreferences preferences;
    public int code=0;
    public int sttCar, countPerson;
    public FirebaseDatabase database;
    public DatabaseReference myRef;


    @Override
    public void onCreate() {
        Log.i(TAG, "Service onCreate");
        database = FirebaseDatabase.getInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        /* now let's wait until the debugger attaches */

        Log.i(TAG, "Service onStartCommand");

        if(haveNetworkConnection(this)) {
            // Đọc trạng thái thiết bị
            readSttCar(startId);

        }else{
            Toast.makeText(this, "disconnected", Toast.LENGTH_LONG).show();
        }

        return START_STICKY;
    }

    private  boolean haveNetworkConnection(Context context){
        boolean haveConnectWifi = false;
        boolean haveConnectMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni: netInfo){
            if (ni.getTypeName().equalsIgnoreCase("WIFI")){
                if(ni.isConnected()){
                    haveConnectWifi = true;
                }
            }
            if (ni.getTypeName().equalsIgnoreCase("MOBILE")){
                if(ni.isConnected()){
                    haveConnectMobile = true;
                }
            }
        }
        return haveConnectWifi || haveConnectMobile;
    }

    private void readSttCar(final int startId){
        myRef = database.getReference("cambien");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                sttCar = dataSnapshot.getValue(Integer.class);
                readPerson(sttCar, startId);
                Log.d(TAG, sttCar+" is: " + sttCar);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private void readPerson(final int sttCar, final int id){
        myRef = database.getReference("songuoi");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                countPerson = dataSnapshot.getValue(Integer.class);
                if(sttCar==0 && countPerson > 0){
                    showNotifi(id);
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });
    }

    private  void showCountPerson() {
        if(countPerson>1) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext());
            builder.setSmallIcon(R.drawable.on);
            Bitmap bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.bg_button_mail);
            builder.setLargeIcon(bitmap);
            builder.setContentTitle("Notification Title");
            builder.setContentText("Hello! Notification service.");
            int notificationId = 001;
            NotificationManager manager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
            manager.notify(notificationId, builder.build());
        }
    }

    private void showNotifi(int id){
        long[] v = {500, 3000};     // Đặt thời gian rung của thiết bị

        String CHANNEL_ID = "my_channel_01";
        CharSequence name = "my_channel";
        String Description = "Cảnh báo";
        Intent resultIntent;

        final NotificationManager mgr = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setDescription(Description);
            notificationChannel.enableVibration(true);
            notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mgr.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

        mBuilder.setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle("Cảnh báo !!!")
                .setContentText("Có "+ countPerson + "người trên xe")
                .setVibrate(v)                               // Thiết lập rung thiết bị
                .setTicker("Có "+ countPerson + "người trên xe")
                .setAutoCancel(true); // clear notification after click

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            try {
                Uri notification = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.alert);
                Ringtone r = RingtoneManager.getRingtone(this, notification);
                r.play();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            Uri alarmSound = Uri.parse("android.resource://" + this.getPackageName() + "/" + R.raw.alert);
            mBuilder.setSound(alarmSound);
        }

        resultIntent = new Intent(this, HomeActivity.class);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(this, code++, resultIntent,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager)this.getSystemService(NOTIFICATION_SERVICE);
        mNotificationManager.notify(code++, mBuilder.build());
        stopSelf(id);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}