package com.example.arsojib.bulksms.Activites;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.arsojib.bulksms.Fragments.ContactListFragment;
import com.example.arsojib.bulksms.Fragments.HomeFragment;
import com.example.arsojib.bulksms.Fragments.MessageFragment;
import com.example.arsojib.bulksms.Listener.ContactImportCompleteListener;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.R;
import com.example.arsojib.bulksms.Utils.CustomSwipeDisableViewPager;
import com.example.arsojib.bulksms.Utils.ViewPagerAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public CustomSwipeDisableViewPager viewPager;
    TabLayout tabLayout;

    public ContactImportCompleteListener contactImportCompleteListener, contactImportCompleteCountListener;

    public ArrayList<Contact> arrayList;
    private int[] tabIcons = {
            R.drawable.home_white,
            R.drawable.list_white,
            R.drawable.message_white
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initialComponent();
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);
        setupTabIcons();
    }

    private void initialComponent() {
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter.mFragmentList.clear();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HomeFragment());
        adapter.addFragment(new ContactListFragment());
        adapter.addFragment(new MessageFragment());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
    }

    private void setupTabIcons() {
        try {
            tabLayout.getTabAt(0).setIcon(tabIcons[0]);
            tabLayout.getTabAt(1).setIcon(tabIcons[1]);
            tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        } catch (RuntimeException ignored) {
        }
    }

}
