# IjkPlayerView
[![Apache 2.0 License](https://img.shields.io/badge/license-Apache%202.0-blue.svg?style=flat)](http://www.apache.org/licenses/LICENSE-2.0.html) [![](https://jitpack.io/v/Rukey7/IjkPlayerView.svg)](https://jitpack.io/#Rukey7/IjkPlayerView)

IjkPlayerView是一个基于[ijkplayer](https://github.com/Bilibili/ijkplayer)的视屏播放库，可以用于播放本地和网络视频。

## Screenshot

![](https://raw.githubusercontent.com/Rukey7/ScreenShot/master/IjkPlayerView/horizontal3.png)
![](https://raw.githubusercontent.com/Rukey7/ScreenShot/master/IjkPlayerView/horizontal.png)
![](https://raw.githubusercontent.com/Rukey7/ScreenShot/master/IjkPlayerView/send_danmaku.png)
![](https://raw.githubusercontent.com/Rukey7/ScreenShot/master/IjkPlayerView/danmaku_h.gif)
![](https://raw.githubusercontent.com/Rukey7/ScreenShot/master/IjkPlayerView/scale.gif)

## Using IjkPlayerView

你需要在项目的根 `build.gradle` 加入如下JitPack仓库链接：

```gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

接着在你的需要依赖的Module的`build.gradle`加入依赖:

```gradle
compile 'com.github.Rukey7:IjkPlayerView:{lastest-version}'
```

其中 `{lastest-version}` 为最新的版本，你可以查看上面显示的jitpack版本信息，也可以到[jitpack.io](https://jitpack.io/#Rukey7/IjkPlayerView)仓库查看。

## Usage

在项目的AndroidManifest.xml文件中队activity进行如下配置：

```xml
	<activity  
	    android:name=".IjkPlayerActivity"  
	    android:configChanges="orientation|keyboardHidden|screenSize"/>
```

把IjkPlayerView作为一个控件添加到你的布局中：

```xml
	<com.dl7.player.media.IjkPlayerView  
	    android:id="@+id/player_view"  
	    android:layout_width="match_parent"  
	    android:layout_height="200dp"/>  
```

最后，在activity中你需要做一些功能上的控制处理，就如下面这样配置：

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

如果你要使用固定全屏播放，可以按下处理：

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

库里也提供了自定义弹幕的功能，可根据需要添加，更多信息请查看例子。

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

可能影响到沉浸式全屏的几个问题：

- 使用 *android:fitssystemwindows="true"* 属性
- 使用 [SystemBarTint](https://github.com/jgilfelt/SystemBarTint) 来渲染状态栏

事实上，你要确保在变换为全屏时IjkPlayerView控件能够填充整个屏幕，不然就会出现播放界面被挤压的情况。这个问题是因为全屏的时候是对当前的IjkPlayerView直接做宽高，所以有局限性，你可以参考别的播放库有别的实现方式来避免这个问题。

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
