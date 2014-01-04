package lib9.j2me;

/**
 * �ṩһ���������ε��࣬����(�ر���2D��Ϸ)�о������ŷǳ���Ҫ�����ã����磺�����ж���ײ
 * @author not attributable
 * @version 1.0
 */
public class L9Rect {
    //���γ���
    final static int K_Rect_Left = 0;
    final static int K_Rect_Top = 1;
    final static int K_Rect_Right = 2;
    final static int K_Rect_Bottom = 3;
    /**
     * Ĭ�Ϲ��캯��������һ������
     */
    public L9Rect() {
    }

    /**
     * �������ϽǺ����½�������������������
     * @param leftX int
     * @param topY int
     * @param rightX int
     * @param bottomY int
     */
    public L9Rect(int leftX, int topY, int rightX, int bottomY) {
        setRect(leftX, topY, rightX, bottomY);
    }

    /**
     * �����������������Σ����鳤��Ϊ4���洢���Ǿ������ϽǺ����½���������
     * rect[0]����leftX,rect[1]����topY,rect[2]����rightX,rect[3]����bottomY
     * @param rect int[]
     */
    public L9Rect(int[] rect) {
        setRect(rect[K_Rect_Left], rect[K_Rect_Top], rect[K_Rect_Right],
                rect[K_Rect_Bottom]);
    }

    /**
     * ���ݾ�������������
     * @param r L9Rect
     */
    L9Rect(L9Rect r) {
        Left = r.Left;
        Top = r.Top;
        Right = r.Right;
        Bottom = r.Bottom;
    }

    /**
     * �������ϽǺ����½��������������þ���
     * @param leftX int
     * @param topY int
     * @param rightX int
     * @param bottomY int
     */
    public void setRect(int leftX, int topY, int rightX, int bottomY) {
        Left = leftX;
        Top = topY;
        Right = rightX;
        Bottom = bottomY;
    }

    /**
     * �������ϽǺ;��εĿ�͸������þ���
     * @param leftX int
     * @param topY int
     * @param w int
     * @param h int
     */
    public void setRect2(int leftX, int topY, int w, int h) {
        Left = leftX;
        Top = topY;
        Right = leftX + w;
        Bottom = topY + h;
    }

    /**
     * ���ر�ʾ�������飬����Ϊ4�������˾��ε����ϽǺ����½����������
     * @return int[]
     */
    public int[] getRect() {
        return new int[] {Left, Top, Right, Bottom};
    }

    /**
     *���ر�ʾ�������飬����Ϊ4�������˾��ε����Ͻ�����;��εĿ�͸�
     * @return int[]
     */
    public int[] getRect2() {
        return new int[] {Left, Top, Width(), Height()};
    }

    /**
     * �жϾ����Ƿ��ཻ
     * @return boolean
     */
    public boolean isInterRect(L9Rect Rect) {
        if (Right < Rect.Left || Rect.Right < Left || Bottom < Rect.Top ||
            Rect.Bottom < Top) {
            return false;
        }
        return true;
    }

    /**
     * �ƶ�����
     * @param offsetX int
     * @param offsetY int
     */
    public void moveRect(int offsetX, int offsetY) {
        Left += offsetX;
        Top += offsetY;
        Right += offsetX;
        Bottom += offsetY;
    }

    /**
     * �жϸõ��Ƿ��ھ���֮��
     * @param x int
     * @param y int
     * @return boolean
     */
    public boolean isInRect(int x, int y) {
        return (x > Left && x < Right &&
                y > Top && y < Bottom);
    }

    /**
     * ���ؾ��εĿ�
     * @return int
     */
    public int Width() {
        return Right - Left;
    }

    /**
     * ���ؾ��εĸ�
     * @return int
     */
    public int Height() {
        return Bottom - Top;
    }

    public int Left;
    public int Top;
    public int Right;
    public int Bottom;
}
