package com.digdes.school;

import java.util.*;

public class CollectionComponent {
    private final List<Map<String, Object>> collection;

    public CollectionComponent() {
        collection = new ArrayList<>();
    }

    public List<Map<String, Object>> insert(Map<String, Object> values) {
        collection.add(values);
        return collection;
    }

    public List<Map<String, Object>> delete() {
        Iterator<Map<String, Object>> iterator = collection.iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
        return collection;
    }

    public List<Map<String, Object>> deleteWithCondition(Map<String, Object> condition) {
        collection.removeIf(row -> {
            try {
                return matchesCondition(row, condition);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        });
        return collection;
    }

    public List<Map<String, Object>> update(Map<String, Object> values) {
        for (Map<String, Object> row : collection) {
            row.putAll(values);
        }
        return collection;
    }

    public List<Map<String, Object>> updateWithCondition(Map<String, Object> values, Map<String, Object> condition) throws Exception {
        for (Map<String, Object> row : collection) {
            if (matchesCondition(row, condition)) {
                row.putAll(values);
            }
        }
        return collection;
    }

    public List<Map<String, Object>> select() {
        return collection;
    }

    public List<Map<String, Object>> selectWithCondition(Map<String, Object> condition) throws Exception {
        for (Map<String, Object> row : collection) {
            if (matchesCondition(row, condition)) {
                return collection;
            }
        }
        return Collections.emptyList();
    }

    private boolean matchesCondition(Map<String, Object> row, Map<String, Object> condition) throws Exception {
        Object logicalOperator = condition.get(Util.LOGICAL_OPERATOR_NAME);
        if (logicalOperator != null) {
            condition.remove(Util.LOGICAL_OPERATOR_NAME);
        }
        boolean isAndOperator = logicalOperator != null && logicalOperator.equals("and");
        boolean isOrOperator = logicalOperator != null && logicalOperator.equals("or");
        boolean result = false;
        boolean firstCondition;
        boolean secondCondition;
        List<Boolean> tempResults = new ArrayList<>();

        for (Map.Entry<String, Object> entry : condition.entrySet()) {
            String column = entry.getKey();
            Object value = entry.getValue();
            Object rowValue = row.get(column);

            if (value instanceof List) {
                List<Object> arrValue = (List<Object>) entry.getValue();
                if (isAndOperator) {
                    Object[] firstArrayElement = (Object[]) arrValue.get(0);
                    Object[] secondArrayElement = (Object[]) arrValue.get(1);
                    firstCondition = matchOperator((String) firstArrayElement[0], firstArrayElement[1], rowValue);
                    secondCondition = matchOperator((String) secondArrayElement[0], secondArrayElement[1], rowValue);
                    result = Util.logicalAnd(firstCondition, secondCondition);
                } else if (isOrOperator) {
                    Object[] firstArrayElement = (Object[]) arrValue.get(0);
                    Object[] secondArrayElement = (Object[]) arrValue.get(1);
                    firstCondition = matchOperator((String) firstArrayElement[0], firstArrayElement[1], rowValue);
                    secondCondition = matchOperator((String) secondArrayElement[0], secondArrayElement[1], rowValue);
                    result = Util.logicalOr(firstCondition, secondCondition);
                }
            } else if (condition.size() > 1) {
                Object[] arrValue = (Object[]) entry.getValue();
                tempResults.add(matchOperator((String) arrValue[0], arrValue[1], rowValue));
                if (tempResults.size() == 2) {
                    if (isAndOperator) {
                        result = Util.logicalAnd(tempResults.get(0), tempResults.get(1));
                    } else if (isOrOperator) {
                        result = Util.logicalOr(tempResults.get(0), tempResults.get(1));
                    }
                }
            } else {
                Object[] arrValue = (Object[]) entry.getValue();
                result = matchOperator((String) arrValue[0], arrValue[1], rowValue);
            }
        }
        return result;
    }

    private boolean matchOperator(String operator, Object value, Object rowValue) throws Exception {
        return switch (operator) {
            case "=" -> Util.compareEqual(rowValue, value);
            case "!=" -> Util.compareNotEqual(rowValue, value);
            case ">=" -> Util.greaterThanOrEqual(rowValue, value);
            case "<=" -> Util.lessThanOrEqual(rowValue, value);
            case "<" -> Util.lessThan(rowValue, value);
            case ">" -> Util.greaterThan(rowValue, value);
            case "like" -> Util.like((String) rowValue, (String) value);
            case "ilike" -> Util.ilike((String) rowValue, (String) value);
            default -> throw new Exception("Unsupported operator: " + operator);
        };
    }
}
