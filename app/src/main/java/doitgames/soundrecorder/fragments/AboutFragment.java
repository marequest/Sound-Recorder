package doitgames.soundrecorder.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import doitgames.soundrecorder.R;

public class AboutFragment extends DialogFragment {

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        View aboutDialog = getActivity().getLayoutInflater().inflate(R.layout.fragment_about, null);
        return new AlertDialog.Builder(getActivity())
                .setTitle("About SoundRecorder")
                .setView(aboutDialog)
                .create();

    }
}
