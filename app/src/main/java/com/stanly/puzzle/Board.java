package com.stanly.puzzle;

import java.util.Random;

import com.stanly.nudge.Screen;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

public class Board {
	Card Cards[][];
	public float board_width, board_height;
	public int board_cards_x, board_cards_y;
	int radius;
	Paint paint = new Paint();
	Paint blank_card_paint = new Paint();
	Screen screen;

	float Card_width, Card_spacing;

	static final int LEFT = 0, RIGHT = 1, UP = 2, DOWN = 3;

	public Board(int board_cards_x, int board_cards_y, Screen screen, float board_width, float board_height) {
		Cards = new Card[board_cards_x][board_cards_y];
		this.board_width = board_width;
		this.board_height = board_height;
		this.board_cards_y = board_cards_y;
		this.board_cards_x = board_cards_x;
		this.screen = screen;

		//set radius
		radius = screen.dpToPx(5);

		//set paint
		this.paint = new Paint();
		paint.setColor(screen.getResources().getColor(R.color.brown));
		paint.setAntiAlias(true);

		this.blank_card_paint = new Paint();
		blank_card_paint.setColor(screen.getResources().getColor(R.color.trans_white));
		blank_card_paint.setAntiAlias(true);

		//calculate card height/width
		Card_spacing = board_width / (board_cards_x * 10);
		Card_width = ((board_width - (Card_spacing * board_cards_x)) / (board_cards_x));

		generateRandomCard(1);

	}

	/* generate a card from 0 to max */
	public void generateRandomCard(int max) {
		Random r = new Random();
		while (true) {
			if (isThereAnEmptySpace()) {
				int x = r.nextInt(board_cards_x);
				int y = r.nextInt(board_cards_y);

				if (isEmpty(x, y)) {
					Cards[x][y] = new Card(r.nextInt(max + 1), Card_width, Card_width, screen);
					return;
				}
			} else
				return;
		}
	}

	public boolean isEmpty(int x, int y) {
		return (Cards[x][y] == null);
	}

	public boolean isThereAnEmptySpace() {
		for (int x_count = 0; x_count < board_cards_x; x_count++) {
			for (int y_count = 0; y_count < board_cards_y; y_count++) {
				if (isEmpty(x_count, y_count))
					return true;
			}
		}
		return false;
	}

	public void moveCard(int x, int y, int x_new, int y_new) {
		Cards[x_new][y_new] = Cards[x][y];
		Cards[x][y] = null;
	}

	public void addCards(int x, int y, int x_new, int y_new) {
		Cards[x_new][y_new].add();
		Cards[x][y] = null;
	}

	public boolean areSameNumber(int x, int y, int x_new, int y_new) {
		if (Cards[x][y] != null && Cards[x_new][y_new] != null)
			return (Cards[x][y].number == Cards[x_new][y_new].number);
		else
			return false;
	}

	//returns true if game won
	public boolean Swipe(int direction) {
		//swipe left
		int moves;
		do {
			moves = 0;
			if (direction == LEFT) {
				for (int x_count = 1; x_count < board_cards_x; x_count++) {
					for (int y_count = 0; y_count < board_cards_y; y_count++) {
						if (!isEmpty(x_count, y_count)) {
							if (isEmpty(x_count - 1, y_count)) {
								moveCard(x_count, y_count, x_count - 1, y_count);
								moves++;
							} else if (areSameNumber(x_count, y_count, x_count - 1, y_count)) {
								addCards(x_count, y_count, x_count - 1, y_count);
								//check if player won
								if (screen.getResources().getStringArray(R.array.Card_text_or_image_name).length <= Cards[x_count - 1][y_count].number+1)
									return true;

								moves++;
							}
						}

					}
				}
			}
			if (direction == RIGHT) {
				for (int x_count = board_cards_x - 2; x_count >= 0; x_count--) {
					for (int y_count = 0; y_count < board_cards_y; y_count++) {
						if (!isEmpty(x_count, y_count)) {
							if (isEmpty(x_count + 1, y_count)) {
								moveCard(x_count, y_count, x_count + 1, y_count);
								moves++;
							} else if (areSameNumber(x_count, y_count, x_count + 1, y_count)) {
								addCards(x_count, y_count, x_count + 1, y_count);
								//check if player won
								if (screen.getResources().getStringArray(R.array.Card_text_or_image_name).length <= Cards[x_count + 1][y_count].number+1)
									return true;
								moves++;
							}
						}

					}
				}
			}
			if (direction == UP) {
				for (int x_count = 0; x_count < board_cards_x; x_count++) {
					for (int y_count = 1; y_count < board_cards_y; y_count++) {
						if (!isEmpty(x_count, y_count)) {
							if (isEmpty(x_count, y_count - 1)) {
								moveCard(x_count, y_count, x_count, y_count - 1);
								moves++;
							} else if (areSameNumber(x_count, y_count, x_count, y_count - 1)) {
								addCards(x_count, y_count, x_count, y_count - 1);
								//check if player won
								if (screen.getResources().getStringArray(R.array.Card_text_or_image_name).length <= Cards[x_count][y_count - 1].number+1)
									return true;
								moves++;
							}
						}
					}
				}
			}
			if (direction == DOWN) {
				for (int x_count = 0; x_count < board_cards_x; x_count++) {
					for (int y_count = board_cards_y - 2; y_count >= 0; y_count--) {
						if (!isEmpty(x_count, y_count)) {
							if (isEmpty(x_count, y_count + 1)) {
								moveCard(x_count, y_count, x_count, y_count + 1);
								moves++;
							} else if (areSameNumber(x_count, y_count, x_count, y_count + 1)) {
								addCards(x_count, y_count, x_count, y_count + 1);
								//check if player won
								if (screen.getResources().getStringArray(R.array.Card_text_or_image_name).length <= Cards[x_count][y_count + 1].number+1)
									return true;
								moves++;
							}
						}

					}
				}
			}
		} while (moves > 0);
		generateRandomCard(1);
		return false;
	}

