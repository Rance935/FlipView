package com.rance.flipview;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageButton;

import com.rance.flipview.bean.FamilyInfo;
import com.rance.flipview.flipview.FlipViewController;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.flip_view)
    FlipViewController flipView;
    @BindView(R.id.left_page)
    ImageButton leftPage;
    @BindView(R.id.right_page)
    ImageButton rightPage;
    @BindView(R.id.shake)
    CheckBox shake;

    private FlipViewAdapter adapter;

    private List<FamilyInfo> familyInfos = new ArrayList<>();

    //总页数
    private int totalPage = 0;

    private Animation shakeAnimation;

    private GridView gridViewLeft;
    private GridView gridViewRight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initWidget();
    }

    private void initWidget() {
        shakeAnimation = AnimationUtils.loadAnimation(this, R.anim.shake);//加载动画资源文件

        for (int i = 0; i < 17; i++) {
            FamilyInfo familyInfo = new FamilyInfo();
            familyInfo.header = "http://upload.jianshu.io/collections/images/429893/timg.jpg?imageMogr2/auto-orient/strip|imageView2/1/w/240/h/240";
            familyInfo.name = "item" + i;
            familyInfos.add(familyInfo);
        }

        /**
         * 当前数据8个一页，获取总页数
         * 两种情况1：数据长度刚好是8的倍数，那么总页数=数据长度÷8
         *        2：数据长度不为8的倍数，总页数=数据长度÷8 + 1 （为什么加1？因为长度与8相除有余数，余数这一部分也要占一页）
         */
        if (familyInfos.size() % 8 == 0) {
            totalPage = familyInfos.size() / 8;
        } else {
            totalPage = familyInfos.size() / 8 + 1;
        }

        /**用一个新的集合来装每一页里面的数据，形成一个新的集合
         * 循环总页数次   用List.subList(int start, int end)方式取出数据  注意这里的参数不是下标，从1开始
         * 同样两种情况1：当当前页的数据刚好8个的时候直接取
         *            2：最后一页的情况，就是从 页码*8->数据长度
         */
        List<List<FamilyInfo>> family = new ArrayList<>();
        for (int i = 0; i < totalPage; i++) {
            List newlist = null;
            if (i * 8 + 7 < familyInfos.size() - 1) {
                newlist = familyInfos.subList(i * 8, (i + 1) * 8);
            } else {
                newlist = familyInfos.subList(i * 8, familyInfos.size());
            }
            family.add(newlist);
        }
        adapter = new FlipViewAdapter(this, family);
        flipView.setAdapter(adapter);

        /**
         * 监听翻页事件
         * 第一页时隐藏左边按钮，最后一页时隐藏右边按钮
         * 当抖动动画正在进行时关闭动画
         */
        flipView.setOnViewFlipListener((view, position) -> {
            if (position == 0) {
                leftPage.setVisibility(View.INVISIBLE);
                rightPage.setVisibility(View.VISIBLE);
            } else if (position == totalPage - 1) {
                leftPage.setVisibility(View.VISIBLE);
                rightPage.setVisibility(View.INVISIBLE);
            } else {
                leftPage.setVisibility(View.VISIBLE);
                rightPage.setVisibility(View.VISIBLE);
            }
            if (shake.isChecked()) {
                shake.setChecked(false);
                for (int i = 0; i < gridViewLeft.getChildCount(); i++) {
                    gridViewLeft.getChildAt(i).clearAnimation();
                }
                for (int i = 0; i < gridViewRight.getChildCount(); i++) {
                    gridViewRight.getChildAt(i).clearAnimation();
                }
            }
        });

        /**
         * 这里之所以监听的是OnClickListener而不是OnCheckedChangeListener原因是
         * 我们在上面的翻页下效果里面调用了checkbox的setChecked()方法，当checkbox的Checked有变化时就会调用OnCheckedChangeListener
         */
        shake.setOnClickListener(v -> {
            boolean isChecked = ((CheckBox) v).isChecked();
            gridViewLeft = (GridView) flipView.getSelectedView().findViewById(R.id.grid_view_left);
            gridViewRight = (GridView) flipView.getSelectedView().findViewById(R.id.grid_view_right);
            for (int i = 0; i < gridViewLeft.getChildCount(); i++) {
                if (isChecked) {
                    gridViewLeft.getChildAt(i).startAnimation(shakeAnimation);
                } else {
                    gridViewLeft.getChildAt(i).clearAnimation();
                }
            }
            for (int i = 0; i < gridViewRight.getChildCount(); i++) {
                if (isChecked) {
                    gridViewRight.getChildAt(i).startAnimation(shakeAnimation);
                } else {
                    gridViewRight.getChildAt(i).clearAnimation();
                }
            }
        });
    }

    //按钮的翻页监听，这里我们不能直接调用flipView.setSelection()方法，因为这样就没有翻页的动画了，我们就在这里dispatchTouchEvent模拟滑动view
    @OnClick({R.id.left_page, R.id.right_page})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.left_page:
                flipView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, flipView.getLeft(), flipView.getTop(), 0));
                flipView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, flipView.getLeft() + 100, flipView.getTop(), 0));
                flipView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, flipView.getLeft() + 200, flipView.getTop(), 0));
                flipView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, flipView.getLeft() + 200, flipView.getTop(), 0));
                break;
            case R.id.right_page:
                flipView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, flipView.getLeft(), flipView.getTop(), 0));
                flipView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, flipView.getLeft() - 100, flipView.getTop(), 0));
                flipView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(), MotionEvent.ACTION_MOVE, flipView.getLeft() - 200, flipView.getTop(), 0));
                flipView.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(),
                        SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, flipView.getLeft() - 200, flipView.getTop(), 0));
                break;
        }
    }
}
