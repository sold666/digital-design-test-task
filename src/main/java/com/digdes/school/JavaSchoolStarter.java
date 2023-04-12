package com.digdes.school;

import java.util.List;
import java.util.Map;

public class JavaSchoolStarter {
    private final RequestParser requestParser = new RequestParser();

    //Дефолтный конструктор
    public JavaSchoolStarter() {

    }

    //На вход запрос, на выход результат выполнения запроса
    public List<Map<String, Object>> execute(String request) throws Exception {
        return requestParser.parse(request);
    }
}

