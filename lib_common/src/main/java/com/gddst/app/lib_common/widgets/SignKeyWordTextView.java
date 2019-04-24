package com.gddst.app.lib_common.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.gddst.app.lib_common.R;

/**
 * 用于显示关键字高亮的textview
 */
public class SignKeyWordTextView extends AppCompatTextView {
    //原始文本
    private String mOriginalText = "";
    //关键字
    private String mSignText;
    //关键字颜色
    private int mSignTextColor;

    public SignKeyWordTextView(Context context) {
        super(context);
    }

    public SignKeyWordTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeAttrs(attrs);
    }

    //初始化自定义属性
    private void initializeAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SignKeyWordTextView);
        //获取关键字
        mSignText = typedArray.getString(R.styleable.SignKeyWordTextView_signText);
        //获取关键字颜色
        mSignTextColor = typedArray.getColor(R.styleable.SignKeyWordTextView_signTextColor, getTextColors().getDefaultColor());
        typedArray.recycle();
    }

    //重写setText方法
    @Override
    public void setText(CharSequence text, BufferType type) {
        this.mOriginalText = text.toString();
        super.setText(getSpannableString(mOriginalText), type);
    }

    /**
     * 匹配关键字，并返回SpannableStringBuilder对象
     * @return
     */
    private SpannableStringBuilder getSpannableString(String mOriginalText ) {
        if (TextUtils.isEmpty(mOriginalText)) {
            return new SpannableStringBuilder("");
        }
        if (TextUtils.isEmpty(mSignText)) {
            return new SpannableStringBuilder(mOriginalText);
        }

        //不包含关键字的时候
        if(!mOriginalText.contains(mSignText)){
            SpannableStringBuilder styleAll = new SpannableStringBuilder(mOriginalText);
            styleAll.setSpan(new ForegroundColorSpan(getCurrentTextColor()),0,mOriginalText.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            return styleAll;
        }
        //关键字和结果一样的时候
        if(mOriginalText.equals(mSignText)){
            SpannableStringBuilder styleAll = new SpannableStringBuilder(mOriginalText);
            styleAll.setSpan(new ForegroundColorSpan(mSignTextColor),0,mOriginalText.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            return styleAll;
        }
        //含有关键字且不等于关键字的时候
        int keyWordStart = mOriginalText.indexOf(mSignText);
        int keyWordEnd= keyWordStart + mSignText.length();
        if(keyWordStart == 0 && keyWordEnd != mOriginalText.length()){
            //关键字在开头
            int normalWordStart = keyWordEnd;
            int normalWordEnd = mOriginalText.length();
            SpannableStringBuilder styleAll = new SpannableStringBuilder(mOriginalText.substring(keyWordStart,keyWordEnd));
            styleAll.setSpan(new ForegroundColorSpan(mSignTextColor),keyWordStart,keyWordEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//            style.setSpan(new ForegroundColorSpan(mContext.getResources().getColor(R.color.word_color_666666)),normalWordStart,normalWordEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            String subString = mOriginalText.substring(normalWordStart,normalWordEnd);  //关键字后面剩余的字符串长度
            if(subString.length() >= mSignText.length()){
                //当关键字之后的字符长度大于关键字，再次循环,把余下的字符串再判断，然后拼接过来
                styleAll.append(getSpannableString(subString));
            }else {
                SpannableStringBuilder sub = new SpannableStringBuilder(subString);
                //否则直接赋予颜色值
                sub.setSpan(new ForegroundColorSpan(getCurrentTextColor()),0,sub.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                styleAll.append(sub);
            }
            return styleAll;
        }else if(keyWordEnd == mOriginalText.length() && keyWordStart != 0){
            SpannableStringBuilder styleAll = new SpannableStringBuilder(mOriginalText);   //在结尾的可以直接使用整个字符串
            //关键字在结尾
            int normalWordStart = 0;
            int normalWordEnd = keyWordStart;
            styleAll.setSpan(new ForegroundColorSpan(getCurrentTextColor()),normalWordStart,normalWordEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            styleAll.setSpan(new ForegroundColorSpan(mSignTextColor),keyWordStart,keyWordEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            return styleAll;
        }else {
            SpannableStringBuilder styleAll = new SpannableStringBuilder(mOriginalText.substring(0,keyWordEnd));
            styleAll.setSpan(new ForegroundColorSpan(mSignTextColor),keyWordStart,keyWordEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            //关键字在中间
            int normalWordStart1 = 0;
            int normalWordEnd1 = keyWordStart;
            int normalWordStart2 = keyWordEnd;
            int normalWordEnd2 = mOriginalText.length();
            styleAll.setSpan(new ForegroundColorSpan(getCurrentTextColor()),normalWordStart1,normalWordEnd1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            styleAll.setSpan(new ForegroundColorSpan(mSignTextColor),keyWordStart,keyWordEnd, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            String subString = mOriginalText.substring(normalWordStart2,normalWordEnd2);  //关键字后面剩余的字符串长度
            if(subString.length() >= mSignText.length()){
                //当关键字之后的字符长度大于关键字，再次循环,把余下的字符串再判断，然后拼接过来
                styleAll.append(getSpannableString(subString));
            }else {
                SpannableStringBuilder sub = new SpannableStringBuilder(subString);
                //否则直接赋予颜色值
                sub.setSpan(new ForegroundColorSpan(getCurrentTextColor()),0,sub.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                styleAll.append(sub);
            }
            return styleAll;
        }

    }

    /**
     * 设置关键字
     * @param signText 关键字
     */
    public void setSignText(String signText) {
        mSignText = signText;
        setText(mOriginalText);
    }

    /**
     * 设置关键字颜色
     * @param signTextColor 关键字颜色
     */
    public void setSignTextColor(int signTextColor) {
        mSignTextColor = signTextColor;
        setText(mOriginalText);
    }
}
