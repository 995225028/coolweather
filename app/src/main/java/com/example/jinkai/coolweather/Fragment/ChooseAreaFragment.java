package com.example.jinkai.coolweather.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    private int currentLevel;

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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel==LEVEL_PROVINCE){

                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {

        }
    }

    /**
     * 初始化控件
     * @param rootView
     */
    private void initView(View rootView) {
        back = (Button) rootView.findViewById(R.id.back);
        back.setOnClickListener(ChooseAreaFragment.this);
        title = (TextView) rootView.findViewById(R.id.title);
        listView = (ListView) rootView.findViewById(R.id.listView);
    }

    /**
     * 查询全国所有的省，优先从数据库中查询，如果没有查询到再去服务器查询
     */
    private void queryProvince(){

    }

    /**
     * 查询所选省份所有的市，优先从数据库中查询，如果没有查询到再去服务器查询
     */
    private void queryCity(){

    }

    /**
     * 查询所选市内所有的县，优先从数据库中查询，如果没有查询到再去服务器查询
     */
    private void queryCounty(){

    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     * @param address
     * @param type
     */
    private void queryFromServer(String address,final String type){

    }
}
