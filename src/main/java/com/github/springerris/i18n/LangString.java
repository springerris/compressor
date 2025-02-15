package com.github.springerris.i18n;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.Map;

/**
 * Text that may resolve differently depending on the desired language.
 * If no language is specified, the string resolves against the
 * {@link I18N#LANGUAGE active language}. Any implicit stringification
 * will translate the object in this way.
 */
public class LangString implements CharSequence {

    @Contract("-> new")
    public static @NotNull Builder builder() {
        return new Builder();
    }

    //

    private final Map<Language, String> map;
    private final String active;

    public LangString(@NotNull Map<Language, String> map) {
        this.map = map;
        this.active = this.get(I18N.LANGUAGE);
    }

    //

    public @NotNull String get(@NotNull Language language) {
        return this.map.getOrDefault(language, "???");
    }

    /**
     * Resolves this string against the {@link I18N#LANGUAGE active language}.
     * @see #get(Language)
     */
    public @NotNull String get() {
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
    public @NotNull CharSequence subSequence(int i, int i1) {
        return this.active.subSequence(i, i1);
    }

    //

    @Override
    public @NotNull String toString() {
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

        @Contract("_, _ -> this")
        public @NotNull Builder add(@NotNull Language language, @NotNull String value) {
            this.map.put(language, value);
            return this;
        }

        @Contract("_ -> this")
        public @NotNull Builder en(@NotNull String value) {
            return this.add(Language.EN, value);
        }

        @Contract("_ -> this")
        public @NotNull Builder ru(@NotNull String value) {
            return this.add(Language.RU, value);
        }

        @Contract("-> new")
        public @NotNull LangString build() {
            return new LangString(this.map);
        }

    }

}
