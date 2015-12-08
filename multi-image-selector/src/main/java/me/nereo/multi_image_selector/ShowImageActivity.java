package me.nereo.multi_image_selector;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import it.sephiroth.android.library.imagezoom.ImageViewTouch.OnImageViewTouchSingleTapListener;
import me.nereo.multi_image_selector.adapter.ImagePagerAdapter;
import me.nereo.multi_image_selector.view.HackyViewPager;

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
	private Map<String, Bitmap> mapBitmap;

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

		pager = findById(R.id.pager);

		mImagePagerAdapter = new ImagePagerAdapter(this, mAllImageUrl, mapBitmap, mSingleTapListener);
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

		mPageCheckMask = findById(R.id.pager_check_mask);
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
