package me.nereo.multi_image_selector.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.R;
import me.nereo.multi_image_selector.bean.Image;

/**
 * 图片Adapter
 * Created by Nereo on 2015/4/7.
 */
public class ImageGridAdapter extends RecyclerView.Adapter<ImageGridAdapter.ImageAdapterViewHolder> {

    private static final int TYPE_CAMERA = 0;
    private static final int TYPE_NORMAL = 1;

    private Context mContext;

    private LayoutInflater mInflater;
    private boolean showCamera = true;
    private boolean showSelectIndicator = true;

    private List<Image> mImages = new ArrayList<>();
    private List<Image> mSelectedImages = new ArrayList<>();

    private int mItemSize;
    private LayoutParams mItemLayoutParams;

    public ImageGridAdapter(Context context, boolean showCamera){
        mContext = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.showCamera = showCamera;
        mItemLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
    }
    /**
     * 显示选择指示器
     * @param b
     */
    public void showSelectIndicator(boolean b) {
        showSelectIndicator = b;
    }

    public void setShowCamera(boolean b){
        if(showCamera == b) return;

        showCamera = b;
        notifyDataSetChanged();
    }

    public boolean isShowCamera(){
        return showCamera;
    }

    /**
     * 选择某个图片，改变选择状态
     * @param image
     */
    public void select(Image image) {
        if(mSelectedImages.contains(image)){
            mSelectedImages.remove(image);
        }else{
            mSelectedImages.add(image);
        }
        notifyDataSetChanged();
    }

    /**
     * 通过图片路径设置默认选择
     * @param resultList
     */
    public void setDefaultSelected(ArrayList<String> resultList) {
        for(String path : resultList){
            Image image = getImageByPath(path);
            if(image != null){
                mSelectedImages.add(image);
            }
        }
        if(mSelectedImages.size() > 0){
            notifyDataSetChanged();
        }
    }

    private Image getImageByPath(String path){
        if(mImages != null && mImages.size()>0){
            for(Image image : mImages){
                if(image.path.equalsIgnoreCase(path)){
                    return image;
                }
            }
        }
        return null;
    }

    /**
     * 设置数据集
     * @param images
     */
    public void setData(List<Image> images) {
        mSelectedImages.clear();

        if(images != null && images.size()>0){
            mImages = images;
        }else{
            mImages.clear();
        }
        notifyDataSetChanged();
    }

    /**
     * 重置每个Column的Size
     * @param columnWidth
     */
    public void setItemSize(int columnWidth) {

        if(mItemSize == columnWidth){
            return;
        }

        mItemSize = columnWidth;

        mItemLayoutParams = new LayoutParams(mItemSize, mItemSize);

        notifyDataSetChanged();
    }

//    @Override
//    public int getViewTypeCount() {
//        return 2;
//    }

    @Override
    public int getItemViewType(int position) {
        if(isShowCamera()){
            return position == 0 ? TYPE_CAMERA : TYPE_NORMAL;
        }
        return TYPE_NORMAL;
    }

//    @Override
//    public int getCount() {
//        return isShowCamera() ? mImages.size()+1 : mImages.size();
//    }
//
//    @Override
    public Image getItem(int i) {
        if(isShowCamera()){
            if(i == 0){
                return null;
            }
            return mImages.get(i-1);
        }else{
            return mImages.get(i);
        }
    }
//
//    @Override
//    public long getItemId(int i) {
//        return i;
//    }
//
//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//
//        int type = getItemViewType(i);
//        if(type == TYPE_CAMERA){
//            view = mInflater.inflate(R.layout.list_item_camera, viewGroup, false);
//            view.setTag(null);
//        }else if(type == TYPE_NORMAL){
//            ImageAdapterViewHolder holde;
//            if(view == null){
//                view = mInflater.inflate(R.layout.list_item_image, viewGroup, false);
//                holde = new ImageAdapterViewHolder(view);
//            }else{
//                holde = (ImageAdapterViewHolder) view.getTag();
//                if(holde == null){
//                    view = mInflater.inflate(R.layout.list_item_image, viewGroup, false);
//                    holde = new ImageAdapterViewHolder(view);
//                }
//            }
//            if(holde != null) {
//                holde.bindData(getItem(i));
//            }
//        }
//
//        /** Fixed View Size */
//        GridView.LayoutParams lp = (GridView.LayoutParams) view.getLayoutParams();
//        if(lp.height != mItemSize){
//            view.setLayoutParams(mItemLayoutParams);
//        }
//
//        return view;
//    }

    public class ImageAdapterViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        ImageView indicator;
        View mask;

        ImageAdapterViewHolder(View view) {
            super(view);

            view.setLayoutParams(mItemLayoutParams);

            image = (ImageView) view.findViewById(R.id.image);
            indicator = (ImageView) view.findViewById(R.id.checkmark);
            mask = view.findViewById(R.id.mask);
            view.setTag(this);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != recyclerListener) {
                        Image image = getDataItem(getAdapterPosition());
                        performClicked(mHolder, image);
                    }
                }
            });
        }

        void bindData(final Image data){
            if(data == null) return;
            // 处理单选和多选状态
            if(showSelectIndicator){
                indicator.setVisibility(View.VISIBLE);
                if(mSelectedImages.contains(data)){
                    // 设置选中状态
                    indicator.setImageResource(R.drawable.btn_selected);
                    mask.setVisibility(View.VISIBLE);
                }else{
                    // 未选择
                    indicator.setImageResource(R.drawable.btn_unselected);
                    mask.setVisibility(View.GONE);
                }
            }else{
                indicator.setVisibility(View.GONE);
            }
            File imageFile = new File(data.path);

            if(mItemSize > 0) {
                // 显示图片
                Picasso.with(mContext)
                        .load(imageFile)
                        .placeholder(R.drawable.default_error)
                                //.error(R.drawable.default_error)
                        .resize(mItemSize, mItemSize)
                        .centerCrop()
                        .into(image);
            }
        }
    }

    protected ImageAdapterViewHolder mHolder;
    @Override
    public ImageAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//        View rootView = LayoutInflater.from(mContext).inflate(mItemLayoutId, parent, false);
//        mHolder = new ImageAdapterViewHolder(rootView);

        if (viewType == TYPE_CAMERA){
            View rootView = mInflater.inflate(R.layout.list_item_camera, viewGroup, false);
            mHolder = new ImageAdapterViewHolder(rootView);
        } else if (viewType == TYPE_NORMAL){
            View rootView = mInflater.inflate(R.layout.list_item_image, viewGroup, false);
            mHolder = new ImageAdapterViewHolder(rootView);
        } else {
            // unknown view type, go to the hell
            mHolder = null;
        }

        return mHolder;
    }

    protected void performClicked(ImageAdapterViewHolder holder, Image image) {
        if (null != recyclerListener) {
            recyclerListener.onElementClick(holder, image);
        }
    }

    private Image getDataItem(int position) {
        if (TYPE_NORMAL == getItemViewType(position)) {
            if (isShowCamera()) {
                return mImages.get(position -1);
            } else {
                return mImages.get(position);
            }
        } else {
            // need not to bind for camera type or others.
            return null;
        }
    }
    @Override
    public void onBindViewHolder(ImageAdapterViewHolder holder, int position) {
        holder.bindData(getDataItem(position));
    }

    @Override
    public int getItemCount() {
        return isShowCamera() ? 1 + mImages.size() : mImages.size();
    }

    private RecyclerClickListener recyclerListener;
    public void setOnItemClickListener(RecyclerClickListener recyclerListener) {
        this.recyclerListener = recyclerListener;
    }
}
