package lib9.j2me;

import javax.microedition.lcdui.*;
import javax.microedition.io.*;
import javax.wireless.messaging.*;
import java.io.*;
import java.util.*;
import javax.microedition.rms.*;
import javax.microedition.media.*;
import javax.microedition.media.control.*;

/**
 * Lib9�����ṩ��һЩ���ù��ܵļ��ϣ����磺��ȡĳ�������ڵ������
 * @author not attributable
 * @version 1.0
 */
public class L9Util {
    public L9Util() {
    }

    /////////////////////////////////////////////////////�й�������Ĳ���/////////////////////////////////////////////////////////////////////////
    private static Random rd = new Random();
    /**
     * ��[lower,upper)֮�����ȡһ��ֵ
     * @param lower int
     * @param upper int
     * @return int
     */
    public static int getRandValue(int lower, int upper) {
        Random rd = new Random();
        int value = rd.nextInt() % upper;
        while (value < lower) {
            value = rd.nextInt(upper);
        }
        return value;
    }

    /**
     * ��[lower,upper)֮�����ȡһ��ֵ,Ҫ������val
     * @param val int
     * @param lower int
     * @param upper int
     * @return int
     */
    public static int getDiffRandValue(int val, int lower, int upper) {
        int tmp = -1;
        while (true) {
            tmp = getRandValue(lower, upper);
            if (val != tmp) {
                break;
            }
        }
        return tmp;
    }

    /**
     * ��[lower,upper)֮�����ȡһ��ֵ,Ҫ������arr�е��κ�һ��
     * @param arr int[]
     * @param lower int
     * @param upper int
     * @return int
     */
    public static int getDiffArrRandValue(int[] arr, int lower, int upper) {
        int tmp = -1, i;
        while (true) {
            tmp = getRandValue(lower, upper);
            for (i = 0; arr != null && i < arr.length; i++) {
                if (tmp == arr[i]) {
                    break;
                }
            }
            if (i == arr.length) {
                break;
            }
        }
        return tmp;
    }

