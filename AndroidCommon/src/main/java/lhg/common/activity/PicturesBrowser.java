package lhg.common.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import lhg.common.R;
import lhg.common.utils.Utils;
import lhg.common.utils.ToastUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class PicturesBrowser extends Activity {
	public static final String		TAG					= "LocalPictureBrowser";
	public static final String		IntentKey_UUID		= "IntentKey_UUID";		// list<String>
	static HashMap<String, Param> _staticParams;
	private String 					uuid;
	private Param 					mParam;
	private Map<Object, File> 		mCacheFiles			= new HashMap<>();
	private ViewPager mViewPager;
	protected MyAdapter 				adapter;
	private ViewPager.OnPageChangeListener changeListener;
	private TextView 				tv_title;

	public static HashMap<String, Param> staticParams() {
		if (_staticParams == null) {
			synchronized (PicturesBrowser.class) {
				if (_staticParams == null) {
					_staticParams = new HashMap<>();
				}
			}
		}
		return _staticParams;
	}

	public static Intent makeIntent(Context context, Param param) {
		if (param.files == null || param.files.isEmpty()) {
			return null;
		}
		String uuid = UUID.randomUUID().toString();
		staticParams().put(uuid, param);
		Intent intent = new Intent(context, PicturesBrowser.class);
		intent.putExtra(IntentKey_UUID, uuid);
		return intent;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.pcm_activity_localpicturebrowser);

		Intent intent = getIntent();
		if (intent != null) {
			uuid = intent.getStringExtra(IntentKey_UUID);
			mParam = staticParams().remove(uuid);
			mParam.index = Math.min(Math.max(mParam.index, 0), mParam.files.size());
		}
		if (mParam == null) {
			finish();
			return;
		}

		mViewPager = findViewById(R.id.viewPager);
		tv_title = findViewById(R.id.textView);
		View iv_delete = findViewById(R.id.iv_delete);
		iv_delete.setOnClickListener(v -> deleteCurrentPicture());
		iv_delete.setVisibility(mParam.showDeleteBtn ? View.VISIBLE : View.GONE);
		mViewPager.setAdapter(adapter = (MyAdapter) createAdapter());
		adapter.showContextMenu = mParam.showContextMenu;
		changeListener = new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				mParam.index = position;
				tv_title.setText((position + 1) + "/" + mViewPager.getAdapter().getCount());
			}

			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

			@Override
			public void onPageScrollStateChanged(int state) {}
		};

		mViewPager.addOnPageChangeListener(changeListener);
		mViewPager.setCurrentItem(mParam.index);
//		mViewPager.setOnLongClickListener(v -> {
//			if (mParam.showContextMenu) {
//				registerForContextMenu(v);
//				openContextMenu(v);
//			}
//			return mParam.showContextMenu;
//		});
		changeListener.onPageSelected(mParam.index);
	}


	@Override
	protected void onDestroy() {
		mViewPager.removeOnPageChangeListener(changeListener);
		changeListener = null;
		super.onDestroy();
	}

	protected PagerAdapter createAdapter() {
		MyAdapter adapter = new MyAdapter();
		adapter.showContextMenu = mParam.showContextMenu;
		return adapter;
	}
	

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		//添加菜单项 menu.add(参数一：分组,选项的id,菜单项的显示顺序(默认是0,代表按照添加的顺序),"选项显示的字段");
		menu.add(0, 1, 0, "下载");
		menu.add(0, 2, 0, "分享");
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// 得到当前被选中的item信息
		int itemId=item.getItemId();
		File file = mCacheFiles.get(mParam.files.get(mParam.index));
		if (file == null) {
			ToastUtil.show(this, "图片正在下载,请稍后重试");
			return true;
		}
		if (itemId == 1) {
			try {
				MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), "title", "description");
				ToastUtil.show(this, "图片已保存到相册");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else if (itemId == 2) {
			Utils.shareImage(this, file.getAbsolutePath());
		}
		return true;
	}


	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		setResult(RESULT_OK, intent);
		super.onBackPressed();
	}

	private void deleteCurrentPicture() {
		mParam.files.remove(mParam.index);
		mViewPager.setAdapter(adapter = new MyAdapter());
		if (mParam.index >= mParam.files.size()) {
			mParam.index = mParam.files.size() - 1;
		}
		if (mParam.files.isEmpty()) {
			setResult(RESULT_OK);
			finish();
			return;
		}
		mViewPager.setCurrentItem(mParam.index, true);
		changeListener.onPageSelected(mParam.index);
	}

	protected  class MyAdapter extends PagerAdapter {
		boolean showContextMenu;

		@Override
		public int getCount() {
			return mParam.files == null ? 0 : mParam.files.size();
		}

		@Override
		public Object instantiateItem(ViewGroup container, final int position) {
			final View view = View.inflate(PicturesBrowser.this, R.layout.pcm_largeimage_view, null);
			final SubsamplingScaleImageView photoView = view.findViewById(R.id.photoView);
			if (showContextMenu) {
				photoView.setOnLongClickListener(v -> {
					Activity activity = Utils.getActivityFromView(v);
					if (activity != null) {
						activity.registerForContextMenu(v);
						activity.openContextMenu(v);
					}
					return true;
				});
			}
			photoView.setTag(mParam.files.get(position));
			photoView.setMaxScale(10);
			container.addView(view);
			FutureTarget<File> target = (FutureTarget<File>) view.getTag();
			if (target != null && !target.isDone()) {
				target.cancel(true);
			}
			view.setTag(null);
			File file = mCacheFiles.get(mParam.files.get(position));
			if (file != null) {
				photoView.setImage(ImageSource.uri(Uri.fromFile(file)));
			} else {
				photoView.setImage(ImageSource.resource(R.drawable.pcm_image_loading));
				RequestListener<File> listener = new RequestListener<File>() {
					SubsamplingScaleImageView tmpPhotoView = photoView;

					@Override
					public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<File> target, boolean isFirstResource) {
						if (model.equals(tmpPhotoView.getTag())) {
							tmpPhotoView.setImage(ImageSource.resource(R.drawable.pcm_image_error));
						}
						return true;
					}

					@Override
					public boolean onResourceReady(File resource, Object model, Target<File> target, DataSource dataSource, boolean isFirstResource) {
						if (model.equals(tmpPhotoView.getTag())) {
							tmpPhotoView.setImage(ImageSource.uri(Uri.fromFile(resource)));
						}
						mCacheFiles.put(model, resource);
						return true;
					}
				};
				target = Glide.with(getApplicationContext())
						.downloadOnly().listener(listener)
						.load(mParam.files.get(position)).submit();
				view.setTag(target);
			}
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			View view = (View) object;
			SubsamplingScaleImageView photoView = view.findViewById(R.id.photoView);
			if (photoView != null) {
				photoView.recycle();
//				if (((Integer) view.getTag()).intValue() != position) {
//					Log.e(TAG, "view.getTag() " + view.getTag() + " is not equal to position " + position);
//				}
			}
			container.removeView(view);
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}
	}
	
	public static class Param {
		public ArrayList files;
		public boolean showDeleteBtn;
		public boolean showContextMenu;
		public int index = 0;
	}
}
