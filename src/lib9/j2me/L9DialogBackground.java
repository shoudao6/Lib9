package lib9.j2me;

import javax.microedition.lcdui.Graphics;

/**
 * ���ƶԻ��򱳾�
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
     * �Ի��򱳾�TOP����(��ʾ����Ĳ���)�ĸ߶ȣ�Ĭ��Ϊ24
     */
    public int dialogTopH = 24;
    /**
     * �Ի��򱳾���ɫ�� Ĭ��Ϊ��ɫ(0xFFFFFF)
     */
    public int dialogBgColor = 0xFFFFFF;
    /**
     * �Ի���߿���ɫ��Ĭ��Ϊ��ɫ(0x000000)
     */
    public int dialogBgBorderColor = 0x0;
    /**
     * �Ի��򱳾�TOP����(��ʾ����Ĳ���)�ı�����ɫ��Ĭ��Ϊ0xffc080
     */
    public int dialogTopBgColor = 0xffc080;
    /**
     * �Ի���������ֵ���ɫ,Ĭ��Ϊ��ɫ(0x000000)
     */
    public int dialogTitleColor = 0x0;
    /**
     * �Ի������Բ�ǵĿ�Ȼ��ȣ�Ĭ��Ϊ10
     */
    public int dialogArcWidth = 10;
    /**
     * �Ի������Բ�Ǹ߶Ȼ��ȣ�Ĭ��Ϊ10
     */
    public int dialogArcHeight = 10;
    /**
     * �Ի������Ķ��뷽ʽ��Ĭ��Ϊ���ж��룬���ֵΪL9Str���л����ַ����Ķ��뷽ʽ
     */
    public int dialogTitleAlign = L9Str.K_Line_Align_Center;
    /**
     * ���Ի������Ķ��뷽ʽ���Ǿ���ʱ�����Ʊ�������X�����ϵ�ƫ����
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
     * ���öԻ��򱳾���ʽ��Ϣ,���磺������ɫ���߿���ɫ,����Բ�Ƿ���
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
     * ���öԻ��򱳾�TOP���ֵĲ���������:TOP���ֵĸ߶ȣ�������ɫ��
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
     * ���ƶԻ��򱳾����п��ܺ��жԻ���title
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
