package com.corner23.android.i9000.notifier;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;

public class DotView extends View {

	private final ShapeDrawable mDrawable;
	private int Ypos = 0;
	private int Xpos = 0;
	private static final int width = 8, height = 8;
	private boolean bShowDot = true;
	
	public DotView(Context context) {
		super(context);
		
		mDrawable = new ShapeDrawable(new RectShape());
		mDrawable.getPaint().setColor(Color.RED);
	}

	protected void onDraw(Canvas canvas) {
		canvas.drawColor(Color.BLACK);
		if (bShowDot) {
			mDrawable.draw(canvas);
		}
	}
	
	public void hideDot() {
		bShowDot = false;
	}
	
	public void showDot(int color) {
		bShowDot = true;

		Random rand = new Random();
		Ypos = rand.nextInt(150);
		Xpos = rand.nextInt(150);
		
		mDrawable.setBounds(Xpos, Ypos, Xpos + width, Ypos + height);
		mDrawable.getPaint().setColor(color);
	}
}
