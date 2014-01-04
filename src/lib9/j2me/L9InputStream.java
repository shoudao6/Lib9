package lib9.j2me;

import java.io.*;

/**
 * Lib9����������������ڶ�ȡint�͡�short�͡�long�����ݣ������ɵ͵��ߵĵ�˳����ֽ����ݶ�ȡ
 * @author not attributable
 * @version 1.0
 */

public class L9InputStream {
    private byte[] _buf;
    private int _off;

    /**
     * �����ɵ͵��ߵĵ�˳����ֽ����ݶ�ȡint��ռ4���ֽ�
     * @param arr byte[]
     * @param off int
     * @return int
     */
    public static int readIntLow(byte[] arr, int off) {
        int i = ((arr[off++] & 0xFF)) +
                ((arr[off++] & 0xFF) << 8) +
                ((arr[off++] & 0xFF) << 16) +
                ((arr[off++] & 0xFF) << 24);
        if ((arr[off - 1] & (1 << 7)) != 0) { //����Ϊ1��ʾ������ע���ڴ���ʹ�õĲ����ʾ
            i = i - 1;
            i = ~i;
            i = -i;
        }
        return i;
    }

    /**
     * �����ɵ͵��ߵĵ�˳����ֽ����ݶ�ȡlong,ռ8���ֽ�
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
        if ((arr[off - 1] & (1 << 7)) != 0) { //����Ϊ1��ʾ������ע���ڴ���ʹ�õĲ����ʾ
            i = i - 1;
            i = ~i;
            i = -i;
        }
        return i;
    }

    /**
     * �����ɵ͵��ߵ�˳����ֽ����ݶ�ȡShort��ռ2���ֽ�
     * @param arr byte[]
     * @param off int
     * @return int
     */
    public static short readShortLow(byte[] arr, int off) {
        int i = ((arr[off++] & 0xFF) +
                 ((arr[off++] & 0xFF) << 8));
        if ((arr[off - 1] & (1 << 7)) != 0) { //����Ϊ1��ʾ������ע���ڴ���ʹ�õĲ����ʾ
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
     * ���ֽ�����arr��ƫ����off��ʼȡlen���ȵ��ֽ�������CInputStream������
     * @param arr byte[]
     * @param off int
     * @param len int
     */
    public L9InputStream(byte[] arr, int off, int len) {
        _Init(arr, off, len);
    }

    /**
     * ����java��������������L9InputStream������
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
     * �����ļ�������������
     * @param file String
     */
    public L9InputStream(String file) {
        this("".getClass().getResourceAsStream(file));
    }

    /**
     * ���ص�ǰָ���ƫ����
     * @return int
     */
    public int getOff() {
        return _off;
    }

    /**
     * �������õ�ǰָ��ƫ����
     * @param off int
     */
    public void setOff(int off) {
        _off = off;
    }

    /**
     * ����Ҫ��ȡ���ݵ��ֽ�����
     * @return byte[]
     */
    public byte[] getBytes() {
        return _buf;
    }

    /**
     * ����һ������ֵ
     * @return boolean
     */
    public boolean readBoolean() {
        return _buf[_off++] == 1;
    }

    /**
     * ����һ��byte�ֽ�
     * @return byte
     */
    public byte readByte() {
        return _buf[_off++];
    }

    /**
     * ��ȡһ��byte�ֽڷ���Ϊintֵ������bSignedΪtrue��ʾ�з�����,����Ϊ�޷�������ReadByte(true)����ReadByte()
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
     * ����len���ȵ��ֽ�����
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
     * ����һ��shortֵ��ռ�����ֽ�
     * @return short
     */
    public short readShort() {
        short s = readShortLow(_buf, _off);
        _off += 2;
        return s;
    }

    /**
     * ����һ��intֵ��ռ4���ֽ�
     * @return int
     */
    public int readInt() {
        int s = readIntLow(_buf, _off);
        _off += 4;
        return s;
    }

    /**
     * ����һ��longֵ,ռ8���ֽ�
     * @return long
     */
    public long readLong() {
        long s = readLongLow(_buf, _off);
        _off += 8;
        return s;
    }

    /**
     * ����ָ���ı��뽫�ֽ����鴴�����ַ���
     * @param pData byte[]
     * @param Encode String
     * @return String
     */
    //ע��:��������һ��tmp��ʱ�����������ַ���
    //String(byte[] bytes, int off, int len, String enc) //�����Ļ�����Bug��ƫ������Ϊ0ʱ����ǳ���
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
     * ��UTF-8��������ȡ�ַ���
     * @return String
     */
    public String readString() {
        return readString("UTF-8");
    }

    /**
     * ����nBytes���ֽ�
     * @param nBytes int
     */
    public void skipBytes(int nBytes) {
        _off += nBytes;
    }
}
