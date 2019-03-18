package com.andres.paint;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class PaintView extends View implements DoubleTapDetector.OnDoubleTapListener  {

    public static final int DEFAULT_STROKE_COLOR = Color.BLACK;
    public static final int DEFAULT_BACKGROUND_COLOR = Color.WHITE;
    public static final int DEFAULT_STROKE_WIDTH = 5;
    public static final int DEFAULT_STROKE_OPACITY = 255;

    private DoubleTapDetector doubleTapDetector;

    private Stroke currentStroke;
    private int backgroundColor, currentStrokeColor, currentStrokeWidth, currentStrokeOpacity;
    private Bitmap currentBitmap;

    private ArrayList<Stroke> strokes;

    public PaintView(Context context, AttributeSet attrs) {
        super(context, attrs);

        strokes = new ArrayList<>();

        // Procesamos los atributos XML personalizados
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PaintView);

        backgroundColor = a.getColor(R.styleable.PaintView_background_color, DEFAULT_BACKGROUND_COLOR);
        currentStrokeColor = a.getColor(R.styleable.PaintView_stroke_color, DEFAULT_STROKE_COLOR);
        currentStrokeWidth = a.getInt(R.styleable.PaintView_stroke_width, DEFAULT_STROKE_WIDTH);
        currentStrokeOpacity = a.getInt(R.styleable.PaintView_stroke_opacity, DEFAULT_STROKE_OPACITY);

        a.recycle();
    }

    public int getCurrentStrokeColor() {
        return currentStrokeColor;
    }

    public void setCurrentStrokeColor(int strokeColor) {
        this.currentStrokeColor = strokeColor;
    }

    public int getCurrentStrokeOpacity() { return currentStrokeOpacity; }

    public void setCurrentStrokeOpacity(int strokeOpacity) { this.currentStrokeOpacity = strokeOpacity; }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    @Override
    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public int getCurrentStrokeWidth() {
        return currentStrokeWidth;
    }

    public void setCurrentStrokeWidth(int strokeWidth) { this.currentStrokeWidth = strokeWidth; }

    public Bitmap getCurrentBitmap() {
        return currentBitmap;
    }

    public void setCurrentBitmap(Bitmap currentBitmap) {
        this.currentBitmap = currentBitmap;
    }

    private Paint createPaint() {

        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setColor(currentStrokeColor);
        paint.setStrokeWidth(currentStrokeWidth);
        paint.setAlpha(currentStrokeOpacity);

        // NO ANDA EL FILTRO BLUR
        // paint.setMaskFilter(new BlurMaskFilter(50, BlurMaskFilter.Blur.NORMAL));

        return paint;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                Paint paint = createPaint();
                if (currentBitmap == null) {
                    currentStroke = new Stroke(x, y, paint);
                } else {
                    currentStroke = new BitmapStroke(x, y, paint, currentBitmap);
                }

                strokes.add(currentStroke);
                return true;

            case MotionEvent.ACTION_UP:

                if (doubleTapDetector == null) {

                    doubleTapDetector = new DoubleTapDetector();
                    doubleTapDetector.setOnDoubleTapListener(this);
                    doubleTapDetector.start();

                } else {

                    if (doubleTapDetector.isAlive() && !doubleTapDetector.isInterrupted()) {

                        doubleTapDetector.tap();
                    }
                }

                break;

            case MotionEvent.ACTION_MOVE:

                if (currentStroke instanceof BitmapStroke) {

                    ((BitmapStroke) currentStroke).addPoint(event.getX(), event.getY());
                } else {

                    currentStroke.lineTo(x, y);
                }

                break;
            default:
                return false;
        }

        postInvalidate();

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(backgroundColor);

        for (Stroke stroke : strokes) {

            if (stroke instanceof BitmapStroke) {

                BitmapStroke bitmapStroke = (BitmapStroke) stroke;
                bitmapStroke.calculateRotations();

                Bitmap bitmap = bitmapStroke.getBitmap();
                Paint paint = bitmapStroke.getPaint();

                Point point;
                Matrix matrix;
                double rotation;
                Bitmap rotatedBitmap;

                while (bitmapStroke.hasNextPoint()) {

                    point = bitmapStroke.getPoint();
                    rotation = bitmapStroke.getRotation();

                    // Log.e("ROTATION", String.valueOf(rotation));

                    if (rotation != 0) {

                        matrix = new Matrix();
                        matrix.postRotate((float) rotation);
                        rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
                                bitmap.getWidth(), bitmap.getHeight(),
                                matrix, true);

                        canvas.drawBitmap(rotatedBitmap, point.x, point.y, paint);
                    } else {
                        
                        canvas.drawBitmap(bitmap, point.x, point.y, paint);
                    }

                    bitmapStroke.next();
                }

                bitmapStroke.resetIndex();

            } else {

                canvas.drawPath(stroke.getPath(), stroke.getPaint());
            }

            /* PRUEBA CON TEXTO
            canvas.drawTextOnPath("Esto es un texto bastante largo para probar que onda",
                    stroke.getPath(), 10, 40, stroke.getPaint()); */
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(calculateDimension(widthMeasureSpec), calculateDimension(heightMeasureSpec));
    }

    private int calculateDimension(int specs) {
        int res = 100; //Default

        int mode = MeasureSpec.getMode(specs);
        int limit = MeasureSpec.getSize(specs);

        if (mode == MeasureSpec.AT_MOST || mode == MeasureSpec.EXACTLY) {
            res = limit;
        }

        return res;
    }

    @Override
    public void onDoubleTap() {
        strokes.clear();
    }

    @Override
    public void onDetectorStopped() {
        doubleTapDetector = null;
    }

    public int getCurrentStrokeColorWithOpacity() {
        return currentStrokeColor + currentStrokeOpacity * 16777216; // * 2 a la 24, para pasarlo a donde va
    }

    public void removeLastStroke () {
        strokes.remove(strokes.size() - 1);
        postInvalidate();
    }
}
