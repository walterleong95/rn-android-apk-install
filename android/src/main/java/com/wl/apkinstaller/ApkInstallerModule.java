package com.wl.apkinstaller;

import android.os.Build;
import android.support.v4.content.FileProvider;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.net.Uri;
import java.io.File;

public class ApkInstallerModule extends ReactContextBaseJavaModule {
  private ReactApplicationContext _context;

  public ApkInstallerModule(ReactApplicationContext reactContext) {
    super(reactContext);
    _context = reactContext;
  }

  @Override
  public String getName() {
    return "ApkInstaller";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();

    return constants;
  }

  @ReactMethod
  public void test(String message) {
    Toast.makeText(getReactApplicationContext(), message, Toast.LENGTH_LONG).show();
  }

    @ReactMethod
    public void install(String path, String bundleID) {
        String cmd = "chmod 777 " + path;
        try {
            Runtime.getRuntime().exec(cmd);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);


        File file = new File(path);
        Uri fileUri = Uri.fromFile(file);


        if (Build.VERSION.SDK_INT >= 24) {
            fileUri = FileProvider.getUriForFile(_context, bundleID + ".provider",
                    file);
        }

        intent.setDataAndType(fileUri, "application/vnd.android" + ".package-archive");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        _context.startActivity(intent);
    }
}
