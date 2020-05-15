package com.demo.demos.FindU.SearchByWiFi.core.Activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;


import androidx.appcompat.app.AppCompatActivity;

import com.demo.demos.FindU.SearchByWiFi.core.ListItem;
import com.demo.demos.FindU.SearchByWiFi.core.WifiProbeManager;
import com.demo.demos.FindU.SearchByWiFi.core.adapter.MyAdapter;
import com.demo.demos.R;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SearchActivity extends AppCompatActivity {

    private ListView mWifi_list;
    //wifi探针
    private int WiFi_time = 30 *1000 ;//30秒扫描一次
    private MyAdapter mAdapter;
    private WifiProbeManager mProbe ;
    private Timer mTimer ;
    private WifiProbeManager.MacListListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchlist);

        mProbe = new WifiProbeManager();

        initView();

        initListener();

        initScan();

    }

    /**
     * 在一定时间内进行扫描（也就是读取文件夹内的mac地址）
     */
    private void initScan() {
        mTimer = new Timer();
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                startScan();
            }
        } , 0 , WiFi_time );
    }

    private void initListener(){

        mListener = new WifiProbeManager.MacListListener() {
            @Override
            public void macList(final List<ListItem> macList) {
                //因为在线程中进行扫描的，所以要切换到主线程
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //取数据 ，显示在界面上
                        showList(macList);
                        mAdapter.setList(macList);
                        mWifi_list.setAdapter(mAdapter);

                    }
                });
            }
        };
    }

    /**
     * 开始扫描 ， 如果wifi断开了数据就会没有了 ， 如果wifi地址变了内容也会变的
     */
    private void startScan() {
        mProbe.startScan(mListener);
    }

    private void initView() {
        mWifi_list = (ListView) findViewById(R.id.list_wifi);
        mAdapter = new MyAdapter(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTimer.cancel();
    }

    public void showList(List<ListItem> macList){
        ListItem item = null;
        Log.d("size", String.valueOf(macList.size()));
        for (int i = 0;i < macList.size(); i++){
            item = macList.get(i);
            Log.d("maclist",item.getIp()+"---"+item.getMac()+"---"+item.getFirm());
        }


    }
}
