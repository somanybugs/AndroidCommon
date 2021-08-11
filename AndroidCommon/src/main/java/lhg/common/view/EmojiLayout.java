package lhg.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class EmojiLayout extends LinearLayout {

    public EmojiLayout(Context context) {
        this(context, null);
    }

    public EmojiLayout(Context context,  AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmojiLayout(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
