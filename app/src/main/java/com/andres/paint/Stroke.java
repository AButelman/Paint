package com.andres.paint;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;

public class Stroke {

    private Path path;
    private Paint paint;

    public Stroke(float x, float y, Paint paint) {

        this.path = new Path();
        this.path.moveTo(x, y);

        this.paint = paint;
    }

    public void lineTo(float x, float y) {

        path.lineTo(x, y);
    }

    public Path getPath() {
        return path;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }

}
