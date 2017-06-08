package ui.github.com.lib;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 參考：cz
 * Created by zhaoyu1 on 2017/6/7.
 */
public class PrivacyLockView extends View {

    private final StringBuilder editable;
    private final ArrayList<OnTextChangedListener> listeners;
    private OnTextSubmitListener submitListener;
    /**
     * 个数
     */
    private int itemCount;
    private Drawable privacyDrawable;
    private int privacyDrawableSize;
    /**
     * 边框颜色
     */
    private int mBorderColor;
    private int mTextColor;
    private Paint mPaint;
    private int mItemSize;
    private int mItemPadding;
    /**
     * 加密
     */
    private boolean mEncrypt;
    private int mTextSize;

    public PrivacyLockView(Context context) {
        this(context, null);
    }

    public PrivacyLockView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrivacyLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        listeners = new ArrayList<>();
        editable = new StringBuilder();
        setFocusableInTouchMode(true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PrivacyLockView);
        setItemPadding((int) a.getDimension(R.styleable.PrivacyLockView_pv_itemPadding, 0));
        setItemCount(a.getInteger(R.styleable.PrivacyLockView_pv_itemCount, 6));
        setPrivacyDrawable(a.getDrawable(R.styleable.PrivacyLockView_pv_privacyDrawable));
        setPrivacyDrawableSize((int) a.getDimension(R.styleable.PrivacyLockView_pv_privacyDrawableSize,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics())));

        setBorderColor(a.getColor(R.styleable.PrivacyLockView_pv_border_color, getResources().getColor(android.R.color.darker_gray)));
        setTextColor(a.getColor(R.styleable.PrivacyLockView_pv_text_color, getResources().getColor(android.R.color.black)));
        setItemSize((int) a.getDimension(R.styleable.PrivacyLockView_pv_item_size,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, getResources().getDisplayMetrics())));
        setItemPadding((int) a.getDimension(R.styleable.PrivacyLockView_pv_itemPadding, 0));
        setEncrypt(a.getBoolean(R.styleable.PrivacyLockView_pv_is_privacy, false));
        setTextSize((int) a.getDimension(R.styleable.PrivacyLockView_pv_text_size,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 16, getResources().getDisplayMetrics())));
        a.recycle();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    public PrivacyLockView setTextSize(int textSize) {
        this.mTextSize = textSize;
        invalidate();
        return this;
    }

    public PrivacyLockView setEncrypt(boolean encrypt) {
        this.mEncrypt = encrypt;
        invalidate();
        return this;
    }

    public PrivacyLockView setTextColor(int colorValue) {
        this.mTextColor = colorValue;
        invalidate();
        return this;
    }

    public PrivacyLockView setItemSize(int itemSize) {
        this.mItemSize = itemSize;
        invalidate();
        return this;
    }


    public PrivacyLockView setBorderColor(int colorValue) {
        this.mBorderColor = colorValue;
        invalidate();
        return this;
    }

    public PrivacyLockView setItemPadding(int itemPadding) {
        this.mItemPadding = itemPadding;
        invalidate();
        return this;
    }

    public PrivacyLockView setItemCount(int count) {
        this.itemCount = count;
        requestLayout();
        return this;
    }

    public PrivacyLockView setPrivacyDrawable(Drawable drawable) {
        this.privacyDrawable = drawable;
        if (this.privacyDrawable == null) {
            this.privacyDrawable = getResources().getDrawable(R.drawable.lib_ui_privacy_circle_shape);
        }
        invalidate();
        return this;
    }

    public PrivacyLockView setPrivacyDrawableSize(int drawableWidth) {
        this.privacyDrawableSize = drawableWidth;
        invalidate();
        return this;
    }

    /**
     * @param text
     */
    private void appendEditText(CharSequence text) {
        if (editable.length() < itemCount) {
            editable.append(text);
            for (OnTextChangedListener listener : listeners) {
                listener.onTextChanged(editable, editable.length() - 1, editable.length());
            }
            if (editable.length() == itemCount) {
                hideSortInput();
                if (null != submitListener) {
                    submitListener.onSubmit(editable);
                }
            }
            invalidate();
        }
    }

    private void deleteLastEditText() {
        if (!TextUtils.isEmpty(editable)) {
            editable.deleteCharAt(editable.length() - 1);
            for (OnTextChangedListener listener : listeners) {
                listener.onTextChanged(editable, editable.length() + 1, editable.length());
            }
            invalidate();
        }
    }

    public void clearEditText() {
        editable.delete(0, editable.length());
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int measureHeight = getPaddingTop() + getPaddingBottom();

        if (MeasureSpec.EXACTLY == heightMode) {
            measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        } else {
            measureHeight += mItemSize;
        }
        setMeasuredDimension(measureWidth, measureHeight);
    }

    /**
     * 获取焦点时，显示键盘，失去时，隐藏键盘
     *
     * @param gainFocus
     * @param direction
     * @param previouslyFocusedRect
     */
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (gainFocus && isFocusableInTouchMode()) {
            imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
        } else {
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (hasWindowFocus && isFocusableInTouchMode()) {
            imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
        } else {
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setFocusableInTouchMode(true);
            InputMethodManager input = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            input.showSoftInput(this, InputMethodManager.SHOW_FORCED);
        }
        return super.onTouchEvent(event);
    }

    /**
     * 让自定义View支持软键盘输入，类型为数字
     *
     * @param outAttrs
     * @return
     */
    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        InputConnection inputConnection = super.onCreateInputConnection(outAttrs);
        if (onCheckIsTextEditor() && isEnabled()) {
            outAttrs.inputType = InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_SIGNED;
            outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE;       // 添加 done
            inputConnection = new MyBaseInputConnection(this, true);
        }
        return inputConnection;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // keycode_del not invoked
