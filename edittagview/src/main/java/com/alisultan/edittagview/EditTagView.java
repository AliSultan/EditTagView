package com.alisultan.edittagview;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import org.apmem.tools.layouts.FlowLayout;

import java.util.ArrayList;

/**
 * Created by Ali on 12/3/2016.
 */

public class EditTagView extends RelativeLayout {

    private EditText editText;
    private FlowLayout flowLayout_container;
    private ScrollView container;
    private ArrayList<String> tagsList = new ArrayList<>();
    private boolean addTag = false;

    private int tagColor;
    private int tagTextColor;
    private int textColor;
    private int textColorHint;
    private String hint;
    private String createTagOnCharacter;
    private boolean showCross;

    private int customLayout = 0;
    private int textViewID;
    private int crossID;

    public interface EditTagViewListener {
        void onTagCreated(View v);

        void onTagAdded(int index);

        void onTagRemoved(int index);
    }

    private EditTagViewListener editTagViewListener;

    public EditTagView(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.tagViewAttributes, 0, 0);
        hint = a.getString(R.styleable.tagViewAttributes_hint);

        tagColor = a.getColor(R.styleable.tagViewAttributes_tagColor,
                getResources().getColor(android.R.color.holo_orange_dark));

        tagTextColor = a.getColor(R.styleable.tagViewAttributes_tagTextColor,
                getResources().getColor(android.R.color.white));

        textColor = a.getColor(R.styleable.tagViewAttributes_textColor,
                getResources().getColor(android.R.color.black));

        textColorHint = a.getColor(R.styleable.tagViewAttributes_textColorHint,
                getResources().getColor(android.R.color.darker_gray));

        createTagOnCharacter = a.getString(R.styleable.tagViewAttributes_createTagOnCharacter);
        if (createTagOnCharacter == null || createTagOnCharacter.isEmpty())
            createTagOnCharacter = " ";

        showCross = a.getBoolean(R.styleable.tagViewAttributes_showCross, true);

        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.edittagview_layout, this, true);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        flowLayout_container = (FlowLayout) findViewById(R.id.flowlayout_container);
        editText = (EditText) findViewById(R.id.editText);

        flowLayout_container.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.requestFocus();
                ((InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE)).showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        this.setClickable(true);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.requestFocus();
                ((InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE)).showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        });

        editText.addTextChangedListener(textWatcher);
        editText.setFilters(new InputFilter[]{filter});
        editText.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (((EditText) v).getText().length() == 0) {
                        removeTag(tagsList.size() - 1);
                        return true;
                    }
                }

