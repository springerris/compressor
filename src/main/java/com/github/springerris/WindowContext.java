// Класс, реализующий контекст окна

package com.github.springerris;

import java.util.logging.Logger;

/**
 * Все параметры между разными {@link Window}
 */
public class WindowContext {

    private final Logger logger;
    private final Zipper zipper;
    private String user;
    public WindowContext(Logger logger, Zipper zipper) {
        this.logger = logger;
        this.zipper = zipper;
        this.user = null;
    }

    public  Logger logger() {
        return this.logger;
    }

    /** Возвращает zipper */
    public Zipper zipper() {
        return this.zipper;
    }


}
