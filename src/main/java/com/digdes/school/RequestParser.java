package com.digdes.school;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestParser {
    private String request;
    private final List<String> columns;
    private final CollectionComponent collectionComponent;

    public RequestParser() {
        columns = Arrays.asList("id", "lastname", "age", "cost", "active");
        collectionComponent = new CollectionComponent();
    }

    public List<Map<String, Object>> parse(String request) throws Exception {
        this.request = request;
        return parseCommand();
    }

    private List<Map<String, Object>> parseCommand() throws Exception {
        String[] parts = request.split(" ", 3);
        String operation = parts[0];

        return switch (operation.toUpperCase()) {
            case "INSERT" -> parseInsertCommand(parts);
            case "UPDATE" -> parseUpdateCommand(parts);
            case "SELECT" -> parseSelectCommand(parts);
            case "DELETE" -> parseDeleteCommand(parts);
            default -> throw new Exception("Invalid command");
        };
    }

    private List<Map<String, Object>> parseInsertCommand(String[] parameters) throws Exception {
        if (parameters.length < 3 || !parameters[1].equalsIgnoreCase("VALUES")) {
            throw new Exception("Invalid insert command");
        }

        Map<String, Object> values = parseValues(new String[]{parameters[2]});
        return collectionComponent.insert(values);
    }

    private List<Map<String, Object>> parseUpdateCommand(String[] tokens) throws Exception {
        if (tokens.length < 3 || !tokens[1].equalsIgnoreCase("VALUES")) {
            throw new Exception("Invalid update command");
        }

        // Разбиваем строку на части до и после оператора WHERE
        String[] parts = tokens[2].split("(?i)where");

        Map<String, Object> values;
        Map<String, Object> conditions;
        if (parts.length == 1) {
            values = parseValues(new String[]{parts[0]});
            return collectionComponent.update(values);
        }
        values = parseValues(new String[]{parts[0]});
        conditions = parseFullConditions(parts[1]);
        return collectionComponent.updateWithCondition(values, conditions);
    }

    private List<Map<String, Object>> parseSelectCommand(String[] tokens) throws Exception {
        if (tokens.length < 2) {
            return collectionComponent.select();
        }
        if (!tokens[1].equalsIgnoreCase("WHERE")) {
            throw new Exception("Invalid select command");
        }

        Map<String, Object> conditionsMap = parseFullConditions(tokens[2]);
        return collectionComponent.selectWithCondition(conditionsMap);
    }

    private List<Map<String, Object>> parseDeleteCommand(String[] tokens) throws Exception {
        if (tokens.length < 2) {
            return collectionComponent.delete();
        }
        if (!tokens[1].equalsIgnoreCase("WHERE")) {
            throw new Exception("Invalid delete command");
        }

        Map<String, Object> conditionsMap = parseFullConditions(tokens[2]);
        return collectionComponent.deleteWithCondition(conditionsMap);
    }

    // Метод для парса значений переданных не в условии выборки
    private Map<String, Object> parseValues(String[] parameters) throws Exception {
        // Разбиваем на ключ-значение
        Map<String, Object> result = new HashMap<>();
        String[] valuesParts = parameters[0].split(",");
        for (String part : valuesParts) {
            String[] keyAndValue = part.trim().split("=", 2);
            String key = keyAndValue[0].toLowerCase().replaceAll("'", "").trim();
            if (!columns.contains(key)) {
                throw new Exception("Invalid column name: " + key);
            }
            String valueStr = keyAndValue[1].trim();
            Object value;
            // Определяем тип значения и преобразуем его
            value = convertFieldValue(key, valueStr);
            result.put(key, value);
        }
        return result;
    }

    // Метод для парса значений переданных в условии выборки
    private Map<String, Object> parseFullConditions(String condition) throws Exception {
        Map<String, Object> conditionsMap = new HashMap<>();
        String[] conditions = condition.trim().split(Util.REGEX_FOR_FULL_CONDITION);
        Pattern pattern = Pattern.compile(Util.REGEX_FOR_FULL_CONDITION);
        Matcher matcher = pattern.matcher(condition);
        String logicalOperator = null;
        if (matcher.find()) {
            logicalOperator = matcher.group().trim();
        }
        for (String conditionStr : conditions) {
            String[] parts = parseCondition(conditionStr);
            String fieldName = parts[0];
            String operator = parts[1];
            String fieldValueStr = parts[2];

            Object fieldValue = switch (operator) {
                case "=", "!=", ">=", "<=", "<", ">", "like", "ilike" -> convertFieldValue(fieldName, fieldValueStr);
                default -> throw new Exception("Unsupported operator: " + operator);
            };

            if (conditionsMap.containsKey(fieldName)) {
                Object existingValue = conditionsMap.get(fieldName);

                if (existingValue instanceof List) {
                    ((List) existingValue).add(new Object[]{operator, fieldValue});
                } else {
                    List<Object[]> valueList = new ArrayList<>();
                    valueList.add(new Object[]{operator, fieldValue});
                    Object[] existingValueArr = (Object[]) existingValue;
                    valueList.add(new Object[]{existingValueArr[0], existingValueArr[1]});
                    conditionsMap.put(fieldName, valueList);
                }
            } else {
                conditionsMap.put(fieldName, new Object[]{operator, fieldValue});
            }
        }
        if (logicalOperator != null) {
            conditionsMap.put(Util.LOGICAL_OPERATOR_NAME, logicalOperator);
        }
        return conditionsMap;
    }

    // Вспомогательный метод для парса значения переданного в условии выборки
    private String[] parseCondition(String input) throws Exception {
        String[] result = new String[3];
        Pattern firstPattern = Pattern.compile(Util.FIRST_REGEX_FOR_MINI_CONDITION);
        Pattern secondPattern = Pattern.compile(Util.SECOND_REGEX_FOR_MINI_CONDITION);
        Matcher firstMatcher = firstPattern.matcher(input);
        Matcher secondMatcher = secondPattern.matcher(input);

        if (firstMatcher.find()) {
            result[0] = firstMatcher.group(1).toLowerCase();
            result[1] = firstMatcher.group(2);
            result[2] = firstMatcher.group(3);
        } else if (secondMatcher.find()) {
            result[0] = secondMatcher.group(1).toLowerCase();
            result[1] = secondMatcher.group(2).toLowerCase();
            result[2] = secondMatcher.group(3);
        } else {
            throw new Exception("Invalid condition");
        }
        return result;
    }

    // Вспомогательный метод для преобразования значения в нужный нам тип
    private Object convertFieldValue(String fieldName, String fieldValueStr) {
        Object fieldValue = null;

        if (fieldName.equalsIgnoreCase("id") || fieldName.equalsIgnoreCase("age")) {
            fieldValue = Long.parseLong(fieldValueStr);
        } else if (fieldName.equalsIgnoreCase("cost")) {
            fieldValue = Double.parseDouble(fieldValueStr);
        } else if (fieldName.equalsIgnoreCase("lastname")) {
            fieldValue = fieldValueStr.replaceAll("'", "");
        } else if (fieldName.equalsIgnoreCase("active")) {
            fieldValue = Boolean.parseBoolean(fieldValueStr);
        }
        return fieldValue;
    }
}
