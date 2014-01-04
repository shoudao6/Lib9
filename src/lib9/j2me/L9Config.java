package lib9.j2me;

/**
 Lib9引擎的配置选项类，提供了很多选项供用户使用，比如：bShowMemory决定了是否显示内存信息，bUseDrawRgb决定采用drawrgb还是drawimage的方式来绘制图像
 *
 * @author not attributable
 * @version 1.0
 */
public class L9Config {
    public L9Config() {
    }

    /**
     * 设置为true有利于根据字符串的内容来调试程序
     */
    public static boolean bDebugString = false;
    /**
     * 设置为true显示内存信息 格式：freeMemory/totalMemory
     */
    public static boolean bShowMemory = false;
    /**
     * 设置为true显示程序的fps
     */
    public static boolean bShowFps = false;
    /**
     * 显示屏幕宽,高和当前的按键值
     */
    public static boolean bShowParam = false;
    /**
     * 设置程序的fps，默认每秒显示15帧
     */
    public static int appFps = 15;
    /**
     * 设置为true将使用drawRGB的方法绘制，否则使用drawImage的方法绘制,默认为false,实际使用时根据实际情况来调整，手机的drawRGB和drawImage的绘制速率可能不一样
     */
    public static boolean bUseDrawRgb = false;

    /**
     * 用于调试Sprite，设置为true会在控制台输出一些调试信息
     */
    public static boolean bDebugSprite = false;
    /**
     * 设置为true将使用双缓冲技术进行绘制操作，即创建和屏幕大小一样大的图片，首先将所绘制的内容首先绘制在图片上，然后再一次性绘制图片，这样做将避免屏幕闪烁
     */
    public static boolean bUseDoubleBuffer = false;
    /**
     * 设置为true表示使用软中断的方式来处理中断，有些硬件设备本身不能处理中断，比如：在打电话的过程中游戏还在进行，这显然是不对的，这个时候就需要使用软中断了
     */
    public static boolean bUseFakeInterrupt = false;
    /**
     * 设置软中断的等待时间，单位为毫秒，默认为3秒
     */
    public static int FakeInterruptWait = 3000;

    /**
     * 设置硬件(主要是手机)的上方向键，默认为标准键值
     */
    public static int PHONE_UP = -1;
    /**
     * 设置硬件(主要是手机)的下方向键，默认为标准键值
     */
    public static int PHONE_DOWN = -2;
    /**
     * 设置硬件(主要是手机)的左方向键，默认为标准键值
     */
    public static int PHONE_LEFT = -3;
    /**
     * 设置硬件(主要是手机)的右方向键，默认为标准键值
     */
    public static int PHONE_RIGHT = -4;
    /**
     * 设置硬件(主要是手机)的Fire键，默认为标准键值
     */
    public static int PHONE_FIRE = -5;
    /**
     * 设置硬件(主要是手机)的左软件，默认为标准键值
     */
    public static int PHONE_SOFT_L = -6;
    /**
     * 设置硬件(主要是手机)的上右软件，默认为标准键值
     */
    public static int PHONE_SOFT_R = -7;

    /**
     * 设置屏幕的宽,默认为640
     */
    public static int SCR_W = 640;
    /**
     * 设置屏幕的高,默认为526
     */
    public static int SCR_H = 530;

    public static String msgDialogTitle = "消息对话框";
    public static String msgDialogBtnText = "确认";
    public static int msgDialogW = 180;

    public static String yesNoDialogTitle = "确认对话框";
    public static String yesNoDialogYesBtnText = "确认";
    public static String yesNoDialogNoBtnText = "返回";
    public static int yesNoDialogW = 180;

    public static int dialogClearScreenColor = 0xFFFFFF;

    public static int FONT_H = 20; //-1表示使用m_font.getHeight()获取的高度
}
