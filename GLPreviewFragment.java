package com.demo.demos.FindU.Camera.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CaptureFailure;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.TotalCaptureResult;
import android.media.Image;
import android.media.ImageReader;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.demo.demos.FindU.Camera.CameraActivity;
import com.demo.demos.FindU.SearchByWiFi.core.Application.MyApplication;
import com.demo.demos.R;
import com.demo.demos.FindU.Camera.base.BaseActivity;
import com.demo.demos.FindU.Camera.base.BaseFragment;
import com.demo.demos.FindU.Camera.filter.ColorFilter;
import com.demo.demos.FindU.Camera.render.CameraPreviewRender;
import com.demo.demos.FindU.Camera.utils.CameraUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class GLPreviewFragment extends BaseFragment {

    GLSurfaceView glSurfaceView;

    boolean useFront = false;//是否使用的是前置相机
    boolean canTake = false;
    String cameraId;
    CameraManager cameraManager;
    List<Size> outputSizes;
    Size photoSize;
    CameraDevice cameraDevice;
    CameraCaptureSession captureSession;
    CaptureRequest.Builder previewRequestBuilder;
    CaptureRequest previewRequest;

    CameraPreviewRender cameraPreviewRender;
    SurfaceTexture surfaceTexture;
    Surface surface;

    Button btnCamera, btnColorFilter, btnPhoto;
    ImageReader reader = null;

    public GLPreviewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_gl_preview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initCamera();

        initViews(view);
    }

    private void initCamera() {
        cameraManager = CameraUtils.getInstance().getCameraManager();
        cameraId = CameraUtils.getInstance().getCameraId(useFront);
        outputSizes = CameraUtils.getInstance().getCameraOutputSizes(cameraId, SurfaceTexture.class);
        photoSize = outputSizes.get(16);
        reader = ImageReader.newInstance(photoSize.getWidth(), photoSize.getHeight(), ImageFormat.JPEG,1);
    }

    private void initViews(View view) {
        glSurfaceView = view.findViewById(R.id.glSurfaceView);
        glSurfaceView.setEGLContextClientVersion(3);
        cameraPreviewRender = new CameraPreviewRender();
        glSurfaceView.setRenderer(cameraPreviewRender);

        btnColorFilter = view.findViewById(R.id.btnColorFilter);
        btnColorFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ColorFilter.COLOR_FLAG < 3){
                    ColorFilter.COLOR_FLAG++;
                }else {
                    ColorFilter.COLOR_FLAG = 0;
                }
            }
        });

        btnPhoto = view.findViewById(R.id.btnPhoto);
        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拍照
                //takePic();
                cameraPreviewRender.setTakingPhoto(true);
                Toast.makeText(MyApplication.getContext(),"图片保存成功",Toast.LENGTH_SHORT).show();
            }
        });

        btnCamera = view.findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换相机
                changeCamera();
            }
        });
    }
    //拍照,只能保存没有滤镜的照片
//    private void takePic() {
//
//        if (!canTake) return;
//
//        reader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
//            @Override
//            public void onImageAvailable(ImageReader imageReader) {
//
//                Image file = imageReader.acquireNextImage();
//                ByteBuffer byteBuffer = file.getPlanes()[0].getBuffer();
//                byte[] bytes = new byte[byteBuffer.remaining()];
//                byteBuffer.get(bytes);
//                SavePic(bytes);
//            }
//        }, null);
//
//        try {
//            CaptureRequest.Builder builder = cameraDevice.createCaptureRequest(cameraDevice.TEMPLATE_STILL_CAPTURE);
//            builder.addTarget(reader.getSurface());
//            builder.set(CaptureRequest.CONTROL_AE_MODE,
//                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
//            final CaptureRequest takePicRequest = builder.build();
//
//            builder.set(CaptureRequest.JPEG_ORIENTATION, 0);
//            captureSession.capture(takePicRequest,null,null);
//
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }

