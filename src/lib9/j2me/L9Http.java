package lib9.j2me;

import javax.microedition.io.HttpConnection;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;

/**
 * Lib9����ͨ�������ṩ�˶�������֧�֣�֧��http��ʽ����(��������Ϊ��̬��ҳ)������post��ʽ��������
 * @author not attributable
 * @version 1.0
 */
public class L9Http implements Runnable {
    public L9Http() {
    }

    private Thread l9Thread = null;
    private boolean bInProgress = false;
    private boolean bCanceled;
    private boolean bError;
    private byte[] responseBin = null;
    private String httpAgent = "10.0.0.172:80";
    /**
     * ���شӷ�������ȡ������
     * @return byte[]
     */
    public byte[] getData() {
        return responseBin;
    }

    /**
     * �����Ƿ�����http������
     * @return boolean
     */
    public boolean isInProcess() {
        return bInProgress;
    }

    /**
     * ����http���ӹ����Ƿ��д�����
     * @return boolean
     */
    public boolean isError() {
        return bError;
    }

    private static boolean bFirstHttpConnection = true;
    private int HttpConnectionTimeOut = 45;
    /**
     * ���õ�һ���ж�http�����ĳ�ʱʱ�䣬Ĭ��Ϊ45�룬��һ��http����Ĭ�Ͻ�������ж�http����㣬���timeoutΪ-1���ж����������ֱ������
     * �����ֽ��������Net��Wap,Ĭ��ʹ��Net�����
     * @param timeout int
     */
    public void setFirstJudgeHttpWay(int timeout) {
        if (timeout == -1) {
            bFirstHttpConnection = false;
        } else {
            HttpConnectionTimeOut = timeout;
            bFirstHttpConnection = true;
        }
    }

    /**
     * ����ʹ��Wap������ʱ��Ҫ�����ø����ԣ�Ĭ��Ϊ10.0.0.172:80
     * @param sAgent String
     */
    public void setAgent(String sAgent) {
        httpAgent = sAgent;
    }

    private HttpConnection httpCon = null;
    private OutputStream httpOut = null;
    private InputStream httpIn = null;
    /**
     * ȡ��http����
     */
    public void cancelHttp() {
        bCanceled = true;
    }

    /**
     * �ر�http���Ӳ��ͷ��й���Դ
     */
    public void closeHttp() {
        if (httpCon != null) {
            try {
                synchronized (httpCon) {
                    httpCon.close();
                }
            } catch (Exception e) {
            }
        }
        if (httpIn != null) {
            try {
                synchronized (httpIn) {
                    httpIn.close();
                }
            } catch (Exception e) {
            }
        }
        if (httpOut != null) {
            try {
                synchronized (httpOut) {
                    httpOut.close();
                }
            } catch (Exception e) {
            }
        }
        httpCon = null;
        httpIn = null;
        httpOut = null;

        l9Thread = null;
        bInProgress = false;
    }

    private byte[] postData;
    private String sHost = "";
    private final String K_Http = "http://";
    private String sQuery = "";
    /**
     * ����http���ӣ�ʹ��post��ʽ����������,headΪ����������ݵ�����
     * @param url String
     * @param head byte[]
     */
    public void openHttp(String url, byte[] head) {
        while (bInProgress) { //��ǰ���ڴ���
            try {
                synchronized (this) {
                    wait(80L);
                }
            } catch (Exception e) {
            }
        }
        if (l9Thread != null) {
            try {
                l9Thread.join();
            } catch (Exception e) {
            }
        }
        url = url.toLowerCase();
        if (url.startsWith(K_Http)) {
            url = url.substring(K_Http.length());
        }
        int pos = url.indexOf("/");
        if (pos == -1) {
            sQuery = "";
            sHost = url;
        } else {
            sQuery = url.substring(pos);
            sHost = url.substring(0, pos);
        }

        postData = head;

        bInProgress = true;
        bCanceled = false;
        bError = false;
        l9Thread = new Thread(this);
        l9Thread.start();
    }

    private static boolean bHttpNet = true; //Ĭ��ʹ��Net�ķ�ʽ����
    /**
     * ����true��ʾ����ʹ��net����㣬����ʹ��wap�����
     * @return boolean
     */
    public boolean getHttpWay() {
        return bHttpNet;
    }

