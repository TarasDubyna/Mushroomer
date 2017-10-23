package taras.mushroomer.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ExpandableListView;

import java.util.ArrayList;

import taras.mushroomer.R;
import taras.mushroomer.adapter.ExpandableListViewAdapter;
import taras.mushroomer.model.Mushroom;

public class DialogPutMushroom extends DialogFragment {

    public ExpandableListView mExpandableListView;
    private static ArrayList<ArrayList<Mushroom>> mMushroomList;

    public DialogPutMushroom() {
    }

    public interface GetMushroomItem{
        void onFinishDialog(Mushroom mushroom);
    }

    public static DialogPutMushroom newInstance(String title, ArrayList<ArrayList<Mushroom>> list){
        mMushroomList = list;
        DialogPutMushroom dialogFragment = new DialogPutMushroom();
        Bundle args = new Bundle();
        args.putString("title", title);
        dialogFragment.setArguments(args);
        return dialogFragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_put_mushroom_window, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mExpandableListView = view.findViewById(R.id.dialog_expandableListView);
        ExpandableListViewAdapter expandableListViewAdapter = new ExpandableListViewAdapter(getActivity(), mMushroomList);
        mExpandableListView.setAdapter(expandableListViewAdapter);
        for (int i = 0; i < 3; i++){
            mExpandableListView.expandGroup(i);
        }
        mExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                GetMushroomItem getMushroomItem = (GetMushroomItem)getActivity();
                getMushroomItem.onFinishDialog(mMushroomList.get(i).get((int) l));
                return false;
            }
        });
        mExpandableListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                GetMushroomItem getMushroomItem = (GetMushroomItem)getActivity();
                getMushroomItem.onFinishDialog(mMushroomList.get(i).get((int) l));
            }
        });
    }
}
