package taras.mushroomer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import taras.mushroomer.DB.DatabaseHelper;
import taras.mushroomer.R;
import taras.mushroomer.model.Mushroom;

public class ExpandableListViewAdapter extends BaseExpandableListAdapter {

    private ArrayList<ArrayList<Mushroom>> mMushroomList;
    private Context mContext;

    public ExpandableListViewAdapter(Context mContext, ArrayList<ArrayList<Mushroom>> mMushroomList) {
        this.mContext = mContext;
        this.mMushroomList = mMushroomList;
    }

    @Override
    public int getGroupCount() {
        return mMushroomList.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return mMushroomList.get(i).size();
    }

    @Override
    public Object getGroup(int i) {
        return mMushroomList.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return mMushroomList.get(i).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.dialog_expandable_group, null);
        }
        TextView textGroup = (TextView) convertView.findViewById(R.id.list_group_text);
        textGroup.setText(mMushroomList.get(groupPosition).get(0).getType());

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {
        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.dialog_expandable_item, null);
        }

        ImageView mushroomImageView = convertView.findViewById(R.id.dialog_expandableListView_item_image);
        TextView mushroomNameText = convertView.findViewById(R.id.dialog_expandableListView_item_text);

        mushroomImageView.setImageDrawable(mContext.getDrawable(mMushroomList.get(groupPosition).get(childPosition).getImageDir()));
        mushroomNameText.setText(mMushroomList.get(groupPosition).get(childPosition).getName());

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
