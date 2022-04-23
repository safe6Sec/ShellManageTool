package util;

public enum JavaVersion {
    JAVA_0_9(1.5f, "0.9"),
    JAVA_1_1(1.1f, "1.1"),
    JAVA_1_2(1.2f, "1.2"),
    JAVA_1_3(1.3f, "1.3"),
    JAVA_1_4(1.4f, "1.4"),
    JAVA_1_5(1.5f, "1.5"),
    JAVA_1_6(1.6f, "1.6"),
    JAVA_1_7(1.7f, "1.7"),
    JAVA_1_8(1.8f, "1.8"),
    JAVA_1_9(9.0f, "9"),
    JAVA_9(9.0f, "9"),
    JAVA_10(10.0f, "10"),
    JAVA_11(11.0f, "11"),
    JAVA_12(12.0f, "12"),
    JAVA_13(13.0f, "13"),
    JAVA_14(14.0f, "14"),
    JAVA_15(15.0f, "15"),
    JAVA_16(16.0f, "16"),
    JAVA_RECENT(maxVersion(), Float.toString(maxVersion()));
    
    private final String name;
    private final float value;

    private JavaVersion(float value2, String name2) {
        this.value = value2;
        this.name = name2;
    }

    public boolean atLeast(JavaVersion requiredVersion) {
        return this.value >= requiredVersion.value;
    }

    public boolean atMost(JavaVersion requiredVersion) {
        return this.value <= requiredVersion.value;
    }

    static JavaVersion getJavaVersion(String nom) {
        return get(nom);
    }

    static JavaVersion get(String nom) {
        if (nom == null) {
            return null;
        }
        if ("0.9".equals(nom)) {
            return JAVA_0_9;
        }
        if ("1.1".equals(nom)) {
            return JAVA_1_1;
        }
        if ("1.2".equals(nom)) {
            return JAVA_1_2;
        }
        if ("1.3".equals(nom)) {
            return JAVA_1_3;
        }
        if ("1.4".equals(nom)) {
            return JAVA_1_4;
        }
        if ("1.5".equals(nom)) {
            return JAVA_1_5;
        }
        if ("1.6".equals(nom)) {
            return JAVA_1_6;
        }
        if ("1.7".equals(nom)) {
            return JAVA_1_7;
        }
        if ("1.8".equals(nom)) {
            return JAVA_1_8;
        }
        if ("9".equals(nom)) {
            return JAVA_9;
        }
        if ("10".equals(nom)) {
            return JAVA_10;
        }
        if ("11".equals(nom)) {
            return JAVA_11;
        }
        if ("12".equals(nom)) {
            return JAVA_12;
        }
        if ("13".equals(nom)) {
            return JAVA_13;
        }
        if ("14".equals(nom)) {
            return JAVA_14;
        }
        if ("15".equals(nom)) {
            return JAVA_15;
        }
        if ("16".equals(nom)) {
            return JAVA_16;
        }
        float v = toFloatVersion(nom);
        if (((double) v) - 1.0d < 1.0d) {
            int firstComma = Math.max(nom.indexOf(46), nom.indexOf(44));
            if (Float.parseFloat(nom.substring(firstComma + 1, Math.max(nom.length(), nom.indexOf(44, firstComma)))) > 0.9f) {
                return JAVA_RECENT;
            }
            return null;
        } else if (v > 10.0f) {
            return JAVA_RECENT;
        } else {
            return null;
        }
    }

    public String toString() {
        return this.name;
    }

    private static float maxVersion() {
        float v = toFloatVersion(System.getProperty("java.specification.version", "99.0"));
        if (v > 0.0f) {
            return v;
        }
        return 99.0f;
    }

    private static float toFloatVersion(String value2) {
        if (!value2.contains(".")) {
            return toFloat(value2, -1);
        }
        String[] toParse = value2.split("\\.");
        if (toParse.length >= 2) {
            return toFloat(toParse[0] + '.' + toParse[1], -1);
        }
        return -1.0f;
    }

    private static float toFloat(String value2, int defaultReturnValue) {
        try {
            return Float.parseFloat(value2);
        } catch (Exception e) {
            return (float) defaultReturnValue;
        }
    }
}
