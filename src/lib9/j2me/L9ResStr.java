package lib9.j2me;

/**
 * ��Lib9Editor�༭���е����ı��������Ӧ��������������й�
 * @author not attributable
 * @version 1.0
 */
public class L9ResStr {
    /**
     * ���캯��,����ָ���ı���������L9StrRes����,����ÿ���ַ�������ָ���ı���
     * @param sFile String
     * @param sEncode String
     */
    public L9ResStr(String sFile, String sEncode) {
        L9InputStream in = new L9InputStream("".getClass().getResourceAsStream(
                sFile));
        _nStrRow = in.readInt();
        _pStr_Text = new String[_nStrRow];
        for (int i = 0; i < _nStrRow; i++) { //��һ��Ϊ����
            _pStr_Text[i] = in.readString(sEncode);
        }
    }

    /**
     * ���캯����Ĭ�ϱ���ΪUTF-8
     * @param sFile String
     */
    public L9ResStr(String sFile) {
        this(sFile, "UTF-8");
    }

    private String[] _pStr_Text;
    private int _nStrRow;
    /**
     * �����ַ�����
     * @return int
     */
    public int getCount() {
        return _nStrRow;
    }

    /**
     * �����ַ������뷵�ض�Ӧ���ַ���
     * @param res_id int
     * @return String
     */
    public String getResStr(int res_id) {
        return _pStr_Text[res_id];
    }

    /**
     * ���ַ����������Ӧ���ַ����в���sToken�ַ�����Ȼ����sArr������ַ������滻����󷵻ر��滻����ַ���
     * ע���滻�ַ���sReplace��sep�ָ���ʹ�����£�
     * String str=����ӵĳɼ�������XXXX,����ʱ��XXXX����!��;
     * str=getResStr(17,"XXXX","86,2",",");
     * ����getResStr��������ַ�����Ϊ����ӵĳɼ�������86,����ʱ��2����!��
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
