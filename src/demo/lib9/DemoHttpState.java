package demo.lib9;

import javax.microedition.lcdui.*;
import javax.microedition.midlet.*;
import lib9.j2me.*;

public class DemoHttpState extends Lib9 implements L9IState {
    private final int K_Status_None = 0;
    private final int K_Status_Being_Get = 1;
    private final int K_Status_Being_Send = 2;
    private int iStatus;
    private L9Http l9http;
    private L9Str l9Str;
    private void ShowMessageBox(String msg) {
        showMsgDialog("��Ϣ�Ի���", msg, "ȷ��", 180);
    }
	public DemoHttpState(MIDlet context) {
		super(context);
		// TODO Auto-generated constructor stub
		//��ʼ��Ӧ�õ�״̬
		changeState(this);
	}

	public void Init() {
		// TODO Auto-generated method stub
        l9http = new L9Http();
        l9Str = new L9Str();
        //\n��ʹ�û����ַ���ʱǿ�ƻ��У�l9Str��֧������Դ�ַ�����ʹ��0xFF0000�ķ�ʽ������ĳһ�����ַ�������ɫֵ
        String str = "��1��ȡ����" + "\n��3����������";
        //�����ַ����ķ�ҳ��Ϣ
        l9Str.setPageSize(str, SCR_W - 8, SCR_H, 10);
	}

	public void Update() {
		// TODO Auto-generated method stub
        if (l9Str == null) {
            return;
        }
        if (iStatus == K_Status_None) {
            if (isKeyPressed(K_KEY_NUM1)) {
                iStatus = K_Status_Being_Get;
                l9http.setFirstJudgeHttpWay(-1);
                byte[] bin = L9Util.getIntBytes(K_Status_Being_Get);
                //ע���������ԣ���androidģ������ʹ��10.0.2.2��������������pc�ϵ�localhost
                //l9http.openHttp("http://localhost/Lib9/testHttp.aspx", bin);
                l9http.openHttp("http://localhost/lib9/testHttp.aspx", bin);
            }
            if (isKeyPressed(K_KEY_NUM3)) {
                iStatus = K_Status_Being_Send;
                //�����ݷ��͵�������
                L9OutputStream out = new L9OutputStream();
                out.writeInt(K_Status_Being_Send); //�洢���
                out.writeString("����");
                out.writeInt(95);
                out.writeString("��ע");
              //ע���������ԣ���androidģ������ʹ��10.0.2.2��������������pc�ϵ�localhost
//                l9http.openHttp("http://localhost/Lib9/testHttp.aspx",
//                                out.getBytes());
                l9http.openHttp("http://localhost/lib9/testHttp.aspx",
                        out.getBytes());
            }
        } else {
            if (!l9http.isError() && !l9http.isInProcess()) { //http��������
                switch (iStatus) {
                case K_Status_Being_Get:
                    byte[] bin = l9http.getData();
                    L9InputStream in = new L9InputStream(bin, 0, bin.length);
                    String name = in.readString();
                    int score = in.readInt();
                    String remark = in.readString();
                    ShowMessageBox("���Է���������Ϣ��\n" + name + "," + score + "," +
                                   remark);
                    break;
                case K_Status_Being_Send:
                    ShowMessageBox("���ݷ�����ϣ�");
                    break;
                }
                iStatus = K_Status_None;
            } else if (l9http.isError()) {
                ShowMessageBox("���������г��ִ���");
                iStatus = K_Status_None;
            }
        }
	}

	public void Paint() {
		// TODO Auto-generated method stub
        if (l9Str == null) {
            return;
        }
        fillScreen(0xFFFFFFFF);
        if (iStatus == K_Status_None) {
            FG.setColor(0xFF000000);
            l9Str.drawPage(FG,0, 4, (SCR_H - 50) >> 1,
                           l9Str.K_Line_Align_Center);
        } else {
            String str = "����0xFF0000��������0x000000��";
            if (iStatus == K_Status_Being_Get) {
                str = "����0xFF0000��������0x000000��";
            }
            FG.setColor(0xFF000000);
            l9Str.drawLine(FG,str, 0, SCR_H >> 1, SCR_W,
                           l9Str.K_Line_Align_Center);
        }
	}
	public void RemovePic() {
		// TODO Auto-generated method stub
		
	}

}