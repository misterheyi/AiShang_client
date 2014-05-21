package com.aishang.app.common;

import android.content.Context;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapCommonUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;

/**
 * Author: wyouflf
 * Date: 13-11-12
 * Time: 上午10:24
 */
public class BitmapHelp {
    private BitmapHelp() {
    }

    private static BitmapUtils bitmapUtils;
    /**
     * BitmapUtils不是单例的 根据需要重载多个获取实例的方法
     *
     * @param appContext application context
     * @return
     */
    public static BitmapUtils getBitmapUtils(Context appContext) {
        if (bitmapUtils == null) {
            bitmapUtils = new BitmapUtils(appContext,"aishang");
			//bitmapUtils.configDefaultBitmapMaxSize(BitmapCommonUtils
					//.getScreenSize(appContext.getApplicationContext()));
            bitmapUtils.configDiskCacheEnabled(true);
            bitmapUtils.configMemoryCacheEnabled(true);
        }
        return bitmapUtils;
    }
}
