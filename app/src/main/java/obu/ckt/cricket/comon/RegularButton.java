package obu.ckt.cricket.comon;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;

/**
 * Created by Administrator on 11/6/2017.
 */

public class RegularButton extends AppCompatButton {
    public RegularButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public RegularButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RegularButton(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/Aleo-Regular.otf");
        setTypeface(tf);
    }
}

