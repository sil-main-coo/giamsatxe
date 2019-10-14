package vn.viethoang.truong.smartclass.View.Home.Fragment;

import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import vn.viethoang.truong.smartclass.Check.CheckConnectInternet;
import vn.viethoang.truong.smartclass.R;
import vn.viethoang.truong.smartclass.services.ListenerService;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.NOTIFICATION_SERVICE;


public class ControlFirstClassFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference myRef;

    private static TextView txtSoNguoi, txtTrangThai;
    private static final String TAG= "SensorSTT";
    private static String preferencesName= "data";
    private static SharedPreferences preferences;
    private static Context thisContext;
    private ProgressDialog dialog;

    private static MediaPlayer mp;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public ControlFirstClassFragment() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=  inflater.inflate(R.layout.layout, container, false);
        txtSoNguoi= view.findViewById(R.id.txtSoNguoi);
        txtTrangThai= view.findViewById(R.id.txtTrangThai);

        thisContext= container.getContext();

        preferences = thisContext.getSharedPreferences("open", MODE_PRIVATE);


        if(CheckConnectInternet.haveNetworkConnection(getContext())) {
            // Đọc trạng thái thiết bị
            readDataFromFB("songuoi");
            readDataFromFB("cambien");
        }else {
            Toast.makeText(getContext(), "Hãy kiểm tra lại kết nối internet !", Toast.LENGTH_LONG).show();
        }

        return view;
    }


    // Lấy dữ liệu thiết bị tắt mở trên references
    private static String getDataInReferences(String name){
        preferences= thisContext.getSharedPreferences(preferencesName,MODE_PRIVATE);
        String value= preferences.getString(name,"");
        return value;
    }


    public static void readDataFromFB(final String name){
        // Read from the database
        myRef = database.getReference(name);
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                int valueInt;

                switch (name){
                    case "cambien":
                        valueInt = dataSnapshot.getValue(Integer.class);
                        Log.d(TAG, name+" is: " + valueInt);
                        saveToPreferences("cambien", String.valueOf(valueInt));
                        showStatusOfCar(valueInt);
                        break;
                    case "songuoi":
                        valueInt = dataSnapshot.getValue(Integer.class);
                        Log.d(TAG, name+" is: " + valueInt);
                        saveToPreferences("songuoi", String.valueOf(valueInt));

                        showCountPersonToText(valueInt);
                        break;
                    default: Log.e(TAG, "Xem lại thông số truyền vào");

                }

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

    }


    public static void saveToPreferences(String name, String value) {
        preferences= thisContext.getSharedPreferences(preferencesName,MODE_PRIVATE);
        SharedPreferences.Editor editor= preferences.edit();
        editor.putString(name, value);
        //editor.putBoolean("SAVE",chkLuuDangNhap.isChecked());
        editor.apply();
        Log.e("RE", "ok");
    }


    private static void showCountPersonToText(long value) {
        txtSoNguoi.setText(value+"");
    }

    private static void showStatusOfCar(long value) {
        if(value==0)
            txtTrangThai.setText("Đã dừng");
        else {
            txtTrangThai.setText("Đang chạy");

        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("SENSOR","om Pause");
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
