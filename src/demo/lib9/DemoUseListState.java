package demo.lib9;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import lib9.j2me.*;

public class DemoUseListState extends Lib9 implements L9IState {

	public DemoUseListState(MIDlet context) {
		super(context);
		// TODO Auto-generated constructor stub
		//初始化应用的状态
		changeState(this);
	}
	private L9Str l9Str;
	public void Init() {
		// TODO Auto-generated method stub
		l9Str=new L9Str();
	}

	public void Update() {
		// TODO Auto-generated method stub
		//按5键或者D键盘的中间键将状态切换到消息对话框
		if(isKeyPressed(K_KEY_NUM5|K_KEY_FIRE)){
			pushState();
	        DemoListState list = new DemoListState(this);
	        list.setList("示例列表", new String[] {
	                     "第一个示例", "显示图片和播放动画", "如何使用图片字体"
	        }, 180, 200);
	        list.setListIndex(0);
	        changeState(list);
		}
	}

	public void Paint() {
		// TODO Auto-generated method stub
		//填充屏幕，在android中注意设置的颜色是严格按照0xAARRGGBB格式，与传统的0xRRGGBB不同
		//FG和FP是lib9引擎android版本中重要的对象
		fillScreen(0xFFFFFFFF);
        FG.setColor( 0xFFFF0000); //画笔颜色
        l9Str.drawLine(FG, "按5键进入列表选择框", 0, SCR_H/2, SCR_W, L9Str.K_Line_Align_Center);
	}

	public void RemovePic() {
		// TODO Auto-generated method stub
		
	}

}
