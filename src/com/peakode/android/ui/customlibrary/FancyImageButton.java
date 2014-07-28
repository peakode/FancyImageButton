package com.peakode.android.ui.customlibrary;

import com.peakode.android.fancybutton.R;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

@SuppressLint("DrawAllocation")
public class FancyImageButton extends ImageView {

	private static final int PRESSED_COLOR_LIGHTUP = 255 / 25;
	private static final int PRESSED_BORDER_ALPHA = 75;
	private static final int DEFAULT_PRESSED_BORDER_WIDTH_DIP = 4;
	private static final int ANIMATION_TIME_ID = android.R.integer.config_shortAnimTime;

	private float y;
	private float x;

	private Paint roundRectOuterPaint;
	private Paint focusPaint;
	private Paint roundRectInnerPaint;

	private float animationProgress;

	private int pressedBorderWidth;
	private int outerColor = Color.BLACK;
	private int innerColor = Color.WHITE;
	private int pressedColor;
	private ObjectAnimator pressedAnimator;

	public FancyImageButton(Context context) {
		super(context);
		init(context, null);
	}

	public FancyImageButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	public FancyImageButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	@Override
	public void setPressed(boolean pressed) {
		super.setPressed(pressed);

		if (roundRectOuterPaint != null) {
			roundRectOuterPaint.setColor(pressed ? pressedColor : outerColor);
		}

		if (pressed) {
			showPressedRing();
		} else {
			hidePressedRing();
		}
	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {

		// Pressed
		RectF rectPressed = new RectF((pressedBorderWidth * 2)
				- animationProgress, (pressedBorderWidth * 2)
				- animationProgress, (x - pressedBorderWidth * 2)
				+ animationProgress, (y - pressedBorderWidth * 2)
				+ animationProgress);

		canvas.drawRoundRect(rectPressed, 10f, 10f, focusPaint);

		// Outer
		RectF rectOuter = new RectF(pressedBorderWidth, pressedBorderWidth,
				(x - pressedBorderWidth), (y - pressedBorderWidth));

		canvas.drawRoundRect(rectOuter, 10f, 10f, roundRectOuterPaint);
		
		// Inner
		RectF rectInner = new RectF(pressedBorderWidth+1.5f, pressedBorderWidth+1.5f,
				(x - pressedBorderWidth)-1.5f, (y - pressedBorderWidth)-1.5f);

		canvas.drawRoundRect(rectInner, 10f, 10f, roundRectInnerPaint);

		super.onDraw(canvas);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		x = w;
		y = h;

	}

	public float getAnimationProgress() {
		return animationProgress;
	}

	public void setAnimationProgress(float animationProgress) {
		this.animationProgress = animationProgress;
		this.invalidate();
	}

	public void setColor(int innerColor, int outerColor) {
		this.outerColor = outerColor;
		this.innerColor = innerColor;
		this.pressedColor = getHighlightColor(outerColor, PRESSED_COLOR_LIGHTUP);

		roundRectOuterPaint.setColor(outerColor);
		
		focusPaint.setColor(outerColor);
		focusPaint.setAlpha(PRESSED_BORDER_ALPHA);
		
		roundRectInnerPaint.setColor(innerColor);

		this.invalidate();
	}

	private void hidePressedRing() {
		pressedAnimator.setFloatValues(pressedBorderWidth, 0f);
		pressedAnimator.start();
	}

	private void showPressedRing() {
		pressedAnimator.setFloatValues(animationProgress, pressedBorderWidth);
		pressedAnimator.start();
	}

	private void init(Context context, AttributeSet attrs) {
		this.setFocusable(true);
		this.setScaleType(ScaleType.CENTER_INSIDE);
		setClickable(true);

		roundRectOuterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		roundRectOuterPaint.setStyle(Paint.Style.FILL);
		
		roundRectInnerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		roundRectInnerPaint.setStyle(Paint.Style.FILL);

		focusPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		focusPaint.setStyle(Paint.Style.STROKE);

		pressedBorderWidth = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, DEFAULT_PRESSED_BORDER_WIDTH_DIP,
				getResources().getDisplayMetrics());

		int outerColor = Color.BLACK;
		int innerColor = Color.WHITE;
		
		if (attrs != null) {
			
			final TypedArray a = context.obtainStyledAttributes(attrs,
					R.styleable.FancyImageButton);
			
			outerColor = a.getColor(R.styleable.FancyImageButton_fibOuterColor, outerColor);
			
			innerColor = a.getColor(R.styleable.FancyImageButton_fibInnerColor, innerColor);
			
			pressedBorderWidth = (int) a.getDimension(
					R.styleable.FancyImageButton_fibPressedBorderWidth,
					pressedBorderWidth);
			
			a.recycle();
		}

		setColor(innerColor, outerColor);

		focusPaint.setStrokeWidth(pressedBorderWidth);
		final int pressedAnimationTime = getResources().getInteger(
				ANIMATION_TIME_ID);
		pressedAnimator = ObjectAnimator.ofFloat(this, "animationProgress", 0f,
				0f);
		pressedAnimator.setDuration(pressedAnimationTime);
	}

	private int getHighlightColor(int color, int amount) {
		return Color.argb(Math.min(255, Color.alpha(color)),
				Math.min(255, Color.red(color) + amount),
				Math.min(255, Color.green(color) + amount),
				Math.min(255, Color.blue(color) + amount));
	}
}
