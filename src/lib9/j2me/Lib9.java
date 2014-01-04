package lib9.j2me;

import java.util.Stack;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.midlet.MIDlet;

/**
 * Lib9����ĺ����࣬���Կ�����һ��״̬��������ĸ�������(ÿ��״̬��ʵ����L9IState�ӿ����ʵ��)����װ�����״̬���������У�ͬʱ���໹ʵ���˶԰���ϵͳ֧�֣�ͨ���԰�����ϵ�ķ��룬ʹ�ÿ�����ÿ��״̬��ȥ�жϰ���(���û�����)�����򻯵ĳ���Ŀ���
 * @author not attributable
 * @version 1.0
 */

public class Lib9 extends  GameCanvas implements Runnable {
    /**
     * ��Ļ�Ŀ�
     */
    public int SCR_W;
    /**
     * ��Ļ�ĸ�
     */
    public int SCR_H;
    public Lib9(MIDlet app) {
    	super(false);
        SCR_W = L9Config.SCR_W;
        SCR_H = L9Config.SCR_H;
        if (L9Config.bUseDoubleBuffer) {
            imgBuffer = Image.createImage(L9Config.SCR_W, L9Config.SCR_H);  //��������ͼƬ
            FG = imgBuffer.getGraphics(); // FBackImage_G;
        }
        setDisplay(app, this);

        setFps(L9Config.appFps);

        new Thread(this).start();
    }

    public void paint(Graphics g) {
        if (L9Config.bUseFakeInterrupt) { //���жϴ���
            long dt = System.currentTimeMillis() - lastFrameTime;
            lastFrameTime = System.currentTimeMillis();
            if (dt > L9Config.FakeInterruptWait) {
                pauseApp();
                resumeApp();
            }
        }
        if (bAppPaused || bAppInPainting) { //��ͣ�������ڻ�����
            return;
        }
        bAppInPainting = true;

        if (L9Config.bUseDoubleBuffer && !bUseGlobalGraphics) { //˫����
            if (imgBuffer == null) {
                return;
            }
            FG = imgBuffer.getGraphics(); // FBackImage_G;
        } else {
            FG = g;
        }

        appPaint();

        if (L9Config.bUseDoubleBuffer && !bUseGlobalGraphics) {
            g.drawImage(imgBuffer, 0, 0, 0);
        }
        if (L9Config.bShowMemory) {
            g.setColor(0xffffff);
            g.fillRect(L9Config.SCR_W - 100, 0, 100, L9Config.FONT_H);
            g.setColor(0);
            String str = "" + Runtime.getRuntime().freeMemory() + "/" +
                         Runtime.getRuntime().totalMemory();
            g.drawString(str, L9Config.SCR_W - 0, 0, g.TOP | g.RIGHT);
        }

        if (L9Config.bShowFps) {
            g.setColor(0xffffff);
            g.fillRect(0, 0, L9Config.SCR_W, 3 * L9Config.FONT_H);
            g.setColor(0);
            g.drawString("fps:" + _fps, 0, 0, g.TOP | g.LEFT);
        }
        if (L9Config.bShowParam) {
            String str = "��Ļ:" + getWidth() + "X" + getHeight() + " ��ֵ:" +
                         _keyCode;
            g.setColor(0xffffff);
            g.fillRect(0, 0, 160, L9Config.FONT_H);
            g.setColor(0);
            g.drawString(str, 0, 0, g.TOP | g.LEFT);
        }

        frameCount++;
        bInterruptNotify = false;
        bAppInPainting = false;
    }

