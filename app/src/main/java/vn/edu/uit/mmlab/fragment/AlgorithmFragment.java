package vn.edu.uit.mmlab.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import vn.edu.uit.mmlab.R;

public class AlgorithmFragment extends Fragment {

    private static final String ARG_POSITION = "position";
    private int position;

    public AlgorithmFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_algorithm,container,false);
        ViewCompat.setElevation(rootView, 50);
        return rootView;
    }

    public static AlgorithmFragment newInstance(int position) {
        AlgorithmFragment f = new AlgorithmFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

}
