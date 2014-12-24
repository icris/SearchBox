package st.one.search.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

/**
 * Created by icris on 2014/11/20.
 */
public class MyEditor extends EditText {
    public MyEditor(Context context) {
        super(context);
    }

    public MyEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public MyEditor(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK){

            Intent i = new Intent("st.one.icris.finish");
            getContext().sendBroadcast(i);
        }

        return super.dispatchKeyEventPreIme(event);
    }
}
