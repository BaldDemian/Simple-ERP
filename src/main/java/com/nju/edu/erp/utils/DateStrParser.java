package com.nju.edu.erp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateStrParser {

    public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String dateToStr(Date date) {
        return format.format(date);
    }

    /**
     * 将给定的日期字符串转换成一个Date对象
     * @param str 必须是"yyyy-MM-dd HH:mm:ss"的格式
     * @return
     */
    public static Date strToDate(String str) {
        Date date = null;
        try {
            date = format.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
