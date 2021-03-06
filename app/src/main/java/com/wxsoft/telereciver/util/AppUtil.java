package com.wxsoft.telereciver.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.List;

public class AppUtil {

    public static TextView getTagTextView(Context context, String tagName) {
        TextView tagView = new TextView(context);
        tagView.setText(tagName);
        tagView.setMaxLines(1);
        int horizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics());
        int verticalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, context.getResources().getDisplayMetrics());
        tagView.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        tagView.setBackgroundResource(R.drawable.primary_tag_bg);
        tagView.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
        tagView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3, context.getResources().getDisplayMetrics());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, margin, 0);
        tagView.setLayoutParams(params);
        tagView.setEllipsize(TextUtils.TruncateAt.END);
        return tagView;
    }

    public static boolean saveImageToGallery(Context context, String fileName, Bitmap bmp) {
        String tmpDirPath = AppContext.getTmpPath();
        File dir = new File(tmpDirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        File file = new File(dir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            //??????io?????????????????????????????????
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();

            //??????????????????????????????
            MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(), fileName, null);
            //????????????????????????????????????????????????
            Uri uri = Uri.fromFile(file);
            context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));

            if (isSuccess) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ???????????????????????????????????????????????????
     * Created by cafeting on 2017/2/4.
     *
     * @param context     ?????????
     * @param packageName ???????????????
     * @return true ?????????????????????false ??????????????????
     */
    public static boolean isAppRunning(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (list.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    public static String toMD5Code(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(bytes);
            byte[] after = md5.digest();

            for (int i = 0; i < after.length; i++) {
                String hex = Integer.toHexString(0xff & after[i]);
                if (hex.length() == 1)
                    hex = "0" + hex;
                sb.append(hex);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return sb.toString();
    }

}
