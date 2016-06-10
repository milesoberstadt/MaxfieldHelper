package maxfieldhelper.milesoberstadt.com.maxfieldhelper;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    public static String debugTag = "MaxfieldHelper";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try{

            setContentView(R.layout.activity_main);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
        }
        catch (Exception e){
            Log.d(debugTag, e.getMessage());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        Button startButton = (Button)findViewById(R.id.btnStart);
        Button stopButton = (Button)findViewById(R.id.btnStop);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkDrawOverlayPermission())
                    startService(new Intent(MainActivity.this, FloatingKeyPlanService.class));
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(new Intent(MainActivity.this, FloatingKeyPlanService.class));
            }
        });

    }

    @Override
    protected void onResume(){
        Bundle bundle = getIntent().getExtras();

        if (bundle != null && bundle.getString("LAUNCH").equals("YES"))
            startService(new Intent(MainActivity.this, FloatingKeyPlanService.class));

        super.onResume();
    }

    /** code to post/handler request for permission */
    public final static int REQUEST_CODE = 111;

    public boolean checkDrawOverlayPermission() {
        try {
            /** check if we already  have permission to draw over other apps */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(getApplicationContext())) {
                    /** if not construct intent to request permission */
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName()));
                    /** request permission via start activity for result */
                    startActivityForResult(intent, REQUEST_CODE);
                    return false;
                }
                else
                    return true;
            }
            else
                return true;
        }
        catch (Exception e){
            Log.d(debugTag, e.getMessage());
        }
        return true;
    }

}
