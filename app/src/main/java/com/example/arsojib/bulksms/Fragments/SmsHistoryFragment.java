package com.example.arsojib.bulksms.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.arsojib.bulksms.R;
import com.example.arsojib.bulksms.Utils.CustomSwipeDisableViewPager;
import com.example.arsojib.bulksms.Utils.ViewPagerAdapter;

public class SmsHistoryFragment extends Fragment {

    View view;
    ImageView back;
    CustomSwipeDisableViewPager viewPager;
    TabLayout tabLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.sms_history_layout, container, false);

        initialComponent();
        tabLayout.setupWithViewPager(viewPager);
        setupViewPager(viewPager);
        setupTabIcons();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
//                getChildFragmentManager().popBackStack();
            }
        });

        return view;
    }

    private void initialComponent() {
        back = view.findViewById(R.id.back);
        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter.mFragmentList.clear();
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new SmsNotSentFragment());
        adapter.addFragment(new SmsSentFragment());
        adapter.addFragment(new SmsDeliveredFragment());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(adapter.getCount());
    }

    private void setupTabIcons() {
        try {
            tabLayout.getTabAt(0).setText("Not Sent");
            tabLayout.getTabAt(1).setText("Sent");
            tabLayout.getTabAt(2).setText("Delivered");
        } catch (RuntimeException ignored) {
        }
    }

}
