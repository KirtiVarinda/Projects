package com.remindme;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListAdapter;
import android.widget.ListView;


import java.util.List;

/**
 * ListView subclass that provides the swipe functionality
 */
public class SwipeListView extends ListView {
    static boolean overScrollCheck = false;
    boolean isstretch = false;
    int slop;
    float touchDownX, touchUpX, touchUpY, deltaY, deltaX;
    boolean isAnimatin = false;
    float lastScroll = 0.0F;
    private long mLastTime;
    float mLastTouchX;
    float mScrollX = 0.0F;
    float moveY;
    float touchDownY = -1.0F;
    float f1;
    float f;
    private static final int MAX_Y_OVERSCROLL_DISTANCE = 110;
    private Context mContext;
    private int mMaxYOverscrollDistance;
    /**
     * Used when user want change swipe list mode on some rows
     */
    public final static int SWIPE_MODE_DEFAULT = -1;

    /**
     * Disables all swipes
     */
    public final static int SWIPE_MODE_NONE = 0;

    /**
     * Enables both left and right swipe
     */
    public final static int SWIPE_MODE_BOTH = 1;
    /**
     * Enables right swipe
     */
    public final static int SWIPE_MODE_RIGHT = 2;
    /**
     * Enables left swipe
     */
    public final static int SWIPE_MODE_LEFT = 3;

    /**
     * Binds the swipe gesture to reveal a view behind the row (Drawer style)
     */
    public final static int SWIPE_ACTION_REVEAL = 0;
    /**
     * Dismisses the cell when swiped over
     */
    public final static int SWIPE_ACTION_DISMISS = 1;

    /**
     * Marks the cell as checked when swiped and release
     */
    public final static int SWIPE_ACTION_CHOICE = 2;

    /**
     * No action when swiped
     */
    public final static int SWIPE_ACTION_NONE = 3;

    /**
     * Default id for front view
     */
    public final static String SWIPE_DEFAULT_FRONT_VIEW = "swipelist_frontview";

    /**
     * Default id for back view
     */
    public final static String SWIPE_DEFAULT_BACK_VIEW = "swipelist_backview";

    /**
     * Indicates no movement
     */
    private final static int TOUCH_STATE_REST = 0;

    /**
     * State scrolling x position
     */
    private final static int TOUCH_STATE_SCROLLING_X = 1;

    /**
     * State scrolling y position
     */
    private final static int TOUCH_STATE_SCROLLING_Y = 2;

    private int touchState = TOUCH_STATE_REST;

    private float lastMotionX;
    private float lastMotionY;
    private int touchSlop;
    public static int width;
    public static int height;
    int swipeFrontView = 0;
    int swipeBackView = 0;

    /**
     * Internal listener for common swipe events
     */
    private SwipeListViewListener swipeListViewListener;

    /**
     * Internal touch listener
     */
    private SwipeListViewTouchListener touchListener;


    /**
     * If you create a View programmatically you need send back and front identifier
     *
     * @param context        Context
     * @param swipeBackView  Back Identifier
     * @param swipeFrontView Front Identifier
     */
    public SwipeListView(Context context, int swipeBackView, int swipeFrontView) {
        super(context);
        mScrollX = 0.0F;
        touchDownY = -1F;
        lastScroll = 0.0F;
        isAnimatin = false;
        this.swipeFrontView = swipeFrontView;
        this.swipeBackView = swipeBackView;
        mContext = context;
        width = getWidth();
        height = getHeight();
        setOverScrollMode(ListView.OVER_SCROLL_ALWAYS);
        Drawable drawable = this.getResources().getDrawable(R.drawable.overscroll);
        setOverscrollHeader(drawable);
        init(null);
    }

    public SwipeListView(Context context) {
        super(context);

    }

