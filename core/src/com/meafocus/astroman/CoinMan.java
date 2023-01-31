package com.meafocus.astroman;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.ArrayList;
import java.util.Random;

public class CoinMan extends ApplicationAdapter
{
	private final com.meafocus.astroman.AdsController adsController;

	SpriteBatch batch;
	private int inicialbackground1 = 0;
	int comprimentoBackground;
	private int inicialbackground2 = 0;
	private final int VIRTUAL_WIDTH = 1080;
	private final int VIRTUAL_HEIGHT = 1920;
	int larguraDispositivo = VIRTUAL_WIDTH;
	int alturaDispositivo = VIRTUAL_HEIGHT;

	// Texture -> Image
	Texture background;

	Texture[] man;
	private Texture gameOver;
	private Texture continueGame;
	int manState;
	int pause = 0;
	float gravity = 0.25f;
	float velocity = 0;
	int manY = 0;
	Rectangle manRectangle;

	// Coin objects
	ArrayList<Integer> coinXs = new ArrayList<>();
	ArrayList<Integer> coinYs = new ArrayList<>();
	ArrayList<Rectangle> coinRectangles = new ArrayList<>();
	Texture coin;
	int coinCount;

	// Bomb objects
	ArrayList<Integer> bombXs = new ArrayList<>();
	ArrayList<Integer> bombYs = new ArrayList<>();
	ArrayList<Rectangle> bombRectangles = new ArrayList<>();
	Texture bomb;

	int bombCount;

	Random random;

	int score;
	// for score text
	BitmapFont font;
	int gameState = 0;
	Texture dizzy;

	Music mario;
	Music coinSound;
	Music bombSound;

	int highestScore;
	int flag_rec;
	int scoreN = 0;
	int starPlus = 0;
	int podeContinuar = 0;

	BitmapFont highFont;
	BitmapFont highFont2;

	Preferences preferencias;

	//rotação de fundos de tela
	String[] opcoes = {"planeta1.jpg", "planeta2.jpg", "planeta3.jpg" };
	int fundo = new Random().nextInt(3);
	String opcaofundo = opcoes[fundo];

	//camera
	private OrthographicCamera camera;
	private Viewport viewport;