    /**
     * ����size��С�Ҳ��ظ�����������
     * @param size int
     * @param lower int
     * @param upper int
     * @return int[]
     */
    public static int[] getDiffArray(int size, int lower, int upper) {
        if (size > upper - lower) {
            try {
                throw new IllegalArgumentException(
                        "�������Ϸ�! size����ҪС��upper - lower");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        int[] rndArr = new int[size];
        for (int i = 0; i < rndArr.length; i++) {
            rndArr[i] = -1;
        }

        for (int i = 0; i < rndArr.length; i++) {
            rndArr[i] = getDiffArrRandValue(rndArr, lower, upper);
        }
        return rndArr;
    }

    /////////////////c++��c#���� �ȶ���д���ֽ� javaȴ�����෴���ȶ���д���ֽ�/////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * ����Short���ֵ��ֽ����飬˳���ɵ͵��ߣ�ռ2λ
     * @param i int
     * @return byte[]
     */
    public static byte[] getShortBytes(short i) {
        byte[] head = new byte[4];
        L9OutputStream.writeShortLow(head, 0, i);
        return head;
    }

    /**
     * �����������ֵ��ֽ����飬˳���ɵ͵���
     * @param i int
     * @return byte[]
     */
    public static byte[] getIntBytes(int i) {
        byte[] head = new byte[4];
        L9OutputStream.writeIntLow(head, 0, i);
        return head;
    }

    /**
     * �����������ֵ��ֽ����飬˳���ɵ͵���
     * @param i int
     * @return byte[]
     */
    public static byte[] getLongBytes(long i) {
        byte[] head = new byte[8];
        L9OutputStream.writeLongLow(head, 0, i);
        return head;
    }

    /**
     * �����ַ�������Encodeָ��������ֽ�����
     * @param Str String
     * @param Encode String
     * @return byte[]
     */
    public static byte[] getStringBytes(String Str, String Encode) {
        byte[] rs = null;
        try {
            rs = Str.getBytes(Encode);
        } catch (UnsupportedEncodingException ex) {
        }
        return rs;
    }

    /**
     * �����ַ�������Encodeָ��������ֽ�����,ǰ����г���,ռ4���ֽ�
     * @param Str String
     * @param Encode String
     * @return byte[]
     */
    public static byte[] getStringBytesWithLen(String Str, String Encode) {
        byte[] tmp = getStringBytes(Str, Encode);
        byte[] rs = new byte[4 + tmp.length];
        L9OutputStream.writeIntLow(rs, 0, tmp.length);
        System.arraycopy(tmp, 0, rs, 4, tmp.length);
        return rs;
    }

    /////////////////////////////////ѹ���㷨//////////////////////////////////////
    /**
     * ����ѹ���㷨��ͷ���,bUse_Flag_HeadΪtrue���������������
     * @param flag byte[]
     * @param Num int
     */
    private static void Set_Flag(byte[] flag, int Num, boolean bLz77HeadFlag) {
        if (bLz77HeadFlag) { //Ϊ���ǲ�������
            int index = Num / 8;
            int pos = Num - index * 8;
            flag[index] |= 1 << pos;
        }
    }

    /**
     * ȡ��ѹ���㷨��ͷ���,bUse_Flag_HeadΪtrue���������������
     * @param flag byte[]
     * @param Num int
     * @return boolean
     */
    private static boolean Get_Flag(byte[] flag, int Num, boolean bLz77HeadFlag) {
        if (bLz77HeadFlag) { //Ϊ���ǲ�������
            int index = Num / 8;
            int pos = Num - index * 8;
            return (flag[index] & (1 << pos)) != 0;
        }
        return false;
    }

//ѹ�����ݴ洢�ṹ Flag+Data
//Flag���������ԭ���ݻ�����Ԫ������,DataΪԭ���ݻ�����Ԫ������
    /**
     * LZ77ѹ���㷨�Ļ��������ֽڱ���
     */
    private static byte[] _pWin = null;
    /**
     * �������ڵĴ�С�����ڳ�������һ���ֽ�����ʾ��ƫ�����ͳ��� ,���Դ��ڳ��Ȳ��ܴ���255
     */
    private static int _pWinLen = 255; //ע�����ڳ�������һ���ֽ�����ʾ��ƫ�����ͳ��� ,���Դ��ڳ��Ȳ��ܴ���255

    private static byte[] _pTmpBuf = null;

    private static int _lz77_off = 0;
    private static int _lz77_len = 0;
    /**
     * ʹ��LZ77ѹ��ǰӦ���ȳ�ʼ����������,���ڳ�������һ���ֽ�����ʾ��ƫ�����ͳ��� ,���Դ��ڳ��Ȳ��ܴ���255
     * @param pByte byte[]
     * @param off int
     * @param wLen int
     */
    private static void LZ77_Init_Win(byte[] pByte, int off, int wLen) {
        _pWinLen = wLen;
        _pWin = new byte[_pWinLen];
        _pTmpBuf = new byte[_pWinLen];
        for (int i = 0; i < _pWinLen; i++) {
            _pWin[i] = pByte[i + off];
        }
        _lz77_off = 0;
        _lz77_len = 0;
    }

    /**
     * ���LZ77����������ռ���ڴ�����,java�в��ã���Ϊ�������ռ�����
     */
    private static void LZ77_CleanUp_Win() {
        /*
            if (pWin)
            {
                delete[] pWin;
            }
         */
    }

    /**
     * �ڻ���������Ѱ��sLen����s��λ��
     * @param s byte[]
     * @param sLen int
     * @return int
     */
    private static int LZ77_AnsiPos(byte[] s, int sLen) {
        for (int i = 0; i < _pWinLen - sLen + 1; i++) {
            int j = 0;
            for (; j < sLen; j++) {
                if (_pWin[i + j] != s[j]) {
                    break;
                }
            }
            if (j == sLen) {
                return i;
            }
        }
        return -1;
    }

    /**
     * �ڻ���������Ѱ���ƥ�䣬����ƥ���ַ����ڴ����е�ƫ������ƥ���ַ�������(_lz77_off��_lz77_len)
     * pByteΪ��ѹ������,pByteLenΪѹ�����ݵĳ���,pByteOffΪ��ǰƫ���� Ҳ����˵ֻ��pByteOff��pByteLen�в���ƥ��
     * @param pByte byte[]
     * @param pByteLen int
     * @param pByteOff int
     * @return boolean
     */
    private static boolean LZ77_Find_Max_Str(byte[] pByte, int pByteLen,
                                             int pByteOff) {
        boolean bFind = false;
        _lz77_off = 0;
        _lz77_len = 0;
        for (int i = 0; i < _pWinLen && i < pByteLen - pByteOff; i++) {
            _pTmpBuf[i] = pByte[pByteOff + i];
            int index = LZ77_AnsiPos(_pTmpBuf, i + 1);
            if (index == -1) {
                if (i == 0) {
                    return false;
                }
                return true;
            }
            _lz77_off = index;
            _lz77_len = i + 1;
            bFind = true;
        }
        return bFind;
    }

    /**
     * �ƶ��������� cΪѹ�����ݵ���һ���ֽ�
     * @param c byte
     */
    private static void LZ77_Move_Scroll_Window(byte c) {
        //byte[] pWin = new byte[_lz77_len];
        for (int i = 0; i < _lz77_len; i++) {
            _pTmpBuf[i] = _pWin[_lz77_off + i];
        }

        if (_lz77_len == _pWinLen - 1 || _lz77_len == _pWinLen) {
            for (int i = 1; i < _lz77_len; i++) {
                _pWin[i - 1] = _pTmpBuf[i];
            }
        } else {
            for (int i = _lz77_len + 1; i < _pWinLen; i++) {
                _pWin[i - (_lz77_len + 1)] = _pWin[i];
            }

            for (int i = 0; i < _lz77_len; i++) {
                _pWin[_pWinLen - (_lz77_len + 1) + i] = _pTmpBuf[i];
            }
        }
        _pWin[_pWinLen - 1] = c;

    }

    /**
     * Lz77ѹ���㷨��pByteΪҪѹ�������ݣ���Ҫѹ��������pByte�д�ƫ����wOff��ʼȡwLen���ַ���Ϊ�����������ݣ�ͷ�����Ϊtrue ����ѹ��,wLen���Ȳ��ܴ���255
     * @param pByte byte[]
     * @param wOff int
     * @param wLen int
     * @return byte[]
     */
    public static byte[] LZ77_Encode(byte[] pByte, int wOff, int wLen) {
        return LZ77_Encode(pByte, pByte, wOff, wLen, true);
    }

    /**
     * Lz77ѹ���㷨��pByteΪҪѹ�������ݣ���Ҫѹ��������pByte��ȡǰ255���ַ���Ϊ�����������ݣ�ͷ�����Ϊtrue ����ѹ��
     * ������ݳ���С��255��ѹ��ֱ�ӷ���ԭ����
     * @param pByte byte[]
     * @return byte[]
     */
    public static byte[] LZ77_Encode(byte[] pByte) {
        if (pByte == null || pByte.length < 255) {
            return pByte;
        }
        return LZ77_Encode(pByte, pByte, 0, 255, true);
    }

    /**
     * Lz77ѹ���㷨��pByteΪҪѹ�������ݣ�pWin��wOff��wLen��ʾ��pWin�����д�wOffƫ������ȡ��wLen���ȵ�������Ϊ�����������ݣ�bLz77HeadFlag��ʾ�Ƿ�ʹ��ͷ����ǵ�ѹ������
     * @param pByte byte[]
     * @param pWin byte[]
     * @param wOff int
     * @param wLen int
     * @param bLz77HeadFlag boolean
     * @return byte[]
     */
    public static byte[] LZ77_Encode(byte[] pByte, byte[] pWin, int wOff,
                                     int wLen, boolean bLz77HeadFlag) {
        LZ77_Init_Win(pWin, wOff, wLen); //��ʼ��

        int pByteLen = pByte.length;
//        byte[] pRs = new byte[pByteLen * 3 / 4]; //��ʼ��Ԥ��ѹ�����СΪԭ��С��75%
        L9OutputStream l9Out = new L9OutputStream();

        byte[] nMaxByteFlag = null;
//////////ע�� �󲿷ֵ�JVM���ܻ�Ĭ�ϳ�ʼ��,��������������Ĭ�ϳ�ʼ����JVM�ͻ�����⣬�����ڴ˳�ʼ��////////
        if (bLz77HeadFlag) {
            nMaxByteFlag = new byte[pByteLen / 8 + 1];
            for (int i = 0; i < pByteLen / 8 + 1; i++) {
                nMaxByteFlag[i] = 0;
            }
        }
/////////////////////////////////////////////////////////////////////////////////////////////
        int iCount = 0;
//        int iRs = 0;
//        if (!bLz77HeadFlag) {
//            //pRs[iRs++] = 0; //ͷ�����
//            l9Out.writeBoolean(false);
//        }
        for (int i = 0; i < pByteLen; ) {
            //��ֹpRs����Խ��,һ���������ѹ������ٴ�С�������ڵ���LZ77_Encode����ʱ�Ͳ���Ҫ���������ֵ�������Ϳ��Լ����ڴ��ʹ��
            //int max_rs_len = 3 * w * h + 4 + 1; //ѹ��������
//            if (iRs + 4 > pRs.length) { //��Ԫ��ֻ���3
//                byte[] tmp_byte = pRs;
//                pRs = new byte[tmp_byte.length + 1024];
//                System.arraycopy(tmp_byte, 0, pRs, 0, tmp_byte.length);
//            }
            //////////////////////////////////////////////////////////////////////////

            boolean bFind = LZ77_Find_Max_Str(pByte, pByteLen, i);
            if (bLz77HeadFlag) { //Ҫ�����ƥ�����3����Ȼ��������ѹ��
                if (bFind) {
                    if (_lz77_len <= 3) {
                        bFind = false;
                        _lz77_off = 0;
                        _lz77_len = 0;
                    }
                }
            }
            //System.out.println(bFind+","+lz77_off+","+lz77_len);
            if (bFind) {
                iCount++;
                if (_lz77_len == pByteLen - i) { //����
                    //pRs[iRs++] = (byte) _lz77_off;
                    //pRs[iRs++] = (byte) _lz77_len;
                    l9Out.writeByte((byte) _lz77_off);
                    l9Out.writeByte((byte) _lz77_len);

                    //pRs[iRs++] = '\0';
                    break;
                } else {
                    //���off,len,c
                    byte c = pByte[i + _lz77_len];
//                    pRs[iRs++] = (byte) _lz77_off;
//                    pRs[iRs++] = (byte) _lz77_len;
//                    pRs[iRs++] = c;
                    l9Out.writeByte((byte) _lz77_off);
                    l9Out.writeByte((byte) _lz77_len);
                    l9Out.writeByte(c);
                    //�ı们������
                    LZ77_Move_Scroll_Window(c);
                }
                i = i + _lz77_len + 1;
                //System.out.println("_lz77_len=" + _lz77_len);
            } else {
                //���off,len,c
                _lz77_off = 0;
                _lz77_len = 0;
                Set_Flag(nMaxByteFlag, iCount++, bLz77HeadFlag);
                byte c = pByte[i + _lz77_len];
                if (!bLz77HeadFlag) { //Ҫ�����ƥ�����3����Ȼ��������ѹ��
//                    pRs[iRs++] = (byte) _lz77_off;
//                    pRs[iRs++] = (byte) _lz77_len;
                    l9Out.writeByte((byte) _lz77_off);
                    l9Out.writeByte((byte) _lz77_len);
                }
//                pRs[iRs++] = c;
                l9Out.writeByte(c);
                //�ı们������
                LZ77_Move_Scroll_Window(c);
                i = i + 1;
            }
        }
        if (!bLz77HeadFlag) {
//            System.arraycopy(pRs, 0, pRs, 0, iRs);
//            return pRs;
        }
        //Ҫ�����ƥ�����3����Ȼ��������ѹ��
        int nByteFlag = (iCount - 1) / 8 + 1;
//        int nRsSize = 1 + wLen + 1 + 4 + nByteFlag + iRs; //�������ݴ�С+��������+ͷ�����+ͷ����Ǵ�С+ͷ���������+ѹ���������
//        byte[] rs_data = new byte[nRsSize];
//
//        int _data_offset = 0;
//
//        rs_data[_data_offset++] = (byte) wLen;
//        System.arraycopy(pWin, wOff, rs_data, _data_offset, wLen);
//        _data_offset += wLen;
//
//        rs_data[_data_offset++] = bLz77HeadFlag ? (byte) 1 : (byte) 0; //ͷ�����
//
//        if (bLz77HeadFlag) { //�������ͷ�����
//            L9OutputStream.writeIntLow(rs_data, _data_offset, nByteFlag);
//            _data_offset += 4;
//            System.arraycopy(nMaxByteFlag, 0, rs_data, _data_offset, nByteFlag);
//            _data_offset += nByteFlag;
//        }
//
//        System.arraycopy(pRs, 0, rs_data, _data_offset, iRs);
//        _data_offset += iRs;

        L9OutputStream out = new L9OutputStream();
        out.writeByte((byte) wLen); //�������ݴ�С
        out.writeBytes(pWin, wOff, wLen); //��������
        out.writeBoolean(bLz77HeadFlag); //ͷ�����
        if (bLz77HeadFlag) { //ͷ���������
            out.writeInt(nByteFlag);
            out.writeBytes(nMaxByteFlag, 0, nByteFlag);
        }
        //ѹ���������
        byte[] bin = l9Out.getBytes();
        out.writeBytes(bin, 0, bin.length);

        return out.getBytes();
    }

    /**
     * LZ77��ѹ�㷨,ʵ��Ҫ��ѹ������λ��offsetƫ�ƿ�ʼ��pByteLen���ֽ�����
     * @param pByte byte[]
     * @param offset int
     * @param pByteLen int
     * @return byte[]
     */
    public static byte[] LZ77_Decode(byte[] pByte, int offset, int pByteLen) {

        L9OutputStream out = new L9OutputStream();

        int _data_offset = offset;

        int wLen = pByte[_data_offset++] & 0xFF;
        LZ77_Init_Win(pByte, _data_offset, wLen); //��ʼ��
        _data_offset += wLen;

        boolean bLz77HeadFlag = (pByte[_data_offset++] == 1); //�ж��Ƿ�ʹ��ͷ�����

        byte[] flag = null;
        if (bLz77HeadFlag) { //Ҫ�����ƥ�����3����Ȼ��������ѹ��
            int nByteFlag = L9InputStream.readIntLow(pByte, _data_offset);
            _data_offset += 4;

            flag = new byte[nByteFlag];
            System.arraycopy(pByte, _data_offset, flag, 0, nByteFlag);
            _data_offset += nByteFlag;
        }
        int iCount = 0;
        int iRs = 0;
        while (_data_offset < offset + pByteLen) {
            try {
                boolean bFind = Get_Flag(flag, iCount++, bLz77HeadFlag);
                if (bFind) {
                    byte c = pByte[_data_offset++];
                    //pRs[iRs++] = c; //���
                    out.writeByte(c);
                    if (_data_offset >= offset + pByteLen) { //�Ѿ���ѹ���
                        break;
                    }
                    //�ı们������
                    _lz77_off = 0;
                    _lz77_len = 0;
                    LZ77_Move_Scroll_Window(c);
                } else {
                    //�Ӵ�����ȡԭ��������
                    _lz77_off = pByte[_data_offset++] & 0xFF;
                    _lz77_len = pByte[_data_offset++] & 0xFF;

                    //System.out.println(""+_lz77_off+","+_lz77_len);

                    for (int i = 0; i < _lz77_len; i++) {
                        byte ch = _pWin[_lz77_off + i];
                        //pRs[iRs++] = ch;
                        out.writeByte(ch);
                    }
                    if (_data_offset >= offset + pByteLen) { //�Ѿ���ѹ���
                        break;
                    }

                    byte c = pByte[_data_offset++];
                    //pRs[iRs++] = c;
                    out.writeByte(c);

                    if (_data_offset >= offset + pByteLen) { //�Ѿ���ѹ���
                        break;
                    }
                    //�ı们������
                    LZ77_Move_Scroll_Window(c);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return out.getBytes();
    }

    /**
     * RLEѹ���㷨,���ǵ��󲿷���ͬ����ɫ�����ᳬ��255,���Բ������� ͷ����ʶ��ȫ������(color,index),color��index��Ϊ�ֽ�
     * @param pByte byte[]
     * @return byte[]
     */
    public static byte[] RLE_Encode(byte[] pByte) {
        byte last_byte = pByte[0]; //��һ��index;
        int iCount = 0;
        int iOff = 0;

        byte[] pRs = new byte[pByte.length * 3 / 4]; //ѹ�����СԤ�ȱ���ԭ���ݴ�С��75%

        for (int i = 0; i < pByte.length; i++) {
            if (pByte[i] == last_byte && iCount < 255) {
                iCount++;
            } else {
                if (iOff > pRs.length - 3) {
                    pRs = addBytes(pRs, 1024);
                }
                pRs[iOff++] = last_byte;
                pRs[iOff++] = (byte) iCount;

                last_byte = pByte[i];
                iCount = 1;
            }
        }
        if (iOff > pRs.length - 3) {
            pRs = addBytes(pRs, iOff - pRs.length + 3);
        }
        //������һ��
        pRs[iOff++] = last_byte;
        pRs[iOff++] = (byte) iCount;

        pRs = trimBytes(pRs, 0, iOff);

        return pRs;
    }

    /**
     * pByteΪ��ѹ�������� iRsSizeΪΪѹ��ǰ���ݵĴ�С
     * @param pByte byte[]
     * @param iRsSize int
     * @return byte[]
     */
    public static byte[] RLE_Decode(byte[] pByte, int off, int pByteLen) {
        int iOff = off;
        L9OutputStream out = new L9OutputStream();
        while (iOff < off + pByteLen) { //pByteLen�ض�Ϊ2��������
            byte c = pByte[iOff++];
            int len = pByte[iOff++] & 0xFF;
            for (int i = 0; i < len; i++) {
                out.writeByte(c);
            }
        }
        return out.getBytes();
    }

////////////////////////////////////////////////////ѹ���㷨 end//////////////////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////�й��ֽ������һЩ����//////////////////////////////////////////////////////////////////////////////////
    /**
     *���ֽ������offƫ�ƴ�����size��С�Ŀռ�,���arrΪ�գ��򷵻�off+size��С�Ŀռ�
     * ���off��������arr�ĳ����򷵻�off+size��С�Ŀռ�,ǰ�������Ϊarr������
     * @param arr byte[]
     * @param size int
     * @param off int
     * @return byte[]
     */
    public static byte[] addBytes(byte[] arr, int off, int size) {
        if (arr == null) {
            return new byte[off + size];
        }
        byte[] tmp_arr = null;
        if (arr.length <= off) {
            tmp_arr = new byte[off + size];
            System.arraycopy(arr, 0, tmp_arr, 0, arr.length);
        } else {
            tmp_arr = new byte[arr.length + size];
            System.arraycopy(arr, 0, tmp_arr, 0, off);
            System.arraycopy(arr, off, tmp_arr, off + size, arr.length - off);
        }
        return tmp_arr;
    }

    /**
     * ���ֽ������ĩβ����size��С�Ŀռ�,���arrΪ���򷵻�size��С�Ŀռ�
     * @param arr byte[]
     * @param step int
     * @return byte[]
     */
    public static byte[] addBytes(byte[] arr, int size) {
        if (arr == null) {
            return new byte[size];
        }
        byte[] tmp_arr = new byte[arr.length + size];
        System.arraycopy(arr, 0, tmp_arr, 0, arr.length);
        return tmp_arr;
    }

    /**
     * ������arr��offƫ�ƴ���ȡ��СΪsize���ֽ����飬���off+size����arr����ĳ��ȣ����ͼ��off��arrĩβ���ֽ�
     * @param arr byte[]
     * @param size int
     * @return byte[]
     */
    public static byte[] trimBytes(byte[] arr, int off, int size) {
        if (arr == null || off >= arr.length) {
            return null;
        }
        byte[] tmp_arr = null;
        if (off + size >= arr.length) {
            tmp_arr = new byte[arr.length - off];
            System.arraycopy(arr, off, tmp_arr, 0, tmp_arr.length);
            return arr;
        } else {
            tmp_arr = new byte[size];
            System.arraycopy(arr, off, tmp_arr, 0, tmp_arr.length);
        }
        return tmp_arr;
    }

    /**
     * �ϲ������ֽ����飬����һ��������
     * @param A byte[]
     * @param B byte[]
     * @return byte[]
     */
    public static byte[] mergeBytes(byte[] A, byte[] B) {
        if (A == null) {
            return B;
        }
        if (B == null) {
            return A;
        }
        byte[] rs = new byte[A.length + B.length];
        System.arraycopy(A, 0, rs, 0, A.length);
        System.arraycopy(B, 0, rs, A.length, B.length);
        return rs;
    }

    /**
     * ���ֽ����鰴���������Ĵ�С���зָ�,���طָ��Ķ�ά�ֽ�����
     * @param rs byte[]
     * @param size int
     * @return byte[][]
     */
    public static byte[][] splitBytes(byte[] rs, int size) {

        if (rs == null || rs.length < 1) {
            return null;
        }

        byte[][] split_rs = null;
        if (rs.length > size) {
            int steps = rs.length / size;
            int lastSize = 0;
            if (rs.length % size != 0) {
                lastSize = rs.length - size * steps;
                steps++;
            }
            split_rs = new byte[steps][];
            for (int i = 0; i < steps - 1; i++) {
                split_rs[i] = new byte[size];
                System.arraycopy(rs, size * i, split_rs[i], 0, size);
            }
            split_rs[steps - 1] = new byte[lastSize];
            System.arraycopy(rs, size * (steps - 1), split_rs[steps - 1], 0,
                             lastSize);

        } else {
            split_rs = new byte[1][];
            split_rs[0] = rs;
        }
        return split_rs;
    }

    /**
     * ����ID�������в�������
     * @param arrID int[]
     * @param ID int
     * @return int
     */
    public static int getIndexByID(int[] arrID, int ID) {
        for (int i = 0; arrID != null && i < arrID.length; i++) {
            if (arrID[i] == ID) {
                return i;
            }
        }
        return -1;
    }

    /**
     * �׳��������쳣����ʾ��ջ��Ϣ�����ڵ��Գ���
     * @param msg String
     */
    public static void throwException(String msg) {
        try {
            System.out.println("error:" + msg);
            throw new Exception(msg);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * ��ʾ�Ѻõ��쳣��Ϣ����ʾ��ջ��Ϣ
     * @param ex Exception
     * @param msg String
     */
    public static void throwException(Exception ex, String msg) {
        System.out.println("error:" + msg);
        ex.printStackTrace();
    }

    /**
     * ��ĳ��ֵ�������
     * @param intArray int[]
     * @param val int
     * @return int[]
     */
    public static int[] fillArray(int[] intArray, int val) {
        for (int i = 0; i < intArray.length; i++) {
            intArray[i] = val;
        }
        return intArray;
    }

    /**
     * �������һ������
     * @param intArray int[]
     * @return int[]
     */
    public static int[] rndArray(int[] intArray) {
        for (int i = 0; intArray != null && i < intArray.length; i++) {
            int index1 = getRandValue(0, intArray.length);
            int index2 = getDiffRandValue(index1, 0, intArray.length);

            int tmp = intArray[index1];
            intArray[index1] = intArray[index2];
            intArray[index2] = tmp;
        }
        return intArray;
    }

    /**
     * ����������������Сֵ������,���arrΪnull�����߳���Ϊ0�򷵻�-1
     * @param arr int[]
     * @return int
     */
    public static int getMinIndex(int[] arr) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        if (arr.length == 1) {
            return 0;
        }

        int min = arr[0];
        int min_Index = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] < min) {
                min = arr[i];
                min_Index = i;
            }
        }
        return min_Index;
    }

    /**
     * ����������������С��ֵ
     * @param arr int[]
     * @return int
     */
    public static int getMinValue(int[] arr) {
        return arr[getMinIndex(arr)];
    }
}
