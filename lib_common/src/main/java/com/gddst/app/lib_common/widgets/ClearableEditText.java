package com.gddst.app.lib_common.widgets;

import android.content.Context;
import android.graphics.drawable.Drawable;
import androidx.appcompat.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;
import com.gddst.app.lib_common.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zzc on 2018/1/2.
 */

public class ClearableEditText extends AppCompatEditText implements View.OnTouchListener, View.OnFocusChangeListener, TextWatcher {

    private Drawable _right;
    private OnTouchListener _t;
    private OnFocusChangeListener _f;
    private Context mContext;
    private static final int MAX_LENGTH = 25;

    public ClearableEditText(Context context) {
        super(context);
        this.mContext=context;
        init();
    }

    public ClearableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.mContext=context;
        init();
    }

    public ClearableEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
        init();
    }

    private void init() {
        _right = getCompoundDrawables()[2];
        if (_right == null) {
            _right = getResources().getDrawable(R.drawable.et_delete);
        }
        _right.setBounds(0, 0, _right.getIntrinsicWidth(), _right.getIntrinsicHeight());
        setCompoundDrawablePadding((int) getResources().getDimension(R.dimen.dp_6));
        super.setOnFocusChangeListener(this);
        super.setOnTouchListener(this);
        addTextChangedListener(this);
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        this._f = l;
    }

    @Override
    public void setOnTouchListener(OnTouchListener l) {
        this._t = l;
    }

    private void setClearIconVisible(boolean visible) {
        Drawable temp = visible ? _right : null;
        Drawable[] drawables = getCompoundDrawables();
        setCompoundDrawables(drawables[0], drawables[1], temp, drawables[3]);
    }


    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        setClearIconVisible(hasFocus && !TextUtils.isEmpty(getText()));
        if (_f != null) {
            _f.onFocusChange(v, hasFocus);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (getCompoundDrawables()[2] != null) {
            boolean tapped = event.getX() > (getWidth() - getPaddingRight() - _right.getIntrinsicWidth());
            if (tapped) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    setText("");
                }
                return true;
            }
        }
        if (_t != null) {
            return _t.onTouch(v, event);
        }
        return false;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        //ignore
    }

    @Override
    public void afterTextChanged(Editable s) {
        setClearIconVisible(isFocused() && !TextUtils.isEmpty(s));
        //如果EditText中的数据不为空，且长度大于指定的最大长度
        if (!TextUtils.isEmpty(s) && s.length() > MAX_LENGTH) {
            //删除指定长度之后的数据
            s.delete(MAX_LENGTH, getSelectionEnd());
            Toast.makeText(mContext, "最大长度为"+MAX_LENGTH+"个字符", Toast.LENGTH_SHORT).show();
        }
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        super.onTextChanged(s, start, before, count);
        //获取输入框中的数据
        String edit = getText().toString();
        //获取过滤特殊字符后的数据
        String stringFilter = stringFilter(edit);
        if (!edit.equals(stringFilter)) {
            //如果2者不等，将匹配后的数据设置给EditText显示
            setText(stringFilter);
        }
        //将光标设置到EditText最后的位置
        setSelection(length());
    }


    /***
     * 匹配特殊字符，将其过滤
     * @param edit
     * @return
     */
    public String stringFilter(String edit) {
        String regEx = "[^a-zA-Z0-9\u4E00-\u9FA5\n]";//这里可以添加需要的匹配符号
        Pattern pattern = Pattern.compile(regEx);
        Matcher matcher = pattern.matcher(edit);
        return matcher.replaceAll("");
    }

    public void setChange(){
        this.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setClearIconVisible(isFocused() && !TextUtils.isEmpty(s));
                //如果EditText中的数据不为空，且长度大于指定的最大长度
                if (!TextUtils.isEmpty(s) && s.length() > MAX_LENGTH) {
                    //删除指定长度之后的数据
                    s.delete(MAX_LENGTH, getSelectionEnd());
                    Toast.makeText(mContext, "最大长度为"+MAX_LENGTH+"个字符", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
