package lhg.common.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import lhg.common.R;
import lhg.common.utils.Utils;
import lhg.common.utils.ViewUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


public class AboutActivity extends BaseActivity {
    static String IntentKeyParams = "params";

    public static Intent makeIntent(Context context, Params params) {
        Intent intent = new Intent(context, AboutActivity.class);
        intent.putExtra(IntentKeyParams, params);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcm_activity_about);
        setSupportActionBar(findViewById(R.id.toolbar));
        showPrevArrowOnActionBar();
        setTitle(Utils.getApplicationName(this));

        TextView tv_title = findViewById(R.id.tvTitle);
        ImageView iv_logo = findViewById(R.id.iv_logo);
        tv_title.setText(Utils.getApplicationName(this) + "\n" + Utils.getAppVersionName(this));
        iv_logo.setImageDrawable(Utils.getAppLauncherIcon(this));
        TextView tv_detail = findViewById(R.id.tvDetail);
        LinearLayout ll_content = findViewById(R.id.ll_content);
        Params params = null;
        if (getIntent() != null && getIntent().hasExtra(IntentKeyParams)) {
            params = (Params) getIntent().getSerializableExtra(IntentKeyParams);
        }
        if (params != null) {
            tv_detail.setText(params.detail);
            if (params.contacts!=null && params.contacts.length > 0) {
                List<View> list = new ArrayList<>();
                for (int i = 0; i < params.contacts.length/2; i++) {
                    list.add(createContactView(params.contacts[i*2], params.contacts[i*2+1]));
                }
                ViewUtils.addItemViewsToLinearLayout(ll_content, list, false, false, 0);
            }
        }
    }

    View createContactView(String key, String val) {
        View view = View.inflate(this, R.layout.pcm_item_about_contact, null);
        TextView tvName = view.findViewById(R.id.tv_name);
        TextView tvValue = view.findViewById(R.id.tv_value);
        tvName.setText(key);
        tvValue.setText(val);
        view.findViewById(R.id.btn_copy).setOnClickListener(v -> Utils.copy2Clipboard(getActivity(), val));
        return view;
    }

    public static class Params implements Serializable {
        public String detail;
        public String[] contacts; // [key1, val1, key2, val2,.....]
    }



}