	public boolean isSwipeAvailable() {
		//swipe left
		boolean isSwipeAvailable = false;

		for (int x_count = 1; x_count < board_cards_x; x_count++) {
			for (int y_count = 0; y_count < board_cards_y; y_count++) {
				if (!isEmpty(x_count, y_count)) {
					if (isEmpty(x_count - 1, y_count) || areSameNumber(x_count, y_count, x_count - 1, y_count)) {
						isSwipeAvailable = true;
					}
				}

			}
		}

		for (int x_count = board_cards_x - 2; x_count >= 0; x_count--) {
			for (int y_count = 0; y_count < board_cards_y; y_count++) {
				if (!isEmpty(x_count, y_count)) {
					if (isEmpty(x_count + 1, y_count) || areSameNumber(x_count, y_count, x_count + 1, y_count)) {
						isSwipeAvailable = true;
					}
				}

			}
		}
		for (int x_count = 0; x_count < board_cards_x; x_count++) {
			for (int y_count = 1; y_count < board_cards_y; y_count++) {
				if (!isEmpty(x_count, y_count)) {
					if (isEmpty(x_count, y_count - 1) || areSameNumber(x_count, y_count, x_count, y_count - 1)) {
						isSwipeAvailable = true;
					}
				}
			}
		}

		for (int x_count = 0; x_count < board_cards_x; x_count++) {
			for (int y_count = board_cards_y - 2; y_count >= 0; y_count--) {
				if (!isEmpty(x_count, y_count)) {
					if (isEmpty(x_count, y_count + 1) || areSameNumber(x_count, y_count, x_count, y_count + 1)) {
						isSwipeAvailable = true;
					}
				}

			}

		}
		return isSwipeAvailable;

	}

	public int getTotalScore() {
		int score = 0;
		for (int x_count = 0; x_count < board_cards_x; x_count++) {
			for (int y_count = 0; y_count < board_cards_y; y_count++) {
				if (!isEmpty(x_count, y_count)) {
					score += Math.pow(2, Cards[x_count][y_count].number + 1);
				}
			}
		}
		return score;
	}

	public void clear() {
		for (int x_count = 0; x_count < board_cards_x; x_count++) {
			for (int y_count = 0; y_count < board_cards_y; y_count++) {
				Cards[x_count][y_count] = null;
			}
		}
		generateRandomCard(1);
		//				for (int x_count = 0; x_count < board_cards_x; x_count++) {
		//					for (int y_count = 0; y_count < board_cards_y; y_count++) {
		//						Cards[x_count][y_count] = new Card(10, Card_width, Card_width, screen);
		//					}
		//				}
	}

	//draw the cards and board to screen
	public void draw(Canvas canvas, float x, float y) {

		canvas.drawRoundRect(new RectF(x, y, x + board_width, y + board_height), radius, radius, paint);

		//loop through all cards and draw them
		for (int x_count = 0; x_count < board_cards_x; x_count++) {
			for (int y_count = 0; y_count < board_cards_y; y_count++) {
				float card_x = (Card_spacing / 2) + x + ((x_count) * (Card_width + Card_spacing));
				float card_y = (Card_spacing / 2) + y + ((y_count) * (Card_width + Card_spacing));

				canvas.drawRoundRect(new RectF(card_x, card_y, card_x + Card_width, card_y + Card_width), Card.radius, Card.radius, blank_card_paint);
				if (Cards[x_count][y_count] != null)
					Cards[x_count][y_count].draw(canvas, card_x, card_y);
			}
		}

	}
}
