# TestAndroidTeam

Android app demonstrating HomeTurf library integration via JitPack

## Integration

1. Add jitpack repository to your project (top-level) build.gradle file, at the bottom of allProjects -> repositories:

```.gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

2a. Add HomeTurf implementation to module level build.gradle file dependencies:

```.gradle
dependencies {
    ...
    implementation 'com.github.HomeTurf-LLC:HomeTurfAndroidLibraryApp:1.0.1'
}
```

We will notify of any required version updates.

2b. (OPTIONAL) If using Auth0, add the following to that same module level build.gradle file:

- android -> defaultConfig -> the following manualPlaceholders:

```.gradle
android {
    ...

    defaultConfig {
        ...

        manifestPlaceholders = [auth0Domain: "@string/home_turf_com_auth0_domain", auth0Scheme: "@string/home_turf_com_auth0_scheme"]
    }
```

- dependencies -> auth0 + jwt:

```
dependencies {
    ...
    implementation 'com.github.HomeTurf-LLC:HomeTurfAndroidLibraryApp:1.0.0' # already added in 2a

    implementation 'com.auth0.android:auth0:2.0.0'
    implementation 'com.auth0.android:jwtdecode:2.0.0'
}
```

3. For file upload support: add a res -> xml resource folder -> `file_paths.xml` file with the following content:

At `res/xml/file_paths.xml`

```.xml
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-path
        name="external"
        path="." />
    <external-files-path
        name="external_files"
        path="." />
    <cache-path
        name="cache"
        path="." />
    <external-cache-path
        name="external_cache"
        path="." />
    <files-path
        name="files"
        path="." />
</paths>
```

4. Add the following string properties to your existing strings.xml file (Auth0 values can be left blank or left out if not using Auth0):

At `res/values/strings.xml`

```
<resources>
    ...
    <string name="home_turf_url">https://app.hometurfapp.com</string>
    <string name="home_turf_team_id">YOUR_TEAM_ID</string>
    <string name="home_turf_com_auth0_client_id"></string>
    <string name="home_turf_com_auth0_domain"></string>
    <string name="home_turf_com_auth0_audience"></string>
    <string name="home_turf_com_auth0_scheme"></string>
    <string name="home_turf_use_auth0">false</string>
</resources>
```

We will provide all required values for your team upon request.

5. Update your AndroidManifest file to include the following provider and HomeTurfWebViewActivity:

```
...
<application ...>
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="com.hometurf.testteamjava.fileprovider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths"></meta-data>
        </provider>
        .... # Existing MainActivity, etc.
        <activity
            android:name="com.hometurf.android.HomeTurfWebViewActivity"
            android:label="HomeTurf"
            android:theme="@android:style/Theme.NoTitleBar.Fullscreen"
            android:configChanges="keyboard|keyboardHidden|orientation|screenSize|uiMode"
            android:launchMode="singleTask"
            android:windowSoftInputMode="adjustPan"
            android:exported="true">
        </activity>
</application>
...
```

6. (OPTIONAL) If using Auth0, copy the [TestHomeTurfAuth0Service](./app/src/main/java/com/hometurf/testandroidteam/TeamHomeTurfAuth0Service.java) file from this project into your own project.

7. Add the following to your launching activity, and then hook up the function to the element in the layout that will launch the app:

Non-Auth0 version

```
    public void launchHomeTurf(View view) {
        // Start application and set contentView
        Intent i = new Intent(MainActivity.this, HomeTurfWebViewActivity.class);
        MainActivity.this.startActivity(i);
    }
```

Auth0 version

```
    public void launchHomeTurf(View view) {
        // Start application and set contentView
        Resources applicationContextResources = getApplicationContext().getResources();
        String useNativeAuth0 = applicationContextResources.getString(com.hometurf.android.R.string.home_turf_use_auth0);
        if (useNativeAuth0.equals("true")) {
            String auth0Audience = applicationContextResources.getString(com.hometurf.android.R.string.home_turf_com_auth0_audience);
            String auth0ClientId = applicationContextResources.getString(com.hometurf.android.R.string.home_turf_com_auth0_client_id);
            String auth0Domain = applicationContextResources.getString(com.hometurf.android.R.string.home_turf_com_auth0_domain);
            String scheme = applicationContextResources.getString(com.hometurf.android.R.string.home_turf_com_auth0_scheme);
            HomeTurfWebViewActivity.setAuth0Service(new TeamHomeTurfAuth0Service(auth0Audience, auth0ClientId, auth0Domain, scheme));
        }
        Intent i = new Intent(MainActivity.this, HomeTurfWebViewActivity.class);
        MainActivity.this.startActivity(i);
    }
```

Note that SMS auth can still be used instead of Auth0 (in case of transitioning, etc.) by setting the `home_turf_use_auth0` to false even if other Auth0 setup has been completed.

8. Gradle sync, run and verify that the app works!

## Updates

Generally updates will only require an update to the HomeTurf version and a subsequent gradle sync.

## Support

Feel free to reach out to HomeTurf if you have any issues with the above steps or need any configuration information.
