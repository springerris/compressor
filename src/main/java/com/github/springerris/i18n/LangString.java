package com.github.springerris.i18n;

import java.util.EnumMap;
import java.util.Map;

public class LangString implements CharSequence {

    public static Builder builder() {
        return new Builder();
    }

    //

    private final Map<Language, String> map;
    private final String active;

    public LangString(Map<Language, String> map) {
        this.map = map;
        this.active = this.get(I18N.LANGUAGE);
    }

    //

    public String get(Language language) {
        return this.map.getOrDefault(language, "???");
    }

    public String get() {
        return this.active;
    }

    //

    @Override
    public int length() {
        return this.active.length();
    }

    @Override
    public char charAt(int i) {
        return this.active.charAt(i);
    }

    @Override
    public CharSequence subSequence(int i, int i1) {
        return this.active.subSequence(i, i1);
    }

    //

    @Override
    public String toString() {
        return this.active;
    }

    @Override
    public int hashCode() {
        return this.active.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof LangString other) {
            return CharSequence.compare(this.active, other.active) == 0;
        }
        return super.equals(obj);
    }

    //

    public static final class Builder {

        private final EnumMap<Language, String> map = new EnumMap<>(Language.class);

        //

        public Builder add(Language language, String value) {
            this.map.put(language, value);
            return this;
        }

        public Builder en(String value) {
            return this.add(Language.EN, value);
        }

        public Builder ru(String value) {
            return this.add(Language.RU, value);
        }

        public LangString build() {
            return new LangString(this.map);
        }

    }

}
