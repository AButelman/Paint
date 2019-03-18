package com.andres.paint;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Main2Activity extends AppCompatActivity {

    private BitmapStroke bitmapStroke;
    private Paint paint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(Color.BLUE);
        paint.setStrokeWidth(5);

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dry_brush_stroke_2);
        bitmapStroke = new BitmapStroke(100, 100, paint, bitmap);

        for (int i = 99; i >= 10; i -= 10) {
            bitmapStroke.addPoint(i, i);
        }

        bitmapStroke.calculateRotations();

        int rotation;

        while (bitmapStroke.hasNextPoint()) {

            rotation = bitmapStroke.getRotation();

            Log.e("ROTATION at index " + bitmapStroke.getIndex(), String.valueOf(rotation));

            bitmapStroke.next();
        }
    }
}
