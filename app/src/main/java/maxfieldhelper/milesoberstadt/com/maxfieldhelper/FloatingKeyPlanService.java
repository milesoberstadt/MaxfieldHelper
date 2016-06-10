package maxfieldhelper.milesoberstadt.com.maxfieldhelper;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by miles on 6/10/16.
 * Big thanks to EatHeat for a starting point...
 * https://github.com/EatHeat/FloatingExample
 */
public class FloatingKeyPlanService extends Service{
    private WindowManager windowManager;
    private LayoutInflater layoutInflater;
    private WindowManager.LayoutParams floatingLayoutParams;

    private ImageView minimizedView;
    private View maximizedView;

    private int lastPosX = 0;
    private int lastPosY = 100;
    private boolean bPositionLocked = true;

    public FloatingKeyPlanService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        layoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        minimizedView = new ImageView(this);
        minimizedView.setImageResource(R.drawable.link);

        final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
        params.gravity = Gravity.TOP | Gravity.LEFT;
        params.x = lastPosX;
        params.y = lastPosY;

        floatingLayoutParams = params;

        windowManager.addView(minimizedView, params);

        // If dragging is allowed, listen for touch events...
        if (!bPositionLocked){
            try {
                minimizedView.setOnTouchListener(new View.OnTouchListener() {
                    private WindowManager.LayoutParams paramsF = params;
                    private int initialX, initialY;
                    private float initialTouchX, initialTouchY;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch(event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                // Get current time in nano seconds
                                initialX = paramsF.x;
                                initialY = paramsF.y;
                                initialTouchX = event.getRawX();
                                initialTouchY = event.getRawY();
                                break;
                            case MotionEvent.ACTION_UP:
                                break;
                            case MotionEvent.ACTION_MOVE:
                                paramsF.x = initialX + (int) (event.getRawX() - initialTouchX);
                                paramsF.y = initialY + (int) (event.getRawY() - initialTouchY);
                                windowManager.updateViewLayout(minimizedView, paramsF);
                        }
                        return false;
                    }
                });
            } catch(Exception e){
                Log.d(MainActivity.debugTag, e.getMessage());
            }
        }
        else{
            try{
                minimizedView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        maximizeView();
                    }
                });
            } catch(Exception e){
                Log.d(MainActivity.debugTag, e.getMessage());
            }
        }


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (minimizedView != null)
            windowManager.removeView(minimizedView);
        if (maximizedView != null)
            windowManager.removeView(maximizedView);
    }

    private void maximizeView(){
        try{
            // Remove the icon, open the menu
            windowManager.removeView(minimizedView);
            minimizedView = null;

            Display display = windowManager.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            floatingLayoutParams.width = size.x;
            floatingLayoutParams.height = 100;

            // Build the ui...
            maximizedView = layoutInflater.inflate(R.layout.floating_key_counter, null);
            windowManager.addView(maximizedView, floatingLayoutParams);
        }
        catch(Exception e){
            Log.d(MainActivity.debugTag, e.getMessage());
        }
    }
}
