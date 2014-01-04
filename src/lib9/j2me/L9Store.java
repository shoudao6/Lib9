package lib9.j2me;
import java.io.UnsupportedEncodingException;
import javax.microedition.rms.*;

/**
 * Lib9�����ṩ�ĶԴ洢��֧�֣�֧�ֱ��ش洢������洢
 * @author not attributable
 * @version 1.0
 */
public class L9Store {
    /**
     * ��ͨ�Ĵ洢ģʽд
     */
    public final static int K_Store_Mode_Write = 0;
    /**
     * ����洢ģʽд
     */
    public final static int K_Store_Mode_Write_Url = 1;
    /**
     * ��ͨ�Ĵ洢ģʽ��
     */
    public final static int K_Store_Mode_Read = 2;
    /**
     * ����Ĵ洢ģʽ��
     */
    public final static int K_Store_Mode_Read_Url = 3;
    private int _mode;
    private String _storeName;
    private L9OutputStream _out;
    private L9InputStream _in;
    private L9Http _http;
    private byte[] _head;

    /**
     *  ���������洢ʱ��ʱ��Ҫ����һЩͷ��Ϣ���������ڷ���������������
     * @param head byte[]
     */
    public void setHead(byte[] head) {
        _head = head;
    }

    /**
     * ���ݴ洢���ƺ�ģʽ������һ���洢���ܹ���5���������ֱ��Ǵ洢��(�п�����url)��ģʽ��ͷ��Ϣ���������ӳ�ʱ
     * ע�⵱�������ݵ�д��������Ҫ����Save��ʽ������󱣴���洢��
     * ע��洢���ƿ���Ϊurl��ַ�����磺http://www.lib9.net/storetest.aspx,��Ҫ����mode������
     * head�Ǵ洢����ʱ����������͵�ͷ���ݣ����������Ը�����Щ������������Ӧ�Ĵ���
     * sAgentĬ��Ϊ10.0.0.172:80,����Ҫʹ�ô������������Ҵ�����10.0.0.172:80ʱ����Ҫ����sAgent
     * timeoutĬ����45�룬����ǵ�һ�������жϽ�����ʱ��,���timeoutΪ-1���ж����������ֱ������(���������ǲ��Խ����Ӳ���)
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
            _http.openHttp(_storeName, head); //�����߳�
            break;
        default:
            try {
                throw new Exception("ģʽ����ֻ���ڶ���д����ģʽ����0��1");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            break
                    ;
        }
    }

    /**
     * ���ݴ洢����ģʽ�ʹ����������洢,���������4����������ô���һ���������ӳ�ʱʱ��ʹ��Ĭ��ֵ45��
     * @param storeName String
     * @param mode int
     * @param sAgent String
     */
    public L9Store(String storeName, int mode, byte[] bin, String sAgent) {
        this(storeName, mode, bin, sAgent, 45);
    }

    /**
     * �����洢������5�����������������3������ô����ζ��������������ʹ��Ĭ��ֵ
     * @param storeName String
     * @param mode int
     * @param bin byte[]
     */
    public L9Store(String storeName, int mode, byte[] bin) {
        this(storeName, mode, bin, null, 45);
    }

    /**
     * ���ݴ洢����ģʽ�������洢������������洢��ʹ������Ĭ������(Ĭ�ϵ�һ���жϽ�����Ĭ�ϴ���)
     * @param storeName String
     * @param mode int
     */
    public L9Store(String storeName, int mode) {
        this(storeName, mode, null);
    }

    /**
     * ���洢д��һ������ֵ
     * @param b boolean
     */
    public void writeBoolean(boolean b) {
        _out.writeBoolean(b);
    }

    /**
     * ���洢д��һ��Byte�ֽ�
     * @param bt byte
     */
    public void writeByte(byte bt) {
        _out.writeByte(bt);
    }

    /**
     * ���ֽ������һ���֣�ƫ����off��ʼȡlen���ȵ��ֽ�д��洢
     * @param arr byte[]
     * @param off int
     * @param len int
     */
    public void writeBytes(byte[] arr, int off, int len) {
        _out.writeBytes(arr, off, len);
    }

    /**
     * ���洢д��һ��shortֵ��ռ�����ֽ�
     * @param s short
     */
    public void writeShort(short s) {
        _out.writeShort(s);
    }

