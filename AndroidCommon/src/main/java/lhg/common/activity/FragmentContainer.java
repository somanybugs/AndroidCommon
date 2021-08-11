package lhg.common.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import lhg.common.R;


public class FragmentContainer extends BaseActivity {
    Toolbar toolbar ;
    public static final String IntentKey_Title = "IntentKey_Title";
    public static final String IntentKey_Fragment = "IntentKey_Fragment";

    public static Intent makeIntent(Context context, String title, Class<?extends Fragment> fragmentClass) {
        Intent intent = new Intent(context, FragmentContainer.class);
        intent.putExtra(IntentKey_Title, title);
        intent.putExtra(IntentKey_Fragment, fragmentClass.getName());
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcm_activity_fragment_container);

        setSupportActionBar(toolbar = findViewById(R.id.toolbar));
        showPrevArrowOnActionBar();

        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        String title = intent.getStringExtra(IntentKey_Title);
        setTitle(title);
        toolbar.setTitle(title);
        String fragmentName = intent.getStringExtra(IntentKey_Fragment);
        try {
            Fragment fragment = (Fragment) Class.forName(fragmentName).newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.frameLayout, fragment)
                    .commitAllowingStateLoss();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}