package doitgames.soundrecorder.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import doitgames.soundrecorder.R;
import doitgames.soundrecorder.listener.RecyclerItemClickListener;
import doitgames.soundrecorder.adapters.FileViewerAdapter;
//TODO U listi se prikazuju itemi koji su sacuvani u database a ostali koji su u file od proslog installa se ne prikazuju
public class FileViewerFragment extends Fragment implements RecyclerItemClickListener.OnRecyclerClickListener {
    private static final String TAG = "FileViewerFragment";
    public static final String ARG_POSITION = "position";

    private int position;
    FileViewerAdapter fileViewerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    public static FileViewerFragment newInstance(int position) {
        FileViewerFragment f = new FileViewerFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);

        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_file_viewer, container, false);

        fileViewerAdapter = new FileViewerAdapter(this.getContext());

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this.getContext(), recyclerView, this));
        recyclerView.setAdapter(fileViewerAdapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        return view;
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick: Click.");
        if(fileViewerAdapter != null)
            fileViewerAdapter.onItemClick(view, position);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick: Long Click.");
        if(fileViewerAdapter != null)
            fileViewerAdapter.onLongItemClick(view, position);
    }
}
