package com.example.sample;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private ImageView man;
    private AnimationDrawable walkAnimation;
    private AnimationDrawable idleAnimation;
    private static final long WALK_DURATION = 3000; // 3 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        startWalkCycleAnimation();
    }

    private void initializeViews() {
        man = findViewById(R.id.man);
        
        // Set up walking animation drawable
        man.setImageResource(R.drawable.man_walk_animation);
        walkAnimation = (AnimationDrawable) man.getDrawable();
        
        // Load idle animation for later use
        idleAnimation = (AnimationDrawable) getResources().getDrawable(R.drawable.man_idle_animation, null);
    }                   

    private void startWalkCycleAnimation() {
        // Start the walk-cycle sprite animation after view is laid out
        man.post(new Runnable() {
            @Override
            public void run() {
                // Calculate screen width for dynamic point B (inside post to ensure view is measured)
                DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                float screenWidth = displayMetrics.widthPixels;
                
                float pointA = 0f;
                float pointB = screenWidth - man.getWidth(); // Leave some margin

                // Start the walk-cycle sprite animation
                walkAnimation.start();

                // Modern approach using ViewPropertyAnimator
                man.animate()
                    .translationX(pointB)
                    .setDuration(WALK_DURATION)
                    .setInterpolator(new LinearInterpolator())
                    .withEndAction(new Runnable() {
                        @Override
                        public void run() {
                            // Stop walking animation
                            if (walkAnimation != null && walkAnimation.isRunning()) {
                                walkAnimation.stop();
                            }
                            
                            // Switch to idle animation using resource
                            man.setImageResource(R.drawable.man_idle_animation);
                            idleAnimation = (AnimationDrawable) man.getDrawable();
                            
                            // Start idle animation immediately
                            if (idleAnimation != null) {
                                idleAnimation.start();
                            }
                        }
                    })
                    .start();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Clean up animations when activity pauses
        if (walkAnimation != null && walkAnimation.isRunning()) {
            walkAnimation.stop();
        }
        if (idleAnimation != null && idleAnimation.isRunning()) {
            idleAnimation.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restart animation if needed
        if (walkAnimation != null && man.getTranslationX() == 0) {
            startWalkCycleAnimation();
        }
    }
}