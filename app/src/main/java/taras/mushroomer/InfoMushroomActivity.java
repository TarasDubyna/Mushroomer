package taras.mushroomer;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import taras.mushroomer.DB.DatabaseHelper;
import taras.mushroomer.model.Mushroom;

public class InfoMushroomActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private static ArrayList<Mushroom> mMushroomList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_mushroom);

        String nameMushroom = getIntent().getStringExtra("mushroomName");
        String typeMushroom = getIntent().getStringExtra("mushroomType");

        DatabaseHelper databaseHelper = new DatabaseHelper(this);
        mMushroomList = databaseHelper.getMushroomsByType(typeMushroom);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setCurrentItem(getItemPosition(nameMushroom));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private int getItemPosition(String mushroomName){
        int i = 0;
        for (i = 0; i < mMushroomList.size(); i++){
            if (mMushroomList.get(i).getName().equals(mushroomName)){
                return i;
            }
        }
        return 0;
    }

    public static class PlaceholderFragment extends Fragment {

        private static final String ARG_SECTION_NUMBER = "section_number";
        private static Context mContext;


        public PlaceholderFragment() {
        }
        public static PlaceholderFragment newInstance(int sectionNumber, Context context) {
            mContext = context;
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_info_mushroom, container, false);

            ImageView imageView = rootView.findViewById(R.id.info_card_image);
            TextView nameTextView = rootView.findViewById(R.id.info_card_name);
            TextView nameLatTextView = rootView.findViewById(R.id.info_card_name_lat);
            TextView descriptionTextView = rootView.findViewById(R.id.info_card_description);

            int position = getArguments().getInt(ARG_SECTION_NUMBER) - 1;
            imageView.setImageDrawable(mContext.getDrawable(mMushroomList.get(position).getImageDir()));
            nameTextView.setText(mMushroomList.get(position).getName());
            nameLatTextView.setText("лат. " + mMushroomList.get(position).getNameLat());
            descriptionTextView.setText(mMushroomList.get(position).getDescription());

            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1, getApplicationContext());
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return mMushroomList.size();
        }



    }
}
