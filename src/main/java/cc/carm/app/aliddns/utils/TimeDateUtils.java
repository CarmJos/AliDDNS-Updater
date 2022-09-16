package cc.carm.app.aliddns.utils;

public class TimeDateUtils {

    /**
     * 将秒数转化为 DD:hh:mm:ss 格式
     *
     * @param allSeconds 秒数
     * @return DD:hh:mm:ss格式文本
     */
    public static String toDHMSStyle(long allSeconds) {
        long days = allSeconds / 86400L;
        long hours = allSeconds % 86400L / 3600L;
        long minutes = allSeconds % 3600L / 60L;
        long seconds = allSeconds % 60L;
        String DateTimes;
        if (days > 0L) {
            DateTimes = days + "天" + (hours > 0L ? hours + "小时" : "") + (minutes > 0L ? minutes + "分钟" : "") + (seconds > 0L ? seconds + "秒" : "");
        } else if (hours > 0L) {
            DateTimes = hours + "小时" + (minutes > 0L ? minutes + "分钟" : "") + (seconds > 0L ? seconds + "秒" : "");
        } else if (minutes > 0L) {
            DateTimes = minutes + "分钟" + (seconds > 0L ? seconds + "秒" : "");
        } else {
            DateTimes = seconds + "秒";
        }

        return DateTimes;
    }
}
