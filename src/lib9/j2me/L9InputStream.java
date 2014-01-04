package lib9.j2me;

import java.io.*;

/**
 * Lib9引擎的输入流，对于读取int型、short型、long型数据，按照由低到高的的顺序从字节数据读取
 * @author not attributable
 * @version 1.0
 */

public class L9InputStream {
    private byte[] _buf;
    private int _off;

    /**
     * 按照由低到高的的顺序从字节数据读取int，占4个字节
     * @param arr byte[]
     * @param off int
     * @return int
     */
    public static int readIntLow(byte[] arr, int off) {
        int i = ((arr[off++] & 0xFF)) +
                ((arr[off++] & 0xFF) << 8) +
                ((arr[off++] & 0xFF) << 16) +
                ((arr[off++] & 0xFF) << 24);
        if ((arr[off - 1] & (1 << 7)) != 0) { //符号为1表示负数，注意内存中使用的补码表示
            i = i - 1;
            i = ~i;
            i = -i;
        }
        return i;
    }

    /**
     * 按照由低到高的的顺序从字节数据读取long,占8个字节
     * @param arr byte[]
     * @param off int
     * @return long
     */
    public static long readLongLow(byte[] arr, int off) {
        long i = ((long) (arr[off++] & 0xFF)) +
                 (((long) (arr[off++] & 0xFF)) << 8) +
                 (((long) (arr[off++] & 0xFF)) << 16) +
                 (((long) (arr[off++] & 0xFF)) << 24) +
                 (((long) (arr[off++] & 0xFF)) << 32) +
                 (((long) (arr[off++] & 0xFF)) << 40) +
                 (((long) (arr[off++] & 0xFF)) << 48) +
                 (((long) (arr[off++] & 0xFF)) << 56);
        if ((arr[off - 1] & (1 << 7)) != 0) { //符号为1表示负数，注意内存中使用的补码表示
            i = i - 1;
            i = ~i;
            i = -i;
        }
        return i;
    }

    /**
     * 按照由低到高的顺序从字节数据读取Short，占2个字节
     * @param arr byte[]
     * @param off int
     * @return int
     */
    public static short readShortLow(byte[] arr, int off) {
        int i = ((arr[off++] & 0xFF) +
                 ((arr[off++] & 0xFF) << 8));
        if ((arr[off - 1] & (1 << 7)) != 0) { //符号为1表示负数，注意内存中使用的补码表示
            i = i - 1;
            i = ~i;
            i = -i;
        }
        return (short) i;
    }

    private void _Init(byte[] arr, int off, int len) {
        _buf = new byte[len];
        _off = 0;
        System.arraycopy(arr, off, _buf, 0, len);
    }

    /**
     * 从字节数组arr的偏移量off开始取len长度的字节来创建CInputStream输入流
     * @param arr byte[]
     * @param off int
     * @param len int
     */
    public L9InputStream(byte[] arr, int off, int len) {
        _Init(arr, off, len);
    }

    /**
     * 根据java的输入流来创建L9InputStream输入流
     * @param in InputStream
     */
    public L9InputStream(InputStream in) {
        int ch;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            while ((ch = in.read()) != -1) {
                baos.write(ch);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        byte[] rs = baos.toByteArray();

        _Init(rs, 0, rs.length);
    }

    /**
     * 根据文件来创建输入流
     * @param file String
     */
    public L9InputStream(String file) {
        this("".getClass().getResourceAsStream(file));
    }

    /**
     * 返回当前指针的偏移量
     * @return int
     */
    public int getOff() {
        return _off;
    }

    /**
     * 重新设置当前指针偏移量
     * @param off int
     */
    public void setOff(int off) {
        _off = off;
    }

    /**
     * 返回要读取数据的字节数组
     * @return byte[]
     */
    public byte[] getBytes() {
        return _buf;
    }

    /**
     * 读出一个布尔值
     * @return boolean
     */
    public boolean readBoolean() {
        return _buf[_off++] == 1;
    }

    /**
     * 读出一个byte字节
     * @return byte
     */
    public byte readByte() {
        return _buf[_off++];
    }

    /**
     * 读取一个byte字节返回为int值，参数bSigned为true表示有符号数,否则为无符号数，ReadByte(true)等于ReadByte()
     * @param bSigned boolean
     * @return int
     */
    public int readByte(boolean bSigned) {
        if (bSigned) {
            return readByte();
        }
        return readByte() & 0xFF;
    }

    /**
     * 读出len长度的字节数组
     * @param len int
     * @return byte[]
     */
    public byte[] readBytes(int len) {
        if (_buf.length - _off < len) {
            throw new IndexOutOfBoundsException();
        }
        byte[] rs = new byte[len];
        System.arraycopy(_buf, _off, rs, 0, rs.length);
        _off += len;
        return rs;
    }

    /**
     * 读出一个short值，占两个字节
     * @return short
     */
    public short readShort() {
        short s = readShortLow(_buf, _off);
        _off += 2;
        return s;
    }

    /**
     * 读出一个int值，占4个字节
     * @return int
     */
    public int readInt() {
        int s = readIntLow(_buf, _off);
        _off += 4;
        return s;
    }

    /**
     * 读出一个long值,占8个字节
     * @return long
     */
    public long readLong() {
        long s = readLongLow(_buf, _off);
        _off += 8;
        return s;
    }

    /**
     * 按照指定的编码将字节数组创建成字符串
     * @param pData byte[]
     * @param Encode String
     * @return String
     */
    //注意:这里用了一个tmp临时变量来创建字符串
    //String(byte[] bytes, int off, int len, String enc) //索爱的机器有Bug，偏移量不为0时将会非常慢
    public String readString(String Encode) {
        int len = readIntLow(_buf, _off);
        _off += 4;
        String rs = null;
        byte[] tmp = new byte[len];
        System.arraycopy(_buf, _off, tmp, 0, len);
        try {
            rs = new String(tmp, Encode);
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }
        _off += len;
        return rs;
    }

    /**
     * 按UTF-8编码来读取字符串
     * @return String
     */
    public String readString() {
        return readString("UTF-8");
    }

    /**
     * 跳过nBytes个字节
     * @param nBytes int
     */
    public void skipBytes(int nBytes) {
        _off += nBytes;
    }
}
