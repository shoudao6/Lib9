package lib9.j2me;

import javax.microedition.io.HttpConnection;
import java.io.InputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import javax.microedition.io.Connector;

/**
 * Lib9引擎通过该类提供了对联网的支持，支持http方式联网(服务器端为动态网页)，采用post方式传递数据
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
     * 返回从服务器获取的数据
     * @return byte[]
     */
    public byte[] getData() {
        return responseBin;
    }

    /**
     * 返回是否正在http连接中
     * @return boolean
     */
    public boolean isInProcess() {
        return bInProgress;
    }

    /**
     * 返回http连接过程是否有错误发生
     * @return boolean
     */
    public boolean isError() {
        return bError;
    }

    private static boolean bFirstHttpConnection = true;
    private int HttpConnectionTimeOut = 45;
    /**
     * 设置第一次判断http接入点的超时时间，默认为45秒，第一次http连接默认将会进行判断http接入点，如果timeout为-1则不判断联网接入点直接联网
     * 有两种接入点联网Net和Wap,默认使用Net接入点
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
     * 联网使用Wap接入点的时候要求设置该属性，默认为10.0.0.172:80
     * @param sAgent String
     */
    public void setAgent(String sAgent) {
        httpAgent = sAgent;
    }

    private HttpConnection httpCon = null;
    private OutputStream httpOut = null;
    private InputStream httpIn = null;
    /**
     * 取消http连接
     */
    public void cancelHttp() {
        bCanceled = true;
    }

    /**
     * 关闭http连接并释放有关资源
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
     * 进行http连接，使用post方式来传递数据,head为向服务器传递的数据
     * @param url String
     * @param head byte[]
     */
    public void openHttp(String url, byte[] head) {
        while (bInProgress) { //当前正在处理
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

    private static boolean bHttpNet = true; //默认使用Net的方式联网
    /**
     * 返回true表示联网使用net接入点，否则使用wap接入点
     * @return boolean
     */
    public boolean getHttpWay() {
        return bHttpNet;
    }

    /**
     * 设置Wap或者Net接入点，默认为Net接入点,如果指定了接入点那么第一次联网时将不会再进行接入点判断了
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
            if (bFirstHttpConnection) { //第一次需要判断网络连接,确定用户是使用Wap还是Net接入点
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
                    if (Net_Time > HttpConnectionTimeOut * 1000) { //联网时间大于超时时间就切换接入点
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
//不要设置User-Agent这个字段，否则有的手机会出现bad request(invalid header name)错误
//      c.setRequestProperty("User-Agent",
//                           "Profile/MIDP-2.0 Configuration/CLDC-1.0");
            httpCon.setRequestProperty("Content-Type", Content_Type);
            //c.setRequestProperty("Connection", "Keep-Alive");
            httpCon.setRequestProperty("Accept", "*/*");
//      c.setRequestProperty("Content-Length",""+my_head_info.length);
            if (postData != null) {
                httpOut = httpCon.openOutputStream();
                httpOut.write(postData);
                //flush函数可能造成HTTP_INTERNAL_ERROR
                //      out.flush();
                httpOut.close();
            }
            int status = httpCon.getResponseCode();
            if (status != HttpConnection.HTTP_OK) { //注册或者找回信息判断连接网络 连接超时
                closeHttp();
                bError = true;
                throw new Exception("status=" + status);
            } else {
//处理移动资费页面
                String s = httpCon.getHeaderField("Content-Type");
                if (s != null) {
                    s = s.toLowerCase();
                    if (!s.startsWith(Content_Type)) { //如果返回的结果不是设置的类型则重新连接
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
            L9Util.throwException(se, "用户拒绝联网请求!");
        } catch (Exception e) {
            bError = true;
            L9Util.throwException(e, "联网中途出现异常!");
        } finally {
            cancelHttp();
            l9Thread = null;
            bInProgress = false;
        }
    }
}
