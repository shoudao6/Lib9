package lib9.j2me;

import java.util.Date;
import java.util.Calendar;
import java.util.TimeZone;

/**
 �ṩ��������ʱ��ķ��������磺��������ʱ�����ߵĲ�
 * @author not attributable
 * @version 1.0
 */
public class L9DateTime {
    /**
     * ��ʾ��ĳ���
     */
    public final static int K_DateTime_Year = 0;
    /**
     * ��ʾ�µĳ���
     */
    public final static int K_DateTime_Month = 1;
    /**
     * ��ʾ�յĳ���
     */
    public final static int K_DateTime_Day = 2;
    /**
     * ��ʾСʱ�ĳ���
     */
    public final static int K_DateTime_Hour = 3;
    /**
     * ��ʾ���ӵĳ���
     */
    public final static int K_DateTime_Minute = 4;
    /**
     * ��ʾ��ĳ���
     */
    public final static int K_DateTime_Second = 5;
    /**
     * ��ʾ�ܵĳ���
     */
    public final static int K_DateTime_DayOfWeek = 6;

    private int _Year;
    private int _Month;
    private int _Day;
    private int _Hour;
    private int _Minute;
    private int _Second;
    private int _DayOfWeek;
    /**
     * ���ݳ�����ʱ���ʱ������L9DateTime����
     * @param time long
     * @param tz TimeZone
     */
    public L9DateTime(long time, TimeZone tz) {
        Date date = new Date(time);
        Calendar calendar = Calendar.getInstance(tz);
        calendar.setTime(date);

        _Year = calendar.get(Calendar.YEAR);
        _Month = calendar.get(Calendar.MONTH) + 1;
        _Day = calendar.get(Calendar.DAY_OF_MONTH);
        _Hour = calendar.get(Calendar.HOUR_OF_DAY);
        _Minute = calendar.get(Calendar.MINUTE);
        _Second = calendar.get(Calendar.SECOND);
        _DayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * ���ݳ�����ʱ���Ĭ��ʱ��������L9DateTime����
     * @param time long
     */
    public L9DateTime(long time) {
        this(time, Calendar.getInstance().getTimeZone());
    }

    /**
     * ����ϵͳ��ǰ����ʱ�����
     */
    public L9DateTime() {
        this(System.currentTimeMillis());
    }

    /**
     * ��������ʱ���ַ���������L9DateTime����,���磺2010/12/08 14:17:56
     * ���ڸ�ʽΪ ��/��/��,ʱ���ʽΪ ʱ:��:��,������ʱ���ɿո�ָ�,�������ڵķָ�������Ϊ-
     * ����L9DateTime�������ֻ�����ڲ��ֻ���ʱ�䲿��
     * @param datetime String
     */
    public L9DateTime(String datetime) {
        datetime = L9Str.trimStr(datetime, " ");
        datetime = L9Str.trimStr(datetime, " ");
        String[] arr = L9Str.splitStr(datetime, " ");
        if (arr.length == 1) {
            if (arr[0].indexOf(":") != -1) {
                calcTime(arr[0]);
            } else {
                calcDate(arr[0]);
                calcWeek();
            }
        } else {
            calcDate(arr[0]);
            calcWeek();
            calcTime(arr[1]);
        }
    }

    private void calcTime(String time) {
        String[] timeArr = L9Str.splitStr(time, ":");
        _Hour = Integer.parseInt(timeArr[0]);
        _Minute = Integer.parseInt(timeArr[1]);
        _Second = Integer.parseInt(timeArr[2]);
    }

    private void calcDate(String date) {
        String[] dateArr = null;
        if (date.indexOf("/") != -1) {
            dateArr = L9Str.splitStr(date, "/");
        } else {
            dateArr = L9Str.splitStr(date, "-");
        }
        _Year = Integer.parseInt(dateArr[0]);
        _Month = Integer.parseInt(dateArr[1]);
        _Day = Integer.parseInt(dateArr[2]);
    }

    private void calcWeek() {
        //�������ڼ�
        int Y = _Year;
        int M = _Month;
        int D = _Day;
        if (M == 1 || M == 2) {
            M += 12;
            Y--;
        }
        //0��ʾ����һ 1��ʾ���ڶ���
        _DayOfWeek = (D + 2 * M + 3 * (M + 1) / 5 + Y + Y / 4 - Y / 100 +
                      Y / 400) % 7;
        //�����޸�Ϊ�����˵ĳ�ʶ��0��ʾ������1��ʾ����һ��
        _DayOfWeek += 1;
        _DayOfWeek = _DayOfWeek % 7;
    }

    /**
     * �������ڵ�ĳ������
     * @param part int
     * @return int
     */
    public int getPart(int part) {
        switch (part) {
        case K_DateTime_Year:
            return _Year;
        case K_DateTime_Month:
            return _Month;
        case K_DateTime_Day:
            return _Day;
        case K_DateTime_Hour:
            return _Hour;
        case K_DateTime_Minute:
            return _Minute;
        case K_DateTime_Second:
            return _Second;
        case K_DateTime_DayOfWeek:
            return _DayOfWeek;
        }
        return -1;
    }

    /**
     * ������-�ָ������ڣ����磺2010-2-12
     * @return String
     */
    public String getDate() {
        return "" + _Year + "-" + _Month + "-" + _Day;
    }

    /**
     * �����ԣ��ָ���ʱ��,���磺14:27:58
     * @return String
     */
    public String getTime() {
        return "" + _Hour + ":" + _Minute + ":" + _Second;
    }

    /**
     * �ж��Ƿ�Ϊ����
     * @param year int
     * @return boolean
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    /**
     * ����ָ���µ�����
     * @param year int
     * @param month int
     * @return int
     */
    private static int getMonthDays(int year, int month) {
        int days = 0;
        switch (month) {
        case 1:
        case 3:
        case 5:
        case 7:
        case 8:
        case 10:
        case 12:
            days = 31;
            break;
        case 4:
        case 6:
        case 9:
        case 11:
            days = 30;
            break;
        case 2:
            if (isLeapYear(year)) {
                days = 29;
            } else {
                days = 28;
            }
            break;
        }
        return days;
    }

    /**
     * ������������֮�������
     * @param setStr String
     * @param SysDate String
     * @return int
     */
    private static int countDays(String dateSmall, String dateBig) {
        String[] str = new String[] {};
//    String setStr = "2000-1-4";
        int m_setYear = 0;
        int m_setMonth = 0;
        int m_setDay = 0;
        int m_SysYear = 0;
        int m_SysMonth = 0;
        int m_SysDay = 0;

        str = L9Str.splitStr(dateSmall, "-");
        m_setYear = Integer.parseInt(str[0]);
        m_setMonth = Integer.parseInt(str[1]);
        m_setDay = Integer.parseInt(str[2]);
//    System.out.println(m_setYear+"  "+m_setMonth+"  "+m_setDay);

        str = L9Str.splitStr(dateBig, "-");
        m_SysYear = Integer.parseInt(str[0]);
        m_SysMonth = Integer.parseInt(str[1]);
        m_SysDay = Integer.parseInt(str[2]);

        int totalDays = 0;
        for (int i = m_setYear + 1; i < m_SysYear; i++) {
            if (isLeapYear(i)) {
                totalDays += 366;
            } else {
                totalDays += 365;
            }
        }

        if (m_setYear != m_SysYear) {
            totalDays += (getMonthDays(m_setYear, m_setMonth) - m_setDay);
            for (int i = m_setMonth + 1; i <= 12; i++) {
                totalDays += getMonthDays(m_setYear, i);
            }
            for (int i = 1; i < m_SysMonth; i++) {
                totalDays += getMonthDays(m_SysYear, i);
            }
            totalDays += m_SysDay;
        } else {

            if (m_setMonth != m_SysMonth) {
                totalDays += (getMonthDays(m_setYear, m_setMonth) - m_setDay);
                for (int i = m_setMonth + 1; i < m_SysMonth; i++) {
                    totalDays += getMonthDays(m_setYear, i);
                }
                totalDays += m_SysDay;
            } else {
                totalDays += (m_SysDay - m_setDay);
            }

        }

        return totalDays;
    }

    /**
     * ������������ʱ��֮��Ĳ�part,part����Ϊ�ꡢ�¡��յȳ���
     * @param part int
     * @param date1 L9DateTime
     * @param date2 L9DateTime
     * @return int
     */
    public static int getDiff(int part, L9DateTime date1, L9DateTime date2) {
        int diffYear = date2.getPart(K_DateTime_Year) -
                       date1.getPart(K_DateTime_Year);
        int diffMonth = date2.getPart(K_DateTime_Month) -
                        date1.getPart(K_DateTime_Month);
        int diffDay = date2.getPart(K_DateTime_Day) -
                      date1.getPart(K_DateTime_Day);
        int diffHour = date2.getPart(K_DateTime_Hour) -
                       date1.getPart(K_DateTime_Hour);
        int diffMinue = date2.getPart(K_DateTime_Minute) -
                        date1.getPart(K_DateTime_Minute);
        int diffSecond = date2.getPart(K_DateTime_Second) -
                         date1.getPart(K_DateTime_Second);

        switch (part) {
        case K_DateTime_Year:
            return diffYear;
        case K_DateTime_Month:
            return diffYear * 12 + diffMonth;
        case K_DateTime_Day:
            if (diffYear > 0) {
                return countDays(date1.getDate(), date2.getDate());
            } else if (diffYear == 0) {
                if (diffMonth > 0) {
                    return countDays(date1.getDate(), date2.getDate());
                } else if (diffMonth == 0) {
                    if (diffDay > 0) {
                        return countDays(date1.getDate(), date2.getDate());
                    } else if (diffDay == 0) {
                        return 0;
                    } else {
                        return -countDays(date2.getDate(), date1.getDate());
                    }
                } else {
                    return -countDays(date2.getDate(), date1.getDate());
                }
            } else {
                return -countDays(date2.getDate(), date1.getDate());
            }
        case K_DateTime_Hour:
            return getDiff(K_DateTime_Day, date1, date2) * 24 + diffHour;
        case K_DateTime_Minute:
            return getDiff(K_DateTime_Hour, date1, date2) * 60 + diffMinue;
        case K_DateTime_Second:
            return getDiff(K_DateTime_Minute, date1, date2) * 60 + diffSecond;
        case K_DateTime_DayOfWeek:
            return getDiff(K_DateTime_Day, date1, date2) / 7;
        default:
            try {
                throw new Exception("���ʹ���!");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            }
                    return -1;
        }

    }
