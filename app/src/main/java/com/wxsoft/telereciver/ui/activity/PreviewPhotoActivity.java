package com.wxsoft.telereciver.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.ui.base.SupportBaseActivity;
import com.wxsoft.telereciver.util.FileUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PreviewPhotoActivity extends SupportBaseActivity {

    public static void launch(Activity from, String imageUrl) {
        Intent intent = new Intent(from, PreviewPhotoActivity.class);
        intent.putExtra(EXTRA_KEY_IMAGE_URL, imageUrl);
        from.startActivity(intent);
    }

    private static final String EXTRA_KEY_IMAGE_URL = "EXTRA_KEY_IMAGE_URL";

    @BindView(R.id.photoView)
    PhotoView mPhotoView;

    private String mImageUrl;
    private PhotoViewAttacher mAttacher;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_preview_photo;
    }

    @Override
    protected void setupViews(Bundle savedInstanceState) {
        mImageUrl = getIntent().getStringExtra(EXTRA_KEY_IMAGE_URL);
        setupPhotoView();
    }

    private void setupPhotoView() {
        mAttacher = new PhotoViewAttacher(mPhotoView);
        new AsyncTask<String, String, Bitmap>(){
            @Override
            protected Bitmap doInBackground(String... strings) {
                try {
                    return Glide.with(PreviewPhotoActivity.this)
                            .asBitmap()
                            .load(mImageUrl)
                            .submit()
                            .get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return null;
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap == null) {
                    ViewUtil.dismissProgressDialog();
                    ViewUtil.showMessage("下载图片失败");
                    return;
                }

                String avatarPath = save(bitmap);
                if (avatarPath != null) {
                    mPhotoView.setImageBitmap(BitmapFactory.decodeFile(avatarPath));
                    mAttacher.update();
                } else {
                    ViewUtil.dismissProgressDialog();
                    ViewUtil.showMessage("保存图片失败");
                }

            }
        }.execute(mImageUrl);
    }

    private String save(Bitmap bmp) {
        if (bmp == null) {
            return null;
        }

        File dir = new File(AppContext.getTmpPath());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = FileUtil.getFileName(mImageUrl);
        File file = new File(AppContext.getTmpPath(), fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            return file.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }
}
