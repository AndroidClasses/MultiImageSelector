package me.nereo.multi_image_selector.adapter;

import me.nereo.multi_image_selector.bean.Image;

/**
 * Created by yangfeng on 15/10/2.
 */
public interface RecyclerClickListener {
    void onElementClick(ImageGridAdapter.ImageAdapterViewHolder holder, Image image);
}