    ////////////////////////////////////״̬��/////////////////////////////////////////////////////////////////////
    /**
     * ����ĵ�ǰ״̬,����ʵ����L9IState�ӿڵĶ���
     */
    public L9IState appState;
    private boolean bFirstInitState = false;
    /**
     * ��������߳��Ƿ�����ͣ��״̬�������ж��л�����Ҫ�������û������Ľ�����������Ҫ����Ϊtrue
     */
    public boolean bAppPaused;
    private Image imgBuffer = null;
    /**
     * ͼ�ζ���������ʾ����Ļ���
     */
    public Graphics FG = null;
    private boolean bAppInPainting = false;
    /**
     * ������󣬿���ͨ����������ȡ�������ó���������Ϣ
     */
    public static MIDlet Application;
    /**
     * Ӧ�ó������ʾ���������������ƵĽ��ڸö�������ʾ
     */
    public static Displayable appDisplay;
    /**
     * Ӧ�ó������л���ʾ����ʱ���ȱ��浱ǰ����ʾ���������������Ժ󷵻ص���һ����ʾ����
     */
    public static Displayable lastAppDisplay;
    /**
     * ���ó�������ó������ʾ����
     * @param app MIDlet
     * @param disp Displayable
     */
    public void setDisplay(MIDlet app, Displayable disp) {
        if (lastAppDisplay != disp) {
            Application = app;
            appDisplay = disp;
            //��¼��һ�ε���ʾ����
            lastAppDisplay = Display.getDisplay(app).getCurrent();

            Display.getDisplay(Application).setCurrent(appDisplay);
            setFullScreenMode(true);
        }
    }

    /**
     * �л�����һ�ε���ʾ����������setDisplay������ʾ������ʱ�򽫻��¼��һ����ʾ����������backLastDisplay������һ����ʾ������ὫlastAppDisplay=null
     */
    public void backLastDisplay() {
        if (lastAppDisplay != null) {
            setDisplay(Application, lastAppDisplay);
            lastAppDisplay = null;
        }
    }

    private boolean bUseGlobalGraphics = false;
    /**
     * ��ʹ��˫����(��L9Config.bUseDoubleBufferΪtrue)������£�ʹ��ȫ�ֵ�Graphics
     * @param bUse boolean
     */
    public void setGlobalGraphics(boolean bUse) {
        bUseGlobalGraphics = bUse;
    }

    /**
     * ��ʹ��˫����(��L9Config.bUseDoubleBufferΪtrue)��ʹ��ȫ�ֵ�Graphics��������������ƻ���ͼƬ������
     */
    public void drawBufferImage() {
        if (bUseGlobalGraphics && L9Config.bUseDoubleBuffer) {
            FG.drawImage(imgBuffer, 0, 0, 0);
        }
    }

    /**
     * �����л�״̬����״̬,���stateΪnull,���˳�����
     * @param state L9IState
     */
    public void changeState(L9IState state) {
        if (state == null) {
            quitApp();
            return;
        }
        if(appState!=null){
        	appState.RemovePic();
        }
        appState = state;
        resetKey(); //״̬�ı�ʱӦ���������
        state.Init();
        //��ʾ�Ѿ����뵽�����״̬��
        if (!bFirstInitState) {
            bFirstInitState = true;
        }
    }

    /**
     * ����ʵ�ֵĳ����෽��������ִ��Ӧ�õĳ����߼�
     */
    public void appUpdate() {
        if (appState != null) {
            appState.Update();
        }
    }

    /**
     * ����ʵ�ֵĳ����෽��������Ӧ�õĻ���
     */
    public void appPaint() {
        if (appState != null) {
            appState.Paint();
        }
    }
    //////////////////////////State Manager//////////////////////////////////////////////
    private Stack sState = new Stack();
    /**
     * ��״̬���ĵ�ǰ״̬��ջ
     */
    public void pushState() {
        pushState(appState);
    }

    /**
     * ��״̬state��ջ
     * @param state L9IState
     */
    public void pushState(L9IState state) {
        sState.push(state);
    }

    /**
     * ��״̬��ջ�����س�ջ��״̬,�����ջΪ�ս�����null
     * @return L9IState
     */
    public L9IState popState() {
        L9IState state = null;
        if (sState.size() > 0) {
            state = (L9IState) sState.pop();
        }
        return state;
    }

    /**
     * ���س�ջ��״̬,���ǲ���ջ,�����ջΪ�ս�����null
     * @return L9IState
     */
    public L9IState topState() {
        return (L9IState) (sState.peek());
    }

