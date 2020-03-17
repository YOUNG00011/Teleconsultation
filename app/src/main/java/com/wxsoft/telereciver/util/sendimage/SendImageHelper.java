package com.wxsoft.telereciver.util.sendimage;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.Toast;

import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.entity.LocalMedia;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.util.FileUtil;

import java.io.File;
import java.util.List;

public class SendImageHelper {

    public interface Callback {
        void sendImage(File file, boolean isOrig);
    }

    public static void sendImageAfterSelfImagePicker(Context context, Intent data, final Callback callback) {

        List<LocalMedia> photos = PictureSelector.obtainMultipleResult(data);
        if (photos == null) {
            Toast.makeText(context, R.string.picker_image_error, Toast.LENGTH_LONG).show();
            return;
        }

        for (LocalMedia photoInfo : photos) {
            new SendImageTask(context, false, photoInfo, new Callback() {
                @Override
                public void sendImage(File file, boolean isOrig) {
                    if (callback != null) {
                        callback.sendImage(file, isOrig);
                    }
                }
            }).execute();
        }
    }

    // 从相册选择图片进行发送(Added by NYB)
    public static class SendImageTask extends AsyncTask<Void, Void, File> {

        private Context context;
        private boolean isOrig;
        private LocalMedia info;
        private Callback callback;

        public SendImageTask(Context context, boolean isOrig, LocalMedia info,
                             Callback callback) {
            this.context = context;
            this.isOrig = isOrig;
            this.info = info;
            this.callback = callback;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected File doInBackground(Void... params) {
            String photoPath = info.getPath();
            if (TextUtils.isEmpty(photoPath))
                return null;
            if (isOrig) {
                // 把原图按md5存放
                String origMD5 = MD5.getStreamMD5(photoPath);
                String extension = FileUtil.getFileExtension(photoPath);
                String origMD5Path = StorageUtil.getWritePath(origMD5
                        + extension, StorageType.TYPE_IMAGE);
//                AttachmentStore.copy(photoPath, origMD5Path);
                // 生成缩略图
                File imageFile = new File(origMD5Path);
                ImageUtil.makeThumbnail(context, imageFile);

                return new File(origMD5Path);
            }
            else {
                File imageFile = new File(photoPath);
//                String mimeType = FileUtil.getFileExtension(photoPath).substring(1);
//
//                imageFile = ImageUtil.getScaledImageFileWithMD5(imageFile, mimeType);
                if (imageFile == null) {
                    new Handler(context.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(context, R.string.picker_image_error, Toast.LENGTH_LONG).show();
                        }
                    });
                    return null;
                } else {
                    ImageUtil.makeThumbnail(context, imageFile);
                }

                return imageFile;
            }
        }

        @Override
        protected void onPostExecute(File result) {
            super.onPostExecute(result);

            if (result != null) {
                if (callback != null) {
                    callback.sendImage(result, isOrig);
                }
            }
        }
    }
}
