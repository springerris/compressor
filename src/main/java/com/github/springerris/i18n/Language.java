package com.github.springerris.i18n;

import java.util.Locale;

public enum Language {
    EN,
    RU;

    public static final Language SYSTEM;
    static {
        SYSTEM = Locale.getDefault().getLanguage().equals("en") ? EN : RU;
    }
}
