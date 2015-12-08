package me.nereo.multi_image_selector.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import me.nereo.multi_image_selector.R;

/**
 * Created by yangfeng on 15-12-8.
 */
public class ImagePagerAdapter extends PagerAdapter {
    private ArrayList<String> mAllImages;
    private Map<String, Bitmap> mapBitmap;

    private LayoutInflater inflater;
    private ImageViewTouch.OnImageViewTouchSingleTapListener mSingleTapListener;

    public ImagePagerAdapter(Context context, ArrayList<String> allImages, Map<String, Bitmap> mapBitmap,
                             ImageViewTouch.OnImageViewTouchSingleTapListener singleTapListener) {
        this.mAllImages = allImages;
        this.mapBitmap = mapBitmap;
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

        showImage(imageUri, imageViewTouchItem, handler, mapBitmap,
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

    public static void showImage(final String imageUri, final ImageViewTouch imageViewTouchItem,
                                 final Handler handler, final Map<String, Bitmap> mapBitmap,
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
}

