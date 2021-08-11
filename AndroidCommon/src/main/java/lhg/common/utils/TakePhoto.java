package lhg.common.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import java.io.File;

/**
 * 日期: 2015年1月8日 下午7:08:55
 * 作者: 刘浩歌
 * 邮箱: okz@outlook.com
 * 作用:
 */
public class TakePhoto {
	private int requestCode_TakePhoto = -1;
	private int requestCode_PickPhoto = -1;
	private Uri takePickureUri;

	Intent takePictureIntent(Context context) {
		//创建一个file，用来存储拍照后的照片
		File takePickureFile = new File(context.getExternalCacheDir(), System.currentTimeMillis() + ".jpg");
		try {
			if (takePickureFile.exists()) {
				takePickureFile.delete();//删除
			}
			takePickureFile.createNewFile();
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (Build.VERSION.SDK_INT >= 24) {
			takePickureUri = FileProvider.getUriForFile(context,
					context.getPackageName() + ".fileprovider", //可以是任意字符串
					takePickureFile);
		} else {
			takePickureUri = Uri.fromFile(takePickureFile);
		}
		//启动相机程序
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, takePickureUri);
		takePickureUri = Uri.fromFile(takePickureFile);
		return intent;
	}

	Intent pickPictureIntent(Context context) {
		Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
		intent.setType("image/*");
//			Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		return intent;
	}


	public void pickPhoto(Fragment fragment, int requestCode) {
		requestCode_TakePhoto = -1;
		requestCode_PickPhoto = requestCode;
		fragment.startActivityForResult(pickPictureIntent(fragment.getContext()), requestCode);
	}

	public void pickPhoto(Activity activity, int requestCode) {
		requestCode_TakePhoto = -1;
		requestCode_PickPhoto = requestCode;
		activity.startActivityForResult(pickPictureIntent(activity), requestCode);
	}


	public void takePhoto(Fragment fragment, int requestCode) {
		requestCode_TakePhoto = requestCode;
		requestCode_PickPhoto = -1;
		fragment.startActivityForResult(takePictureIntent(fragment.getContext()), requestCode_TakePhoto);
	}

	public void takePhoto(Activity activity, int requestCode) {
		requestCode_TakePhoto = requestCode;
		requestCode_PickPhoto = -1;
		activity.startActivityForResult(takePictureIntent(activity), requestCode_TakePhoto);
	}

	public Uri getUri(Intent data) {
		if (takePickureUri != null) {
			return takePickureUri;
		}
		return data != null ? data.getData() : null;
	}

}
