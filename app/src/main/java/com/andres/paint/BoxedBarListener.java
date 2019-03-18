package com.andres.paint;

import android.util.Log;
import android.view.ViewGroup;
import android.widget.ImageView;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;

public class BoxedBarListener implements BoxedVertical.OnValuesChangeListener {

    @Override
    public void onPointsChanged(BoxedVertical boxedVertical, int i) {

        PaintView paintView = MainActivity.paintView;
        CircleDrawable strokeCircle = MainActivity.strokeCircle;
        ImageView selectedBrush = MainActivity.selectedBrush;

        switch (boxedVertical.getId()) {
            case (R.id.stroke_width_seek_bar):
                // Log.e("Stroke width", String.valueOf(boxedVertical.getValue()));
                int strokeWidth = boxedVertical.getValue() * MainActivity.STROKE_WIDTH_MAX / 100;
                strokeCircle.setSide(strokeWidth);

                ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) selectedBrush.getLayoutParams();

                params.width = strokeWidth;
                params.height = params.width;
                selectedBrush.setLayoutParams(params);
                selectedBrush.setX(paintView.getWidth() / 2 - selectedBrush.getWidth() / 2);
                selectedBrush.setY(paintView.getHeight() / 2 - selectedBrush.getHeight() / 2);

                paintView.setCurrentStrokeWidth(strokeWidth);
                break;

            case (R.id.opacity_seek_bar):
                // Log.e("Stroke opacity", String.valueOf(boxedVertical.getValue()));
                int opacity = boxedVertical.getValue() * MainActivity.OPACITY_MAX / 100;
                strokeCircle.setOpacity(opacity);
                paintView.setCurrentStrokeOpacity(opacity);
                MainActivity.colorPickerButton.setBackgroundColor(paintView.getCurrentStrokeColorWithOpacity());
                break;
        }
    }

    @Override
    public void onStartTrackingTouch(BoxedVertical boxedVertical) {

    }

    @Override
    public void onStopTrackingTouch(BoxedVertical boxedVertical) {

    }
}
