package demo.lib9;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import lib9.j2me.*;

public class DemoChangeState extends Lib9 implements L9IState {
	public DemoChangeState(MIDlet context) {
		super(context);
		// TODO Auto-generated constructor stub
		//��ʼ��Ӧ�õ�״̬
		changeState(this);
	}
    final int K_Demo_First = 0;
    final int K_Demo_ImageAndAnimation = 1;
    final int K_Demo_ImageFont = 2;
    public DemoListState list;
    public DemoStateImageAndAnimation imageAndAnimation;
    public DemoStateImageFont imageFont;
	public void Init() {
		// TODO Auto-generated method stub
		l9Str=new L9Str();
	}

	public void Update() {
		// TODO Auto-generated method stub
		if(isKeyPressed(K_KEY_NUM5|K_KEY_FIRE)){
            pushState();
            list = new DemoListState(this);
            list.setList("ʾ���б�", new String[] {
                         "��һ��ʾ��", "��ʾͼƬ�Ͳ��Ŷ���", "���ʹ��ͼƬ����"
            }, 180, 200);
            list.setListIndex(0);
            changeState(list);
            return;
        }
        if (list != null) {
            int listIndex = list.getListIndex();
            switch (listIndex) {
            case K_Demo_First:

//                pushState();//��ʾ��Ϣ�Ի���������洢��ǰ״̬
                showMsgDialog("��Ϣ�Ի���", "�㿴����0xFF0000��Ϣ�Ի���0x000000����", "ȷ��",
                              180);
                break;
            case K_Demo_ImageAndAnimation:
                pushState();
                imageAndAnimation = new DemoStateImageAndAnimation(this);
                changeState(imageAndAnimation);
                break;
            case K_Demo_ImageFont:
                pushState();
                imageFont = new DemoStateImageFont(this);
                changeState(imageFont);
                break;
            }
            //��仰����Ҫ
            list.setListIndex( -1);
        }
	}

	public void Paint() {
		// TODO Auto-generated method stub
        fillScreen(0xFFFFFFFF);
        FG.setColor(0xFF000000);
        l9Str.drawLine(FG, "��5��ʾ����ʾ", 0, (SCR_H - 20) >> 1,SCR_W,L9Str.K_Line_Align_Center);
//        FG.drawString("��5��ʾ����ʾ", SCR_W >> 1, (SCR_H - 20) >> 1,FG.TOP | FG.HCENTER);
	}
    public L9Str l9Str;
    public void drawSoftKey(String left, String right) {
        if (left != null) {
            l9Str.drawLine(FG,left, 0, SCR_H - 20);
        }
        if (right != null) {
            l9Str.drawLine(FG,right, 0, SCR_H - 20, SCR_W,
                           l9Str.K_Line_Align_Right);
        }
    }

	public void RemovePic() {
		// TODO Auto-generated method stub
		
	}
}