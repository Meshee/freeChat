package cn.meshee.freechat.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import java.util.List;
import cn.meshee.freechat.ui.base.BaseFragment;

public class CommonFragmentPagerAdapter extends FragmentPagerAdapter {

    public static int MAIN_VIEW_PAGER = 1;

    private int mViewPagerType = 0;

    public String[] mainViewPagerTitle = null;

    private List<BaseFragment> mFragments;

    public CommonFragmentPagerAdapter(FragmentManager fm, List<BaseFragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    public CommonFragmentPagerAdapter(FragmentManager fm, List<BaseFragment> fragments, int viewPagerType) {
        this(fm, fragments);
        mViewPagerType = viewPagerType;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments != null ? mFragments.size() : 0;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }
}
