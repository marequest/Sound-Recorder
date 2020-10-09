package doitgames.soundrecorder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.Snackbar;

import doitgames.soundrecorder.activites.MainActivity;
//TODO Permissions - change this
public class MyPermissions extends MainActivity {
    private static final String TAG = "MyPermissions";
    // Requesting permission to RECORD_AUDIO
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    public boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static MyPermissions instance = null;

    public static MyPermissions getInstance(){
        if(instance == null){
            instance = new MyPermissions();
        }
        return instance;
    }

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
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                Log.d(TAG, "onRequestPermissionsResult: grantResults.length = " + grantResults.length);
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
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

    public void AskPermission() {
        if (!hasPermissions(this, permissions)) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        }
    }

    public void AskAgainForPermission() {
        Snackbar mySnackbar = Snackbar.make(findViewById(R.id.main_activity), "Recording and listening to audio requires permissions!", Snackbar.LENGTH_INDEFINITE);
        mySnackbar.setAction("Ask Permissions", new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AskPermission();
            }
        });
        mySnackbar.show();
    }
}
