package com.wxsoft.teleconsultation.util;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.module.GlideModule;

/**
 * Created by liping on 2018/3/19.
 */
public class GlideConfiguration implements GlideModule {

    // 图片缓存最大容量，150M，根据自己的需求进行修改
    public static final int GLIDE_CATCH_SIZE = 150 * 1000 * 1000;

    // 图片缓存子目录
    public static final String GLIDE_CATCH_DIR = "image_catch";

    // 需要在AndroidManifest.xml中声明
    // <meta-data
    //    android:name="com.llsy.models.util.GlideConfiguration"
    //    android:value="GlideModule" />

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        //自定义缓存目录
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context,
                GLIDE_CATCH_DIR,
                GLIDE_CATCH_SIZE));
    }


    @Override
    public void registerComponents(Context context, Glide glide, Registry registry) {

    }
}
