package taras.mushroomer.dialog;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import taras.mushroomer.DB.DatabaseHelper;
import taras.mushroomer.R;
import taras.mushroomer.interfaces.DeleteMushroomMarker;
import taras.mushroomer.model.MarkerMushroomPair;
import taras.mushroomer.model.Mushroom;


public class DialogInfoMarker extends DialogFragment{

    MarkerMushroomPair mMarkerMushroomPair;

    ImageView imageView;
    TextView nameText;
    TextView typeText;

    Button deleteButton;
    Button updateButton;



    public DialogInfoMarker() {
    }

    public static DialogInfoMarker newInstance(){
        DialogInfoMarker dialogFragment = new DialogInfoMarker();
        return dialogFragment;

    }

    public void setMarkerMushroomPair(MarkerMushroomPair mMarkerMushroomPair){
        this.mMarkerMushroomPair = mMarkerMushroomPair;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return inflater.inflate(R.layout.dialog_marker_mushroom_info, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        imageView = view.findViewById(R.id.marker_info_window_pic);
        nameText = view.findViewById(R.id.marker_info_window_name);
        typeText = view.findViewById(R.id.marker_info_window_type);
        deleteButton = view.findViewById(R.id.marker_info_window_delete_button);
        updateButton = view.findViewById(R.id.marker_info_window_update_button);

        String name = mMarkerMushroomPair.getMushroom().getName();
        String type = mMarkerMushroomPair.getMushroom().getType();
        int imageDir = mMarkerMushroomPair.getMushroom().getImageDir();

        nameText.setText(name);
        typeText.setText(type);
        imageView.setImageDrawable(getResources().getDrawable(imageDir));
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DeleteMushroomMarker deleteMushroomMarker = (DeleteMushroomMarker) getActivity();
                deleteMushroomMarker.deleteMushroomMarker(true);
                getDialog().dismiss();
            }
        });

        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseHelper databaseHelper = new DatabaseHelper(view.getContext());
                ArrayList<ArrayList<Mushroom>> mushroomList = databaseHelper.getAllMushrooms();
                DialogPutMushroom dialogPutMushroom = DialogPutMushroom.newInstance(mushroomList, mMarkerMushroomPair);
                getDialog().dismiss();
                dialogPutMushroom.show(getFragmentManager(), "UpdateMushroom");
            }
        });
    }

}
