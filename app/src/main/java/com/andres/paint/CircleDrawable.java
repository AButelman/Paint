package com.andres.paint;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.support.annotation.MainThread;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class CircleDrawable extends View {

    private ShapeDrawable shapeDrawable;
    private DisplayMetrics displayMetrics;
    private int screenWidth, screenHeight, centerX, centerY, x, y, side;
    private int touchedX, touchedY;
    private Rect bounds;
    // private boolean isCentered;

    public CircleDrawable(Context context, AttributeSet attrs) {
        this(context);
    }

    public CircleDrawable(Context context) {
        super(context);

        side = MainActivity.paintView.getCurrentStrokeWidth();
        displayMetrics = MainActivity.getScreenSize(context);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        shapeDrawable = new ShapeDrawable(new OvalShape());
        shapeDrawable.getPaint().setAntiAlias(true);

        shapeDrawable.getPaint().setColor(MainActivity.paintView.getCurrentStrokeColor());
        shapeDrawable.getPaint().setAlpha(MainActivity.paintView.getCurrentStrokeOpacity());
        centerCircle();
        calculatePosition();

    }

    public void reCenterCircle() {
        centerCircle();
        calculatePosition();
    }

    private void centerCircle() {

        centerX = screenWidth / 2;
        centerY = screenHeight / 2;
    }

    private void calculatePosition() {
        x = centerX - side / 2;
        y = centerY - side / 2;
        shapeDrawable.setBounds(x, y, x + side, y + side);
        this.bounds = shapeDrawable.getBounds();
    }

    public void setColor(int color) {

        shapeDrawable.getPaint().setColor(color);
        this.postInvalidate();
    }

    public void setOpacity(int opacity) {
        shapeDrawable.getPaint().setAlpha(opacity);
        this.postInvalidate();
    }

    public void setSide(int side) {
        this.side = side;
        calculatePosition();
        this.postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        shapeDrawable.draw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                touchedX = centerX - (int) event.getX();
                touchedY = centerY - (int) event.getY();

                break;

            case MotionEvent.ACTION_MOVE:

                centerX = (int) event.getX() + touchedX;
                centerY = (int) event.getY() + touchedY;

                /*
                Log.e("Moviendo a X", String.valueOf(x));
                Log.e("Moviendo a Y", String.valueOf(y));
                */
                calculatePosition();
                postInvalidate();

                break;

            case MotionEvent.ACTION_UP:
                break;

        }
        return true;
    }

    public Rect getBounds() { return bounds; }
}
