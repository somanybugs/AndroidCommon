package lhg.common.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import lhg.common.activity.PicturesBrowser;
import lhg.common.R;
import lhg.common.utils.Glide4Engine;
import lhg.common.utils.ViewUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.ui.MatisseActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static com.zhihu.matisse.MimeType.BMP;
import static com.zhihu.matisse.MimeType.JPEG;
import static com.zhihu.matisse.MimeType.PNG;

public class ImagesGridEditView extends ImagesGridView {

	protected final String TAG = "ImagesGridEditView";
	private int maxCount = 9;//默认九张
	private ContextActivityRequest activityRequest;

	public ImagesGridEditView(Context context) {
		this(context, null);
	}

	public ImagesGridEditView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ImagesGridEditView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		activityRequest = new ContextActivityRequest(context, TAG);
		setImageUrls(null, null);
	}


	protected void addImage(Object obj) {
		int index = imageCount();
		imageUrls.add(obj);
		addView(obtainItemView(), index);
		updateItemView(index);
	}
	protected void addImages(List objs) {
		for (Object obj : objs) {
			addImage(obj);
		}
	}

	/**
	 * @param index
	 */
	protected void removeImageAtIndex(int index) {
		if (index < this.imageUrls.size() && index >= 0) {
			imageUrls.remove(index);
			removeViewAt(index);
		}
	}

	@Override
	protected int getItemCount() {
		return super.getItemCount() + 1;
	}

	@Override
	protected void updateItemView(int index) {
		ImageView imageView = (ImageView) ((ViewGroup) getChildAt(index)).getChildAt(0);
		if (index >= imageCount()) {
			imageView.setImageResource(R.drawable.pcm_add_picture);
		} else {
			Object file = imageUrls.get(index);
			imageView.setImageDrawable(null);
			Glide.with(imageView).load(file).apply(displayImageOptionsSmall).into(imageView);
		}
	}

	@Override
	public void onItemClick(HGGridLayout parentView, int position) {
		if (position >= imageCount()) {
			if (imageCount() >= getMaxCount()) {
				Toast.makeText(getContext(), "最多只能添加" + maxCount + "张图片", Toast.LENGTH_SHORT).show();
				return;
			}
			Matisse matisse = Matisse.from(ViewUtils.findActivity(getContext()));
			matisse.choose(EnumSet.of(JPEG, PNG, BMP))
					.countable(true)
					.maxSelectable(getMaxCount() - imageCount())
//				.gridExpectedSize(getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
					.restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
					.thumbnailScale(0.85f)
					.imageEngine(new Glide4Engine());

			Intent intent = new Intent(getContext(), MatisseActivity.class);
			activityRequest.startActivityForResult(intent, (resultCode, data) -> {
				if (data != null) {
					addImages(Matisse.obtainPathResult(data));
				}
			});
		} else {
			PicturesBrowser.Param param = new PicturesBrowser.Param();
			param.files = new ArrayList(imageUrls);
			param.index = position;
			param.showContextMenu = false;
			param.showDeleteBtn = true;
			activityRequest.startActivityForResult(PicturesBrowser.makeIntent(getContext(), param),
					(resultCode, data) -> setImageUrls(param.files, null));
		}
	}


	public int getMaxCount() {
		return maxCount;
	}

	public void setMaxCount(int maxCount) {
		this.maxCount = maxCount;
	}

	public List<File> getLocalFiles() {
		List urls = getImageUrls();
		List<File> files = new ArrayList<>();
		for (Object obj : urls) {
			if (obj instanceof String) {
				String url = (String) obj;
				if (url.startsWith("file:")) {
					files.add(new File(url.substring("file://".length())));
				}
			} else if (obj instanceof File) {
				files.add((File) obj);
			}
		}
		return files;
	}

//	private void onActionPickPicture() {
//		try {
//			Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//			((Activity) getContext()).startActivityForResult(i, RequestCode_PickPhoto);
//		} catch (Exception e) {
//			e.printStackTrace();
//			Toast.makeText(getContext(), "无法打开图库,请确认安装了图库浏览程序", Toast.LENGTH_LONG).show();
//		}
//	}

//	private void onActionTakePicture() {
//		try {
//			File out = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".jpg",
//					Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
//			takePickureUri = Uri.fromFile(out);
//
//			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                 /*获取当前系统的android版本号*/
//			int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//			Log.e("currentapiVersion", "currentapiVersion====>" + currentapiVersion);
//			if (currentapiVersion < 24) {
//				intent.putExtra(MediaStore.EXTRA_OUTPUT, takePickureUri);
//				((Activity) getContext()).startActivityForResult(intent, RequestCode_TakePhoto);
//			} else {
//				ContentValues contentValues = new ContentValues(1);
//				contentValues.put(MediaStore.Images.Media.DATA, out.getAbsolutePath());
//				Uri uri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
//				intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//				((Activity) getContext()).startActivityForResult(intent, RequestCode_TakePhoto);
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//			Toast.makeText(getContext(), "无法打开相机", Toast.LENGTH_LONG).show();
//		}
//	}

//	public void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if ((requestCode == RequestCode_TakePhoto) && (resultCode == Activity.RESULT_OK)) {
//			if (takePickureUri != null) {
//				Log.i(TAG, "拍照的照片路径 " + takePickureUri.toString());
//				String picturePath = takePickureUri.toString();
//				addImage(picturePath);
//				if (getAddPictureHook() != null) {
//					getAddPictureHook().onAddFinished();
//				}
//			}
//			takePickureUri = null;
//		}
//		if ((requestCode == RequestCode_PickPhoto) && (resultCode == Activity.RESULT_OK)) {
//			if (data != null) {
//				String picturePath = Utils.parseFilePath(getContext(), data.getData());
//				if (!TextUtils.isEmpty(picturePath)) {
//					addImage("file://" + picturePath);
//					if (getAddPictureHook() != null) {
//						getAddPictureHook().onAddFinished();
//					}
//				} else {
//					Toast.makeText(getContext(), "无法读取照片", Toast.LENGTH_LONG).show();
//				}
//			}
//		}
//	}

//	public void removeUrl(String url) {
//		int index = imageUrls.indexOf(url);
//		removeImageAtIndex(index);
//	}
//
//	public List<String> getRemoteUrls() {
//		List<String> urls = getImageUrls();
//		List<String> files = new ArrayList<>();
//		for (String url : urls) {
//			if (!url.startsWith("file:")) {
//				files.add(url);
//			}
//		}
//		return files;
//	}




}
