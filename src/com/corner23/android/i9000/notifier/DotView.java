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
	
	public DotView(Context context) {
		super(context);
		
		Random rand = new Random();
		Ypos = rand.nextInt(150);
		Xpos = rand.nextInt(150);
		
		mDrawable = new ShapeDrawable(new RectShape());
		mDrawable.getPaint().setColor(Color.RED);
		mDrawable.setBounds(Xpos, Ypos, Xpos + width, Ypos + height);
	}

	protected void onDraw(Canvas canvas) {
		mDrawable.draw(canvas);
	}
}
