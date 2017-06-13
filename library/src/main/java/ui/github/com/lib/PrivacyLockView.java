package ui.github.com.lib;


import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;


/**
 * 參考：cz
 * Created by zhaoyu1 on 2017/6/7.
 */
public class PrivacyLockView extends EditText {

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
    private Paint mPaint;
    private int mItemSize;
    private int mItemPadding;
    /**
     * 加密
     */
    private boolean mEncrypt;

    public PrivacyLockView(Context context) {
        super(context);
    }

    public PrivacyLockView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public PrivacyLockView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PrivacyLockView);
        setItemPadding((int) a.getDimension(R.styleable.PrivacyLockView_pv_itemPadding, 0));
        setItemCount(a.getInteger(R.styleable.PrivacyLockView_pv_itemCount, 6));
        setPrivacyDrawable(a.getDrawable(R.styleable.PrivacyLockView_pv_privacyDrawable));
        setPrivacyDrawableSize((int) a.getDimension(R.styleable.PrivacyLockView_pv_privacyDrawableSize,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics())));

        setBorderColor(a.getColor(R.styleable.PrivacyLockView_pv_border_color, getResources().getColor(android.R.color.darker_gray)));
        setItemSize((int) a.getDimension(R.styleable.PrivacyLockView_pv_item_size,
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, getResources().getDisplayMetrics())));
        setItemPadding((int) a.getDimension(R.styleable.PrivacyLockView_pv_itemPadding, 0));
        setEncrypt(a.getBoolean(R.styleable.PrivacyLockView_pv_is_privacy, false));
        a.recycle();
        setCursorVisible(false);
        setBackgroundColor(Color.TRANSPARENT);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
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

    public PrivacyLockView setEncrypt(boolean encrypt) {
        this.mEncrypt = encrypt;
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
        InputFilter[] FilterArray = new InputFilter[1];
        FilterArray[0] = new InputFilter.LengthFilter(itemCount);
        this.setFilters(FilterArray);
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

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        if (text.length() < itemCount) {
            invalidate();
        }
        if(text.length() == itemCount) {
            hideSortInput();
            if(submitListener != null) {
                submitListener.onSubmit(text);
            }
        }
    }

    public void clearEditText() {
        setText("");
        invalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        drawRectItem(canvas);
        drawUserInput(canvas);
    }

    /**
     * 方形item
     */
    private void drawRectItem(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int offsetLeft = ((width - mItemPadding * (itemCount - 1)) - mItemSize * itemCount) / 2;


        // 外边框
        mPaint.setColor(mBorderColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(0);
        RectF rect = new RectF(offsetLeft, getPaddingTop(), getWidth() - offsetLeft, height - getPaddingBottom());
        canvas.drawRoundRect(rect, 2, 2, mPaint);

        for (int i = 0; i < mItemSize - 1; i++) {
            float startX = offsetLeft + (i + 1) * mItemSize + i * mItemPadding;
            canvas.drawLine(startX, rect.top, startX, rect.bottom, mPaint);
        }
    }

    Rect mRect;

    private void drawUserInput(Canvas canvas) {

        String text = getText().toString();
        if (TextUtils.isEmpty(text)) {
            return;
        }

        mPaint.setColor(getTextColors().getDefaultColor());
        mPaint.setTextSize(getTextSize());
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);        // 加入居中处理，指定位置居中处理
        int offsetLeft;


        if (mRect == null) {
            mRect = new Rect();
            mPaint.getTextBounds(text.toString(), 0, text.length(), mRect);
        }
        offsetLeft = ((getWidth() - mItemPadding * (itemCount - 1)) - mItemSize * itemCount) / 2 + (mItemSize) / 2;

        for (int i = 0; i < text.length(); i++) {
            String c = String.valueOf(text.charAt(i));
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

    @Override
    protected void onDetachedFromWindow() {
        hideSortInput();
        super.onDetachedFromWindow();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (hasWindowFocus) {
            imm.showSoftInput(this, InputMethodManager.SHOW_FORCED);
        } else {
            imm.hideSoftInputFromWindow(getWindowToken(), 0);
        }
    }

    private void hideSortInput() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
    }

    public void setOnTextSubmitListener(OnTextSubmitListener listener) {
        this.submitListener = listener;
    }

    public interface OnTextSubmitListener {
        void onSubmit(CharSequence editable);
    }
}
