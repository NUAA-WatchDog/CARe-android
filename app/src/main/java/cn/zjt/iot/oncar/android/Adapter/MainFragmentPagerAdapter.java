package cn.zjt.iot.oncar.android.Adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import cn.zjt.iot.oncar.android.Activity.MainActivity;

/**
 * @author Mr Dk.
 * @version 2018.4.22
 * @see MainActivity
 * @since 2018.4.22
 */

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

    /**
     * @Variables
     */
    private List<Fragment> fragmentList;

    /**
     * @Methods
     */

    /*
     * @Constructor MainFragmentPagerAdapter
     * @Param FragmentManager fm
     * @Param List<Fragment> fragmentList
     */
    public MainFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList) {
        super(fm);
        this.fragmentList = fragmentList;
    }

    /**
     * @Override
     */

    /*
     * @Override getItem
     * @Param position
     * @Return Fragment
     */
    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    /*
     * @Override getCount
     * @Return int
     */
    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
