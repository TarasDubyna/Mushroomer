package taras.mushroomer;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import taras.mushroomer.DB.DatabaseHelper;
import taras.mushroomer.fragment.MushroomListFragment;
import taras.mushroomer.model.Mushroom;

public class MushroomListsActivity extends AppCompatActivity {


    private ViewPager viewPager;
    private TabLayout tabLayout;
    private Toolbar toolbar;
    private ArrayList<ArrayList<Mushroom>> mushroomArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mushroom_lists);

        if (mushroomArrayList == null) {
            mushroomArrayList = new ArrayList<>();
            DatabaseHelper databaseHelper = new DatabaseHelper(this);
            for (int i = 0; i < typeNames.length; i++) {
                mushroomArrayList.add(databaseHelper.getMushroomsByType(getTypeBySection(i)));
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        viewPager = (ViewPager) findViewById(R.id.container);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    static String[] typeNames = {"Съедобные", "Условно-съедобные", "Несъедобные"};

    private static String getTypeBySection(int section) {
        return typeNames[section];
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(MushroomListFragment.newInstance("Съедобные"), "Съедобные");
        adapter.addFragment(MushroomListFragment.newInstance("Условно-съедобные"), "Условно-съедобные");
        adapter.addFragment(MushroomListFragment.newInstance("Несъедобные"), "Несъедобные");
        viewPager.setAdapter(adapter);
    }


    private static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
