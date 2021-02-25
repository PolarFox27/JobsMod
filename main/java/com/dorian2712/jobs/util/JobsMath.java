package com.dorian2712.jobs.util;

public class JobsMath {

    public static long clamp(long value, long min, long max)
    {
        if(value < min) return min;
        if(value > max) return max;
        else return value;
    }
    public static int clamp(int value, int min, int max)
    {
        if(value < min) return min;
        if(value > max) return max;
        else return value;
    }
}
