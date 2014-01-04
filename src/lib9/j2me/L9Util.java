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
 * Lib9引擎提供的一些有用功能的集合，比如：获取某个返回内的随机数
 * @author not attributable
 * @version 1.0
 */
public class L9Util {
    public L9Util() {
    }

    /////////////////////////////////////////////////////有关随机数的操作/////////////////////////////////////////////////////////////////////////
    private static Random rd = new Random();
    /**
     * 在[lower,upper)之间随机取一个值
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
     * 在[lower,upper)之间随机取一个值,要求不能是val
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
     * 在[lower,upper)之间随机取一个值,要求不能是arr中的任何一个
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
     * 返回size大小且不重复的整数数组
     * @param size int
     * @param lower int
     * @param upper int
     * @return int[]
     */
    public static int[] getDiffArray(int size, int lower, int upper) {
        if (size > upper - lower) {
            try {
                throw new IllegalArgumentException(
                        "参数不合法! size必须要小于upper - lower");
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

    /////////////////c++或c#中流 先读或写低字节 java却正好相反，先读或写高字节/////////////////

///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    /**
     * 返回Short数字的字节数组，顺序由低到高，占2位
     * @param i int
     * @return byte[]
     */
    public static byte[] getShortBytes(short i) {
        byte[] head = new byte[4];
        L9OutputStream.writeShortLow(head, 0, i);
        return head;
    }

    /**
     * 返回整形数字的字节数组，顺序由低到高
     * @param i int
     * @return byte[]
     */
    public static byte[] getIntBytes(int i) {
        byte[] head = new byte[4];
        L9OutputStream.writeIntLow(head, 0, i);
        return head;
    }

    /**
     * 返回整形数字的字节数组，顺序由低到高
     * @param i int
     * @return byte[]
     */
    public static byte[] getLongBytes(long i) {
        byte[] head = new byte[8];
        L9OutputStream.writeLongLow(head, 0, i);
        return head;
    }

    /**
     * 返回字符串的由Encode指定编码的字节数组
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
     * 返回字符串的由Encode指定编码的字节数组,前面带有长度,占4个字节
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

    /////////////////////////////////压缩算法//////////////////////////////////////
    /**
     * 设置压缩算法的头标记,bUse_Flag_Head为true这个函数才有意义
     * @param flag byte[]
     * @param Num int
     */
    private static void Set_Flag(byte[] flag, int Num, boolean bLz77HeadFlag) {
        if (bLz77HeadFlag) { //为真标记才有意义
            int index = Num / 8;
            int pos = Num - index * 8;
            flag[index] |= 1 << pos;
        }
    }

    /**
     * 取得压缩算法的头标记,bUse_Flag_Head为true这个函数才有意义
     * @param flag byte[]
     * @param Num int
     * @return boolean
     */
    private static boolean Get_Flag(byte[] flag, int Num, boolean bLz77HeadFlag) {
        if (bLz77HeadFlag) { //为真标记才有意义
            int index = Num / 8;
            int pos = Num - index * 8;
            return (flag[index] & (1 << pos)) != 0;
        }
        return false;
    }

//压缩数据存储结构 Flag+Data
//Flag标记数据是原数据还是三元组数据,Data为原数据或者三元组数据
    /**
     * LZ77压缩算法的滑动窗口字节变量
     */
    private static byte[] _pWin = null;
    /**
     * 滑动窗口的大小，由于程序中用一个字节来表示的偏移量和长度 ,所以窗口长度不能大于255
     */
    private static int _pWinLen = 255; //注意由于程序中用一个字节来表示的偏移量和长度 ,所以窗口长度不能大于255

    private static byte[] _pTmpBuf = null;

    private static int _lz77_off = 0;
    private static int _lz77_len = 0;
    /**
     * 使用LZ77压缩前应该先初始化滑动窗口,由于程序中用一个字节来表示的偏移量和长度 ,所以窗口长度不能大于255
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
     * 清除LZ77滑动窗口所占的内存数据,java中不用，因为有垃圾收集机制
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
     * 在滑动窗口中寻找sLen长度s的位置
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
     * 在滑动窗口中寻求最长匹配，返回匹配字符串在窗口中的偏移量和匹配字符串长度(_lz77_off和_lz77_len)
     * pByte为需压缩数据,pByteLen为压缩数据的长度,pByteOff为当前偏移量 也就是说只在pByteOff至pByteLen中查找匹配
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
     * 移动滑动窗口 c为压缩数据的下一个字节
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
     * Lz77压缩算法，pByte为要压缩的数据，从要压缩的数据pByte中从偏移量wOff开始取wLen个字符作为滑动窗口数据，头部标记为true 进行压缩,wLen长度不能大于255
     * @param pByte byte[]
     * @param wOff int
     * @param wLen int
     * @return byte[]
     */
    public static byte[] LZ77_Encode(byte[] pByte, int wOff, int wLen) {
        return LZ77_Encode(pByte, pByte, wOff, wLen, true);
    }

    /**
     * Lz77压缩算法，pByte为要压缩的数据，从要压缩的数据pByte中取前255个字符作为滑动窗口数据，头部标记为true 进行压缩
     * 如果数据长度小于255则不压缩直接返回原数据
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
     * Lz77压缩算法，pByte为要压缩的数据，pWin，wOff，wLen表示在pWin数据中从wOff偏移量中取出wLen长度的数据作为滑动窗口数据，bLz77HeadFlag表示是否使用头部标记的压缩方法
     * @param pByte byte[]
     * @param pWin byte[]
     * @param wOff int
     * @param wLen int
     * @param bLz77HeadFlag boolean
     * @return byte[]
     */
    public static byte[] LZ77_Encode(byte[] pByte, byte[] pWin, int wOff,
                                     int wLen, boolean bLz77HeadFlag) {
        LZ77_Init_Win(pWin, wOff, wLen); //初始化

        int pByteLen = pByte.length;
//        byte[] pRs = new byte[pByteLen * 3 / 4]; //初始话预计压缩后大小为原大小的75%
        L9OutputStream l9Out = new L9OutputStream();

        byte[] nMaxByteFlag = null;
//////////注意 大部分的JVM可能会默认初始化,但可能有少数不默认初始化的JVM就会出问题，所以在此初始化////////
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
//            //pRs[iRs++] = 0; //头部标记
//            l9Out.writeBoolean(false);
//        }
        for (int i = 0; i < pByteLen; ) {
            //防止pRs数据越界,一般情况下是压缩会减少大小，所以在调用LZ77_Encode方法时就不需要传递最大保险值，这样就可以减少内存的使用
            //int max_rs_len = 3 * w * h + 4 + 1; //压缩的最坏结果
//            if (iRs + 4 > pRs.length) { //三元组只需加3
//                byte[] tmp_byte = pRs;
//                pRs = new byte[tmp_byte.length + 1024];
//                System.arraycopy(tmp_byte, 0, pRs, 0, tmp_byte.length);
//            }
            //////////////////////////////////////////////////////////////////////////

            boolean bFind = LZ77_Find_Max_Str(pByte, pByteLen, i);
            if (bLz77HeadFlag) { //要求最低匹配大于3，不然将不会有压缩
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
                if (_lz77_len == pByteLen - i) { //结束
                    //pRs[iRs++] = (byte) _lz77_off;
                    //pRs[iRs++] = (byte) _lz77_len;
                    l9Out.writeByte((byte) _lz77_off);
                    l9Out.writeByte((byte) _lz77_len);

                    //pRs[iRs++] = '\0';
                    break;
                } else {
                    //输出off,len,c
                    byte c = pByte[i + _lz77_len];
//                    pRs[iRs++] = (byte) _lz77_off;
//                    pRs[iRs++] = (byte) _lz77_len;
//                    pRs[iRs++] = c;
                    l9Out.writeByte((byte) _lz77_off);
                    l9Out.writeByte((byte) _lz77_len);
                    l9Out.writeByte(c);
                    //改变滑动窗口
                    LZ77_Move_Scroll_Window(c);
                }
                i = i + _lz77_len + 1;
                //System.out.println("_lz77_len=" + _lz77_len);
            } else {
                //输出off,len,c
                _lz77_off = 0;
                _lz77_len = 0;
                Set_Flag(nMaxByteFlag, iCount++, bLz77HeadFlag);
                byte c = pByte[i + _lz77_len];
                if (!bLz77HeadFlag) { //要求最低匹配大于3，不然将不会有压缩
//                    pRs[iRs++] = (byte) _lz77_off;
//                    pRs[iRs++] = (byte) _lz77_len;
                    l9Out.writeByte((byte) _lz77_off);
                    l9Out.writeByte((byte) _lz77_len);
                }
//                pRs[iRs++] = c;
                l9Out.writeByte(c);
                //改变滑动窗口
                LZ77_Move_Scroll_Window(c);
                i = i + 1;
            }
        }
        if (!bLz77HeadFlag) {
//            System.arraycopy(pRs, 0, pRs, 0, iRs);
//            return pRs;
        }
        //要求最低匹配大于3，不然将不会有压缩
        int nByteFlag = (iCount - 1) / 8 + 1;
//        int nRsSize = 1 + wLen + 1 + 4 + nByteFlag + iRs; //窗口数据大小+窗口数据+头部标记+头部标记大小+头部标记数据+压缩后的数据
//        byte[] rs_data = new byte[nRsSize];
//
//        int _data_offset = 0;
//
//        rs_data[_data_offset++] = (byte) wLen;
//        System.arraycopy(pWin, wOff, rs_data, _data_offset, wLen);
//        _data_offset += wLen;
//
//        rs_data[_data_offset++] = bLz77HeadFlag ? (byte) 1 : (byte) 0; //头部标记
//
//        if (bLz77HeadFlag) { //如果存在头部标记
//            L9OutputStream.writeIntLow(rs_data, _data_offset, nByteFlag);
//            _data_offset += 4;
//            System.arraycopy(nMaxByteFlag, 0, rs_data, _data_offset, nByteFlag);
//            _data_offset += nByteFlag;
//        }
//
//        System.arraycopy(pRs, 0, rs_data, _data_offset, iRs);
//        _data_offset += iRs;

        L9OutputStream out = new L9OutputStream();
        out.writeByte((byte) wLen); //窗口数据大小
        out.writeBytes(pWin, wOff, wLen); //窗口数据
        out.writeBoolean(bLz77HeadFlag); //头部标记
        if (bLz77HeadFlag) { //头部标记数据
            out.writeInt(nByteFlag);
            out.writeBytes(nMaxByteFlag, 0, nByteFlag);
        }
        //压缩后的数据
        byte[] bin = l9Out.getBytes();
        out.writeBytes(bin, 0, bin.length);

        return out.getBytes();
    }

    /**
     * LZ77解压算法,实际要解压的数据位从offset偏移开始的pByteLen个字节数据
     * @param pByte byte[]
     * @param offset int
     * @param pByteLen int
     * @return byte[]
     */
    public static byte[] LZ77_Decode(byte[] pByte, int offset, int pByteLen) {

        L9OutputStream out = new L9OutputStream();

        int _data_offset = offset;

        int wLen = pByte[_data_offset++] & 0xFF;
        LZ77_Init_Win(pByte, _data_offset, wLen); //初始化
        _data_offset += wLen;

        boolean bLz77HeadFlag = (pByte[_data_offset++] == 1); //判断是否使用头部标记

        byte[] flag = null;
        if (bLz77HeadFlag) { //要求最低匹配大于3，不然将不会有压缩
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
                    //pRs[iRs++] = c; //输出
                    out.writeByte(c);
                    if (_data_offset >= offset + pByteLen) { //已经解压完毕
                        break;
                    }
                    //改变滑动窗口
                    _lz77_off = 0;
                    _lz77_len = 0;
                    LZ77_Move_Scroll_Window(c);
                } else {
                    //从窗口中取原来的数据
                    _lz77_off = pByte[_data_offset++] & 0xFF;
                    _lz77_len = pByte[_data_offset++] & 0xFF;

                    //System.out.println(""+_lz77_off+","+_lz77_len);

                    for (int i = 0; i < _lz77_len; i++) {
                        byte ch = _pWin[_lz77_off + i];
                        //pRs[iRs++] = ch;
                        out.writeByte(ch);
                    }
                    if (_data_offset >= offset + pByteLen) { //已经解压完毕
                        break;
                    }

                    byte c = pByte[_data_offset++];
                    //pRs[iRs++] = c;
                    out.writeByte(c);

                    if (_data_offset >= offset + pByteLen) { //已经解压完毕
                        break;
                    }
                    //改变滑动窗口
                    LZ77_Move_Scroll_Window(c);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return out.getBytes();
    }

    /**
     * RLE压缩算法,考虑到大部分相同的颜色都不会超过255,所以不再设立 头部标识，全部采用(color,index),color和index均为字节
     * @param pByte byte[]
     * @return byte[]
     */
    public static byte[] RLE_Encode(byte[] pByte) {
        byte last_byte = pByte[0]; //第一个index;
        int iCount = 0;
        int iOff = 0;

        byte[] pRs = new byte[pByte.length * 3 / 4]; //压缩后大小预先保留原数据大小的75%

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
        //输出最后一次
        pRs[iOff++] = last_byte;
        pRs[iOff++] = (byte) iCount;

        pRs = trimBytes(pRs, 0, iOff);

        return pRs;
    }

    /**
     * pByte为解压缩的数据 iRsSize为为压缩前数据的大小
     * @param pByte byte[]
     * @param iRsSize int
     * @return byte[]
     */
    public static byte[] RLE_Decode(byte[] pByte, int off, int pByteLen) {
        int iOff = off;
        L9OutputStream out = new L9OutputStream();
        while (iOff < off + pByteLen) { //pByteLen必定为2的整数倍
            byte c = pByte[iOff++];
            int len = pByte[iOff++] & 0xFF;
            for (int i = 0; i < len; i++) {
                out.writeByte(c);
            }
        }
        return out.getBytes();
    }

////////////////////////////////////////////////////压缩算法 end//////////////////////////////////////////////////////////////////////////////////////

//////////////////////////////////////////有关字节数组的一些操作//////////////////////////////////////////////////////////////////////////////////
    /**
     *在字节数组的off偏移处增加size大小的空间,如果arr为空，则返回off+size大小的空间
     * 如果off大于数组arr的长度则返回off+size大小的空间,前面的数据为arr的数据
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
     * 在字节数组的末尾新增size大小的空间,如果arr为空则返回size大小的空间
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
     * 在数组arr的off偏移处截取大小为size的字节数组，如果off+size大于arr数组的长度，则截图从off到arr末尾的字节
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
     * 合并两个字节数组，返回一个新数组
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
     * 将字节数组按照所给出的大小进行分割,返回分割后的二维字节数组
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
     * 根据ID在数组中查找索引
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
     * 抛出并捕获异常，显示堆栈信息，便于调试程序
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
     * 显示友好的异常信息并显示堆栈信息
     * @param ex Exception
     * @param msg String
     */
    public static void throwException(Exception ex, String msg) {
        System.out.println("error:" + msg);
        ex.printStackTrace();
    }

    /**
     * 用某个值填充数组
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
     * 随机打乱一个数组
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
     * 返回整型数组中最小值的索引,如果arr为null，或者长度为0则返回-1
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
     * 返回整型数组中最小的值
     * @param arr int[]
     * @return int
     */
    public static int getMinValue(int[] arr) {
        return arr[getMinIndex(arr)];
    }
}