    /**
     * ����״̬��ջ�Ĵ�С
     * @return int
     */
    public int getStateSize() {
        return sState.size();
    }

    /**
     * ���״̬����ջ
     */
    public void clearState() {
        sState.removeAllElements();
    }

    /**
     * ������ͣ
     */
    public void pauseApp() {
        bAppPaused = true;
    }

    /**
     * �������г���
     */
    public void resumeApp() {
        if (bAppPaused) {
            bAppPaused = false;
            setDisplay(Application, this);
            bInterruptNotify = true;
            repaint();
            resetKey();
        }
    }

    /**
     * �˳�����
     */
    public void quitApp() {
        appState = null;
        Application.notifyDestroyed();
    }

    /////////////////////////////////////////����ϵͳ//////////////////////////////////////////////////////////
    ///////////////////logic key value////////////////////////////////////////
    public final static int K_KEY_INVALID = -1;
    public final static int K_KEY_NUM0 = (1<<0);
    public final static int K_KEY_NUM1 = (1<<1);
    public final static int K_KEY_NUM2 = (1<<2);
    public final static int K_KEY_NUM3 = (1<<3);
    public final static int K_KEY_NUM4 = (1<<4);
    public final static int K_KEY_NUM5 = (1<<5);
    public final static int K_KEY_NUM6 = (1<<6);
    public final static int K_KEY_NUM7 = (1<<7);
    public final static int K_KEY_NUM8 = (1<<8);
    public final static int K_KEY_NUM9 = (1<<9);
    public final static int K_KEY_UP = (1<<10);
    public final static int K_KEY_DOWN = (1<<11);
    public final static int K_KEY_LEFT = (1<<12);
    public final static int K_KEY_RIGHT = (1<<13);
    public final static int K_KEY_FIRE = (1<<14);
    public final static int K_KEY_SOFT_L = (1<<15);
    public final static int K_KEY_SOFT_R = (1<<16);
    public final static int K_KEY_STAR = (1<<17);
    public final static int K_KEY_POUND = (1<<18);
    public final static int K_KEY_SEND = (1<<19);
    public final static int K_KEY_END = (1<<20);
    public final static int nKEY_NUM = 22;
    /// Previous frame keys pressed.
    private static int m_keys_pressed;
    /// Previous frame keys released.
    private static int m_keys_released;
    /// Previous frame keys state.
    private static int m_keys_state;
    /// Current keys state.
    private static int m_current_keys_state;
    /// Current keys pressed.
    private static int m_current_keys_pressed;
    /// Current keys released.
    private static int m_current_keys_released;

    private int m_current_PointerX;
    private int m_current_PointerY;

    private int PointerX = -1;
    private int PointerY = -1;
    private int DragPointerX = -1;
    private int DragPointerY = -1;

    /**
     * ���øú�������ģ�ⰴ��Ч����ʹ�ô�ͳ����ϵͳ����Ϸ�ƶ�����������Ϊ�˼��ٹ�����������Ҫģ�ⰴ��Ч��
     * @param keyCode int
     */
    public void logicKeyPressed(int keyCode) {
        int keyFlag = getLogicKeyByKeyCode(keyCode);

        m_current_keys_pressed |= keyFlag;
        m_current_keys_state |= keyFlag;
    }


    protected void keyPressed(int keyCode) {
        logicKeyPressed(keyCode);
    }


    protected void keyReleased(int keyCode) {
        int keyFlag = getLogicKeyByKeyCode(keyCode);

        m_current_keys_released |= keyFlag;
        m_current_keys_state &= ~keyFlag;
    }

    protected void keyRepeated(int keyCode) {
        keyPressed(keyCode); //�����������ֽ���󷵻أ����������
    }

