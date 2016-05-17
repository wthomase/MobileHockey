package com.project.tcss450.wthomase.mobilehockey.gameengine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;

import com.project.tcss450.wthomase.mobilehockey.R;

public class GameEngine extends Activity {

    // gameView will be the view of the game
    // It will also hold the logic of the game
    // and respond to screen touches as well
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize gameView and set it as the view
        gameView = new GameView(this);
        setContentView(gameView);

    }

    // Here is our implementation of GameView
    // It is an inner class.
    // Note how the final closing curly brace }
    // is inside SimpleGameEngine

    // Notice we implement runnable so we have
    // A thread and can override the run method.
    class GameView extends SurfaceView implements Runnable {

        // This is our thread
        Thread gameThread = null;

        // This is new. We need a SurfaceHolder
        // When we use Paint and Canvas in a thread
        // We will see it in action in the draw method soon.
        SurfaceHolder ourHolder;

        // A boolean which we will set and unset
        // when the game is running- or not.
        volatile boolean playing;

        // A Canvas and a Paint object
        Canvas canvas;
        Paint paint;

        int canvasWidth;
        int canvasHeight;

        // This variable tracks the game frame rate
        long fps;

        // Used to track the velocity of touch based events
        private VelocityTracker mVelocityTracker;

        // This is used to help calculate the fps
        private long timeThisFrame;

        private Timer gameTimer;
        private double clockTick;

        private boolean aiEnabled;
        private float aiSpeed;

        // Declare an object of type Bitmap
        Bitmap bitmapMallet1;
        Bitmap bitmapMallet2;
        Bitmap bitmapPuck;

        // Detects whether the mallet is moving or not
        boolean isMoving = false;

        // He starts 10 pixels from the left
        float mallet1_XPosition;
        float mallet1_YPosition;

        float mallet2_XPosition;
        float mallet2_YPosition;


        // Puck data
        float puckXposition;
        float puckYposition;
        float puckXvelocity = 0;
        float puckYvelocity = -500;
        float puckMaxVelocity = 1000;

        // When the we initialize (call new()) on gameView
        // This special constructor method runs
        public GameView(Context context) {
            // The next line of code asks the
            // SurfaceView class to set up our object.
            // How kind.
            super(context);

            // Initialize ourHolder and paint objects
            ourHolder = getHolder();
            paint = new Paint();

            //Get the width and height of the screen
            DisplayMetrics d = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(d);
            canvasWidth  = d.widthPixels;
            Log.d("debug", "Canvas width: " + canvasWidth);
            canvasHeight = d.heightPixels;
            Log.d("debug", "Canvas height: " + canvasHeight);

            aiEnabled = true;
            aiSpeed = 250;

            gameTimer = new Timer();

            // Load mallets from the .png files
            bitmapMallet1 = BitmapFactory.decodeResource(this.getResources(), R.drawable.mallet_test);
            bitmapMallet2 = BitmapFactory.decodeResource(this.getResources(), R.drawable.mallet_test);
            bitmapPuck = BitmapFactory.decodeResource(this.getResources(), R.drawable.puck);

            // initialize positions and velocities
            mallet1_XPosition = (canvasWidth / 2) - (bitmapPuck.getWidth() / 2);
            mallet1_YPosition = 100;

            // initialize positions and velocities
            mallet2_XPosition = (canvasWidth / 2) - (bitmapMallet1.getWidth() / 2);
            mallet2_YPosition = canvasHeight - 100 - (bitmapMallet2.getHeight());

            puckXposition = (canvasWidth / 2) - (bitmapPuck.getHeight() / 2);
            puckYposition = (canvasHeight / 2) - (bitmapPuck.getWidth() / 2);
        }

        @Override
        public void run() {
            while (playing) {

                // Capture the current time in milliseconds in startFrameTime
                long startFrameTime = System.currentTimeMillis();

                clockTick = gameTimer.tick();

                // Update the frame
                update();

                // Draw the frame
                draw();

                // Calculate the fps this frame
                // We can then use the result to
                // time animations and more.
                timeThisFrame = System.currentTimeMillis() - startFrameTime;
                if (timeThisFrame > 0) {
                    fps = 1000 / timeThisFrame;
                }

            }

        }

        // Everything that needs to be updated goes in here
        // In later projects we will have dozens (arrays) of objects.
        // We will also do other things like collision detection.
        public void update() {

            // Update puck position
            // Note: We don't need to update the mallet1 position since it is handled by our
            // touch event handler when a touch event is registered.
            puckXposition += puckXvelocity * ((float) clockTick);
            puckYposition += puckYvelocity * ((float) clockTick);

            // Check puck collision with mallet1
            if (isPuckMalletColliding(bitmapMallet1, mallet1_XPosition, mallet1_YPosition)) {
                // Collision detected between player mallet and puck, give the puck the mallet velocity
                puckYvelocity = -puckYvelocity;
                puckXvelocity = puckXposition - mallet1_XPosition;
//                if (mVelocityTracker != null) {
//                    mVelocityTracker.computeCurrentVelocity(1000, puckMaxVelocity);
//
//                    // if our x-velocities are traveling in the same direction, reverse it
//                    // so we don't collide with the mallet
//                    float diffX = puckXposition - mallet1_XPosition;
//                    //diffX += mVelocityTracker.getXVelocity();
//                    puckXvelocity = diffX;
//
//                    float speed = (float) Math.sqrt(puckXvelocity * puckXvelocity + puckYvelocity * puckYvelocity);
//                    if (speed > puckMaxVelocity) {
//                        float ratio = puckMaxVelocity / speed;
//                        puckXvelocity *= ratio;
//                        puckYvelocity *= ratio;
//                    }
//                }
                puckXposition += puckXvelocity * ((float) clockTick);
                puckYposition += puckYvelocity * ((float) clockTick);
            }

            // Check puck collision with mallet1
            if (isPuckMalletColliding(bitmapMallet2, mallet2_XPosition, mallet2_YPosition)) {
                // Collision detected between player mallet and puck, give the puck the mallet velocity
                puckYvelocity = -puckYvelocity;
                puckXvelocity = puckXposition - mallet2_XPosition;
                puckXposition += puckXvelocity * ((float) clockTick);
                puckYposition += puckYvelocity * ((float) clockTick);
            }

            // Check collision of puck with canvas
            if (collideLeft(bitmapPuck, puckXposition, puckYposition)
                || collideRight(bitmapPuck, puckXposition, puckYposition)) {
                puckXvelocity = -puckXvelocity;
                if (collideLeft(bitmapPuck, puckXposition, puckYposition)) puckXposition = 0;
                if (collideRight(bitmapPuck, puckXposition, puckYposition)) puckXposition = canvasWidth - bitmapPuck.getWidth();
                puckXposition += puckXvelocity * ((float) clockTick);
                puckYposition += puckYvelocity * ((float) clockTick);
            }

            if (collideTop(bitmapPuck, puckXposition, puckYposition)
                    || collideBottom(bitmapPuck, puckXposition, puckYposition)) {
                puckYvelocity = -puckYvelocity;
                //puckXposition = (canvasWidth / 2) - (bitmapPuck.getWidth() / 2);
                //puckYposition = (canvasHeight / 2) - (bitmapPuck.getHeight() / 2);
                if (collideTop(bitmapPuck, puckXposition, puckYposition)) puckYposition = 0;
                if (collideBottom(bitmapPuck, puckXposition, puckYposition)) puckYposition = canvasHeight - bitmapPuck.getHeight();
                puckXposition += puckXvelocity * ((float) clockTick);
                puckYposition += puckYvelocity * ((float) clockTick);
            }

            // check AI
            if (aiEnabled) {
                if (puckXposition - 50 > mallet2_XPosition) {
                    mallet2_XPosition += aiSpeed * ((float) clockTick);
                }

                if (puckXposition + 50 < mallet2_XPosition) {
                    mallet2_XPosition -= aiSpeed *((float) clockTick);
                }
            }
        }

        // Draw the newly updated scene
        public void draw() {

            // Make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                // Make the drawing surface our canvas object
                canvas = ourHolder.lockCanvas();

                // Draw the background color
                canvas.drawColor(Color.argb(255,  26, 128, 182));

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255,  249, 129, 0));

                // Make the text a bit bigger
                paint.setTextSize(45);

                // Display the current fps on the screen
                canvas.drawText("FPS:" + fps, 20, 40, paint);

                // Draw bitmaps
                canvas.drawBitmap(bitmapMallet1, mallet1_XPosition, mallet1_YPosition, paint);
                canvas.drawBitmap(bitmapPuck, puckXposition, puckYposition, paint);
                canvas.drawBitmap(bitmapMallet2, mallet2_XPosition, mallet2_YPosition, paint);

                // Draw everything to the screen
                // and unlock the drawing surface
                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        // If SimpleGameEngine Activity is paused/stopped
        // shutdown our thread.
        public void pause() {
            playing = false;
            try {
                gameThread.join();
            } catch (InterruptedException e) {
                Log.e("Error:", "joining thread");
            }

        }

        // If SimpleGameEngine Activity is started then
        // start our thread.
        public void resume() {
            playing = true;
            gameThread = new Thread(this);
            gameThread.start();
        }

        // The SurfaceView class implements onTouchListener
        // So we can override this method and detect screen touches.
        @Override
        public boolean onTouchEvent(MotionEvent motionEvent) {

            switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {

                // Player has touched the screen
                case MotionEvent.ACTION_DOWN:
                    if (mVelocityTracker == null) {
                        mVelocityTracker = VelocityTracker.obtain();
                    } else {
                        mVelocityTracker.clear();
                    }
                    mVelocityTracker.addMovement(motionEvent);
                    float x = motionEvent.getX();
                    float y = motionEvent.getY();

                    // Detects whether the touch point is colliding with the mallet
                    if (isTouchColliding(bitmapMallet1, mallet1_XPosition, mallet1_YPosition, x, y)) {
                        isMoving = true;
                    }

                    break;

                // Player is holding down on the screen
                case MotionEvent.ACTION_MOVE:
                    float curX = motionEvent.getX();
                    float curY = motionEvent.getY();

                    if (isMoving && isTouchColliding(bitmapMallet1, mallet1_XPosition, mallet1_YPosition, curX, curY)) {
                        if (!isPuckMalletColliding(bitmapMallet1, mallet1_XPosition, mallet1_YPosition)) {
                            mVelocityTracker.addMovement(motionEvent);
                            float newX = motionEvent.getX() - (bitmapMallet1.getWidth() / 2);
                            float newY = motionEvent.getY() - (bitmapMallet1.getHeight() / 2);

                            if (!collideLeft(bitmapMallet1, newX, newY) &&
                                    !collideRight(bitmapMallet1, newX, newY)) {
                                mallet1_XPosition = newX;
                            }

                            if (!collideBottom(bitmapMallet1, newX, newY) &&
                                    !collideTop(bitmapMallet1, newX, newY)) {
                                //mallet1_YPosition = newY;
                            }
                        }
                    }

                    break;

                // Player has removed finger from screen
                case MotionEvent.ACTION_UP:

                    // Set isMoving so Bob does not move
                    if (isMoving) {
                        isMoving = false;
                    }


                    break;
            }
            return true;
        }

        public boolean isPuckMalletColliding(Bitmap mallet, float xcoord, float ycoord) {
            // Check collision of puck with mallet
            if (puckXposition < xcoord + mallet.getWidth() &&
                    puckXposition + bitmapPuck.getWidth() > xcoord &&
                    puckYposition < ycoord + mallet.getHeight() &&
                    puckYposition + bitmapPuck.getHeight() > ycoord) {
                return true;
            }
            return false;

        }

        // Checks whether the given touch coordinates are colliding with our image on screen.
        public boolean isTouchColliding(Bitmap image, float x, float y, float touchX, float touchY) {
            if (x < touchX && x + image.getWidth() > touchX) {
                if (y < touchY && y + image.getHeight() > touchY) {
                    return true;
                }
            }
            return false;
        }

        public boolean collideLeft(Bitmap image, float x, float y) {
            return x < 0;
        }

        public boolean collideRight(Bitmap image, float x, float y) {
            return x + image.getWidth() > canvasWidth;
        }

        public boolean collideTop(Bitmap image, float x, float y) {
            return y < 0;
        }

        public boolean collideBottom(Bitmap image, float x, float y) {
            return y + image.getHeight() > canvasHeight;
        }

    }

    class Timer {
        private double gameTime;
        private double maxStep;
        private double wallLastTimestamp;

        public Timer() {
            gameTime = 0;
            maxStep = .05;
            wallLastTimestamp = 0;
        }

        public double tick() {
            double wallCurrent = System.currentTimeMillis();
            double wallDelta = (wallCurrent - wallLastTimestamp) / 1000;
            wallLastTimestamp = wallCurrent;

            double gameDelta = Math.min(wallDelta, maxStep);
            gameTime += gameDelta;
            return gameDelta;
        }


    }

    // This method executes when the player starts the game
    @Override
    protected void onResume() {
        super.onResume();

        // Tell the gameView resume method to execute
        gameView.resume();
    }

    // This method executes when the player quits the game
    @Override
    protected void onPause() {
        super.onPause();

        // Tell the gameView pause method to execute
        gameView.pause();
    }

}