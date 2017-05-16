package com.example.jinkai.coolweather.Fragment;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.example.jinkai.coolweather.R;
import com.example.jinkai.coolweather.db.City;
import com.example.jinkai.coolweather.db.County;
import com.example.jinkai.coolweather.db.Province;
import com.example.jinkai.coolweather.utils.DbUtil;
import com.example.jinkai.coolweather.utils.HttpUtil;
import com.google.gson.Gson;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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
    private ProgressDialog progressDialog;
    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countiesList;
    /**
     * 被选中的省份
     */
    private Province selectedProvince;
    /**
     * 被选中的城市
     */
    private City selectedCity;


    public static ChooseAreaFragment newInstance() {

        Bundle args = new Bundle();

        ChooseAreaFragment fragment = new ChooseAreaFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.chose_area, container,false);
        initView(rootView);
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
                    selectedProvince = provinceList.get(position);
                    queryCity();
                }else if (currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounty();
                }
            }
        });
        queryProvince();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.back) {
            if (currentLevel==LEVEL_COUNTY){
                queryCity();
            }else if (currentLevel==LEVEL_CITY){
                queryProvince();
            }
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
        title.setText("中国");
        back.setVisibility(View.GONE);
        provinceList = DataSupport.findAll(Province.class);
        if (provinceList.size()>0){
            datalist.clear();
            for (Province province:provinceList) {
                datalist.add(province.getProvinceName());
            }
            stringArrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }else {
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }
    }

    /**
     * 查询所选省份所有的市，优先从数据库中查询，如果没有查询到再去服务器查询
     */
    private void queryCity(){
        title.setText(selectedProvince.getProvinceName());
        back.setVisibility(View.VISIBLE);
        cityList = DataSupport.where("provinceID=?",
                String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size()>0){
            datalist.clear();
            for (City city:cityList) {
                datalist.add(city.getCityName());
            }
            stringArrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else {
            String address="http://guolin.tech/api/china/"+selectedProvince.getProvinceCode();
            queryFromServer(address,"city");
        }
    }

    /**
     * 查询所选市内所有的县，优先从数据库中查询，如果没有查询到再去服务器查询
     */
    private void queryCounty(){
        title.setText(selectedCity.getCityName());
        back.setVisibility(View.VISIBLE);
        countiesList = DataSupport.where("cityID=?",String.valueOf(selectedCity.getId())).find(County.class);
        if (countiesList.size()>0){
            datalist.clear();
            for (County county:countiesList) {
                datalist.add(county.getCountyName());
            }
            stringArrayAdapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else {
            int provinceCode = selectedProvince.getProvinceCode();
            int cityCode = selectedCity.getCityCode();
            String address="http://guolin.tech/api/china/"+provinceCode+"/"+cityCode;
            queryFromServer(address,"county");
        }
    }

    /**
     * 根据传入的地址和类型从服务器上查询省市县数据
     * @param address
     * @param type
     */
    private void queryFromServer(String address,final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //通过runOnUiThread()方法回到主线程处理逻辑
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseStr = response.body().string();
                boolean result=false;
                if ("province".equals(type)){
                    result = DbUtil.handleProvinceResponse(responseStr);
                }else if ("city".equals(type)){
                    result=DbUtil.handleCityResponse(responseStr,selectedProvince.getId());
                }else if ("county".equals(type)){
                    result=DbUtil.handleCountyResponse(responseStr,selectedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvince();
                            }else if ("city".equals(type)){
                                queryCity();
                            }else if ("county".equals(type)){
                                queryCounty();
                            }
                        }
                    });
                }
            }
        });
    }


    /**
     * 显示进度对话框
     */
    private void showProgressDialog(){
        if (progressDialog==null){
            progressDialog=new ProgressDialog(getContext());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    /**
     * 关闭进度对话框
     */
    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }
}
