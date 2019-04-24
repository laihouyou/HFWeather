package com.gddst.app.lib_common.recyclerView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gddst.app.lib_common.R;


/**
 * RecyclerView的HeaderView，简单的展示一个TextView
 */
public class TextHeader extends RelativeLayout {
    private TextView headerText;

    public TextHeader(Context context) {
        super(context);
        init(context);
    }

    public TextHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {
        View view=inflate(context, R.layout.text_header, this);
        headerText=view.findViewById(R.id.tv_header);
    }

    public void setHeaderText(String header){
        if (headerText!=null){
            headerText.setText(header);
        }
    }
}