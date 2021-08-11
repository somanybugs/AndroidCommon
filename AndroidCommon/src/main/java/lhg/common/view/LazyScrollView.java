//package lhg.common.view;
//
//import android.content.Context;
//import android.graphics.Canvas;
//import android.util.AttributeSet;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.VelocityTracker;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewParent;
//import android.widget.OverScroller;
//
//import androidx.annotation.Nullable;
//
//public class LazyScrollView extends View {
//
//    private static final String TAG = "ViewScrollHelper";
//    public static final int INVALID_POINTER = -1;
//
//    // Distance to travel before a drag may begin
//    protected int mTouchSlop;
//
//    // Last known position/pointer tracking
//    private int mActivePointerId = INVALID_POINTER;
//    private int mLastMotionX;
//    private int mLastMotionY;
//    private int mInitMotionX;
//    private int mInitMotionY;
//
//    private VelocityTracker mVelocityTracker;
//    private float mMaxVelocity;
//    private float mMinVelocity;
//
//    private boolean mIsDragging;
//    private OverScroller mScroller;
//    private boolean wrongDirectionScrollFirst = false;
//
//
//    public LazyScrollView(Context context) {
//        super(context);
//    }
//
//    public LazyScrollView(Context context, @Nullable AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public LazyScrollView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//
//    public boolean onTouchEvent(MotionEvent ev) {
//        final int actionMasked = ev.getActionMasked();
//        if (actionMasked == MotionEvent.ACTION_DOWN) {
//            // Reset things for a new event stream, just in case we didn't get
//            // the whole previous stream.
//            cancel();
//        }
//        if (mVelocityTracker == null) {
//            mVelocityTracker = VelocityTracker.obtain();
//        }
//        mVelocityTracker.addMovement(ev);
//
//        switch (actionMasked) {
//            case MotionEvent.ACTION_DOWN: {
//                if ((mIsDragging = !mScroller.isFinished())) {
//                    final ViewParent parent = mView.getParent();
//                    if (parent != null) {
//                        parent.requestDisallowInterceptTouchEvent(true);
//                    }
//                }
//
//                // Remember where the motion event started
//                mInitMotionX = mLastMotionX = (int) ev.getX();
//                mInitMotionY = mLastMotionY = (int) ev.getY();
//                Log.i("RTYJK", "mInitMotionY +" + mInitMotionY) ;
//                mActivePointerId = ev.getPointerId(0);
//                break;
//            }
//            case MotionEvent.ACTION_MOVE:
//                final int activePointerIndex = ev.findPointerIndex(mActivePointerId);
//                if (activePointerIndex == -1) {
//                    Log.e(TAG, "Invalid pointerId=" + mActivePointerId + " in onTouchEvent");
//                    break;
//                }
//
//                final int x = (int) ev.getX(activePointerIndex);
//                final int y = (int) ev.getY(activePointerIndex);
//                int dx = x - mLastMotionX;
//                int dy = y - mLastMotionY;
//                if (!mIsDragging) {
//                    boolean slop = checkTouchSlop(dx, dy);
//                    if (!slop) {
//                        dx = x - mInitMotionX;
//                        dy = y - mInitMotionY;
//                        Log.i("RTYJK", "dy = " + dy);
//                        slop = checkTouchSlop(dx, dy);
//                    }
//                    if (slop) {
//                        View toCapture = findTopChildUnder(mView, x, y);
//                        if (toCapture != null) {
//                            int edge = getEdge(dx, dy);
//                            mIsDragging = mCallback.onDragBegin(toCapture, x, y, dx, dy, edge);
//                        }
//                    }
//                    if (mIsDragging) {
//                        final ViewParent parent = mView.getParent();
//                        if (parent != null) {
//                            parent.requestDisallowInterceptTouchEvent(true);
//                        }
//                    }
//                }
//                mLastMotionX = x;
//                mLastMotionY = y;
//                if (mIsDragging) {
//                    mCallback.onScroll(x, y, dx, dy);
//                }
//                break;
//            case MotionEvent.ACTION_UP:
//            case MotionEvent.ACTION_CANCEL:
//                if (mIsDragging) {
//                    releaseViewForPointerUp();
//                }
//                cancel();
//                break;
//            case MotionEvent.ACTION_POINTER_UP:
//                onSecondaryPointerUp(ev);
//                break;
//        }
//        return true;
//    }
//
//    private void onSecondaryPointerUp(MotionEvent ev) {
//        final int pointerIndex = (ev.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >>
//                MotionEvent.ACTION_POINTER_INDEX_SHIFT;
//        final int pointerId = ev.getPointerId(pointerIndex);
//        if (pointerId == mActivePointerId) {
//            // This was our active pointer going up. Choose a new
//            // active pointer and adjust accordingly.
//            // TODO: Make this decision more intelligent.
//            final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
//            mLastMotionX = (int) ev.getX(newPointerIndex);
//            mLastMotionY = (int) ev.getY(newPointerIndex);
//            mActivePointerId = ev.getPointerId(newPointerIndex);
//            if (mVelocityTracker != null) {
//                mVelocityTracker.clear();
//            }
//        }
//    }
//
//    protected boolean checkTouchSlop(float dx, float dy) {
//        final int direction = mCallback.getScrollDirection();
//        if (direction == DIRECTION_ALL) {
//            return dx * dx + dy * dy > mTouchSlop * mTouchSlop;
//        } else if (direction == DIRECTION_HORIZONTAL) {
//            if (!mCallback.shouldDragIfWrongDirectionScrollFirst() && !wrongDirectionScrollFirst) {
//                wrongDirectionScrollFirst = (Math.abs(dy) > Math.abs(dx)) && (Math.abs(dy)> mTouchSlop);
//            }
//            return Math.abs(dx) > mTouchSlop;
//        } else if (direction == DIRECTION_VERTICAL) {
//            if (!mCallback.shouldDragIfWrongDirectionScrollFirst() && !wrongDirectionScrollFirst) {
//                wrongDirectionScrollFirst = (Math.abs(dx) > Math.abs(dy)) && (Math.abs(dx)> mTouchSlop);
//            }
//            return Math.abs(dy) > mTouchSlop;
//        }
//        return false;
//    }
//
//
//    private void releaseViewForPointerUp() {
//        mIsDragging = false;
//        mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
//        final float xvel = clampMag(
//                mVelocityTracker.getXVelocity(mActivePointerId),
//                mMinVelocity, mMaxVelocity);
//        final float yvel = clampMag(
//                mVelocityTracker.getYVelocity(mActivePointerId),
//                mMinVelocity, mMaxVelocity);
//        mCallback.onDragEnd((int)xvel, (int)yvel);
//    }
//
//    @Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//    }
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//    }
//}
