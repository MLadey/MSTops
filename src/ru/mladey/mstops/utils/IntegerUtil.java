package ru.mladey.mstops.utils;

@SuppressWarnings("All")
public class IntegerUtil {

    public static double a(double d) {
        return Math.floor(d * 100) / 100;
    }

    public static float a(float d) {
        return (float) (Math.floor(d * 100) / 100);
    }

    public boolean canParse(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }
}