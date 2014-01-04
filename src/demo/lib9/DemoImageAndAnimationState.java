package demo.lib9;

import java.io.InputStream;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import lib9.j2me.*;

public class DemoImageAndAnimationState extends Lib9 implements L9IState {
	public DemoImageAndAnimationState(MIDlet context) {
		super(context);
		// TODO Auto-generated constructor stub
		// ��ʼ��Ӧ�õ�״̬
		changeState(this);
	}

	public L9Sprite spriteImage;
	public L9Animation l9Anim;
	public boolean bPlayAnimation;
	public L9Str l9Str;

	public void Init() {
		// TODO Auto-generated method stub
		l9Str = new L9Str();
		spriteImage = new L9Sprite("/img");

		l9Anim = new L9Animation(spriteImage, 0); // ʹ�õ�0������
		int w = l9Anim.getAnimWidth();
		int h = l9Anim.getAnimHeight();
		l9Anim.setAnim((SCR_W - w) >> 1, (SCR_H - h) >> 1, true); // ���в�ѭ�����Ŷ���
	}

	public void Update() {
		// TODO Auto-generated method stub
		if (spriteImage == null) {
			return;
		}
		if (isKeyPressed(Lib9.K_KEY_NUM5 | Lib9.K_KEY_FIRE)) {
			bPlayAnimation = !bPlayAnimation;
		}
	}

	public void Paint() {
		// TODO Auto-generated method stub
		if (spriteImage == null) {
			return;
		}
		// ����
		fillScreen(0xFFFFFFFF);

		int y = 0;
		if (bPlayAnimation) {
			l9Anim.updateAnimation(FG);

			y = l9Anim.getAnimY() + l9Anim.getAnimHeight();
			FG.setColor(0xFF000000);
			l9Str.drawLine(FG,"��5��ֹͣ", 0, y, SCR_W,
					l9Str.K_Line_Align_Center);
		} else {
			// ���������ʾͼƬ
			int w = spriteImage.getFrameWidth(0);
			int h = spriteImage.getFrameHeight(0);
			spriteImage.paintFrame(FG, 0, (SCR_W - w) >> 1,
					(SCR_H - h) >> 1);

			y = h + ((SCR_H - h) >> 1);
			FG.setColor(0xFF000000);
			l9Str.drawLine(FG,  "��5������", 0, y, SCR_W,
					l9Str.K_Line_Align_Center);
		}
	}

	public void RemovePic() {
		// TODO Auto-generated method stub
		
	}

}
