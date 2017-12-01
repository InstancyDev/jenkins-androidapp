package com.instancy.instancylearning.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.instancy.instancylearning.R;

public class CustomFlowLayout extends ViewGroup {

    /**
     * The amount of space used by children in the left gutter.
     */
    private int mLeftWidth;

    /**
     * The amount of space used by children in the right gutter.
     */
    private int mRightWidth;

    /**
     * These are used for computing child frames based on their gravity.
     */
    private final Rect mTmpContainerRect = new Rect();
    private final Rect mTmpChildRect = new Rect();


    private int paddingHorizontal = 5, paddingVertical = 5;

    public CustomFlowLayout(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public CustomFlowLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
    }

    public CustomFlowLayout(Context context, AttributeSet attrs,
                            int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // TODO Auto-generated constructor stub
    }

    public CustomFlowLayout(Context context, AttributeSet attrs,
                            int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        // TODO Auto-generated constructor stub
    }

    /**
     * Any layout manager that doesn't scroll will want this.
     */
    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    /**
     * Ask all children to measure themselves and compute the measurement of
     * this layout based on the children.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // // super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //
        int count = getChildCount();
        //
        // // These keep track of the space we are using on the left and right
        // for
        // // views positioned there; we need member variables so we can also
        // use
        // // these for layout later.
        // mLeftWidth = 0;
        // mRightWidth = 0;
        //
        // // Measurement will ultimately be computing these values.
        // int maxHeight = 0;
        // int maxWidth = 0;
        // int childState = 0;
        //
        // // Iterate through all children, measuring them and computing our
        // // dimensions
        // // from their size.
        // for (int i = 0; i < count; i++) {
        // final View child = getChildAt(i);
        // if (child.getVisibility() != GONE) {
        // // Measure the child.
        // measureChildWithMargins(child, widthMeasureSpec, 0,
        // heightMeasureSpec, 0);
        //
        // // Update our size information based on the layout params.
        // // Children
        // // that asked to be positioned on the left or right go in those
        // // gutters.
        // final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        // if (lp.position == LayoutParams.POSITION_LEFT) {
        // mLeftWidth += Math.max(maxWidth, child.getMeasuredWidth()
        // + lp.leftMargin + lp.rightMargin);
        // } else if (lp.position == LayoutParams.POSITION_RIGHT) {
        // mRightWidth += Math.max(maxWidth, child.getMeasuredWidth()
        // + lp.leftMargin + lp.rightMargin);
        // } else {
        // maxWidth = Math.max(maxWidth, child.getMeasuredWidth()
        // + lp.leftMargin + lp.rightMargin);
        // }
        // maxHeight = Math.max(maxHeight, child.getMeasuredHeight()
        // + lp.topMargin + lp.bottomMargin);
        // childState = combineMeasuredStates(childState,
        // child.getMeasuredState());
        // }
        // }
        //
        // // Total width is the maximum width of all inner children plus the
        // // gutters.
        // maxWidth += mLeftWidth + mRightWidth;
        //
        // // Check against our minimum height and width
        // maxHeight = Math.max(maxHeight, getSuggestedMinimumHeight());
        // maxWidth = Math.max(maxWidth, getSuggestedMinimumWidth());
        //
        // // Report our final dimensions.
        // setMeasuredDimension(
        // resolveSizeAndState(maxWidth, widthMeasureSpec, childState),
        // resolveSizeAndState(maxHeight, heightMeasureSpec,
        // childState << MEASURED_HEIGHT_STATE_SHIFT));


//		int childLeft = getPaddingLeft();
//		int childTop = getPaddingTop();
//		int childRight = getPaddingRight();
//		int childBottom = getPaddingBottom();
//		
//		int lineHeight = 0;
//		// 100 is a dummy number, widthMeasureSpec should always be EXACTLY for
//		// FlowLayout
//		int myWidth = resolveSize(View.MeasureSpec.EXACTLY, widthMeasureSpec);
//		int wantedHeight = 0;
//		for (int i = 0; i < count; i++) {
//			final View child = getChildAt(i);
//			if (child.getVisibility() == View.GONE) {
//				continue;
//			}
//			// let the child measure itself
//			child.measure(
//					getChildMeasureSpec(widthMeasureSpec, childLeft
//							+ childRight, child.getLayoutParams().width),
//					getChildMeasureSpec(heightMeasureSpec, childTop
//							+ childBottom,
//							child.getLayoutParams().height));
//			
//			int childWidth = child.getMeasuredWidth();
//			int childHeight = child.getMeasuredHeight();
//			// lineHeight is the height of current line, should be the height of
//			// the highest view
//			lineHeight = Math.max(childHeight, lineHeight);
//			if (childWidth + childLeft + childRight > myWidth) {
//				// wrap this line
//				childLeft = getPaddingLeft();
//				childTop += paddingVertical + lineHeight;
//				lineHeight = childHeight;
//			}
//			childLeft += childWidth + paddingHorizontal;
//		}
//		wantedHeight += childTop + lineHeight + childBottom;
//		setMeasuredDimension(myWidth,
//				resolveSize(wantedHeight, heightMeasureSpec));


        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int lineHeight = 0;
        // 100 is a dummy number, widthMeasureSpec should always be EXACTLY for FlowLayout
        int myWidth = resolveSize(100, widthMeasureSpec);
        int wantedHeight = 0;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            // let the child measure itself
            child.measure(
                    getChildMeasureSpec(widthMeasureSpec, getPaddingLeft() + getPaddingRight(),
                            child.getLayoutParams().width),
                    getChildMeasureSpec(heightMeasureSpec, getPaddingTop() + getPaddingBottom(),
                            child.getLayoutParams().height));
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            // lineheight is the height of current line, should be the height of the heightest view
            lineHeight = Math.max(childHeight, lineHeight);
            if (childWidth + childLeft + getPaddingRight() > myWidth) {
                // wrap this line
                childLeft = getPaddingLeft();
                childTop += paddingVertical + lineHeight;
                lineHeight = childHeight;
            }
            childLeft += childWidth + paddingHorizontal;
        }
        wantedHeight += childTop + lineHeight + getPaddingBottom();
        setMeasuredDimension(myWidth, resolveSize(wantedHeight, heightMeasureSpec));
    }

    /**
     * Position all children within this layout.
     */
    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        int count = getChildCount();
        //
        // // These are the far left and right edges in which we are performing
        // // layout.
        // int leftPos = getPaddingLeft();
        // int rightPos = right - left - getPaddingRight();
        //
        // // This is the middle region inside of the gutter.
        // final int middleLeft = leftPos + mLeftWidth;
        // final int middleRight = rightPos - mRightWidth;
        //
        // // These are the top and bottom edges in which we are performing
        // layout.
        // final int parentTop = getPaddingTop();
        // final int parentBottom = bottom - top - getPaddingBottom();
        //
        // for (int i = 0; i < count; i++) {
        // final View child = getChildAt(i);
        // if (child.getVisibility() != GONE) {
        // final LayoutParams lp = (LayoutParams) child.getLayoutParams();
        //
        // final int width = child.getMeasuredWidth();
        // final int height = child.getMeasuredHeight();
        //
        // // Compute the frame in which we are placing this child.
        // if (lp.position == LayoutParams.POSITION_LEFT) {
        // mTmpContainerRect.left = leftPos + lp.leftMargin;
        // mTmpContainerRect.right = leftPos + width + lp.rightMargin;
        // leftPos = mTmpContainerRect.right;
        // } else if (lp.position == LayoutParams.POSITION_RIGHT) {
        // mTmpContainerRect.right = rightPos - lp.rightMargin;
        // mTmpContainerRect.left = rightPos - width - lp.leftMargin;
        // rightPos = mTmpContainerRect.left;
        // } else {
        // mTmpContainerRect.left = middleLeft + lp.leftMargin;
        // mTmpContainerRect.right = middleRight - lp.rightMargin;
        // }
        // mTmpContainerRect.top = parentTop + lp.topMargin;
        // mTmpContainerRect.bottom = parentBottom - lp.bottomMargin;
        //
        // // Use the child's gravity and size to determine its final
        // // frame within its container.
        // Gravity.apply(lp.gravity, width, height, mTmpContainerRect,
        // mTmpChildRect);
        //
        // // Place the child.
        // child.layout(mTmpChildRect.left, mTmpChildRect.top,
        // mTmpChildRect.right, mTmpChildRect.bottom);
        // }
        // }


//		int childLeft = getPaddingLeft();
//		int childTop = getPaddingTop();
//		int childRight = getPaddingRight();
//		int lineHeight = 0;
//		int myWidth = right - left;
//		for (int i = 0; i < count; i++) {
//			final View child = getChildAt(i);
//			if (child.getVisibility() == View.GONE) {
//				continue;
//			}
//			int childWidth = child.getMeasuredWidth();
//			int childHeight = child.getMeasuredHeight();
//			lineHeight = Math.max(childHeight, lineHeight);
//			if (childWidth + childLeft + childRight > myWidth) {
//				childLeft = getPaddingLeft();
//				childTop += paddingVertical + lineHeight;
//				lineHeight = childHeight;
//			}
//			child.layout(childLeft, childTop, childLeft + childWidth, childTop
//					+ childHeight);
//			childLeft += childWidth + paddingHorizontal;
//		}


