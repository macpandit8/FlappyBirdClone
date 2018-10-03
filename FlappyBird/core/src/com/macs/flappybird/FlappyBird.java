package com.macs.flappybird;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;

import java.util.Random;

public class FlappyBird extends ApplicationAdapter {
	SpriteBatch batch;
	Texture background;
	Texture gameOver;
	Circle birdCircle;
	//ShapeRenderer shapeRenderer;

	Texture[] birds;
	int flapstat = 0;
	float birdY;
	int velocity;
	int gravity = 2;
	int gameState = 0;

	Texture topTube;
	Texture bottomTube;
	float gap = 400;
	float maxOffSet;
	Random randomGenerator;
	int numberOfTubes = 4;
	float distanceBetweenTubes;
	float tubeVelocity = 4;
	float[] tubeX = new float[numberOfTubes];
    float[] tubeOffSet = new float[numberOfTubes];
    Rectangle[] topTubeRectangles;
    Rectangle[] bottomTubeRectangles;
    int score = 0;
    int scoringTube = 0;
    BitmapFont font;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("bg.png");
		gameOver = new Texture("gameover.png");
		birds = new Texture[2];
		birds[0] = new Texture("bird.png");
        birds[1] = new Texture("bird2.png");
        birdCircle = new Circle();
        font = new BitmapFont();
        font.setColor(Color.YELLOW);
        font.getData().setScale(10);

        //ShapeRenderers are used for getting shapes into picture so that it is more clear for collision detection
        //Shapes are used for collision detection as it is not possible with textures
        //shapeRenderer = new ShapeRenderer();

        //Initializing Tubes
        topTube = new Texture("toptube.png");
        bottomTube = new Texture("bottomtube.png");
        topTubeRectangles = new Rectangle[numberOfTubes];
        bottomTubeRectangles = new Rectangle[numberOfTubes];

        maxOffSet = Gdx.graphics.getHeight() / 2 - gap / 2 - 100;
        randomGenerator = new Random();
        distanceBetweenTubes = Gdx.graphics.getWidth() * 3 / 4;

        startGame();

	}

    /**
     * startGame()
     * Initializes the position of the bird in the center of the screen
     * Initializes the position of the tubes for the first time
     */
	public void startGame() {

        birdY = Gdx.graphics.getHeight()/2 - birds[flapstat].getHeight()/2;

        for(int i = 0; i < numberOfTubes; i++) {

            tubeX[i] = Gdx.graphics.getWidth() / 2 - topTube.getWidth() / 2 + i*distanceBetweenTubes + Gdx.graphics.getWidth();
            tubeOffSet[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

            topTubeRectangles[i] = new Rectangle();
            bottomTubeRectangles[i] = new Rectangle();

        }

    }

	@Override
	public void render () {

        //To position the bird into the center of the screen
        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics. getHeight());


        //For bird to move up upon tap
        if(Gdx.input.justTouched()) {

            if(gameState == 0) {
                gameState = 1;
            } else {
                velocity = -25; //To make bird move in upward direction
            }

        }


        if(gameState == 1) {       //User has started the game

            //When tube is passed half the screen, User gets points
            if(tubeX[scoringTube] < Gdx.graphics.getWidth()/2) {

                score++;

                //Rotating the scoringTube
                if(scoringTube >= numberOfTubes - 1) {

                    scoringTube = 0;

                } else {

                    scoringTube++;

                }

            }

            if(birdY > 0) {

                velocity += gravity;
                birdY -= velocity;

            } else {

                gameState = 2; //Gameover state when bird hits the ground

            }

            for(int i = 0; i < numberOfTubes; i++) {

                if(tubeX[i] < - topTube.getWidth()) {

                    tubeX[i] = numberOfTubes * distanceBetweenTubes;
                    tubeOffSet[i] = (randomGenerator.nextFloat() - 0.5f) * (Gdx.graphics.getHeight() - gap - 200);

                } else {

                    tubeX[i] -= tubeVelocity;

                }
                batch.draw(topTube, tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffSet[i]);
                batch.draw(bottomTube, tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffSet[i]);

                topTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffSet[i], topTube.getWidth(), topTube.getHeight());
                bottomTubeRectangles[i] = new Rectangle(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffSet[i], bottomTube.getWidth(), bottomTube.getHeight());

            }

        } else if (gameState == 2) {    //Game Over

            //Game is over now
            batch.draw(gameOver, Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2, Gdx.graphics.getHeight() / 2 + gameOver.getHeight());

            if(Gdx.input.justTouched()) {

                gameState = 1;
                score = 0;
                startGame();
                velocity = 0;
                scoringTube = 0;

            }

        }

	    if(flapstat == 0) {
	        flapstat = 1;
        } else {
	        flapstat = 0;
        }


        batch.draw(birds[flapstat], Gdx.graphics.getWidth()/2 - birds[flapstat].getWidth()/2, birdY);
        font.draw(batch, String.valueOf(score), Gdx.graphics.getWidth()/2, Gdx.graphics.getHeight() * 4 / 5);
		batch.end();

        birdCircle.set(Gdx.graphics.getWidth() / 2, birdY + birds[flapstat].getHeight() / 2, birds[flapstat].getWidth() / 2);

		//shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		//shapeRenderer.setColor(Color.RED);
		//shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

        for(int i = 0; i < numberOfTubes; i++) {

            //shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 + gap / 2 + tubeOffSet[i], topTube.getWidth(), topTube.getHeight());
            //shapeRenderer.rect(tubeX[i], Gdx.graphics.getHeight() / 2 - gap / 2 - bottomTube.getHeight() + tubeOffSet[i], bottomTube.getWidth(), bottomTube.getHeight());

            //Collision detection when shapes intersect with each other
            if(Intersector.overlaps(birdCircle, topTubeRectangles[i]) || Intersector.overlaps(birdCircle, bottomTubeRectangles[i])){

                gameState = 2; //Gameover state when bird collides with any pipe

            }

        }
		//shapeRenderer.end();

	}
	
	@Override
	public void dispose () {
		batch.dispose();
		//img.dispose();
	}
}
