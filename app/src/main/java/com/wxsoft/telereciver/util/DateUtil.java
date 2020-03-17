package com.wxsoft.telereciver.util;

import android.content.Context;

import com.wxsoft.telereciver.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    /**
     * 判断时间date1是否在时间date2之前
     * 时间格式 2005-4-21 16:16:34
     * @param date1
     * @param date2
     * @return
     */
    public static boolean isDateBefore(String date1,String date2){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            if (sdf.parse(date1).getTime() - sdf.parse(date2).getTime() < 0) {
                return true;
            }
            return false;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 计算剩余年数（就是计算工作经验）
     * @param startDateStr
     * @param endDateStr
     * @return
     */
    public static int remainYear(String startDateStr, String endDateStr) {
        Calendar calS = Calendar.getInstance();
        java.util.Date startDate = null;
        java.util.Date endDate = null;
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        try {
            endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        calS.setTime(startDate);
        int startY = calS.get(Calendar.YEAR);
        int startM = calS.get(Calendar.MONTH);
        int startD = calS.get(Calendar.DATE);
        int startDayOfMonth = calS.getActualMaximum(Calendar.DAY_OF_MONTH);

        calS.setTime(endDate);
        int endY = calS.get(Calendar.YEAR);
        int endM = calS.get(Calendar.MONTH);
        int endD = calS.get(Calendar.DATE);
        int endDayOfMonth = calS.getActualMaximum(Calendar.DAY_OF_MONTH);

        if (endDate.compareTo(startDate)<0) {
            return 0;
        }
        int lday = endD - startD;
        if (lday < 0) {
            endM = endM - 1;
            lday = startDayOfMonth + lday;
        }
        //处理天数问题，如：2011-01-01 到 2013-12-31  2年11个月31天     实际上就是3年
        if (lday == endDayOfMonth) {
            endM = endM + 1;
        }
        int mos = (endY - startY) * 12 + (endM - startM);
        return mos / 12;
    }

    /**
     * 计算剩余年、月、日
     * @param startDateStr
     * @param endDateStr
     * @return
     */
    public static String remainDateToString(String startDateStr, String endDateStr){
        Calendar calS = Calendar.getInstance();
        java.util.Date startDate = null;
        java.util.Date endDate = null;
        try {
            startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        try {
            endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return "";
        }
        calS.setTime(startDate);
        int startY = calS.get(Calendar.YEAR);
        int startM = calS.get(Calendar.MONTH);
        int startD = calS.get(Calendar.DATE);
        int startDayOfMonth = calS.getActualMaximum(Calendar.DAY_OF_MONTH);

        calS.setTime(endDate);
        int endY = calS.get(Calendar.YEAR);
        int endM = calS.get(Calendar.MONTH);
        //处理2011-01-10到2011-01-10，认为服务为一天
        int endD = calS.get(Calendar.DATE) + 1;
        int endDayOfMonth = calS.getActualMaximum(Calendar.DAY_OF_MONTH);

        StringBuilder sBuilder = new StringBuilder();
        if (endDate.compareTo(startDate)<0) {
            return sBuilder.append("过期").toString();
        }
        int lday = endD - startD;
        if (lday<0) {
            endM = endM - 1;
            lday = startDayOfMonth+ lday;
        }
        //处理天数问题，如：2011-01-01 到 2013-12-31  2年11个月31天     实际上就是3年
        if (lday == endDayOfMonth) {
            endM = endM + 1;
            lday =0;
        }
        int mos = (endY - startY)*12 + (endM- startM);
        int lyear = mos/12;
        int lmonth = mos%12;
        if (lyear >0) {
            sBuilder.append(lyear+"年");
        }
        if (lmonth > 0) {
            sBuilder.append(lmonth+"个月");
        }
        if (lday >0 ) {
            sBuilder.append(lday+"天");
        }
        return sBuilder.toString();
    }


    static SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    /**
     * 24小时制转化成12小时制
     *
     * @param strDay
     */
    public static String timeFormatStr(Calendar calendar, String strDay) {
        String tempStr = "";
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        if (hour > 11) {
            tempStr = "下午" + " " + strDay;
        } else {
            tempStr = "上午" + " " + strDay;
        }
        return tempStr;
    }

    /**
     * 时间转化为星期
     *
     * @param indexOfWeek
     *            星期的第几天
     */
    public static String getWeekDayStr(int indexOfWeek) {
        String weekDayStr = "";
        switch (indexOfWeek) {
            case 1:
                weekDayStr = "星期日";
                break;
            case 2:
                weekDayStr = "星期一";
                break;
            case 3:
                weekDayStr = "星期二";
                break;
            case 4:
                weekDayStr = "星期三";
                break;
            case 5:
                weekDayStr = "星期四";
                break;
            case 6:
                weekDayStr = "星期五";
                break;
            case 7:
                weekDayStr = "星期六";
                break;
        }
        return weekDayStr;
    }

    /**
     * 将时间戳格式化，13位的转为10位
     *
     * @param timestamp
     * @return
     */
    public static long timestampFormate(long timestamp) {
        String timestampStr = timestamp + "";
        if (timestampStr.length() == 13) {
            timestamp = timestamp / 1000;
        }
        return timestamp;
    }

    /**
     * 获取日起时间秒差
     *
     * @param time
     *            需要格式化的时间 如"2014-07-14 19:01:45"
     * @param pattern
     *            输入参数time的时间格式 如:"yyyy-MM-dd HH:mm:ss"
     *            <p/>
     *            如果为空则默认使用"yyyy-MM-dd HH:mm:ss"格式
     * @return time为null，或者时间格式不匹配，输出空字符""
     * @throws ParseException
     */
    public static long formatLongTime(String time, String pattern) {
        long dTime = 0;
        if (time != null) {
            if (pattern == null)
                pattern = "yyyy-MM-dd HH:mm:ss";
            Date tDate;
            try {
                tDate = new SimpleDateFormat(pattern).parse(time);
                Date today = new Date();
                if (tDate != null) {
                    dTime = (today.getTime() - tDate.getTime()) / 1000;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dTime;
    }

    /**
     * 获取日期时间戳
     *
     * @param time
     *            需要格式化的时间 如"2014-07-14 19:01:45"
     * @param pattern
     *            输入参数time的时间格式 如:"yyyy-MM-dd HH:mm:ss"
     *            <p/>
     *            如果为空则默认使用"yyyy-MM-dd HH:mm:ss"格式
     * @return time为null，或者时间格式不匹配，输出空字符""
     * @throws ParseException
     */
    public static long timeStamp(String time, String pattern) {
        long dTime = 0;
        if (time != null) {
            if (pattern == null)
                pattern = "yyyy-MM-dd HH:mm:ss";
            Date tDate;
            try {
                tDate = new SimpleDateFormat(pattern).parse(time);
                if (tDate != null) {
                    dTime = tDate.getTime() / 1000;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return dTime;
    }


    public static String getCustomTimeStamp(Context context,String time, String pattern) {
        long dTime = 0;
        if (time != null) {
            if (pattern == null)
                pattern = "yyyy-MM-dd HH:mm:ss";
            Date tDate;
            try {
                tDate = new SimpleDateFormat(pattern).parse(time);
                if (tDate != null) {
                    dTime = tDate.getTime() ;
                    long now=System.currentTimeMillis();
                    long res=now-dTime;
                    if(res>=7*24*3600*1000){

                       return "7"+context.getString(R.string.date_day)+"前";
                    }else  if(res/(24*3600000)>=1){
                        return "1"+context.getString(R.string.date_day)+"前";
                    }else if((res/3600000)>=1){
                        return String.valueOf(res/3600000)+context.getString(R.string.date_hour)+"前";
                    }else {
                        return String.valueOf(res/60000)+context.getString(R.string.date_minute)+"前";
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 时间转化为显示字符串
     *
     * @param timeStamp
     *            单位为秒
     */
    public static String getTimeStr(long timeStamp) {
        if (timeStamp == 0)
            return "";
        Calendar inputTime = Calendar.getInstance();
        inputTime.setTimeInMillis(timeStamp * 1000);
        Date currenTimeZone = inputTime.getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(currenTimeZone);
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (calendar.before(inputTime)) {
            return "昨天";
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, -5);
            if (calendar.before(inputTime)) {
                return getWeekDayStr(inputTime.get(Calendar.DAY_OF_WEEK));
            } else {
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                int year = inputTime.get(Calendar.YEAR);
                int month = inputTime.get(Calendar.MONTH);
                int day = inputTime.get(Calendar.DAY_OF_MONTH);
                return year + "/" + month + "/" + day;
            }

        }

    }


    /**
     * 时间转化为聊天界面显示字符串
     *
     * @param timeStamp
     *            单位为秒
     */
    public static String getChatTimeStr(long timeStamp) {
        if (timeStamp == 0)
            return "";
        Calendar inputTime = Calendar.getInstance();
        String timeStr = timeStamp + "";
        if (timeStr.length() == 10) {
            timeStamp = timeStamp * 1000;
        }
        inputTime.setTimeInMillis(timeStamp);
        Date currenTimeZone = inputTime.getTime();
        Calendar calendar = Calendar.getInstance();
        // if (calendar.before(inputTime)){
        // //当前时间在输入时间之前
        // SimpleDateFormat sdf = new SimpleDateFormat("yyyy" +
        // "年"+"MM"+"月"+"dd"+"日");
        // return sdf.format(currenTimeZone);
        // }
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm");
            return timeFormatStr(inputTime, sdf.format(currenTimeZone));
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("h:mm");
            return "昨天" + " " + timeFormatStr(inputTime, sdf.format(currenTimeZone));
        } else {
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.MONTH, Calendar.JANUARY);
            if (calendar.before(inputTime)) {
                SimpleDateFormat sdf = new SimpleDateFormat("M" + "月" + "d" + "日");
                String temp1 = sdf.format(currenTimeZone);
                SimpleDateFormat sdf1 = new SimpleDateFormat("h:mm");
                String temp2 = timeFormatStr(inputTime, sdf1.format(currenTimeZone));
                return temp1 + temp2;
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + "/" + "M" + "/" + "d" + " ");
                String temp1 = sdf.format(currenTimeZone);
                SimpleDateFormat sdf1 = new SimpleDateFormat("h:mm");
                String temp2 = timeFormatStr(inputTime, sdf1.format(currenTimeZone));
                return temp1 + temp2;
            }

        }

    }

    public static String currentTime(){
        long timestamp=System.currentTimeMillis();
        Calendar current = Calendar.getInstance();
        current.setTimeInMillis(timestamp);
        Date currenTimeZone = current.getTime();
        return sf.format(currenTimeZone);

    }

    /**
     * 群发使用的时间转换
     */
    public static String multiSendTimeToStr(long timeStamp) {

        if (timeStamp == 0)
            return "";
        Calendar inputTime = Calendar.getInstance();
        String timeStr = timeStamp + "";
        if (timeStr.length() == 10) {
            timeStamp = timeStamp * 1000;
        }
        inputTime.setTimeInMillis(timeStamp);
        Date currenTimeZone = inputTime.getTime();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        if (calendar.before(inputTime)) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return sdf.format(currenTimeZone);
        }
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        if (calendar.before(inputTime)) {
            @SuppressWarnings("unused")
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            return "昨天";
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, -5);
            if (calendar.before(inputTime)) {
                return getWeekDayStr(inputTime.get(Calendar.DAY_OF_WEEK));
            } else {
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                if (calendar.before(inputTime)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("M" + "月" + "d" + "日");
                    String temp1 = sdf.format(currenTimeZone);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                    String temp2 = sdf1.format(currenTimeZone);
                    return temp1 + temp2;
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy" + "年" + "M" + "月" + "d" + "日");
                    String temp1 = sdf.format(currenTimeZone);
                    SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                    String temp2 = sdf1.format(currenTimeZone);
                    return temp1 + temp2;
                }
            }
        }
    }

    /**
     * 格式化时间（输出类似于 刚刚, 4分钟前, 一小时前, 昨天这样的时间）
     *
     * @param time
     *            需要格式化的时间 如"2014-07-14 19:01:45"
     * @param pattern
     *            输入参数time的时间格式 如:"yyyy-MM-dd HH:mm:ss"
     *            <p/>
     *            如果为空则默认使用"yyyy-MM-dd HH:mm:ss"格式
     * @return time为null，或者时间格式不匹配，输出空字符""
     */
    public static String formatDisplayTime(String time, String pattern) {
        String display = "";
        int tMin = 60 * 1000;
        int tHour = 60 * tMin;
        int tDay = 24 * tHour;

        if (time != null) {
            if (pattern == null)
                pattern = "yyyy-MM-dd HH:mm:ss";
            try {
                Date tDate = new SimpleDateFormat(pattern).parse(time);
                Date today = new Date();
                SimpleDateFormat thisYearDf = new SimpleDateFormat("yyyy");
                SimpleDateFormat todayDf = new SimpleDateFormat("yyyy-MM-dd");
                Date thisYear = new Date(thisYearDf.parse(thisYearDf.format(today)).getTime());
                Date yesterday = new Date(todayDf.parse(todayDf.format(today)).getTime());
                Date beforeYes = new Date(yesterday.getTime() - tDay);
                if (tDate != null) {
                    @SuppressWarnings("unused")
                    SimpleDateFormat halfDf = new SimpleDateFormat("MM月dd日");
                    long dTime = today.getTime() - tDate.getTime();
                    if (tDate.before(thisYear)) {
                        display = new SimpleDateFormat("yyyy年MM月dd日").format(tDate);
                    } else {

                        if (dTime < tMin) {
                            display = "刚刚";
                        } else if (dTime < tHour) {
                            display = (int) Math.ceil(dTime / tMin) + "分钟前";
                        } else if (dTime < tDay && tDate.after(yesterday)) {
                            display = (int) Math.ceil(dTime / tHour) + "小时前";
                        } else if (tDate.after(beforeYes) && tDate.before(yesterday)) {
                            display = "昨天  " + new SimpleDateFormat("HH:mm").format(tDate);
                        } else {
                            display = multiSendTimeToStr(tDate.getTime() / 1000);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return display;
    }
}