        int childLeft = getPaddingLeft();
        int childTop = getPaddingTop();
        int lineHeight = 0;
        int myWidth = right - left;
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() == View.GONE) {
                continue;
            }
            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();
            lineHeight = Math.max(childHeight, lineHeight);
            if (childWidth + childLeft + getPaddingRight() > myWidth) {
                childLeft = getPaddingLeft();
                childTop += paddingVertical + lineHeight;
                lineHeight = childHeight;
            }
            child.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);
            childLeft += childWidth + paddingHorizontal;
        }
    }
    // ----------------------------------------------------------------------
    // The rest of the implementation is for custom per-child layout parameters.
    // If you do not need these (for example you are writing a layout manager
    // that does fixed positioning of its children), you can drop all of this.

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new CustomFlowLayout.LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
    }

    @Override
    protected ViewGroup.LayoutParams generateLayoutParams(
            ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    /**
     * Custom per-child layout information.
     */
    public static class LayoutParams extends MarginLayoutParams {
        /**
         * The gravity to apply with the View to which these layout parameters
         * are associated.
         */
        public int gravity = Gravity.TOP | Gravity.START;

        public static int POSITION_MIDDLE = 0;
        public static int POSITION_LEFT = 1;
        public static int POSITION_RIGHT = 2;

        public int position = POSITION_MIDDLE;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            // Pull the layout param values from the layout XML during
            // inflation. This is not needed if you don't care about
            // changing the layout behavior in XML.
            TypedArray a = c.obtainStyledAttributes(attrs,
                    R.styleable.CustomFlowLayoutLP);
            gravity = a.getInt(
                    R.styleable.CustomFlowLayoutLP_android_layout_gravity,
                    gravity);
            position = a.getInt(R.styleable.CustomFlowLayoutLP_layout_position,
                    position);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
    }

}
