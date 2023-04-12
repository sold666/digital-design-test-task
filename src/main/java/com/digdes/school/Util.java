package com.digdes.school;

public class Util {
    public static final String LOGICAL_OPERATOR_NAME = "logicalOperator";
    public static final String REGEX_FOR_FULL_CONDITION =
            "\\s+(?i)and\\s+|\\s+(?i)or\\s+";
    public static final String FIRST_REGEX_FOR_MINI_CONDITION =
            "'([a-zA-Z]*)'\\s*(=|[!<>]=?)\\s*((?:\\d+|\\d*.\\d+|'(?:[^']*(?:.[^']*)*)')|true|false)\\s*";
    public static final String SECOND_REGEX_FOR_MINI_CONDITION =
            "'([a-zA-Z]*)'\\s*(like|ilike)\\s*'(.*?)'\\s*";

    // =
    public static boolean compareEqual(Object rowValue, Object conditionValue) throws Exception {
        checkTypes(rowValue);
        checkTypes(conditionValue);
        if (rowValue.getClass() != conditionValue.getClass()) {
            return false;
        }
        if (rowValue instanceof Boolean) {
            return (Boolean) rowValue == (Boolean) conditionValue;
        }
        if (rowValue instanceof String) {
            return ((String) rowValue).equals((String) conditionValue);
        }
        if (rowValue instanceof Long) {
            return (Long) rowValue == (Long) conditionValue;
        }
        if (rowValue instanceof Double) {
            return (Double) rowValue == (Double) conditionValue;
        }
        return false;
    }

    // !=
    public static boolean compareNotEqual(Object rowValue, Object conditionValue) throws Exception {
        return !compareEqual(rowValue, conditionValue);
    }

    // >=
    public static boolean greaterThanOrEqual(Object rowValue, Object conditionValue) throws Exception {
        if (rowValue instanceof Long && conditionValue instanceof Long) {
            return ((Long) rowValue).compareTo((Long) conditionValue) >= 0;
        } else if (rowValue instanceof Double && conditionValue instanceof Double) {
            return ((Double) rowValue).compareTo((Double) conditionValue) >= 0;
        } else if (rowValue instanceof Long && conditionValue instanceof Double) {
            return ((Long) rowValue).doubleValue() >= (Double) conditionValue;
        } else if (rowValue instanceof Double && conditionValue instanceof Long) {
            return ((Double) rowValue) >= ((Long) conditionValue).doubleValue();
        } else {
            throw new Exception("Invalid operand types for operator '>='");
        }
    }

    // <=
    public static boolean lessThanOrEqual(Object rowValue, Object conditionValue) throws Exception {
        if (rowValue instanceof Long && conditionValue instanceof Long) {
            return ((Long) rowValue).compareTo((Long) conditionValue) <= 0;
        } else if (rowValue instanceof Double && conditionValue instanceof Double) {
            return ((Double) rowValue).compareTo((Double) conditionValue) <= 0;
        } else if (rowValue instanceof Long && conditionValue instanceof Double) {
            return ((Long) rowValue).doubleValue() <= (Double) conditionValue;
        } else if (rowValue instanceof Double && conditionValue instanceof Long) {
            return ((Double) rowValue) <= ((Long) conditionValue).doubleValue();
        } else {
            throw new Exception("Invalid operand types for operator '<='");
        }
    }

    // <
    public static boolean lessThan(Object a, Object b) throws Exception {
        if (a instanceof Long && b instanceof Long) {
            return (Long) a < (Long) b;
        } else if (a instanceof Double && b instanceof Double) {
            return (Double) a < (Double) b;
        } else if (a instanceof Long && b instanceof Double) {
            return (double) (Long) a < (Double) b;
        } else if (a instanceof Double && b instanceof Long) {
            return (Double) a < (double) (Long) b;
        } else {
            throw new Exception("Invalid operand types for operator '<'");
        }
    }

    // >
    public static boolean greaterThan(Object a, Object b) throws Exception {
        if (a instanceof Long && b instanceof Long) {
            return (Long) a > (Long) b;
        } else if (a instanceof Double && b instanceof Double) {
            return (Double) a > (Double) b;
        } else if (a instanceof Long && b instanceof Double) {
            return (double) (Long) a > (Double) b;
        } else if (a instanceof Double && b instanceof Long) {
            return (Double) a > (double) (Long) b;
        } else {
            throw new Exception("Invalid operand types for operator '>'");
        }
    }


    public static boolean like(String str, String pattern) {
        String regex = pattern.replace("%", ".*");
        return str.matches(regex);
    }

    public static boolean ilike(String str, String pattern) {
        String regex = pattern.replace("%", ".*").toLowerCase();
        return str.toLowerCase().matches(regex);
    }

    // AND
    public static boolean logicalAnd(Object a, Object b) throws Exception {
        if (a instanceof Boolean && b instanceof Boolean) {
            return (Boolean) a && (Boolean) b;
        } else {
            throw new Exception("Invalid operand types for operator 'AND'");
        }
    }

    // OR
    public static boolean logicalOr(Object a, Object b) throws Exception {
        if (a instanceof Boolean && b instanceof Boolean) {
            return (Boolean) a || (Boolean) b;
        } else {
            throw new Exception("Invalid operand types for operator 'OR'");
        }
    }

    // Вспомогательная проверка на наличие нужных типов (Boolean, String, Long, Double) у аргументов
    private static void checkTypes(Object value) throws Exception {
        if (!(value instanceof Boolean || value instanceof String || value instanceof Long || value instanceof Double)) {
            throw new Exception("Invalid operand types");
        }
    }
}
