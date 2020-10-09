package doitgames.soundrecorder;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class RecordingService extends Service {
    private static final String TAG = "RecordingService";

    private AppDatabase mDatabase;
    
    private MediaRecorder mMediaRecorder = null;
    private String mFileName;
    private String mFilePath;
    private long mStartingTimeMillis;
    private long mElapsedTimeMillis;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        mDatabase = AppDatabase.getInstance(this);
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        stopRecording();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand: Start");
        startRecording();
        return Service.START_STICKY;
    }

    private void startRecording(){
        Log.d(TAG, "startRecording: Recording started...");
        setFileNameAndPath();

        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        mMediaRecorder.setOutputFile(mFilePath);
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mMediaRecorder.setAudioChannels(1);
        if (MySharedPreferences.getPrefHighQuality(this)) {
            mMediaRecorder.setAudioSamplingRate(44100);
            mMediaRecorder.setAudioEncodingBitRate(192000);
        }
        try{
            mMediaRecorder.prepare();
            mMediaRecorder.start();
            mStartingTimeMillis = System.currentTimeMillis();

            //TODO foreground service and notifications
            //startTimer();
            //startForeground(1, createNotification());
            Log.d(TAG, "startRecording: Recording started with file path " + mFilePath);
        } catch (IOException e){
            Log.d(TAG, "startRecording: MediaRecorder prepare failed!");
            e.printStackTrace();
        }
    }

    private void stopRecording(){
        Log.d(TAG, "stopRecording: Stopping recording.");

        mMediaRecorder.stop();
        mMediaRecorder.release();
        mElapsedTimeMillis = (System.currentTimeMillis() - mStartingTimeMillis);
        Toast.makeText(this, "Recording saved to " + mFilePath, Toast.LENGTH_LONG).show();

        try{
            mDatabase.addRecording(mFileName, mFilePath, mElapsedTimeMillis);
            Log.d(TAG, "stopRecording: Successfully added recording to the database");
        } catch (Exception e){
            Log.e(TAG, "stopRecording: Failed to add recording to database");
        }
    }

    private void setFileNameAndPath(){
        File f;
        int count = 0;
        do {
            count++;
            mFileName = "MyRecording" + "_" + (mDatabase.getCount() + count) + ".mp4";
            mFilePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            mFilePath += "/MySoundRecorder/" + mFileName;
            f = new File(mFilePath);
        } while(f.exists() && !f.isDirectory()); // Not sure why is it in while loop
        if(f.getParentFile().mkdirs()){
            Log.d(TAG, "setFileNameAndPath: f.mkdir returned true");
        } else {
            Log.d(TAG, "setFileNameAndPath: f.mkdir returned false");
        }
    }

}
