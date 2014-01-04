package lib9.j2me;

import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * ���ַ���String���ܵ����ӣ�֧�ִ�����ɫ�ķ�ʽ�����ַ���������ͼƬ�ַ�������ҳ���Ƶ�
 * @author not attributable
 * @version 1.0
 */
public class L9Str {

    public L9Str() {
    }

    /**
     *�����ַ������뽫DEBUG_STRING_POS����Ϊtrue
     * @param str String
     */
    private void Debug_Str(String str) {
        if (L9Config.bDebugString) {
            try {
                System.out.println("str=" + str);
                throw new Exception();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ��������ʱ���������
     */
    private Font _font = Font.getFont(Font.FACE_SYSTEM, 0,
                                      Font.SIZE_SMALL /*SIZE_MEDIUM*/);
    private String _mapChar;
    private L9Sprite _spriteMapChar;
    private int _pal = 0;
    /**
     * ����ͼƬ���������spriteMapCharΪͼƬ��Ӧ��sprite����mapCharΪͼƬӳ���ַ����ַ���˳���ӦspriteMapChar�е�module
     * @param spriteMapChar L9Sprite
     * @param mapChar String
     */
    public void setImageFont(L9Sprite spriteMapChar, String mapChar) {
        _font = null;
        _spriteMapChar = spriteMapChar;
        _mapChar = mapChar;
    }

    /**
     * ʹ��ϵͳ�����������ַ���
     * @param font Font
     */
    public void setSystemFont(Font font) {
        _spriteMapChar = null;
        _mapChar = null;
        _font = font;
    }

    /**
     * ��ȡ�ַ��Ŀ��
     * @param c char
     * @return int
     */
    public int getCharWidth(char c) {
        if (_font != null) {
            return _font.charWidth(c);
        }
        int index = _mapChar.indexOf(c);
        return _spriteMapChar.getModuleWidth(index);
    }

    /**
     * ��ȡ�ַ��ĸ߶�
     * @param c char
     * @return int
     */
    public int getCharHeight(char c) {
        if (_font != null) {
            return _font.getHeight();
        }
        int index = _mapChar.indexOf(c);
        return _spriteMapChar.getModuleHeight(index);
    }

    /**
     * �������ַ����ĸ߶�,Ҳ�����ַ������и߶���ߵ��ַ��ĸ߶���Ϊ�еĸ߶�
     * @param Line String
     * @return int
     */
    public int getLineHeight(String Line) {
        int maxH = 0;
        for (int i = 0; i < Line.length(); i++) {
            char c = Line.charAt(i);
            //"0x"��Ϊ��ɫ�ı��,������λ��ʾ��ɫֵ
            if (c == '0') {
                try {
                    if (i + 1 < Line.length()) {
                        char c2 = Line.charAt(i + 1);
                        if ((c2 == 'x' || c2 == 'X')) {
                            String color = Line.substring(i + 2, i + 8);
                            int iColor = Integer.parseInt(color, 16);
                            i += 7; //��Ϊi����ִ��һ��
                            continue;
                        } else if ((c2 == 'p' || c2 == 'P')) {
                            String color = Line.substring(i + 2, i + 3);
                            int iColor = Integer.parseInt(color);
                            i += 2; //��Ϊi����ִ��һ��
                            continue;
                        }
                    }
                } catch (Exception ex) {
                    //�����쳣˵��������Ч�ı��
                    //ex.printStackTrace();
                }
            }
            if (maxH < getCharWidth(c)) {
                maxH = getCharWidth(c);
            }
        }
        return maxH;
    }

    /**
     * ����ͼƬ����ĵ�ɫ�壬Ĭ��ʹ��ͼƬ����
     * @param pal int
     */
    public void setImagePal(int pal) {
        _pal = pal;
    }

    /**
     * ��ָ����λ�û����ַ�
     * @param g Graphics
     * @param c char
     * @param x int
     * @param y int
     */
    public void drawChar(Graphics g, char c, int x, int y) {
        if (_font != null) {
            g.drawChar(c, x, y, 0);
            return;
        }
        int index = _mapChar.indexOf(c);
        _spriteMapChar.paintModule(g, index, x, y, 0, _pal);
    }

    ////////////////////////////////////////////////////////�й��ַ����Ĳ���/////////////////////////////////////////////////////////////////////////////
    /**
     * ָ���ָ������ָ��ַ���,���ط���ַ�������,֧�ֶ���ָ������������,���磺aaxxxaa ���ָ���"x"��ô���ص���[aa,aa]�����ַ����������Ƿ��ص�["aa","","","","aa"]5���ַ���]
     * @param str String
     * @param sep String
     * @return String[]
     */
    public static String[] splitStr(String str, String sep) {
        Vector v = new Vector();
        while (str.length() > sep.length()) {
            str = trimStr(str, sep);
            int index = str.indexOf(sep);
            if (index == -1) {
                if (str.length() > 0) {
                    v.addElement(str);
                }
                break;
            } else {
                v.addElement(str.substring(0, index));
                str = str.substring(index + sep.length());
            }
        }
        String[] rs = new String[v.size()];
        v.copyInto(rs);
        return rs;
    }

    /**
     * ���ַ������鰴��sep�����ϳ�һ���ַ���
     * @param strArr String[]
     * @param sep String
     * @return String
     */
    public static String joinStr(String[] strArr, String sep) {
        String Rs = "";
        for (int i = 0; i < strArr.length; i++) {
            Rs += strArr[i];
            if (i != strArr.length - 1) {
                Rs += sep;
            }
        }
        return Rs;
    }

    /**
     * ȥ���ַ���ǰ�ߵ�����sep���
     * @param str String
     * @param sep String
     * @return String
     */
    public static String leftStr(String str, String sep) {
        while (str.startsWith(sep)) {
            str = str.substring(sep.length());
        }
        return str;
    }

    /**
     * ȥ���ַ�����ߵ�����sep���
     * @param str String
     * @param sep String
     * @return String
     */
    public static String rightStr(String str, String sep) {
        while (str.endsWith(sep)) {
            str = str.substring(0, str.length() - sep.length());
        }
        return str;
    }

    /**
     * ����ַ���ǰ���sep���
     * @param str String
     * @param sep String
     * @return String
     */
    public static String trimStr(String str, String sep) {
        str = leftStr(str, sep);
        str = rightStr(str, sep);
        return str;
    }

    /**
     * ���ַ���sReplace�滻�ַ���str�е�sFind�ַ�����ֻ�滻��һ�������Ҫȫ���滻�����replaceStrAll����
     * @param str String
     * @param sFind String
     * @param sReplace String
     * @return String
     */
    public static String replaceStr(String str, String sFind, String sReplace) {
        int index = str.indexOf(sFind);
        if (index != -1) {
            str = str.substring(0, index) + sReplace +
                  str.substring(index + sFind.length());
        }
        return str;
    }

    /**
     * ���ַ����в���sToken�ַ�����Ȼ����sArr������ַ������滻����󷵻ر��滻����ַ�����ʹ�����£�
     * String str=����ӵĳɼ�������XXXX,����ʱ��XXXX����!��;
     * str=getResStr(17,"XXXX",new String[]{"86","2"});
     * ����getResStr��������ַ�����Ϊ����ӵĳɼ�������86,����ʱ��2����!��
     * @param str String
     * @param sFind String
     * @param sReplace String
     * @param sep String
     * @return String
     */
    public static String replaceStr(String str, String sFind, String sReplace,
                                    String sep) {
        String[] sArr = splitStr(sReplace, sep);
        for (int i = 0; sArr != null && i < sArr.length; i++) {
            str = replaceStr(str, sFind, sArr[i]);
        }
        return str;
    }

    /**
     * ���ַ���sReplace�滻�ַ���str�е�sFind�ַ�����ȫ���滻�������ֻ���滻��һ�������replaceStr����
     * @param str String
     * @param sFind String
     * @param sReplace String
     * @return String
     */
    public static String replaceStrAll(String str, String sFind,
                                       String sReplace) {
        int index = str.indexOf(sFind);
        while (index != -1) {
            str = str.substring(0, index) + sReplace +
                  str.substring(index + sFind.length());
            index = str.indexOf(sReplace);
        }
        return str;
    }

    /**
     * ���ַ����ָ�Ϊ�����ΪlineW���ַ�������,�ҽ�\n��Ϊ���б�ʶ������֧�ֶ�����������ɫ
     * ���磺���ǵ�0xFF0000�ֻ�����0x000000������ģ���ô���ֻ����桱�����ɫ��ʾ
     * ϵͳ����ʹ��0xFF0000���ָ�ʽ������ɫ,ͼƬ������ʹ��0p2���ַ�ʽ�����õ�ɫ��
     * @param pStr String
     * @param lineW int
     * @return String[]
     */
    public String[] updateString(String pStr, int lineW) {
        if (pStr == null || pStr.length() < 1) {
            return null;
        }

        Vector vLines = new Vector();
        StringBuffer tmpLine = new StringBuffer();
        int width = 0;
        int max_w = lineW;
        for (int i = 0; i < pStr.length(); ) {
            char c = pStr.charAt(i);
            //"0x"��Ϊ��ɫ�ı��,������λ��ʾ��ɫֵ
            if (c == '0') {
                try {
                    if (i + 1 < pStr.length()) {
                        char c2 = pStr.charAt(i + 1);
                        if (c2 == 'x' || c2 == 'X') { //0x��ʾϵͳ�������ɫ
                            String color = pStr.substring(i + 2, i + 8);
                            int tmp = Integer.parseInt(color, 16);
                            tmpLine.append(c);
                            tmpLine.append(c2);
                            tmpLine.append(color);
                            i += 7 + 1; //��Ϊcontinue��û��i++��
                            continue;
                        } else if (c2 == 'p' || c2 == 'P') { //0p��ʾͼƬ�������ɫ,��ɫ����ɫֻ��Ϊ[0-9]
                            String color = pStr.substring(i + 2, i + 3);
                            int tmp = Integer.parseInt(color);
                            tmpLine.append(c);
                            tmpLine.append(c2);
                            tmpLine.append(color);
                            i += 2 + 1; //��Ϊcontinue��û��i++��
                            continue;
                        }
                    }
                } catch (Exception ex) {
                    //�����쳣˵��������Ч�ı��
                    //ex.printStackTrace();
                }
            }

//            width += getCharWidth(c);

            if (c == '\n' || width + getCharWidth(c) > max_w) {
                if (c == '\n') {
                    width = 0;
                } else {
                    width = getCharWidth(c);
                }
                vLines.addElement(tmpLine.toString());
                tmpLine = new StringBuffer();
            } else {
                width += getCharWidth(c);
            }
            i++;
            if (c != '\n') {
                tmpLine.append(c);
            }
        }
        if (tmpLine.length() > 0) {
            vLines.addElement(tmpLine.toString()); //���һ��
        }

        String[] sLines = new String[vLines.size()];
        vLines.copyInto(sLines);

        return sLines;
    }

    /**
     * �����ַ����Ŀ�
     * @param Line String
     * @return int
     */
    public int getLineW(String Line) {
        int w = 0;
        for (int i = 0; i < Line.length(); i++) {
            char c = Line.charAt(i);
            //"0x"��Ϊ��ɫ�ı��,������λ��ʾ��ɫֵ
            if (c == '0') {
                try {
                    if (i + 1 < Line.length()) {
                        char c2 = Line.charAt(i + 1);
                        if ((c2 == 'x' || c2 == 'X')) {
                            String color = Line.substring(i + 2, i + 8);
                            int iColor = Integer.parseInt(color, 16);
                            i += 7; //��Ϊi����ִ��һ��
                            continue;
                        } else if ((c2 == 'p' || c2 == 'P')) {
                            String color = Line.substring(i + 2, i + 3);
                            int iColor = Integer.parseInt(color);
                            i += 2; //��Ϊi����ִ��һ��
                            continue;
                        }
                    }
                } catch (Exception ex) {
                    //�����쳣˵��������Ч�ı��
                    //ex.printStackTrace();
                }
            }
            w += getCharWidth(c);
        }
        return w;
    }

    /**
     * �����ַ��������г��������ַ���������,���LinesΪ�ջ��߳���Ϊ0�򷵻�-1
     * @param Lines String[]
     * @return int
     */
    public int getLineMaxWIndex(String[] Lines) {
        int maxW = 0;
        int index = -1;
        for (int i = 0; Lines != null && i < Lines.length; i++) {
            if (maxW < getLineW(Lines[i])) {
                maxW = getLineW(Lines[i]);
                index = i;
            }
        }
        return index;
    }

    /**
     * �����ַ�������������п����LinesΪ�ջ��߳���Ϊ0�򷵻�0
     * @param Lines String[]
     * @return int
     */
    public int getMaxLineW(String[] Lines) {
        int index = getLineMaxWIndex(Lines);
        return index != -1 ? getLineW(Lines[index]) : 0;
    }

    /**
     * ʹ��ѭ���ķ�ʽһ���ַ�һ���ַ������ַ���
     * @param g Graphics
     * @param Line String
     * @param X int
     * @param Y int
     */
    public void drawLine(Graphics g, String Line, int X, int Y) {
        int x = X;
        for (int i = 0; i < Line.length(); i++) {
            char c = Line.charAt(i);
            //"0x"��Ϊ��ɫ�ı��,������λ��ʾ��ɫֵ
            if (c == '0') {
                try {
                    if (i + 1 < Line.length()) {
                        char c2 = Line.charAt(i + 1);
                        if ((c2 == 'x' || c2 == 'X')) {
                            String color = Line.substring(i + 2, i + 8);
                            int iColor = Integer.parseInt(color, 16);
                            g.setColor(iColor);
                            i += 7; //��Ϊi����ִ��һ��
                            continue;
                        } else if (c2 == 'p' || c2 == 'P') { //0p��ʾͼƬ�������ɫ,��ɫ����ɫֻ��Ϊ[0-9]
                            String color = Line.substring(i + 2, i + 3);
                            int iColor = Integer.parseInt(color);
                            setImagePal(iColor);
                            i += 2; //��Ϊi����ִ��һ��
                            continue;
                        }
                    }
                } catch (Exception ex) {
                    //�����쳣˵��������Ч�ı��
                    //ex.printStackTrace();
                }
            }

            drawChar(g, c, x, Y);
            x += getCharWidth(c);
        }
        Debug_Str(Line);
    }

    private String[] _pageLines;
    private int _pageLineSpace;
    private int _pageLineW;
    private int _nPageSize;
    /**
     * ���÷�ҳ�����ַ�����Ϣ,pageLineW��ʾҳ��Ŀ�ȣ�pageH��ʾҳ��ĸ߶�,lineSpace��ʾ�м��
     * @param sText String
     * @param pageLineW int
     * @param pageH int
     * @param lineSpace int
     */
    public void setPageSize(String sText, int pageLineW, int pageH,
                            int lineSpace) {
        _pageLines = updateString(sText, pageLineW);
        _pageLineW = pageLineW;
        _pageLineSpace = lineSpace;
        _nPageSize = pageH / (getLineHeight(_pageLines[0]) + lineSpace);
    }

    /**
     * ����ÿҳ������
     * @return int
     */
    public int getPageSize() {
        return _nPageSize;
    }

    /**
     * �ַ�����ҳ�󣬷����ж���ҳ
     * @return int
     */
    public int getPageCount() {
        int nPages = _pageLines.length / _nPageSize;
        if (nPages * _nPageSize != _pageLines.length) {
            nPages++;
        }
        return nPages;
    }

    /**
     * ����ָ��ҳ���ַ���,����drawPage֮ǰ���ȵ���setPageSize�����÷�ҳ��Ϣ
     * @param g Graphics
     * @param iPage int
     * @param x int
     * @param y int
     */
    public void drawPage(Graphics g, int iPage, int x, int y) {
        int off = iPage * _nPageSize;
        int YY = y;
        for (int i = off;
                     _pageLines != null && i < off + _nPageSize &&
                     i < _pageLines.length; i++) {
            drawLine(g, _pageLines[i], x, YY);
            YY += getLineHeight(_pageLines[i]) + _pageLineSpace;
        }
    }

    /**
     * �����ַ����Ķ��뷽ʽ�������
     */
    public final static int K_Line_Align_Left = 0;
    /**
     * �����ַ����Ķ��뷽ʽ�����ж���
     */
    public final static int K_Line_Align_Center = 1;
    /**
     * �����ַ����Ķ��뷽ʽ���Ҷ���
     */
    public final static int K_Line_Align_Right = 2;
    /**
     * �ڸ�����λ�ü���ȵ������»������ַ���,alignΪ�����еĶ��뷽ʽ��0��ʾ�����,1��ʾ���ж���,2��ʾ�Ҷ���
     * @param g Graphics
     * @param line String
     * @param y int
     */
    public void drawLine(Graphics g, String line, int x, int y, int lineW,
                         int align) {
        switch (align) {
        case K_Line_Align_Left:
            drawLine(g, line, x, y);
            break;
        case K_Line_Align_Center: {
            int sW = getLineW(line);
            int XX = x + ((lineW - sW) >> 1);
            drawLine(g, line, XX, y);
        }
        break;
        case K_Line_Align_Right: {
            int sW = getLineW(line);
            int XX = x + lineW - sW;
            drawLine(g, line, XX, y);
        }
        break;
        }
    }

    /**
     * �ڸ�����λ�ü���ȵ������»���ҳ,����drawPage֮ǰ���ȵ���setPageSize�����÷�ҳ��Ϣ,alignΪ�����еĶ��뷽ʽ��0��ʾ�����,1��ʾ���ж���,2��ʾ�Ҷ���
     * @param g Graphics
     * @param iPage int
     * @param x int
     * @param y int
     * @param align int
     */
    public void drawPage(Graphics g, int iPage, int x, int y, int align) {
        int off = iPage * _nPageSize;
        int YY = y;
        for (int i = off;
                     _pageLines != null && i < off + _nPageSize &&
                     i < _pageLines.length; i++) {
            drawLine(g, _pageLines[i], x, YY, _pageLineW, align);
            YY += getLineHeight(_pageLines[i]) + _pageLineSpace;
        }
    }
}
