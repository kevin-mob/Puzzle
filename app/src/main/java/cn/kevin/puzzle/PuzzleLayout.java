package cn.kevin.puzzle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import cn.kevin.puzzle.model.Block;

/**
 * 创建日期：2017/10/23.
 *
 * @author kevin
 */

public class PuzzleLayout extends RelativeLayout {
    private ViewDragHelper viewDragHelper;
    private static final String TAG = PuzzleLayout.class.getSimpleName();
    private DataHelper mHelper;
    private int mDrawableId;
    private int mSquareRootNum;
    private int mHeight;
    private int mWidth;
    private int mItemWidth;
    private int mItemHeight;
    private OnCompleteCallback mOnCompleteCallback;

    public PuzzleLayout(Context context) {
        super(context);
        init();
    }


    public PuzzleLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PuzzleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {

        getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mHeight = getHeight();
                mWidth = getWidth();
                getViewTreeObserver().removeOnPreDrawListener(this);
                if(mDrawableId != 0 && mSquareRootNum != 0){
                    createChildren();
                }
                return false;
            }
        });
        mHelper = new DataHelper();

        viewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(View child, int pointerId) {
                int index = indexOfChild(child);
                return mHelper.getScrollDirection(index) != DataHelper.N;
            }

            @Override
            public int clampViewPositionHorizontal(View child, int left, int dx) {

                int index = indexOfChild(child);
                int position = mHelper.getModel(index).position;
                int selfLeft = (position % mSquareRootNum) * mItemWidth;
                int leftEdge = selfLeft - mItemWidth;
                int rightEdge = selfLeft + mItemWidth;
                int direction = mHelper.getScrollDirection(index);
                //Log.d(TAG, "left " + left + " index" + index + " dx " + dx + " direction " + direction);
                switch (direction){
                    case DataHelper.L:
                        if(left <= leftEdge)
                            return leftEdge;
                        else if(left >= selfLeft)
                            return selfLeft;
                        else
                            return left;

                    case DataHelper.R:
                        if(left >= rightEdge)
                            return rightEdge;
                        else if (left <= selfLeft)
                            return selfLeft;
                        else
                            return left;
                    default:
                        return selfLeft;
                }
            }

            @Override
            public int clampViewPositionVertical(View child, int top, int dy) {
                int index = indexOfChild(child);
                Block model = mHelper.getModel(index);
                int position = model.position;

                int selfTop = (position / mSquareRootNum) * mItemHeight;
                int topEdge = selfTop - mItemHeight;
                int bottomEdge = selfTop + mItemHeight;
                int direction = mHelper.getScrollDirection(index);
                //Log.d(TAG, "top " + top + " index " + index + " direction " + direction);
                switch (direction){
                    case DataHelper.T:
                        if(top <= topEdge)
                            return topEdge;
                        else if (top >= selfTop)
                            return selfTop;
                        else
                            return top;
                    case DataHelper.B:
                        if(top >= bottomEdge)
                            return bottomEdge;
                        else if (top <= selfTop)
                            return selfTop;
                        else
                            return top;
                    default:
                        return selfTop;
                }
            }

            @Override
            public void onViewReleased(View releasedChild, float xvel, float yvel) {
                Log.d(TAG, "xvel " + xvel + " yvel " + yvel);
                int index = indexOfChild(releasedChild);
                boolean isCompleted = mHelper.swapValueWithWhite(index);
                Block item =  mHelper.getModel(index);
                viewDragHelper.settleCapturedViewAt(item.hPosition * mItemWidth, item.vPosition * mItemHeight);
                View whiteView = getChildAt(0);
                ViewGroup.LayoutParams layoutParams = whiteView.getLayoutParams();
                whiteView.setLayoutParams(releasedChild.getLayoutParams());
                releasedChild.setLayoutParams(layoutParams);
                invalidate();
                if(isCompleted){
                    whiteView.setVisibility(VISIBLE);
                    mOnCompleteCallback.onComplete();
                }
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event){
        return viewDragHelper.shouldInterceptTouchEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        viewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll()
    {
        if(viewDragHelper.continueSettling(true))
        {
            invalidate();
        }
    }

    public void setImage(int drawableId, int squareRootNum){
        this.mSquareRootNum = squareRootNum;
        this.mDrawableId = drawableId;
        if(mWidth != 0 && mHeight != 0){
            createChildren();
        }
    }

    /**
     *  将子View index与mHelper中models的index一一对应，
     *  每次在交换子View位置的时候model同步更新currentPosition。
     */
    private void createChildren(){
        removeAllViews();
        mHelper.setSquareRootNum(mSquareRootNum);

        DisplayMetrics dm = getResources().getDisplayMetrics();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDensity = dm.densityDpi;

        Bitmap resource = BitmapFactory.decodeResource(getResources(), mDrawableId, options);
        Bitmap bitmap = BitmapUtil.zoomImg(resource, mWidth, mHeight);
        resource.recycle();

        mItemWidth = mWidth / mSquareRootNum;

        mItemHeight = mHeight / mSquareRootNum;


        for (int i = 0; i < mSquareRootNum; i++){
            for (int j = 0; j < mSquareRootNum; j++){
                Log.d(TAG, "mItemWidth * x " + (mItemWidth * i));
                Log.d(TAG, "mItemWidth * y " + (mItemWidth * j));
                ImageView iv = new ImageView(getContext());
                iv.setScaleType(ImageView.ScaleType.FIT_XY);
                LayoutParams lp = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.leftMargin = j * mItemWidth;
                lp.topMargin = i * mItemHeight;
                iv.setLayoutParams(lp);
                Bitmap b = Bitmap.createBitmap(bitmap, lp.leftMargin, lp.topMargin, mItemWidth, mItemHeight);
                iv.setImageBitmap(b);
                addView(iv);
            }
        }
        randomOrder();
    }

    public void randomOrder(){
        int num = mSquareRootNum * mSquareRootNum * 8;
        View whiteView = getChildAt(0);
        View neighbor;
        for (int i = 0; i < num; i ++){
            int neighborPosition = mHelper.findNeighborIndexOfWhite();
            ViewGroup.LayoutParams whiteLp = whiteView.getLayoutParams();
            neighbor = getChildAt(neighborPosition);
            whiteView.setLayoutParams(neighbor.getLayoutParams());
            neighbor.setLayoutParams(whiteLp);
            mHelper.swapValueWithWhite(neighborPosition);
        }
        whiteView.setVisibility(INVISIBLE);
    }

    public void setOnCompleteCallback(OnCompleteCallback onCompleteCallback){
        mOnCompleteCallback = onCompleteCallback;
    }

    public interface OnCompleteCallback{
        void onComplete();
    }
}
