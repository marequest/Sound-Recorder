package doitgames.soundrecorder.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import doitgames.soundrecorder.R;
import doitgames.soundrecorder.RecordItem;
//TODO Stavi onPause, onStop, onStart, onDestroy da vidim kada se sta desava u apk na fonu
public class PlaybackFragment extends DialogFragment {
    private static final String TAG = "PlaybackFragment";
    private static final String ARGS_ITEM = "item";

    private RecordItem item;
    private long minutes;
    private long seconds;

    private SeekBar mSeekBar;
    private TextView mFileName;
    private TextView mRecordCurrentProgress;
    private TextView mRecordLength;
    private FloatingActionButton fabPlay;

    MediaPlayer mediaPlayer = null;
    private boolean isPlaying = false;

    Handler handler = new Handler();

    public static PlaybackFragment newInstance(RecordItem item){
        Log.d(TAG, "newInstance: putting Serializable item");
        PlaybackFragment f = new PlaybackFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(ARGS_ITEM, item);
        f.setArguments(bundle);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        item = (RecordItem) getArguments().getSerializable(ARGS_ITEM);
        minutes = TimeUnit.MILLISECONDS.toMinutes(item.getmLength());
        seconds = TimeUnit.MILLISECONDS.toSeconds(item.getmLength()) - TimeUnit.MINUTES.toSeconds(minutes);

        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog); //OVO
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        //OVO
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_media_playback, null, false); // You MUST have getActivity. // probaj false/true

        mFileName = view.findViewById(R.id.fileNameTextView);
        mRecordLength = view.findViewById(R.id.recordLengthTextView);
        mRecordCurrentProgress = view.findViewById(R.id.currentProgressTextView);

        mSeekBar = view.findViewById(R.id.seekBar);
        mSeekBar.setMax(item.getmLength());
        ColorFilter filter = new LightingColorFilter
                (getResources().getColor(R.color.primary), getResources().getColor(R.color.primary));
        mSeekBar.getProgressDrawable().setColorFilter(filter);
        mSeekBar.getThumb().setColorFilter(filter);
        // Vidi kako da se krece clean
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                    handler.removeCallbacks(runnable);


                    updateSeekBar();
                }
                long minutes = TimeUnit.MILLISECONDS.toMinutes(progress);
                long seconds = TimeUnit.MILLISECONDS.toSeconds(progress) - TimeUnit.MINUTES.toSeconds(minutes);
                mRecordCurrentProgress.setText(String.format("%02d:%02d", minutes,seconds));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if(mediaPlayer != null){
                    handler.removeCallbacks(runnable);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(mediaPlayer != null){
                    handler.removeCallbacks(runnable);
                    mediaPlayer.seekTo(seekBar.getProgress());

                    setCurrentProgressTextView();
                    updateSeekBar();
                }
                if(mediaPlayer == null) {
                    prepareMediaPlayerFromPoint(seekBar.getProgress());
                }
            }
        });

        fabPlay = view.findViewById(R.id.fabPlay);
        fabPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Log.d(TAG, "onClick: FAB clicked entered isPlaying = " + isPlaying);
                onPlay(isPlaying);
//                isPlaying = !isPlaying;
//                Log.d(TAG, "onClick: FAB clicked leaving isPlaying = " + isPlaying);
            }
        });

        mFileName.setText(item.getmName());
        mRecordLength.setText(String.format("%02d:%02d", minutes, seconds));

        builder.setView(view);

        return builder.create();
    }

    private void onPlay(boolean isPlaying){
        if(!isPlaying){
            // Play audio
            if(mediaPlayer == null){
                this.isPlaying = true;
                startPlaying();
            } else {
                this.isPlaying = true;
                resumePlaying();
            }
        } else {
            this.isPlaying = false;
            pausePlaying();
        }
    }

    private void startPlaying(){
        fabPlay.setImageResource(R.drawable.baseline_pause_circle_outline_black_48dp);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                fabPlay.setImageResource(R.drawable.outline_play_arrow_black_48dp);
                mSeekBar.setProgress(mSeekBar.getMax());
                mRecordCurrentProgress.setText(mRecordLength.getText());
                stopPlayer();
            }
        });

        try {
            mediaPlayer.setDataSource(item.getmFilePath());
            mediaPlayer.prepare();
//            mSeekBar.setMax(mediaPlayer.getDuration());
            mediaPlayer.start();

            handler.post(runnable);//dodato
//            updateSeekBar();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    private void pausePlaying(){
        fabPlay.setImageResource(R.drawable.outline_play_arrow_black_48dp);
        if(mediaPlayer != null){
            mediaPlayer.pause();
            handler.removeCallbacks(runnable);
        }
    }

    private void resumePlaying(){
        fabPlay.setImageResource(R.drawable.baseline_pause_circle_outline_black_48dp);
        handler.removeCallbacks(runnable);
        mediaPlayer.start();
//        isPlaying = true;
//        Log.d(TAG, "resumePlaying: isPlaying = " + isPlaying);
        updateSeekBar();
    }

    private void stopPlayer(){
        if(mediaPlayer != null){
            isPlaying = false;
//            Log.d(TAG, "stopPlayer: isPlaying = " + isPlaying);
            handler.removeCallbacks(runnable);

            mediaPlayer.release();
            mediaPlayer = null;
//            Toast.makeText(getActivity(), "MediaPlayer released", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        stopPlayer();
    }

    private void prepareMediaPlayerFromPoint(int progress){
        mediaPlayer = new MediaPlayer();
        try{
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    fabPlay.setImageResource(R.drawable.outline_play_arrow_black_48dp);
                    mSeekBar.setProgress(mSeekBar.getMax());
                    mRecordCurrentProgress.setText(mRecordLength.getText());
                    stopPlayer();
                }
            });
            mediaPlayer.setDataSource(item.getmFilePath());
            mediaPlayer.prepare();
            mediaPlayer.seekTo(progress);
//            mSeekBar.setMax(mediaPlayer.getDuration());
            setCurrentProgressTextView();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            int currentAudioPosition = mediaPlayer.getCurrentPosition();
            mSeekBar.setProgress(currentAudioPosition);

            setCurrentProgressTextView();

            updateSeekBar();
        }
    };

    private void updateSeekBar(){
        handler.postDelayed(runnable, 1000);
    }

    private void setCurrentProgressTextView(){
        int currentAudioPosition = mediaPlayer.getCurrentPosition();

        long minutes = TimeUnit.MILLISECONDS.toMinutes(currentAudioPosition);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(currentAudioPosition) - TimeUnit.MINUTES.toSeconds(minutes);
        mRecordCurrentProgress.setText(String.format("%02d:%02d", minutes,seconds));
    }
}










