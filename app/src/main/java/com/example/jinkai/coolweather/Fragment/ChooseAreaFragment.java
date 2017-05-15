package com.example.jinkai.coolweather.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.jinkai.coolweather.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jinkai on 2017/5/15.
 */

public class ChooseAreaFragment extends Fragment implements View.OnClickListener {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;
    protected View rootView;
    protected Button back;
    protected TextView title;
    protected ListView listView;
    private ArrayAdapter<String> stringArrayAdapter;
    private List<String> datalist=new ArrayList<>();

    public static ChooseAreaFragment newInstance() {

        Bundle args = new Bundle();

        ChooseAreaFragment fragment = new ChooseAreaFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initView(rootView);
        rootView = inflater.inflate(R.layout.chose_area, container,false);
        stringArrayAdapter = new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,datalist);
        listView.setAdapter(stringArrayAdapter);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {

        }
    }

    private void initView(View rootView) {
        back = (Button) rootView.findViewById(R.id.back);
        back.setOnClickListener(ChooseAreaFragment.this);
        title = (TextView) rootView.findViewById(R.id.title);
        listView = (ListView) rootView.findViewById(R.id.listView);
    }
}
