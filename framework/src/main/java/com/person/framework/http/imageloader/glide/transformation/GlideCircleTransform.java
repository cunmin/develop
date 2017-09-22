package com.person.framework.http.imageloader.glide.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

/**
 * Created by Administrator on 2017/2/7.
 */

public class GlideCircleTransform extends BitmapTransformation {

    private int BORDER_COLOR = 0xffffffff;
    private int BORDER_WITH = 20;

    public GlideCircleTransform(Context context) {
        super(context);
    }

    public GlideCircleTransform(Context context,int borderColor,int borderWith) {
        super(context);
        BORDER_COLOR = borderColor;
        BORDER_WITH = borderWith;
    }

    @Override protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return circleCrop(pool, toTransform);
    }

    private  Bitmap circleCrop(BitmapPool pool, Bitmap source) {
        if (source == null) return null;

        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        // TODO this could be acquired from the pool too
        Bitmap squared = Bitmap.createBitmap(source, x, y, size, size);

        Bitmap result = pool.get(size, size, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        }

        float r = size / 2f;
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint();

        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new BitmapShader(squared, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        canvas.drawCircle(r, r, r-BORDER_WITH, paint);

        paint.reset();
        paint.setDither(true);
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(BORDER_WITH);
        paint.setColor(Color.parseColor("#F44336"));
        canvas.drawCircle(r, r, r-BORDER_WITH/2, paint);
        return result;
    }

    @Override public String getId() {
        return String.valueOf(BORDER_COLOR+BORDER_WITH);
    }
}