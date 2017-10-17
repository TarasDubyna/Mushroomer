package taras.mushroomer.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import taras.mushroomer.InfoMushroomActivity;
import taras.mushroomer.Model.Mushroom;
import taras.mushroomer.R;

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.CardViewHolder> {

    private ArrayList<Mushroom> mMushroomList;
    private Context mContext;
    private int position;


    public static class CardViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView mImageView;
        public TextView mTextView;
        public CardView mCardView;
        public String typeM;

        public CardViewHolder(View view, String typeM) {
            super(view);
            this.typeM = typeM;
            this.mCardView = view.findViewById(R.id.card_view);
            this.mImageView = view.findViewById(R.id.card_pic);
            this.mTextView = view.findViewById(R.id.card_text);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Intent intent = new Intent(view.getContext(), InfoMushroomActivity.class);
            intent.putExtra("mushroomType", typeM);
            intent.putExtra("mushroomName", mTextView.getText().toString());
            view.getContext().startActivity(intent);
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
        return new CardViewHolder(mainGroup, mMushroomList.get(0).getType());
    }

    @Override
    public void onBindViewHolder(CardViewHolder holder, int position) {
        holder.mImageView.setImageDrawable(mContext.getDrawable(mMushroomList.get(position).getImageDir()));
        holder.mTextView.setText(mMushroomList.get(position).getName());
        this.position = position;
    }

    @Override
    public int getItemCount() {
        return (null != mMushroomList ? mMushroomList.size() : 0);
    }

}