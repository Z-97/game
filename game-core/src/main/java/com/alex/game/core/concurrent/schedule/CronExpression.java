package com.alex.game.core.concurrent.schedule;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableSet;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TimeZone;
import java.util.TreeSet;

/**
 * quantz的cron表达式，实现getTimeBefore，quartz到今天(20170403)一直没更新getTimeBefore的实现(QUARTZ-423)
 * 代码参考quantz的CronExpression，主要是为了在游戏中支持cron schedule，java的ThreadPoolScheduler不支持cron和固定次数。
 * getTimeBefore主要用于游戏中的停服，当机导致定时任务错过执行，游戏服启动时，获取启动时刻前一次执行时间，
 * 当玩家身上保存的任务执行时刻小于任务的前一次的执行则进行相应的重置或者刷新操作,如签到。
 * 
 * @author Alex
 * @date 2017年4月3日 下午7:06:59
 */
public class CronExpression implements Serializable {

    private static final long serialVersionUID = 12423409423L;

    protected static final int SECOND = 0;
    protected static final int MINUTE = 1;
    protected static final int HOUR = 2;
    protected static final int DAY_OF_MONTH = 3;
    protected static final int MONTH = 4;
    protected static final int DAY_OF_WEEK = 5;
    protected static final int YEAR = 6;
    protected static final int ALL_SPEC_INT = 99; // '*'
    protected static final int NO_SPEC_INT = 98; // '?'
    protected static final Integer ALL_SPEC = Integer.valueOf(ALL_SPEC_INT);
    protected static final Integer NO_SPEC = Integer.valueOf(NO_SPEC_INT);
    
    protected static final Map<String, Integer> monthMap = new HashMap<String, Integer>(20);
    protected static final Map<String, Integer> dayMap = new HashMap<String, Integer>(60);
    static {
        monthMap.put("JAN", Integer.valueOf(0));
        monthMap.put("FEB", Integer.valueOf(1));
        monthMap.put("MAR", Integer.valueOf(2));
        monthMap.put("APR", Integer.valueOf(3));
        monthMap.put("MAY", Integer.valueOf(4));
        monthMap.put("JUN", Integer.valueOf(5));
        monthMap.put("JUL", Integer.valueOf(6));
        monthMap.put("AUG", Integer.valueOf(7));
        monthMap.put("SEP", Integer.valueOf(8));
        monthMap.put("OCT", Integer.valueOf(9));
        monthMap.put("NOV", Integer.valueOf(10));
        monthMap.put("DEC", Integer.valueOf(11));

        dayMap.put("SUN", Integer.valueOf(1));
        dayMap.put("MON", Integer.valueOf(2));
        dayMap.put("TUE", Integer.valueOf(3));
        dayMap.put("WED", Integer.valueOf(4));
        dayMap.put("THU", Integer.valueOf(5));
        dayMap.put("FRI", Integer.valueOf(6));
        dayMap.put("SAT", Integer.valueOf(7));
    }

    private String cronExpression = null;
    private TimeZone timeZone = TimeZone.getDefault();;
    protected transient TreeSet<Integer> seconds;
    protected transient TreeSet<Integer> minutes;
    protected transient TreeSet<Integer> hours;
    protected transient TreeSet<Integer> daysOfMonth;
    protected transient TreeSet<Integer> months;
    protected transient TreeSet<Integer> daysOfWeek;
    protected transient TreeSet<Integer> years;

    protected transient boolean lastdayOfWeek = false;
    protected transient int nthdayOfWeek = 0;
    protected transient boolean lastdayOfMonth = false;
    protected transient boolean nearestWeekday = false;
    protected transient int lastdayOffset = 0;
    protected transient boolean expressionParsed = false;
    
    //最小日期为当前日期前的100年，防止很鬼扯的cron表达式计算
    public static final int MIN_YEAR = Calendar.getInstance().get(Calendar.YEAR) - 100;
    public static final int MAX_YEAR = Calendar.getInstance().get(Calendar.YEAR) + 100;

