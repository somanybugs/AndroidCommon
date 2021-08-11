package lhg.common.view;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialog;

import com.airbnb.lottie.LottieAnimationView;

public class LoadingDialog extends AppCompatDialog {
    LottieAnimationView view;
    public LoadingDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        view = new LottieAnimationView(getContext());
        view.setAnimation("loading_lottie_dots.json");
        view.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dp2px(96)));
        view.setRepeatCount(ValueAnimator.INFINITE);
        setContentView(view);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);// 一句话搞定
        getWindow().setDimAmount(0);//设置昏暗度为0
    }

    protected int dp2px(float dp) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!view.isAnimating()) {
            view.playAnimation();
        }
    }

    @Override
    protected void onStop() {
        if (view.isAnimating()) {
            view.cancelAnimation();
        }
        super.onStop();
    }
}
