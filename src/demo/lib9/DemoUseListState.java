package demo.lib9;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import lib9.j2me.*;

public class DemoUseListState extends Lib9 implements L9IState {

	public DemoUseListState(MIDlet context) {
		super(context);
		// TODO Auto-generated constructor stub
		//��ʼ��Ӧ�õ�״̬
		changeState(this);
	}
	private L9Str l9Str;
	public void Init() {
		// TODO Auto-generated method stub
		l9Str=new L9Str();
	}

	public void Update() {
		// TODO Auto-generated method stub
		//��5������D���̵��м����״̬�л�����Ϣ�Ի���
		if(isKeyPressed(K_KEY_NUM5|K_KEY_FIRE)){
			pushState();
	        DemoListState list = new DemoListState(this);
	        list.setList("ʾ���б�", new String[] {
	                     "��һ��ʾ��", "��ʾͼƬ�Ͳ��Ŷ���", "���ʹ��ͼƬ����"
	        }, 180, 200);
	        list.setListIndex(0);
	        changeState(list);
		}
	}

	public void Paint() {
		// TODO Auto-generated method stub
		//�����Ļ����android��ע�����õ���ɫ���ϸ���0xAARRGGBB��ʽ���봫ͳ��0xRRGGBB��ͬ
		//FG��FP��lib9����android�汾����Ҫ�Ķ���
		fillScreen(0xFFFFFFFF);
        FG.setColor( 0xFFFF0000); //������ɫ
        l9Str.drawLine(FG, "��5�������б�ѡ���", 0, SCR_H/2, SCR_W, L9Str.K_Line_Align_Center);
	}

	public void RemovePic() {
		// TODO Auto-generated method stub
		
	}

}
