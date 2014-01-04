package demo.lib9;

import java.io.InputStream;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import lib9.j2me.*;

public class DemoMultiState extends Lib9 implements L9IState {

	public DemoMultiState(MIDlet context) {
		super(context);
		// TODO Auto-generated constructor stub
		//��ʼ��Ӧ�õ�״̬
		changeState(this);
	}
    //������Դ��������Lib9Editor�༭���ڵ����ı���Դ��ʱ���Զ�����
    static int K_TEXT_NAME = 0;
    static int K_TEXT_SCORE = 1;
    static int K_TEXT_COLOR = 2;

    public L9ResStr l9StrRes;
    public L9Str l9Str;
    public String[] strLines;
    
	public void Init() {
		// TODO Auto-generated method stub
        //�����ַ����������
        l9Str = new L9Str();
        //�����ַ�����Դ����ע���������
		l9StrRes = new L9ResStr("/text","GB2312");
        //��ȡ��Ӧ��������Դ��XXXXΪ��Դ����Ҫ���滻���ַ�������2,87����Ҫ�滻���ַ���
        String name = l9StrRes.getResStr(K_TEXT_NAME);
        String score = l9StrRes.getResStr(K_TEXT_SCORE, "XXXX", "2,87", ",");
        String color = l9StrRes.getResStr(K_TEXT_COLOR);
        //\n��ʹ�û����ַ���ʱǿ�ƻ��У�l9Str��֧������Դ�ַ�����ʹ��0xFF0000�ķ�ʽ������ĳһ�����ַ�������ɫֵ
        String str = name + "\n" + score + "\n" + color;
        //�����ַ����ķ�ҳ��Ϣ
        l9Str.setPageSize(str, SCR_W - 8, SCR_H, 1);
	}

	public void Update() {
		// TODO Auto-generated method stub
        if (l9StrRes == null) {
            return;
        }
        if (isKeyPressed(Lib9.K_KEY_NUM5)) {
            quitApp();
        }
	}

	public void Paint() {
		// TODO Auto-generated method stub
        if (l9StrRes == null) {
            return;
        }
        fillScreen(0xFFFFFFFF);
        FG.setColor(0xFF000000);
        l9Str.drawPage(FG,0, 4, 50, l9Str.K_Line_Align_Center);
	}

	public void RemovePic() {
		// TODO Auto-generated method stub
		
	}

}
