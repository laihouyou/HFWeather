package com.gddst.lhy.weather.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gddst.app.lib_common.base.BaseFragment;

import androidx.appcompat.widget.SearchView;

public class CitySearchFragment extends BaseFragment {
    private SearchView searchView;
    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return null;
    }

    @Override
    protected void initView(View view) {
        searchView.getSuggestionsAdapter();
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void lazyLoad() {

    }
}