    /**
     * Constructs a new <CODE>CronExpression</CODE> based on the specified 
     * parameter.
     * 
     * @param cronExpression String representation of the cron expression the
     *                       new object should represent
     * @throws java.text.ParseException
     *         if the string expression cannot be parsed into a valid 
     *         <CODE>CronExpression</CODE>
     */
    public CronExpression(String cronExpression) {
        if (cronExpression == null) {
            throw new IllegalArgumentException("cronExpression cannot be null");
        }
       
        try {
        	 this.cronExpression = cronExpression.toUpperCase(Locale.US);
        	buildExpression(this.cronExpression);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
    
    /**
     * Returns the next date/time <I>after</I> the given date/time which
     * satisfies the cron expression.
     * 
     * @param date the date/time at which to begin the search for the next valid
     *             date/time
     * @return the next valid date/time
     */
    public Date getNextValidTimeAfter(Date date) {
        return getTimeAfter(date);
    }
    
    /**
     * Returns the time zone for which this <code>CronExpression</code> 
     * will be resolved.
     */
    public TimeZone getTimeZone() {
    	
        return timeZone;
    }

    /**
     * Returns the string representation of the <CODE>CronExpression</CODE>
     * 
     * @return a string representation of the <CODE>CronExpression</CODE>
     */
    @Override
    public String toString() {
        return cronExpression;
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // Expression Parsing Functions
    //
    ////////////////////////////////////////////////////////////////////////////

    protected void buildExpression(String expression) throws ParseException {
        expressionParsed = true;

        try {

            if (seconds == null) {
                seconds = new TreeSet<Integer>();
            }
            if (minutes == null) {
                minutes = new TreeSet<Integer>();
            }
            if (hours == null) {
                hours = new TreeSet<Integer>();
            }
            if (daysOfMonth == null) {
                daysOfMonth = new TreeSet<Integer>();
            }
            if (months == null) {
                months = new TreeSet<Integer>();
            }
            if (daysOfWeek == null) {
                daysOfWeek = new TreeSet<Integer>();
            }
            if (years == null) {
                years = new TreeSet<Integer>();
            }

            int exprOn = SECOND;

            StringTokenizer exprsTok = new StringTokenizer(expression, " \t",
                    false);

            while (exprsTok.hasMoreTokens() && exprOn <= YEAR) {
                String expr = exprsTok.nextToken().trim();

                // throw an exception if L is used with other days of the month
                if(exprOn == DAY_OF_MONTH && expr.indexOf('L') != -1 && expr.length() > 1 && expr.indexOf(",") >= 0) {
                    throw new ParseException("Support for specifying 'L' and 'LW' with other days of the month is not implemented", -1);
                }
                // throw an exception if L is used with other days of the week
                if(exprOn == DAY_OF_WEEK && expr.indexOf('L') != -1 && expr.length() > 1  && expr.indexOf(",") >= 0) {
                    throw new ParseException("Support for specifying 'L' with other days of the week is not implemented", -1);
                }
                if(exprOn == DAY_OF_WEEK && expr.indexOf('#') != -1 && expr.indexOf('#', expr.indexOf('#') +1) != -1) {
                    throw new ParseException("Support for specifying multiple \"nth\" days is not imlemented.", -1);
                }
                
                StringTokenizer vTok = new StringTokenizer(expr, ",");
                while (vTok.hasMoreTokens()) {
                    String v = vTok.nextToken();
                    storeExpressionVals(0, v, exprOn);
                }

                exprOn++;
            }

            if (exprOn <= DAY_OF_WEEK) {
                throw new ParseException("Unexpected end of expression.",
                            expression.length());
            }

            if (exprOn <= YEAR) {
                storeExpressionVals(0, "*", YEAR);
            }

            TreeSet<Integer> dow = getSet(DAY_OF_WEEK);
            TreeSet<Integer> dom = getSet(DAY_OF_MONTH);

            // Copying the logic from the UnsupportedOperationException below
            boolean dayOfMSpec = !dom.contains(NO_SPEC);
            boolean dayOfWSpec = !dow.contains(NO_SPEC);

            if (dayOfMSpec && !dayOfWSpec) { 
                // skip
            } else if (dayOfWSpec && !dayOfMSpec) { 
                // skip
            } else {
                throw new ParseException(
                        "Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.", 0);
            }
        } catch (ParseException pe) {
            throw pe;
        } catch (Exception e) {
            throw new ParseException("Illegal cron expression format ("
                    + e.toString() + ")", 0);
        }
    }

    protected int storeExpressionVals(int pos, String s, int type)
        throws ParseException {

        int incr = 0;
        int i = skipWhiteSpace(pos, s);
        if (i >= s.length()) {
            return i;
        }
        char c = s.charAt(i);
        if ((c >= 'A') && (c <= 'Z') && (!s.equals("L")) && (!s.equals("LW")) && (!s.matches("^L-[0-9]*[W]?"))) {
            String sub = s.substring(i, i + 3);
            int sval = -1;
            int eval = -1;
            if (type == MONTH) {
                sval = getMonthNumber(sub) + 1;
                if (sval <= 0) {
                    throw new ParseException("Invalid Month value: '" + sub + "'", i);
                }
                if (s.length() > i + 3) {
                    c = s.charAt(i + 3);
                    if (c == '-') {
                        i += 4;
                        sub = s.substring(i, i + 3);
                        eval = getMonthNumber(sub) + 1;
                        if (eval <= 0) {
                            throw new ParseException("Invalid Month value: '" + sub + "'", i);
                        }
                    }
                }
            } else if (type == DAY_OF_WEEK) {
                sval = getDayOfWeekNumber(sub);
                if (sval < 0) {
                    throw new ParseException("Invalid Day-of-Week value: '"
                                + sub + "'", i);
                }
                if (s.length() > i + 3) {
                    c = s.charAt(i + 3);
                    if (c == '-') {
                        i += 4;
                        sub = s.substring(i, i + 3);
                        eval = getDayOfWeekNumber(sub);
                        if (eval < 0) {
                            throw new ParseException(
                                    "Invalid Day-of-Week value: '" + sub
                                        + "'", i);
                        }
                    } else if (c == '#') {
                        try {
                            i += 4;
                            nthdayOfWeek = Integer.parseInt(s.substring(i));
                            if (nthdayOfWeek < 1 || nthdayOfWeek > 5) {
                                throw new Exception();
                            }
                        } catch (Exception e) {
                            throw new ParseException(
                                    "A numeric value between 1 and 5 must follow the '#' option",
                                    i);
                        }
                    } else if (c == 'L') {
                        lastdayOfWeek = true;
                        i++;
                    }
                }

            } else {
                throw new ParseException(
                        "Illegal characters for this position: '" + sub + "'",
                        i);
            }
            if (eval != -1) {
                incr = 1;
            }
            addToSet(sval, eval, incr, type);
            return (i + 3);
        }

        if (c == '?') {
            i++;
            if ((i + 1) < s.length() 
                    && (s.charAt(i) != ' ' && s.charAt(i + 1) != '\t')) {
                throw new ParseException("Illegal character after '?': "
                            + s.charAt(i), i);
            }
            if (type != DAY_OF_WEEK && type != DAY_OF_MONTH) {
                throw new ParseException(
                            "'?' can only be specfied for Day-of-Month or Day-of-Week.",
                            i);
            }
            if (type == DAY_OF_WEEK && !lastdayOfMonth) {
                int val = ((Integer) daysOfMonth.last()).intValue();
                if (val == NO_SPEC_INT) {
                    throw new ParseException(
                                "'?' can only be specfied for Day-of-Month -OR- Day-of-Week.",
                                i);
                }
            }

            addToSet(NO_SPEC_INT, -1, 0, type);
            return i;
        }

        if (c == '*' || c == '/') {
            if (c == '*' && (i + 1) >= s.length()) {
                addToSet(ALL_SPEC_INT, -1, incr, type);
                return i + 1;
            } else if (c == '/'
                    && ((i + 1) >= s.length() || s.charAt(i + 1) == ' ' || s
                            .charAt(i + 1) == '\t')) { 
                throw new ParseException("'/' must be followed by an integer.", i);
            } else if (c == '*') {
                i++;
            }
            c = s.charAt(i);
            if (c == '/') { // is an increment specified?
                i++;
                if (i >= s.length()) {
                    throw new ParseException("Unexpected end of string.", i);
                }

                incr = getNumericValue(s, i);

                i++;
                if (incr > 10) {
                    i++;
                }
                if (incr > 59 && (type == SECOND || type == MINUTE)) {
                    throw new ParseException("Increment > 60 : " + incr, i);
                } else if (incr > 23 && (type == HOUR)) { 
                    throw new ParseException("Increment > 24 : " + incr, i);
                } else if (incr > 31 && (type == DAY_OF_MONTH)) { 
                    throw new ParseException("Increment > 31 : " + incr, i);
                } else if (incr > 7 && (type == DAY_OF_WEEK)) { 
                    throw new ParseException("Increment > 7 : " + incr, i);
                } else if (incr > 12 && (type == MONTH)) {
                    throw new ParseException("Increment > 12 : " + incr, i);
                }
            } else {
                incr = 1;
            }

            addToSet(ALL_SPEC_INT, -1, incr, type);
            return i;
        } else if (c == 'L') {
            i++;
            if (type == DAY_OF_MONTH) {
                lastdayOfMonth = true;
            }
            if (type == DAY_OF_WEEK) {
                addToSet(7, 7, 0, type);
            }
            if(type == DAY_OF_MONTH && s.length() > i) {
                c = s.charAt(i);
                if(c == '-') {
                    ValueSet vs = getValue(0, s, i+1);
                    lastdayOffset = vs.value;
                    if(lastdayOffset > 30)
                        throw new ParseException("Offset from last day must be <= 30", i+1);
                    i = vs.pos;
                }                        
                if(s.length() > i) {
                    c = s.charAt(i);
                    if(c == 'W') {
                        nearestWeekday = true;
                        i++;
                    }
                }
            }
            return i;
        } else if (c >= '0' && c <= '9') {
            int val = Integer.parseInt(String.valueOf(c));
            i++;
            if (i >= s.length()) {
                addToSet(val, -1, -1, type);
            } else {
                c = s.charAt(i);
                if (c >= '0' && c <= '9') {
                    ValueSet vs = getValue(val, s, i);
                    val = vs.value;
                    i = vs.pos;
                }
                i = checkNext(i, s, val, type);
                return i;
            }
        } else {
            throw new ParseException("Unexpected character: " + c, i);
        }

        return i;
    }

    protected int checkNext(int pos, String s, int val, int type)
        throws ParseException {
        
        int end = -1;
        int i = pos;

        if (i >= s.length()) {
            addToSet(val, end, -1, type);
            return i;
        }

        char c = s.charAt(pos);

        if (c == 'L') {
            if (type == DAY_OF_WEEK) {
                if(val < 1 || val > 7)
                    throw new ParseException("Day-of-Week values must be between 1 and 7", -1);
                lastdayOfWeek = true;
            } else {
                throw new ParseException("'L' option is not valid here. (pos=" + i + ")", i);
            }
            TreeSet<Integer> set = getSet(type);
            set.add(Integer.valueOf(val));
            i++;
            return i;
        }
        
        if (c == 'W') {
            if (type == DAY_OF_MONTH) {
                nearestWeekday = true;
            } else {
                throw new ParseException("'W' option is not valid here. (pos=" + i + ")", i);
            }
            if(val > 31)
                throw new ParseException("The 'W' option does not make sense with values larger than 31 (max number of days in a month)", i); 
            TreeSet<Integer> set = getSet(type);
            set.add(Integer.valueOf(val));
            i++;
            return i;
        }

        if (c == '#') {
            if (type != DAY_OF_WEEK) {
                throw new ParseException("'#' option is not valid here. (pos=" + i + ")", i);
            }
            i++;
            try {
                nthdayOfWeek = Integer.parseInt(s.substring(i));
                if (nthdayOfWeek < 1 || nthdayOfWeek > 5) {
                    throw new Exception();
                }
            } catch (Exception e) {
                throw new ParseException(
                        "A numeric value between 1 and 5 must follow the '#' option",
                        i);
            }

            TreeSet<Integer> set = getSet(type);
            set.add(Integer.valueOf(val));
            i++;
            return i;
        }

        if (c == '-') {
            i++;
            c = s.charAt(i);
            int v = Integer.parseInt(String.valueOf(c));
            end = v;
            i++;
            if (i >= s.length()) {
                addToSet(val, end, 1, type);
                return i;
            }
            c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                ValueSet vs = getValue(v, s, i);
                int v1 = vs.value;
                end = v1;
                i = vs.pos;
            }
            if (i < s.length() && ((c = s.charAt(i)) == '/')) {
                i++;
                c = s.charAt(i);
                int v2 = Integer.parseInt(String.valueOf(c));
                i++;
                if (i >= s.length()) {
                    addToSet(val, end, v2, type);
                    return i;
                }
                c = s.charAt(i);
                if (c >= '0' && c <= '9') {
                    ValueSet vs = getValue(v2, s, i);
                    int v3 = vs.value;
                    addToSet(val, end, v3, type);
                    i = vs.pos;
                    return i;
                } else {
                    addToSet(val, end, v2, type);
                    return i;
                }
            } else {
                addToSet(val, end, 1, type);
                return i;
            }
        }

        if (c == '/') {
            i++;
            c = s.charAt(i);
            int v2 = Integer.parseInt(String.valueOf(c));
            i++;
            if (i >= s.length()) {
                addToSet(val, end, v2, type);
                return i;
            }
            c = s.charAt(i);
            if (c >= '0' && c <= '9') {
                ValueSet vs = getValue(v2, s, i);
                int v3 = vs.value;
                addToSet(val, end, v3, type);
                i = vs.pos;
                return i;
            } else {
                throw new ParseException("Unexpected character '" + c + "' after '/'", i);
            }
        }

        addToSet(val, end, 0, type);
        i++;
        return i;
    }

    public String getCronExpression() {
        return cronExpression;
    }
    
    public String getExpressionSummary() {
        StringBuilder buf = new StringBuilder(256);

        buf.append("seconds: ");
        buf.append(getExpressionSetSummary(seconds));
        buf.append("\n");
        buf.append("minutes: ");
        buf.append(getExpressionSetSummary(minutes));
        buf.append("\n");
        buf.append("hours: ");
        buf.append(getExpressionSetSummary(hours));
        buf.append("\n");
        buf.append("daysOfMonth: ");
        buf.append(getExpressionSetSummary(daysOfMonth));
        buf.append("\n");
        buf.append("months: ");
        buf.append(getExpressionSetSummary(months));
        buf.append("\n");
        buf.append("daysOfWeek: ");
        buf.append(getExpressionSetSummary(daysOfWeek));
        buf.append("\n");
        buf.append("lastdayOfWeek: ");
        buf.append(lastdayOfWeek);
        buf.append("\n");
        buf.append("nearestWeekday: ");
        buf.append(nearestWeekday);
        buf.append("\n");
        buf.append("NthDayOfWeek: ");
        buf.append(nthdayOfWeek);
        buf.append("\n");
        buf.append("lastdayOfMonth: ");
        buf.append(lastdayOfMonth);
        buf.append("\n");
        buf.append("years: ");
        buf.append(getExpressionSetSummary(years));
        buf.append("\n");

        return buf.toString();
    }

    protected String getExpressionSetSummary(java.util.Set<Integer> set) {

        if (set.contains(NO_SPEC)) {
            return "?";
        }
        if (set.contains(ALL_SPEC)) {
            return "*";
        }

        StringBuffer buf = new StringBuffer();

        Iterator<Integer> itr = set.iterator();
        boolean first = true;
        while (itr.hasNext()) {
            Integer iVal = itr.next();
            String val = iVal.toString();
            if (!first) {
                buf.append(",");
            }
            buf.append(val);
            first = false;
        }

        return buf.toString();
    }

    protected String getExpressionSetSummary(java.util.ArrayList<Integer> list) {

        if (list.contains(NO_SPEC)) {
            return "?";
        }
        if (list.contains(ALL_SPEC)) {
            return "*";
        }

        StringBuffer buf = new StringBuffer();

        Iterator<Integer> itr = list.iterator();
        boolean first = true;
        while (itr.hasNext()) {
            Integer iVal = itr.next();
            String val = iVal.toString();
            if (!first) {
                buf.append(",");
            }
            buf.append(val);
            first = false;
        }

        return buf.toString();
    }

    protected int skipWhiteSpace(int i, String s) {
        for (; i < s.length() && (s.charAt(i) == ' ' || s.charAt(i) == '\t'); i++) {
            ;
        }

        return i;
    }

    protected int findNextWhiteSpace(int i, String s) {
        for (; i < s.length() && (s.charAt(i) != ' ' || s.charAt(i) != '\t'); i++) {
            ;
        }

        return i;
    }

    protected void addToSet(int val, int end, int incr, int type)
        throws ParseException {
        
        TreeSet<Integer> set = getSet(type);

        if (type == SECOND || type == MINUTE) {
            if ((val < 0 || val > 59 || end > 59) && (val != ALL_SPEC_INT)) {
                throw new ParseException(
                        "Minute and Second values must be between 0 and 59",
                        -1);
            }
        } else if (type == HOUR) {
            if ((val < 0 || val > 23 || end > 23) && (val != ALL_SPEC_INT)) {
                throw new ParseException(
                        "Hour values must be between 0 and 23", -1);
            }
        } else if (type == DAY_OF_MONTH) {
            if ((val < 1 || val > 31 || end > 31) && (val != ALL_SPEC_INT) 
                    && (val != NO_SPEC_INT)) {
                throw new ParseException(
                        "Day of month values must be between 1 and 31", -1);
            }
        } else if (type == MONTH) {
            if ((val < 1 || val > 12 || end > 12) && (val != ALL_SPEC_INT)) {
                throw new ParseException(
                        "Month values must be between 1 and 12", -1);
            }
        } else if (type == DAY_OF_WEEK) {
            if ((val == 0 || val > 7 || end > 7) && (val != ALL_SPEC_INT)
                    && (val != NO_SPEC_INT)) {
                throw new ParseException(
                        "Day-of-Week values must be between 1 and 7", -1);
            }
        }

        if ((incr == 0 || incr == -1) && val != ALL_SPEC_INT) {
            if (val != -1) {
                set.add(Integer.valueOf(val));
            } else {
                set.add(NO_SPEC);
            }
            
            return;
        }

        int startAt = val;
        int stopAt = end;

        if (val == ALL_SPEC_INT && incr <= 0) {
            incr = 1;
            set.add(ALL_SPEC); // put in a marker, but also fill values
        }

        if (type == SECOND || type == MINUTE) {
            if (stopAt == -1) {
                stopAt = 59;
            }
            if (startAt == -1 || startAt == ALL_SPEC_INT) {
                startAt = 0;
            }
        } else if (type == HOUR) {
            if (stopAt == -1) {
                stopAt = 23;
            }
            if (startAt == -1 || startAt == ALL_SPEC_INT) {
                startAt = 0;
            }
        } else if (type == DAY_OF_MONTH) {
            if (stopAt == -1) {
                stopAt = 31;
            }
            if (startAt == -1 || startAt == ALL_SPEC_INT) {
                startAt = 1;
            }
        } else if (type == MONTH) {
            if (stopAt == -1) {
                stopAt = 12;
            }
            if (startAt == -1 || startAt == ALL_SPEC_INT) {
                startAt = 1;
            }
        } else if (type == DAY_OF_WEEK) {
            if (stopAt == -1) {
                stopAt = 7;
            }
            if (startAt == -1 || startAt == ALL_SPEC_INT) {
                startAt = 1;
            }
        } else if (type == YEAR) {
            if (stopAt == -1) {
                stopAt = MAX_YEAR;
            }
            if (startAt == -1 || startAt == ALL_SPEC_INT) {
                startAt = 1970;
            }
        }

        // if the end of the range is before the start, then we need to overflow into 
        // the next day, month etc. This is done by adding the maximum amount for that 
        // type, and using modulus max to determine the value being added.
        int max = -1;
        if (stopAt < startAt) {
            switch (type) {
              case       SECOND : max = 60; break;
              case       MINUTE : max = 60; break;
              case         HOUR : max = 24; break;
              case        MONTH : max = 12; break;
              case  DAY_OF_WEEK : max = 7;  break;
              case DAY_OF_MONTH : max = 31; break;
              case         YEAR : throw new IllegalArgumentException("Start year must be less than stop year");
              default           : throw new IllegalArgumentException("Unexpected type encountered");
            }
            stopAt += max;
        }

        for (int i = startAt; i <= stopAt; i += incr) {
            if (max == -1) {
                // ie: there's no max to overflow over
                set.add(Integer.valueOf(i));
            } else {
                // take the modulus to get the real value
                int i2 = i % max;

                // 1-indexed ranges should not include 0, and should include their max
                if (i2 == 0 && (type == MONTH || type == DAY_OF_WEEK || type == DAY_OF_MONTH) ) {
                    i2 = max;
                }

                set.add(Integer.valueOf(i2));
            }
        }
    }

    protected TreeSet<Integer> getSet(int type) {
        switch (type) {
            case SECOND:
                return seconds;
            case MINUTE:
                return minutes;
            case HOUR:
                return hours;
            case DAY_OF_MONTH:
                return daysOfMonth;
            case MONTH:
                return months;
            case DAY_OF_WEEK:
                return daysOfWeek;
            case YEAR:
                return years;
            default:
                return null;
        }
    }

    protected ValueSet getValue(int v, String s, int i) {
        char c = s.charAt(i);
        StringBuilder s1 = new StringBuilder(String.valueOf(v));
        while (c >= '0' && c <= '9') {
            s1.append(c);
            i++;
            if (i >= s.length()) {
                break;
            }
            c = s.charAt(i);
        }
        ValueSet val = new ValueSet();
        
        val.pos = (i < s.length()) ? i : i + 1;
        val.value = Integer.parseInt(s1.toString());
        return val;
    }

    protected int getNumericValue(String s, int i) {
        int endOfVal = findNextWhiteSpace(i, s);
        String val = s.substring(i, endOfVal);
        return Integer.parseInt(val);
    }

    protected int getMonthNumber(String s) {
        Integer integer = (Integer) monthMap.get(s);

        if (integer == null) {
            return -1;
        }

        return integer.intValue();
    }

    protected int getDayOfWeekNumber(String s) {
        Integer integer = (Integer) dayMap.get(s);

        if (integer == null) {
            return -1;
        }

        return integer.intValue();
    }

    ////////////////////////////////////////////////////////////////////////////
    //
    // Computation Functions
    //
    ////////////////////////////////////////////////////////////////////////////

    public Date getTimeAfter(Date afterTime) {

        // Computation is based on Gregorian year only.
        Calendar cl = new java.util.GregorianCalendar(getTimeZone()); 

        // move ahead one second, since we're computing the time *after* the
        // given time
        afterTime = new Date(afterTime.getTime() + 1000);
        // CronTrigger does not deal with milliseconds
        cl.setTime(afterTime);
        cl.set(Calendar.MILLISECOND, 0);

        boolean gotOne = false;
        // loop until we've computed the next time, or we've past the endTime
        while (!gotOne) {

            //if (endTime != null && cl.getTime().after(endTime)) return null;
            if(cl.get(Calendar.YEAR) > 2999) { // prevent endless loop...
                return null;
            }

            SortedSet<Integer> st = null;
            int t = 0;

            int sec = cl.get(Calendar.SECOND);
            int min = cl.get(Calendar.MINUTE);

            // get second.................................................
            st = seconds.tailSet(Integer.valueOf(sec));
            if (st != null && st.size() != 0) {
                sec = st.first().intValue();
            } else {
                sec = seconds.first().intValue();
                min++;
                cl.set(Calendar.MINUTE, min);
            }
            cl.set(Calendar.SECOND, sec);

            min = cl.get(Calendar.MINUTE);
            int hr = cl.get(Calendar.HOUR_OF_DAY);
            t = -1;

            // get minute.................................................
            st = minutes.tailSet(Integer.valueOf(min));
            if (st != null && st.size() != 0) {
                t = min;
                min = st.first().intValue();
            } else {
                min = minutes.first().intValue();
                hr++;
            }
            if (min != t) {
                cl.set(Calendar.SECOND, 0);
                cl.set(Calendar.MINUTE, min);
                setCalendarHour(cl, hr);
                continue;
            }
            cl.set(Calendar.MINUTE, min);

            hr = cl.get(Calendar.HOUR_OF_DAY);
            int day = cl.get(Calendar.DAY_OF_MONTH);
            t = -1;

            // get hour...................................................
            st = hours.tailSet(Integer.valueOf(hr));
            if (st != null && st.size() != 0) {
                t = hr;
                hr = st.first().intValue();
            } else {
                hr = hours.first().intValue();
                day++;
            }
            if (hr != t) {
                cl.set(Calendar.SECOND, 0);
                cl.set(Calendar.MINUTE, 0);
                cl.set(Calendar.DAY_OF_MONTH, day);
                setCalendarHour(cl, hr);
                continue;
            }
            cl.set(Calendar.HOUR_OF_DAY, hr);

            day = cl.get(Calendar.DAY_OF_MONTH);
            int mon = cl.get(Calendar.MONTH) + 1;
            // '+ 1' because calendar is 0-based for this field, and we are
            // 1-based
            t = -1;
            int tmon = mon;
            
            // get day...................................................
            boolean dayOfMSpec = !daysOfMonth.contains(NO_SPEC);
            boolean dayOfWSpec = !daysOfWeek.contains(NO_SPEC);
            if (dayOfMSpec && !dayOfWSpec) { // get day by day of month rule
                st = daysOfMonth.tailSet(Integer.valueOf(day));
                if (lastdayOfMonth) {
                    if(!nearestWeekday) {
                        t = day;
                        day = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                        day -= lastdayOffset;
                        if(t > day) {
                        	mon++;
                        	if(mon > 12) { 
                        		mon = 1;
                        		tmon = 3333; // ensure test of mon != tmon further below fails
                        		cl.add(Calendar.YEAR, 1);
                        	}
                            day = 1;
                        }
                    } else {
                        t = day;
                        day = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                        day -= lastdayOffset;
                        
                        java.util.Calendar tcal = java.util.Calendar.getInstance(getTimeZone());
                        tcal.set(Calendar.SECOND, 0);
                        tcal.set(Calendar.MINUTE, 0);
                        tcal.set(Calendar.HOUR_OF_DAY, 0);
                        tcal.set(Calendar.DAY_OF_MONTH, day);
                        tcal.set(Calendar.MONTH, mon - 1);
                        tcal.set(Calendar.YEAR, cl.get(Calendar.YEAR));
                        
                        int ldom = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                        int dow = tcal.get(Calendar.DAY_OF_WEEK);

                        if(dow == Calendar.SATURDAY && day == 1) {
                            day += 2;
                        } else if(dow == Calendar.SATURDAY) {
                            day -= 1;
                        } else if(dow == Calendar.SUNDAY && day == ldom) { 
                            day -= 2;
                        } else if(dow == Calendar.SUNDAY) { 
                            day += 1;
                        }
                    
                        tcal.set(Calendar.SECOND, sec);
                        tcal.set(Calendar.MINUTE, min);
                        tcal.set(Calendar.HOUR_OF_DAY, hr);
                        tcal.set(Calendar.DAY_OF_MONTH, day);
                        tcal.set(Calendar.MONTH, mon - 1);
                        Date nTime = tcal.getTime();
                        if(nTime.before(afterTime)) {
                            day = 1;
                            mon++;
                        }
                    }
                } else if(nearestWeekday) {
                    t = day;
                    day = ((Integer) daysOfMonth.first()).intValue();

                    java.util.Calendar tcal = java.util.Calendar.getInstance(getTimeZone());
                    tcal.set(Calendar.SECOND, 0);
                    tcal.set(Calendar.MINUTE, 0);
                    tcal.set(Calendar.HOUR_OF_DAY, 0);
                    tcal.set(Calendar.DAY_OF_MONTH, day);
                    tcal.set(Calendar.MONTH, mon - 1);
                    tcal.set(Calendar.YEAR, cl.get(Calendar.YEAR));
                    
                    int ldom = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                    int dow = tcal.get(Calendar.DAY_OF_WEEK);

                    if(dow == Calendar.SATURDAY && day == 1) {
                        day += 2;
                    } else if(dow == Calendar.SATURDAY) {
                        day -= 1;
                    } else if(dow == Calendar.SUNDAY && day == ldom) { 
                        day -= 2;
                    } else if(dow == Calendar.SUNDAY) { 
                        day += 1;
                    }
                        
                
                    tcal.set(Calendar.SECOND, sec);
                    tcal.set(Calendar.MINUTE, min);
                    tcal.set(Calendar.HOUR_OF_DAY, hr);
                    tcal.set(Calendar.DAY_OF_MONTH, day);
                    tcal.set(Calendar.MONTH, mon - 1);
                    Date nTime = tcal.getTime();
                    if(nTime.before(afterTime)) {
                        day = ((Integer) daysOfMonth.first()).intValue();
                        mon++;
                    }
                } else if (st != null && st.size() != 0) {
                    t = day;
                    day = st.first().intValue();
                    // make sure we don't over-run a short month, such as february
                    int lastDay = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                    if (day > lastDay) {
                        day = ((Integer) daysOfMonth.first()).intValue();
                        mon++;
                    }
                } else {
                    day = ((Integer) daysOfMonth.first()).intValue();
                    mon++;
                }
                
                if (day != t || mon != tmon) {
                    cl.set(Calendar.SECOND, 0);
                    cl.set(Calendar.MINUTE, 0);
                    cl.set(Calendar.HOUR_OF_DAY, 0);
                    cl.set(Calendar.DAY_OF_MONTH, day);
                    cl.set(Calendar.MONTH, mon - 1);
                    // '- 1' because calendar is 0-based for this field, and we
                    // are 1-based
                    continue;
                }
            } else if (dayOfWSpec && !dayOfMSpec) { // get day by day of week rule
                if (lastdayOfWeek) { // are we looking for the last xxx day of
                    // the month?
                    int dow = ((Integer) daysOfWeek.first()).intValue(); // desired
                    // d-o-w
                    int cDow = cl.get(Calendar.DAY_OF_WEEK); // current d-o-w
                    int daysToAdd = 0;
                    if (cDow < dow) {
                        daysToAdd = dow - cDow;
                    }
                    if (cDow > dow) {
                        daysToAdd = dow + (7 - cDow);
                    }

                    int lDay = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));

                    if (day + daysToAdd > lDay) { // did we already miss the
                        // last one?
                        cl.set(Calendar.SECOND, 0);
                        cl.set(Calendar.MINUTE, 0);
                        cl.set(Calendar.HOUR_OF_DAY, 0);
                        cl.set(Calendar.DAY_OF_MONTH, 1);
                        cl.set(Calendar.MONTH, mon);
                        // no '- 1' here because we are promoting the month
                        continue;
                    }

                    // find date of last occurrence of this day in this month...
                    while ((day + daysToAdd + 7) <= lDay) {
                        daysToAdd += 7;
                    }

                    day += daysToAdd;

                    if (daysToAdd > 0) {
                        cl.set(Calendar.SECOND, 0);
                        cl.set(Calendar.MINUTE, 0);
                        cl.set(Calendar.HOUR_OF_DAY, 0);
                        cl.set(Calendar.DAY_OF_MONTH, day);
                        cl.set(Calendar.MONTH, mon - 1);
                        // '- 1' here because we are not promoting the month
                        continue;
                    }

                } else if (nthdayOfWeek != 0) {
                    // are we looking for the Nth xxx day in the month?
                    int dow = ((Integer) daysOfWeek.first()).intValue(); // desired
                    // d-o-w
                    int cDow = cl.get(Calendar.DAY_OF_WEEK); // current d-o-w
                    int daysToAdd = 0;
                    if (cDow < dow) {
                        daysToAdd = dow - cDow;
                    } else if (cDow > dow) {
                        daysToAdd = dow + (7 - cDow);
                    }

                    boolean dayShifted = false;
                    if (daysToAdd > 0) {
                        dayShifted = true;
                    }

                    day += daysToAdd;
                    int weekOfMonth = day / 7;
                    if (day % 7 > 0) {
                        weekOfMonth++;
                    }

                    daysToAdd = (nthdayOfWeek - weekOfMonth) * 7;
                    day += daysToAdd;
                    if (daysToAdd < 0
                            || day > getLastDayOfMonth(mon, cl
                                    .get(Calendar.YEAR))) {
                        cl.set(Calendar.SECOND, 0);
                        cl.set(Calendar.MINUTE, 0);
                        cl.set(Calendar.HOUR_OF_DAY, 0);
                        cl.set(Calendar.DAY_OF_MONTH, 1);
                        cl.set(Calendar.MONTH, mon);
                        // no '- 1' here because we are promoting the month
                        continue;
                    } else if (daysToAdd > 0 || dayShifted) {
                        cl.set(Calendar.SECOND, 0);
                        cl.set(Calendar.MINUTE, 0);
                        cl.set(Calendar.HOUR_OF_DAY, 0);
                        cl.set(Calendar.DAY_OF_MONTH, day);
                        cl.set(Calendar.MONTH, mon - 1);
                        // '- 1' here because we are NOT promoting the month
                        continue;
                    }
                } else {
                    int cDow = cl.get(Calendar.DAY_OF_WEEK); // current d-o-w
                    int dow = ((Integer) daysOfWeek.first()).intValue(); // desired
                    // d-o-w
                    st = daysOfWeek.tailSet(Integer.valueOf(cDow));
                    if (st != null && st.size() > 0) {
                        dow = st.first().intValue();
                    }

                    int daysToAdd = 0;
                    if (cDow < dow) {
                        daysToAdd = dow - cDow;
                    }
                    if (cDow > dow) {
                        daysToAdd = dow + (7 - cDow);
                    }

                    int lDay = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));

                    if (day + daysToAdd > lDay) { // will we pass the end of
                        // the month?
                        cl.set(Calendar.SECOND, 0);
                        cl.set(Calendar.MINUTE, 0);
                        cl.set(Calendar.HOUR_OF_DAY, 0);
                        cl.set(Calendar.DAY_OF_MONTH, 1);
                        cl.set(Calendar.MONTH, mon);
                        // no '- 1' here because we are promoting the month
                        continue;
                    } else if (daysToAdd > 0) { // are we swithing days?
                        cl.set(Calendar.SECOND, 0);
                        cl.set(Calendar.MINUTE, 0);
                        cl.set(Calendar.HOUR_OF_DAY, 0);
                        cl.set(Calendar.DAY_OF_MONTH, day + daysToAdd);
                        cl.set(Calendar.MONTH, mon - 1);
                        // '- 1' because calendar is 0-based for this field,
                        // and we are 1-based
                        continue;
                    }
                }
            } else { // dayOfWSpec && !dayOfMSpec
                throw new UnsupportedOperationException(
                        "Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.");
            }
            cl.set(Calendar.DAY_OF_MONTH, day);

