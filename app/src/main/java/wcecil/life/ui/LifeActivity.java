package wcecil.life.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Calendar;

import wcecil.life.R;
import wcecil.life.game.GameState;
import wcecil.life.ui.util.SystemUiHider;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class LifeActivity extends Activity {
    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final boolean TOGGLE_ON_CLICK = false;
    private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;
    private SystemUiHider mSystemUiHider;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_life);

        GameState gameState = GameState.getInstance();
        final View controlsView = findViewById(R.id.fullscreen_content_controls);
        final View contentView = findViewById(R.id.fullscreenView);

        final ImageView imageView = (ImageView)contentView;
        gameState.setImageView(imageView);

        Display currentDisplay = getWindowManager().getDefaultDisplay();
        Bitmap bitmap;
        Canvas canvas;
        Paint paint;
        int dw;
        int dh;
        dw = currentDisplay.getWidth();
        dh = currentDisplay.getHeight();

        bitmap = Bitmap.createBitmap((int) dw, (int) dh,
                Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        imageView.setImageBitmap(bitmap);
        gameState.setCanvas(canvas);

        gameState.displayCurrentStateOfBoard();


        // Set up an instance of SystemUiHider to control the system UI for
        // this activity.
        mSystemUiHider = SystemUiHider.getInstance(this, contentView, HIDER_FLAGS);
        mSystemUiHider.setup();
        mSystemUiHider
                .setOnVisibilityChangeListener(new SystemUiHider.OnVisibilityChangeListener() {
                    // Cached values.
                    int mControlsHeight;
                    int mShortAnimTime;

                    @Override
                    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
                    public void onVisibilityChange(boolean visible) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                            // If the ViewPropertyAnimator API is available
                            // (Honeycomb MR2 and later), use it to animate the
                            // in-layout UI controls at the bottom of the
                            // screen.
                            if (mControlsHeight == 0) {
                                mControlsHeight = controlsView.getHeight();
                            }
                            if (mShortAnimTime == 0) {
                                mShortAnimTime = getResources().getInteger(
                                        android.R.integer.config_shortAnimTime);
                            }
                            controlsView.animate()
                                    .translationY(visible ? 0 : mControlsHeight)
                                    .setDuration(mShortAnimTime);
                        } else {
                            // If the ViewPropertyAnimator APIs aren't
                            // available, simply show or hide the in-layout UI
                            // controls.
                            controlsView.setVisibility(visible ? View.VISIBLE : View.GONE);
                        }

                        if (visible && AUTO_HIDE) {
                            // Schedule a hide().
                            delayedHide(AUTO_HIDE_DELAY_MILLIS);
                        }
                    }
                });

        contentView.setOnTouchListener(
                new View.OnTouchListener() {
                    private static final int MAX_CLICK_DURATION = 300;
                    private long startClickTime;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN: {
                                startClickTime = Calendar.getInstance().getTimeInMillis();
                            }
                            break;
                            case MotionEvent.ACTION_UP: {
                                long clickDuration = Calendar.getInstance().getTimeInMillis() - startClickTime;
                                if (clickDuration < MAX_CLICK_DURATION) {
                                    //click event has occurred
                                    handleClick(v, event);
                                } else {
                                    handleDrag(v, event);
                                }
                            }
                            break;
                            case MotionEvent.ACTION_MOVE: {
                                handleDrag(v, event);
                            }
                            break;
                        }
                        return true;
                    }
                });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.
        findViewById(R.id.runPauseButton).setOnTouchListener(mDelayHideTouchListener);
        findViewById(R.id.stepButton).setOnTouchListener(mDelayHideTouchListener);


        findViewById(R.id.runPauseButton).setOnClickListener(runPauseListener);
        findViewById(R.id.stepButton).setOnClickListener(stepListener);
    }

    private void handleDrag(View v, MotionEvent event) {
        System.out.println("Drag : " + event);

        GameState.getInstance().running=false;

        GameState.getInstance().handleClick(event.getX(), event.getY(),false);

        if (TOGGLE_ON_CLICK) {
            mSystemUiHider.toggle();
        } else {
            mSystemUiHider.show();
        }
    }

    private void handleClick(View view, MotionEvent event) {
        System.out.println("Click : " + event);

        GameState.getInstance().running=false;

        GameState.getInstance().handleClick(event.getX(), event.getY(),true);

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */
    View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    View.OnClickListener stepListener = new View.OnClickListener(){

        @Override
        public void onClick(View v) {
            GameState.getInstance().nextState();
            GameState.getInstance().displayCurrentStateOfBoard();
        }
    };

    View.OnClickListener runPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            GameState.getInstance().running = !GameState.getInstance().running;

            updateRunningButton();
        }
    };

    private void updateRunningButton() {
        Button b = (Button) findViewById(R.id.runPauseButton);
        if (!GameState.getInstance().running) {
            b.setText(R.string.run);
        } else {
            b.setText(R.string.pause);
        }
    }

    Handler mHideHandler = new Handler();
    Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            mSystemUiHider.hide();
        }
    };

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }
}
