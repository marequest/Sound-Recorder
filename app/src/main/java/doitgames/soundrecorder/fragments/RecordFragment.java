package doitgames.soundrecorder.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import doitgames.soundrecorder.MyPermissions;
import doitgames.soundrecorder.R;
import doitgames.soundrecorder.RecordingService;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {
    private static final String TAG = "RecordFragment";
    public static final String ARG_POSITION = "position";
    
    private int position;
    private boolean mStartRecording = true;
    private int mRecordPromptCount = 0;

    private TextView mRecordingStatus;
    private FloatingActionButton mRecordingButtonFab;
    private Button mPauseButton;
    private Chronometer mChronometer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View recordView = inflater.inflate(R.layout.fragment_record, container, false);

        mRecordingStatus = recordView.findViewById(R.id.recording_status_text);
        mChronometer = recordView.findViewById(R.id.chronometer);

        mRecordingButtonFab = recordView.findViewById(R.id.fab);
        mRecordingButtonFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnRecord(mStartRecording);
                mStartRecording = !mStartRecording;
            }
        });

        mPauseButton = recordView.findViewById(R.id.btnPause);

        mPauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Pause Button Clicked", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        return recordView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    public static Fragment newInstance(int position){
        RecordFragment fragment = new RecordFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        fragment.setArguments(b);
        return fragment;
    }

    private void OnRecord(boolean startRecording){
        //TODO Permission - change this
//        if(!MyPermissions.getInstance().permissionToRecordAccepted){
////            Toast.makeText(getActivity(), "You don't have permission to record!", Toast.LENGTH_LONG).show();
//            MyPermissions.getInstance().AskAgainForPermission();
//            return;
//        }

        Intent serviceIntent = new Intent(getActivity(), RecordingService.class);

        if(startRecording){

            mRecordingButtonFab.setImageResource(R.drawable.baseline_stop_black_48dp);

            Toast.makeText(getActivity(), "Recording Started", Toast.LENGTH_SHORT).show();
            // TODO File nesto

            mChronometer.setBase(SystemClock.elapsedRealtime());
            mChronometer.start();
            mChronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                @Override
                public void onChronometerTick(Chronometer chronometer) {
                    if(mRecordPromptCount == 0){
                        mRecordingStatus.setText("Recording.");
                    } else if(mRecordPromptCount == 1){
                        mRecordingStatus.setText("Recording..");
                    } else if(mRecordPromptCount == 2){
                        mRecordingStatus.setText("Recording...");
                        mRecordPromptCount = -1;
                    }
                    mRecordPromptCount++;
                }
            });

            getActivity().startService(serviceIntent);

            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        } else {
            mRecordingButtonFab.setImageResource(R.drawable.round_mic_black_48dp);

            mChronometer.stop();
            mChronometer.setBase(SystemClock.elapsedRealtime());

            mRecordingStatus.setText("Tap the button to start recording!");

            getActivity().stopService(serviceIntent);

            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }

    //TODO Implement pause recording
//    private void OnPauseRecord(){
//
//    }
}


