            mon = cl.get(Calendar.MONTH) + 1;
            // '+ 1' because calendar is 0-based for this field, and we are
            // 1-based
            int year = cl.get(Calendar.YEAR);
            t = -1;

            // test for expressions that never generate a valid fire date,
            // but keep looping...
            if (year > MAX_YEAR) {
                return null;
            }

            // get month...................................................
            st = months.tailSet(Integer.valueOf(mon));
            if (st != null && st.size() != 0) {
                t = mon;
                mon = st.first().intValue();
            } else {
                mon = months.first().intValue();
                year++;
            }
            if (mon != t) {
                cl.set(Calendar.SECOND, 0);
                cl.set(Calendar.MINUTE, 0);
                cl.set(Calendar.HOUR_OF_DAY, 0);
                cl.set(Calendar.DAY_OF_MONTH, 1);
                cl.set(Calendar.MONTH, mon - 1);
                // '- 1' because calendar is 0-based for this field, and we are
                // 1-based
                cl.set(Calendar.YEAR, year);
                continue;
            }
            cl.set(Calendar.MONTH, mon - 1);
            // '- 1' because calendar is 0-based for this field, and we are
            // 1-based

            year = cl.get(Calendar.YEAR);
            t = -1;

            // get year...................................................
            st = years.tailSet(Integer.valueOf(year));
            if (st != null && st.size() != 0) {
                t = year;
                year = st.first().intValue();
            } else {
                return null; // ran out of years...
            }

