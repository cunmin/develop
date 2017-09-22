package com.person.framework.http.imageloader.glide.transformation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.Log;

import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * Created by Administrator on 2017/2/7.
 */

public class GlideCutTransform extends BitmapTransformation {

    private float startAngle = 90f;
    private float endAngle = 45f;
    private int baseColor = Color.TRANSPARENT;

    public GlideCutTransform(Context context) {
        super(context);
    }

    public GlideCutTransform(Context context,float startAngle,float endAngle, int baseColor) {
        super(context);
        this.baseColor = baseColor;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
    }

    @Override protected Bitmap transform(BitmapPool pool, Bitmap toTransform, int outWidth, int outHeight) {
        return crop(pool, toTransform);
    }

    private  Bitmap crop(BitmapPool pool, Bitmap source) {
        Log.d("crop","source is null:"+(source==null?true:false));
        if (source == null) return null;

        int w = source.getWidth();
        int h = source.getHeight();

        Bitmap result = pool.get(w, h, Bitmap.Config.ARGB_8888);
        if (result == null) {
            result = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        }

        Path path = new Config().getPath(h, w, startAngle, endAngle);
        Canvas canvas = new Canvas(result);
        Paint paint = new Paint(ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        if (baseColor == Color.TRANSPARENT) {
            paint.setShader(new BitmapShader(source, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP));
        }else{
            paint.setColor(baseColor);
            paint.setAlpha(255);
        }
        canvas.drawPath(path, paint);
        return result;
    }

    @Override public String getId() {
        return "cut"+String.valueOf(startAngle+endAngle+baseColor);
    }

    public static class Config {
        public int getTanWithOutConflict(float h, float w, float angle, double hype) {
            if (angle > 90) {
                angle = 180 - angle;
            }
            int val = (int) Math.ceil(h / Math.tan(Math.toRadians(angle)));
            if (val <= hype) {
                return val;
            }
            int p = (int) Math.ceil(w * Math.tan(Math.toRadians(angle)));
            return getTanWithOutConflict(p, w, angle, hype);
        }


        public Path getPath(float h, float w, float left_angle, float right_angle) {

            double hyp = Math.hypot(w, h);
            int left_base = getTanWithOutConflict(h, w, left_angle, hyp);
            int right_base = getTanWithOutConflict(h, w, right_angle, hyp);
            float a1 = 0, a2 = 0, b1 = w, b2 = 0, c1 = w, c2 = h, d1 = 0, d2 = h;
            try {
                if (left_angle > 90 & left_angle <= 180) {
                    if (left_angle > 145) {
                        d1 = w;
                        d2 = (float) Math.ceil(w * Math.tan(Math.toRadians(180 - left_angle)));
                    } else {
                        d1 = Math.abs(left_base);
                    }
                } else {
                    a1 = Math.abs(left_base);
                    if (a1 == 0) {
                        a1 = w;
                    }
                    if (left_angle < 45)
                        a2 = h - (float) Math.ceil(w * Math.tan(Math.toRadians(left_angle)));
                }

                if (right_angle > 90) {

                    if (right_angle > 135) {
                        b1 = 0;
                        b2 = h - (float) Math.ceil(w * Math.tan(Math.toRadians(180 - right_angle)));
                    } else {
                        b1 = Math.abs(w - right_base);//w - right_base;
                    }

                } else if (right_angle <= 180) {
                    if (right_angle < 45) {
                        c2 = (float) Math.floor(w * Math.tan(Math.toRadians(right_angle)));
                        c1 = 0;
                    } else {
                        c1 = w - Math.abs(right_base);//w - right_base;
                    }
                }

        /*    Log.e("left_angle " + left_angle + "  right_angle " + right_angle,
                    " | a1 " + a1 + " | a2 " + a2 + " | b1 " + b1 + " | b2 " + b2 + " | c1 " + c1 + " | c2 " + c2 + " | d1 " + d1 + " | d2 " + d2);
        */
            } catch (Exception e) {
                Log.e("exception", "" + e.getMessage());
            }
            Path path = new Path();
            path.moveTo(a1, a2);
            path.lineTo(b1, b2);
            path.lineTo(c1, c2);
            path.lineTo(d1, d2);
            path.lineTo(a1, a2);
            path.close();
            return path;
        }

    }
}