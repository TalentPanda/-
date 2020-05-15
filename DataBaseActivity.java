package com.demo.demos.FindU.SearchByWiFi.core.Activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;

import com.demo.demos.FindU.SearchByWiFi.core.SQLite.DataBaseItem;
import com.demo.demos.FindU.SearchByWiFi.core.SQLite.DataBaseOperator;
import com.demo.demos.R;

import java.util.ArrayList;
import java.util.List;

public class DataBaseActivity extends AppCompatActivity {
    private EditText mMacEditText;
    private EditText mFirmEditText;
    private Button mAddBtn;
    private Button mDeleteBtn;
    private Button mShowBtn;
    private TextView mShowTv;
    private DataBaseOperator mOperator;
    private DataBaseItem mData;
    private List<DataBaseItem> mDatas = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_base);

        mOperator = DataBaseOperator.getInstance();

        initView();
        //增加
        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mMacCheck = mMacEditText.getText().toString();
                String trueMacAddress = "([A-Fa-f0-9]{2}-){5}[A-Fa-f0-9]{2}";
                ////合理性检查
                if (mMacCheck.matches(trueMacAddress)){
                    mData = toDataBaseItemByString(mMacEditText.getText().toString(),
                            mFirmEditText.getText().toString());
                    mOperator.add(mData);
                }else {
                    mShowTv.setText("");
                    mShowTv.append("Mac地址格式输入错误，示例：FF:FF:FF:FF:FF:FF");
                }
            }
        });
        //删除
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mOperator.delete(mMacEditText.getText().toString());
            }
        });
//        //修改
//        mUpdateBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mData = toDataBaseItemByString(mMacEditText.getText().toString(),
//                        mFirmEditText.getText().toString());
//                mOperator.update(mData);
//            }
//        });
        //显示
        mShowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.isEmpty(mMacEditText.getText())){
                    mData = mOperator.selectFirmByMac(mMacEditText.getText().toString());
                    if (mData == null){
                        Log.d("mData", "未找到匹配项");
                    }else {
                        mShowTv.setText("");
                        mShowTv.append("\n"+mData.getmMac()+"---------"+mData.getmFirm());
                    }

                }else {
                    mDatas = mOperator.selectAll();
                    if (mDatas == null){
                        Log.d("mDatas", "为空");
                    }else {
                        mShowTv.setText("");
                        for (DataBaseItem item:mDatas) {
                            mShowTv.append("\n"+item.getmMac()+"---------"+item.getmFirm());
                        }
                    }
                }
            }
        });


    }

    private void initView(){
        mMacEditText = findViewById(R.id.mac_EditText);
        mFirmEditText = findViewById(R.id.firm_EditText);
        mAddBtn = findViewById(R.id.add_btn);
        mDeleteBtn = findViewById(R.id.delete_btn);
        mShowBtn = findViewById(R.id.show_btn);
        mShowTv = findViewById(R.id.show_textView);
    }


    private DataBaseItem toDataBaseItemByString(String mac, String firm){
        DataBaseItem item = new DataBaseItem();
        item.setmMac(mac);
        item.setmFirm(firm);
        return item;
    }
}
