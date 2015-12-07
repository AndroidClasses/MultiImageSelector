package me.nereo.multi_image_selector;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.bean.Folder;
import me.nereo.multi_image_selector.bean.Image;

/**
 * Created by yangfeng on 15-12-7.
 */

public class ImagePickerSelection {
    // 结果数据
    private ArrayList<String> resultList = new ArrayList<>();
    // 文件夹数据
    private ArrayList<Folder> mResultFolder = new ArrayList<>();

    private static ImagePickerSelection _instance;
    private ImagePickerSelection() {
    }

    public static ImagePickerSelection getInstance() {
        if (null == _instance) {
            _instance = new ImagePickerSelection();
        }
        return _instance;
    }

    public void setSourceList(ArrayList<String> tmp) {
        resultList.clear();
        mResultFolder.clear();
        if(tmp != null && !tmp.isEmpty()) {
            resultList = tmp;
        }
    }

    public ArrayList<String> getList() {
        return resultList;
    }

    public int toggleSelection(String path, int maxCount) {
        int result;
        if (resultList.contains(path)) {
            resultList.remove(path);
            result = -1;
        } else {
            if (getSelectedCount() < maxCount) {
                resultList.add(path);
                result = 1;
            } else {
                result = 0;
            }
        }
        return result;
    }

    public int getSelectedCount() {
        int count = 0;
        if (null == resultList) {
            count = 0;
        } else {
            count = resultList.size();
        }
        return count;
    }

    public void parseFolder(Image image, String path) {
        // 获取文件夹名称
        File imageFile = new File(path);
        File folderFile = imageFile.getParentFile();
        Folder folder = new Folder();
        folder.name = folderFile.getName();
        folder.path = folderFile.getAbsolutePath();
        folder.cover = image;
        if (!mResultFolder.contains(folder)) {
            List<Image> imageList = new ArrayList<>();
            imageList.add(image);
            folder.images = imageList;
            mResultFolder.add(folder);
        } else {
            // 更新
            Folder f = mResultFolder.get(mResultFolder.indexOf(folder));
            f.images.add(image);
        }
    }

    public List<Folder> getFolderList() {
        return mResultFolder;
    }
}
