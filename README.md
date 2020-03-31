# React Native APK Installer 
[![npm version](https://badge.fury.io/js/rn-android-apk-install.svg)](https://badge.fury.io/js/rn-android-apk-install)


Install an Android APK from your react-native project.
This project is based `react-native-install-apk` with enhancements to support current RN versions.

# Installing ApkInstaller

1. Add `rn-android-apk-install` to your project

  - Install directly from npm: `npm install --save rn-android-apk-install`.
  - Or do `npm install --save git+https://github.com/walterleong95/rn-android-apk-install.git` in your main project.

2. Link the library:

  - Add the following to `android/settings.gradle`:

    ```
      include ':rn-android-apk-install'
      project(':rn-android-apk-install').projectDir = new File(settingsDir, '../node_modules/rn-android-apk-install/android')
    ```

  - Add the following to `android/app/build.gradle`:

    ```xml
      ...

      dependencies {
          ...
          implementation project(':rn-android-apk-install')
      }
    ```

  - Add the following to `android/app/src/main/java/**/MainApplication.java`:

    ```java
      ...
      import com.wl.apkinstaller.ApkInstallerPackage;  // add this for rn-android-apk-install

      public class MainApplication extends Application implements ReactApplication {

          @Override
          protected List<ReactPackage> getPackages() {
              return Arrays.<ReactPackage>asList(
                  new MainReactPackage(),
                  ...
                  new ApkInstallerPackage()     // add this for rn-android-apk-install
              );
          }
      }
    ```
4. Add the following to your `AndroidManifest.xml` file. 
   Take note of the value of `android:authorities`. It will be used in the last step. For example, `(APP_BUNDLE_ID).provider`.
   ```xml
    <uses-permission android:name="android.permission.REQUEST_INSTALL_PACKAGES"/>
   ```
  ```xml
    <provider
            android:name="android.support.v4.content.FileProvider"
            android:authorities="${applicationId}.provider"
            android:exported="false"
            android:grantUriPermissions="true"
            tools:replace="android:authorities">
            <!-- you might need the tools:replace thing to workaround rn-fetch-blob or other definitions of provider -->
            <!-- just make sure if you "replace" here that you include all the paths you are replacing *plus* the cache path we use -->
            <meta-data tools:replace="android:resource"
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/filepaths"/>
        </provider>
  ```
5. Create a file named `filepaths.xml` in the `res/xml` directory & append it with the following content.
  ```xml
  <?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">


    <!-- Select one of the following based on your apk location -->

    <!-- cache dir is always available and requires no permissions, but space may be limited -->
    <cache-path name="cache" path="/" />
    <root-path name="root" path="." />

    <!-- <files-path name="name" path="/" />  -->

    <!-- External cache dir is maybe user-friendly for downloaded APKs, but you must be careful. -->
    <!-- 1) in API <19 (KitKat) this requires WRITE_EXTERNAL_STORAGE permission. >=19, no permission -->
    <!-- 2) this directory may not be available, check Environment.isExternalStorageEmulated(file) to see -->
    <!-- 3) there may be no beneifit versus cache-path if external storage is emulated. Check Environment.isExternalStorageEmulated(File) to verify -->
    <!-- 4) the path will change for each app 'com.example' must be replaced by your application package -->
    <!-- <external-cache-path name="external-cache" path="/data/user/0/com.example/cache" /> -->

    <!-- Note that these external paths require WRITE_EXTERNAL_STORAGE permission -->
    <!-- <external-path name="some_external_path" path="put-your-specific-external-path-here" />  -->
    <!-- <external-files-path name="external-files" path="/data/user/0/com.example/cache" />  -->
    <!-- <external-media-path name="external-media" path="put-your-path-to-media-here" />  -->
</paths>
  ```

6. Lastly, add `import ApkInstaller from 'rn-android-apk-install'` to the list of imports. (You might also need to install `react-native-fs` package). **Note: Your file provider ID must be the same as the one you've defined in step 4**

  ```javascript
    import RNFS from 'react-native-fs';
    import ApkInstaller from 'rn-android-apk-install'

     try {
         var filePath = RNFS.CachesDirectoryPath + '/com.example.app.apk';
         var download = RNFS.downloadFile({
           fromUrl: 'http://example.com/com.example.app.apk',
           toFile: filePath,
           progress: res => {
               console.log((res.bytesWritten / res.contentLength).toFixed(2));
           },
           progressDivider: 1
         });

         download.promise.then(result => {
           if(result.statusCode == 200) {
             console.log(filePath);
             ApkInstaller.install(filePath, 'com.your.application.provider');
           }
         });
     }
     catch(error) {
         console.warn(error);
     }
  ```