	public CoinMan(AdsController adsController) {
		this.adsController = adsController;
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void create ()// For First Time
	{
		batch = new SpriteBatch(); // STARTING POINT FOR PUTTING SOMETHING ON THE SCREEN
		background = new Texture(opcaofundo);
		comprimentoBackground = larguraDispositivo * 4;

		//Obtem o melhor placar salvo
		preferencias = Gdx.app.getPreferences("coinAstro");
		highestScore = preferencias.getInteger("highestScore",0);

		//CAMERA
		camera = new OrthographicCamera();
		camera.position.set(VIRTUAL_WIDTH /2, VIRTUAL_HEIGHT/2, 0);
		viewport = new StretchViewport(VIRTUAL_WIDTH, VIRTUAL_HEIGHT, camera);

		man = new Texture[4];
		man[0] = new Texture("frame-1a.png");
		man[1] = new Texture("frame-2a.png");
		man[2] = new Texture("frame-3a.png");
		man[3] = new Texture("frame-4a.png");

		manY = alturaDispositivo/2;  // CENTER OF SCREEN

		coin = new Texture("estrela128.png");
		bomb = new Texture("meteoritea.png");

		gameOver = new Texture("gameover.png");
		continueGame = new Texture("continue.png");

		random = new Random();

		font= new BitmapFont();
		font.setColor(Color.YELLOW);
		font.getData().setScale(8);

		dizzy = new Texture("dizzy-1a.png");

		mario = Gdx.audio.newMusic(Gdx.files.internal("mario.mp3"));
		coinSound = Gdx.audio.newMusic(Gdx.files.internal("coin.mp3"));
		bombSound = Gdx.audio.newMusic(Gdx.files.internal("bomb.mp3"));

		highFont= new BitmapFont();
		highFont.setColor(Color.WHITE);
		highFont.getData().setScale(5);

		highFont2= new BitmapFont();
		highFont2.setColor(Color.WHITE);
		highFont2.getData().setScale(2);
	}

	public void verificaRec ()
	{
		flag_rec = preferencias.getInteger("recompensa",0);
		Gdx.app.log("Recompensa","flag: " + flag_rec);

	}


	public void makeCoin()
	{
		float height  = random.nextFloat() * alturaDispositivo;
		coinYs.add((int)height);
		coinXs.add(larguraDispositivo);
	}

	public void makeBomb()
	{
		float height  = random.nextFloat() * alturaDispositivo;
		bombYs.add((int)height);
		bombXs.add(larguraDispositivo);
	}


	@Override
	public void render ()// RUNS UNTIL YOU FINISH THE GAME
	{
		//if(Gdx.input.justTouched()) if(adsController != null) adsController.showRewardedVideo();
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
		batch.setProjectionMatrix(camera.combined);
		batch.begin();

		batch.draw(background, inicialbackground1,0,comprimentoBackground,alturaDispositivo);
		batch.draw(background, (inicialbackground2 + comprimentoBackground),0,comprimentoBackground,alturaDispositivo);
		if(gameState == 1)
		{			// CHECK IF GAME IS LIVE
			batch.draw(background, inicialbackground1,0,comprimentoBackground,alturaDispositivo);
			batch.draw(background, (inicialbackground2 + comprimentoBackground),0,comprimentoBackground,alturaDispositivo);
			inicialbackground1 = inicialbackground1 - 3;
			inicialbackground2 =  inicialbackground2 - 3;
			if ((inicialbackground1 + comprimentoBackground) <= 0)
			{
				inicialbackground1 = 0;
				inicialbackground2 = 0;
			}

			// Bomb after 100 unit time
			mario.play();

			if (starPlus == 1){

				if(bombCount < 150)
				{
					bombCount++;
				}
				else
				{
					bombCount = 0;
					makeBomb();
				}

			}else {

				if(bombCount < 250)
				{
					bombCount++;
				}
				else
				{
					bombCount = 0;
					makeBomb();
				}

			}



			bombRectangles.clear();
			for (int i = 0; i < bombXs.size(); i++)
			{
				// for drawing

				batch.draw(bomb, bombXs.get(i), bombYs.get(i));

				bombXs.set(i, bombXs.get(i) - 8);
				// IF TOUCHED
				bombRectangles.add(new Rectangle(bombXs.get(i), bombYs.get(i), bomb.getWidth(), bomb.getHeight()));
			}
			// Coin after 100 unit time

			verificaRec();

			if (starPlus == 1){

				if(coinCount < 30)
				{
					coinCount++;
				}
				else
				{
					coinCount = 0;
					makeCoin();
				}


			}else {

				if(coinCount < 70)
				{
					coinCount++;
				}
				else
				{
					coinCount = 0;
					makeCoin();
				}


			}




			coinRectangles.clear();
			for (int i = 0; i < coinXs.size(); i++)
			{
				// for drawing
				batch.draw(coin, coinXs.get(i), coinYs.get(i));
				coinXs.set(i, coinXs.get(i) - 13);
				// if touched
				coinRectangles.add(new Rectangle(coinXs.get(i), coinYs.get(i), coin.getWidth(), coin.getHeight()));
			}

			// TO JUMP ON SINGLE TOUCH
			if(Gdx.input.justTouched())
			{
				velocity = -15;
			}

			//  TO BRING A LITTLE INTERVAL BETWEEN IMAGES (SLOW THE )
			if(pause < 6)
			{
				pause++;
			}
			else
			{
				pause = 0;
				// LOOP THROUGH DIFFERENT IMAGES IF THE MAN
				if (manState < 3)
				{
					manState++;
				}
				else
				{
					manState = 0;
				}
			}

			// FALLING DOWN
			velocity = velocity + gravity;
			manY -= velocity;
			// TO PREVENT HIM FROM GETTING OF THE SCREEN
			if(manY <= 0)
			{
				manY = 0;
			}

		}
		else if (gameState == 0)
		{// WAITING TO START

			if(Gdx.input.justTouched() )
			{
				gameState=1;
			}
		}
		else if (gameState == 2)
		{ // GAME OVER
			mario.stop();
			if(Gdx.input.justTouched() )
			{
				adsController.showRewardedVideo();
				gameState=1;
				manY = alturaDispositivo/2;  // CENTER OF SCREEN
				score = 0;
				velocity=0;
				coinCount = 0;
				bombCount =0;
				coinXs.clear();
				coinYs.clear();
				coinRectangles.clear();
				bombRectangles.clear();
				bombXs.clear();
				bombYs.clear();
				starPlus = 0;
			}
		}


		if(gameState == 2)
		{
			batch.draw(dizzy,larguraDispositivo/2 -100 - man[manState].getWidth(),manY);
			batch.draw(gameOver, larguraDispositivo / 2 - gameOver.getWidth()/2, alturaDispositivo / 2  - gameOver.getHeight()/2 );

			podeContinuar = preferencias.getInteger("podeContinuar",0);

			if (podeContinuar == 1){
				batch.draw(continueGame, larguraDispositivo / 2 - gameOver.getWidth()/2, alturaDispositivo / 4  - gameOver.getHeight()/2 );
			}


		}
		else
		{
			batch.draw(man[manState],larguraDispositivo/2-100 - man[manState].getWidth(),manY);

		}
// score recompensa
		if (flag_rec == 1){
//			preferencias = Gdx.app.getPreferences("coinAstro");

			scoreN = preferencias.getInteger("score",0);
			score = score + scoreN + 1;

			preferencias.putInteger("score", 0);
			preferencias.putInteger("recompensa", 0);
			preferencias.flush();
			starPlus = 1;
			preferencias.putInteger("podeContinuar", 0);
			preferencias.flush();


		}
		// fim score recompensa

		// drawing man at the center of the screen
		// 	FOR COIN COLLISION
		manRectangle = new Rectangle(larguraDispositivo/2 -100- man[manState].getWidth() , manY ,man[manState].getWidth(),man[manState].getHeight());
		for (int i = 0; i < coinRectangles.size(); i++)
		{
			if(Intersector.overlaps(manRectangle,coinRectangles.get(i)))
			{
				if (coinSound.isPlaying()){
					coinSound.stop();
				}
				coinSound.play();
//				Gdx.app.log("Moeda!","Colisão!");
				preferencias.putInteger("score", score);
				preferencias.flush();


				score++;
				if (highestScore<score)
				{
					preferencias.putInteger("highestScore", highestScore);
					preferencias.flush();
					highestScore = score;
				}
				// GET RID OF COIN
				coinRectangles.remove(i);
				coinXs.remove(i);
				coinYs.remove(i);
				break;
			}
		}

		// 	FOR BOMB COLLISION
		for (int i = 0; i < bombRectangles.size(); i++)
		{
			if(Intersector.overlaps(manRectangle,bombRectangles.get(i)))
			{
				if (gameState == 1) {
				bombSound.play();
//				Gdx.app.log("Bomba!","Colisão!");
				// game over
					preferencias.putInteger("recompensa", 0);
					preferencias.flush();

					gameState =2;
				}
			}
		}
		// SHOWING SCORE
		font.draw(batch, String.valueOf(score),larguraDispositivo - 1000,alturaDispositivo-100);
		highFont.draw(batch, String.valueOf(highestScore),larguraDispositivo - 200,alturaDispositivo-100);
		highFont2.draw(batch, "Recorde" ,larguraDispositivo - 165,alturaDispositivo-180);
		batch.end();
	}
	
	@Override
	public void dispose () {
		batch.dispose();
	}
}
