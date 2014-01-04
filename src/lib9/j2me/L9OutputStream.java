package lib9.j2me;

import java.io.UnsupportedEncodingException;

/**
 * Lib9����������������д��int�͡�short�͡�long�����ݣ������ɵ͵��ߵĵ�˳��д��
 * @author not attributable
 * @version 1.0
 */
public class L9OutputStream {
    private byte[] _buf;
    private int _off;

    /**
     * �����ɵ͵��ߵĵ�˳��int����д���ֽ����ݣ�ռ4���ֽ�
     * @param arr byte[]
     * @param off int
     * @param i int
     */
    public static void writeIntLow(byte[] arr, int off, int i) {
        arr[off++] = (byte) (i & 0x000000FF);
        arr[off++] = (byte) ((i & 0x0000FF00) >> 8);
        arr[off++] = (byte) ((i & 0x00FF0000) >> 16);
        arr[off++] = (byte) ((i & 0xFF000000) >> 24);
    }

    /**
     * �����ɵ͵��ߵĵ�˳��long����д���ֽ����ݣ�ռ8���ֽ�
     * @param arr byte[]
     * @param off int
     * @param i long
     */
    public static void writeLongLow(byte[] arr, int off, long i) {
        arr[off++] = (byte) (i & 0x000000FF);
        arr[off++] = (byte) ((i & 0x000000000000FF00L) >> 8);
        arr[off++] = (byte) ((i & 0x0000000000FF0000L) >> 16);
        arr[off++] = (byte) ((i & 0x00000000FF000000L) >> 24);
        arr[off++] = (byte) ((i & 0x000000FF00000000L) >> 32);
        arr[off++] = (byte) ((i & 0x0000FF0000000000L) >> 40);
        arr[off++] = (byte) ((i & 0x00FF000000000000L) >> 48);
        arr[off++] = (byte) ((i & 0xFF00000000000000L) >> 56);
    }

    /**
     * �����ɵ͵��ߵĵ�˳��short����д���ֽ����ݣ�ռ2���ֽ�
     * @param arr byte[]
     * @param off int
     * @param i int
     */
    public static void writeShortLow(byte[] arr, int off, short i) {
        arr[off++] = (byte) ((i & 0x00FF));
        arr[off++] = (byte) ((i & 0xFF00) >> 8);
    }

    public L9OutputStream() {
        this(32);
    }

    /**
     * ������ʼ����Ϊcapacity��С��L9OutputStream�����
     * @param capacity int
     */
    public L9OutputStream(int capacity) {
        _off = 0;
        _buf = new byte[capacity];
    }

    /**
     * Ԥ������newsize��С���������ʣ��Ŀռ����newsize�򱣳ֲ��䣬���С�ھͶ�����32���ֽ�
     * @param newsize int
     */
    private void AutoIncrease(int newsize) {
        if (_buf.length - _off < newsize) {
            _buf = L9Util.addBytes(_buf, newsize + _off - _buf.length + 32);
        }
    }

    /**
     * д��һ������ֵ
     * @param b boolean
     */
    public void writeBoolean(boolean b) {
        AutoIncrease(1);
        _buf[_off++] = (byte) (b ? 1 : 0);
    }

    /**
     * д��һ��Byte�ֽ�
     * @param bt byte
     */
    public void writeByte(byte bt) {
        AutoIncrease(1);
        _buf[_off++] = bt;
    }

    /**
     * ���ֽ������һ���֣�ƫ����off��ʼȡlen���ȵ��ֽ�д������
     * @param arr byte[]
     * @param off int
     * @param len int
     */
    public void writeBytes(byte[] arr, int off, int len) {
        if (arr == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > arr.length) || (len < 0) ||
                   ((off + len) > arr.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len <= 0) {
            return;
        }
        for (int i = 0; i < len; i++) {
            writeByte(arr[off + i]);
        }
    }

    /**
     * д��һ��shortֵ��ռ�����ֽ�
     * @param s short
     */
    public void writeShort(short s) {
        AutoIncrease(2);
        writeShortLow(_buf, _off, s);
        _off += 2;
    }

    /**
     * д��һ��intֵ��ռ4���ֽ�
     * @param s int
     */
    public void writeInt(int s) {
        AutoIncrease(4);
        writeIntLow(_buf, _off, s);
        _off += 4;
    }

    /**
     * д��һ��longֵ��ռ8���ֽ�
     * @param s long
     */
    public void writeLong(long s) {
        AutoIncrease(8);
        writeLongLow(_buf, _off, s);
        _off += 8;
    }

    /**
     * ����ָ���ı��뽫�ַ���д������
     * @param pStr String
     * @param Encode String
     */
    public void writeString(String pStr, String Encode) {
        byte[] rs = L9Util.getStringBytesWithLen(pStr, Encode);
        AutoIncrease(rs.length);
        System.arraycopy(rs, 0, _buf, _off, rs.length);
        _off += rs.length;
    }

    /**
     * ��UTF-8������д���ַ���
     * @return String
     */
    public void writeString(String pStr) {
        writeString(pStr, "UTF-8");
    }

    /**
     * �����Ѿ�д���������ݵĴ�С
     * @return int
     */
    public int Size() {
        return _off;
    }

    /**
     * ����ǰ�Ѿ�д�����е�Ϊ�ֽ����鷵��
     * @return byte[]
     */
    public byte[] getBytes() {
        if (_off < 1) {
            return null;
        }
        byte[] rs = new byte[_off];
        System.arraycopy(_buf, 0, rs, 0, rs.length);
        return rs;
    }
}