    /**
     * ����Wap����Net����㣬Ĭ��ΪNet�����,���ָ���˽������ô��һ������ʱ�������ٽ��н�����ж���
     * @param bNetOrWap boolean
     */
    public void setHttpWay(boolean bNetOrWap) {
        bHttpNet = bNetOrWap;
        setFirstJudgeHttpWay( -1);
    }

    private String getHttpUrl() {
        if (bHttpNet) {
            return K_Http + sHost + sQuery;
        }
        return K_Http + httpAgent + sQuery;
    }

    public void run() {
        responseBin = null;
        bError = false;
        bInProgress = true;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String Content_Type = "application/octet-stream";

        if (bCanceled) {
            closeHttp();
            return;
        }
        try {
            if (bFirstHttpConnection) { //��һ����Ҫ�ж���������,ȷ���û���ʹ��Wap����Net�����
                long Net_Time = 0;
                long Net_BeginTime = System.currentTimeMillis();
                new Thread() {
                    public void run() {
                        try {
                            httpCon = (HttpConnection) Connector.open(
                                    getHttpUrl(), Connector.READ_WRITE, true);
                        } catch (Exception ex) {
                            httpCon = null;
                        }
                    }
                }.start();
                boolean bRun = true;
                while (bRun) {
                    Net_Time += (System.currentTimeMillis() - Net_BeginTime);
                    Net_BeginTime = System.currentTimeMillis();
                    try {
                        Thread.sleep(100);
                        Thread.yield();
                    } catch (Exception e) {}
                    if (httpCon != null) {
                        bRun = false;
                    } else
                    if (Net_Time > HttpConnectionTimeOut * 1000) { //����ʱ����ڳ�ʱʱ����л������
                        bHttpNet = !bHttpNet;
                        bRun = false;
                    }
                }
                bFirstHttpConnection = false;
            }
            httpCon = (HttpConnection) Connector.open(getHttpUrl(),
                    Connector.READ_WRITE, true);
            if (bCanceled) {
                closeHttp();
            }
            httpCon.setRequestProperty("X-Online-Host",
                                       L9Str.splitStr(sHost, "/")[0]);
            // Set the request method and headers
            httpCon.setRequestMethod(HttpConnection.POST);
//��Ҫ����User-Agent����ֶΣ������е��ֻ������bad request(invalid header name)����
//      c.setRequestProperty("User-Agent",
//                           "Profile/MIDP-2.0 Configuration/CLDC-1.0");
            httpCon.setRequestProperty("Content-Type", Content_Type);
            //c.setRequestProperty("Connection", "Keep-Alive");
            httpCon.setRequestProperty("Accept", "*/*");
//      c.setRequestProperty("Content-Length",""+my_head_info.length);
            if (postData != null) {
                httpOut = httpCon.openOutputStream();
                httpOut.write(postData);
                //flush�����������HTTP_INTERNAL_ERROR
                //      out.flush();
                httpOut.close();
            }
            int status = httpCon.getResponseCode();
            if (status != HttpConnection.HTTP_OK) { //ע������һ���Ϣ�ж��������� ���ӳ�ʱ
                closeHttp();
                bError = true;
                throw new Exception("status=" + status);
            } else {
//�����ƶ��ʷ�ҳ��
                String s = httpCon.getHeaderField("Content-Type");
                if (s != null) {
                    s = s.toLowerCase();
                    if (!s.startsWith(Content_Type)) { //������صĽ���������õ���������������
                        openHttp(getHttpUrl(), postData);
                        return;
                    }
                }
                httpIn = httpCon.openInputStream();
                int ch = 0;
                while ((ch = httpIn.read()) != -1) {
                    baos.write(ch);
                }
                responseBin = baos.toByteArray();
                if (responseBin != null && responseBin.length == 0) {
                    responseBin = null;
                }
            }
        } catch (SecurityException se) {
            bError = true;
            L9Util.throwException(se, "�û��ܾ���������!");
        } catch (Exception e) {
            bError = true;
            L9Util.throwException(e, "������;�����쳣!");
        } finally {
            cancelHttp();
            l9Thread = null;
            bInProgress = false;
        }
    }
}
