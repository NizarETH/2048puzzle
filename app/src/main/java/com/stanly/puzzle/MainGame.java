package com.stanly.puzzle;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.view.MotionEvent;
import android.widget.Toast;
/*import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;*/
import com.stanly.nudge.Button;
import com.stanly.nudge.Screen;
import com.stanly.nudge.Share;
import com.stanly.nudge.SingleScore;

public class MainGame extends Screen {

	//Paints
	Paint Title_Paint = new Paint();
	Paint subTitle_Paint = new Paint();
	Paint Background_Paint = new Paint();
	Paint score_paint = new Paint();
	Paint score_text_Paint = new Paint();
	Paint menu_background_Paint = new Paint();

	//states
	final int GAMEPLAY = 1, MENU = 2, GAMEOVER = 3, RESTART = 4, INSTRUCTIONS = 5;
	int state = GAMEPLAY;
	boolean reached_max = false;

	//score
	int score = 0;

	//ad
	/*private InterstitialAd interstitial;*/
	int ad_counter = 0;

	//board
	Board board;
	float board_x, board_y;

	//buttons
	Button restart, menu, restart_yes, restart_no, rate_app, how_to_play, share, exit, gameover_restart, gameover_share;

	//TODO: Unhighlight TO ADD Leaderboard
	//Button leaderboard;

	//time
	Calendar time = Calendar.getInstance();
	private long now = SystemClock.elapsedRealtime(), lastTick;

	//screenshot holder
	Bitmap screenshot;

	//best score
	SingleScore.Highscore BestScore;
	SingleScore scoreManager = new SingleScore(this);

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		//setDebugMode(true);
		//initialiseAccelerometer();

		/*if (getResources().getString(R.string.InterstitialAd_unit_id).length() > 0) {
			// Create the interstitial
			interstitial = new InterstitialAd(this);
			interstitial.setAdUnitId(getResources().getString(R.string.InterstitialAd_unit_id));
		}*/
		//initialise banner ad
		this.BANNER_AD_UNIT_ID = getResources().getString(R.string.BannerAd_unit_id);
		//showBanner();

		//restart time
		time.set(0, 0, 0, 0, 0, 0);