    /**
     * ��֡������Ӧ�û�����Ϸ(֡�����ķ�ʽ��������Ϸ)��ÿһ֡��Ҫ�������ֵ��isKeyPressed,isKeyReleased�����ܵ�Ӱ��,��isKeyUp,isKeyDown����Ҫ�ֶ�
     * ����
     */
    public void updateKey() {

        m_keys_pressed = m_current_keys_pressed;
        m_keys_released = m_current_keys_released;
        m_keys_state = m_current_keys_state;
        m_current_keys_pressed = 0;
        m_current_keys_released = 0;

        PointerX = m_current_PointerX;
        PointerY = m_current_PointerY;
        m_current_PointerX = -1;
        m_current_PointerY = -1;
    }

    /**
     * �������ֵ
     */
    public void resetKey() {
        m_keys_pressed = 0;
        m_keys_released = 0;
        m_keys_state = 0;
        m_current_keys_state = 0;
        m_current_keys_pressed = 0;
        m_current_keys_released = 0;
    }

    /**
     * �ж�ĳ�����Ƿ��Ѿ����ͷ��ˣ�ע��������߼���ֵ�����������isKeyReleased������������Ӧ�ó����ÿһ֡isKeyPressed��Ӧ�ļ�ֵ���ᱻ�����
     * ��isKeyDown�����ʹ��ResetKey����ResetAKey�������
     * @param logicKey int
     * @return boolean
     */
    public boolean isKeyDown(int logicKey) {
        return (m_keys_state & (1 << logicKey)) != 0;
    }

    /**
     * �ж�ĳ�����Ƿ��Ѿ��������ˣ�ע��������߼���ֵ�����������isKeyPressed������������Ӧ�ó����ÿһ֡isKeyPressed��Ӧ�ļ�ֵ���ᱻ�����
     * ��isKeyUp�����ʹ��ResetKey����ResetAKey�������
     * @param logicKey int
     * @return boolean
     */
    public boolean isKeyUp(int logicKey) {
        return (m_keys_state & (1 << logicKey)) == 0;
    }

