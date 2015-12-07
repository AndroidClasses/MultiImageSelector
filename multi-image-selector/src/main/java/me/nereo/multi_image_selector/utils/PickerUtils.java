package me.nereo.multi_image_selector.utils;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.ImagePickerConstants;
import me.nereo.multi_image_selector.bean.Image;

/**
 * Created by yangfeng on 15-12-7.
 */
public class PickerUtils {
    public static void startPreview(Activity activity, List<Image> selectedList,
                                                     int maxSelectedCount, List<Image> allList,
                                                     int previewIndex) {
        ArrayList<String> selection = new ArrayList<String>();
        for (Image image : selectedList) {
            selection.add(image.path);
        }
        ArrayList<String> all = new ArrayList<String>();
        for (Image image : allList) {
            all.add(image.path);
        }

        startPreviewActivityForResult(activity, selection, maxSelectedCount, all, previewIndex);
    }
    public static void startPreviewActivityForResult(Activity activity, ArrayList<String> selectedList,
                                                     int maxSelectedCount, ArrayList<String> allList,
                                                     int previewIndex) {
        Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(ImagePickerConstants.EXTRA_SELECT_COUNT, maxSelectedCount);
        intent.putExtra(ImagePickerConstants.EXTRA_DEFAULT_SELECTED_LIST, selectedList);
        intent.putExtra(ImagePickerConstants.EXTRA_All_SOURCE_LIST, allList);
        intent.putExtra(ImagePickerConstants.EXTRA_PREVIEW_INDEX, previewIndex);
        intent.setPackage(activity.getPackageName());
        try {
            activity.startActivityForResult(intent, ImagePickerConstants.REQUEST_IMAGE);
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }
}
