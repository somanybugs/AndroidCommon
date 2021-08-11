package lhg.common.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import lhg.common.R;
import lhg.common.utils.FileUtils;
import lhg.common.view.ViewHolder;

import java.io.File;
import java.security.Permissions;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FilePickerActivity extends BaseActivity {

    ListView listView;
    MyAdapter myAdapter;
    List<String> suffixList;
    TextView tv_full_path;
    List<FileEntity> dirList = new ArrayList<>();
    String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String rootDir;

    public static Intent makeIntent(Context context, String path, String... suffix) {
        Intent intent = new Intent(context, FilePickerActivity.class);
        if (!TextUtils.isEmpty(path)) {
            intent.putExtra("root_path", path);
        }

        if (suffix != null && suffix.length > 0) {
            intent.putExtra("suffix", suffix);
        }
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pcm_file_picker_activity);
        setSupportActionBar(findViewById(R.id.toolbar));
        tv_full_path = findViewById(R.id.tv_full_path);
        listView = findViewById(R.id.listView);
        listView.setAdapter(myAdapter = new MyAdapter());
        Intent intent = getIntent();
        if (intent == null) {
            finish();
            return;
        }
        rootDir = intent.getStringExtra("root_path");
        if (TextUtils.isEmpty(rootDir)) {
            rootDir = sdcard;
        }
        String[] suffix_tmp = intent.getStringArrayExtra("suffix");
        if (suffix_tmp != null && suffix_tmp.length > 0) {
            suffixList = Arrays.asList(suffix_tmp);
        }

        listView.setOnItemClickListener((parent, view, position, id) -> {
            FileEntity dir = myAdapter.file;
            dir.selectPosition = position;
            dir.selectTop = view.getTop();
            FileEntity fe = (FileEntity) parent.getAdapter().getItem(position);
            if (fe.isDir) {
                openDir(new File(fe.fullPath), true);
            } else {
                Intent ii = new Intent();
                ii.putExtra("file", fe.fullPath);
                setResult(RESULT_OK, ii);
                finish();
            }
        });
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openDir(new File(rootDir), true);
        }
    }

    boolean gotoParent() {
       if (dirList.isEmpty()) {
           openDir(new File(sdcard), true);
           return true;
       } else {
           FileEntity last = dirList.remove(dirList.size()-1);
           String dir = last.fullPath;
           if (!dir.contains(sdcard)) {
               openDir(new File(sdcard), true);
               return true;
           } else if (!sdcard.equals(dir)) {
               File parent = new File(dir).getParentFile();
               openDir(parent, false);
               return true;
           }
       }
       return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_close) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



    static String withPathSeparator(String path) {
        if (path.endsWith(File.separator)) {
            return path;
        }
        return path + File.separator;
    }

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    FileEntity fileToEntity(File file, boolean readChild) {
        FileEntity entity = new FileEntity();
        entity.isDir = file.isDirectory();
        entity.name = file.getName();
//
        entity.fullPath  = file.getAbsolutePath();
        entity.time = sdf.format(file.lastModified());
        entity.size = entity.isDir ? 0: file.length();

        if (readChild && entity.isDir) {
            List<FileEntity> dirList = new ArrayList<>();
            List<FileEntity> fileList = new ArrayList<>();
            File[] files = file.listFiles();
            if (files == null) {
                return entity;
            }
            entity.childCount = files.length;
            for (File f : files) {
                if (f.getName().equals(".") || f.getName().equals("..")) {
                    continue;
                }
                if (f.isDirectory()) {
                    dirList.add(fileToEntity(f, false));
                } else {
                    if (!hasSuffix(f)) {
                        continue;
                    }
                    fileList.add(fileToEntity(f, false));
                }
            }
            Collections.sort(dirList, (o1, o2) -> o1.name.compareTo(o2.name));
            Collections.sort(fileList, (o1, o2) -> o1.name.compareTo(o2.name));
            dirList.addAll(fileList);
            entity.children = dirList;
        }
        return entity;
    }

    private void openDir(File dir, boolean force) {
        String strDir = dir.getAbsolutePath();
        FileEntity fe = null;
        for (int i = dirList.size() - 1; i >= 0; i--) {
            FileEntity ii = dirList.get(i);
            if (strDir.equals(ii.fullPath)) {
                if (!force) {
                    fe = ii;
                    break;
                }
            }
            if (!strDir.startsWith(withPathSeparator(ii.fullPath))) {
                dirList.remove(i);
            }
        }
        if (fe == null) {
            fe = fileToEntity(dir, true);
            dirList.add(fe);
        }
        myAdapter.setFile(fe);
        if (fe.selectTop != 0) {
            listView.setSelectionFromTop(fe.selectPosition, fe.selectTop);
        }
        tv_full_path.setText(dir.getAbsolutePath());
    }

    @Override
    public void onBackPressed() {
        if (!gotoParent()) {
            super.onBackPressed();
        }
    }

    boolean hasSuffix(File f) {
        for (String tt : suffixList) {
            if (f.getName().endsWith(tt)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pcm_file_picker, menu);
        return true;
    }



    static class FileEntity {
        public String name;
        public String fullPath;
        public long size;
        public boolean isDir;
        public int childCount;
        public String time;
        int selectPosition;
        int selectTop;
        List<FileEntity> children;
    }


    static class MyAdapter extends BaseAdapter {

        FileEntity file;
        Drawable icFolder, icFile;

        public void setFile(FileEntity file) {
            this.file = file;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return file == null || file.children == null ? 0 : file.children.size();
        }

        @Override
        public FileEntity getItem(int position) {
            return file.children.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (icFolder == null) {
                icFolder = parent.getContext().getResources().getDrawable(R.drawable.ic_folder);
                icFile = parent.getContext().getResources().getDrawable(R.drawable.ic_file);
            }
            if (convertView == null) {
                convertView = View.inflate(parent.getContext(), R.layout.pcm_file_picker_item, null);
            }
            FileEntity t = getItem(position);
            ViewHolder.imageView(convertView, R.id.iv_icon).setImageDrawable(t.isDir ? icFolder : icFile);
            ViewHolder.textView(convertView, R.id.tv_name).setText(t.name);
            if (t.isDir) {
                ViewHolder.textView(convertView, R.id.tv_info).setText(t.childCount + "项  |  "  + t.time );
            } else {
                ViewHolder.textView(convertView, R.id.tv_info).setText(FileUtils.formatFileSize(t.size)+"  |  "  + t.time );
            }
            return convertView;
        }
    }
}
