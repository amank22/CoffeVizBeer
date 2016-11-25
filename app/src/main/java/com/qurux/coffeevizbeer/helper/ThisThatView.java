package com.qurux.coffeevizbeer.helper;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeControllerBuilder;
import com.facebook.drawee.drawable.ScalingUtils;
import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.generic.GenericDraweeHierarchyBuilder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.DraweeHolder;
import com.facebook.drawee.view.MultiDraweeHolder;
import com.qurux.coffeevizbeer.R;

import java.util.List;

/**
 * Created by Aman Kapoor on 19-11-2016.
 */

public class ThisThatView extends View {

    private static final int desiredWidth = 300;
    private static final int desiredHeight = 200;
    private GestureDetector gestureDetector;
    private MultiDraweeHolder<GenericDraweeHierarchy> mDraweeHolder;
    private PipelineDraweeControllerBuilder builder;
    private int overlapWidth = 20;
    private String TAG = "ThisThatView";
    private OnClickListenerForThisThat clickListener;

    public ThisThatView(Context context) {
        super(context);
        init();
    }

    public ThisThatView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ThisThatView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ThisThatView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        builder = Fresco.newDraweeControllerBuilder();
        //TODO: Add custom xml attributes
        GenericDraweeHierarchy gdh = new GenericDraweeHierarchyBuilder(getResources())
                .setRoundingParams(RoundingParams.asCircle())
                .setActualImageScaleType(ScalingUtils.ScaleType.FOCUS_CROP)
                .setPlaceholderImage(R.drawable.circle_blue)
                .setFadeDuration(200)
                .build();
        GenericDraweeHierarchy gdh2 = new GenericDraweeHierarchyBuilder(getResources())
                .setRoundingParams(RoundingParams.asCircle())
                .setActualImageScaleType(ScalingUtils.ScaleType.FOCUS_CROP)
                .setPlaceholderImage(R.drawable.circle_green)
                .setFadeDuration(200)
                .build();
        DraweeHolder<GenericDraweeHierarchy> thisDraweeHolder = DraweeHolder.create(gdh, getContext());
        DraweeHolder<GenericDraweeHierarchy> thatDraweeHolder = DraweeHolder.create(gdh2, getContext());
        //Updated these callbacks
        //Hopefully it will solve the fresco image issue.
        //Need to see where we can remove this callback if there is any need somewhere
        // When a holder is set to the view for the first time,
        // don't forget to set the callback to its top-level drawable:
        thisDraweeHolder.getTopLevelDrawable().setCallback(this);
        thatDraweeHolder.getTopLevelDrawable().setCallback(this);
        mDraweeHolder = new MultiDraweeHolder<>();
        mDraweeHolder.add(thisDraweeHolder);
        mDraweeHolder.add(thatDraweeHolder);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int midWidth;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            midWidth = (getMeasuredWidth() - getPaddingEnd() - getPaddingStart()) / 2;
        } else {
            midWidth = (getMeasuredWidth() - getPaddingRight() - getPaddingLeft()) / 2;
        }
        int height = getMeasuredHeight() - getPaddingTop() - getPaddingBottom();
        for (int i = 0; i < mDraweeHolder.size(); i++) {
            Drawable drawable = mDraweeHolder.get(i).getTopLevelDrawable();
            int shift;
            //Checking if drawee should go to left or right according to odd-even position
            if ((i + 1) % 2 == 0) {
                shift = -overlapWidth + (i / 2) * height;
                drawable.setBounds(midWidth + shift, getPaddingTop(), midWidth + shift + height, height + getPaddingTop());
            } else {
                shift = overlapWidth - (i / 2) * height;
                drawable.setBounds(midWidth + shift - height, getPaddingTop(), midWidth + shift, height + getPaddingTop());
            }
            drawable.draw(canvas);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        //Get the width measurement
        int widthSize = View.resolveSize(desiredWidth, widthMeasureSpec);

        //Get the height measurement
        int heightSize = View.resolveSize(desiredHeight, heightMeasureSpec);

        //MUST call this to store the measurements
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected boolean verifyDrawable(@NonNull Drawable who) {
        return mDraweeHolder.verifyDrawable(who) || super.verifyDrawable(who);
    }

    @Override
    public void invalidateDrawable(@NonNull Drawable drawable) {
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (clickListener != null)
            return gestureDetector.onTouchEvent(event);
        else
            return mDraweeHolder.onTouchEvent(event) || super.onTouchEvent(event);
    }

    public void setClickListenerForThisThat(OnClickListenerForThisThat listener) {
        clickListener = listener;
        DraweeGestureDetector cgestureDetector = new DraweeGestureDetector();
        gestureDetector = new GestureDetector(getContext(), cgestureDetector);
    }

    public void setImageToAllImages(List<String> links) throws Exception {
        if (mDraweeHolder.size() != links.size()) {
            throw new Exception("Size of links and drawables does not match");
        }
        for (int i = 0; i < links.size(); i++) {
            setImage(i, links.get(i));
        }
    }

    public void setImageToAllImages(String[] links) throws Exception {
        if (mDraweeHolder.size() != links.length) {
            throw new Exception("Size of links and drawables does not match");
        }
        for (int i = 0; i < links.length; i++) {
            setImage(i, links[i]);
        }
    }

    public void setImage(int position, String link) {
        builder.reset();
        builder.setUri(link)
                .setOldController(mDraweeHolder.get(position).getController())
                .setContentDescription("Vizo Image " + position);
        mDraweeHolder.get(position).setController(builder.build());
    }

    public void setImage(int position, Uri uri) {
        builder.reset();
        builder.setUri(uri)
                .setOldController(mDraweeHolder.get(position).getController())
                .setContentDescription("Vizo Image " + position);
        mDraweeHolder.get(position).setController(builder.build());
    }

    public int getOverlapWidth() {
        return overlapWidth;
    }

    public void setOverlapWidth(int overlapWidth) {
        this.overlapWidth = overlapWidth;
    }

    public Drawable getDrawableAtPosition(int position) {
        return mDraweeHolder.get(position).getTopLevelDrawable();
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mDraweeHolder.onDetach();
    }

    @Override
    public void onStartTemporaryDetach() {
        super.onStartTemporaryDetach();
        mDraweeHolder.onDetach();
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        mDraweeHolder.onAttach();
    }

    @Override
    public void onFinishTemporaryDetach() {
        super.onFinishTemporaryDetach();
        mDraweeHolder.onAttach();
    }

    public interface OnClickListenerForThisThat {
        void onThisClicked();

        void onThatClicked();
    }

    private class DraweeGestureDetector extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mDraweeHolder.get(1).getTopLevelDrawable().getBounds().contains((int) e.getX(), (int) e.getY())) {
                clickListener.onThatClicked();
                return true;
            } else if (mDraweeHolder.get(0).getTopLevelDrawable().getBounds().contains((int) e.getX(), (int) e.getY())) {
                clickListener.onThisClicked();
                return true;
            }
            return false;
        }

    }

}
