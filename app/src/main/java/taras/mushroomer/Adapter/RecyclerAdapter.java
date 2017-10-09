package taras.mushroomer.Adapter;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import taras.mushroomer.Model.Mushroom;
import taras.mushroomer.R;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private ArrayList<Mushroom> mMushroomList;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView mImageView;
        public TextView mTextView;

        public ViewHolder(View v) {
            super(v);
            mImageView = (ImageView) v.findViewById(R.id.card_pic);
            mTextView = (TextView) v.findViewById(R.id.card_text);
        }
    }

    public RecyclerAdapter(ArrayList<Mushroom> mMushroomList) {
        this.mMushroomList = mMushroomList;
    }

    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_card_item, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mImageView.setImageBitmap(mMushroomList.get(position).getImage());
        holder.mTextView.setText(mMushroomList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mMushroomList.size();
    }

}