package me.biubiubiu.justifytext.library;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by ccheng on 3/18/14.
 */
public class JustifyTextView extends TextView {

    private int mLineY;
    private int mViewWidth;

    public JustifyTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //TextPaint is an extension of Paint that leaves room for some extra data used during text measuring and drawing.
        TextPaint paint = getPaint();
        paint.setColor(getCurrentTextColor());
        paint.drawableState = getDrawableState();

        //被父控件measure后的宽度
        mViewWidth = getMeasuredWidth();


        String text = (String) getText();
        mLineY = 0;
        mLineY += getTextSize() * 1.5;

        Layout layout = getLayout();
        for (int i = 0; i < layout.getLineCount(); i++) {
            //获取本行的起始index和结束index,将本行的字符串截取下来。
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            String line = text.substring(lineStart, lineEnd);

            //StaticLayout is a Layout for text that will not be edited after it is laid out. Use DynamicLayout for text that may change.
            //获取每一行文本的宽度
            float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, getPaint());

            //如果该行需要扩展，且不是最后一行。
            if (needScale(line) && i < layout.getLineCount() -1) {
                drawScaledText(canvas, lineStart, line, width);
            } else {
                canvas.drawText(line, 0, mLineY, paint);
            }

            mLineY += getLineHeight();
        }
    }

    /**
     * Scale the current line
     *
     * @param canvas from onDraw()
     * @param lineStart the index of the beginning of the line
     * @param line the text string of the current line
     * @param lineWidth the width of this line
     */
    private void drawScaledText(Canvas canvas, int lineStart, String line, float lineWidth) {
        float currentLeftX = 0;
        if (isFirstLineOfParagraph(lineStart, line)) {
            String blanks = "  ";
            //drawText 方法的第二个参数代表该行最左侧的x坐标
            //第三个参数代表该行baseline的y坐标
            canvas.drawText(blanks, currentLeftX, mLineY, getPaint());
            float bw = StaticLayout.getDesiredWidth(blanks, getPaint());
            currentLeftX += bw;

            line = line.substring(3);
        }

        //通过行尾剩余空间计算得出字与字间应该多填充多少空间
        float scaleWidth = (mViewWidth - lineWidth) / line.length() - 1;

        Log.e("lineSpace",String.valueOf(scaleWidth));
        //在当前行中逐字绘制
        for (int i = 0; i < line.length(); i++) {
            String c = String.valueOf(line.charAt(i));
            //get the desired width of every char
            float charWidth = StaticLayout.getDesiredWidth(c, getPaint());
            canvas.drawText(c, currentLeftX, mLineY, getPaint());
            currentLeftX += charWidth + scaleWidth;
        }
    }

    private boolean isFirstLineOfParagraph(int lineStart, String line) {
        return line.length() > 3 && line.charAt(0) == ' ' && line.charAt(1) == ' ';
    }

    /**
     * If the length of this line is 0, or this line ends by '/n', do not scale this line
     * @param line the split string by line
     * @return if this line need to scale
     */
    private boolean needScale(String line) {
        if (line.length() == 0) {
            return false;
        } else {
            return line.charAt(line.length() - 1) != '\n';
        }
    }

}
