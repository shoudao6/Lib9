package lib9.j2me;

/**
 Lib9���������ѡ���࣬�ṩ�˺ܶ�ѡ��û�ʹ�ã����磺bShowMemory�������Ƿ���ʾ�ڴ���Ϣ��bUseDrawRgb��������drawrgb����drawimage�ķ�ʽ������ͼ��
 *
 * @author not attributable
 * @version 1.0
 */
public class L9Config {
    public L9Config() {
    }

    /**
     * ����Ϊtrue�����ڸ����ַ��������������Գ���
     */
    public static boolean bDebugString = false;
    /**
     * ����Ϊtrue��ʾ�ڴ���Ϣ ��ʽ��freeMemory/totalMemory
     */
    public static boolean bShowMemory = false;
    /**
     * ����Ϊtrue��ʾ�����fps
     */
    public static boolean bShowFps = false;
    /**
     * ��ʾ��Ļ��,�ߺ͵�ǰ�İ���ֵ
     */
    public static boolean bShowParam = false;
    /**
     * ���ó����fps��Ĭ��ÿ����ʾ15֡
     */
    public static int appFps = 15;
    /**
     * ����Ϊtrue��ʹ��drawRGB�ķ������ƣ�����ʹ��drawImage�ķ�������,Ĭ��Ϊfalse,ʵ��ʹ��ʱ����ʵ��������������ֻ���drawRGB��drawImage�Ļ������ʿ��ܲ�һ��
     */
    public static boolean bUseDrawRgb = false;

    /**
     * ���ڵ���Sprite������Ϊtrue���ڿ���̨���һЩ������Ϣ
     */
    public static boolean bDebugSprite = false;
    /**
     * ����Ϊtrue��ʹ��˫���弼�����л��Ʋ���������������Ļ��Сһ�����ͼƬ�����Ƚ������Ƶ��������Ȼ�����ͼƬ�ϣ�Ȼ����һ���Ի���ͼƬ����������������Ļ��˸
     */
    public static boolean bUseDoubleBuffer = false;
    /**
     * ����Ϊtrue��ʾʹ�����жϵķ�ʽ�������жϣ���ЩӲ���豸�����ܴ����жϣ����磺�ڴ�绰�Ĺ�������Ϸ���ڽ��У�����Ȼ�ǲ��Եģ����ʱ�����Ҫʹ�����ж���
     */
    public static boolean bUseFakeInterrupt = false;
    /**
     * �������жϵĵȴ�ʱ�䣬��λΪ���룬Ĭ��Ϊ3��
     */
    public static int FakeInterruptWait = 3000;

    /**
     * ����Ӳ��(��Ҫ���ֻ�)���Ϸ������Ĭ��Ϊ��׼��ֵ
     */
    public static int PHONE_UP = -1;
    /**
     * ����Ӳ��(��Ҫ���ֻ�)���·������Ĭ��Ϊ��׼��ֵ
     */
    public static int PHONE_DOWN = -2;
    /**
     * ����Ӳ��(��Ҫ���ֻ�)���������Ĭ��Ϊ��׼��ֵ
     */
    public static int PHONE_LEFT = -3;
    /**
     * ����Ӳ��(��Ҫ���ֻ�)���ҷ������Ĭ��Ϊ��׼��ֵ
     */
    public static int PHONE_RIGHT = -4;
    /**
     * ����Ӳ��(��Ҫ���ֻ�)��Fire����Ĭ��Ϊ��׼��ֵ
     */
    public static int PHONE_FIRE = -5;
    /**
     * ����Ӳ��(��Ҫ���ֻ�)���������Ĭ��Ϊ��׼��ֵ
     */
    public static int PHONE_SOFT_L = -6;
    /**
     * ����Ӳ��(��Ҫ���ֻ�)�����������Ĭ��Ϊ��׼��ֵ
     */
    public static int PHONE_SOFT_R = -7;

    /**
     * ������Ļ�Ŀ�,Ĭ��Ϊ640
     */
    public static int SCR_W = 640;
    /**
     * ������Ļ�ĸ�,Ĭ��Ϊ526
     */
    public static int SCR_H = 530;

    public static String msgDialogTitle = "��Ϣ�Ի���";
    public static String msgDialogBtnText = "ȷ��";
    public static int msgDialogW = 180;

    public static String yesNoDialogTitle = "ȷ�϶Ի���";
    public static String yesNoDialogYesBtnText = "ȷ��";
    public static String yesNoDialogNoBtnText = "����";
    public static int yesNoDialogW = 180;

    public static int dialogClearScreenColor = 0xFFFFFF;

    public static int FONT_H = 20; //-1��ʾʹ��m_font.getHeight()��ȡ�ĸ߶�
}
