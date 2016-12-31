package com.example.edittagview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.alisultan.edittagview.EditTagView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "MainActivity";

    @BindView(R.id.editTagView)
    EditTagView editTagView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        /** Adds listener **/
        editTagView.setEditTagViewListener(new EditTagView.EditTagViewListener() {
            @Override
            public void onTagCreated(View v) {
                Log.d(TAG, "onTagCreated");
            }

            @Override
            public void onTagAdded(int index) {
                Log.d(TAG, "onTagAdded " + index);
            }

            @Override
            public void onTagRemoved(int index) {
                Log.d(TAG, "onTagRemoved " + index);
            }
        });

        /** To inflate custom layout for your tag. **/
//        editTagView.inflateTagCustomLayout(TAG_LAYOUT_ID, TAG_TEXTVIEW_ID, TAG_CROSSBUTTON_ID);

        /** Add tags to editTagView. **/
//        ArrayList<String> tagList = new ArrayList<String>();
//
//        tagList.add("tag_1");
//        tagList.add("tag_2");
//        tagList.add("tag_3");
//
//        editTagView.addTagsList(tagList);
    }
}
