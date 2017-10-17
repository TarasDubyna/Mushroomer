package taras.mushroomer.Fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Arrays;

import taras.mushroomer.Adapter.RecyclerAdapter;
import taras.mushroomer.DB.DatabaseHelper;
import taras.mushroomer.Model.Mushroom;
import taras.mushroomer.R;

public class MushroomListFragment extends Fragment {

    public static final String ARGUMENT_MASHROOM_TYPE = "mashroom_type";
    private String type;

    private View view;

    private RecyclerView recyclerView;

    public static MushroomListFragment newInstance(String mashroomType) {
        MushroomListFragment fragment = new MushroomListFragment();
        // Supply index input as an argument.
        Bundle args = new Bundle();
        args.putString(ARGUMENT_MASHROOM_TYPE, mashroomType);
        fragment.setArguments(args);
        return fragment;
    }

    private int getColor(String type){
        switch (type) {
            case "Съедобные":
                return R.color.green_dark;
            case "Условно-съедобные":
                return R.color.orange;
            case "Несъедобные":
                return R.color.red_dark;
        }
        return 0;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        // Get arguments
        parseArguments();
        view = inflater.inflate(R.layout.fragment_main, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.recycle_view_main);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        DatabaseHelper databaseHelper = new DatabaseHelper(getActivity());
        ArrayList<Mushroom> mushrooms = databaseHelper.getAllMushroomsByType(type);
        Log.e("MM", "GOT MASHROOMS " + Arrays.toString(mushrooms.toArray()));
        RecyclerAdapter adapter = new RecyclerAdapter(getActivity(), mushrooms);

        recyclerView.setAdapter(adapter);
    }

    private void parseArguments() {
        Bundle arguments = getArguments();
        this.type = arguments.getString(ARGUMENT_MASHROOM_TYPE);
    }
}
