package me.nereo.multi_image_selector;

/**
 * Created by yangfeng on 15/12/6.
 */
public class ImagePickerConstants {
    /** 最大图片选择次数，int类型 */
    public static final String EXTRA_SELECT_COUNT = "max_select_count";
    /** default max selection count **/
    public static final int DEFAULT_MAX_COUNT = 9;

    /** 图片选择模式，int类型 */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";
    /** 是否显示相机，boolean类型 */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /** 默认选择的数据集 */
//    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_result";
    /** 可选择/预览的数据集 */
    public static final String EXTRA_All_SOURCE_LIST = "all_source";

    /** 可预览的默认当前数据页,int类型 */
    public static final String EXTRA_PREVIEW_INDEX = "preview_index";
    public static final int DEFAULT_PREVIEW_INDEX = 0;

    /** 单选 */
    public static final int MODE_SINGLE = 0;
    /** 多选 */
    public static final int MODE_MULTI = 1;


    public static final int REQUEST_IMAGE = 2;
//    public static final String EXTRA_RESULT = "select_result";
}