    /**
     * @see ListView#ListView(Context, AttributeSet)
     */
    public SwipeListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(attrs);

    }

    /**
     * @see ListView#ListView(Context, AttributeSet, int)
     */
    public SwipeListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mScrollX = 0.0F;
        touchDownY = -1F;
        lastScroll = 0.0F;
        isAnimatin = false;
        Drawable drawable = this.getResources().getDrawable(R.drawable.overscroll);
        setOverScrollMode(ListView.OVER_SCROLL_ALWAYS);
        setOverscrollHeader(drawable);
        setLayerType(2, null);

        mContext = context;
        init(attrs);

    }

    private boolean isInLandscape() {
        return getWidth() > getHeight();
    }

    /**
     * Init ListView
     *
     * @param attrs AttributeSet
     */
    private void init(AttributeSet attrs) {

        int swipeMode = SWIPE_MODE_BOTH;
        boolean swipeOpenOnLongPress = true;
        boolean swipeCloseAllItemsWhenMoveList = true;
        long swipeAnimationTime = 0;
        float swipeOffsetLeft = 0;
        float swipeOffsetRight = 0;
        int swipeDrawableChecked = 0;
        int swipeDrawableUnchecked = 0;

        int swipeActionLeft = SWIPE_ACTION_REVEAL;
        int swipeActionRight = SWIPE_ACTION_REVEAL;

        if (attrs != null) {
            TypedArray styled = getContext().obtainStyledAttributes(attrs, R.styleable.SwipeListView);
            swipeMode = styled.getInt(R.styleable.SwipeListView_swipeMode, SWIPE_MODE_BOTH);
            swipeActionLeft = styled.getInt(R.styleable.SwipeListView_swipeActionLeft, SWIPE_ACTION_REVEAL);
            swipeActionRight = styled.getInt(R.styleable.SwipeListView_swipeActionRight, SWIPE_ACTION_REVEAL);
            swipeOffsetLeft = styled.getDimension(R.styleable.SwipeListView_swipeOffsetLeft, 0);
            swipeOffsetRight = styled.getDimension(R.styleable.SwipeListView_swipeOffsetRight, 0);
            swipeOpenOnLongPress = styled.getBoolean(R.styleable.SwipeListView_swipeOpenOnLongPress, true);
            swipeAnimationTime = styled.getInteger(R.styleable.SwipeListView_swipeAnimationTime, 0);
            swipeCloseAllItemsWhenMoveList = styled.getBoolean(R.styleable.SwipeListView_swipeCloseAllItemsWhenMoveList, true);
            swipeDrawableChecked = styled.getResourceId(R.styleable.SwipeListView_swipeDrawableChecked, 0);
            swipeDrawableUnchecked = styled.getResourceId(R.styleable.SwipeListView_swipeDrawableUnchecked, 0);
            swipeFrontView = styled.getResourceId(R.styleable.SwipeListView_swipeFrontView, 0);
            swipeBackView = styled.getResourceId(R.styleable.SwipeListView_swipeBackView, 0);
        }

        if (swipeFrontView == 0 || swipeBackView == 0) {
            swipeFrontView = getContext().getResources().getIdentifier(SWIPE_DEFAULT_FRONT_VIEW, "id", getContext().getPackageName());
            swipeBackView = getContext().getResources().getIdentifier(SWIPE_DEFAULT_BACK_VIEW, "id", getContext().getPackageName());

            if (swipeFrontView == 0 || swipeBackView == 0) {
                throw new RuntimeException(String.format("You forgot the attributes swipeFrontView or swipeBackView. You can add this attributes or use '%s' and '%s' identifiers", SWIPE_DEFAULT_FRONT_VIEW, SWIPE_DEFAULT_BACK_VIEW));
            }
        }

        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        touchSlop = ViewConfigurationCompat.getScaledPagingTouchSlop(configuration);
        touchListener = new SwipeListViewTouchListener(this, swipeFrontView, swipeBackView);
        if (swipeAnimationTime > 0) {
            touchListener.setAnimationTime(swipeAnimationTime);
        }
        touchListener.setRightOffset(swipeOffsetRight);
        touchListener.setLeftOffset(swipeOffsetLeft);
        touchListener.setSwipeActionLeft(swipeActionLeft);
        touchListener.setSwipeActionRight(swipeActionRight);
        touchListener.setSwipeMode(swipeMode);
        touchListener.setSwipeClosesAllItemsWhenListMoves(swipeCloseAllItemsWhenMoveList);
        touchListener.setSwipeOpenOnLongPress(swipeOpenOnLongPress);
        touchListener.setSwipeDrawableChecked(swipeDrawableChecked);
        touchListener.setSwipeDrawableUnchecked(swipeDrawableUnchecked);
        setOnTouchListener(touchListener);
        // setOnScrollListener(touchListener.makeScrollListener());

        final DisplayMetrics metrics = mContext.getResources()
                .getDisplayMetrics();
        final float density = metrics.density;

        mMaxYOverscrollDistance = (int) (density * MAX_Y_OVERSCROLL_DISTANCE);
    }

    /**
     * Recycle cell. This method should be called from getView in Adapter when use SWIPE_ACTION_CHOICE
     *
     * @param convertView parent view
     * @param position    position in list
     */
    public void recycle(View convertView, int position) {
        touchListener.reloadChoiceStateInView(convertView.findViewById(swipeFrontView), position);
    }

    /**
     * Get if item is selected
     *
     * @param position position in list
     * @return
     */
    public boolean isChecked(int position) {
        return touchListener.isChecked(position);
    }

    /**
     * Get positions selected
     *
     * @return
     */
    public List<Integer> getPositionsSelected() {
        return touchListener.getPositionsSelected();
    }

    /**
     * Count selected
     *
     * @return
     */
    public int getCountSelected() {
        return touchListener.getCountSelected();
    }

    /**
     * Unselected choice state in item
     */
    public void unselectedChoiceStates() {
        touchListener.unselectedChoiceStates();
    }

    /**
     * @see ListView#setAdapter(ListAdapter)
     */
    @Override
    public void setAdapter(ListAdapter adapter) {
        super.setAdapter(adapter);
        touchListener.resetItems();
                /* adapter.registerDataSetObserver(new DataSetObserver() {
		@Override
		public void onChanged() {
		super.onChanged();
		onListChanged();
		touchListener.resetItems();
		}
		});*/
    }

    public float getOffset1() {
        return computeVerticalScrollOffset();
    }

    public float getOffset2() {
        return computeVerticalScrollRange() - computeVerticalScrollExtent();
    }

    @Override
    protected void onOverScrolled(int i, int j, boolean flag, boolean flag1) {

        super.onOverScrolled(i, j, flag, flag1);
        // Log.v("On OverScroll", "scrollX:" + scrollX + " scrollY:" + scrollY + " clampedX:"
        // + clampedX + " clampedY:" + clampedX);
        overScrollCheck = true;


    }
 

	/*protected void onScrollChanged (int i, int j, int k, int l)
	{


		float f1;
		float f2;
		float f3;
		super.onScrollChanged(i, j, k, l);
		if(getScaleX() != 1.0F && !isAnimatin)
			setScaleX(1.0F);
		if(getScaleY() != 1.0F && !isAnimatin)
			setScaleY(1.0F);
		long l1 = SystemClock.uptimeMillis();
		long l2 = l1 - mLastTime;
		float f;
		AnimatorSet animatorset1;
		float af2[];
		ObjectAnimator objectanimator2;
		float af3[];
		ObjectAnimator objectanimator3;
		if((float)mLastTime > 0.0F)
			f = (float)l2 / 1000F;
		else
			f = 0.0F;
		f1 = Math.min(0.02F, f);
		mLastTime = l1;
		f2 = computeVerticalScrollOffset();
		moveY = f2 - lastScroll;
		f3 = computeVerticalScrollRange() - computeVerticalScrollExtent();
		if(f2 >= 60F || moveY >= -20F || isAnimatin || touchDownY != -1F){
			if(f2 > f3 - 60F && moveY > 0.0F && !isAnimatin && touchDownY == -1F)
			{
				moveY = -Math.min(60F, moveY);
				setPivotY(0.0F);
				setPivotX(getWidth() / 2);
				AnimatorSet animatorset = new AnimatorSet();
				isAnimatin = true;
				float af[] = new float[2];
				af[0] = 1.0F;
				af[1] = 1.0F - f1 * (0.1F * -moveY);
				ObjectAnimator objectanimator = ObjectAnimator.ofFloat(this, "scaleY", af).setDuration((long)(f1 * (300F * -moveY)));
				objectanimator.setInterpolator(new DecelerateInterpolator(2.0F));
				float af1[] = new float[2];
				af1[0] = 1.0F - f1 * (0.1F * -moveY);
				af1[1] = 1.0F;
				ObjectAnimator objectanimator1 = ObjectAnimator.ofFloat(this, "scaleY", af1);
				objectanimator1.setStartDelay((long)(f1 * (100F * -moveY)));
				objectanimator1.setInterpolator(new DecelerateInterpolator(3F));
				objectanimator1.setDuration((long)(f1 * (-moveY * (1.8F * 300F)))).start();
				animatorset.playTogether(new Animator[] {
						objectanimator
				});
				objectanimator1.addListener(new android.animation.Animator.AnimatorListener() {

					public void onAnimationCancel(Animator animator)
					{
					}

					public void onAnimationEnd(Animator animator)
					{
						isAnimatin = false;
					}

					public void onAnimationRepeat(Animator animator)
					{
					}

					public void onAnimationStart(Animator animator)
					{
					}





				}
						);
				animatorset.start();
			}
		}
		else {
			moveY = Math.max(-70F, moveY);
			setPivotY(getHeight());
			setPivotX(getWidth() / 2);
			animatorset1 = new AnimatorSet();
			isAnimatin = true;
			af2 = new float[2];
			af2[0] = 1.0F;
			af2[1] = 1.0F - f1 * (0.1F * -moveY);
			objectanimator2 = ObjectAnimator.ofFloat(this, "scaleY", af2).setDuration((long)(f1 * (300F * -moveY)));
			objectanimator2.setInterpolator(new DecelerateInterpolator(2.0F));
			af3 = new float[2];
			af3[0] = 1.0F - f1 * (0.1F * -moveY);
			af3[1] = 1.0F;
			objectanimator3 = ObjectAnimator.ofFloat(this, "scaleY", af3);
			objectanimator3.setStartDelay((long)(f1 * (100F * -moveY)));
			objectanimator3.setInterpolator(new DecelerateInterpolator(3F));
			objectanimator3.setDuration((long)(f1 * (-moveY * (1.8F * 300F)))).start();
			animatorset1.playTogether(new Animator[] {
					objectanimator2
			});
			objectanimator3.addListener(new android.animation.Animator.AnimatorListener() {

				public void onAnimationCancel(Animator animator)
				{
				}

				public void onAnimationEnd(Animator animator)
				{
					isAnimatin = false;
				}

				public void onAnimationRepeat(Animator animator)
				{
				}

				public void onAnimationStart(Animator animator)
				{
				}


			}
					);
			animatorset1.start();
		}

		if(true){
			lastScroll = computeVerticalScrollOffset();
			return;
		}


	}*/

    /**
     * Dismiss item
     *
     * @param position Position that you want open
     */
    public void dismiss(int position) {
        int height = touchListener.dismiss(position);
        if (height > 0) {
            touchListener.handlerPendingDismisses(height);
        } else {
            int[] dismissPositions = new int[1];
            dismissPositions[0] = position;
            onDismiss(dismissPositions);
            touchListener.resetPendingDismisses();
        }
        closeOpenedItems();
    }

    /**
     * Dismiss items selected
     */
    public void dismissSelected() {
        List<Integer> list = touchListener.getPositionsSelected();
        int[] dismissPositions = new int[list.size()];
        int height = 0;
        for (int i = 0; i < list.size(); i++) {
            int position = list.get(i);
            dismissPositions[i] = position;
            int auxHeight = touchListener.dismiss(position);
            if (auxHeight > 0) {
                height = auxHeight;
            }
        }
        if (height > 0) {
            touchListener.handlerPendingDismisses(height);
        } else {
            onDismiss(dismissPositions);
            touchListener.resetPendingDismisses();
        }
        touchListener.returnOldActions();
    }

    /**
     * Open ListView's item
     *
     * @param position Position that you want open
     */
    public void openAnimate(int position) {
        touchListener.openAnimate(position);
    }

    /**
     * Close ListView's item
     *
     * @param position Position that you want open
     */
    public void closeAnimate(int position) {
        touchListener.closeAnimate(position);
    }

    /**
     * Notifies onDismiss
     *
     * @param reverseSortedPositions All dismissed positions
     */
    protected void onDismiss(int[] reverseSortedPositions) {
        if (swipeListViewListener != null) {
            swipeListViewListener.onDismiss(reverseSortedPositions);
        }
    }

    /**
     * Start open item
     *
     * @param position list item
     * @param action   current action
     * @param right    to right
     */
    protected void onStartOpen(int position, int action, boolean right) {
        if (swipeListViewListener != null && position != ListView.INVALID_POSITION) {
            swipeListViewListener.onStartOpen(position, action, right);
        }
    }


    protected void onDone(int position) {
        swipeListViewListener.onDone(position);
    }

    /**
     * Start close item
     *
     * @param position list item
     * @param right
     */
    protected void onStartClose(int position, boolean right) {
        if (swipeListViewListener != null && position != ListView.INVALID_POSITION) {
            swipeListViewListener.onStartClose(position, right);
        }
    }

    /**
     * Notifies onClickFrontView
     *
     * @param position item clicked
     */
    protected void onClickFrontView(int position) {
        if (swipeListViewListener != null && position != ListView.INVALID_POSITION) {
            swipeListViewListener.onClickFrontView(position);
        }
    }

    /**
     * Notifies onClickBackView
     *
     * @param position back item clicked
     */
    protected void onClickBackView(int position) {
        if (swipeListViewListener != null && position != ListView.INVALID_POSITION) {
            swipeListViewListener.onClickBackView(position);
        }
    }

    /**
     * Notifies onOpened
     *
     * @param position Item opened
     * @param toRight  If should be opened toward the right
     */
    protected void onOpened(int position, boolean toRight) {
        if (swipeListViewListener != null && position != ListView.INVALID_POSITION) {
            swipeListViewListener.onOpened(position, toRight);
        }
    }

    /**
     * Notifies onClosed
     *
     * @param position  Item closed
     * @param fromRight If open from right
     */
    protected void onClosed(int position, boolean fromRight) {
        if (swipeListViewListener != null && position != ListView.INVALID_POSITION) {
            swipeListViewListener.onClosed(position, fromRight);
        }
    }

    /**
     * Notifies onChoiceChanged
     *
     * @param position position that choice
     * @param selected if item is selected or not
     */
    protected void onChoiceChanged(int position, boolean selected) {
        if (swipeListViewListener != null && position != ListView.INVALID_POSITION) {
            swipeListViewListener.onChoiceChanged(position, selected);
        }
    }

    /**
     * User start choice items
     */
    protected void onChoiceStarted() {
        if (swipeListViewListener != null) {
            swipeListViewListener.onChoiceStarted();
        }
    }

    /**
     * User end choice items
     */
    protected void onChoiceEnded() {
        if (swipeListViewListener != null) {
            swipeListViewListener.onChoiceEnded();
        }
    }

    /**
     * User is in first item of list
     */
    protected void onFirstListItem() {
        if (swipeListViewListener != null) {
            swipeListViewListener.onFirstListItem();
        }
    }

    /**
     * User is in last item of list
     */
    protected void onLastListItem() {
        if (swipeListViewListener != null) {
            swipeListViewListener.onLastListItem();
        }
    }

    /**
     * Notifies onListChanged
     */
    protected void onListChanged() {
        if (swipeListViewListener != null) {
            swipeListViewListener.onListChanged();
        }
    }

    /**
     * Notifies onMove
     *
     * @param position Item moving
     * @param x        Current position
     */
    protected void onMove(int position, float x) {
        if (swipeListViewListener != null && position != ListView.INVALID_POSITION) {
            swipeListViewListener.onMove(position, x);
        }
    }

    protected int changeSwipeMode(int position) {
        if (swipeListViewListener != null && position != ListView.INVALID_POSITION) {
            return swipeListViewListener.onChangeSwipeMode(position);
        }
        return SWIPE_MODE_DEFAULT;
    }

    /**
     * Sets the Listener
     *
     * @param swipeListViewListener Listener
     */
    public void setSwipeListViewListener(SwipeListViewListener swipeListViewListener) {
        this.swipeListViewListener = swipeListViewListener;
    }

    /**
     * Resets scrolling
     */
    public void resetScrolling() {
        touchState = TOUCH_STATE_REST;
    }

    /**
     * Set offset on right
     *
     * @param offsetRight Offset
     */
    public void setOffsetRight(float offsetRight) {
        touchListener.setRightOffset(offsetRight);
    }

    /**
     * Set offset on left
     *
     * @param offsetLeft Offset
     */
    public void setOffsetLeft(float offsetLeft) {
        touchListener.setLeftOffset(offsetLeft);
    }

    /**
     * Set if all items opened will be closed when the user moves the ListView
     *
     * @param swipeCloseAllItemsWhenMoveList
     */
    public void setSwipeCloseAllItemsWhenMoveList(boolean swipeCloseAllItemsWhenMoveList) {
        touchListener.setSwipeClosesAllItemsWhenListMoves(swipeCloseAllItemsWhenMoveList);
    }

    /**
     * Sets if the user can open an item with long pressing on cell
     *
     * @param swipeOpenOnLongPress
     */
    public void setSwipeOpenOnLongPress(boolean swipeOpenOnLongPress) {
        touchListener.setSwipeOpenOnLongPress(swipeOpenOnLongPress);
    }

    /**
     * Set swipe mode
     *
     * @param swipeMode
     */
    public void setSwipeMode(int swipeMode) {
        touchListener.setSwipeMode(swipeMode);
    }

    /**
     * Return action on left
     *
     * @return Action
     */
    public int getSwipeActionLeft() {
        return touchListener.getSwipeActionLeft();
    }

    /**
     * Set action on left
     *
     * @param swipeActionLeft Action
     */
    public void setSwipeActionLeft(int swipeActionLeft) {
        touchListener.setSwipeActionLeft(swipeActionLeft);
    }

    /**
     * Return action on right
     *
     * @return Action
     */
    public int getSwipeActionRight() {
        return touchListener.getSwipeActionRight();
    }

    /**
     * Set action on right
     *
     * @param swipeActionRight Action
     */
    public void setSwipeActionRight(int swipeActionRight) {
        touchListener.setSwipeActionRight(swipeActionRight);
    }

    /**
     * Sets animation time when user drops cell
     *
     * @param animationTime milliseconds
     */
    public void setAnimationTime(long animationTime) {
        touchListener.setAnimationTime(animationTime);
    }

    /**
     * @see ListView#onInterceptTouchEvent(MotionEvent)
     */
    void createAnimationForOffset(float f, float f1, float f2) {
        setPivotX(getWidth() / 2);
        setSelected(false);
        float f3;
        if (isInLandscape())
            f3 = 0.0002F;
        else
            f3 = 0.0001F;
        if (f < 0.0F && f1 == 0.0F) {
            clearAnimation();
            setPivotY(getHeight());
            float af1[] = new float[1];
            af1[0] = 1.0F + f * (f3 * 2.0F);
            ObjectAnimator.ofFloat(this, "scaleY", af1).setDuration(0L).start();
        } else if (f > 0.0F && f1 == f2) {
            clearAnimation();
            setPivotY(0.0F);
            float af[] = new float[1];
            af[0] = 1.0F + -f * (2.0F * f3);
            ObjectAnimator.ofFloat(this, "scaleY", af).setDuration(0L).start();
            return;
        }
    }

    void createSnapBackAnimationForOffset(float f) {
        setPivotX(getWidth() / 2);
        float f1;
        if (isInLandscape())
            f1 = 0.0002F;
        else
            f1 = 0.0001F;
        if (f < 0.0F) {
            setPivotY(getHeight());
            float af1[] = new float[2];
            af1[0] = 1.0F + f * (f1 * 2.0F);
            af1[1] = 1.0F;
            ObjectAnimator objectanimator1 = ObjectAnimator.ofFloat(this, "scaleY", af1).setDuration(Math.min((long) (-f), 300L));
            objectanimator1.setInterpolator(new DecelerateInterpolator(3F));
            objectanimator1.start();
            return;
        } else {
            setPivotY(0.0F);
            float af[] = new float[2];
            af[0] = 1.0F + -f * (2.0F * f1);
            af[1] = 1.0F;
            ObjectAnimator objectanimator = ObjectAnimator.ofFloat(this, "scaleY", af).setDuration(Math.min((long) f, 300L));
            objectanimator.setInterpolator(new DecelerateInterpolator(3F));
            objectanimator.start();
            return;
        }
    }

    public void getF(float a) {
        a = computeVerticalScrollOffset();

    }

    public void getF1(float a1) {
        a1 = computeVerticalScrollRange() - computeVerticalScrollExtent();
    }

    protected void onLayout(boolean flag, int i, int j, int k, int l) {
        super.onLayout(flag, i, j, k, l);

        // setBackgroundColor(Color.argb(190, 0, 0, 0));
        /**setOnTouchListener(new android.view.View.OnTouchListener() {

         public boolean onTouch(View view, MotionEvent motionevent)
         {


         if(mLastTouchX - motionevent.getY() != 0.0F)
         mScrollX = mLastTouchX - motionevent.getY();

         if(motionevent.getAction() == 0){
         touchDownY = motionevent.getY();
         touchDownX = motionevent.getX();
         }


         if(motionevent.getAction() == 1)
         {
         if(isstretch){
         float f4 = touchDownY - (motionevent.getY()*2);
         if(f == 0.0F || f == f1)
         createSnapBackAnimationForOffset(f4);
         touchDownY = -1F;
         isstretch=false;
         return false;
         }


         }
         if(motionevent.getAction() == 2)
         {
         touchUpX=motionevent.getX();
         touchUpY=motionevent.getY();
         deltaX=touchUpX-touchDownX;
         deltaY=touchUpY-touchDownY;
         if(Math.abs(deltaY)>Math.abs(deltaX)+30){
         if(f == 0.0F && touchDownY < motionevent.getY())
         {
         if(touchDownY == -1F)
         touchDownY = motionevent.getY();
         float f3 = touchDownY - (motionevent.getY()*2);
         createAnimationForOffset(f3, f, f1);
         isstretch=true;
         return true;
         }
         Log.i("IM", (new StringBuilder("K")).append(f).append(" D ").append(f1).append(" D").append(touchDownY).append(" m ").append(motionevent.getY()).toString());
         if((int)f == (int)f1 && touchDownY > motionevent.getY())
         {
         if(touchDownY == -1F)
         touchDownY = motionevent.getY();
         float f2 = touchDownY - (motionevent.getY()*2);
         Log.i("IM", "K");
         createAnimationForOffset(f2, f, f1);
         isstretch=true;
         return true;
         } else
         {
         mLastTouchX = motionevent.getY();
         return false;
         }
         }


         }
         return false;
         }





         }
         );*/
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = MotionEventCompat.getActionMasked(ev);
        final float x = ev.getX();
        final float y = ev.getY();

        if (isEnabled() && touchListener.isSwipeEnabled()) {

            if (touchState == TOUCH_STATE_SCROLLING_X) {
                return touchListener.onTouch(this, ev);
            }

            switch (action) {
                case MotionEvent.ACTION_MOVE:
                    checkInMoving(x, y);
                    return touchState == TOUCH_STATE_SCROLLING_Y;
                case MotionEvent.ACTION_DOWN:
                    touchListener.onTouch(this, ev);
                    touchState = TOUCH_STATE_REST;
                    lastMotionX = x;
                    lastMotionY = y;
                    return false;
                case MotionEvent.ACTION_CANCEL:
                    touchState = TOUCH_STATE_REST;
                    break;
                case MotionEvent.ACTION_UP:
                    touchListener.onTouch(this, ev);
                    return touchState == TOUCH_STATE_SCROLLING_Y;
                default:
                    break;
            }
        }

        return super.onInterceptTouchEvent(ev);
    }

    /**
     * Check if the user is moving the cell
     *
     * @param x Position X
     * @param y Position Y
     */
    private void checkInMoving(float x, float y) {
        final int xDiff = (int) Math.abs(x - lastMotionX);
        final int yDiff = (int) Math.abs(y - lastMotionY);

        final int touchSlop = this.touchSlop;
        boolean xMoved = xDiff > touchSlop;
        boolean yMoved = yDiff > touchSlop;

        if (xMoved) {
            touchState = TOUCH_STATE_SCROLLING_X;
            lastMotionX = x;
            lastMotionY = y;
        }

        if (yMoved) {
            touchState = TOUCH_STATE_SCROLLING_Y;
            lastMotionX = x;
            lastMotionY = y;
        }
    }

    /**
     * Close all opened items
     */
    public void closeOpenedItems() {
        touchListener.closeOpenedItems();
    }

    public void closeOpenedItem(int position) {
        touchListener.closeOpenedItem(position);
    }

    public int getLastOpenedPosition() {
        return touchListener.getLastOpenedPosition();
    }

    public void openDefault(int position) {
        touchListener.openAnimate(position);

    }

}
