package cbp.marketlist.tableview;

/**
 * 带高亮的样式
 *
 * @author cbp
 */
class HighLightStyle extends Style {
    public static final HighLightStyle DEFAULT = new HighLightStyle(Style.DEFAULT, 0xFF080F2B);
    private final int highLightBackgroundColor;

    HighLightStyle(Style baseStyle, int backgroundColor) {
        this(baseStyle.textSizeSp, baseStyle.textColor, baseStyle.backgroundColor, backgroundColor);
    }

    private HighLightStyle(int textSizeDip, int textColor, int backgroundColor, int highLightBackgroundColor) {
        super(textSizeDip, textColor, backgroundColor);
        this.highLightBackgroundColor = highLightBackgroundColor;
    }

    int getHighLightBackgroundColor() {
        return highLightBackgroundColor;
    }
}