    /**
     * �ж��Ƿ����˼�
     * @return boolean
     */
    public boolean isAnyKeyPressed() {
        for (int i = 0; i < nKEY_NUM; i++) {
            if ((m_keys_pressed & (1 << i)) != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * �ж��Ƿ��м����ͷ���
     * @return int
     */
    public boolean isAnyKeyReleased() {
        for (int i = 0; i < nKEY_NUM; i++) {
            if ((m_keys_released & (1 << i)) != 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * �ж�ĳ�����Ƿ��Ѿ��������ˣ�ע��������߼���ֵ
     * @param logicKey int
     * @return boolean
     */
    public static boolean isKeyPressed(int logicKey) {
        return ((m_keys_pressed & logicKey) != 0);
    }

    /**
     * �ж�ĳ�����Ƿ��ͷ��ˣ�ע��������߼���ֵ
     * @param logicKey int
     * @return boolean
     */
    public boolean isKeyReleased(int logicKey) {
        return (m_keys_released & logicKey) != 0;
    }

    private int _keyCode = -1;
    /**
     * ���ݼ�ֵ���ؼ����߼�ֵ
     * @param keyCode int
     * @return int
     */
    public int getLogicKeyByKeyCode(int keyCode) {
        _keyCode = keyCode; //������鰴��
        //number,star,pound key
        if ((keyCode >= Canvas.KEY_NUM0) && (keyCode <= Canvas.KEY_NUM9)) {
            return (1 << (keyCode - Canvas.KEY_NUM0));
        } else if (keyCode == Canvas.KEY_POUND) {
            return K_KEY_POUND;
        } else if (keyCode == Canvas.KEY_STAR) {
            return K_KEY_STAR;
        }
        if (keyCode == L9Config.PHONE_UP) {
            return K_KEY_UP;
        } else if (keyCode == L9Config.PHONE_DOWN) {
            return K_KEY_DOWN;
        } else if (keyCode == L9Config.PHONE_LEFT) {
            return K_KEY_LEFT;
        } else if (keyCode == L9Config.PHONE_RIGHT) {
            return K_KEY_RIGHT;
        } else if (keyCode == L9Config.PHONE_FIRE) {
            return K_KEY_FIRE;
        } else if (keyCode == L9Config.PHONE_SOFT_L) {
            return K_KEY_SOFT_L;
        } else if (keyCode == L9Config.PHONE_SOFT_R) {
            return K_KEY_SOFT_R;
        }
        return 0;
    }

    /**
     * �����߼������ذ���ֵ
     * @param logicKey int
     * @return int
     */
    public int getKeyCodeByLogicKey(int logicKey) {
        if ((logicKey & K_KEY_NUM0) != 0) {
            return Canvas.KEY_NUM0;
        }
        if ((logicKey & K_KEY_NUM1) != 0) {
            return Canvas.KEY_NUM1;
        }
        if ((logicKey & K_KEY_NUM2) != 0) {
            return Canvas.KEY_NUM2;
        }
        if ((logicKey & K_KEY_NUM3) != 0) {
            return Canvas.KEY_NUM3;
        }
        if ((logicKey & K_KEY_NUM4) != 0) {
            return Canvas.KEY_NUM4;
        }
        if ((logicKey & K_KEY_NUM5) != 0) {
            return Canvas.KEY_NUM5;
        }
        if ((logicKey & K_KEY_NUM6) != 0) {
            return Canvas.KEY_NUM6;
        }
        if ((logicKey & K_KEY_NUM7) != 0) {
            return Canvas.KEY_NUM7;
        }
        if ((logicKey & K_KEY_NUM8) != 0) {
            return Canvas.KEY_NUM8;
        }
        if ((logicKey & K_KEY_NUM9) != 0) {
            return Canvas.KEY_NUM9;
        }
        if ((logicKey & K_KEY_STAR) != 0) {
            return Canvas.KEY_STAR;
        }
        if ((logicKey & K_KEY_POUND) != 0) {
            return Canvas.KEY_POUND;
        }
        if ((logicKey & K_KEY_UP) != 0) {
            return L9Config.PHONE_UP;
        }
        if ((logicKey & K_KEY_DOWN) != 0) {
            return L9Config.PHONE_DOWN;
        }
        if ((logicKey & K_KEY_LEFT) != 0) {
            return L9Config.PHONE_LEFT;
        }
        if ((logicKey & K_KEY_RIGHT) != 0) {
            return L9Config.PHONE_RIGHT;
        }
        if ((logicKey & K_KEY_FIRE) != 0) {
            return L9Config.PHONE_FIRE;
        }
        if ((logicKey & K_KEY_SOFT_L) != 0) {
            return L9Config.PHONE_SOFT_L;
        }
        if ((logicKey & K_KEY_SOFT_R) != 0) {
            return L9Config.PHONE_SOFT_R;
        }
        return 0;
    }

    /**
     * ֡����,ÿһ֡��������1,����֡������Ӧ��(��������Ϸ)�зǳ�����
     */
    public int frameCount = 0;
    private long lastFrameTime;
    private boolean bInterruptNotify;
    private long m_lBeginTime;
    private long m_l_DT;
    private long m_frameRate;
    private long _fps = 15;
    /**
     * ���ó����fps,����ÿ������ʾ��֡��
     * @param fps int
     */
    public void setFps(int fps) {
        _fps = fps;
        m_frameRate = ((long) 1000) / _fps;
    }

    public void run() {
//        Change_State(State_Main_Menu); //change state
        while (appState != null || !bFirstInitState) {
            if (!bAppPaused) {
//                System.out.println("appState=" + appState);
                m_lBeginTime = System.currentTimeMillis();
                try {
                    frameCount++;
                    updateKey();
                    appUpdate();
                    repaint(0, 0, L9Config.SCR_W, L9Config.SCR_H);
                    serviceRepaints();
                    m_l_DT = System.currentTimeMillis() - m_lBeginTime;
                    while (m_l_DT < m_frameRate) { //FRAME_RATE){
                        Thread.sleep(50);
//                            Thread.yield();
                        m_l_DT = System.currentTimeMillis() - m_lBeginTime;
                    }

                    if (L9Config.bShowFps) {
                        if (m_l_DT <= 0) {
                            m_l_DT = 1;
                        }
                        _fps = ((long) 1000) / m_l_DT;
                    }
//          System.out.println("m_frameRate="+m_frameRate);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//      System.out.println("list_elements_index="+List_Elements_Index);
        } //while App_State
    }

    /**
     * �ж�ĳ���Ƿ���ĳ���߶�֮��
     * @param pX int
     * @param leftX int
     * @param rightX int
     * @return boolean
     */
    public boolean isInLine(int pX, int leftX, int rightX) {
        return pX >= leftX && pX < rightX;
    }

    /**
     * �ж�ĳ�����Ƿ��ھ����У�����rect��ʾ���ε����ϽǺ����½���������
     * @param pX int
     * @param pY int
     * @param rect int[]
     * @return boolean
     */
    public boolean isInRect(int pX, int pY, L9Rect rect) {
        return isInLine(pX, rect.Left, rect.Right) &&
                isInLine(pY, rect.Top, rect.Bottom);
    }

    ///////////////////////////�ж����////////////////////////////////////////////////
    protected void hideNotify() {
        pauseApp();
    }

    protected void showNotify() {
        resumeApp();
    }

    protected void pointerPressed(int x, int y) {
        m_current_PointerX = x;
        m_current_PointerY = y;
    }

    protected void pointerReleased(int x, int y) {
        DragPointerX = -1;
        DragPointerY = -1;
    }

    protected void pointerDragged(int x, int y) {
        DragPointerX = x;
        DragPointerY = y;
    }

    /**
     * �ж��Ƿ����϶�״̬
     * @return boolean
     */
    public boolean isDragPointer() {
        return DragPointerX != -1 && DragPointerY != -1;
    }

    /**
     * ���ش������X����
     * @return int
     */
    public int getPointerX() {
        return PointerX;
    }

    /**
     * ���ش������Y����
     * @return int
     */
    public int getPointerY() {
        return PointerY;
    }

    /**
     * �ڴ����϶�״̬��ʱ�򷵻ص�ǰ���X����
     * @return int
     */
    public int getDragPointerX() {
        return DragPointerX;
    }

    /**
     * �ڴ����϶�״̬��ʱ�򷵻ص�ǰ���Y����
     * @return int
     */
    public int getDragPointerY() {
        return DragPointerY;
    }

    //////////////////////////////////////////////��Ϣ�Ի���/////////////////////////////////////////////////////////////
    /**
     * ��ʾ��Ϣ�Ի���Ĭ�Ͼ�����ʾ������Ǵ������Ļ�����֧���϶�
     * @param sTitle String
     * @param strMsg String
     * @param btnText String
     * @param Dialog_W int
     */
    public void showMsgDialog(String sTitle, String strMsg, String btnText,
                              int Dialog_W) {
        pushState();
        setGlobalGraphics(true);
        L9DialogMsg dialogMsg = new L9DialogMsg(this);
        dialogMsg.setMsgDialog(sTitle, strMsg, btnText, Dialog_W);
        changeState(dialogMsg);
    }

    /**
     * ��ʾȷ�϶Ի���Ĭ�Ͼ�����ʾ������Ǵ������Ļ�����֧���϶�
     * @param sTitle String
     * @param strMsg String
     * @param sYes String
     * @param sNo String
     * @param Dialog_W int
     */
    public void showYesNoDialog(String sTitle, String strMsg, String sYes,
                                String sNo, int Dialog_W) {
        pushState();
        setGlobalGraphics(true);
        L9DialogYesNo dialogYesNo = new L9DialogYesNo(this);
        dialogYesNo.setYesNoDialog(sTitle, strMsg, sYes, sNo, Dialog_W);
        changeState(dialogYesNo);
    }

    /**
     * ������������color��ɫֵ�����������Ļ
     * @param color int
     */
    public void fillScreen(int color) {
        FG.setColor(color);
        FG.fillRect(0, 0, SCR_W, SCR_H);
    }
}
