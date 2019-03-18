package com.andres.paint;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import abak.tr.com.boxedverticalseekbar.BoxedVertical;
import eltos.simpledialogfragment.SimpleDialog;
import eltos.simpledialogfragment.color.*;

import static eltos.simpledialogfragment.color.SimpleColorDialog.COLOR;
import static eltos.simpledialogfragment.color.SimpleColorDialog.COLORS;


public class MainActivity extends AppCompatActivity implements DrawerLayout.DrawerListener,
                                                                View.OnClickListener,
                                                                SimpleDialog.OnDialogResultListener {

    public static final String INSTANCE_KEY = "PAINT_VIEW";
    public static final String COLOR_PICKER = "COLOR PICKER TAG";

    public static final int STROKE_WIDTH_MAX = 400;
    public static final int OPACITY_MAX = 255;

    public static PaintView paintView;

    private RelativeLayout contentLayout;

    private DrawerLayout drawerLayout;
    private NavigationView leftNavigationView, rightNavigationView;
    public static Button colorPickerButton;
    public static CircleDrawable strokeCircle;
    public static ImageView selectedBrush;

    private BoxedVertical strokeWidthSeekBar, strokeOpacitySeekBar;
    private BoxedBarListener boxedBarListener;

    private ImageView[] strokeConfigButtons;

    public static DisplayMetrics getScreenSize(Context context) {

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        return metrics;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                    WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        paintView = (PaintView) findViewById(R.id.paint_view);

        setupNavigationViews();
    }

    private void setupNavigationViews() {

        contentLayout = (RelativeLayout) findViewById(R.id.content_layout);

        // Círculo para mostrar tamaño y color de brocha
        strokeCircle = new CircleDrawable(this);
        strokeCircle.setVisibility(View.INVISIBLE);
        contentLayout.addView(strokeCircle);

        selectedBrush = new ImageView(this);
        selectedBrush.setVisibility(View.INVISIBLE);
        contentLayout.addView(selectedBrush);

        // Que no se haga sombreado el paintview al mostrar el navigation drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setScrimColor(Color.TRANSPARENT);

        drawerLayout.setDrawerListener(this);

        setupRightNavigationView();
        setupLeftNavigationView();
    }

    private void setupLeftNavigationView() {

        leftNavigationView = (NavigationView) findViewById(R.id.stroke_type_view);
        final View headerLayout = leftNavigationView.getHeaderView(0);

        strokeConfigButtons = new ImageView[1]; // Cantidad de botones que hay

        strokeConfigButtons[0] = (ImageView) headerLayout.findViewById(R.id.button_bitmap_1);
        strokeConfigButtons[0].setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.dry_brush_stroke_2);
                paintView.setCurrentBitmap(bitmap);

                selectedBrush.setImageBitmap(bitmap);
                ViewGroup.LayoutParams params = (ViewGroup.LayoutParams) selectedBrush.getLayoutParams();
                params.width = paintView.getCurrentStrokeWidth();
                params.height = params.width;
                selectedBrush.setLayoutParams(params);
                selectedBrush.setX(paintView.getWidth() / 2 - selectedBrush.getWidth() / 2);
                selectedBrush.setY(paintView.getHeight() / 2 - selectedBrush.getHeight() / 2);

                drawerLayout.closeDrawer(leftNavigationView);
            }
        });

        changeLeftNavigationBarImageButtonsColors();
    }


    private void setupRightNavigationView() {

        rightNavigationView = (NavigationView) findViewById(R.id.stroke_config_view);
        View headerLayout = rightNavigationView.getHeaderView(0);

        boxedBarListener = new BoxedBarListener();

        strokeWidthSeekBar = (BoxedVertical) headerLayout.findViewById(R.id.stroke_width_seek_bar);
        strokeWidthSeekBar.setValue(paintView.getCurrentStrokeWidth() * 100 / STROKE_WIDTH_MAX);
        strokeWidthSeekBar.setOnBoxedPointsChangeListener(boxedBarListener);

        strokeOpacitySeekBar = (BoxedVertical) headerLayout.findViewById(R.id.opacity_seek_bar);
        strokeOpacitySeekBar.setValue(paintView.getCurrentStrokeOpacity() * 100 / OPACITY_MAX);
        strokeOpacitySeekBar.setOnBoxedPointsChangeListener(boxedBarListener);

        colorPickerButton = (Button) headerLayout.findViewById(R.id.button_color_picker);
        colorPickerButton.setBackgroundColor(paintView.getCurrentStrokeColorWithOpacity());
        colorPickerButton.setOnClickListener(this);
    }

    @Override
    public void onDrawerOpened(@NonNull View view) {

        if (view.getId() == R.id.stroke_config_view) {

            if (paintView.getCurrentBitmap() == null) {

                strokeCircle.reCenterCircle();  // Vuelve a poner el círculo en el centro cada vez que se abre el drawer
                strokeCircle.setVisibility(View.VISIBLE);
            } else {

                selectedBrush.setVisibility(View.VISIBLE);
            }

        }

        paintView.removeLastStroke();   // Borramos lo último que se dibujó porque no era necesario
    }

    @Override
    public void onDrawerClosed(@NonNull View view) {

        if (view.getId() == R.id.stroke_config_view) {

            strokeCircle.setVisibility(View.INVISIBLE);
            selectedBrush.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onDrawerSlide(@NonNull View view, float v) {
        // VER DE HACER QUE VAYA APARECIENDO Y DESAPARECIENDO DE A POCO
    }

    @Override
    public void onDrawerStateChanged(int i) {}

    // PREVIENE QUE SE CIERRE EL NAVIGATION DRAWER AL TOCAR FUERA DE ESTE
    // (PARA PODER SCROLLEAR EL CÍRCULO DEL STROKE
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {

        boolean isOutSideClicked = false;

        if (drawerLayout.isDrawerOpen(rightNavigationView)) { //Your code here to check whether drawer is open or not.

            int[] contentLocation = new int[2];
            rightNavigationView.getLocationOnScreen(contentLocation);
            Rect navigationDrawerRect = new Rect(contentLocation[0],
                    contentLocation[1],
                    contentLocation[0] + rightNavigationView.getWidth(),
                    contentLocation[1] + rightNavigationView.getHeight());

            if (!(navigationDrawerRect.contains((int) event.getX(), (int) event.getY()))) {
                isOutSideClicked = true;
            } else {
                isOutSideClicked = false;
            }

            Rect circleBounds = strokeCircle.getBounds();

            if (circleBounds.contains((int) event.getX(), (int) event.getY())) {

                strokeCircle.onTouchEvent(event);
                return true;
            }

        } else {
            return super.dispatchTouchEvent(event);
        }

        if (isOutSideClicked) {
            return true; //restrict the touch event here
        }else{
            return super.dispatchTouchEvent(event);
        }
    }

    @Override
    public void onClick(View v) {
        // paintView.setCurrentBitmap(null); // Para sacar de pintar la imagen si la había
        int color = paintView.getCurrentStrokeColor();
        SimpleColorWheelDialog.build()
                .color(paintView.getCurrentStrokeColor())
                .alpha(false)
                .show(this, COLOR_PICKER);
    }

    @Override
    public boolean onResult(@NonNull String dialogTag, int which, @NonNull Bundle extras) {

        if (COLOR_PICKER.equals(dialogTag)){
            switch(which){
                case BUTTON_POSITIVE:
                    int color = extras.getInt(SimpleColorWheelDialog.COLOR);

                    // PARA VOLVER AL STROKE NORMAL
                    paintView.setCurrentBitmap(null);

                    paintView.setCurrentStrokeColor(color);
                    strokeCircle.setColor(paintView.getCurrentStrokeColorWithOpacity());
                    selectedBrush.setColorFilter(paintView.getCurrentStrokeColorWithOpacity());
                    colorPickerButton.setBackgroundColor(paintView.getCurrentStrokeColorWithOpacity());
                    changeLeftNavigationBarImageButtonsColors();
                    break;

                default:
                    return false;
            }
            return true;
        }

        return false;
    }

    private void changeLeftNavigationBarImageButtonsColors() {

        for (ImageView imageButton : strokeConfigButtons) {

            imageButton.setColorFilter(paintView.getCurrentStrokeColor());
        }

    }
}
