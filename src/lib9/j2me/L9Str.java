package lib9.j2me;

import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * 对字符串String功能的增加，支持带有颜色的方式绘制字符串、绘制图片字符串、分页绘制等
 * @author not attributable
 * @version 1.0
 */
public class L9Str {

    public L9Str() {
    }

    /**
     *调试字符串，请将DEBUG_STRING_POS设置为true
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
     * 绘制文字时的字体变量
     */
    private Font _font = Font.getFont(Font.FACE_SYSTEM, 0,
                                      Font.SIZE_SMALL /*SIZE_MEDIUM*/);
    private String _mapChar;
    private L9Sprite _spriteMapChar;
    private int _pal = 0;
    /**
     * 设置图片字体参数，spriteMapChar为图片对应的sprite对象，mapChar为图片映射字符，字符的顺序对应spriteMapChar中的module
     * @param spriteMapChar L9Sprite
     * @param mapChar String
     */
    public void setImageFont(L9Sprite spriteMapChar, String mapChar) {
        _font = null;
        _spriteMapChar = spriteMapChar;
        _mapChar = mapChar;
    }

    /**
     * 使用系统字体来绘制字符串
     * @param font Font
     */
    public void setSystemFont(Font font) {
        _spriteMapChar = null;
        _mapChar = null;
        _font = font;
    }

