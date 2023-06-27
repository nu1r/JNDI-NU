package com.qi4l.jndi.gadgets.utils.dirty;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class RandomString {

    /**
     * 26个大写字母
     */
    public static final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * 26个小写字母
     */
    public static final String lower = upper.toLowerCase(Locale.ROOT);

    /**
     * 数字
     */
    public static final String digits = "0123456789";

    /**
     * alphanum为26个大写字母+26个小写字母+10个数字。产生的随机字符串从此挑选字符生成
     */
    public static final String alphanum = upper + lower + digits;

    private final Random random;

    private final char[] symbols;

    private final char[] buf;

    public RandomString(int length, Random random, String symbols) {
        if (length < 1) throw new IllegalArgumentException();
        if (symbols.length() < 2) throw new IllegalArgumentException();
        this.random = Objects.requireNonNull(random);
        this.symbols = symbols.toCharArray();
        this.buf = new char[length];
    }

    /**
     * 通过字母数字字符串生成器生成随机字符串。
     */
    public RandomString(int length, Random random) {
        this(length, random, alphanum);
    }

    /**
     * 生成随机字符串
     */
    public String getString() {
        for (int idx = 0; idx < buf.length; ++idx) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }
        return new String(buf);
    }
}
