package demo.lib9;

import java.io.InputStream;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import lib9.j2me.*;

public class DemoMultiState extends Lib9 implements L9IState {

	public DemoMultiState(MIDlet context) {
		super(context);
		// TODO Auto-generated constructor stub
		//初始化应用的状态
		changeState(this);
	}
    //文字资源常量，由Lib9Editor编辑器在导出文本资源的时候自动生成
    static int K_TEXT_NAME = 0;
    static int K_TEXT_SCORE = 1;
    static int K_TEXT_COLOR = 2;

    public L9ResStr l9StrRes;
    public L9Str l9Str;
    public String[] strLines;
    
	public void Init() {
		// TODO Auto-generated method stub
        //创建字符串处理对象
        l9Str = new L9Str();
        //创建字符串资源对象，注意编码问题
		l9StrRes = new L9ResStr("/text","GB2312");
        //获取相应的文字资源，XXXX为资源中需要被替换的字符串，“2,87”是要替换的字符串
        String name = l9StrRes.getResStr(K_TEXT_NAME);
        String score = l9StrRes.getResStr(K_TEXT_SCORE, "XXXX", "2,87", ",");
        String color = l9StrRes.getResStr(K_TEXT_COLOR);
        //\n将使得绘制字符串时强制换行，l9Str还支持在资源字符串中使用0xFF0000的方式来设置某一部分字符串的颜色值
        String str = name + "\n" + score + "\n" + color;
        //设置字符串的分页信息
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
