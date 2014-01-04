package lib9.j2me;

/**
 * 提供一个操作矩形的类，程序(特别是2D游戏)中矩形有着非常重要的作用，比如：用来判断碰撞
 * @author not attributable
 * @version 1.0
 */
public class L9Rect {
    //矩形常量
    final static int K_Rect_Left = 0;
    final static int K_Rect_Top = 1;
    final static int K_Rect_Right = 2;
    final static int K_Rect_Bottom = 3;
    /**
     * 默认构造函数，创建一个矩形
     */
    public L9Rect() {
    }

    /**
     * 根据左上角和右下角两点坐标来创建矩形
     * @param leftX int
     * @param topY int
     * @param rightX int
     * @param bottomY int
     */
    public L9Rect(int leftX, int topY, int rightX, int bottomY) {
        setRect(leftX, topY, rightX, bottomY);
    }

    /**
     * 根据数组来创建矩形，数组长度为4，存储的是矩形左上角和右下角两点坐标
     * rect[0]代表leftX,rect[1]代表topY,rect[2]代表rightX,rect[3]代表bottomY
     * @param rect int[]
     */
    public L9Rect(int[] rect) {
        setRect(rect[K_Rect_Left], rect[K_Rect_Top], rect[K_Rect_Right],
                rect[K_Rect_Bottom]);
    }

    /**
     * 根据矩形来创建矩形
     * @param r L9Rect
     */
    L9Rect(L9Rect r) {
        Left = r.Left;
        Top = r.Top;
        Right = r.Right;
        Bottom = r.Bottom;
    }

    /**
     * 根据左上角和右下角两点坐标来设置矩形
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
     * 根据左上角和矩形的宽和高来设置矩形
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
     * 返回表示整数数组，长度为4，代表了矩形的左上角和右下角两点的坐标
     * @return int[]
     */
    public int[] getRect() {
        return new int[] {Left, Top, Right, Bottom};
    }

    /**
     *返回表示整数数组，长度为4，代表了矩形的左上角坐标和矩形的宽和高
     * @return int[]
     */
    public int[] getRect2() {
        return new int[] {Left, Top, Width(), Height()};
    }

    /**
     * 判断矩形是否相交
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
     * 移动矩形
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
     * 判断该点是否在矩形之内
     * @param x int
     * @param y int
     * @return boolean
     */
    public boolean isInRect(int x, int y) {
        return (x > Left && x < Right &&
                y > Top && y < Bottom);
    }

    /**
     * 返回矩形的宽
     * @return int
     */
    public int Width() {
        return Right - Left;
    }

    /**
     * 返回矩形的高
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
