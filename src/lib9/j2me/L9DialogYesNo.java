package lib9.j2me;

/**
 * 显示确认对话框供用户确认，最后通过isYesNo方法返回布尔值true或者false，该类实现了L9IState接口，表示该类是一个状态类，该类的实例可以构成程序的一个状态
 * @author not attributable
 * @version 1.0
 */
public class L9DialogYesNo extends L9DialogBackground implements L9IState {
    private Lib9 lib9;
    private boolean _bYesNo;
    private L9Str l9Str;
    private String[] strArr;

    private int dialogX;
    private int dialogY;
    private int dialogW;
    private int dialogH;
    private int dialogLineSpace;
    private int dialogLineTextOffsetX = 4;
    private int dialogLineColor = 0x0;
    private int dialogBarColor = 0xffc0c0;

//    final int K_FONT_H = 20;
//    final int K_Top_H = 24;

    private String msgTitle;

    private int btnX; //为了支持触摸屏
    private int btnY;
    private int btnW;

    private int _msgDragBeginX = -1;
    private int _msgDragBeginY = -1;
    /**
     * 设置确认对话框
     * @param title String
     * @param msg String
     * @param sYes String
     * @param sNo String
     * @param boxW int
     */
    public void setYesNoDialog(String title, String msg, String sYes,
                               String sNo, int boxW) {
        msgTitle = title;
        //使用\n强制换行
        String sPaint = msg + "\n" + sYes + "\n" + sNo;
        if (l9Str == null) {
            l9Str = new L9Str();
        }
        strArr = l9Str.updateString(sPaint, boxW - 8);
        dialogW = boxW;
        dialogH = strArr.length * L9Config.FONT_H + dialogTopH + 5;
        dialogX = (lib9.SCR_W - boxW) >> 1;
        dialogY = (lib9.SCR_H - (dialogH + dialogTopH)) >> 1;
//        setDialogBackgroundTopStype(dialogTopH, 0xffc080, 0x0,
//                                         L9Str.K_Line_Align_Center);
//        setDialogBackgroundStyle(0xFFFFFF, 0x0, 10, 10);
    }

    /**
     * 设置确认对话框参数,除了msg参数外，其它参数为系统默认，在L9Config配置中可以修改
     * @param msg String
     */
    public void setYesNoDialog(String msg) {
        setYesNoDialog(L9Config.yesNoDialogTitle, msg,
                       L9Config.yesNoDialogYesBtnText,
                       L9Config.yesNoDialogNoBtnText, L9Config.yesNoDialogW);
    }

    public L9DialogYesNo(Lib9 lib9) {
        super(lib9.FG);
        this.lib9 = lib9;
    }

    /**
     * 设置对话框格式信息，比如：textOffsetX为要绘制的文本到边框的偏移量,lineSpace为文本每一行的间距
     * @param textOffsetX int
     * @param lineSpace int
     */
    public void setDialogStyle(int lineOffX, int lineSpace, int lineColor,
                               int barColor) {
        dialogLineTextOffsetX = lineOffX;
        dialogLineSpace = lineSpace;
        dialogLineColor = lineColor;
        dialogBarColor = barColor;
    }

    /**
     * 返回用户选择
     * @return boolean
     */
    public boolean isYesNo() {
        return _bYesNo;
    }

    /**
     *
     * @todo Implement this lib9.L9IState method
     */
    public void Init() {
    }

    /**
     *
     * @todo Implement this lib9.L9IState method
     */
    public void Paint() {
        //如果使用双缓冲，则用上一个状态的画面清屏，否则使用L9Config.dialogClearScreenColor的颜色清屏
        if (L9Config.bUseDoubleBuffer) {
            lib9.drawBufferImage();
        } else {
            lib9.fillScreen(L9Config.dialogClearScreenColor);
        }

        drawDialogBackground(dialogX, dialogY, dialogW, dialogH, msgTitle);
        //绘制上下移动的条的背景
        lib9.FG.setColor(dialogBarColor);
        int YY = dialogY + dialogTopH +
                 (strArr.length - 2) * (L9Config.FONT_H + dialogLineSpace);

        btnY = YY;

        YY -= 1;
        if (!_bYesNo) {
            YY += L9Config.FONT_H;
        }
        lib9.FG.fillRoundRect(dialogX + 1, YY, dialogW - 2, L9Config.FONT_H + 2,
                              10,
                              10);

        lib9.FG.setColor(dialogLineColor);
        for (int i = 0; strArr != null && i < strArr.length; i++) {
            //绘制对话框内容，最后两行“是”与“否”居中绘制
            YY = dialogY + dialogTopH + i * (L9Config.FONT_H + dialogLineSpace) +
                 2;
            if (i >= strArr.length - 2) {
                l9Str.drawLine(lib9.FG, strArr[i], dialogX, YY,
                               dialogW, l9Str.K_Line_Align_Center);
            } else {
                l9Str.drawLine(lib9.FG, strArr[i],
                               dialogX + dialogLineTextOffsetX, YY,
                               dialogW, l9Str.K_Line_Align_Left);
            }
        }
    }

    /**
     *
     * @todo Implement this lib9.L9IState method
     */
    public void Update() {
        if (lib9.isDragPointer()) {
            if (_msgDragBeginX == -1) {
                if (lib9.isInRect(lib9.getDragPointerX(),
                                  lib9.getDragPointerY(),
                                  new L9Rect(dialogX, dialogY,
                                             dialogX + dialogW,
                                             dialogY + dialogH))) {
                    _msgDragBeginX = lib9.getDragPointerX();
                    _msgDragBeginY = lib9.getDragPointerY();
                }
            } else {
                dialogX += lib9.getDragPointerX() - _msgDragBeginX;
                dialogY += lib9.getDragPointerY() - _msgDragBeginY;

                _msgDragBeginX = lib9.getDragPointerX();
                _msgDragBeginY = lib9.getDragPointerY();
            }
        } else {
            _msgDragBeginX = -1;
            _msgDragBeginY = -1;
        }
        if (lib9.isKeyPressed(Lib9.K_KEY_UP | Lib9.K_KEY_NUM2 |
                              Lib9.K_KEY_DOWN | Lib9.K_KEY_NUM8)) {
            _bYesNo = !_bYesNo;
        }
        if (lib9.isKeyPressed(Lib9.K_KEY_FIRE | Lib9.K_KEY_NUM5)) {
            lib9.resetKey();
            lib9.changeState(lib9.popState());
            lib9.setGlobalGraphics(false);
        }
        if (lib9.getPointerX() > 0) {
            if (lib9.isInRect(lib9.getPointerX(), lib9.getPointerY(),
                              new L9Rect(dialogX, btnY, dialogX + dialogW,
                                         btnY + L9Config.FONT_H))) {
                if (!_bYesNo) {
                    _bYesNo = true;
                } else {
                    lib9.logicKeyPressed(lib9.getKeyCodeByLogicKey(Lib9.
                            K_KEY_FIRE | Lib9.K_KEY_NUM5));
                }
            } else if (lib9.isInRect(lib9.getPointerX(), lib9.getPointerY(),
                                     new L9Rect(dialogX, btnY + L9Config.FONT_H,
                                                dialogX + dialogW,
                                                btnY + 2 * L9Config.FONT_H))) {
                if (_bYesNo) {
                    _bYesNo = false;
                } else {
                    lib9.logicKeyPressed(lib9.getKeyCodeByLogicKey(Lib9.
                            K_KEY_FIRE |
                            Lib9.K_KEY_NUM5));
                }
            }
        }
    }

	public void RemovePic() {
		// TODO Auto-generated method stub
		
	}
}
