package taras.mushroomer;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import taras.mushroomer.Adapter.RecyclerAdapter;
import taras.mushroomer.DB.DatabaseHelper;
import taras.mushroomer.Model.Mushroom;

public class MushroomListFragment extends Fragment {

    private String type;

    private View view;

    private RecyclerView recyclerView;

    public MushroomListFragment() {
    }

    public MushroomListFragment(String type) {
        this.type = type;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_main, container, false);
        recyclerView = view.findViewById(R.id.recycle_view_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        ArrayList<Mushroom> mushrooms = databaseHelper.getAllMushroomsByType(type);
        RecyclerAdapter adapter = new RecyclerAdapter(getActivity(), mushrooms);
        recyclerView.setAdapter(adapter);
        return view;
    }

}
