package com.derek.app.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.derek.app.R;

/**
 * 圆形菜单
 */
public class CircleMenuLayout extends ViewGroup {
    // 直径
    private int mRadius;
    // 容器内child item的默认尺寸
    private static final float RADIO_DEFAULT_CHILD_DIMENSION = 1/4f;
    // 改容器的内边距，无视padding属性，
    private static final float RADIO_PADDING_LAYOUT = 1/12f;
    private float mPadding;
    private double mStartAngle = 0;

    private String[] mItemTexts;
    private int[] mItemImgs;

    private int mMenuItemCount;

    private int mMenuItemLayoutId = com.derek.db.R.layout.circle_menu_item;

    private OnItemClickListener mOnMenuItemClickListener;

    public CircleMenuLayout(Context context) {
        super(context);
        setPadding(0,0,0,0);
    }

    public void setMenuItemIconsAndTests(int[] images,String[] texts){
        if (images == null && texts == null){

        }

        mItemImgs = images;
        mItemTexts = texts;

        mMenuItemCount = images == null ? texts.length : images.length;
        if (images != null && texts != null){
            mMenuItemCount = Math.min(images.length,texts.length);
        }
        buildMenuItems();
    }

    private void buildMenuItems() {
        for (int i = 0;i<mMenuItemCount;i++){
            View itemView = inflateMenuView(i);
            initMenuItem(itemView,i);
            addView(itemView);
        }
    }

    private void initMenuItem(View itemView, int i) {
        ImageView iv = itemView.findViewById(R.id.id_circle_menu_image);
        TextView tv = itemView.findViewById(R.id.id_circle_menu_text);
        iv.setVisibility(View.VISIBLE);
        iv.setImageResource(mItemImgs[i]);
        tv.setVisibility(View.VISIBLE);
        tv.setText(mItemTexts[i]);
    }

    public void setMenuItemLayoutId(int menuItemLayoutId){
        this.mMenuItemLayoutId = menuItemLayoutId;
    }

    public  void seOnItemClickListener(OnItemClickListener listener){
        this.mOnMenuItemClickListener = listener;
    }

    private View inflateMenuView(final int childIndex) {
        LayoutInflater mInflater = LayoutInflater.from(getContext());
        View itemView = mInflater.inflate(mMenuItemLayoutId,this,false);
        itemView.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if (mOnMenuItemClickListener != null){
                    mOnMenuItemClickListener.onClick(v,childIndex);
                }
            }
        });
        return itemView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        measureMyself(widthMeasureSpec,heightMeasureSpec);
        measureChildViews();
    }

    private void measureChildViews() {

    }

    private void measureMyself(int widthMeasureSpec, int heightMeasureSpec) {
        int resWidth = 0;
        int resHeight = 0;

//        int
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

    }


    public interface OnItemClickListener{
        void onClick(View v,int childIndex);
    }
}
