package demo.lib9;

import java.io.InputStream;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import lib9.j2me.*;

public class DemoStateImageFont implements L9IState {
    private Lib9 lib9;
    private Graphics FG;
    private int SCR_W;
    private int SCR_H;
	public DemoStateImageFont(Lib9 lib9) {
		// TODO Auto-generated constructor stub
		this.lib9=lib9;
        FG = lib9.FG;
        SCR_W = lib9.SCR_W;
        SCR_H = lib9.SCR_H;
	}
    public L9Sprite spriteImgFont;
    public L9Str l9Str;
	public void Init() {
		// TODO Auto-generated method stub
        //����ͼƬ����sprite���� 
		spriteImgFont = new L9Sprite("/imgfont");
        
        //�����ַ����������
        l9Str = new L9Str();
        //����ͼƬ����ӳ���ַ�����ע������ַ����ַ���˳��Ӧ����ͼƬsprite��Դ�е�module˳�򱣳�һ��Ŷ
        String mapChar = "�˵�����ȡ��";
        l9Str.setImageFont(spriteImgFont, mapChar);
        //\n��ʹ�û����ַ���ʱǿ�ƻ��У�l9Str��֧������Դ�ַ�����ʹ��0p1�ķ�ʽ������ĳһ�����ַ�������ɫֵ
        String str = "0p0�˵�\n0p1���ز˵�\n0p2ȡ�����ز˵�";
        //�����ַ����ķ�ҳ��Ϣ
        l9Str.setPageSize(str, SCR_W - 8, SCR_H, 1);
	}

	public void Update() {
		// TODO Auto-generated method stub
        if (l9Str == null) {
            return;
        }
//        if (lib9.isKeyPressed(Lib9.K_KEYCODE_SOFT_RIGHT)) {
//        	lib9.changeState(lib9.popState());
//        }
        if(lib9.isInRect(lib9.getPointerX(), lib9.getPointerY(), new L9Rect(SCR_W-20,SCR_H-20,SCR_W,SCR_H))){
        	lib9.changeState(lib9.popState());
        }
	}

	public void Paint() {
		// TODO Auto-generated method stub
        if (l9Str == null) {
            return;
        }
        lib9.fillScreen(0xFFFFFFFF);
        FG.setColor(0xFF000000);
        l9Str.drawPage(FG, 0, 4, 50, l9Str.K_Line_Align_Center);
        ((DemoChangeState) (lib9)).drawSoftKey(null, "����");
	}

	public void RemovePic() {
		// TODO Auto-generated method stub
		
	}

}
