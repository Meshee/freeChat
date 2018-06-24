package cn.meshee.freechat.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import com.cjt2325.cameralibrary.JCameraView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import cn.meshee.freechat.R;
import cn.meshee.freechat.app.AppConst;
import cn.meshee.freechat.ui.base.BaseActivity;
import cn.meshee.freechat.ui.base.BasePresenter;
import cn.meshee.freechat.util.UIUtils;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionGen;
import kr.co.namee.permissiongen.PermissionSuccess;

public class TakePhotoActivity extends BaseActivity {

    private JCameraView mJCameraView;

    @Override
    public void init() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED)
            PermissionGen.with(this).addRequestCode(100).permissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.RECORD_AUDIO).request();
    }

    @Override
    public void initView() {
        mJCameraView = (JCameraView) findViewById(R.id.cameraview);
        mJCameraView.setSaveVideoPath(Environment.getExternalStorageDirectory().getPath());
        mJCameraView.setAutoFoucs(false);
        File file = new File(AppConst.VIDEO_SAVE_DIR);
        if (!file.exists())
            file.mkdirs();
        mJCameraView.setSaveVideoPath(AppConst.VIDEO_SAVE_DIR);
    }

    @Override
    public void initListener() {
        mJCameraView.setCameraViewListener(new JCameraView.CameraViewListener() {

            @Override
            public void quit() {
                finish();
            }

            @Override
            public void captureSuccess(Bitmap bitmap) {
                String path = saveBitmap(bitmap, AppConst.PHOTO_SAVE_DIR);
                Intent data = new Intent();
                data.putExtra("take_photo", true);
                data.putExtra("path", path);
                setResult(RESULT_OK, data);
                finish();
            }

            @Override
            public void recordSuccess(String url) {
                Intent data = new Intent();
                data.putExtra("take_photo", false);
                data.putExtra("path", url);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    @PermissionSuccess(requestCode = 100)
    public void permissionSuccess() {
        UIUtils.postTaskDelay(() -> {
            recreate();
        }, 500);
    }

    @PermissionFail(requestCode = 100)
    public void permissionFail() {
        UIUtils.showToast("获取相机权限失败");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mJCameraView != null)
            mJCameraView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mJCameraView != null)
            mJCameraView.onPause();
    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected int provideContentViewId() {
        return R.layout.activity_take_photo;
    }

    public String saveBitmap(Bitmap bm, String dir) {
        String path = "";
        File f = new File(dir, "CSDN_LQR_" + SystemClock.currentThreadTimeMillis() + ".png");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            path = f.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return path;
    }
}
