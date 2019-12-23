package utils;

public class StringUtil {
    public static boolean isEmpty(String str) {
        return (str == null || str.isEmpty());
    }

    public static String strip(String target, char c) {
        char[] origin = target.toCharArray();
        int l = 0;
        int r = target.length() - 1;
        while (l < r) {
            if(origin[l] == c)  l ++;
            if(origin[r] == c)  r --;

            if(origin[l] != c && origin[r] != c)    break;
        }
        return target.substring(l, r+1);
    }
}
