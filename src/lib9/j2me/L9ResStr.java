package lib9.j2me;

/**
 * 与Lib9Editor编辑器中导出文本数据相对应，往往与多语言有关
 * @author not attributable
 * @version 1.0
 */
public class L9ResStr {
    /**
     * 构造函数,根据指定的编码来构造L9StrRes对象,里面每个字符串都是指定的编码
     * @param sFile String
     * @param sEncode String
     */
    public L9ResStr(String sFile, String sEncode) {
        L9InputStream in = new L9InputStream("".getClass().getResourceAsStream(
                sFile));
        _nStrRow = in.readInt();
        _pStr_Text = new String[_nStrRow];
        for (int i = 0; i < _nStrRow; i++) { //第一行为标题
            _pStr_Text[i] = in.readString(sEncode);
        }
    }

    /**
     * 构造函数，默认编码为UTF-8
     * @param sFile String
     */
    public L9ResStr(String sFile) {
        this(sFile, "UTF-8");
    }

    private String[] _pStr_Text;
    private int _nStrRow;
    /**
     * 返回字符串数
     * @return int
     */
    public int getCount() {
        return _nStrRow;
    }

    /**
     * 根据字符串编码返回对应的字符串
     * @param res_id int
     * @return String
     */
    public String getResStr(int res_id) {
        return _pStr_Text[res_id];
    }

    /**
     * 在字符串编号所对应的字符串中查找sToken字符串，然后用sArr里面的字符串来替换，最后返回被替换后的字符串
     * 注意替换字符串sReplace以sep分隔，使用如下：
     * String str=“红队的成绩，分数XXXX,所用时间XXXX分钟!”;
     * str=getResStr(17,"XXXX","86,2",",");
     * 调用getResStr函数后的字符串变为“红队的成绩，分数86,所用时间2分钟!”
     * @param res_id int
     * @param sToken String
     * @param sReplace String
     * @param sep String
     * @return String
     */
    public String getResStr(int res_id, String sToken, String sReplace,
                            String sep) {
        return L9Str.replaceStr(_pStr_Text[res_id], sToken, sReplace, sep);
    }
}
