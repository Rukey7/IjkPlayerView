# IjkPlayerView
[![Apache 2.0 License](https://img.shields.io/badge/license-Apache%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0.html) [![](https://jitpack.io/v/Rukey7/IjkPlayerView.svg)](https://jitpack.io/#Rukey7/IjkPlayerView)

IjkPlayerView is an media player for Android base on [ijkplayer](https://github.com/Bilibili/ijkplayer). It used to play video both locally and over the Internet.

## Screenshot

![](https://raw.githubusercontent.com/Rukey7/ScreenShot/master/IjkPlayerView/horizontal3.png)
![](https://raw.githubusercontent.com/Rukey7/ScreenShot/master/IjkPlayerView/horizontal.png)
![](https://raw.githubusercontent.com/Rukey7/ScreenShot/master/IjkPlayerView/send_danmaku.png)
![](https://raw.githubusercontent.com/Rukey7/ScreenShot/master/IjkPlayerView/danmaku_h.gif)
![](https://raw.githubusercontent.com/Rukey7/ScreenShot/master/IjkPlayerView/scale.gif)

## Using IjkPlayerView

You need to make sure you have the JitPack repository included inthe `build.gradle` file in the root of your project:

```gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Next, include the following in your module's `build.gradle` file:

```gradle
compile 'com.github.Rukey7:IjkPlayerView:{lastest-version}'
```

where `{lastest-version}` is the your preferred version. For the latest version, see the
project's [Releases][]. For more details, see the project on [jitpack.io](https://jitpack.io/#Rukey7/IjkPlayerView).

## Usage

Add the configuration to your Activity in the AndroidManifest.xml:

```xml
	<activity  
	    android:name=".IjkPlayerActivity"  
	    android:configChanges="orientation|keyboardHidden|screenSize"/>
```

Using IjkPlayerView is like to use other widgets in your layout:

```xml
	<com.dl7.player.media.IjkPlayerView  
	    android:id="@+id/player_view"  
	    android:layout_width="match_parent"  
	    android:layout_height="200dp"/>  
```

Finally, you should do this in your activity:

```java

	public class IjkPlayerActivity extends AppCompatActivity {  
  
	    private IjkPlayerView mPlayerView;  
	  
	    @Override  
	    protected void onCreate(Bundle savedInstanceState) {  
	        super.onCreate(savedInstanceState);  
	        setContentView(R.layout.activity_ijk_player);  
	        setSupportActionBar(mToolbar);  
	        //  Choose any one interface you need, init() must be the first to use.
	        Glide.with(this).load(IMAGE_URL).fitCenter().into(mPlayerView.mPlayerThumb); // Show the thumb before play
	        mPlayerView.init()              // Initialize, the first to use 
	                .setTitle("Title")  	// set title  
	                .setSkipTip(1000*60*1)  // set the position you want to skip  
	                .enableOrientation()    // enable orientation 
	          //      .setVideoPath(VIDEO_URL)    // set video url  
	                .setVideoSource(null, VIDEO_URL, VIDEO_URL, VIDEO_URL, null) // set multiple video url  
	                .setMediaQuality(IjkPlayerView.MEDIA_QUALITY_HIGH)  // set the initial video url
	                .enableDanmaku()        // enable Danmaku  
	                .setDanmakuSource(getResources().openRawResource(R.raw.comments)) // add Danmaku source, you need to use enableDanmaku() first 
	                .start();   // Start playing 
	    }  
	  
	    @Override  
	    protected void onResume() {  
	        super.onResume();  
	        mPlayerView.onResume();  
	    }  
	  
	    @Override  
	    protected void onPause() {  
	        super.onPause();  
	        mPlayerView.onPause();  
	    }  
	  
	    @Override  
	    protected void onDestroy() {  
	        super.onDestroy();  
	        mPlayerView.onDestroy();  
	    }  
	  
	    @Override  
	    public void onConfigurationChanged(Configuration newConfig) {  
	        super.onConfigurationChanged(newConfig);  
	        mPlayerView.configurationChanged(newConfig);  
	    }  
	  
	    @Override  
	    public boolean onKeyDown(int keyCode, KeyEvent event) {  
	        if (mPlayerView.handleVolumeKey(keyCode)) {  
	            return true;  
	        }  
	        return super.onKeyDown(keyCode, event);  
	    }  
	  
	    @Override  
	    public void onBackPressed() {  
	        if (mPlayerView.onBackPressed()) {  
	            return;  
	        }  
	        super.onBackPressed();  
	    } 
 	}   

```

If you want keep fullscreen, you can do this:
```java
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPlayerView = new IjkPlayerView(this);
        setContentView(mPlayerView);
        Glide.with(this).load(IMAGE_URL).fitCenter().into(mPlayerView.mPlayerThumb);
        mPlayerView.init()
                .setTitle("Title")
                .alwaysFullScreen()			// keep fullscreen
                .setVideoPath(VIDEO_URL)	
                .start();
    }

```

You can use custom data type of Danmaku. For more details please see the example.
```java
	
    mPlayerView.init()
            .enableDanmaku()
            .setDanmakuCustomParser(new DanmakuParser(), DanmakuLoader.instance(), DanmakuConverter.instance())
            .setDanmakuSource(stream)
            .setVideoPath(VIDEO_URL)	
            .setDanmakuListener(new OnDanmakuListener<DanmakuData>() {
                    @Override
                    public boolean isValid() {
                        return true;
                    }

                    @Override
                    public void onDataObtain(DanmakuData data) {
                    }
                });

```


### Other

There will be some problems in fullscreen when you use do the following options:

- Use *android:fitssystemwindows="true"* 
- Use [SystemBarTint](https://github.com/jgilfelt/SystemBarTint)

Ensure IjkPlayerView is on the top of your layout except ToolBar.

### Thanks

- [ijkplayer](https://github.com/Bilibili/ijkplayer)
- [DanmakuFlameMaster](https://github.com/Bilibili/DanmakuFlameMaster)
- [jjdxm_ijkplayer](https://github.com/jjdxmashl/jjdxm_ijkplaye)

License
-------

    Copyright 2016 Rukey7

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
