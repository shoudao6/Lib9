package demo.lib9;

import java.io.InputStream;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import lib9.j2me.*;

public class DemoMapState extends Lib9 implements L9IState {

	public DemoMapState(MIDlet context) {
		super(context);
		// TODO Auto-generated constructor stub
		//初始化应用的状态
		changeState(this);
	}
    public L9Map map;
    public L9Sprite spriteMap;
    public L9Rect hero;
    public void moveHero(int off_X, int off_Y) {
        hero.moveRect(off_X, off_Y);
        //防止将主角移动到屏幕外面去了
        if (hero.Top < 0) {
            hero.moveRect(0, -hero.Top);
        }
        if (hero.Bottom > SCR_H) {
            hero.moveRect(0, SCR_H - hero.Bottom);
        }
        if (hero.Left < 0) {
            hero.moveRect( -hero.Left, 0);
        }
        if (hero.Right > SCR_W) {
            hero.moveRect(SCR_W - hero.Right, 0);
        }
    }

    public void walk(int direct) {
        if (hero == null || map == null || spriteMap == null) {
            return;
        }
        final int K_Step = 8;
        switch (direct) {
        case L9Map.K_Map_Direction_Up:
            if ((hero.Top + hero.Height() / 2) < SCR_H / 2) {
                map.moveMap(direct, K_Step);
                if (map.isInScrollingStatus()) {
                    moveHero(0, -K_Step);
                } else {
                    moveHero(0, -1); //在滚动地图的过程中仍然小步移动主角
                }
            } else {
                moveHero(0, -K_Step);
            }
            break;
        case L9Map.K_Map_Direction_Down:
            if ((hero.Top + hero.Height() / 2) > SCR_H / 2) {
                map.moveMap(direct, K_Step);
                if (map.isInScrollingStatus()) {
                    moveHero(0, K_Step);
                } else {
                    moveHero(0, 1); //在滚动地图的过程中仍然小步移动主角
                }
            } else {
                moveHero(0, K_Step);
            }
            break;
        case L9Map.K_Map_Direction_Left:
            if ((hero.Left + hero.Width() / 2) < SCR_W / 2) {
                map.moveMap(direct, K_Step);
                if (map.isInScrollingStatus()) {
                    moveHero( -K_Step, 0);
                } else {
                    moveHero( -1, 0); //在滚动地图的过程中仍然小步移动主角
                }
            } else {
                moveHero( -K_Step, 0);
            }
            break;
        case L9Map.K_Map_Direction_Right:
            if ((hero.Left + hero.Width() / 2) > SCR_W / 2) {
                map.moveMap(direct, K_Step);
                if (map.isInScrollingStatus()) {
                    moveHero(K_Step, 0);
                } else {
                    moveHero(1, 0); //在滚动地图的过程中仍然小步移动主角
                }
            } else {
                moveHero(K_Step, 0);
            }
            break;
        }
    }
	public void Init() {
		// TODO Auto-generated method stub
        if (spriteMap == null) {
    		spriteMap = new L9Sprite("/map");
            map = new L9Map(spriteMap, SCR_W, SCR_H);
            hero = new L9Rect(0, 0, 40, 40);
        }
	}

	public void Update() {
		// TODO Auto-generated method stub
        if (map == null) {
            return;
        }
        //按下方向键后将移动主角就实现了地图的滚动，为了简单起见，这里用移动一个矩形来代表主角
        if (isKeyPressed(Lib9.K_KEY_UP)) {
            walk(L9Map.K_Map_Direction_Up);
        }
        if (isKeyPressed(Lib9.K_KEY_DOWN)) {
            walk(L9Map.K_Map_Direction_Down);
        }
        if (isKeyPressed(Lib9.K_KEY_LEFT)) {
            walk(L9Map.K_Map_Direction_Left);
        }
        if (isKeyPressed(Lib9.K_KEY_RIGHT)) {
            walk(L9Map.K_Map_Direction_Right);
        }
	}

	public void Paint() {
		// TODO Auto-generated method stub
        if (map == null) {
            return;
        }
        //绘制地图
        map.updateMap(FG);
        //绘制地图上的物品
        map.updateMapObjects(FG);

        //绘制主角,为了简单实用一个矩形来代表主角，注意在绘制矩形的时候和j2me中的差异
        FG.setColor(0xFFFF0000);
        FG.fillRect(hero.Left, hero.Top, hero.Width(), hero.Height());
	}

	public void RemovePic() {
		// TODO Auto-generated method stub
		
	}

}
