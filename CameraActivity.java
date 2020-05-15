package com.demo.demos.FindU.Camera;

import android.Manifest;
import android.os.Bundle;

import com.demo.demos.FindU.Camera.base.BaseActivity;
import com.demo.demos.FindU.Camera.fragments.GLPreviewFragment;
import com.demo.demos.FindU.Camera.utils.CameraUtils;
import com.demo.demos.FindU.Camera.utils.GLUtil;
import com.demo.demos.R;

public class CameraActivity extends BaseActivity{


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        CameraUtils.init(this);
        GLUtil.init(this);

        requestPermission("请给予相机、存储权限，以便app正常工作", null,
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container_camera, new GLPreviewFragment())
                .commit();
    }

}