    /**
     * ���洢д��һ��intֵ��ռ4���ֽ�
     * @param s int
     */
    public void writeInt(int s) {
        _out.writeInt(s);
    }

    /**
     * ���洢д��һ��longֵ,ռ8���ֽ�
     * @param s long
     */
    public void writeLong(long s) {
        _out.writeLong(s);
    }

    /**
     * ����ָ���ı��뽫�ַ���д��洢��
     * @param pStr String
     * @param Encode String
     */
    public void writeString(String pStr, String Encode) {
        _out.writeString(pStr, Encode);
    }

    /**
     * ��UTF-8���뽫�ַ���д��洢��
     * @return String
     */
    public void writeString(String pStr) {
        _out.writeString(pStr);
    }

    private boolean _bInSaving = false;
    /**
     * ����洢�������������е�д��������Ҫ���ø÷��������洢��
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
            _http.openHttp(_storeName, bin); //�����߳�
        }
    }

    /**
     * ���õ�һ���ж�http�����ĳ�ʱʱ�䣬Ĭ��Ϊ45�룬��һ��http����Ĭ�Ͻ�������ж�http����㣬���timeoutΪ-1���ж����������ֱ������(���������ǲ��Խ����Ӳ���)
     * @param timeout int
     */
    public void setFirstJudgeHttpWay(int timeout) {
        _http.setFirstJudgeHttpWay(timeout);
    }

    /**
     * ����ʹ��Wap������ʱ��Ҫ�����ø����ԣ�Ĭ��Ϊ10.0.0.172:80
     * @param sAgent String
     */
    public void setAgent(String sAgent) {
        _http.setAgent(sAgent);
    }

    /**
     * �����Ƿ����ڴ�����
     * @return boolean
     */
    public boolean isInProcess() {
        return _http.isInProcess();
    }

    /**
     * ����http���ӹ����Ƿ��д�����
     * @return boolean
     */
    public boolean isError() {
        return _http.isError();
    }

    //////////////////////////////////////////////////////////��ģʽ/////////////////////////////////////
    /**
     * �Ӵ洢�ж���һ������ֵ
     * @return boolean
     */
    public boolean readBoolean() {
        return _in.readBoolean();
    }

    /**
     * �Ӵ洢�ж���һ��byte�ֽ�
     * @return byte
     */
    public byte readByte() {
        return _in.readByte();
    }

    /**
     * �Ӵ洢�ж�ȡһ��byte�ֽڷ���Ϊintֵ������bSignedΪtrue��ʾ�з�����,����Ϊ�޷�������readByte(true)����readByte()
     * @param bSigned boolean
     * @return int
     */
    public int readByte(boolean bSigned) {
        return _in.readByte(bSigned);
    }

    /**
     * �Ӵ洢�ж���len���ȵ��ֽ�����
     * @param len int
     * @return byte[]
     */
    public byte[] readBytes(int len) {
        return _in.readBytes(len);
    }

    /**
     * �Ӵ洢�ж���һ��shortֵ��ռ�����ֽ�
     * @return short
     */
    public short readShort() {
        return _in.readShort();
    }

    /**
     * �Ӵ洢�ж���һ��intֵ��ռ4���ֽ�
     * @return int
     */
    public int readInt() {
        return _in.readInt();
    }

    /**
     * �Ӵ洢�ж���һ��longֵ��ռ8���ֽ�
     * @return long
     */
    public long readLong() {
        return _in.readLong();
    }

    /**
     * ����ָ���ı���Ӵ洢�ж����ַ���
     * @param Encode String
     * @return String
     */
    public String readString(String Encode) {
        return _in.readString(Encode);
    }

    /**
     * ��UTF-8�������Ӵ洢�ж�ȡ�ַ���
     * @return String
     */
    public String readString() {
        return _in.readString();
    }

    /**
     * ����nBytes���ֽ�
     * @param nBytes int
     */
    public void Skip(int nBytes) {
        _in.skipBytes(nBytes);
    }

    /**
     * ����������洢����Ҫ���øú����ж��Ƿ��Ѿ�׼���洢�ˣ�����Ƕ�ģʽֻ�иú�������true���ܽ��ж������������дģʽ����true��ʾ�Ѿ��������
     * @return boolean
     */
    public boolean isReady() {
        switch (_mode) {
        case K_Store_Mode_Write_Url:
            if (_bInSaving && !isInProcess() && !isError()) { //��ʾ�Ѿ��������
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
