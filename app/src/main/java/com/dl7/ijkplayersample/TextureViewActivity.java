package com.dl7.ijkplayersample;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.TextureView;
import android.widget.FrameLayout;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TextureViewActivity extends AppCompatActivity implements TextureView.SurfaceTextureListener {


    @BindView(R.id.texture_view)
    TextureView mTextureView;

    private Camera mCamera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_texture_view);
        ButterKnife.bind(this);
        mTextureView.setSurfaceTextureListener(this);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        mCamera = Camera.open();
        Camera.Size previewSize = mCamera.getParameters().getPreviewSize();
        mTextureView.setLayoutParams(new FrameLayout.LayoutParams(
                previewSize.width, previewSize.height, Gravity.CENTER));
        try {
            mCamera.setPreviewTexture(surface);
        } catch (IOException t) {
        }
        mCamera.startPreview();
        mTextureView.setAlpha(0.5f);
        mTextureView.setRotation(45.0f);
        Log.e("TextureViewActivity", "onSurfaceTextureAvailable");
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

        Log.w("TextureViewActivity", "onSurfaceTextureSizeChanged");
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        Log.d("TextureViewActivity", "onSurfaceTextureDestroyed");
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        Log.i("TextureViewActivity", "onSurfaceTextureUpdated");

    }
}
