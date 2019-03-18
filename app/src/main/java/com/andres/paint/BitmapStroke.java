package com.andres.paint;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.ColorFilter;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathDashPathEffect;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;

public class BitmapStroke extends Stroke {

    private Bitmap bitmap;
    private ColorFilter colorFilter;
    private ArrayList<Point> points;
    private int[] rotations;
    private int index;

    public BitmapStroke(float x, float y, Paint paint, Bitmap resourceBitmap) {
        super (x, y, paint);

        points = new ArrayList<>();
        addPoint(x, y);

        index = 0;

        // Cambiamos el bitmap al tamaño deseado
        int newWidth = (int) (paint.getStrokeWidth() * (resourceBitmap.getWidth() / resourceBitmap.getHeight()));
        bitmap = Bitmap.createScaledBitmap(resourceBitmap, newWidth,
                                            (int) paint.getStrokeWidth(), false);


        // Filtramos la imagen con el color seleccionado
        colorFilter= new PorterDuffColorFilter(paint.getColor(), PorterDuff.Mode.SRC_IN);
        this.getPaint().setColorFilter(colorFilter);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void addPoint(float x, float y) {

        // Movemos el punto para que la imagen se centre en donde se apoyó el dedo
        Point point = new Point(((int) (x - getPaint().getStrokeWidth() / 2)), (int) (y - getPaint().getStrokeWidth() / 2));
        points.add(point);
        // Log.e("Agregando punto ", "X: " + (int) (x - getPaint().getStrokeWidth() / 2) + ", Y: " + (int) (y - getPaint().getStrokeWidth() / 2));
    }

    public boolean hasNextPoint() { return index < points.size(); }

    public void resetIndex() { index = 0; }

    public Point getPoint() {

        return points.get(index);
    }

    public int getRotation() {

        return rotations[index];
    }

    public void next() {

        index++;
    }

    public int getIndex() { return index; }

    public void calculateRotations() {

        rotations = new int[points.size()];

        rotations[0] = 0;

        for (int i = 1; i < rotations.length; i++) {

            // rotations[i] = rotations[i - 1] - (rotations[i - 1] - calculateRotation(points.get(i - 1), points.get(i)));

            rotations[i] = calculateRotation(points.get(i - 1), points.get(i));

            Point thisPoint = points.get(i);
            Point previousPoint = points.get(i - 1);
            Point firstPoint = points.get(0);

            if (thisPoint.y > firstPoint.y && thisPoint.x < thisPoint.y) {

                if (thisPoint.x < previousPoint.x) {

                    rotations[i] = 180 - rotations[i];
                }
            }

            if (rotations[i] > 359) {
                rotations[i] =  rotations[i] - 360;
            } else if (rotations[i] < 0) {
                rotations[i] = 360 + rotations[i];
            }
        }

        // Arreglamos la primera y segunda rotación de acuerdo a la rotación del tercero
        if (rotations.length > 2) {
            rotations[0] = rotations[2];
            rotations[1] = rotations[2];
        }

    }

    private int calculateRotation(Point previousPoint, Point thisPoint) {
        double rotation;

        /*
        Log.e("Point A", "X: " + previousPoint.x + ", Y: " + previousPoint.y);
        Log.e("Point B", "X: " + thisPoint.x + ", Y: " + thisPoint.y);
        */

        int opossiteSide = Math.abs(previousPoint.y - thisPoint.y);
        int adjacentSide = Math.abs(previousPoint.x - thisPoint.x);

        rotation = Math.toDegrees(Math.atan2(opossiteSide, adjacentSide));

        return (int) rotation;
    }

    public ArrayList<Point> getPoints() { return points; }

    public ColorFilter getColorFilter() { return colorFilter; }
}
