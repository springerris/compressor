package com.github.springerris.gui.impl;

import com.github.springerris.gui.WindowContext;
import com.github.springerris.gui.helper.ChoiceWindow;
import com.github.springerris.util.YanHandler;

public class UploadChoiceWindow extends ChoiceWindow {

    public UploadChoiceWindow(WindowContext ctx) {
        super(ctx, "Выбрать сервис", 300, 100);
    }

    //

    @Override
    protected String[] getChoices() {
        return new String[] {
                "Отправить на Yandex Disk",
                "Отправить на Google Drive"
        };
    }

    @Override
    protected void onClickChoice(int index) {
        switch (index) {
            case 0 -> this.onClickChoice0();
            case 1 -> this.onClickChoice1();
        }
    }

    //

    private void onClickChoice0() {
        YanHandler yh = new YanHandler(this.ctx, this);
        yh.upload();
    }

    private void onClickChoice1() {
        // TODO
    }

}
