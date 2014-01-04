package demo.lib9;

import java.io.InputStream;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import lib9.j2me.*;

public class DemoStateImageAndAnimation implements L9IState {
    private Lib9 lib9;
    private Graphics FG;
    private int SCR_W;
    private int SCR_H;
	public DemoStateImageAndAnimation(Lib9 lib9) {
		// TODO Auto-generated constructor stub
		this.lib9=lib9;
        FG = lib9.FG;
        SCR_W = lib9.SCR_W;
        SCR_H = lib9.SCR_H;
	}
    public L9Sprite spriteImage;
    public L9Animation l9Anim;
    public boolean bPlayAnimation;
    public L9Str l9Str;
	public void Init() {
		// TODO Auto-generated method stub
		l9Str=new L9Str();
		
        spriteImage = new L9Sprite("/img");
        
        l9Anim = new L9Animation(spriteImage, 0); //使用第0个动画
        int w = l9Anim.getAnimWidth();
        int h = l9Anim.getAnimHeight();
        l9Anim.setAnim((SCR_W - w) >> 1, (SCR_H - h) >> 1, true); //居中并循环播放动画
	}

	public void Update() {
		// TODO Auto-generated method stub
        if (spriteImage == null) {
            return;
        }
        if (lib9.isKeyPressed(Lib9.K_KEY_NUM5 | Lib9.K_KEY_FIRE)) {
            bPlayAnimation = !bPlayAnimation;
        }
        if(lib9.isInRect(lib9.getPointerX(), lib9.getPointerY(), new L9Rect(SCR_W-20,SCR_H-20,SCR_W,SCR_H))){
        	lib9.changeState(lib9.popState());
        }
	}

	public void Paint() {
		// TODO Auto-generated method stub
        if (spriteImage == null) {
            return;
        }
        //清屏
        lib9.fillScreen(0xFFFFFFFF);
        System.out.println("当前帧的坐标" + spriteImage.getAnimWidth(0));
        int y = 0;
        if (bPlayAnimation) {
            l9Anim.updateAnimation(FG);

            y = l9Anim.getAnimY() + l9Anim.getAnimHeight();
            FG.setColor(0xFF000000);
            l9Str.drawLine(FG,"按5键停止", 0, y, SCR_W,
                    l9Str.K_Line_Align_Center);
        } else {
            //这里居中显示图片
            int w = spriteImage.getFrameWidth(0);
            int h = spriteImage.getFrameHeight(0);
            spriteImage.paintFrame(FG,2, (SCR_W - w) >> 1, (SCR_H - h) >> 1);

            y = h + ((SCR_H - h) >> 1);
            FG.setColor(0xFF000000);
            l9Str.drawLine(FG, "按5键停止", 0, y, SCR_W,
                    l9Str.K_Line_Align_Center);
        }
        ((DemoChangeState) (lib9)).drawSoftKey(null, "返回");
	}

	public void RemovePic() {
		// TODO Auto-generated method stub
		
	}

}
