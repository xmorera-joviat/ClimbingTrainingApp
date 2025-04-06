package com.xmorera.climbingtrainingapp.utils;

import android.os.Handler;
import android.os.Looper;
import android.view.View;

/**
 * Classe auxiliar que fa que un element View faci pampallugues.
 * Aquesta classe utilitza un Handler per alternar la visibilitat d'un View
 * entre VISIBLE i INVISIBLE a intervals especificats.
 */
public class BlinkHelper {

    private final Handler blinkHandler;
    private Runnable blinkRunnable;
    private boolean isBlinking = false;

    /**
     * Constructor de la classe BlinkHelper.
     * Inicialitza el Handler associat al fil principal de l'aplicació.
     */
    public BlinkHelper() {
        this.blinkHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Inicia el pampallugueig d'un View especificat.
     *
     * @param blinkingTarget El View que es vol fer pampallugues.
     */
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

    /**
     * Atura el pampallugueig d'un View especificat.
     *
     * @param blinkingTarget El View del qual es vol aturar el pampallugueig.
     */
    public void stopBlinking(View blinkingTarget) {
        if (isBlinking) {
            isBlinking = false;
            blinkHandler.removeCallbacks(blinkRunnable); // Atura el pampallugueig
            blinkingTarget.setVisibility(View.VISIBLE); // Assegura visibilitat
        }
    }

    /**
     * Neteja el Handler per evitar memory leaks.
     * Aquesta funció elimina tots els callbacks i missatges pendents.
     */
    public void cleanup() {
        blinkHandler.removeCallbacksAndMessages(null);
    }
}
