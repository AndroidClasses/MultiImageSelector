package me.nereo.multiimageselector;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.OnClick;
import me.nereo.multi_image_selector.ImagePickerConstants;

public class MainActivity extends AppActivity {

    @Bind(R.id.result) TextView mResultText;
    @Bind(R.id.choice_mode) RadioGroup mChoiceMode;
    @Bind(R.id.show_camera) RadioGroup mShowCamera;
    @Bind(R.id.request_num) EditText mRequestNum;

    @OnClick(R.id.button)
    void onPickButtonClick(View v) {
        int selectedMode = MultiImageSelectorActivity.MODE_MULTI;

        if (mChoiceMode.getCheckedRadioButtonId() == R.id.single){
            selectedMode = MultiImageSelectorActivity.MODE_SINGLE;
        } else {
            selectedMode = MultiImageSelectorActivity.MODE_MULTI;
        }

        boolean showCamera = mShowCamera.getCheckedRadioButtonId() == R.id.show;

        int maxNum = ImagePickerConstants.DEFAULT_MAX_COUNT;
        if(!TextUtils.isEmpty(mRequestNum.getText())){
            maxNum = Integer.valueOf(mRequestNum.getText().toString());
        }

        Intent intent = new Intent(MainActivity.this, MultiImageSelectorActivity.class);
        // 是否显示拍摄图片
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, showCamera);
        // 最大可选择图片数量
        intent.putExtra(ImagePickerConstants.EXTRA_SELECT_COUNT, maxNum);
        // 选择模式
        intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, selectedMode);
        // 默认选择
        if(mSelectPath != null && mSelectPath.size() > 0){
            intent.putExtra(ImagePickerConstants.EXTRA_DEFAULT_SELECTED_LIST, mSelectPath);
        }
        startActivityForResult(intent, ImagePickerConstants.REQUEST_IMAGE);
    }

    private ArrayList<String> mSelectPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mChoiceMode.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                if (checkedId == R.id.multi) {
                    mRequestNum.setEnabled(true);
                } else {
                    mRequestNum.setEnabled(false);
                    mRequestNum.setText("");
                }
            }
        });
    }

    @Override
    protected boolean isHomeAsUpEnabled() {
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == ImagePickerConstants.REQUEST_IMAGE){
            if(resultCode == RESULT_OK){
                mSelectPath = data.getStringArrayListExtra(ImagePickerConstants.EXTRA_RESULT);
                StringBuilder sb = new StringBuilder();
                for(String p: mSelectPath){
                    sb.append(p);
                    sb.append("\n");
                }
                mResultText.setText(sb.toString());
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
