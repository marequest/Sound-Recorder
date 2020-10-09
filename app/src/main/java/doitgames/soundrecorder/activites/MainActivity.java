package doitgames.soundrecorder.activites;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.widget.Toolbar;

import com.astuetz.PagerSlidingTabStrip;
import com.google.android.material.snackbar.Snackbar;

import doitgames.soundrecorder.MyPermissions;
import doitgames.soundrecorder.R;
import doitgames.soundrecorder.fragments.FileViewerFragment;
import doitgames.soundrecorder.fragments.RecordFragment;
import doitgames.soundrecorder.fragments.SettingsFragment;
//TODO Dodaj da se iz liste remove itemi slajdovanjem
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    // Requesting permission to RECORD_AUDIO
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    // Helper method to check multiple permissions and see if any of them are not granted
    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                Log.d(TAG, "onRequestPermissionsResult: grantResults.length = " + grantResults.length);
                for(int i = 0; i < grantResults.length; i++){
                    if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                        Log.d(TAG, "onRequestPermissionsResult: Permissions granted!");
                        permissionToRecordAccepted = true;
                    } else {
                        Log.d(TAG, "onRequestPermissionsResult: Permissions DENIED");
                        permissionToRecordAccepted = false;
                        AskAgainForPermission();
                        break;
                    }
                }
                break;
        }
    }

    private void AskPermission(){
        if(!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    private void AskAgainForPermission(){
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.main_activity), "Recording and listening to audio requires permissions!", Snackbar.LENGTH_INDEFINITE);
        mySnackbar.setAction("Ask Permissions", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AskPermission();
            }
        });
        mySnackbar.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: Starts");
        setContentView(R.layout.activity_main);

        Window window = this.getWindow();
        // clear FLAG_TRANSLUCENT_STATUS flag:
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // add FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS flag to the window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // finally change the color
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary_dark));


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Initialize the ViewPager and set an adapter
        ViewPager pager = findViewById(R.id.pager);
        pager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));

        // Bind the tabs to the ViewPager
        PagerSlidingTabStrip tabs = findViewById(R.id.tabs);
        tabs.setShouldExpand(true);
//        tabs.setDividerColor(R.color.white);
//        tabs.setUnderlineColor(R.color.primary_dark);
//        tabs.setTextColorResource(R.color.white);
        tabs.setDividerColorResource(R.color.pager_divider);
        tabs.setIndicatorHeight(15);
        //TODO Klikom na tabove da bude bele boje klik a ne plave

        tabs.setViewPager(pager);

        //TODO Ako je permission denied zabrani koriscenje usluga!
        AskPermission();

        Log.d(TAG, "onCreate: Ends");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.d(TAG, "onCreateOptionsMenu: Creating OptionsMenu");
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle presses on the action bar items
        switch (item.getItemId()){
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public class MyPagerAdapter extends FragmentPagerAdapter{

        private String[] titles = {"Record", "Saved Recordings"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
            Log.d(TAG, "MyPagerAdapter: MyPagerAdapter Constructor");
        }

        @Override
        public Fragment getItem(int position) {
            Log.d(TAG, "getItem: getItem(" + position + ")");
            switch (position){
                case 0:
                    return RecordFragment.newInstance(position);
                case 1:
                    return FileViewerFragment.newInstance(position);
            }
            return null;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

}
