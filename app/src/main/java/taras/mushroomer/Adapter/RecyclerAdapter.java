package taras.mushroomer.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import taras.mushroomer.Model.Mushroom;
import taras.mushroomer.R;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.CardViewHolder>{

    private static ArrayList<Mushroom> mMushroomList;
    private static Context mContext;

    public static class CardViewHolder extends RecyclerView.ViewHolder{
        public ImageView mImageView;
        public TextView mTextView;
        public CardView mCardView;

        public CardViewHolder(View view) {
            super(view);
            this.mCardView = view.findViewById(R.id.card_view);
            this.mImageView = view.findViewById(R.id.card_pic);
            this.mTextView = view.findViewById(R.id.card_text);
        }

    }

    public RecyclerAdapter(Context mContext, ArrayList<Mushroom> mMushroomList) {
        this.mMushroomList = mMushroomList;
        this.mContext = mContext;
    }

    @Override
    public CardViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater mInflater = LayoutInflater.from(viewGroup.getContext());
        ViewGroup mainGroup = (ViewGroup) mInflater.inflate(R.layout.recycler_card_item, viewGroup, false);
        CardViewHolder mainHolder = new CardViewHolder(mainGroup);
        return mainHolder;
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        final CardViewHolder cardViewHolder = holder;
        cardViewHolder.mImageView.setImageBitmap(mMushroomList.get(position).getImage());
        cardViewHolder.mTextView.setText(mMushroomList.get(position).getName());
    }


    @Override
    public int getItemCount() {
        return (null != mMushroomList ? mMushroomList.size() : 0);
    }

}