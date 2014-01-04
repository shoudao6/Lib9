package demo.lib9;

import javax.microedition.midlet.*;
import javax.microedition.lcdui.*;
import lib9.j2me.*;

public class DemoActivity extends MIDlet {
	/** Called when the activity is first created. */
	static Lib9 lib9;

	public DemoActivity() {
//		lib9 = new DemoFirst(this);
//		lib9 = new DemoImageAndAnimationState(this);
//		lib9 = new DemoImageFontState(this);
//		lib9 = new DemoMapState(this);
//		lib9 = new DemoMultiState(this);
//		lib9 = new DemoHttpState(this);
//		lib9 = new DemoUseListState(this);
		lib9 = new DemoChangeState(this);
	}

	public void startApp() {
		lib9.resumeApp();
	}

	public void pauseApp() {
		lib9.pauseApp();
	}

	public void destroyApp(boolean unconditional) {
		if (lib9 != null) {
			lib9.quitApp();
		}
	}
}