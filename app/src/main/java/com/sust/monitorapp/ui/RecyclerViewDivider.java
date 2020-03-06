package com.sust.monitorapp.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;

import com.sust.monitorapp.R;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by yhl on 2020/3/6.
 *
 * RecyclerView设置分隔线
 */

public class RecyclerViewDivider extends RecyclerView.ItemDecoration {

    private int mOrientation = LinearLayoutManager.VERTICAL;
    private int mItemSize = 1;//item之间分割线的size，默认为1

    private Paint mPaint;//绘制item分割线的画笔，和设置其属性

    /**
     * 构造方法1 2 3个参数， 1 和2个参数的都是调用3个参数的构造方法，3个参数调用4个参数的构造方法
     *        构造方法4个参数直接写内容
     *
     * 构造方法1个参数(1.上下文)
     *        2个参数(1,上下文，2，绘制方向 )
     *        2个参数(1,上下文，2，绘制方向 3,分隔线颜色 颜色资源id)
     * @param context
     */

    public RecyclerViewDivider(Context context) {//构造方法非1个参数
        this(context,LinearLayoutManager.VERTICAL, R.color.colorAccent);
    }

    public RecyclerViewDivider(Context context, int orientation) {//构造方法2个参数
        this(context,orientation, R.color.colorAccent);
    }

    public RecyclerViewDivider(Context context, int orientation, int dividerColor){//构造方法3个参数
        this(context,orientation,dividerColor,20);//这里的最后一个参数填入1
    }

    /**
     * @param context
     * @param orientation 绘制方向
     * @param dividerColor 分割线颜色 颜色资源id
     * @param mItemSize 分割线宽度 传入dp值就行
     */
    public RecyclerViewDivider(Context context, int orientation, int dividerColor, int mItemSize){
        this.mOrientation = orientation;
        if(orientation != LinearLayoutManager.VERTICAL && orientation != LinearLayoutManager.HORIZONTAL){
            throw new IllegalArgumentException("请传入正确的参数") ;
        }
        //把dp值换算成px
        this.mItemSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,mItemSize,
                context.getResources().getDisplayMetrics());
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaint.setColor(context.getResources().getColor(dividerColor));
    }


    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if(mOrientation == LinearLayoutManager.VERTICAL){
            drawVertical(c,parent) ;//调用下方的绘制纵向的方法
        }else {
            drawHorizontal(c,parent) ;
        }
    }

    /**
     * 绘制纵向 item 分割线
     * @param canvas
     * @param parent
     */
    private void drawVertical(Canvas canvas,RecyclerView parent){
        final int left = parent.getPaddingLeft() ;
        final int right = parent.getMeasuredWidth() - parent.getPaddingRight();
        final int childSize = parent.getChildCount();
        for(int i = 0 ; i < childSize ; i ++){
            final View child = parent.getChildAt( i ) ;
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int top = child.getBottom() + layoutParams.bottomMargin ;
            final int bottom = top + mItemSize ;
            canvas.drawRect(left,top,right,bottom,mPaint);
        }
    }

    /**
     * 绘制横向 item 分割线
     * @param canvas
     * @param parent
     */
    private void drawHorizontal(Canvas canvas,RecyclerView parent){
        final int top = parent.getPaddingTop() ;
        final int bottom = parent.getMeasuredHeight() - parent.getPaddingBottom() ;
        final int childSize = parent.getChildCount() ;
        for(int i = 0 ; i < childSize ; i ++){
            final View child = parent.getChildAt( i ) ;
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) child.getLayoutParams();
            final int left = child.getRight() + layoutParams.rightMargin ;
            final int right = left + mItemSize ;
            canvas.drawRect(left,top,right,bottom,mPaint);
        }
    }

    /**
     * 设置item分割线的size
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(mOrientation == LinearLayoutManager.VERTICAL){
            outRect.set(0,0,0,mItemSize);//横向排列 底部偏移 （第4个参数）
        }else {
            outRect.set(0,0,mItemSize,0);//纵向排列 右边偏移 (在第三个参数)
        }
    }
}
