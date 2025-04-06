package com.xmorera.climbingtrainingapp.utils;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

/**
 * Classe auxiliar que fa que un element View faci pampallugues
 *
 */
public class BlinkHelper {

        private final Handler blinkHandler;
        private Runnable blinkRunnable;
        private boolean isBlinking = false;

        public BlinkHelper() {
            this.blinkHandler = new Handler(Looper.getMainLooper());
        }

        public void startBlinking(View blinkingTarget) {
            if (!isBlinking) {
                isBlinking = true;

                blinkRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (blinkingTarget.getVisibility() == View.VISIBLE) {
                            blinkingTarget.setVisibility(View.INVISIBLE);
                        } else {
                            blinkingTarget.setVisibility(View.VISIBLE);
                        }
                        // Reprograma el runnable cada 500ms
                        blinkHandler.postDelayed(this, 500);
                    }
                };

                // Inicia el pampallugueig
                blinkHandler.post(blinkRunnable);
            }
        }

        public void stopBlinking(View blinkingTarget) {
            if (isBlinking) {
                isBlinking = false;
                blinkHandler.removeCallbacks(blinkRunnable); // Atura el pampallugueig
                blinkingTarget.setVisibility(View.VISIBLE); // Assegura visibilitat
            }
        }

        // Neteja per evitar memory leaks
        public void cleanup() {
            blinkHandler.removeCallbacksAndMessages(null);
        }
}
