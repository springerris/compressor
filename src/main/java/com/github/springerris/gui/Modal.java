package com.github.springerris.gui;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.UnknownNullability;

/**
 * An interface that a Window may implement to mark itself as a "modal", meaning that it is meant to be used as a
 * popup and may provide a {@link #modalValue()} to the code which initiated the popup.
 */
public interface Modal<T> {

    @UnknownNullability T modalValue();

    //

    /**
     * A modal which does not report a modal value.
     */
    interface Nullary extends Modal<Void> {

        @Contract("-> null")
        @Override
        default Void modalValue() {
            return null;
        }

    }

}