            if (year != t) {
                cl.set(Calendar.SECOND, 0);
                cl.set(Calendar.MINUTE, 0);
                cl.set(Calendar.HOUR_OF_DAY, 0);
                cl.set(Calendar.DAY_OF_MONTH, 1);
                cl.set(Calendar.MONTH, 0);
                // '- 1' because calendar is 0-based for this field, and we are
                // 1-based
                cl.set(Calendar.YEAR, year);
                continue;
            }
            cl.set(Calendar.YEAR, year);

            gotOne = true;
        } // while( !done )

        return cl.getTime();
    }

    /**
     * Advance the calendar to the particular hour paying particular attention
     * to daylight saving problems.
     * 
     * @param cal
     * @param hour
     */
    protected void setCalendarHour(Calendar cal, int hour) {
        cal.set(java.util.Calendar.HOUR_OF_DAY, hour);
        if (cal.get(java.util.Calendar.HOUR_OF_DAY) != hour && hour != 24) {
            cal.set(java.util.Calendar.HOUR_OF_DAY, hour + 1);
        }
    }

    /**
     * NOT YET IMPLEMENTED: Returns the time before the given time
     * that the <code>CronExpression</code> matches.
     */ 
    public Date getTimeBefore(Date endTime) {
        //采用公历
        Calendar cl = new java.util.GregorianCalendar(getTimeZone());

		/*
         * 往前推一秒，cron最小精度为秒，可能碰巧当前时间就是job触发的时间，所有往前推一秒获取前一次触发的时间
		 */
        endTime = new Date(endTime.getTime() - 1000);

		/*
		 * cron表达式不处理毫秒
		 */
        cl.setTime(endTime);
        cl.set(Calendar.MILLISECOND, 0);

        //是否找到前一个触发时间
        boolean gotOne = false;

        //直到找到前一个触发时间为止
        while (!gotOne) {

            //不能小于1970，date的getime距离1970的毫米数
            if (cl.get(Calendar.YEAR) < 1970) {
                return null;
            }

            SortedSet<Integer> st = null;
            int t = 0;

            //时间中的秒数
            int sec = cl.get(Calendar.SECOND);
            //时间中的分钟数
            int min = cl.get(Calendar.MINUTE);

            //获取前一个触发点的秒..................................................................................................
            st = seconds.headSet(sec, true);

            if (st != null && st.size() != 0) {
                sec = st.last();
            } else {
                sec = seconds.last();
                min--;
                cl.set(Calendar.MINUTE, min);
            }

            cl.set(Calendar.SECOND, sec);

            min = cl.get(Calendar.MINUTE);
            //前一次触发点的小时
            int hr = cl.get(Calendar.HOUR_OF_DAY);
            t = -1;

            // 获取前一次触发的分钟数..................................................................................................
            st = minutes.headSet(min, true);

            if (st != null && st.size() != 0) {
                t = min;
                min = st.last();
            } else {
                min = minutes.last();
                hr--;
            }

            if (min != t) {
                cl.set(Calendar.SECOND, 59);
                cl.set(Calendar.MINUTE, min);
                setCalendarHour(cl, hr);
                continue;
            }
            cl.set(Calendar.MINUTE, min);

            hr = cl.get(Calendar.HOUR_OF_DAY);
            //前一次触发的几号
            int day = cl.get(Calendar.DAY_OF_MONTH);
            t = -1;

            // 获取前一次触发的小时....................................................................................................
            st = hours.headSet(hr, true);
            if (st != null && st.size() != 0) {
                t = hr;
                hr = st.last();
            } else {
                hr = hours.last();
                day--;
            }
            if (hr != t) {
                cl.set(Calendar.SECOND, 59);
                cl.set(Calendar.MINUTE, 59);
                cl.set(Calendar.DAY_OF_MONTH, day);
                setCalendarHour(cl, hr);
                continue;
            }

            cl.set(Calendar.HOUR_OF_DAY, hr);
            day = cl.get(Calendar.DAY_OF_MONTH);

            //Calendar的月份从0开始的，quantz的cron表达式计算是从0开始的
            int mon = cl.get(Calendar.MONTH) + 1;

            t = -1;
            int tmon = mon;

            // 获取触发前的号数....................................................................................................
            //月份中的号数是否包含'?'，NO_SPEC：问号，不关心具体数值
            boolean dayOfMSpec = !daysOfMonth.contains(NO_SPEC);
            //星期中的是否包含'?'，NO_SPEC：问号，不关心具体数值
            boolean dayOfWSpec = !daysOfWeek.contains(NO_SPEC);

            if (dayOfMSpec && !dayOfWSpec) {// 根据月份中的号数规则计算前一次号数
                st = daysOfMonth.headSet(day, true);

                if (lastdayOfMonth) {//月份的最后一天
                    if (!nearestWeekday) {//nearestWeekday:最接近指定天的工作日（周一到周五）,每个月的最后一个工作日
                        t = day;
                        day = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                        day -= lastdayOffset; //假如cron为0 0 9,12,18,21 L-12 * ? *，lastdayOffset为12
                        if (t < day) {
                            mon--;
                            if (mon < 1) {
                                mon = 12;
                                tmon = 3333; // 保证mon != tmon
                                cl.roll(Calendar.YEAR, 1);
                            }

                            day = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                        }
                    } else {
                        t = day;
                        day = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                        day -= lastdayOffset;

                        java.util.Calendar tcal = java.util.Calendar.getInstance(getTimeZone());
                        tcal.set(Calendar.SECOND, 0);
                        tcal.set(Calendar.MINUTE, 0);
                        tcal.set(Calendar.HOUR_OF_DAY, 0);
                        tcal.set(Calendar.DAY_OF_MONTH, day);
                        tcal.set(Calendar.MONTH, mon - 1);
                        tcal.set(Calendar.YEAR, cl.get(Calendar.YEAR));

                        int ldom = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                        int dow = tcal.get(Calendar.DAY_OF_WEEK);

                        if (dow == Calendar.SATURDAY && day == 1) {
                            day += 2;
                        } else if (dow == Calendar.SATURDAY) {
                            day -= 1;
                        } else if (dow == Calendar.SUNDAY && day == ldom) {
                            day -= 2;
                        } else if (dow == Calendar.SUNDAY) {
                            day += 1;
                        }

                        tcal.set(Calendar.SECOND, sec);
                        tcal.set(Calendar.MINUTE, min);
                        tcal.set(Calendar.HOUR_OF_DAY, hr);
                        tcal.set(Calendar.DAY_OF_MONTH, day);
                        tcal.set(Calendar.MONTH, mon - 1);
                        Date nTime = tcal.getTime();
                        if (nTime.after(endTime)) {
                            day = getLastDayOfMonth(mon - 1, tcal.get(Calendar.YEAR));

                            mon--;
                        }
                    }
                } else if (nearestWeekday) {
                    t = day;
                    day = ((Integer) daysOfMonth.first()).intValue();

                    java.util.Calendar tcal = java.util.Calendar.getInstance(getTimeZone());
                    tcal.set(Calendar.SECOND, 0);
                    tcal.set(Calendar.MINUTE, 0);
                    tcal.set(Calendar.HOUR_OF_DAY, 0);
                    tcal.set(Calendar.DAY_OF_MONTH, day);
                    tcal.set(Calendar.MONTH, mon - 1);
                    tcal.set(Calendar.YEAR, cl.get(Calendar.YEAR));

                    int ldom = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));
                    int dow = tcal.get(Calendar.DAY_OF_WEEK);

                    if (dow == Calendar.SATURDAY && day == 1) {
                        day += 2;
                    } else if (dow == Calendar.SATURDAY) {
                        day -= 1;
                    } else if (dow == Calendar.SUNDAY && day == ldom) {
                        day -= 2;
                    } else if (dow == Calendar.SUNDAY) {
                        day += 1;
                    }

                    tcal.set(Calendar.SECOND, sec);
                    tcal.set(Calendar.MINUTE, min);
                    tcal.set(Calendar.HOUR_OF_DAY, hr);
                    tcal.set(Calendar.DAY_OF_MONTH, day);
                    tcal.set(Calendar.MONTH, mon - 1);
                    Date nTime = tcal.getTime();
                    if (nTime.after(endTime)) {
                        day = ((Integer) daysOfMonth.last()).intValue();
                        mon--;
                    }
                } else if (st != null && st.size() != 0) {
                    t = day;
                    day = st.last();
                } else {
                    day = ((Integer) daysOfMonth.last()).intValue();
                    mon--;

                    // 小月还要继续处理
                    int lastDay = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));

                    NavigableSet<Integer> headSet = daysOfMonth.headSet(lastDay, true);

                    //比如：日期触发为：[28,29,30,31],碰巧为2月或者小月，计算出来的触发日期为：2月31
                    if (headSet != null && headSet.size() != 0) {
                        day = headSet.last();
                    } else {
                        day = ((Integer) daysOfMonth.last()).intValue();
                        mon--;
                    }
                }

                if (day != t || mon != tmon) {
                    cl.set(Calendar.SECOND, 59);
                    cl.set(Calendar.MINUTE, 59);
                    cl.set(Calendar.HOUR_OF_DAY, 23);
                    cl.set(Calendar.DAY_OF_MONTH, day);
                    cl.set(Calendar.MONTH, mon - 1);
                    continue;
                }
            } else if (dayOfWSpec && !dayOfMSpec) { // 根据星期的规则来获取日期
                if (lastdayOfWeek) { // 每个月的最后一个星期几
                    // 期望的星期几
                    int dow = ((Integer) daysOfWeek.last()).intValue();
                    // 当前星期几
                    int cDow = cl.get(Calendar.DAY_OF_WEEK);
                    int daysToDel = 0;
                    if (cDow > dow) {
                        daysToDel = cDow - dow;
                    }
                    if (cDow < dow) {
                        daysToDel = cDow + (7 - dow);
                    }

                    int lDay = getLastDayOfMonth(mon, cl.get(Calendar.YEAR));

                    if (day - daysToDel < 1 || day - daysToDel + 7 <= lDay) { // 是否出过当月触发的时间
                        cl.set(Calendar.SECOND, 59);
                        cl.set(Calendar.MINUTE, 59);
                        cl.set(Calendar.HOUR_OF_DAY, 23);
                        cl.set(Calendar.DAY_OF_MONTH, getLastDayOfMonth(mon - 1, cl.get(Calendar.YEAR)));
                        //不用担心"mon - 2"为负数，为负数会自动退回年份和月份，月份从0开始的
                        cl.set(Calendar.MONTH, mon - 2);
                        continue;
                    }

                    day -= daysToDel;

                    if (daysToDel > 0) {
                        cl.set(Calendar.SECOND, 59);
                        cl.set(Calendar.MINUTE, 59);
                        cl.set(Calendar.HOUR_OF_DAY, 23);
                        cl.set(Calendar.DAY_OF_MONTH, day);
                        //月份从0开始的
                        cl.set(Calendar.MONTH, mon - 1);
                        continue;
                    }

                } else if (nthdayOfWeek != 0) {// 每个月的第几个星期的日期计算
                    //期待的符合cron要求的日期，desired
                    int dow = ((Integer) daysOfWeek.last()).intValue();
                    // 当前日期
                    int cDow = cl.get(Calendar.DAY_OF_WEEK);

                    //前一个出发点需要减去的天数
                    int daysToDel = 0;
                    if (cDow > dow) {
                        daysToDel = cDow - dow;
                    } else if (cDow < dow) {
                        daysToDel = cDow + (7 - dow);
                    }

                    //日期是否变动
                    boolean dayShifted = false;
                    if (daysToDel > 0) {
                        dayShifted = true;
                    }

                    day -= daysToDel;
                    //月份的第几个星期
                    int weekOfMonth = day / 7;
                    if (day % 7 > 0) {
                        weekOfMonth++;
                    }

                    daysToDel = (weekOfMonth - nthdayOfWeek) * 7;
                    day -= daysToDel;
                    if (daysToDel < 0 || day < 1) {//当月的日期不满足，向前推一个月
                        cl.set(Calendar.SECOND, 59);
                        cl.set(Calendar.MINUTE, 59);
                        cl.set(Calendar.HOUR_OF_DAY, 23);
                        cl.set(Calendar.DAY_OF_MONTH, getLastDayOfMonth(mon - 1, cl.get(Calendar.YEAR)));
                        cl.set(Calendar.MONTH, mon - 2);
                        continue;
                    } else if (daysToDel > 0 || dayShifted) {
                        cl.set(Calendar.SECOND, 59);
                        cl.set(Calendar.MINUTE, 59);
                        cl.set(Calendar.HOUR_OF_DAY, 23);
                        cl.set(Calendar.DAY_OF_MONTH, day);
                        //月份从0开始
                        cl.set(Calendar.MONTH, mon - 1);
                        continue;
                    }
                } else {
                    //当前日期
                    int cDow = cl.get(Calendar.DAY_OF_WEEK);
                    //期待的符合cron要求的日期，desired
                    int dow = ((Integer) daysOfWeek.first()).intValue();

                    st = daysOfWeek.headSet(Integer.valueOf(cDow), true);
                    if (st != null && st.size() > 0) {
                        dow = st.last().intValue();
                    }

                    //向前推的天数
                    int daysToDel = 0;
                    if (cDow > dow) {
                        daysToDel = cDow - dow;
                    }
                    if (cDow < dow) {
                        daysToDel = cDow + (7 - dow);
                    }


                    if (day - daysToDel < 1) { // 向前推的时间小于1号，前推一个月
                        cl.set(Calendar.SECOND, 59);
                        cl.set(Calendar.MINUTE, 59);
                        cl.set(Calendar.HOUR_OF_DAY, 23);
                        cl.set(Calendar.DAY_OF_MONTH, getLastDayOfMonth(mon - 1, cl.get(Calendar.YEAR)));
                        //月份从0开始
                        cl.set(Calendar.MONTH, mon - 2);
                        continue;
                    } else if (daysToDel > 0) { // 和满足要求的星期天数的日期交换日期？
                        cl.set(Calendar.SECOND, 59);
                        cl.set(Calendar.MINUTE, 59);
                        cl.set(Calendar.HOUR_OF_DAY, 23);
                        cl.set(Calendar.DAY_OF_MONTH, day - daysToDel);
                        cl.set(Calendar.MONTH, mon - 1);
                        continue;
                    }
                }
            } else { // dayOfWSpec && !dayOfMSpec
                UnsupportedOperationException unsupportedOperationException = new UnsupportedOperationException(
                        "Support for specifying both a day-of-week AND a day-of-month parameter is not implemented.");
                throw unsupportedOperationException;
            }
            cl.set(Calendar.DAY_OF_MONTH, day);

            //我们的月份计算时从1开始 的
            mon = cl.get(Calendar.MONTH) + 1;
            int year = cl.get(Calendar.YEAR);
            t = -1;

            //防止cron表达式错误，导致无法触发
            if (year < MIN_YEAR) {
                return null;
            }

            // 获取月份......................................................................................................
            st = months.headSet(Integer.valueOf(mon), true);
            if (st != null && st.size() != 0) {
                t = mon;
                mon = st.last().intValue();
            } else {
                mon = months.last().intValue();
                year--;
            }
            if (mon != t) {
                cl.set(Calendar.SECOND, 59);
                cl.set(Calendar.MINUTE, 59);
                cl.set(Calendar.HOUR_OF_DAY, 23);
                cl.set(Calendar.DAY_OF_MONTH, getLastDayOfMonth(mon, year));
                cl.set(Calendar.MONTH, mon - 1);
                cl.set(Calendar.YEAR, year);
                continue;
            }
            cl.set(Calendar.MONTH, mon - 1);

            year = cl.get(Calendar.YEAR);
            t = -1;

            // 获取年份......................................................................................................
            st = years.headSet(Integer.valueOf(year), true);
            if (st != null && st.size() != 0) {
                t = year;
                year = st.last().intValue();
            } else {
                return null; // 没在触发年份内
            }

            if (year != t) {
                cl.set(Calendar.SECOND, 59);
                cl.set(Calendar.MINUTE, 59);
                cl.set(Calendar.HOUR_OF_DAY, 23);
                cl.set(Calendar.DAY_OF_MONTH, getLastDayOfMonth(12, year));
                cl.set(Calendar.MONTH, 11);
                cl.set(Calendar.YEAR, year);
                continue;
            }
            cl.set(Calendar.YEAR, year);

            gotOne = true;
        }

        return cl.getTime();
    }

    protected boolean isLeapYear(int year) {
        return ((year % 4 == 0 && year % 100 != 0) || (year % 400 == 0));
    }

    protected int getLastDayOfMonth(int monthNum, int year) {

        switch (monthNum) {
            case 1:
                return 31;
            case 2:
                return (isLeapYear(year)) ? 29 : 28;
            case 3:
                return 31;
            case 4:
                return 30;
            case 5:
                return 31;
            case 6:
                return 30;
            case 7:
                return 31;
            case 8:
                return 31;
            case 9:
                return 30;
            case 10:
                return 31;
            case 11:
                return 30;
            case 12:
                return 31;
            default:
                throw new IllegalArgumentException("Illegal month number: "
                        + monthNum);
        }
    }
    
}

class ValueSet {
    public int value;

    public int pos;
}