//    public void SavePic(byte[] bytes){
//        Log.d("Save", "保存中");
//        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsoluteFile();
//        if (!dir.exists()){
//            dir.mkdirs();
//        }
//        File file = new File(dir,System.currentTimeMillis()+".jpeg");
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(file);
//            fos.write(bytes);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }finally {
//            if (fos != null){
//                try {
//                    fos.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//
//    }

    @Override
    public void onResume() {
        super.onResume();
        ((BaseActivity) getActivity()).requestPermission("请给予相机、存储权限，以便app正常工作",
                new BaseActivity.Callback() {
                    @Override
                    public void success() {
//                        glSurfaceView.onResume();
                        openCamera();
                    }

                    @Override
                    public void failed() {
                        Toast.makeText(getContext(), "未授予相机、存储权限！", Toast.LENGTH_SHORT).show();
                    }
                },
                new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }

    @Override
    public void onPause() {
//        glSurfaceView.onPause();
        releaseCamera();
        super.onPause();
    }

    @SuppressLint("MissingPermission")
    private void openCamera() {
        try {
            cameraManager.openCamera(cameraId, cameraStateCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
            Log.d(TAG, "相机访问异常");
        }
    }

    private void changeCamera(){
        releaseCamera();
        useFront = !useFront;
        cameraId = CameraUtils.getInstance().getCameraId(useFront);
        openCamera();

        cameraPreviewRender.setUseFront(useFront);
    }

    private void releaseCamera() {
        CameraUtils.getInstance().releaseCameraSession(captureSession);
        CameraUtils.getInstance().releaseCameraDevice(cameraDevice);
    }

    CameraDevice.StateCallback cameraStateCallback = new CameraDevice.StateCallback() {
        @Override
        public void onOpened(CameraDevice camera) {
            Log.d(TAG, "相机已启动");
            surfaceTexture = cameraPreviewRender.getSurfaceTexture();
            if (surfaceTexture == null) {
                return;
            }
            surfaceTexture.setDefaultBufferSize(photoSize.getWidth(), photoSize.getHeight());
            surfaceTexture.setOnFrameAvailableListener(new SurfaceTexture.OnFrameAvailableListener() {
                @Override
                public void onFrameAvailable(final SurfaceTexture surfaceTexture) {
                    glSurfaceView.requestRender();
                }
            });
            surface = new Surface(surfaceTexture);

            try {
                cameraDevice = camera;
                previewRequestBuilder = cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
                previewRequestBuilder.addTarget(surface);
                previewRequest = previewRequestBuilder.build();

                cameraDevice.createCaptureSession(Arrays.asList(surface,reader.getSurface()), sessionsStateCallback, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                Log.d(TAG, "相机访问异常");
            }
        }

        @Override
        public void onDisconnected(CameraDevice camera) {
            Log.d(TAG, "相机已断开连接");
        }

        @Override
        public void onError(CameraDevice camera, int error) {
            Log.d(TAG, "相机打开出错");
        }
    };

    CameraCaptureSession.StateCallback sessionsStateCallback = new CameraCaptureSession.StateCallback() {
        @Override
        public void onConfigured(CameraCaptureSession session) {
            if (null == cameraDevice) {
                return;
            }

            captureSession = session;
            try {
                captureSession.setRepeatingRequest(previewRequest,
                        new CameraCaptureSession.CaptureCallback() {
                            @Override
                            public void onCaptureCompleted(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull TotalCaptureResult result) {
                                super.onCaptureCompleted(session, request, result);
                                canTake = true;
                            }

                            @Override
                            public void onCaptureFailed(@NonNull CameraCaptureSession session, @NonNull CaptureRequest request, @NonNull CaptureFailure failure) {
                                super.onCaptureFailed(session, request, failure);
                                Log.d("onCaptureFailed","开启捕捉失败");
                            }
                        }, null);
            } catch (CameraAccessException e) {
                e.printStackTrace();
                Log.d(TAG, "相机访问异常");
            }
        }

        @Override
        public void onConfigureFailed(CameraCaptureSession session) {
            Log.d(TAG, "会话注册失败");
        }
    };
}
