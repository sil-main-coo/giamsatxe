package vn.viethoang.truong.smartclass.View.ScreenFlash;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import vn.viethoang.truong.smartclass.Check.CheckConnectInternet;
import vn.viethoang.truong.smartclass.R;
import vn.viethoang.truong.smartclass.View.Home.HomeActivity;
import vn.viethoang.truong.smartclass.services.ListenerService;

public class ScreenFlashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_flash);

        if (android.os.Build.VERSION.SDK_INT >= 21) {
            // only for gingerbread and newer versions
            Intent intent = new Intent(this, ListenerService.class);
            startService(intent);
        }else{
            startService(new Intent(ListenerService.class.getName()));
        }


        /**
         *  Để tạo màn hình chờ của app ta sử dụng thread với sleep là số giây dừng màn hình chờ
         *  Lưu ý trong khi ta sử dụng sleep thì không thể tác động trực tiếp vào UI của màn hình được.
         *  Để khắc phục điều này ta sử dụng runOnUIThread , với hàm run()
         */
        final Thread thread= new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if(CheckConnectInternet.haveNetworkConnection(ScreenFlashActivity.this)) {
                                Intent iManHinhChao = new Intent(ScreenFlashActivity.this, HomeActivity.class);
                                startActivity(iManHinhChao);
                                finish();
                            }else{
                                Toast.makeText(ScreenFlashActivity.this, R.string.in_err, Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    });

                }
            }
        });
        thread.start();

    }
}
