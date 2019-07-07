package ru.smartflex.tools.dbf;

import java.io.InputStream;

class TestHelper {

    static InputStream getFile(String fn) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(fn);
    }
}