		//load local scores
		BestScore = scoreManager.load_localscore();
		if (BestScore.Details == null)
			saveBestScore();
	}

	/*public void openAd() {
		if (getResources().getString(R.string.InterstitialAd_unit_id).length() > 0) {
			runOnUiThread(new Runnable() {
				public void run() {
					System.out.println("show ad");
					// Create ad request.
					AdRequest adRequest = new AdRequest.Builder()
							.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
							.addTestDevice("275D94C2B5B93B3C4014933E75F92565")///nexus7//////testing
							.addTestDevice("91608B19766D984A3F929C31EC6AB947") /////////////////testing//////////////////remove///////////
							.addTestDevice("6316D285813B01C56412DAF4D3D80B40") ///test htc sensesion xl
							.addTestDevice("8C416F4CAF490509A1DA82E62168AE08")//asus transformer
							.addTestDevice("7B4C6D080C02BA40EF746C4900BABAD7")//Galaxy S4
							.build();

					// Begin loading your interstitial.
					interstitial.loadAd(adRequest);

					if (interstitial.isLoaded()) {
						interstitial.show();
					}
				}
			});
		}
	}*/

	@Override
	public void Start() {
		super.Start();
		//fonts
		Typeface ROBOTO_medium = Typeface.createFromAsset(getAssets(), "earth aircraft universe.ttf");
		Typeface ROBOTO_bold = Typeface.createFromAsset(getAssets(), "earth aircraft universe.ttf");

		//TODO: change any font sizes from here.
		//set paints
		//title
		Title_Paint.setTextSize(dpToPx(65));
		Title_Paint.setAntiAlias(true);
		Title_Paint.setColor(getResources().getColor(R.color.trans_white_background));
		Title_Paint.setTypeface(ROBOTO_bold);
		//subTitle
		subTitle_Paint.setTextSize(dpToPx(25));
		subTitle_Paint.setAntiAlias(true);
		subTitle_Paint.setColor(getResources().getColor(R.color.black));
		subTitle_Paint.setTypeface(ROBOTO_medium);

		//score text paint
		score_text_Paint.setTextSize(dpToPx(25));
		score_text_Paint.setAntiAlias(true);
		score_text_Paint.setColor(getResources().getColor(R.color.black));
		score_text_Paint.setTypeface(ROBOTO_medium);

		//background paint
		Background_Paint.setAntiAlias(true);
		Background_Paint.setColor(getResources().getColor(R.color.background_color));

		//score background paint
		score_paint = new Paint();
		score_paint.setColor(this.getResources().getColor(R.color.brown));
		score_paint.setAntiAlias(true);

		menu_background_Paint = new Paint();
		menu_background_Paint.setColor(this.getResources().getColor(R.color.trans_white_background));
		menu_background_Paint.setAntiAlias(true);

		//set world origin
		setOrigin(BOTTOM_LEFT);

		//initialise board
		float board_width = 0.75f * ScreenWidth();
		board_x = (ScreenWidth() - board_width) / 2;
		board_y = 0.25f * ScreenHeight();
		board = new Board(getResources().getInteger(R.integer.Board_width), getResources().getInteger(R.integer.Board_width), this, board_width, board_width);

		//initialise buttons
		//gameplay buttons
		restart = new Button(getResources().getString(R.string.Restart), 25, ROBOTO_medium, getResources().getColor(R.color.red), board_x, board_y, this, false);
		restart.y = board_y - restart.getHeight() - dpToPx(5);

		menu = new Button(getResources().getString(R.string.Menu), 25, ROBOTO_medium, getResources().getColor(R.color.red), board_x, board_y, this, false);
		menu.y = board_y - menu.getHeight() - dpToPx(5);
		menu.x = board_x + board_width - menu.getWidth();

		//restart menu
		restart_yes = new Button(getResources().getString(R.string.Yes), 25, ROBOTO_medium, getResources().getColor(R.color.black), board_x, board_y, this, false);
		restart_yes.y = ScreenHeight() / 2;
		restart_yes.x = ScreenWidth() / 2 - restart_yes.getWidth() - dpToPx(20);

		restart_no = new Button(getResources().getString(R.string.No), 25, ROBOTO_medium, getResources().getColor(R.color.black), ScreenWidth() / 2 + dpToPx(20), ScreenHeight() / 2, this, false);

		//main menu
		//TODO: Unhighlight TO ADD Leaderboard
		//leaderboard = new Button(getResources().getString(R.string.Leaderboard), 25, ROBOTO_medium, getResources().getColor(R.color.black), 0, ScreenHeight() * 0.4f, this, false);
		//leaderboard.x = ScreenWidth() / 2 - leaderboard.getWidth() / 2;
		//rate_app = new Button(getResources().getString(R.string.Rate), 25, ROBOTO_medium, getResources().getColor(R.color.black), 0, leaderboard.y + leaderboard.getHeight() + dpToPx(20), this, false);

		rate_app = new Button(getResources().getString(R.string.Rate), 25, ROBOTO_medium, getResources().getColor(R.color.black), 0, ScreenHeight() * 0.4f, this, false);
		rate_app.x = ScreenWidth() / 2 - rate_app.getWidth() / 2;

		how_to_play = new Button(getResources().getString(R.string.How_To_Play), 25, ROBOTO_medium, getResources().getColor(R.color.black), 0, rate_app.y + rate_app.getHeight() + dpToPx(20), this, false);
		how_to_play.x = ScreenWidth() / 2 - how_to_play.getWidth() / 2;

		share = new Button(getResources().getString(R.string.Share_menu), 25, ROBOTO_medium, getResources().getColor(R.color.black), 0, how_to_play.y + how_to_play.getHeight() + dpToPx(20), this, false);
		share.x = ScreenWidth() / 2 - share.getWidth() / 2;

		exit = new Button(getResources().getString(R.string.Exit), 25, ROBOTO_medium, getResources().getColor(R.color.black), 0, share.y + share.getHeight() + dpToPx(20), this, false);
		exit.x = ScreenWidth() / 2 - exit.getWidth() / 2;

		//game over menu
		gameover_share = new Button(getResources().getString(R.string.Share), 25, ROBOTO_medium, getResources().getColor(R.color.black), 0, ScreenHeight() * 0.5f, this, false);
		gameover_share.x = ScreenWidth() / 2 - gameover_share.getWidth() / 2;

		gameover_restart = new Button(getResources().getString(R.string.Restart_gameover), 25, ROBOTO_medium, getResources().getColor(R.color.black), 0, gameover_share.y + gameover_share.getHeight() + dpToPx(20), this, false);
		gameover_restart.x = ScreenWidth() / 2 - gameover_restart.getWidth() / 2;

		//refresh game and start
		StartGame();

	}

	@Override
	synchronized public void Step() {
		super.Step();
		if (state == GAMEPLAY) {
			//update score
			score = board.getTotalScore();

			//update clock
			now = SystemClock.elapsedRealtime();
			if (now - lastTick > 1000) {//every 1 second
				lastTick = SystemClock.elapsedRealtime();
				time.add(Calendar.SECOND, 1);
			}
		}

	}

	@Override
	public synchronized void onAccelerometer(PointF point) {

	}

	@Override
	public synchronized void BackPressed() {
		if (state == GAMEPLAY) {
			Exit();
		} else if (state == RESTART) {
			state = GAMEPLAY;
		} else if (state == INSTRUCTIONS) {
			state = GAMEPLAY;
		} else if (state == MENU) {
			state = GAMEPLAY;
		} else if (state == GAMEOVER) {
			StartGame();
		}
	}

	@Override
	public synchronized void onTouch(float TouchX, float TouchY, MotionEvent event) {
		//all button eevents here
		if (state == MENU) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				//TODO: Unhighlight TO ADD Leaderboard
				//if (leaderboard.isTouched(event)) {
				//	leaderboard.Highlight(getResources().getColor(R.color.red));
				//}
				if (rate_app.isTouched(event)) {
					rate_app.Highlight(getResources().getColor(R.color.red));
				}
				if (how_to_play.isTouched(event)) {
					how_to_play.Highlight(getResources().getColor(R.color.red));
				}
				if (share.isTouched(event)) {
					share.Highlight(getResources().getColor(R.color.red));
				}
				if (exit.isTouched(event)) {
					exit.Highlight(getResources().getColor(R.color.red));
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				//leaderboard.LowLight(); //TODO: Unhighlight TO ADD Leaderboard
				rate_app.LowLight();
				how_to_play.LowLight();
				share.LowLight();
				exit.LowLight();

				//TODO: Unhighlight TO ADD Leaderboard
				//if (leaderboard.isTouched(event)) {
				//TODO: feel free to open leaderboard here.
				//}
				if (rate_app.isTouched(event)) {
					Rate();
				}
				if (how_to_play.isTouched(event)) {
					state = INSTRUCTIONS;
				}
				if (share.isTouched(event)) {
					share();
				}
				if (exit.isTouched(event)) {
					Exit();
				}
			}
		} else if (state == RESTART) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (restart_no.isTouched(event)) {
					restart_no.Highlight(getResources().getColor(R.color.red));
				}
				if (restart_yes.isTouched(event)) {
					restart_yes.Highlight(getResources().getColor(R.color.red));
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				restart_no.LowLight();
				restart_yes.LowLight();

				if (restart_no.isTouched(event)) {
					state = GAMEPLAY;
				}
				if (restart_yes.isTouched(event)) {
					StartGame();
				}
			}

		} else if (state == GAMEOVER) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (gameover_restart.isTouched(event)) {
					gameover_restart.Highlight(getResources().getColor(R.color.red));
				}
				if (gameover_share.isTouched(event)) {
					gameover_share.Highlight(getResources().getColor(R.color.red));
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				gameover_share.LowLight();
				gameover_restart.LowLight();

				if (gameover_share.isTouched(event)) {
					share();
				}
				if (gameover_restart.isTouched(event)) {
					StartGame();
				}
			}
		} else if (state == GAMEPLAY) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				if (menu.isTouched(event)) {
					menu.Highlight(getResources().getColor(R.color.black));
				}
				if (restart.isTouched(event)) {
					restart.Highlight(getResources().getColor(R.color.black));
				}
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				menu.LowLight();
				restart.LowLight();

				if (menu.isTouched(event)) {
					state = MENU;
				}
				if (restart.isTouched(event)) {
					state = RESTART;
				}
			}

		}

	}

	@Override
	public void onSwipeLeft() {
		if (state == GAMEPLAY) {
			if (board.Swipe(Board.LEFT)) {
				state = GAMEOVER;
				reached_max = true;
			}
			if (!board.isThereAnEmptySpace()) {
				if (!board.isSwipeAvailable()) {
					GameOver();
				}
			}
		}
	}

	@Override
	public void onSwipeRight() {
		if (state == GAMEPLAY) {
			if (board.Swipe(Board.RIGHT)) {
				state = GAMEOVER;
				reached_max = true;
			}
			if (!board.isThereAnEmptySpace()) {
				if (!board.isSwipeAvailable()) {
					GameOver();
				}
			}
		}
	}

	@Override
	public void onSwipeUp() {
		if (state == GAMEPLAY) {
			if (board.Swipe(Board.UP)) {
				state = GAMEOVER;
				reached_max = true;
			}
			if (!board.isThereAnEmptySpace()) {
				if (!board.isSwipeAvailable()) {
					GameOver();
				}
			}
		}
	}

	@Override
	public void onSwipeDown() {
		if (state == GAMEPLAY) {
			if (board.Swipe(Board.DOWN)) {
				state = GAMEOVER;
				reached_max = true;
			}
			if (!board.isThereAnEmptySpace()) {
				if (!board.isSwipeAvailable()) {
					GameOver();
				}
			}
		}
	}

	//..................................................Game Functions..................................................................................................................................

	public void StartGame() {

		//refresh score
		score = 0;

		//refresh camera
		cameraY = 0;
		cameraX = 0;

		//refresh board
		board.clear();

		state = GAMEPLAY;

		//restart time
		time.set(0, 0, 0, 0, 0, 0);
		reached_max = false;
	}

	public synchronized void GameOver() {
		saveBestScore();
		if (ad_counter >= getResources().getInteger(R.integer.ad_shows_every_X_gameovers)) {
		/*	openAd();*/
			ad_counter = 0;
		}
		ad_counter++;
		state = GAMEOVER;
	}

	public void Rate() {
		Uri uri = Uri.parse("market://details?id=" + getPackageName());
		Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
		try {
			startActivity(goToMarket);
		} catch (ActivityNotFoundException e) {
			Toast.makeText(this, getResources().getString(R.string.unable_to_reach_market), Toast.LENGTH_LONG).show();
		}
	}

	public void share() {
		//share
		Share sharer = new Share();
		screenshot = Bitmap.createBitmap(ScreenWidth(), ScreenHeight(), Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas();
		canvas.setBitmap(screenshot);
		int temp_state = state;
		state = GAMEPLAY;
		Draw(canvas);
		state = temp_state;
		sharer.share_screenshot(this, screenshot);
	}

	public void saveBestScore() {
		//convert time to string
		SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");

		//load local scores
		BestScore = scoreManager.load_localscore();

		if (score >= BestScore.score) {

			SingleScore.Highscore temp_score = new SingleScore.Highscore();
			temp_score.score = score;
			temp_score.Details = new String[1];
			temp_score.Details[0] = dateFormat.format(time.getTime());
			scoreManager.save_localscores(temp_score);
			BestScore = scoreManager.load_localscore();
		}

	}

	//...................................................Rendering of screen............................................................................................................................
	@Override
	public void Draw(Canvas canvas) {
		float margin = dpToPx(4);
		float scoreBox_width = dpToPx(70);
		float bestBox_width = dpToPx(70);
		float scorex1 = board_x;
		float scorex2 = board_x + board.board_width - scoreBox_width;
		float scorey = 0.25f * ScreenHeight() + board.board_height + dpToPx(10);

		//draw background
		canvas.drawRect(0, 0, ScreenWidth(), ScreenHeight(), Background_Paint);

		//draw Titles__________________________________________________________________________________
		//draw title
		canvas.drawText(getResources().getString(R.string.Title), (ScreenWidth() / 2) - (Title_Paint.measureText(getResources().getString(R.string.Title)) / 2), (float) (ScreenHeight() * 0.15f), Title_Paint);

		//draw subtitle
		Rect bounds = new Rect();
		Title_Paint.getTextBounds(getResources().getString(R.string.Title), 0, getResources().getString(R.string.Title).length(), bounds);
		canvas.drawText(getResources().getString(R.string.Subtitle), (ScreenWidth() / 2) - (subTitle_Paint.measureText(getResources().getString(R.string.Subtitle)) / 2), (float) (ScreenHeight() * 0.15f) + (bounds.height() / 2) + margin, subTitle_Paint);

		//draw board___________________________________________________________________________________
		board.draw(canvas, board_x, board_y);

		//draw score boxes_____________________________________________________________________________
		//SCORE
		canvas.drawRoundRect(new RectF(scorex1, scorey, scorex1 + scoreBox_width, scorey + scoreBox_width), dpToPx(5), dpToPx(5), score_paint);
		subTitle_Paint.getTextBounds(getResources().getString(R.string.Score), 0, getResources().getString(R.string.Score).length(), bounds);
		canvas.drawText(getResources().getString(R.string.Score), scorex1 + (scoreBox_width / 2) - (bounds.width() / 2), scorey + bounds.height() + margin, subTitle_Paint);
		//braw score
		score_text_Paint.getTextBounds("" + score, 0, ("" + score).length(), bounds);
		canvas.drawText("" + score, scorex1 + (scoreBox_width / 2) - (bounds.width() / 2), scorey + (scoreBox_width / 2) + (bounds.height() / 2), score_text_Paint);

		//TIME
		canvas.drawRoundRect(new RectF(scorex2, scorey, scorex2 + scoreBox_width, scorey + scoreBox_width), dpToPx(5), dpToPx(5), score_paint);
		subTitle_Paint.getTextBounds(getResources().getString(R.string.Time), 0, getResources().getString(R.string.Time).length(), bounds);
		canvas.drawText(getResources().getString(R.string.Time), scorex2 + (scoreBox_width / 2) - (bounds.width() / 2), scorey + bounds.height() + margin, subTitle_Paint);
		//convert time to string
		SimpleDateFormat dateFormat = new SimpleDateFormat("mm:ss");
		String time_string = dateFormat.format(time.getTime());
		//draw time
		score_text_Paint.getTextBounds("" + time_string, 0, ("" + time_string).length(), bounds);
		canvas.drawText("" + time_string, scorex2 + (scoreBox_width / 2) - (bounds.width() / 2), scorey + (scoreBox_width / 2) + (bounds.height() / 2), score_text_Paint);

		//BEST
		canvas.drawRoundRect(new RectF(ScreenWidth() / 2 - bestBox_width / 2, scorey, ScreenWidth() / 2 + bestBox_width / 2, scorey + scoreBox_width), dpToPx(5), dpToPx(5), score_paint);
		subTitle_Paint.getTextBounds(getResources().getString(R.string.Best), 0, getResources().getString(R.string.Best).length(), bounds);
		canvas.drawText(getResources().getString(R.string.Best), ScreenWidth() / 2 - (bounds.width() / 2), scorey + bounds.height() + margin, subTitle_Paint);
		//best score
		score_text_Paint.getTextBounds("" + BestScore.score, 0, ("" + BestScore.score).length(), bounds);
		canvas.drawText("" + BestScore.score, (ScreenWidth() / 2) - (bounds.width() / 2), scorey + (scoreBox_width * 0.4f) + (bounds.height() / 2), score_text_Paint);
		//best time
		score_text_Paint.getTextBounds(BestScore.Details[0], 0, (BestScore.Details[0]).length(), bounds);
		canvas.drawText(BestScore.Details[0], (ScreenWidth() / 2) - (bounds.width() / 2), scorey + (scoreBox_width * 0.4f) + (bounds.height() * 2), score_text_Paint);

		//draw gameplay buttons
		restart.draw(canvas);
		menu.draw(canvas);

		//MENU/ RESTART/ GAMEOVER/ WIN overlays_________________________________________________________
		if (state == MENU) {
			//draw white overlay
			canvas.drawRect(0, 0, ScreenWidth(), ScreenHeight(), menu_background_Paint);

			//draw menu
			canvas.drawText(getResources().getString(R.string.Menu), (ScreenWidth() / 2) - (Title_Paint.measureText(getResources().getString(R.string.Menu)) / 2), (float) (ScreenHeight() * 0.35f), Title_Paint);

			//draw menu buttons
			//leaderboard.draw(canvas);		//TODO: Unhighlight TO ADD Leaderboard
			rate_app.draw(canvas);
			how_to_play.draw(canvas);
			share.draw(canvas);
			exit.draw(canvas);
		} else if (state == RESTART) {
			//draw white overlay
			canvas.drawRect(0, 0, ScreenWidth(), ScreenHeight(), menu_background_Paint);

			//draw restart
			canvas.drawText(getResources().getString(R.string.Restart_question), (ScreenWidth() / 2) - (Title_Paint.measureText(getResources().getString(R.string.Restart_question)) / 2), (float) (ScreenHeight() * 0.35f), Title_Paint);

			//draw restart buttons
			restart_no.draw(canvas);
			restart_yes.draw(canvas);
		} else if (state == GAMEOVER) {
			//draw white overlay
			canvas.drawRect(0, 0, ScreenWidth(), ScreenHeight(), menu_background_Paint);

			if (reached_max) {
				//draw you won
				canvas.drawText(getResources().getString(R.string.Win), (ScreenWidth() / 2) - (Title_Paint.measureText(getResources().getString(R.string.Win)) / 2), (float) (ScreenHeight() * 0.35f), Title_Paint);
			} else {
				//draw gameover
				canvas.drawText(getResources().getString(R.string.game_over), (ScreenWidth() / 2) - (Title_Paint.measureText(getResources().getString(R.string.game_over)) / 2), (float) (ScreenHeight() * 0.35f), Title_Paint);
			}
			
			//draw share and restart buttons
			gameover_restart.draw(canvas);
			gameover_share.draw(canvas);
		}
		else if (state == INSTRUCTIONS) {
			//draw white overlay
			canvas.drawRect(0, 0, ScreenWidth(), ScreenHeight(), menu_background_Paint);

			//draw instructions title
			canvas.drawText(getResources().getString(R.string.How_To_Play), (ScreenWidth() / 2) - (Title_Paint.measureText(getResources().getString(R.string.How_To_Play)) / 2), (float) (ScreenHeight() * 0.35f), Title_Paint);

			//draw instructions
			StaticLayout instructionlayout = new StaticLayout(getResources().getString(R.string.help), new TextPaint(score_text_Paint), (int) board.board_width, Layout.Alignment.ALIGN_NORMAL, 1.3f, 0, false);
			canvas.translate(board_x, (float) (ScreenHeight() * 0.45f)); //position the text
			instructionlayout.draw(canvas);
			canvas.translate(-board_x, (float) -(ScreenHeight() * 0.45f)); //position the text
		}

		//physics.drawDebug(canvas);
		super.Draw(canvas);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
