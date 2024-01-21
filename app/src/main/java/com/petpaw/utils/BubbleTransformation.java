package com.petpaw.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;

import com.squareup.picasso.Transformation;

public class BubbleTransformation implements Transformation {
    private static final int outerMargin = 0;

    private final int margin;  // dp

    // margin is the board in dp
    public BubbleTransformation(final int margin) {
        this.margin = margin;
    }




    @Override
    public Bitmap transform(final Bitmap source) {
//        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(output);
//        final Paint paint = new Paint();
//        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
//
//        paint.setAntiAlias(true);
//        canvas.drawARGB(0, 0, 0, 0);
//        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
//        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
//        canvas.drawBitmap(bitmap, rect, rect, paint);
//
//        return output;

        Bitmap output = Bitmap.createBitmap(source.getWidth(), source.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        Paint paintBorder = new Paint();
        paintBorder.setColor(0x2FA6A7);
        paintBorder.setStrokeWidth(margin);
        canvas.drawRoundRect(new RectF(outerMargin, outerMargin, source.getWidth() - outerMargin, source.getHeight() - outerMargin), 0, 0, paintBorder);

        Paint trianglePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        trianglePaint.setStrokeWidth(2);
        trianglePaint.setColor(0x3E7EFF);
        trianglePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        trianglePaint.setAntiAlias(true);

        Path triangle = new Path();
        triangle.setFillType(Path.FillType.EVEN_ODD);
        triangle.moveTo(outerMargin, source.getHeight() / 2);
        triangle.lineTo(source.getWidth()/2,source.getHeight());
        triangle.lineTo(source.getWidth()-outerMargin,source.getHeight()/2);
        triangle.close();

        canvas.drawPath(triangle, trianglePaint);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setShader(new BitmapShader(source, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
        canvas.drawRoundRect(new RectF(margin+outerMargin, margin+outerMargin, source.getWidth() - (margin + outerMargin), source.getHeight() - (margin + outerMargin)), 0, 0, paint);

        if (source != output) {
            source.recycle();
        }

        return output;
    }

    @Override
    public String key() {
        return "rounded";
    }
}
