package com.example.cyclops.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.cyclops.R;
import com.example.cyclops.adapter.MainPagerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupViewPager();
        setupTabLayout();
        setupFab();
    }

    private void initViews() {
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        fab = findViewById(R.id.fab);
    }

    private void setupViewPager() {
        MainPagerAdapter pagerAdapter = new MainPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOffscreenPageLimit(4); // 保留所有 Fragment 实例

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == 3) {
                    fab.hide();
                } else {
                    fab.show();
                }
            }
        });
    }

    private void setupTabLayout() {
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0:
                    tab.setText(R.string.tab_today); // 修改
                    break;
                case 1:
                    tab.setText(R.string.tab_habits); // 修改
                    break;
                case 2:
                    tab.setText(R.string.tab_stats); // 修改
                    break;
                case 3:
                    tab.setText(R.string.tab_discover); // 修改
                    break;
            }
        }).attach();
    }

    private void setupFab() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPage = viewPager.getCurrentItem();
                switch (currentPage) {
                    case 0:
                    case 1:
                        openHabitCreation();
                        break;
                    case 2:
                        shareStats();
                        break;
                }
            }
        });
    }

    private void openHabitCreation() {
        Intent intent = new Intent(this, HabitCreationActivity.class);
        startActivity(intent);
    }

    private void shareStats() {
        // 分享功能
        // Intent shareIntent = new Intent(Intent.ACTION_SEND);
        // shareIntent.setType("text/plain");
        // shareIntent.putExtra(Intent.EXTRA_TEXT, "看看我在Cyclops上的习惯统计！");
        // startActivity(Intent.createChooser(shareIntent, "分享统计"));
    }
}
