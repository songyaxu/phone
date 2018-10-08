package utils;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author ：yaxuSong
 * @Description:
 * @Date: 14:49 2018/4/8
 * @Modified by:
 */
public class TimeUtil {

    /**
     * 一秒钟
     */
    public static final int ONE_SECOND = 1;
    /**
     * 一分钟
     */
    public static final int ONE_MINUTE = 60;
    /**
     * 一小时
     */
    public static final int ONE_HOUR = 60 * ONE_MINUTE;
    /**
     * 一天
     */
    public static final int ONE_DAY = 24 * ONE_HOUR;
    /**
     * 一周
     */
    public static final int A_WEEK = 7 * ONE_DAY;
    /**
     * 默认日期格式
     */
    public static final String DEFAULT_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 默认日期格式
     */
    public static final String DEFAULT_FORMAT_STR = "yyyyMMddHHmmss";

    /**
     * 整型日期格式
     */
    public static final String INTEGER_FORMAT = "yyyyMMdd";
    /**
     * 字符型日期格式
     */
    public static final String TIME_FORMAT = "yyyy-MM-dd";

    public static final String TIME_FORMAT_CN ="yyyy年MM月dd日";

    public static final String TIME_BEIJING_FORMAT="MM/dd HH:mm:ss";

    public static final String TIME_BEIJING_FORMAT_1="MM/dd HH:mm";

    public static final SimpleDateFormat TIME_FORMAT_MILLION = new SimpleDateFormat(
            "HH:mm:ss:SSS");

    public static final String TIME_SHORT_FORMAT="MM/dd";

    /**
     * 获取系统当前时间戳
     *
     * @return int
     */
    public static int time() {
        return (int) (System.currentTimeMillis() / 1000);
    }
    

    /**
     * 根据传进来的系统时间获取时间戳
     *
     * @param timeMillis
     * @return
     */
    public static int getTime(long timeMillis) {
        return (int) (timeMillis / 1000);
    }

    /**
     * 获取系统当前时间
     *
     * @return long
     */
    public static long millisTime() {
        return System.currentTimeMillis();
    }

    /**
     * 把当前时间格式化成yyyy-MM-dd HH:mm:ss
     *
     * @return String
     */
    public static String date() {
        return new SimpleDateFormat(DEFAULT_FORMAT).format(System.currentTimeMillis());
    }

    public static String date(Date date,String timeFormat) {
        return new SimpleDateFormat(timeFormat).format(date);
    }

    public static long getZeroTimestamp(){
        Calendar calendar = Calendar.getInstance();
        Calendar zeroCalendar = Calendar.getInstance();
        zeroCalendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),0,0,0);
        return zeroCalendar.getTimeInMillis()/1000;
    }

    public static long getCurrenTimestampOrLate(){
        Calendar calendar = Calendar.getInstance();
        Calendar currentCalendar = Calendar.getInstance();
        return (((currentCalendar.getTimeInMillis()/1000)-60)/60)*60;
    }

    public static String aWeekAgo(Calendar calendar) {
        Date current = calendar.getTime();
        Long aWeekAgo = current.getTime() - A_WEEK * 1000;
        return new SimpleDateFormat(TIME_FORMAT).format(new Date(aWeekAgo));
    }

    public static Date getTotayZero(){
        Calendar calendar = Calendar.getInstance();
        Calendar zeroCalendar = Calendar.getInstance();
        zeroCalendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),0,0,0);
        zeroCalendar.set(Calendar.MILLISECOND,0);
        return zeroCalendar.getTime();
    }

    //获取前几天的零时时间
    public static Date geBeforeZero(int num){
        Calendar calendar = Calendar.getInstance();
        Calendar zeroCalendar = Calendar.getInstance();
        zeroCalendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),0,0,0);
        zeroCalendar.add(Calendar.DATE, 0-num);
        return zeroCalendar.getTime();
    }

    public static Date getTime(int year,int month,int day,int hour,int minute,int second){
        Calendar calendar = Calendar.getInstance();
        calendar.set(year,month-1,day,hour,minute,second);
        return calendar.getTime();
    }

    //将传入的时间 格式化
    public static String formatTime(Date date){
        SimpleDateFormat format =new SimpleDateFormat(" HH:mm");
        SimpleDateFormat format1 =new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String str = format.format(date);

        if(date.compareTo(getTotayZero()) > -1 ){
            return "今天"+str;
        }else if(date.compareTo(geBeforeZero(1)) > -1){
            return "昨天"+str;
        }else if(date.compareTo(geBeforeZero(2)) > -1){
            return "前天"+str;
        }else{
            str = format1.format(date);
            return str;
        }
    }

    /**
     * 获取当前最新小时
     * @param calendar
     * @return
     */
    public static Date getLatestHourTime(Calendar calendar){
        Calendar calendarCurrent = Calendar.getInstance();
        calendarCurrent.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.HOUR_OF_DAY),0,0);
        calendarCurrent.set(Calendar.MILLISECOND,0);
        return calendarCurrent.getTime();
    }

    public static Date getLatestHourTime(Date date){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.HOUR_OF_DAY),0,0);
        calendar.set(Calendar.MILLISECOND,0);
        return calendar.getTime();
    }

    public static long getLatestHourTimeBefore(Calendar calendar){
        Calendar calendarCurrent = Calendar.getInstance();
        calendarCurrent.set(calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH),calendar.get(Calendar.HOUR_OF_DAY),0,0);
        calendarCurrent.set(Calendar.MILLISECOND,0);
        return calendarCurrent.getTime().getTime()-3600000;
    }
    
    public static String getDayTime(Date date){
        SimpleDateFormat format =new SimpleDateFormat(TIME_FORMAT);
        return format.format(date);
    }

    public static String getDefaultTimeStr(Date date){
        SimpleDateFormat format =new SimpleDateFormat(DEFAULT_FORMAT_STR);
        return format.format(date);
    }

    public static String getFormatTimeStr(Date date,String dateStrFormatter){
        SimpleDateFormat format =new SimpleDateFormat(dateStrFormatter);
        return format.format(date);
    }

    public static Date getDateByString(String time,String timeFormat){
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat);
        try {
            Date utilDate = sdf.parse(time);
            return utilDate;
        }catch (Exception e){
            return null;
        }
    }

    public static void main(String[] args) {

        Date date = new Timestamp(1537428674963L);
        System.out.println(getFormatTimeStr(date,DEFAULT_FORMAT));
    }
}
