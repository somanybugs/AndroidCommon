package lhg.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import lhg.common.activity.PicturesBrowser;
import lhg.common.R;
import lhg.common.utils.Utils;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

public class ImagesGridView extends HGGridLayout  {
	RequestOptions displayImageOptionsBig = new RequestOptions().dontTransform().placeholder(R.drawable.pcm_image_loading).error(R.drawable.pcm_image_error);
	RequestOptions displayImageOptionsSmall = new RequestOptions().dontTransform().centerCrop().placeholder(R.drawable.pcm_image_loading).error(R.drawable.pcm_image_error);
	List<SoftReference<View>> sItemViewsCache = new ArrayList<>();

	List imageUrls = new ArrayList();
	List thumbUrls = new ArrayList();

	float itemHeightScale = 1;
	private OnItemClickListener mOnItemClickListener;

	public ImagesGridView(Context context) {
		this(context, null);
	}

	public ImagesGridView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ImagesGridView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setOnItemClickListener((parentView, position) -> ImagesGridView.this.onItemClick(parentView, position));
	}

	//////////////////////////update items

	protected int getItemCount() {
		return imageCount();
	}

	protected void updateItemView(int index) {
		RatioFrameLayout layout = (RatioFrameLayout) getChildAt(index);
		ImageView imageView = (ImageView) layout.getChildAt(0);
		imageView.setImageDrawable(null);
		layout.setHeightRatio(itemHeightScale);

		Object url = null;
		if (!Utils.isListEmpty(thumbUrls)) {
			url = thumbUrls.get(index);
		} else {
			url = imageUrls.get(index);
		}
		Glide.with(this).load(url).apply(
				imageCount() == 1 ? displayImageOptionsBig : displayImageOptionsSmall
		).into(imageView);
	}

	///////////////////////////

	public void setImageUrls(List pImageUrls, List pThumbUrls) {
		imageUrls.clear();
		if (pImageUrls != null) {
			imageUrls.addAll(pImageUrls);
		}
		thumbUrls.clear();
		if (pThumbUrls != null) {
			thumbUrls.addAll(pThumbUrls);
		}
		updateItemViews();
	}

	private void updateItemViews() {
		int itemCount = getItemCount();
		while (getChildCount() < itemCount) {
			addView(obtainItemView());
		}
		if (getChildCount() > itemCount) {
			for (int i = itemCount; i < getChildCount(); i++) {
				recyleItemView(getChildAt(i));
			}
			removeViews(itemCount, getChildCount() - itemCount);
		}
		for (int i = 0; i < itemCount; i++) {
			updateItemView(i);
		}
//		invalidate();
//		requestLayout();
	}

	protected void recyleItemView(View view) {
		view.setOnClickListener(null);
		sItemViewsCache.add(new SoftReference<View>(view));
	}

	protected View obtainItemView() {
		if (sItemViewsCache.size() > 5) {
			//清理一下空值
			for (int i = sItemViewsCache.size() - 1; i >= 0; i--) {
				if (sItemViewsCache.get(i).get() == null) {
					sItemViewsCache.remove(i);
				}
			}
		}
		RatioFrameLayout layout = null;
		if (!sItemViewsCache.isEmpty()) {
			int lastIndex = sItemViewsCache.size() - 1;
			layout = (RatioFrameLayout) sItemViewsCache.get(lastIndex).get();
			sItemViewsCache.remove(lastIndex);
		}
		if (layout == null) {
			layout = new RatioFrameLayout(getContext());
			ImageView imageView = new ImageView(getContext());
			imageView.setBackgroundResource(R.drawable.pcm_ic_menuitem);
			layout.addView(imageView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
		ImageView imageView = (ImageView) layout.getChildAt(0);
		imageView.setScaleType(getScaleType());
		layout.setHeightRatio(getItemHeightScale());
		layout.setOnClickListener(v -> ImagesGridView.this.onClick(v));
		return layout;
	}

	int hitTest(View child) {
		for (int i = 0, size = getChildCount(); i < size; i++) {
			if (child == getChildAt(i)) {
				return i;
			}
		}
		return -1;
	}

	public OnItemClickListener getOnItemClickListener() {
		return mOnItemClickListener;
	}

	public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
		this.mOnItemClickListener = onItemClickListener;
	}

	public void onClick(View view) {
		int index = hitTest(view);
		if (index >= 0) {
			if (mOnItemClickListener != null) {
				mOnItemClickListener.onItemClick(this, index);
			}
		}
	}

	//////////////////////
	public ScaleType getScaleType() {
		return ScaleType.CENTER_CROP;
	}

	public void setItemHeightScale(float scale) {
		this.itemHeightScale = scale;
	}

	public float getItemHeightScale() {
		return itemHeightScale;
	}

	public int imageCount() {
		return imageUrls.size();
	}

	public List getImageUrls() {
		return imageUrls;
	}

	/////////////////////
	/*
	 */
	public void onItemClick(HGGridLayout parentView, int position) {
		getContext().startActivity(PicturesBrowser.makeIntent(getContext(), createBrowserParam(position)));
	}

	public PicturesBrowser.Param createBrowserParam(int position) {
		PicturesBrowser.Param param = new PicturesBrowser.Param();
		param.showDeleteBtn = false;
		param.showContextMenu = true;
		param.files = new ArrayList(imageUrls);
		param.index = position;
		return param;
	}

	public interface OnItemClickListener {
		void onItemClick(HGGridLayout parentView, int position);
	}
}
