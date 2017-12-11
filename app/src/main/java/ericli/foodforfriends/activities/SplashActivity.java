package ericli.foodforfriends.activities;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import ericli.foodforfriends.MainActivity;
import ericli.foodforfriends.R;

/**
 * Created by ericli on 11/29/2017.
 */

/**
 * this is the activity that shows when the app first starts, the splash screen
 * */
public class SplashActivity extends AppCompatActivity {

    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window _window = getWindow();
        _window.setFormat(PixelFormat.RGBA_8888);
    }


    Thread _thread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        _StartAnimations_();

    }

    /**
     * this method is used to start animation
     * */
    private void _StartAnimations_() {
        Animation _anim = AnimationUtils.loadAnimation(this, R.anim.alpha_for_splash);
        _anim.reset();

        RelativeLayout _relLayout = (RelativeLayout) findViewById(R.id.lin_lay);
        _relLayout.clearAnimation();
        _relLayout.startAnimation(_anim);

        _anim = AnimationUtils.loadAnimation(this, R.anim.translate_for_splash);
        _anim.reset();

        ImageView _img = (ImageView) findViewById(R.id.splash);
        _img.clearAnimation();
        _img.startAnimation(_anim);

        _thread = new Thread() {
            @Override
            public void run() {
                try {
                    int _wait = 0;
                    // Splash screen pause time
                    while (_wait < 1500) {
                        sleep(70);
                        _wait += 70;
                    }
                    Intent _intent = new Intent(SplashActivity.this,
                            MainActivity.class);
                    _intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                      startActivity(_intent);
                     SplashActivity.this.finish();

                } catch (InterruptedException e) {
                   e.printStackTrace();
                } finally {
                    SplashActivity.this.finish();
                }
            }
        };
        _thread.start();

    }

}
