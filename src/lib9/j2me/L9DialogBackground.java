package lib9.j2me;

import javax.microedition.lcdui.Graphics;

/**
 * 绘制对话框背景
 * @author not attributable
 * @version 1.0
 */
public class L9DialogBackground {
    private Graphics FG;
    private L9Str l9Str;
    public L9DialogBackground(Graphics FG) {
        this.FG = FG;
        l9Str = new L9Str();
    }

    /**
     * 对话框背景TOP部分(显示标题的部分)的高度，默认为24
     */
    public int dialogTopH = 24;
    /**
     * 对话框背景颜色， 默认为白色(0xFFFFFF)
     */
    public int dialogBgColor = 0xFFFFFF;
    /**
     * 对话框边框颜色，默认为黑色(0x000000)
     */
    public int dialogBgBorderColor = 0x0;
    /**
     * 对话框背景TOP部分(显示标题的部分)的背景颜色，默认为0xffc080
     */
    public int dialogTopBgColor = 0xffc080;
    /**
     * 对话框标题文字的颜色,默认为黑色(0x000000)
     */
    public int dialogTitleColor = 0x0;
    /**
     * 对话框矩形圆角的宽度弧度，默认为10
     */
    public int dialogArcWidth = 10;
    /**
     * 对话框矩形圆角高度弧度，默认为10
     */
    public int dialogArcHeight = 10;
    /**
     * 对话框标题的对齐方式，默认为居中对齐，这个值为L9Str类中绘制字符串的对齐方式
     */
    public int dialogTitleAlign = L9Str.K_Line_Align_Center;
    /**
     * 当对话框标题的对齐方式不是居中时，绘制标题文字X方向上的偏移量
     */
    public int dialogTitleOffX = 5;
    private int getDialogTitleOffX() {
        int offX = 0;
        switch (dialogTitleAlign) {
        case L9Str.K_Line_Align_Left:
            offX = dialogTitleOffX;
            break;
        case L9Str.K_Line_Align_Right:
            offX = -dialogTitleOffX;
            break;
        }
        return offX;
    }

    /**
     * 设置对话框背景样式信息,比如：背景颜色，边框颜色,矩形圆角幅度
     * @param bg_color int
     * @param bg_border_color int
     * @param arcWidth int
     * @param arcHeight int
     */
    public void setDialogBackgroundStyle(int bg_color, int bg_border_color,
                                         int arcWidth, int arcHeight) {
        dialogBgColor = bg_color;
        dialogBgBorderColor = bg_border_color;
        dialogArcWidth = arcWidth;
        dialogArcHeight = arcHeight;
    }

    /**
     * 设置对话框背景TOP部分的参数，比如:TOP部分的高度，背景颜色等
     * @param top_h int
     * @param top_bg_color int
     * @param title_color int
     * @param align int
     */
    public void setDialogBackgroundTopStype(int top_h, int top_bg_color,
                                            int title_color, int align) {
        dialogTopBgColor = top_bg_color;
        dialogTopH = top_h;
        dialogTitleColor = title_color;
        dialogTitleAlign = align;
    }

    /**
     * 绘制对话框背景，有可能含有对话框title
     * @param dialogX int
     * @param dialogY int
     * @param dialogW int
     * @param dialogH int
     * @param title String
     */
    public void drawDialogBackground(int dialogX, int dialogY, int dialogW,
                                     int dialogH,
                                     String title) {
        FG.setColor(dialogBgColor);
        FG.fillRoundRect(dialogX, dialogY, dialogW, dialogH, dialogArcWidth,
                         dialogArcHeight);
        FG.setColor(dialogBgBorderColor);
        FG.drawRoundRect(dialogX, dialogY, dialogW, dialogH, dialogArcWidth,
                         dialogArcHeight);
        if (title != null) {
            FG.setColor(dialogTopBgColor);
            FG.fillRoundRect(dialogX + 1, dialogY + 1, dialogW - 2,
                             dialogTopH, dialogArcWidth, dialogArcHeight);
            FG.setColor(dialogTitleColor);
            l9Str.drawLine(FG, title, dialogX + getDialogTitleOffX(),
                           dialogY + 2,
                           dialogW,
                           dialogTitleAlign);
        }
    }
}
