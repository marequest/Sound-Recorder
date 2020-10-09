package doitgames.soundrecorder.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.drm.DrmStore;
import android.net.Uri;
import android.os.Environment;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.FileProvider;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import doitgames.soundrecorder.AppDatabase;
import doitgames.soundrecorder.R;
import doitgames.soundrecorder.RecordItem;
import doitgames.soundrecorder.fragments.FileViewerFragment;
import doitgames.soundrecorder.fragments.PlaybackFragment;

public class FileViewerAdapter extends RecyclerView.Adapter<FileViewerAdapter.RecordingsViewHolder>
        implements AppDatabase.OnDatabaseChangedListener {
    private static final String TAG = "FileViewerAdapter";
    public static final String PLAYBACK_DIALOG_TAG = "dialog_playback";
    
    AppDatabase mAppDatabase;
    Context mContext;

    public FileViewerAdapter(Context context){
        mContext = context;
        mAppDatabase = AppDatabase.getInstance(mContext);
        mAppDatabase.setOnDatabaseChangedListener(this);
    }

    @NonNull
    @Override
    public FileViewerAdapter.RecordingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recording_card_view, parent, false);
        return new RecordingsViewHolder(view);
    }

    public class RecordingsViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        CardView cardView;
        TextView fileNameText;
        TextView fileLengthText;
        TextView fileDateText;

        public RecordingsViewHolder(@NonNull View itemView) {
            super(itemView);
            Log.d(TAG, "RecordingsViewHolder: Create");
            cardView = itemView.findViewById(R.id.cardView);
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //This is just for the animation
                }
            });
            fileNameText = itemView.findViewById(R.id.fileNameText);
            fileLengthText = itemView.findViewById(R.id.fileLengthText);
            fileDateText = itemView.findViewById(R.id.fileDateText);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewerAdapter.RecordingsViewHolder holder, int position) {
        RecordItem item = mAppDatabase.getItemAt(position);

        long minutes = TimeUnit.MILLISECONDS.toMinutes(item.getmLength());
        long seconds = TimeUnit.MILLISECONDS.toSeconds(item.getmLength()) - TimeUnit.MINUTES.toSeconds(minutes);

        holder.fileNameText.setText(item.getmName());
        holder.fileLengthText.setText(String.format("%02d:%02d", minutes, seconds));
        holder.fileDateText.setText(
                DateUtils.formatDateTime(
                        mContext,
                        item.getmTime(),
                        DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_NUMERIC_DATE | DateUtils.FORMAT_SHOW_TIME | DateUtils.FORMAT_SHOW_YEAR
                )
        );
    }

    @Override
    public int getItemCount() {
        return mAppDatabase.getCount();
    }

    @Override
    public void onDatabaseEntryAdded() {
        notifyItemInserted(getItemCount() - 1);
    }

    @Override
    public void onDatabaseEntryRenamed() {
        notifyDataSetChanged();
    }

    public void onItemClick(View view, int position){
        RecordItem item = mAppDatabase.getItemAt(position);
        if(item == null){
            Log.e(TAG, "onItemClick: FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF");
        }
        PlaybackFragment playbackFragment = PlaybackFragment.newInstance(item);
        FragmentTransaction fragmentTransaction = ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction();
        playbackFragment.show(fragmentTransaction, PLAYBACK_DIALOG_TAG);
    }

    public void onLongItemClick(View view, final int position){
        List<String> entrys = new ArrayList<>();
        entrys.add("Share");
        entrys.add("Rename");
        entrys.add("Delete");

        final CharSequence[] items = entrys.toArray(new CharSequence[entrys.size()]);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Options - " + mAppDatabase.getItemAt(position).getmName());
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int item) {
                if(item == 0){
                    shareFileDialog(position);
                } else if(item == 1){
                    renameFileDialog(position);
                } else if(item == 2){
                    deleteFileDialog(position);
                }
            }
        });
        builder.setCancelable(true);
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        builder.create().show();
    }

    void shareFileDialog(int position){
        /*Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(mAppDatabase.getItemAt(position).getmFilePath())));
        shareIntent.setType("audio/mp4");
        mContext.startActivity(Intent.createChooser(shareIntent, "Send to"));*/
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        Uri imageUri = FileProvider.getUriForFile(mContext, "doitgames.soundrecorder.fileprovider", new File(mAppDatabase.getItemAt(position).getmFilePath()));
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.setType("audio/mp4");
        mContext.startActivity(Intent.createChooser(shareIntent, "Send to"));
    }

    void renameFileDialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View renameView = layoutInflater.inflate(R.layout.rename_file_dialog, null);

        final EditText editText = renameView.findViewById(R.id.newName);

        builder.setTitle("Rename")
                .setView(renameView)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        boolean cancelDialog = true;
                        String newName = editText.getText().toString().trim();
                        if(newName.length() > 0){
                            cancelDialog = rename(position, newName);
//                            cancelDialog = rename(position, newName.replaceAll("\\s+",""));
                        }
                        if(cancelDialog){
                            dialogInterface.cancel();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.create().show();
    }

    void deleteFileDialog(final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("Confirm deletion")
                .setMessage("Are you sure you want to delete this item?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        delete(position);
                        dialogInterface.cancel();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
        builder.create().show();
    }

    //Promeni u boolean
    boolean rename(int position, String newName){
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        filePath += "/MySoundRecorder/" + newName + ".mp4";
        File f = new File(filePath);
        if(f.exists() && !f.isDirectory()){
            Toast.makeText(mContext, String.format("The file %1$s already exists. Please choose different name.", newName), Toast.LENGTH_LONG).show();
            return false;
        } else {
            RecordItem item = mAppDatabase.getItemAt(position);
            File oldFilePath = new File(item.getmFilePath());
            if(!oldFilePath.renameTo(f)){
                Log.e(TAG, "rename: ERROR renaming a RecordItem" );
            }
            mAppDatabase.renameItem(item, newName, filePath);
            notifyDataSetChanged();
            Log.d(TAG, "rename: Successfully renamed an item!");
            return true;
        }
    }

    void delete(int position){
        // Remove item from storage, database and notify recyclerView
        RecordItem item = mAppDatabase.getItemAt(position);
        File file = new File(item.getmFilePath());
        if(file.delete()){
            Toast.makeText(mContext, item.getmName() + " has been deleted.", Toast.LENGTH_SHORT).show(); // TODO Snackbar za undo
        } else {
            Log.e(TAG, "delete: FAILED deleting item from file storage");
        }

        mAppDatabase.removeItemWithId(item.getmId());
        notifyDataSetChanged();
    }
}












