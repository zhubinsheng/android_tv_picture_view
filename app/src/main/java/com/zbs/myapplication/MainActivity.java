package com.zbs.myapplication;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.concurrent.ExecutionException;

/*
 * Main Activity class that loads {@link MainFragment}.
 */
public class MainActivity extends Activity {

    private String TAG = "main";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        verifyStoragePermissions(this);
    }

    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                String[] permissions = {
                        android.Manifest.permission.ACCESS_NETWORK_STATE,
                        android.Manifest.permission.ACCESS_WIFI_STATE,
                        android.Manifest.permission.READ_EXTERNAL_STORAGE,
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
};
                ActivityCompat.requestPermissions(activity, permissions, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void show(View view) {
        view.setVisibility(View.GONE);
        findViewById(R.id.button3).setVisibility(View.GONE);
        findViewById(R.id.imageView).setVisibility(View.VISIBLE);

        String str = (String) SPUtil.get(MainActivity.this, "file", "picture","nu");

        Glide.with(view)
                .load(str)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.movie)
                .into((ImageView) findViewById(R.id.imageView));
    }

    public void down(final View view) {
        findViewById(R.id.imageView).setVisibility(View.VISIBLE);

        EditText editText = findViewById(R.id.editTextTextPersonName);
        final String picUrl = editText.getText().toString();

        Glide.with(view)
                .load(picUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.drawable.movie)
                .into((ImageView) findViewById(R.id.imageView));

        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(10_000);
                    File file = Glide.with(MainActivity.this).downloadOnly().load(picUrl).submit().get();

                    String galleryPath = Environment.getExternalStorageDirectory()
                            + File.separator + "DCIM"
                            + File.separator +  file.getName() + ".jpg"
                            ;

                    Log.d(TAG, "galleryPath: " + galleryPath);
                    Log.d(TAG, "file: " + file.getName());
                    Log.d(TAG, "file: " + file.getPath());
                    Log.d(TAG, "file: " + file.getAbsolutePath());
                    copy(file, new File(galleryPath));

                    SPUtil.putString(MainActivity.this, "file", "picture", file.getPath());
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 复制文件
     *
     * @param source 输入文件
     * @param target 输出文件
     */
    public static void copy(File source, File target) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
            fileOutputStream.flush();
            fileInputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}