//        if(KeyEvent.KEYCODE_DEL == keyCode) {
//            deleteLastEditText();
//            return true;
//        } else
    if (KeyEvent.KEYCODE_ENTER == keyCode) {
            hideSortInput();
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }

    /**
     * ViewGroup never invokes onKeyUp,instead it invokes its own method dispatchKeyEvent,
     * use this method to del
     * @param event
     * @return
     */
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(KeyEvent.KEYCODE_DEL == event.getKeyCode() && event.getAction() == KeyEvent.ACTION_DOWN) {
            deleteLastEditText();
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawRectItem(canvas);
        drawUserInput(canvas);
    }

    /**
     * 方形item
     */
    private void drawRectItem(Canvas canvas) {
        mPaint.setColor(mBorderColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(0);
        Rect rect;
        int paddingTop = getPaddingTop();

        // 居中显示
        int offsetLeft = ((getWidth() - mItemPadding * (itemCount - 1)) - mItemSize * itemCount) / 2;
        for (int i = 0; i < itemCount; i++) {
            rect = new Rect(offsetLeft, paddingTop, offsetLeft + mItemSize, paddingTop + mItemSize);
            offsetLeft += mItemSize + mItemPadding;
            canvas.drawRect(rect, mPaint);
        }
    }

    Rect mRect;

    private void drawUserInput(Canvas canvas) {
        if (editable.length() == 0) {
            return;
        }

        mPaint.setColor(mTextColor);
        mPaint.setTextSize(mTextSize);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);        // 加入居中处理，指定位置居中处理
        int offsetLeft;

        if (mRect == null) {
            mRect = new Rect();
            mPaint.getTextBounds(editable.toString(), 0, editable.length(), mRect);
        }
        offsetLeft = ((getWidth() - mItemPadding * (itemCount - 1)) - mItemSize * itemCount) / 2 + (mItemSize) / 2;

        for (int i = 0; i < editable.length(); i++) {
            String c = String.valueOf(editable.charAt(i));
            if (mEncrypt) {     // 显示密文
                privacyDrawable.setBounds(offsetLeft - (privacyDrawableSize) / 2,
                        getPaddingTop() + mItemSize / 2 - privacyDrawableSize / 2,
                        offsetLeft - (privacyDrawableSize) / 2 + privacyDrawableSize,
                        getPaddingTop() + mItemSize / 2 + privacyDrawableSize / 2);
                offsetLeft += (mItemSize + mItemPadding);
                privacyDrawable.draw(canvas);
            } else {
                canvas.drawText(c, offsetLeft, mRect.height() + (getHeight() - mRect.height()) / 2, mPaint);      // // 文字居中处理
                offsetLeft += mItemSize + mItemPadding;
            }
        }
    }

    /**
     * 重写此方法，让其可以编辑
     *
     * @return
     */
    @Override
    public boolean onCheckIsTextEditor() {
        return true;
    }

    @Override
    protected void onDetachedFromWindow() {
        hideSortInput();
        super.onDetachedFromWindow();
    }

    private void hideSortInput() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
    }

    public class MyBaseInputConnection extends BaseInputConnection {
        public MyBaseInputConnection(View targetView, boolean fullEditor) {
            super(targetView, fullEditor);
        }

        @Override
        public boolean commitText(CharSequence text, int newCursorPosition) {
            appendEditText(text);    // 用户每次输入的字符
            return true;
        }

        @Override
        public boolean deleteSurroundingText(int beforeLength, int afterLength) {
            boolean result;
            if ((Build.VERSION.SDK_INT >= 14) && (beforeLength == 1 && afterLength == 0)) {
                result = super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                        && super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
            } else {
                result = super.deleteSurroundingText(beforeLength, afterLength);
            }
            return result;
        }
    }

    public void addOnTextChangedListener(OnTextChangedListener listener) {
        if (null != listener && !this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    public void removeOnTextChangedListener(OnTextChangedListener listener) {
        if (null != listener) {
            this.listeners.remove(listener);
        }
    }

    public void setOnTextSubmitListener(OnTextSubmitListener listener) {
        this.submitListener = listener;
    }

    public interface OnTextChangedListener {
        void onTextChanged(CharSequence editable, int lastLength, int length);
    }


    public interface OnTextSubmitListener {
        void onSubmit(CharSequence editable);
    }
}
