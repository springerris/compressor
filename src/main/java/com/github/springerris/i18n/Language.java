package com.github.springerris.i18n;

import java.util.Locale;

/**
 * A language supported by the frontend
 */
public enum Language {
    /** English */
    EN,
    /** Russian */
    RU;

    /** The system default language */
    public static final Language SYSTEM;
    static {
        SYSTEM = Locale.getDefault().getLanguage().equals("en") ? EN : RU;
    }
}
