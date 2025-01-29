package com.github.springerris.i18n;

import static com.github.springerris.i18n.LangString.builder;

/** Internationalization */
public final class I18N {

    /** Language in use */
    public static Language LANGUAGE = Language.SYSTEM;

    //

    public static LangString LIFECYCLE_START = builder()
            .en("Starting PP2024")
            .build();

    public static LangString LIFECYCLE_CONTEXT = builder()
            .en("Created application context")
            .build();

    public static LangString POPUP_INFO = builder()
            .en("Notice")
            .ru("Информация")
            .build();

    public static LangString POPUP_ERROR = builder()
            .en("Error")
            .ru("Ошибка")
            .build();

    public static LangString WINDOW_MAIN_TITLE = builder()
            .en("Main Window")
            .ru("Главное окно")
            .build();

    public static LangString WINDOW_MAIN_BUTTON_ADD_FILE = builder()
            .en("Add file to archive")
            .ru("<html>Добавить <br>файл в архив</html>")
            .build();

    public static LangString WINDOW_MAIN_BUTTON_WRITE_ZIP = builder()
            .en("Write archive")
            .ru("<html>Записать<br> архив</html>")
            .build();

    public static LangString WINDOW_MAIN_BUTTON_ASCEND = builder()
            .en("Move Up ..")
            .ru("На уровень выше ..")
            .build();

    public static LangString WINDOW_MAIN_BUTTON_ADD_DIR = builder()
            .en("Add directory to archive")
            .ru("<html>Добавить папку<br> с файлами в архив</html>")
            .build();

    public static LangString WINDOW_MAIN_BUTTON_SEND = builder()
            .en("Send archive")
            .ru("<html>Отправить <br> архив в..       </html>")
            .build();

    public static LangString WINDOW_MAIN_ERROR_ADD_FILE = builder()
            .en("Failed to add file (violates hierarchy)")
            .ru("Не удалось добавить файл")
            .build();

    public static LangString WINDOW_MAIN_ERROR_ADD_DIR = builder()
            .en("Failed to add directory (violates hierarchy)")
            .ru("Не удалось добавить папку")
            .build();

    public static LangString WINDOW_UPLOAD_TITLE = builder()
            .en("Select a Service")
            .ru("Выбрать сервис")
            .build();

    public static LangString WINDOW_UPLOAD_OPTION_YANDEX = builder()
            .en("Upload to Yandex Disk")
            .ru("Отправить на Yandex Disk")
            .build();

    public static LangString WINDOW_UPLOAD_OPTION_DRIVE = builder()
            .en("Upload to Google Drive")
            .ru("Отправить на Google Drive")
            .build();

    public static LangString STAGE_PASSWORD_PROMPT_TITLE = builder()
            .en("Encryption")
            .ru("Выбор пароля")
            .build();

    public static LangString STAGE_PASSWORD_PROMPT_CONFIRM = builder()
            .en("Encrypt the archive?")
            .ru("Добавить пароль для доступа к архиву?")
            .build();

    public static LangString STAGE_PASSWORD_PROMPT_ENTER = builder()
            .en("Enter the password for the archive")
            .ru("Введите пароль для архива")
            .build();

    public static LangString SEND_YANDEX_PICK_NAME = builder()
            .en("Enter a file name for the archive")
            .ru("Введите название файла для архива")
            .build();

}