//                if (keyCode == EditorInfo.IME_NULL && event.getAction() == KeyEvent.ACTION_UP) {
//                    if (((EditText) v).getText().length() != 0) {
//                        makeTag(((EditText) v).getText().toString());
//                        return true;
//                    }
//                }

                return false;
            }
        });

        editText.setHintTextColor(textColorHint);
        editText.setTextColor(textColor);
        editText.setHint(hint);
    }

    private InputFilter filter = new InputFilter() {
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {

            //if (source.toString().equalsIgnoreCase(" ") || source.toString().equalsIgnoreCase("\n")) {
            if (source.toString().equalsIgnoreCase(createTagOnCharacter)) {

                //  if (ValidationUtils.isValidEmail(dest.toString().trim())) {
                addTag = true;
                // if (source.toString().equalsIgnoreCase("\n"))
                //} else {
                //  return "";
                //}
            }

            return source;
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (addTag) {
                addTag = false;
                addTag(s.toString().substring(0, s.length() - 1));
            }
        }
    };

    public void setEditTagViewListener(EditTagViewListener listener) {
        this.editTagViewListener = listener;
    }

    void addTag(String tagString) {

        tagsList.add(tagString);

        View tagView = createTagView(tagString);

        if (editTagViewListener != null)
            editTagViewListener.onTagCreated(tagView);

        int index = flowLayout_container.getChildCount() - 1;

        flowLayout_container.addView(tagView, index);

        if (editTagViewListener != null)
            editTagViewListener.onTagAdded(index);

        if (!tagsList.isEmpty()) {
            editText.setHint("");
        }

        editText.setText("");
    }

    public void removeTag(int index) {
        if (index >= 0) {
            tagsList.remove(index);
            flowLayout_container.removeViewAt(index);
            if (tagsList.isEmpty()) {
                editText.setHint(hint);
            }

            if (editTagViewListener != null)
                editTagViewListener.onTagRemoved(index);
        }
    }

    public void removeTag(String tagString) {
        if (!tagString.isEmpty()) {

            int index = tagsList.indexOf(tagString);

            flowLayout_container.removeViewAt(index);
            tagsList.remove(tagString);
            if (tagsList.isEmpty()) {
                editText.setHint(hint);
            }

            if (editTagViewListener != null)
                editTagViewListener.onTagRemoved(index);
        }
    }

    public ArrayList<String> getTagsList() {

        if (editText.length() > 0) {
            addTag(editText.getText().toString());
        }

        return tagsList;
    }

    public void addTagsList(ArrayList<String> tagsList) {

        for (String tagString : tagsList) {
            addTag(tagString);
        }
    }

    public int getTagColor() {
        return tagColor;
    }

    public void setTagColor(@IdRes int tagColor) {
        this.tagColor = tagColor;
    }

    public int getTagTextColor() {
        return tagTextColor;
    }

    public void setTagTextColor(int tagTextColor) {
        this.tagTextColor = tagTextColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public int getTextColorHint() {
        return textColorHint;
    }

    public void setTextColorHint(int textColorHint) {
        this.textColorHint = textColorHint;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getCreateTagOnCharacter() {
        return createTagOnCharacter;
    }

    public void setCreateTagOnCharacter(String createTagOnCharacter) {
        this.createTagOnCharacter = createTagOnCharacter;
    }

    public boolean isShowCross() {
        return showCross;
    }

    public void setShowCross(boolean showCross) {
        this.showCross = showCross;
    }

    public void focusView() {
        editText.requestFocus();
    }

    private View createTagView(String tagString) {

        LayoutInflater lf = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        final View tagView = lf.inflate((customLayout != 0) ? customLayout : R.layout.tag_layout, null);
        final TextView textView = (TextView) tagView.findViewById((customLayout != 0) ? textViewID : R.id.text_tag);
        final View cross_btn = tagView.findViewById((customLayout != 0) ? crossID : R.id.ic_clear_tag);

        android.view.ViewTreeObserver viewTreeObserver = tagView.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {

            viewTreeObserver.addOnGlobalLayoutListener(new android.view.ViewTreeObserver.OnGlobalLayoutListener() {

                @Override
                public void onGlobalLayout() {

                    if (cross_btn != null) {
                        textView.setMaxWidth(flowLayout_container.getWidth() - cross_btn.getMeasuredWidth() - getPaddingRight());

                        tagView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                }
            });
        }

        if (customLayout == 0) {
            textView.setTextColor(tagTextColor);
            textView.setBackgroundColor(tagColor);
        }
        textView.setText(tagString);

        if (showCross) {
            cross_btn.setVisibility(VISIBLE);
            cross_btn.setClickable(true);
            cross_btn.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeTag(flowLayout_container.indexOfChild((LinearLayout) v.getParent()));
                    editText.requestFocus();
                }
            });

        } else {
            cross_btn.setVisibility(GONE);
        }

        return tagView;
    }

    public void inflateTagCustomLayout(@LayoutRes int layout, @IdRes int textViewID, @IdRes int crossID) {
        this.customLayout = layout;
        this.textViewID = textViewID;
        this.crossID = crossID;
    }

    public int getTagCount() {
        return tagsList.size();
    }
}
