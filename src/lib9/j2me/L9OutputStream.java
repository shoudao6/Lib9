package lib9.j2me;

import java.io.UnsupportedEncodingException;

/**
 * Lib9引擎的输出流，对于写入int型、short型、long型数据，按照由低到高的的顺序写入
 * @author not attributable
 * @version 1.0
 */
public class L9OutputStream {
    private byte[] _buf;
    private int _off;

    /**
     * 按照由低到高的的顺序将int数字写入字节数据，占4个字节
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
     * 按照由低到高的的顺序将long数字写入字节数据，占8个字节
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
     * 按照由低到高的的顺序将short数字写入字节数据，占2个字节
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
     * 创建初始容量为capacity大小的L9OutputStream输出流
     * @param capacity int
     */
    public L9OutputStream(int capacity) {
        _off = 0;
        _buf = new byte[capacity];
    }

    /**
     * 预计增加newsize大小，但是如果剩余的空间大于newsize则保持不变，如果小于就多增加32个字节
     * @param newsize int
     */
    private void AutoIncrease(int newsize) {
        if (_buf.length - _off < newsize) {
            _buf = L9Util.addBytes(_buf, newsize + _off - _buf.length + 32);
        }
    }

    /**
     * 写入一个布尔值
     * @param b boolean
     */
    public void writeBoolean(boolean b) {
        AutoIncrease(1);
        _buf[_off++] = (byte) (b ? 1 : 0);
    }

    /**
     * 写入一个Byte字节
     * @param bt byte
     */
    public void writeByte(byte bt) {
        AutoIncrease(1);
        _buf[_off++] = bt;
    }

    /**
     * 将字节数组的一部分，偏移量off开始取len长度的字节写入流中
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
     * 写入一个short值，占两个字节
     * @param s short
     */
    public void writeShort(short s) {
        AutoIncrease(2);
        writeShortLow(_buf, _off, s);
        _off += 2;
    }

    /**
     * 写入一个int值，占4个字节
     * @param s int
     */
    public void writeInt(int s) {
        AutoIncrease(4);
        writeIntLow(_buf, _off, s);
        _off += 4;
    }

    /**
     * 写入一个long值，占8个字节
     * @param s long
     */
    public void writeLong(long s) {
        AutoIncrease(8);
        writeLongLow(_buf, _off, s);
        _off += 8;
    }

    /**
     * 按照指定的编码将字符串写入流中
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
     * 按UTF-8编码来写入字符串
     * @return String
     */
    public void writeString(String pStr) {
        writeString(pStr, "UTF-8");
    }

    /**
     * 返回已经写入流中数据的大小
     * @return int
     */
    public int Size() {
        return _off;
    }

    /**
     * 将当前已经写入流中的为字节数组返回
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
