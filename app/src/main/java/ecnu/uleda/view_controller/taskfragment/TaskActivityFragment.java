package ecnu.uleda.view_controller.taskfragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.AppCompatImageView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ecnu.uleda.R;
import ecnu.uleda.model.UActivity;
import ecnu.uleda.view_controller.widgets.NoScrollViewPager;
import ecnu.uleda.view_controller.widgets.RecyclerViewBanner;
import ecnu.uleda.view_controller.widgets.StickyNavLayout;

/**
 * Created by jimmyhsu on 2017/4/11.
 */

public class TaskActivityFragment extends Fragment implements StickyNavLayout.OnRefreshListener {

    private static final String[] TYPES = {"全部", "运动", "社团", "公益"};
    private static final int MESSAGE_REFRESH_COMPLETE = 0x110;
    private Unbinder mUnbinder;
    private Handler mHandler;

    @BindView(R.id.stickynavlayout)
    StickyNavLayout mContainer;
    @BindView(R.id.id_stickynavlayout_topview)
    RecyclerViewBanner mBanner;
    @BindView(R.id.id_stickynavlayout_indicator)
    TabLayout mIndicator;
    @BindView(R.id.id_stickynavlayout_viewpager)
    NoScrollViewPager mPager;
    private List<UActivity> mActivityList;

    private static TaskActivityFragment mInstance;
    public static Fragment getInstance() {
        if (mInstance == null) {
            synchronized (TaskActivityFragment.class) {
                if (mInstance == null) {
                    mInstance = new TaskActivityFragment();
                }
            }
        }
        return mInstance;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.task_activity_fragment, container, false);
        mUnbinder = ButterKnife.bind(this, v);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initHandler();
        initPager();
        initIndicator();
        initRollPager();
        mContainer.setOnRefreshListener(this);
        mActivityList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            mActivityList.add(new UActivity("xiaohong.jpg", "小蓝", System.currentTimeMillis()/1000 - 24*3600,
                    "校园", getResources().getString(R.string.activity_example)));
        }
    }

    private void initHandler() {
        mHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MESSAGE_REFRESH_COMPLETE:
                        mContainer.refreshComplete();
                        break;
                }
            }
        };

    }

    private void initPager() {
        mPager.setNoScroll(true);
        mPager.setAdapter(new FragmentPagerAdapter(getChildFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                return ActivityListFragment.getInstance();
            }

            @Override
            public int getCount() {
                return 1;
            }
        });
    }

    private void initIndicator() {
        for (String title : TYPES)
            mIndicator.addTab(mIndicator.newTab().setText(title));
        mIndicator.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                EventBus.getDefault().post(tab);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    private void initRollPager() {
        List<String> datas = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            datas.add(i + "");
        }
        mBanner.setRvBannerData(datas);
        mBanner.setOnSwitchRvBannerListener(new RecyclerViewBanner.OnSwitchRvBannerListener() {
            private final int[] res = {R.drawable.img1, R.drawable.img2, R.drawable.img3,
            R.drawable.img2};
            @Override
            public void switchBanner(int i, AppCompatImageView appCompatImageView) {
                appCompatImageView.setImageResource(res[i]);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                    mHandler.sendEmptyMessage(MESSAGE_REFRESH_COMPLETE);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
