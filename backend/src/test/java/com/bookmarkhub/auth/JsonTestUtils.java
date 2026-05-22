package com.bookmarkhub.auth;

import com.jayway.jsonpath.JsonPath;

final class JsonTestUtils {

    private JsonTestUtils() {
    }

    static String readJson(String json, String path) {
        return JsonPath.read(json, path);
    }
}
