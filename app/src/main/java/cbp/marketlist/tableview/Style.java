package cbp.marketlist.tableview;

/**
 * 样式
 * 不可变类
 *
 * @author cbp
 */
public class Style {
    public static final Style DEFAULT = new Style(16, 0xFFFFFFFF, 0x00000000);
    final int textSizeSp;
    protected final int textColor;
    final int backgroundColor;
    private int clickBackgroundColor = 0xFF1E1E1E;


    public Style(int textSizeSp, int textColor, int backgroundColor) {
        this.textSizeSp = textSizeSp;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
    }

    public Style(int textSizeSp, int textColor, int backgroundColor, int clickBgColor) {
        this.textSizeSp = textSizeSp;
        this.textColor = textColor;
        this.backgroundColor = backgroundColor;
        this.clickBackgroundColor = clickBgColor;
    }

    int getTextSizeSp() {
        return textSizeSp;
    }

    public int getTextColor() {
        return textColor;
    }

    int getBackgroundColor() {
        return backgroundColor;
    }

    int getClickBackgroundColor() {
        return clickBackgroundColor;
    }

}
