package varunbehl.showstime.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import varunbehl.showstime.R;
import varunbehl.showstime.adapter.ImageAdapter;

public class ImageFragment extends Fragment {

    private ArrayList<String> values;

    public static ImageFragment newInstance(ArrayList<String> values) {
        Bundle args = new Bundle();
        args.putStringArrayList("images", values);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        Bundle arguments = getArguments();
        values = arguments.getStringArrayList("images");
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2,
                StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        ImageAdapter rcAdapter = new ImageAdapter(getContext(), values);
        recyclerView.setAdapter(rcAdapter);
        return view;
    }

}

