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
        viewPager.setOffscreenPageLimit(4);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                // 逻辑修改：
                // Position 0: Today (显示 FAB)
                // Position 1: Habits (显示 FAB)
                // Position 2: Stats  (显示 FAB -> 用户希望在这里也能添加习惯)
                // Position 3: Discover (隐藏 FAB -> 这里主要用于导入模板)
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
                    tab.setText(R.string.tab_today);
                    break;
                case 1:
                    tab.setText(R.string.tab_habits);
                    break;
                case 2:
                    tab.setText(R.string.tab_stats);
                    break;
                case 3:
                    tab.setText(R.string.tab_discover);
                    break;
            }
        }).attach();
    }

    private void setupFab() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 逻辑修改：
                // 无论当前在 "今日"、"习惯" 还是 "统计" 页面，
                // 点击加号按钮都统一跳转到创建习惯页面。
                openHabitCreation();
            }
        });
    }

    private void openHabitCreation() {
        Intent intent = new Intent(this, HabitCreationActivity.class);
        startActivity(intent);
    }

    // 之前的 shareStats 方法如果不再使用，可以保留为空或删除
    private void shareStats() {
        // 分享功能预留
    }
}