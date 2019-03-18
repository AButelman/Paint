package com.andres.paint;

public class DoubleTapDetector extends Thread {

    public static final int TIME_BETWEEN_FINGER_UPS = 175; //ms

    private OnDoubleTapListener listener;
    private int tapCounter;

    public DoubleTapDetector() {
        tapCounter = 1;
    }

    @Override
    public void run() {

        long startingTime = System.currentTimeMillis();
        long now = System.currentTimeMillis() - startingTime;

        while (now <= TIME_BETWEEN_FINGER_UPS) {

            if (tapCounter == 2) {
                listener.onDoubleTap();
                this.interrupt();
                break;
            }

            now = System.currentTimeMillis() - startingTime;
        }

        listener.onDetectorStopped();
    }

    public void tap() { tapCounter++; }

    public void setOnDoubleTapListener(OnDoubleTapListener listener) {
        this.listener = listener;
    }

    public interface OnDoubleTapListener {
        void onDoubleTap();
        void onDetectorStopped();
    }
}
