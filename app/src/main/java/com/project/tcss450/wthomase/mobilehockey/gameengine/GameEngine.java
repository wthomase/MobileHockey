package com.project.tcss450.wthomase.mobilehockey.gameengine;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.VelocityTracker;

import com.project.tcss450.wthomase.mobilehockey.EndGameDialog;
import com.project.tcss450.wthomase.mobilehockey.LoginMenuActivity;
import com.project.tcss450.wthomase.mobilehockey.R;

/**
 * Primary GameEngine class used to display graphics and run our game. Implements its own run and
 * update/render loops to process the data within the game. Utilizes two inner-classes, a Timer
 * class to "tick" the game forward and a GameView class (SurfaceView) to display and update the
 * game.
 *
 * The implementation of this engine was initially aided by some basic "skeleton" code from the
 * following URL:
 *
 * http://gamecodeschool.com/android/building-a-simple-game-engine/
 *
 */

public class GameEngine extends Activity {

    /**
     * Stores an instance of our GameView, used to display graphics and store our game logic.
     */
    GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize gameView and set it as the view
        gameView = new GameView(this);
        setContentView(gameView);

    }

    /**
     * Inner GameView class used to display graphics through SurfaceView and store game logic.
     */
    class GameView extends SurfaceView implements Runnable {

        /** Stores the games Thread */
        Thread gameThread = null;

        /** SurfaceHolder used for Paint and Canvas. */
        SurfaceHolder ourHolder;

        /** Volatile boolean value to store whether the game is active
         * or not (whether the update/render loop should run) */
        private volatile boolean playing;

        /** Canvas object used to paint graphics to the screen. */
        private Canvas canvas;
        /** Paint object used to help paint graphics to the screen. */
        private Paint paint;

        /** Stores this Canvas' width. */
        private int canvasWidth;
        /** Stores this Canvas' height. */
        private int canvasHeight;
        /** Stores our psuedo-friction value for physics calculations */
        private float canvasFriction = 0.9f;

        /** Long variable used to store the framerate (used for debugging) */
        private long fps;

        /** VelocityTracker used to store and access the touch input velocity of the player. */
        private VelocityTracker mVelocityTracker;

        /** Long variable used to store the duration of this frame, used in FPS calculations. */
        private long timeThisFrame;

        /** Game Timer used to help tick the game logic at a consistent rate independent of FPS. */
        private Timer gameTimer;
        /** Stores the current clockTick duration delay. */
        private double clockTick;

        /** Stores whether the AI should be enabled. */
        private boolean aiEnabled;
        /** Stores the AI mallet speed. */
        private float aiSpeed;

        // Scoreboard
        /** Stores the player1 (main player) score */
        private int player1Score;
        /** Stores the player2 (AI player or player2) score */
        private int player2Score;

        // Detects whether the mallet is moving or not
        /** Boolean flag to determine whether touch input is detected. */
        private boolean isMoving;

        // The player mallet data
        /** Stores player1's mallet X position. */
        private float mallet1_XPosition;
        /** Stores player1's mallet Y position. */
        private float mallet1_YPosition;
        /** Stores the player1's mallet radius. */
        private float mallet1_Radius;


        // The AI mallet data
        /** Stores player2's (AI) mallet X position. */
        private float mallet2_XPosition;
        /** Stores player2's (AI) mallet Y position. */
        private float mallet2_YPosition;
        /** Stores player2's (AI) radius */
        private float mallet2_Radius;

        // Puck data
        /** Stores the puck's X position. */
        private float puckXposition;
        /** Stores the puck's Y position. */
        private float puckYposition;
        /** Stores the puck radius. */
        private float puckRadius;
        /** Stores the puck's current X velocity. */
        private float puckXvelocity;
        /** Stores the puck's current Y velocity. */
        private float puckYvelocity;
        /** Stores the puck's maximum allowed velocity. */
        private float puckMaxVelocity;

        // Goal objects
        /** The rectangle that represents player1's goal. */
        private RectF myGoal;
        /** The rectangle that represents player2's (AI) goal. */
        private RectF theirGoal;

        // Time settings
        /** Stores the startTime when the game starts. */
        private long startTime;
        /** Stores the duration of the game in milliseconds. */
        private long gameDuration;

        /** Constructs a new GameView and initializes all of our initial game states. */
        public GameView(Context context) {
            // The next line of code asks the
            // SurfaceView class to set up our object.
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
            isMoving = false;

            aiEnabled = true;
            //aiSpeed = 250;
            aiSpeed = 500;

            gameTimer = new Timer();
            mVelocityTracker = VelocityTracker.obtain();

            player1Score = 0;
            player2Score = 0;

            // initialize positions and velocities
            // player 1
            mallet1_Radius = 100;
            mallet1_XPosition = (canvasWidth / 2);
            mallet1_YPosition = canvasHeight - 150 - mallet1_Radius;

            // player 2 (ai)
            mallet2_Radius = 100;
            mallet2_XPosition = (canvasWidth / 2);
            mallet2_YPosition = 100 + mallet2_Radius;

            puckXposition = (canvasWidth / 2);
            puckYposition = (canvasHeight / 2);
            puckXvelocity = 0;
            puckYvelocity = 500;
            puckMaxVelocity = 2500;
            puckRadius = 75;

            // goals
            myGoal = new RectF(canvasWidth/4, canvasHeight - 105, (canvasWidth/4)*3, canvasHeight);
            theirGoal = new RectF(canvasWidth/4, 0, (canvasWidth/4)*3, 20);

            startTime = System.currentTimeMillis();
            gameDuration = 60000;
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

        /**
         * The game's core update loop, updates all of the positions of the mallets, puck timer
         * and score. Detects collisions between the mallet and puck.
         */
        public void update() {

            // Update puck position
            // Note: We don't need to update the mallet1 position since it is handled by our
            // touch event handler when a touch event is registered.
            puckXposition += puckXvelocity * ((float) clockTick);
            puckYposition += puckYvelocity * ((float) clockTick);

            // Check puck collision with mallet1
            if (isPuckMalletColliding(mallet1_XPosition, mallet1_YPosition, mallet1_Radius)) {
                // Collision detected between player mallet and puck, give the puck the mallet velocity
                if (mVelocityTracker != null) {
                    mVelocityTracker.computeCurrentVelocity(1000, puckMaxVelocity);

                    float dist = distance(puckXposition, puckYposition, mallet1_XPosition, mallet1_YPosition);
                    float delta = puckRadius + mallet1_Radius - dist;
                    float difX = (puckXposition - mallet1_XPosition) / dist;
                    float difY = (puckYposition - mallet1_YPosition) / dist;

                    puckXposition += difX * delta / 2;
                    puckYposition += difY * delta / 2;

                    if (mVelocityTracker.getXVelocity() == 0 && mVelocityTracker.getYVelocity() == 0) {
                        puckXvelocity = -puckXvelocity * 0.4f;
                        puckYvelocity = -puckYvelocity * 0.4f;
                    } else {
                        puckXvelocity = mVelocityTracker.getXVelocity();
                        puckYvelocity = mVelocityTracker.getYVelocity();
                    }

                    float speed = (float) Math.sqrt(puckXvelocity * puckXvelocity + puckYvelocity * puckYvelocity);
                    if (speed > puckMaxVelocity) {
                        float ratio = puckMaxVelocity / speed;
                        puckXvelocity *= ratio;
                        puckYvelocity *= ratio;
                    }
                }
                puckXposition += puckXvelocity * ((float) clockTick);
                puckYposition += puckYvelocity * ((float) clockTick);
            }

//            // Check puck collision with mallet2
//            if (isPuckMalletColliding(mallet2_XPosition, mallet2_YPosition, mallet2_Radius)) {
//                // Collision detected between player mallet and puck, give the puck the mallet velocity
//                System.out.println("puck x velocity: " + puckXvelocity);
//                System.out.println("puck y velocity: " + puckYvelocity);
//                puckYvelocity = -puckYvelocity;
//                puckXvelocity = puckXposition - mallet2_XPosition;
//                puckXposition += puckXvelocity * ((float) clockTick);
//                puckYposition += puckYvelocity * ((float) clockTick);
//                System.out.println("puck x: " + puckXposition + puckXvelocity * ((float) clockTick));
//                System.out.println("puck y: " + puckYposition + puckYvelocity * ((float) clockTick));
//            }

            // Check puck collision with mallet2
            // check for x-axis collision]
            //puck left against mallet right
            float x1 = (puckXposition-puckRadius)-(mallet2_XPosition+mallet2_Radius);
            //puck right against mallet left
            float x2 = (mallet2_XPosition-mallet2_Radius)-(puckXposition+puckRadius);
            //puck top against mallet bottom
            float y1 = (puckYposition-puckRadius)-(mallet2_YPosition+mallet2_Radius);
            //puck bottom against mallet top
            float y2 = (mallet2_YPosition-mallet2_Radius)-(puckYposition+puckRadius);

            if(isPuckMalletColliding(mallet2_XPosition, mallet2_YPosition, mallet2_Radius)) {
                // mallet hits top of puck
                if ( (puckXposition - puckRadius) - (mallet2_XPosition + mallet2_Radius) <= 0 && (puckXposition - puckRadius) - (mallet2_XPosition + mallet2_Radius) > -mallet2_Radius) {
                    float dist = (puckXposition - puckRadius) - (mallet2_XPosition + mallet2_Radius);
                    puckXposition -= (-dist);
                    puckXvelocity = -puckXvelocity;
                }
                // mallet hits bottom of puck
                else if ((mallet2_XPosition - mallet2_Radius) - (puckXposition + puckRadius) <= 0 && (mallet2_XPosition - mallet2_Radius) - (puckXposition + puckRadius) > -mallet2_Radius) {
                    float dist = (mallet2_XPosition - mallet2_Radius) - (puckXposition + puckRadius);
                    puckXposition += (-dist);
                    puckXvelocity = -puckXvelocity;
                }
                // mallet hits left side of puck
                if ((puckYposition - puckRadius) - (mallet2_YPosition + mallet2_Radius) <= 0) {
                    float dist = (puckYposition - puckRadius) - (mallet2_YPosition + mallet2_Radius);
                    puckYposition += (-dist);
                    puckYvelocity = -puckYvelocity;
                }
                // mallet hits right side of puck
                else if ((mallet2_YPosition - mallet2_Radius) - (puckYposition + puckRadius) <= 0) {
                    float dist = (mallet2_YPosition - mallet2_Radius) - (puckYposition + puckRadius);
                    puckYposition -= (-dist);
                    //puckYvelocity = -puckYvelocity;
                }
            }

//            if (puckYposition > (mallet2_YPosition - mallet2_Radius) && puckYposition < (mallet2_YPosition + mallet2_Radius)) {
//                // coll from the right
//                if ((puckXposition - puckRadius) < (mallet2_XPosition + mallet2_Radius) && (puckXposition < (mallet2_XPosition + mallet2_Radius + 1))) {
//                    float dist = (mallet2_XPosition + mallet2_Radius) - (puckXposition - puckRadius);
//                    puckXposition += (dist + 1);
//                    puckXvelocity = -puckXvelocity;
//                // coll from the left
//                } else if ((puckXposition + puckRadius) > (mallet2_XPosition - mallet2_Radius) && (puckXposition > (mallet2_XPosition - mallet2_Radius - 1))) {
//                    float dist = (puckXposition + puckRadius) - (mallet2_XPosition - mallet2_Radius);
//                    puckXposition -= (dist - 1);
//                    puckXvelocity = -puckXvelocity;
//                }
//                //if (puckXvelocity == 0) {
//                //    puckXvelocity = 0.4f;
//                //}
//            }
//            // check for y-axis collision
//            else if (puckXposition > (mallet2_XPosition - mallet2_Radius) && puckXposition < (mallet2_XPosition + mallet2_Radius)) {
//                // coll from the top
//                if ((puckYposition + puckRadius) > (mallet2_YPosition - mallet2_Radius) && (puckYposition < (mallet2_YPosition + mallet1_Radius + 1))) {
//                    float dist = (puckYposition + puckRadius) - (mallet2_YPosition - mallet2_Radius);
//                    puckYposition -= (dist - 1);
//                    puckYvelocity = -puckYvelocity;
//                    // coll from the bottom
//                } else if ((puckYposition - puckRadius) < (mallet2_YPosition + mallet2_Radius) && (puckYposition > (mallet2_YPosition - mallet1_Radius - 1))) {
//                    float dist = (mallet2_YPosition + mallet2_Radius) - (puckYposition - puckRadius);
//                    puckYposition += (dist + 1);
//                    puckYvelocity = -puckYvelocity;
//                }
//                if (puckYvelocity == 0) {
//                    puckYvelocity = 0.4f;
//                }
//            }




            // Check collision of puck with canvas
            if (collideLeft(puckXposition, puckRadius)
                    || collideRight(puckXposition, puckRadius)) {
                puckXvelocity = -puckXvelocity;
                if (collideLeft(puckXposition, puckRadius)) puckXposition = puckRadius;
                if (collideRight(puckXposition, puckRadius)) puckXposition = canvasWidth - puckRadius;
                puckXposition += puckXvelocity * ((float) clockTick);
                puckYposition += puckYvelocity * ((float) clockTick);
            }

            if (collideTop(puckYposition, puckRadius)
                    || collideBottom(puckYposition, puckRadius)) {
                //if (collideTop(puckYposition, puckRadius)) player2Score++; // check if puck is in ai goal
                if (collideTop(puckYposition, puckRadius)) {
                    if (puckXposition > theirGoal.left && puckXposition < theirGoal.right) {
                        player1Score++;
                        puckXvelocity = 0;
                        puckYvelocity = 0;
                        puckXposition = canvasWidth / 2;
                        puckYposition = canvasHeight / 2;
                    } else {
                        puckYvelocity = -puckYvelocity;
                        puckYposition = puckRadius;
                    }
                }
                //if (collideBottom(puckYposition, puckRadius)) player1Score++; // check if puck is in player goal
                if (collideBottom(puckYposition, puckRadius)) {
                    if (puckXposition > myGoal.left && puckXposition < myGoal.right) {
                        player2Score++;
                        puckXvelocity = 0;
                        puckYvelocity = 0;
                        puckXposition = canvasWidth / 2;
                        puckYposition = canvasHeight / 2;
                    } else {
                        puckYvelocity = -puckYvelocity;
                        puckYposition = canvasHeight - 85 - puckRadius;
                    }
                }
            }

            // check AI
            // moves ai left and right depending on x coord of the puck
            // add in y coord movement
            if (aiEnabled) {
                if (puckXposition - puckRadius > mallet2_XPosition) {
                    mallet2_XPosition += aiSpeed * ((float) clockTick);
                }

                if (puckXposition + puckRadius < mallet2_XPosition) {
                    mallet2_XPosition -= aiSpeed *((float) clockTick);
                }

                // if the puck is on player 1's side
                if (puckYposition > canvasHeight/2) {
                    if (mallet2_YPosition > 100 + mallet2_Radius) {
                        mallet2_YPosition -= aiSpeed * ((float) clockTick);
                    }
                    // else if the puck is on the ai's side
                } else if (puckYposition <= canvasHeight/2) {
                    //if the puck ycoord > ai ycoord
                    if (mallet2_YPosition < puckYposition) {
                        mallet2_YPosition += aiSpeed * ((float) clockTick);
                        // else if the puck ycoord < ai ycoord
                    } else if (mallet2_YPosition > puckYposition) {
                        //if the mallet has not reached its lowest ycoord position
                        if (mallet2_YPosition >= (100 + mallet1_Radius)) {
                            mallet2_YPosition -= aiSpeed * ((float) clockTick);
                        }

                    }

                }
                // Check to see if AI is crossing the center line
                if((mallet2_YPosition + mallet2_Radius) >= canvasHeight/2) {
                    //System.out.println("mallet1_YPosition is > canvasHeight/2");
                    mallet2_YPosition = ((canvasHeight/2) - mallet2_Radius);
                }
            }

            puckXvelocity -= (1 - canvasFriction) * clockTick * puckXvelocity;
            puckYvelocity -= (1 - canvasFriction) * clockTick * puckYvelocity;
            mVelocityTracker.clear();

            if (System.currentTimeMillis() >= startTime + gameDuration) {
                // our current time has exceeded our timer

                // change the playing variable, pop up a dialog
                LoginMenuActivity.mostRecentHighScore = player1Score + "";
                playing = false;

                // Launch our DialogFragment to signal the end of the game
                DialogFragment newFragment = new EndGameDialog();
                newFragment.show(getFragmentManager(), "end_game_dialog");
            }
        }

        /**
         * The game's draw (or "render") method. Draws all of our graphical and UI elements to the
         * screen.
         */
        public void draw() {

            // Make sure our drawing surface is valid or we crash
            if (ourHolder.getSurface().isValid()) {
                // Lock the canvas ready to draw
                // Make the drawing surface our canvas object
                canvas = ourHolder.lockCanvas();

                // Draw the background color
                //canvas.drawColor(Color.argb(255,  26, 128, 182));
                canvas.drawColor(Color.parseColor("#FFFFFF"));

                // Choose the brush color for drawing
                paint.setColor(Color.argb(255,  249, 129, 0));

                // Make the text a bit bigger
                paint.setTextSize(45);

                // Display the current fps on the screen (DEBUG)
//                canvas.drawText("FPS:" + fps, 20, 40, paint);
//                mVelocityTracker.computeCurrentVelocity(1000, puckMaxVelocity);
//                canvas.drawText("X-vel: " + mVelocityTracker.getXVelocity(), 20, 80, paint);
//                canvas.drawText("Y-vel: " + mVelocityTracker.getYVelocity(), 20, 140, paint);
//                canvas.drawText("current X Pos: " + mallet1_XPosition, 20, 250, paint);
//                canvas.drawText("current Y Pos: " + mallet1_YPosition, 20, 280, paint);

                // Draw "hockey table" elements
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.parseColor("#000000"));
                paint.setStrokeWidth(8);
                int scaledSize = getResources().getDimensionPixelSize(R.dimen.scoreTextSize);
                paint.setTextSize(scaledSize);
                //canvas.drawText(player1Score + "", (canvasWidth * 0.75f), (canvasHeight / 2) * 0.9f, paint);
                //canvas.drawText(player2Score + "", (canvasWidth * 0.75f), (canvasHeight / 2) * 1.2f, paint);
                paint.setColor(Color.parseColor("#0000FF"));
                canvas.drawText(player1Score + "", (canvasWidth * 0.90f), (canvasHeight / 2) * 1.2f, paint);
                paint.setColor(Color.parseColor("#FF0000"));
                canvas.drawText(player2Score + "", (canvasWidth * 0.90f), (canvasHeight / 2) * 0.9f, paint);
                paint.setColor(Color.parseColor("#000000"));
                canvas.drawLine(0, canvasHeight / 2, canvasWidth, canvasHeight / 2, paint);
                canvas.drawCircle(canvasWidth / 2, canvasHeight / 2, 100, paint);

                paint.setStyle(Paint.Style.FILL);

                // Draw goals
                paint.setColor(Color.parseColor("#0000FF"));
                canvas.drawRect(myGoal, paint);
                paint.setColor(Color.parseColor("#FF0000"));
                canvas.drawRect(theirGoal, paint);

                // Draw player mallet (BLUE)
                //paint.setColor(Color.parseColor("#ADD8E6"));
                paint.setColor(Color.parseColor("#0000FF"));
                canvas.drawCircle(mallet1_XPosition, mallet1_YPosition, mallet1_Radius, paint);

                // Draw AI mallet (RED)
                paint.setColor(Color.parseColor("#FF0000"));
                canvas.drawCircle(mallet2_XPosition, mallet2_YPosition, mallet2_Radius, paint);

                // Draw puck (BLACK)
                paint.setColor(Color.parseColor("#000000"));
                canvas.drawCircle(puckXposition, puckYposition, puckRadius, paint);

                // Draw time remaining
                long timeLeft = ((startTime + gameDuration) - System.currentTimeMillis()) / 1000;
                canvas.drawText("" + timeLeft, (canvasWidth * 0.1f), (canvasHeight / 2) * 0.9f, paint);

                // Draw everything to the screen
                // and unlock the drawing surface
                ourHolder.unlockCanvasAndPost(canvas);
            }

        }

        // If SimpleGameEngine Activity is paused/stopped
        // shutdown our thread.

        /**
         * Helper method to determine game behavior when pausing.
         */
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

        /**
         * Helper method to determine game behavior when resuming.
         */
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
                    if (isTouchColliding(mallet1_Radius, mallet1_XPosition, mallet1_YPosition, x, y)) {
                        isMoving = true;
                    }

                    break;

                // Player is holding down on the screen
                case MotionEvent.ACTION_MOVE:
                    float newX = motionEvent.getX();
                    float newY = motionEvent.getY();

                    if (isMoving) {
                        // if we aren't colliding with the puck, check to see if we're colliding with
                        // the walls and change our position if appropriate
                        mVelocityTracker.addMovement(motionEvent);

                        if (!isPuckMalletColliding(mallet1_XPosition, mallet1_YPosition, mallet1_Radius)) {

                            boolean isCollidingLeftRight = collideLeft(mallet1_XPosition, mallet1_Radius) &&
                                    collideRight(newX, mallet1_Radius);
                            boolean isCollidingUpDown = (collideBottom(mallet1_YPosition, mallet1_Radius) &&
                                    collideTop(newY, mallet1_Radius));

                            if (collideLeft(mallet1_XPosition, mallet1_Radius)) {
                                mallet1_XPosition = mallet1_Radius;
                            }

                            if (collideRight(puckXposition, puckRadius)) {
                                mallet1_XPosition = canvasWidth - mallet1_Radius;
                            }

                            if (!isCollidingLeftRight) {
                                mallet1_XPosition = newX;
                            }

                            if (!isCollidingUpDown) {
                                mallet1_YPosition = newY;
                            }
                            // else we are colliding with the puck, allow movement if it makes us unstuck
                            // with the puck
                        } else {
                            boolean isCollidingLeftRight = collideLeft(newX, mallet1_Radius) &&
                                    collideRight(newX, mallet1_Radius);
                            boolean isCollidingUpDown = (collideBottom(newY, mallet1_Radius) &&
                                    collideTop(newY, mallet1_Radius));

                            if (!isPuckMalletColliding(newX, newY, mallet1_Radius)) {
                                if (!isCollidingLeftRight) {
                                    mallet1_XPosition = newX;
                                }

                                if (!isCollidingUpDown) {
                                    mallet1_YPosition = newY;
                                }
                            }
                        }
                        // Check if crossing center line (ADD THIS BACK IN AFTER AI GETS FIXED!)
                        // mallet1 is the player mallet
                        //System.out.println("mallet1_YPosition = " + mallet1_YPosition);
                        if((mallet1_YPosition - mallet1_Radius) <= canvasHeight/2) {
                            //System.out.println("mallet1_YPosition is > canvasHeight/2");
                            mallet1_YPosition = ((canvasHeight/2) + mallet1_Radius);
                        }
                    }



                    break;

                // Player has removed finger from screen
                case MotionEvent.ACTION_UP:

                    if (isMoving) {
                        isMoving = false;
                    }


                    break;

                case MotionEvent.ACTION_CANCEL:
                    // Return a VelocityTracker object back to be re-used by others.
                    mVelocityTracker.recycle();
                    break;

            }
            return true;
        }

        /**
         * Helper method to determine whether a mallet is colliding with the puck.
         * @param x1 is the x coordinate of the given mallet.
         * @param y1 is the y coordinate of the given mallet.
         * @param radius is the radius of the given mallet.
         * @return whether the mallet is colliding with the puck.
         */
        public boolean isPuckMalletColliding(float x1, float y1, float radius) {
            // Check collision of puck with mallet
            return circlesColliding(radius, puckRadius, x1, y1, puckXposition, puckYposition);

        }

        // Checks whether the given touch coordinates are colliding with our mallet on screen.

        /**
         * Given the touchX and touchY coordinates, returns whether the X and Y coordinates are
         * within the bounds of the given mallet (x, y, radius).
         * @param radius is the radius of the mallet.
         * @param x is the x-coordinate of the mallet.
         * @param y is the y-coordinate of the mallet.
         * @param touchX is the x-coordinate of the touch point.
         * @param touchY is the y-coordinate of the touch point.
         * @return whether the touch point collides with the given mallet info.
         */
        public boolean isTouchColliding(float radius, float x, float y, float touchX, float touchY) {
            return circlesColliding(radius, 1, x, y, touchX, touchY);
        }

        /**
         * Simply returns whether the two circles collide, given their coordinates and radii.
         * @param radius1 the radius of the first circle.
         * @param radius2 the radius of the second circle.
         * @param x1 the x-coordinate of the first circle.
         * @param y1 the y-coordinate of the first circle.
         * @param x2 the x-coordiante of the second circle.
         * @param y2 the y-coordinate of the second circle.
         * @return whether the two circles collide or not.
         */
        public boolean circlesColliding(float radius1, float radius2, float x1, float y1, float x2, float y2) {
            return distance(x1, y1, x2, y2) < radius1 + radius2;
        }

        /**
         * Determines whether the the given circle collides with the edge of our screen.
         * @param x is the x-coordinate of the circle.
         * @param radius is the radius of the circle.
         */
        public boolean collideLeft(float x, float radius) {
            return x - radius < 0;
        }

        /**
         *
         * @param x is the x-coordinate of the circle.
         * @param radius is the radius of the circle.
         * @return
         */
        public boolean collideRight(float x, float radius) {
            return x + radius > canvasWidth;
        }

        /**
         * Determines whether the the given circle collides with the edge of our screen.
         * @param y is the y-coordinate of the circle.
         * @param radius is the radius of the circle.
         */
        public boolean collideTop(float y, float radius) {
            return y - radius < 0;
        }

        /**
         * Determines whether the the given circle collides with the edge of our screen.
         * @param y is the y-coordinate of the circle.
         * @param radius is the radius of the circle.
         */
        public boolean collideBottom(float y, float radius) {
            return y + radius > canvasHeight;
        }
    }

    /**
     * Inner Timer class used to "tick" the game logic forward independent of frame rate.
     */
    class Timer {
        /** Used to store the current gameTime. */
        private double gameTime;
        /** Used to cap the simulation rate of the game. */
        private double maxStep;
        /** Used to store the last time stamp of the game. */
        private double wallLastTimestamp;

        /**
         * Initializes a new Timer.
         */
        public Timer() {
            gameTime = 0;
            maxStep = .05;
            wallLastTimestamp = 0;
        }

        /**
         * Ticks the game forward, taking timeDelta into account.
         * @return the new deltaTime.
         */
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

    /**
     * Private helper method, simply calculates distance between two points.
     * @param x1 is the x-coordinate of the first point.
     * @param y1 is the y-coordinate of the first point.
     * @param x2 is the x-coordinate of the second point.
     * @param y2 is the y-coordinate of the second point.
     * @return the distance between the two points.
     */
    private float distance(float x1, float y1, float x2, float y2) {
        float dx = x1 - x2;
        float dy = y1 - y2;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

}