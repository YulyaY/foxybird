package com.foxybird.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.ScreenUtils;

import java.util.Random;

public class FoxyBird extends ApplicationAdapter {
	SpriteBatch batch;

	Texture background;

	Texture[] bird;
	int birdStateFlag = 0;
	float flyHeight;
	float fallingSpeed = 0;
	int gameStateFlag = 0;

	Texture topTube;
	Texture bottomTube;
	int spaceBetweenTube = 1000;
	Random random;
	int tubeSpeed = 2;
	int tubesNumber = 15;
	float tubeX[] = new float[tubesNumber];
	float tubeShift[] = new float[tubesNumber];
	float distanceBetweenTubes;

	Circle birdCircle;
	Rectangle[] topTubeRectangles;
	Rectangle[] bottomTubeRectangles;
//	ShapeRenderer shapeRenderer;

	int gameScore = 0;
	int passedTubeIndex = 0;
	BitmapFont scoreFont;

	Texture gameOver;
	
	@Override
	public void create () {
		batch = new SpriteBatch();
		background = new Texture("background.png");
//		shapeRenderer = new ShapeRenderer();

		birdCircle = new Circle();
		topTubeRectangles = new Rectangle[tubesNumber];
		bottomTubeRectangles = new Rectangle[tubesNumber];

		bird = new Texture[2];
		bird[0] = new Texture("bird_wings_up.png");
		bird[1] = new Texture("bird_wings_down.png");

		topTube = new Texture("top_tube.png");
		bottomTube = new Texture("bottom_tube.png");
		random = new Random();
		scoreFont = new BitmapFont();
		scoreFont.setColor(Color.CYAN);
		scoreFont.getData().setScale(10);

		gameOver = new Texture("game_over.png");

		distanceBetweenTubes = Gdx.graphics.getWidth() / 2;

		initGame();

	}

	public void initGame() {
		flyHeight = Gdx.graphics.getHeight() / 2 - bird[0].getHeight() / 2;

		for (int i = 0; i < tubesNumber; i++) {
			tubeX[i] = (float) (Gdx.graphics.getWidth() / 2
					- topTube.getWidth() / 2) + Gdx.graphics.getWidth() + i * distanceBetweenTubes;
			tubeShift[i] = (random.nextFloat() - 0.5f) *
					(Gdx.graphics.getHeight() - spaceBetweenTube - 200);
			topTubeRectangles[i] = new Rectangle();
			bottomTubeRectangles[i] = new Rectangle();
		}
	}

	@Override
	public void render () {

		batch.begin();
		batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		if (gameStateFlag == 1) {

			Gdx.app.log("Game score", String.valueOf(gameScore));

			if(tubeX[passedTubeIndex] < Gdx.graphics.getWidth() / 2) {
				gameScore++;
				if(passedTubeIndex < tubesNumber - 1) {
					passedTubeIndex++;
				} else {
					passedTubeIndex = 0;
				}
			}

			if(Gdx.input.justTouched()) {
				fallingSpeed = -30;
			}

			for (int i = 0; i < tubesNumber; i++) {

				if (tubeX[i] < -topTube.getWidth()) {
					tubeX[i] = tubesNumber * distanceBetweenTubes;
				} else {
					tubeX[i] -= tubeSpeed;
				}

				tubeX[i] -= tubeSpeed;
				batch.draw(topTube, (float) tubeX[i],
						Gdx.graphics.getHeight() / 2 + spaceBetweenTube / 2 + tubeShift[i],
						(float) topTube.getWidth(),
						(float) (topTube.getHeight() * 1.2));
				batch.draw(bottomTube, (float) tubeX[i],
						(float) (Gdx.graphics.getHeight() / 2 - spaceBetweenTube / 2 -
								bottomTube.getHeight() * 1.2 + tubeShift[i]),
						(float) bottomTube.getWidth(),
						(float) (bottomTube.getHeight() * 1.2));

				topTubeRectangles[i] = new Rectangle((float) tubeX[i],
						Gdx.graphics.getHeight() / 2 + spaceBetweenTube / 2 + tubeShift[i],
						topTube.getWidth(), (float) (topTube.getHeight() * 1.2));

				bottomTubeRectangles[i] = new Rectangle((float) tubeX[i],
						(float) (Gdx.graphics.getHeight() / 2 - spaceBetweenTube / 2 -
								bottomTube.getHeight() * 1.2 + tubeShift[i]),
						bottomTube.getWidth(),
						(float) (bottomTube.getHeight() * 1.2));
			}

			if (flyHeight > 0) {
				fallingSpeed++;
				flyHeight -= fallingSpeed;
			} else {
				gameStateFlag = 2;
			}

		} else if (gameStateFlag == 0){
			if(Gdx.input.justTouched()) {
				Gdx.app.log("Tap", "Oops!");
				gameStateFlag = 1;
			}
		} else if(gameStateFlag == 2) {
			batch.draw(gameOver,
					Gdx.graphics.getWidth() / 2 - gameOver.getWidth() / 2,
					Gdx.graphics.getHeight() / 2 - gameOver.getHeight() / 2);

			if(Gdx.input.justTouched()) {
				Gdx.app.log("Tap", "Oops!");
				gameStateFlag = 1;
				initGame();
				gameScore = 0;
				passedTubeIndex = 0;
				fallingSpeed = 0;
			}
		}

		if (birdStateFlag == 0) {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			birdStateFlag = 1;
		} else {
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			birdStateFlag = 0;
		}


		batch.draw(bird[birdStateFlag], Gdx.graphics.getWidth() / 2 -
						bird[birdStateFlag].getWidth() / 2, flyHeight);

		scoreFont.draw(batch, String.valueOf(gameScore), 100, 200);
		batch.end();

		birdCircle.set(Gdx.graphics.getWidth() / 2,
				flyHeight + bird[birdStateFlag].getHeight() / 2,
				bird[birdStateFlag].getWidth() / 2);
//		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
//		shapeRenderer.setColor(Color.CORAL);
//		shapeRenderer.circle(birdCircle.x, birdCircle.y, birdCircle.radius);

		for (int i = 0; i < tubesNumber; i++) {

//			shapeRenderer.rect((float) tubeX[i],
//					Gdx.graphics.getHeight() / 2 + spaceBetweenTube / 2 + tubeShift[i],
//					topTube.getWidth(), (float) (topTube.getHeight() * 1.2));
//			shapeRenderer.rect((float) tubeX[i],
//					(float) (Gdx.graphics.getHeight() / 2 - spaceBetweenTube / 2 -
//							bottomTube.getHeight() * 1.2 + tubeShift[i]),
//					bottomTube.getWidth(),
//					(float) (bottomTube.getHeight() * 1.2));

			if(Intersector.overlaps(birdCircle, topTubeRectangles[i]) ||
					Intersector.overlaps(birdCircle, bottomTubeRectangles[i])) {
				Gdx.app.log("Intersected", "Bump!");
				gameStateFlag = 2;
			}

		}


//		shapeRenderer.end();
	}
	
	@Override
	public void dispose () {

	}
}