    /**
     * 获取字符的宽度
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
     * 获取字符的高度
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
     * 返回行字符串的高度,也就是字符串行中高度最高的字符的高度作为行的高度
     * @param Line String
     * @return int
     */
    public int getLineHeight(String Line) {
        int maxH = 0;
        for (int i = 0; i < Line.length(); i++) {
            char c = Line.charAt(i);
            //"0x"作为颜色的标记,后面六位表示颜色值
            if (c == '0') {
                try {
                    if (i + 1 < Line.length()) {
                        char c2 = Line.charAt(i + 1);
                        if ((c2 == 'x' || c2 == 'X')) {
                            String color = Line.substring(i + 2, i + 8);
                            int iColor = Integer.parseInt(color, 16);
                            i += 7; //因为i还会执行一次
                            continue;
                        } else if ((c2 == 'p' || c2 == 'P')) {
                            String color = Line.substring(i + 2, i + 3);
                            int iColor = Integer.parseInt(color);
                            i += 2; //因为i还会执行一次
                            continue;
                        }
                    }
                } catch (Exception ex) {
                    //发生异常说明不是有效的标记
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
     * 设置图片字体的调色板，默认使用图片本身
     * @param pal int
     */
    public void setImagePal(int pal) {
        _pal = pal;
    }

    /**
     * 在指定的位置绘制字符
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

    ////////////////////////////////////////////////////////有关字符串的操作/////////////////////////////////////////////////////////////////////////////
    /**
     * 指定分割标记来分割字符串,返回风格字符串数组,支持多个分隔符连续的情况,比如：aaxxxaa ，分隔符"x"那么返回的是[aa,aa]两个字符串，二不是返回的["aa","","","","aa"]5个字符串]
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
     * 将字符串数组按照sep标记组合成一个字符串
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
     * 去掉字符串前边的所有sep标记
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
     * 去掉字符串后边的所有sep标记
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
     * 清除字符串前后的sep标记
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
     * 用字符串sReplace替换字符串str中的sFind字符串，只替换第一个，如果要全部替换请调用replaceStrAll函数
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
     * 在字符串中查找sToken字符串，然后用sArr里面的字符串来替换，最后返回被替换后的字符串，使用如下：
     * String str=“红队的成绩，分数XXXX,所用时间XXXX分钟!”;
     * str=getResStr(17,"XXXX",new String[]{"86","2"});
     * 调用getResStr函数后的字符串变为“红队的成绩，分数86,所用时间2分钟!”
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
     * 用字符串sReplace替换字符串str中的sFind字符串，全部替换个，如果只想替换第一个请调用replaceStr函数
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
     * 将字符串分割为最大宽度为lineW的字符串数组,且将\n作为换行标识，并且支持对文字设置颜色
     * 比如：我们的0xFF0000手机引擎0x000000是最棒的，那么“手机引擎”将会红色显示
     * 系统字体使用0xFF0000这种格式设置颜色,图片字体这使用0p2这种方式来设置调色板
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
            //"0x"作为颜色的标记,后面六位表示颜色值
            if (c == '0') {
                try {
                    if (i + 1 < pStr.length()) {
                        char c2 = pStr.charAt(i + 1);
                        if (c2 == 'x' || c2 == 'X') { //0x表示系统字体的颜色
                            String color = pStr.substring(i + 2, i + 8);
                            int tmp = Integer.parseInt(color, 16);
                            tmpLine.append(c);
                            tmpLine.append(c2);
                            tmpLine.append(color);
                            i += 7 + 1; //因为continue后没有i++了
                            continue;
                        } else if (c2 == 'p' || c2 == 'P') { //0p表示图片字体的颜色,调色板颜色只能为[0-9]
                            String color = pStr.substring(i + 2, i + 3);
                            int tmp = Integer.parseInt(color);
                            tmpLine.append(c);
                            tmpLine.append(c2);
                            tmpLine.append(color);
                            i += 2 + 1; //因为continue后没有i++了
                            continue;
                        }
                    }
                } catch (Exception ex) {
                    //发生异常说明不是有效的标记
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
            vLines.addElement(tmpLine.toString()); //最后一行
        }

        String[] sLines = new String[vLines.size()];
        vLines.copyInto(sLines);

        return sLines;
    }

    /**
     * 返回字符串的宽
     * @param Line String
     * @return int
     */
    public int getLineW(String Line) {
        int w = 0;
        for (int i = 0; i < Line.length(); i++) {
            char c = Line.charAt(i);
            //"0x"作为颜色的标记,后面六位表示颜色值
            if (c == '0') {
                try {
                    if (i + 1 < Line.length()) {
                        char c2 = Line.charAt(i + 1);
                        if ((c2 == 'x' || c2 == 'X')) {
                            String color = Line.substring(i + 2, i + 8);
                            int iColor = Integer.parseInt(color, 16);
                            i += 7; //因为i还会执行一次
                            continue;
                        } else if ((c2 == 'p' || c2 == 'P')) {
                            String color = Line.substring(i + 2, i + 3);
                            int iColor = Integer.parseInt(color);
                            i += 2; //因为i还会执行一次
                            continue;
                        }
                    }
                } catch (Exception ex) {
                    //发生异常说明不是有效的标记
                    //ex.printStackTrace();
                }
            }
            w += getCharWidth(c);
        }
        return w;
    }

    /**
     * 返回字符串数组中长度最宽的字符串的索引,如果Lines为空或者长度为0则返回-1
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
     * 返回字符串数组中最长的行宽，如果Lines为空或者长度为0则返回0
     * @param Lines String[]
     * @return int
     */
    public int getMaxLineW(String[] Lines) {
        int index = getLineMaxWIndex(Lines);
        return index != -1 ? getLineW(Lines[index]) : 0;
    }

    /**
     * 使用循环的方式一个字符一个字符绘制字符串
     * @param g Graphics
     * @param Line String
     * @param X int
     * @param Y int
     */
    public void drawLine(Graphics g, String Line, int X, int Y) {
        int x = X;
        for (int i = 0; i < Line.length(); i++) {
            char c = Line.charAt(i);
            //"0x"作为颜色的标记,后面六位表示颜色值
            if (c == '0') {
                try {
                    if (i + 1 < Line.length()) {
                        char c2 = Line.charAt(i + 1);
                        if ((c2 == 'x' || c2 == 'X')) {
                            String color = Line.substring(i + 2, i + 8);
                            int iColor = Integer.parseInt(color, 16);
                            g.setColor(iColor);
                            i += 7; //因为i还会执行一次
                            continue;
                        } else if (c2 == 'p' || c2 == 'P') { //0p表示图片字体的颜色,调色板颜色只能为[0-9]
                            String color = Line.substring(i + 2, i + 3);
                            int iColor = Integer.parseInt(color);
                            setImagePal(iColor);
                            i += 2; //因为i还会执行一次
                            continue;
                        }
                    }
                } catch (Exception ex) {
                    //发生异常说明不是有效的标记
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
     * 设置分页绘制字符串信息,pageLineW表示页面的宽度，pageH表示页面的高度,lineSpace表示行间距
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
     * 返回每页的行数
     * @return int
     */
    public int getPageSize() {
        return _nPageSize;
    }

    /**
     * 字符串分页后，返回有多少页
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
     * 绘制指定页的字符串,调用drawPage之前会先调用setPageSize来设置分页信息
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
     * 绘制字符串的对齐方式，左对齐
     */
    public final static int K_Line_Align_Left = 0;
    /**
     * 绘制字符串的对齐方式，居中对齐
     */
    public final static int K_Line_Align_Center = 1;
    /**
     * 绘制字符串的对齐方式，右对齐
     */
    public final static int K_Line_Align_Right = 2;
    /**
     * 在给定的位置及宽度的限制下绘制行字符串,align为绘制行的对齐方式，0表示左对齐,1表示居中对齐,2表示右对齐
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
     * 在给定的位置及宽度的限制下绘制页,调用drawPage之前会先调用setPageSize来设置分页信息,align为绘制行的对齐方式，0表示左对齐,1表示居中对齐,2表示右对齐
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
