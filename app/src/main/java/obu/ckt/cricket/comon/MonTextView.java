package obu.ckt.cricket.comon;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

/**
 * Created by Administrator on 1/21/2018.
 */

public class MonTextView extends AppCompatTextView {
    public MonTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public MonTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MonTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Montserrat-Regular.ttf");
        setTypeface(tf);
    }
}