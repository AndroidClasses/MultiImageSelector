package me.nereo.multiimageselector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchSingleTapListener;
import me.nereo.multi_image_selector.ImagePickerConstants;
import me.nereo.multi_image_selector.ImagePickerSelection;

/**
 * @brief 图片展示页面，支持多点触摸放大缩小
 * 
 * 
 * 
 */
public class ShowImageActivity extends AppActivity {
	public static final String KEY_PIC = "show_pic";

	private HackyViewPager pager;
	private CheckBox mPageCheckMask;

	private Bitmap mBitmap;

//	private ArrayList<String> mSelectedUrl = new ArrayList<String>();
	private static Map<String, Bitmap> mapBitmap;

	private int mDefaultCount;

	private ImagePagerAdapter mImagePagerAdapter;

	public int screenWidth;
	public int screenHeight;
	public int statusHeight;
	public int newScreenHeight;

	private ArrayList<String> mAllImageUrl = new ArrayList<String>();
	private int mDefaultPreviewIndex;

	private ImagePickerSelection mImageSelection;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_image);

		init();
	}

	private void init() {
		initImageLoader(this);

		mImageSelection = ImagePickerSelection.getInstance();

		if (null == mapBitmap) {
			mapBitmap = new HashMap<String, Bitmap>();
		}

		screenWidth = getWindowManager().getDefaultDisplay().getWidth();
		screenHeight = getWindowManager().getDefaultDisplay().getHeight();
		statusHeight = getStatusBarHeight(ShowImageActivity.this);
		newScreenHeight = screenHeight - statusHeight;

		Intent intent = getIntent();
		mDefaultCount = intent.getIntExtra(ImagePickerConstants.EXTRA_SELECT_COUNT,
				ImagePickerConstants.DEFAULT_MAX_COUNT);

		mDefaultPreviewIndex = intent.getIntExtra(ImagePickerConstants.EXTRA_PREVIEW_INDEX, ImagePickerConstants.DEFAULT_PREVIEW_INDEX);

//		if (intent.hasExtra(ImagePickerConstants.EXTRA_DEFAULT_SELECTED_LIST)) {
//			ArrayList<String> resultList = intent.getStringArrayListExtra(ImagePickerConstants.EXTRA_DEFAULT_SELECTED_LIST);
//			if (null != resultList) {
//				mSelectedUrl.addAll(resultList);
//			}
//		} else {
			Bundle bd = intent.getExtras();
			if (bd != null) {
				String imageUrl = bd.getString(KEY_PIC);
				if (!TextUtils.isEmpty(imageUrl)) {
//					mSelectedUrl.add(imageUrl);
					mImageSelection.setSelected(imageUrl);
				}
			}
//		}

		if (intent.hasExtra(ImagePickerConstants.EXTRA_All_SOURCE_LIST)) {
			ArrayList<String> allList = intent.getStringArrayListExtra(ImagePickerConstants.EXTRA_All_SOURCE_LIST);
			if (null == allList || allList.isEmpty()) {
				mAllImageUrl.addAll(mImageSelection.getList());
			} else {
				mAllImageUrl = allList;
			}
		}
	}

	@Override
	protected void onPostCreate(Bundle saveInstanceState) {
		super.onPostCreate(saveInstanceState);

		pager = (HackyViewPager) findViewById(R.id.pager);

		mImagePagerAdapter = new ImagePagerAdapter(this, mAllImageUrl, mSingleTapListener);
		pager.setAdapter(mImagePagerAdapter);
		pager.setCurrentItem(mDefaultPreviewIndex);

		// 设置滑动监听器
		pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				refreshWithResultUi();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		mPageCheckMask = (CheckBox)findViewById(R.id.pager_check_mask);
		mPageCheckMask.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				boolean isChecked = mPageCheckMask.isChecked();
				int currentItem = pager.getCurrentItem();
				String currentUrl = mAllImageUrl.get(currentItem);
				if (isChecked) {
					mImageSelection.setSelected(currentUrl);
//					mSelectedUrl.add(currentUrl);
				} else {
					mImageSelection.clearSelected(currentUrl);
//					mSelectedUrl.remove(currentUrl);
				}
				refreshWithResultUi();
			}
		});

		refreshWithResultUi();
	}

	// 获取状态栏的高度
	public static int getStatusBarHeight(Context context) {
		int x = 0;
		int statusBarHeight = 0;
		try {
			Class<?> c = Class.forName("com.android.internal.R$dimen");
			Object obj = c.newInstance();
			java.lang.reflect.Field field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			statusBarHeight = context.getResources().getDimensionPixelSize(x);
			return statusBarHeight;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return statusBarHeight;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mBitmap != null) {
			mBitmap = null;
		}

		if (null != mapBitmap) {
			for (Entry<String, Bitmap> entry : mapBitmap.entrySet()) {
				if (entry.getValue() != null && !entry.getValue().isRecycled()) {
					entry.getValue().recycle();
				}
			}
			mapBitmap.clear();
			mapBitmap = null;
		}
	}

	private OnImageViewTouchSingleTapListener mSingleTapListener = new OnImageViewTouchSingleTapListener() {
		@Override
		public void onSingleTapConfirmed() {
			if (null != mAllImageUrl && mAllImageUrl.size() == 1) {
				finish();
			}
		}
	};

	public static class ImagePagerAdapter extends PagerAdapter {
		private ArrayList<String> mAllImages;
		private LayoutInflater inflater;
		private OnImageViewTouchSingleTapListener mSingleTapListener;

		ImagePagerAdapter(Context context, ArrayList<String> allImages, OnImageViewTouchSingleTapListener singleTapListener) {
			this.mAllImages = allImages;
			this.mSingleTapListener = singleTapListener;

			inflater = LayoutInflater.from(context);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}

		@Override
		public void finishUpdate(View container) {
		}

		@Override
		public int getCount() {
			return mAllImages.size();
		}

		@Override
		public Object instantiateItem(ViewGroup view, final int position) {
			final View imageLayout = inflater.inflate(R.layout.item_pager_image, null, false);
			final ImageViewTouch imageViewTouchItem = (ImageViewTouch) imageLayout.findViewById(R.id.image);
			final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
			final LinearLayout ll_process = (LinearLayout) imageLayout.findViewById(R.id.ll_process);
			final TextView tv_process = (TextView) imageLayout.findViewById(R.id.tv_process);

			if (null != mSingleTapListener) {
				imageViewTouchItem.setSingleTapListener(mSingleTapListener);
			}

			final Handler handler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					switch (msg.what) {
					case 0:
						// roundProgressBar.setVisibility(View.GONE);
						spinner.setVisibility(View.VISIBLE);
						ll_process.setVisibility(View.VISIBLE);

						break;
					default:
						break;
					}
				}
			};
			final String imageUri = Uri.fromFile(new File(mAllImages.get(position))).toString();

			showImage(imageUri, imageViewTouchItem, handler,
					spinner, ll_process, tv_process);

			((ViewPager) view).addView(imageLayout);
			return imageLayout;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view.equals(object);
		}

		@Override
		public void restoreState(Parcelable state, ClassLoader loader) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View container) {
		}
	}

	public static void showImage(final String imageUri, final ImageViewTouch imageViewTouchItem,
								 final Handler handler,
								 final ProgressBar spinner, final LinearLayout ll_process,
								 final TextView tv_process) {
		ImageLoader imageLoader = ImageLoader.getInstance();

		imageLoader.clearMemoryCache();
		System.gc();

		int transparentColorResId = android.R.color.transparent;
		DisplayImageOptions options = new DisplayImageOptions.Builder().
				showImageOnLoading(transparentColorResId).
				showImageForEmptyUri(transparentColorResId).
				showImageOnFail(transparentColorResId).
				cacheInMemory(false).cacheOnDisc(true)/*.displayer(new FadeInBitmapDisplayer(500))*/.build();

		imageLoader.displayImage(imageUri, imageViewTouchItem, options, new SimpleImageLoadingListener() {
			@Override
			public void onLoadingStarted(String imageUri, View view) {
				Message msg = new Message();
				msg.what = 0;
				handler.sendMessage(msg);
				ImageViewTouch image = (ImageViewTouch) view;
				image.setDoubleTapEnabled(false);
			}

			@Override
			public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
				String message = null;
				switch (failReason.getType()) {
					case IO_ERROR:
						message = "Input/Output error";
						break;
					case DECODING_ERROR:
						message = "Image can't be decoded";
						break;
					case NETWORK_DENIED:
						message = "Downloads are denied";
						break;
					case OUT_OF_MEMORY:
						message = "Out Of Memory error";
						break;
					case UNKNOWN:
						message = "Unknown error";
						break;
				}

				imageViewTouchItem.setBackgroundResource(R.drawable.pic_failed);
				spinner.setVisibility(View.GONE);
				ll_process.setVisibility(View.GONE);
			}

			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
				imageViewTouchItem.setBackgroundResource(android.R.color.black);
				spinner.setVisibility(View.GONE);
				ll_process.setVisibility(View.GONE);
				if (loadedImage != null && !loadedImage.isRecycled()) {
					if (mapBitmap != null && !mapBitmap.containsKey(imageUri)) {
						mapBitmap.put(imageUri, loadedImage);
					}

					ImageViewTouch image = (ImageViewTouch) view;
					image.setDoubleTapEnabled(true);
				}
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				// Empty implementation
			}
		}, new ImageLoadingProgressListener() {
			@Override
			public void onProgressUpdate(String imageUri, View view, int current, int total) {
				// 监听图片的加载进度
				if (total != 0) {
					int progress = (int) (((double) current / (double) total) * 100);
					if (current == 0)
						progress = 1;
					spinner.setProgress(progress);
					tv_process.setText(String.valueOf(progress));
				} else {
					if (spinner != null && tv_process != null) {
						spinner.setVisibility(View.GONE);
						tv_process.setVisibility(View.GONE);
					}
				}
			}
		});
	}
	public static void initImageLoader(Context context) {
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).
				threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory().
						discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO).
						memoryCache(new WeakMemoryCache()).writeDebugLogs().build();

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}

	private String getSelectedResult(boolean haveResult) {
		if(haveResult){
			return getString(R.string.picker_menu, mImageSelection.getSelectedCount(), mDefaultCount);
		} else {
			return getString(R.string.picker_menu_empty);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.menu_picker_action, menu);
		return true;
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// Set 'pick' menu item state depending on count
		MenuItem mPickMenuItem = menu.findItem(R.id.action_pick);
		boolean haveResult = mImageSelection.hasSelection();
		mPickMenuItem.setTitle(getSelectedResult(haveResult));
		mPickMenuItem.setEnabled(haveResult);

		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.action_pick) {
			onSelectionSubmit();
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	// todo: post event to notify inter-activities to close and show up the result.
	void onSelectionSubmit() {
		if(mImageSelection.hasSelection()){
			// 返回已选择的图片数据
			setResult(RESULT_OK);
			finish();
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		finish();
	}

	private void refreshWithResultUi() {
		invalidateOptionsMenu();
		setTitle((1 + pager.getCurrentItem()) + "/" + mAllImageUrl.size());

		if (null != mPageCheckMask) {
			String url = mAllImageUrl.get(pager.getCurrentItem());
			mPageCheckMask.setChecked(mImageSelection.wasSelected(url));
		}
	}
}
