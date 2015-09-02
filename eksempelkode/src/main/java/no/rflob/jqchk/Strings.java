package no.rflob.jqchk;

public final class Strings {

    public static String reverse(String s) {
        char[] chars = s.toCharArray();

        boolean hasSurrogates = false;
        int n = chars.length - 1;
        for (int j = (n-1) >> 1; j >= 0; j--) {
            int k = n - j;
            char cj = chars[j];
            char ck = chars[k];
            chars[j] = ck;
            chars[k] = cj;
            if (Character.isSurrogate(cj) ||
                    Character.isSurrogate(ck)) {
                hasSurrogates = true;
            }
        }
        if (hasSurrogates) {
            reverseAllValidSurrogatePairs(chars);
        }
        return new String(chars);
    }

    private static void reverseAllValidSurrogatePairs(char[] chars) {
        for (int i = 0; i < chars.length - 1; i++) {
            char c2 = chars[i];
            if (Character.isLowSurrogate(c2)) {
                char c1 = chars[i + 1];
                if (Character.isHighSurrogate(c1)) {
                    chars[i++] = c1;
                    chars[i] = c2;
                }
            }
        }
    }




    private Strings() {} static { new Strings(); }
}
