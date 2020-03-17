package com.wxsoft.telereciver.ui.fragment.user.sign;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.LinearLayout;

import com.wxsoft.telereciver.AppConstant;
import com.wxsoft.telereciver.AppContext;
import com.wxsoft.telereciver.R;
import com.wxsoft.telereciver.ui.base.BaseActivity;
import com.wxsoft.telereciver.ui.widget.PaletteView;
import com.wxsoft.telereciver.util.DensityUtil;
import com.wxsoft.telereciver.util.FileUtil;
import com.wxsoft.telereciver.util.ViewUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ModifySignFragment extends BaseActivity {

    public static void launch(Activity from) {
        Intent intent = new Intent(from, ModifySignFragment.class);
        from.startActivityForResult(intent,REQUEST_MODIFY_SIGN);

    }

    public static final int REQUEST_MODIFY_SIGN = 57;
    public static final String KEY_SIGN_URL = "KEY_SIGN_URL";

    @BindView(R.id.palette)
    PaletteView mPaletteView;

    @BindView(R.id.ll_paint_stroke_width)
    LinearLayout mPaintStrokeWidthView;

    @BindView(R.id.v_low)
    View mLowView;

    @BindView(R.id.v_middle)
    View mMiddleView;

    @BindView(R.id.v_high)
    View mHighView;

    @OnClick(R.id.iv_paint_width)
    void paintWidthClick() {
        if (mPaintStrokeWidthView.getVisibility() == View.GONE) {
            mPaintStrokeWidthView.setVisibility(View.VISIBLE);
        } else {
            mPaintStrokeWidthView.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.fl_low)
    void lowClick() {
        setAllStrokeUncheck();
        mLowView.setBackgroundResource(R.drawable.ic_paint_checked_bg);
        mPaletteView.setPenRawSize(DensityUtil.dip2px(this, 4));
    }

    @OnClick(R.id.fl_middle)
    void middleClick() {
        setAllStrokeUncheck();
        mMiddleView.setBackgroundResource(R.drawable.ic_paint_checked_bg);
        mPaletteView.setPenRawSize(DensityUtil.dip2px(this, 8));
    }

    @OnClick(R.id.fl_high)
    void highClick() {
        setAllStrokeUncheck();
        mHighView.setBackgroundResource(R.drawable.ic_paint_checked_bg);
        mPaletteView.setPenRawSize(DensityUtil.dip2px(this, 12));
    }

    @OnClick(R.id.btn_clear)
    void clearClick() {
        mPaletteView.clear();
    }

    @OnClick(R.id.btn_commit)
    void commitClick() {
        if (!mPaletteView.canUndo()) {
            ViewUtil.showMessage("请写入签名");
            return;
        }

        ViewUtil.createProgressDialog(this, "提交中...");
        Bitmap bmp = mPaletteView.buildBitmap();
        String filePath = save(bmp);
        if (filePath == null) {
            ViewUtil.dismissProgressDialog();
            ViewUtil.showMessage("保存图片失败");
        } else {
            commit(filePath);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_modify_sign;
    }

    @Override
    public void setupViews( Bundle savedInstanceState) {
        setupToolbar();
        //强制横屏
        //_mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }

    private void setupToolbar() {

        ActionBar actionBar=getSupportActionBar();
        if(actionBar==null)return;
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(R.string.sign_modify_title);
    }

    private void setAllStrokeUncheck() {
        mPaintStrokeWidthView.setVisibility(View.GONE);
        mLowView.setBackgroundResource(R.drawable.ic_paint_unchecked_bg);
        mMiddleView.setBackgroundResource(R.drawable.ic_paint_unchecked_bg);
        mHighView.setBackgroundResource(R.drawable.ic_paint_unchecked_bg);
    }

    private String save(Bitmap bmp) {
        if (bmp == null) {
            return null;
        }

        File dir = new File(AppContext.getTmpPath());
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(dir, fileName);
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

    private void commit(final String filePath) {
        File file = new File(filePath);
        String fileName = FileUtil.getFileName(file);
        MultipartBody.Builder builder = new MultipartBody.Builder().setType(MultipartBody.FORM)
                .addFormDataPart("UserId", AppContext.getUser().getId());
        builder.addFormDataPart("file", fileName, RequestBody.create(MediaType.parse("image/*"), file));

        String url = AppConstant.BASE_URL + "api/Platform/UploadUserSignatureImg?isMobile=true";
        Request request = new Request.Builder()
                .url(url)
                .post(builder.build())//传参数、文件或者混合，改一下就行请求体就行
                .build();

        new OkHttpClient().newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    ViewUtil.dismissProgressDialog();
                    ViewUtil.showMessage(e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) {
                runOnUiThread(() -> {
                    ViewUtil.dismissProgressDialog();
                    try {
                        String str = response.body().string();
                        JSONObject jsonObject = new JSONObject(str);
                        String url = jsonObject.getString("data");
                        Intent intent = new Intent();
                        intent.putExtra(KEY_SIGN_URL, url);
                        setResult(RESULT_OK, intent);
                        finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        });
    }
}
