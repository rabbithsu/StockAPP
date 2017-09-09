package edu.nccu.cs.nccustock.stockdetail;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.nccu.cs.nccustock.R;

/**
 * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
 * sections of the app.
 */
public  class AppSectionsPagerAdapter extends FragmentPagerAdapter {
    public String stock_name="";
    public AppSectionsPagerAdapter(FragmentManager fm,String stockname) {
        super(fm);
        this.stock_name=stockname;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i) {
            case 0:
                // The first section of the app is the most interesting -- it offers
                // a launchpad into the other demonstrations in this example application.
                return StockPositiveNewsFragment.newInstance(true,stock_name);

            default:
                // The other sections of the app are dummy placeholders.
                return StockPositiveNewsFragment.newInstance(false,stock_name);

        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if(position==0){
            return "正向新聞";
        }else{
            return "負向新聞";
        }

    }

    public static class DemoObjectFragment extends Fragment {

        public static final String ARG_OBJECT = "object";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_collection_object, container, false);
            Bundle args = getArguments();
            ((TextView) rootView.findViewById(android.R.id.text1)).setText(
                    Integer.toString(1));
            return rootView;
        }
    }
}