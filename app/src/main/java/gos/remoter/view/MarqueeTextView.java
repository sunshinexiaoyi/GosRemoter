package gos.remoter.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

//字幕滚动效果
public class MarqueeTextView extends TextView{

    public MarqueeTextView(Context con) {
	  super(con);
	}

	public MarqueeTextView(Context context, AttributeSet attrs) {
	  super(context, attrs);
	}
	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
	  super(context, attrs, defStyle);
	}
	
	public boolean isFocused() {  //  焦点
		return true;
	}
	
	@SuppressLint("MissingSuperCall")
	protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
	}


}
