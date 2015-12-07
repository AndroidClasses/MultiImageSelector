package me.nereo.multiimageselector;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.util.ArrayList;

import me.nereo.multi_image_selector.ImagePickerConstants;
import me.nereo.multi_image_selector.MultiImageSelectorFragment;

/**
 * 多图选择
 * Created by Nereo on 2015/4/7.
 */
public class MultiImageSelectorActivity extends AppActivity implements MultiImageSelectorFragment.Callback {
    void onSelectionSubmit() {
        if(resultList != null && resultList.size() > 0){
            // 返回已选择的图片数据
            Intent data = new Intent();
            data.putStringArrayListExtra(ImagePickerConstants.EXTRA_RESULT, resultList);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private ArrayList<String> resultList = new ArrayList<>();
    private int mDefaultCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_default);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Intent intent = getIntent();
        mDefaultCount = intent.getIntExtra(ImagePickerConstants.EXTRA_SELECT_COUNT,
                ImagePickerConstants.DEFAULT_MAX_COUNT);
        int mode = intent.getIntExtra(ImagePickerConstants.EXTRA_SELECT_MODE, ImagePickerConstants.MODE_MULTI);
        boolean isShow = intent.getBooleanExtra(ImagePickerConstants.EXTRA_SHOW_CAMERA, true);
        if (mode == ImagePickerConstants.MODE_MULTI && intent.hasExtra(ImagePickerConstants.EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(ImagePickerConstants.EXTRA_DEFAULT_SELECTED_LIST);
        }

        Bundle bundle = new Bundle();
        bundle.putInt(ImagePickerConstants.EXTRA_SELECT_COUNT, mDefaultCount);
        bundle.putInt(ImagePickerConstants.EXTRA_SELECT_MODE, mode);
        bundle.putBoolean(ImagePickerConstants.EXTRA_SHOW_CAMERA, isShow);
        bundle.putStringArrayList(ImagePickerConstants.EXTRA_DEFAULT_SELECTED_LIST, resultList);

        getSupportFragmentManager().beginTransaction()
                .add(R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
                .commit();

        refreshWithResultUi();
    }

    private String getSelectedResult(boolean haveResult) {
        if(haveResult){
            return getString(R.string.picker_menu, resultList.size(), mDefaultCount);
        } else {
            return getString(R.string.picker_menu_empty);
        }
    }

    @Override
    public void onSingleImageSelected(String path) {
        Intent data = new Intent();
        resultList.add(path);
        data.putStringArrayListExtra(ImagePickerConstants.EXTRA_RESULT, resultList);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onImageSelected(String path) {
        if(null != resultList && !resultList.contains(path)) {
            resultList.add(path);
        }
        refreshWithResultUi();
    }

    @Override
    public void onImageUnselected(String path) {
        if (null != resultList && resultList.contains(path)) {
            resultList.remove(path);
        }
        refreshWithResultUi();
    }

    @Override
    public void onCameraShot(File imageFile) {
        if(imageFile != null) {
            Intent data = new Intent();
            resultList.add(imageFile.getAbsolutePath());
            data.putStringArrayListExtra(ImagePickerConstants.EXTRA_RESULT, resultList);
            setResult(RESULT_OK, data);
            finish();
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
        boolean haveResult = resultList != null && !resultList.isEmpty();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ImagePickerConstants.REQUEST_IMAGE){ // todo: apply Bus system and redesign these.
            if(resultCode == RESULT_OK){
                // forward the result from preview activity
                onSelectionSubmit();
            }
        }
    }

    private void refreshWithResultUi() {
        invalidateOptionsMenu();
    }
}
