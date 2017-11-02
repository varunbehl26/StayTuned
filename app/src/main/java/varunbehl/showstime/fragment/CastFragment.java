package varunbehl.showstime.fragment;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import varunbehl.showstime.R;
import varunbehl.showstime.adapter.TvCastAdapter;
import varunbehl.showstime.pojo.Cast.Cast;

public class CastFragment extends Fragment {

    private ArrayList<Cast> values;

    public static CastFragment newInstance(List<Cast> values, int tvId) {
        Bundle args = new Bundle();
        ArrayList<Parcelable> parcelableArrayList = new ArrayList<>();
        parcelableArrayList.addAll(values);
        args.putParcelableArrayList("images", parcelableArrayList);
        args.putInt("tvid", tvId);
        CastFragment fragment = new CastFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_image, container, false);
        Bundle arguments = getArguments();
        values = arguments.getParcelableArrayList("images");
        int tvId = arguments.getInt("tvid");

        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        GridLayoutManager staggeredGridLayoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);
        TvCastAdapter tvCastAdapter = new TvCastAdapter(getContext(), values, tvId);
        recyclerView.setAdapter(tvCastAdapter);
        return view;
    }

}

