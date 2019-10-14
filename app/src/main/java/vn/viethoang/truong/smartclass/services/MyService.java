//package vn.viethoang.truong.smartclass.services;
//
//import android.app.Service;
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Binder;
//import android.os.IBinder;
//import android.util.Log;
//
//import com.google.firebase.database.DatabaseReference;
//import com.google.firebase.database.FirebaseDatabase;
//
//import java.util.HashMap;
//import java.util.Map;
//
//import vn.viethoang.truong.smartclass.Check.CheckConnectInternet;
//
//public class MyService extends Service {
//    private static String LOG_TAG = "WeatherService";
//
//    private static final String TAG = "HelloService";
//    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
//    private static DatabaseReference myRef;
//    private static String preferencesName= "data";
//    private static SharedPreferences preferences;
//
//    // Lưu trữ dữ liệu thời tiết.
//    private static final Map<String, String> weatherData = new HashMap<String,String>();
//
//    private final IBinder binder = new LocalServiceBinder();
//
//    public class LocalServiceBinder extends Binder {
//
//        public MyService getService()  {
//            return MyService.this;
//        }
//    }
//
//    public MyService() {
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        //Creating new thread for my service
//        //Always write your long running tasks in a separate thread, to avoid ANR
//
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//
//                if(CheckConnectInternet.haveNetworkConnection(getBaseContext())) {
//                    // Đọc trạng thái thiết bị
//                    readFromFB("songuoi", getBaseContext());
//                    readFromFB("cambien", getBaseContext());
//                }
//
//                //Your logic that service will perform will be placed here
//                //In this example we are just looping and waits for 1000 milliseconds in each loop.
//                for (int i = 0; i < 5; i++) {
//                    try {
//                        Thread.sleep(1000);
//                    } catch (Exception e) {
//                    }
//
//                    if(isRunning){
//                        Log.i(TAG, "Service running");
//                    }
//                }
//
//                //Stop service once it finishes its task
//                // stopSelf();
//            }
//        }).start();
//
//        return Service.START_STICKY;
//    }
//
//    @Override
//    public IBinder onBind(Intent intent) {
//        Log.i(LOG_TAG,"onBind");
//        return this.binder;
//    }
//
//    @Override
//    public void onRebind(Intent intent) {
//        Log.i(LOG_TAG, "onRebind");
//        super.onRebind(intent);
//    }
//
//    @Override
//    public boolean onUnbind(Intent intent) {
//        Log.i(LOG_TAG, "onUnbind");
//        return true;
//    }
//
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Log.i(LOG_TAG, "onDestroy");
//    }
//
//    // Trả về thông tin thời tiết ứng với địa điểm của ngày hiện tại.
//    public String getWeatherToday(String location) {
//        Date now= new Date();
//        DateFormat df= new SimpleDateFormat("dd-MM-yyyy");
//
//        String dayString = df.format(now);
//        String keyLocAndDay = location + "$"+ dayString;
//
//        String weather=  weatherData.get(keyLocAndDay);
//        //
//        if(weather != null)  {
//            return weather;
//        }
//
//        //
//        String[] weathers = new String[]{"Rainy", "Hot", "Cool", "Warm" ,"Snowy"};
//
//        // Giá trị ngẫu nhiên từ 0 tới 4
//        int i= new Random().nextInt(5);
//
//        weather =weathers[i];
//        weatherData.put(keyLocAndDay, weather);
//        //
//        return weather;
//    }
//
//}
