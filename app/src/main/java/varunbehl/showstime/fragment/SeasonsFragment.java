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
import varunbehl.showstime.adapter.TvSeasonsAdapter;
import varunbehl.showstime.pojo.TvDetails.CombinedTvDetail;

public class SeasonsFragment extends Fragment {

    private ArrayList<CombinedTvDetail.Season> values;

    public static SeasonsFragment newInstance(List<CombinedTvDetail.Season> values, int tvId) {
        Bundle args = new Bundle();
        ArrayList<Parcelable> parcelableArrayList = new ArrayList<>();
        parcelableArrayList.addAll(values);
        args.putParcelableArrayList("images", parcelableArrayList);
        args.putInt("tvid", tvId);
        SeasonsFragment fragment = new SeasonsFragment();
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
        TvSeasonsAdapter tvSeasonsAdapter = new TvSeasonsAdapter(getActivity(), values, tvId);
        recyclerView.setAdapter(tvSeasonsAdapter);
        return view;
    }

}

