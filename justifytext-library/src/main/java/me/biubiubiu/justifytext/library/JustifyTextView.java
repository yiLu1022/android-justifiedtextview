package me.biubiubiu.justifytext.library;

import android.content.Context;
import android.graphics.Canvas;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * JustifyTextView is designed to justify the text in the discover screen.
 * For the moment, it doesn't support Spannable type
 */
public class JustifyTextView extends TextView {

    private int baseLineY;
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

        //the width which measured by the parent view
        mViewWidth = getMeasuredWidth();


        String text = (String) getText();
        baseLineY = 0;
        baseLineY += getTextSize();

        Layout layout = getLayout();
        for (int i = 0; i < layout.getLineCount(); i++) {
            //Gets the start index and end index of the current line and cut it.
            int lineStart = layout.getLineStart(i);
            int lineEnd = layout.getLineEnd(i);
            String line = text.substring(lineStart, lineEnd);

            //StaticLayout is a Layout for text that will not be edited after it is laid out. Use DynamicLayout for text that may change.
            //Get the width of every line
            float width = StaticLayout.getDesiredWidth(text, lineStart, lineEnd, getPaint());

            //Do not scale the last line and the line which has 0 length
            if (needScale(line) && i < layout.getLineCount() - 1) {
                drawScaledText(canvas, lineStart, line, width);
            } else {
                canvas.drawText(line, 0, baseLineY, paint);
            }

            baseLineY += getLineHeight();
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
            //The second parameter represents the most left X of this string
            //The third parameter represents the baseline of this string
            canvas.drawText(blanks, currentLeftX, baseLineY, getPaint());
            float bw = StaticLayout.getDesiredWidth(blanks, getPaint());
            currentLeftX += bw;

            line = line.substring(3);
        }
        //Calculate the space between each two chars according to the space left at the end of
        // the line
        float scaleWidth = (mViewWidth - lineWidth) / (line.length() - 1);
        //draw the current line char by char
        for (int i = 0; i < line.length(); i++) {
            String c = String.valueOf(line.charAt(i));
            //get the desired width of every char
            float charWidth = StaticLayout.getDesiredWidth(c, getPaint());
            canvas.drawText(c, currentLeftX, baseLineY, getPaint());
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
