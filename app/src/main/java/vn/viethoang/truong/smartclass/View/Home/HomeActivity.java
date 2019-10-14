package vn.viethoang.truong.smartclass.View.Home;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import com.google.firebase.database.FirebaseDatabase;
import vn.viethoang.truong.smartclass.R;
import vn.viethoang.truong.smartclass.View.Home.Activity.AboutAppActivity;
import vn.viethoang.truong.smartclass.View.ScreenFlash.ScreenFlashActivity;
import vn.viethoang.truong.smartclass.services.ListenerService;

public class HomeActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;  // Drawer menu
    private NavigationView nv;   // NavigationView hiển thị drawerview
    private ActionBarDrawerToggle toggle;  // drawer button
    private Toolbar toolbar;   // toolbar
    private ViewPager pager;  // pager show content tablayout:  rooms...
    private TabLayout tabLayout;  //  tablayout
    private PagerAdapter pagerAdapter;   // pager tab layout

    private static SharedPreferences preferences;   // save data: data devices, status, v.v.v
    private static String preferencesName= "data";  // name sharedPreferences
    private static final String TAG= "HOME";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        addControls();
    }

    private void addControls() {
        drawerLayout= findViewById(R.id.drawer);
        nv= findViewById(R.id.nv);
        toolbar= findViewById(R.id.toolbar);
        pager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);

        // Setup tablayout
        FragmentManager fragmentManager= getSupportFragmentManager();
        pagerAdapter= new vn.viethoang.truong.smartclass.Adapter.PagerAdapter(fragmentManager);
        pager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(pager);
        pager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);//deprecated
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(pager));


        // setup drawerLayout
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toggle= new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close);
        drawerLayout.addDrawerListener(toggle);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toggle.syncState();
        setupDrawerContent(nv);

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("ON RESUME", "I am here");
    }

    // Hàm setup menu drawer
    private void setupDrawerContent(NavigationView nv) {
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectDrawerItem(item);
                return false;
            }
        });
    }

    private void selectDrawerItem(MenuItem item) {
        Intent intent;
        switch (item.getItemId()){
            case R.id.mnControl:
                drawerLayout.closeDrawers();
                break;
            case R.id.mnAboutApp:
                intent= new Intent(HomeActivity.this, AboutAppActivity.class);
                startActivity(intent);
                break;

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // Lấy dữ liệu thiết bị tắt mở trên references
    private String getDataInReferences(String name){
        preferences= getSharedPreferences(preferencesName,MODE_PRIVATE);
        String value= preferences.getString(name,"");
        return value;
    }
}
