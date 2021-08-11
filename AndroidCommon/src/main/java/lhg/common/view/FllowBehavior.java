package lhg.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.appbar.AppBarLayout;

public class FllowBehavior extends CoordinatorLayout.Behavior<View> {



    public FllowBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //确定所提供的子视图是否有另一个特定的同级视图作为布局从属。
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
//这个方法是说明这个子控件是依赖AppBarLayout的
        if (child.getTag() == null || dependency.getTag()==null) {
            return false;
        }
        return (child.getTag().toString()).endsWith(dependency.getTag().toString());
    }

    //用于响应从属布局的变化
    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        child.setTop(dependency.getBottom());
        return true;
    }

}
