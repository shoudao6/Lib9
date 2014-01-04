package lib9.j2me;
import java.io.UnsupportedEncodingException;
import javax.microedition.rms.*;

/**
 * Lib9引擎提供的对存储的支持，支持本地存储和网络存储
 * @author not attributable
 * @version 1.0
 */
public class L9Store {
    /**
     * 普通的存储模式写
     */
    public final static int K_Store_Mode_Write = 0;
    /**
     * 网络存储模式写
     */
    public final static int K_Store_Mode_Write_Url = 1;
    /**
     * 普通的存储模式读
     */
    public final static int K_Store_Mode_Read = 2;
    /**
     * 网络的存储模式读
     */
    public final static int K_Store_Mode_Read_Url = 3;
    private int _mode;
    private String _storeName;
    private L9OutputStream _out;
    private L9InputStream _in;
    private L9Http _http;
    private byte[] _head;

    /**
     *  进行联网存储时有时需要设置一些头信息，这样便于服务器来处理数据
     * @param head byte[]
     */
    public void setHead(byte[] head) {
        _head = head;
    }

    /**
     * 根据存储名称和模式来创建一个存储，总共有5个参数，分别是存储名(有可能是url)、模式、头信息、代理、连接超时
     * 注意当进行数据的写操作后，需要调用Save方式才能最后保存进存储中
     * 注意存储名称可能为url地址，比如：http://www.lib9.net/storetest.aspx,这要根据mode来决定
     * head是存储操作时向服务器发送的头数据，服务器可以根据这些数据来进行相应的处理
     * sAgent默认为10.0.0.172:80,当需要使用代理联网，并且代理不是10.0.0.172:80时则需要设置sAgent
     * timeout默认是45秒，这个是第一次联网判断接入点的时间,如果timeout为-1则不判断联网接入点直接联网(如果接入点是不对将连接不上)
     * @param storeName String
     * @param mode int
     * @param head byte[]
     * @param sAgent String
     * @param timeout int
     */
    public L9Store(String storeName, int mode, byte[] head, String sAgent, int timeout) {
        _storeName = storeName;
        _mode = mode;
        _head = head;
        switch (_mode) {
        case K_Store_Mode_Write:
            _out = new L9OutputStream();
            break;
        case K_Store_Mode_Write_Url:
            _out = new L9OutputStream();
            _http = new L9Http();
            break;
        case K_Store_Mode_Read:
            try {
                RecordStore rs = RecordStore.openRecordStore(_storeName, true);
                RecordEnumeration re = rs.enumerateRecords(null, null, false);
                if (re.hasNextElement()) {
                    byte[] rms_data = re.nextRecord();
                    _in = new L9InputStream(rms_data, 0, rms_data.length);
                }
                rs.closeRecordStore();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            break
                    ;
        case K_Store_Mode_Read_Url:
            _http = new L9Http();
            if (sAgent != null && sAgent.length() > 0) {
                setAgent(sAgent);
            }
            setFirstJudgeHttpWay(timeout);
            _http.openHttp(_storeName, head); //开启线程
            break;
        default:
            try {
                throw new Exception("模式错误，只存在读和写两种模式，即0和1");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            break
                    ;
        }
    }

    /**
     * 根据存储名、模式和代理来创建存储,这里给出了4个参数，那么最后一个参数连接超时时间使用默认值45秒
     * @param storeName String
     * @param mode int
     * @param sAgent String
     */
    public L9Store(String storeName, int mode, byte[] bin, String sAgent) {
        this(storeName, mode, bin, sAgent, 45);
    }

    /**
     * 创建存储对象有5个参数，这里给出了3个，那么就意味着其它两个参数使用默认值
     * @param storeName String
     * @param mode int
     * @param bin byte[]
     */
    public L9Store(String storeName, int mode, byte[] bin) {
        this(storeName, mode, bin, null, 45);
    }

    /**
     * 根据存储名和模式来创建存储，如果是联网存储则使用联网默认设置(默认第一个判断接入点和默认代理)
     * @param storeName String
     * @param mode int
     */
    public L9Store(String storeName, int mode) {
        this(storeName, mode, null);
    }

    /**
     * 往存储写入一个布尔值
     * @param b boolean
     */
    public void writeBoolean(boolean b) {
        _out.writeBoolean(b);
    }

    /**
     * 往存储写入一个Byte字节
     * @param bt byte
     */
    public void writeByte(byte bt) {
        _out.writeByte(bt);
    }

    /**
     * 将字节数组的一部分，偏移量off开始取len长度的字节写入存储
     * @param arr byte[]
     * @param off int
     * @param len int
     */
    public void writeBytes(byte[] arr, int off, int len) {
        _out.writeBytes(arr, off, len);
    }

    /**
     * 往存储写入一个short值，占两个字节
     * @param s short
     */
    public void writeShort(short s) {
        _out.writeShort(s);
    }

    /**
     * 往存储写入一个int值，占4个字节
     * @param s int
     */
    public void writeInt(int s) {
        _out.writeInt(s);
    }

    /**
     * 往存储写入一个long值,占8个字节
     * @param s long
     */
    public void writeLong(long s) {
        _out.writeLong(s);
    }

    /**
     * 按照指定的编码将字符串写入存储中
     * @param pStr String
     * @param Encode String
     */
    public void writeString(String pStr, String Encode) {
        _out.writeString(pStr, Encode);
    }

    /**
     * 按UTF-8编码将字符串写入存储中
     * @return String
     */
    public void writeString(String pStr) {
        _out.writeString(pStr);
    }

    private boolean _bInSaving = false;
    /**
     * 保存存储，当进行完所有的写操作后需要调用该方法来保存储存
     */
    public void Save() {
        switch (_mode) {
        case K_Store_Mode_Write:
            try {
                RecordStore.deleteRecordStore(_storeName);
            } catch (Exception ex) {
            }
            try {
                RecordStore rs = RecordStore.openRecordStore(_storeName, true);
                byte[] rms = _out.getBytes();
                rs.addRecord(rms, 0, rms.length);
                rs.closeRecordStore();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            break
                    ;
        case K_Store_Mode_Write_Url:
            _bInSaving = true;
            byte[] bin = _out.getBytes();
            if (_head != null) {
                bin = L9Util.mergeBytes(_head, bin);
            }
            _http.openHttp(_storeName, bin); //开启线程
        }
    }

    /**
     * 设置第一次判断http接入点的超时时间，默认为45秒，第一次http连接默认将会进行判断http接入点，如果timeout为-1则不判断联网接入点直接联网(如果接入点是不对将连接不上)
     * @param timeout int
     */
    public void setFirstJudgeHttpWay(int timeout) {
        _http.setFirstJudgeHttpWay(timeout);
    }

    /**
     * 联网使用Wap接入点的时候要求设置该属性，默认为10.0.0.172:80
     * @param sAgent String
     */
    public void setAgent(String sAgent) {
        _http.setAgent(sAgent);
    }

    /**
     * 返回是否正在处理中
     * @return boolean
     */
    public boolean isInProcess() {
        return _http.isInProcess();
    }

    /**
     * 返回http连接过程是否有错误发生
     * @return boolean
     */
    public boolean isError() {
        return _http.isError();
    }

    //////////////////////////////////////////////////////////读模式/////////////////////////////////////
    /**
     * 从存储中读出一个布尔值
     * @return boolean
     */
    public boolean readBoolean() {
        return _in.readBoolean();
    }

    /**
     * 从存储中读出一个byte字节
     * @return byte
     */
    public byte readByte() {
        return _in.readByte();
    }

    /**
     * 从存储中读取一个byte字节返回为int值，参数bSigned为true表示有符号数,否则为无符号数，readByte(true)等于readByte()
     * @param bSigned boolean
     * @return int
     */
    public int readByte(boolean bSigned) {
        return _in.readByte(bSigned);
    }

    /**
     * 从存储中读出len长度的字节数组
     * @param len int
     * @return byte[]
     */
    public byte[] readBytes(int len) {
        return _in.readBytes(len);
    }

    /**
     * 从存储中读出一个short值，占两个字节
     * @return short
     */
    public short readShort() {
        return _in.readShort();
    }

    /**
     * 从存储中读出一个int值，占4个字节
     * @return int
     */
    public int readInt() {
        return _in.readInt();
    }

    /**
     * 从存储中读出一个long值，占8个字节
     * @return long
     */
    public long readLong() {
        return _in.readLong();
    }

    /**
     * 按照指定的编码从存储中读出字符串
     * @param Encode String
     * @return String
     */
    public String readString(String Encode) {
        return _in.readString(Encode);
    }

    /**
     * 按UTF-8编码来从存储中读取字符串
     * @return String
     */
    public String readString() {
        return _in.readString();
    }

    /**
     * 跳过nBytes个字节
     * @param nBytes int
     */
    public void Skip(int nBytes) {
        _in.skipBytes(nBytes);
    }

    /**
     * 如果是联网存储，需要调用该函数判断是否已经准备存储了，如果是读模式只有该函数返回true才能进行读操作，如果是写模式返回true表示已经保存完毕
     * @return boolean
     */
    public boolean isReady() {
        switch (_mode) {
        case K_Store_Mode_Write_Url:
            if (_bInSaving && !isInProcess() && !isError()) { //表示已经保存完毕
                _bInSaving = false;
                byte[] data = _http.getData();
                if (data != null) {
                    _in = new L9InputStream(data, 0, data.length);
                }
                return true;
            }
            break;
        case K_Store_Mode_Read_Url:
            if (!isInProcess() && !isError()) {
                if (_in == null) {
                    byte[] data = _http.getData();
                    _in = new L9InputStream(data, 0, data.length);
                }
                return true;
            }
            break;
        }
        return false;
    }
}
