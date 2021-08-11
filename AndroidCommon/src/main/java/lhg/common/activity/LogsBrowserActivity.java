package lhg.common.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;

import lhg.common.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LogsBrowserActivity extends BaseActivity {


    private Toolbar toolbar;
    String UploaderClass;

    public static Intent makeIntent(Context context, String title, String path,  Class<?extends LogViewActivity.Uploader> upload) {
        Intent intent = new Intent(context, LogsBrowserActivity.class);
        intent.putExtra("path", path);
        intent.putExtra("title", title);
        if (upload != null) {
            intent.putExtra("UploaderClass", upload.getName());
        }
        return intent;
    }
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcm_activity_log_browser);
        setSupportActionBar(toolbar=findViewById(R.id.toolbar));
        showPrevArrowOnActionBar();

        listView = findViewById(R.id.listView);
        setTitle(getIntent().getStringExtra("title"));
        if (toolbar != null) {
            toolbar.setTitle(getIntent().getStringExtra("title"));
        }
        UploaderClass = getIntent().getStringExtra("UploaderClass");

        String path = getIntent().getStringExtra("path");
        File file = new File(path);
        List<String> files = new ArrayList<>();
        if (file.isDirectory()) {
            for (File f:file.listFiles()) {
                String name = f.getName();
                if (f.isFile() && f.canRead() && isTextFile(name)) {
                    files.add(0, name);
                }
            }
        }
        listView.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1, files));
        listView.setOnItemClickListener((parent, view, position, id) -> {
            String name = (String) parent.getAdapter().getItem(position);
            LogViewActivity.startActivity(name, Uri.fromFile(new File(path, name)).toString(), UploaderClass, getActivity());
        });
    }


    static boolean isTextFile(String name) {
        name = name.toLowerCase();
        return name.endsWith(".log")
                || name.endsWith(".txt")
                || name.endsWith(".ini")
                || name.endsWith(".html")
                || name.endsWith(".xml")
                || name.endsWith(".json")
                ;
    }
}
