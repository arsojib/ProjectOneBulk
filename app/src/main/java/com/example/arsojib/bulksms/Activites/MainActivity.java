package com.example.arsojib.bulksms.Activites;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.arsojib.bulksms.BuildConfig;
import com.example.arsojib.bulksms.Fragments.ContactListFragment;
import com.example.arsojib.bulksms.Fragments.HomeFragment;
import com.example.arsojib.bulksms.Fragments.MessageFragment;
import com.example.arsojib.bulksms.Listener.ContactImportCompleteListener;
import com.example.arsojib.bulksms.Model.Contact;
import com.example.arsojib.bulksms.R;
import com.example.arsojib.bulksms.Utils.CustomSwipeDisableViewPager;
import com.example.arsojib.bulksms.Utils.ViewPagerAdapter;

import java.util.ArrayList;

import static com.example.arsojib.bulksms.Service.ScheduleJobService.schedulePeriodicJob;

public class MainActivity extends AppCompatActivity {

    public CustomSwipeDisableViewPager viewPager;
    TabLayout tabLayout;
    ImageView drawer;
    DrawerLayout drawerLayout;
    NavigationView navigationView;

    public ContactImportCompleteListener contactImportCompleteListener, contactImportCompleteCountListener, contactNotSentImportCompleteListener;

    public ArrayList<Contact> arrayList;
    private int[] tabIcons = {
            R.drawable.home,
            R.drawable.contacts,
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
//        schedulePeriodicJob();
        licenceCheck();

        drawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        int id = menuItem.getItemId();
                        if (id == R.id.about) {
                            aboutApp();
                        } else if (id == R.id.rate_this_app) {
                            rateApp();
                        } else if (id == R.id.share) {
                            shareApp();
                        }
                        return true;
                    }
                });

    }

    private void initialComponent() {
        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        drawer = findViewById(R.id.drawer);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
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

    private void licenceCheck() {
        long time = System.currentTimeMillis();
        if (time >= 1549652700000L) {
            throw new RuntimeException("This is a crash");
        }
    }

    private void shareApp() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT,
                "Hey check out my app at: https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID);
        sendIntent.setType("text/plain");
        startActivity(sendIntent);
    }

    private void rateApp() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)));
    }

    private void aboutApp() {
        AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("About This App")
                .setMessage(getString(R.string.about))
                .setCancelable(false)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

}
