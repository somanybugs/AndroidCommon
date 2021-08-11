package lhg.common.view;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;


/**
 * Created by lhg on 2017/6/14.
 */

public class CountDownDialogNotCompat extends android.app.AlertDialog {

    CharSequence title;
    int seconds;
    CountDownTimer countDownTimer;

    public CountDownDialogNotCompat(Context context, int seconds) {
        super(context);
        this.seconds = seconds;
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        countDownTimer = new CountDownTimer(seconds*1000, 1000) {
            public void onTick(long millisUntilFinished) {
                int sec = (int) (millisUntilFinished / 1000);
                CountDownDialogNotCompat.super.setTitle(title + "(" + sec + "s)");
            }
            public void onFinish() {
                postFinish();
            }
        };
    }

    void postFinish() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        dismiss();
        //取消倒计时
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        this.title = title;
        super.setTitle(title + "(" + seconds + "s)");
    }

    @Override
    public void show() {
        super.show();
        countDownTimer.start();
    }

}
