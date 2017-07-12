package com.stanly.puzzle;

import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

/*import com.google.android.gms.internal.dp;*/
import com.stanly.nudge.Screen;
import com.stanly.nudge.Sprite;

public class Card {

	static int radius = 5;
	public Sprite sprite;
	public float height, width;
	Paint paint = new Paint();
	Screen screen;
	boolean isSprite = false;
	public Paint textPaint = new Paint();
	public String text;
	public int number;

	float scale = 0;

	public Card(int number, float height, float width, Screen screen) {
		this.height = height;
		this.width = width;
		this.screen = screen;
		setCard(number);
		radius = screen.dpToPx(5);
	}

	public void setCard(int number) {
		if (screen.getResources().getStringArray(R.array.Card_text_or_image_name).length > number) {
			this.number = number;
			Resources res = screen.getResources();
			//get detais of card nmber #
			//color
			TypedArray colors = res.obtainTypedArray(R.array.Card_color);
			int color = colors.getColor(number, 0);
			colors.recycle();

			//type
			int type = res.getIntArray(R.array.Card_type)[number];
			if (type == 0)
				isSprite = false;
			else
				isSprite = true;

			//text/image name
			String content = res.getStringArray(R.array.Card_text_or_image_name)[number];

			//set paint
			this.paint = new Paint();
			paint.setColor(color);
			paint.setAntiAlias(true);

			//set content
			if (isSprite) {
				sprite = new Sprite(BitmapFactory.decodeResource(res, res.getIdentifier(content, "drawable", screen.getPackageName())), width * 0.75f);
			} else {
				text = content;
				//font
				Typeface ROBOTO = Typeface.createFromAsset(screen.getAssets(), "earth aircraft universe.ttf");
				textPaint = new Paint();
				textPaint.setTextSize(screen.dpToPx((int) (int) (0.15f * height)));
				textPaint.setAntiAlias(true);
				textPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
				textPaint.setColor(res.getColor(R.color.black));
				textPaint.setTypeface(ROBOTO);
			}
		}
	}

	/* returns true if player reached maximum tile */
	public boolean add() {
		if (screen.getResources().getStringArray(R.array.Card_text_or_image_name).length > number) {
			setCard(number + 1);
			scale = 1.2f;

			return false;
		}
		else
			return true;
	}

	//draw the cards and board to screen
	public void draw(Canvas canvas, float x, float y) {
		canvas.drawRoundRect(new RectF(x + (width / 2) - (width * scale) / 2, y + (height / 2) - (height * scale) / 2, x + (width * scale), y + (height * scale)), radius, radius, paint);
		if (isSprite) {
			sprite.draw(canvas, x + (width / 2) - (sprite.getWidth() / 2), y + (height / 2) - (sprite.getHeight() / 2));
		} else {
			Rect bounds = new Rect();
			textPaint.getTextBounds(text, 0, text.length(), bounds);

			canvas.drawText(text, x + (width / 2) - (bounds.width() / 2), (float) y + (height / 2) + bounds.height() / 2, textPaint);
		}

		if (Math.abs(scale - 1) < 0.0001f)
			scale = 1;
		if (scale < 1) {
			scale += 0.2f;
		}
		if (scale > 1) {
			scale -= 0.05f;
		}

	}

}
