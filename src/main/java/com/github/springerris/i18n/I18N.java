package com.github.springerris.i18n;

import static com.github.springerris.i18n.LangString.builder;

/**
 * Holds internationalization keys; effectively an enum.
 * @see #LANGUAGE
 */
public final class I18N {


    /**
     * The language in use.
     * Used to resolve {@link LangString}s when not otherwise specified.
     */
    public static final Language LANGUAGE = Language.SYSTEM;

    //

    public static final LangString LIFECYCLE_START = builder()
            .en("Starting PP2024")
            .build();

    public static final LangString LIFECYCLE_CONTEXT = builder()
            .en("Created application context")
            .ru("Создан контекст приложения")
            .build();

    public static final LangString POPUP_INFO = builder()
            .en("Notice")
            .ru("Информация")
            .build();

    public static final LangString POPUP_ERROR = builder()
            .en("Error")
            .ru("Ошибка")
            .build();

    public static final LangString WINDOW_MAIN_TITLE = builder()
            .en("Main Window")
            .ru("Главное окно")
            .build();

    public static final LangString WINDOW_MAIN_BUTTON_ADD = builder()
            .en("Add")
            .ru("Добавить файл")
            .build();

    public static final LangString WINDOW_MAIN_BUTTON_IMPORT = builder()
            .en("Import")
            .ru("Импортировать")
            .build();

    public static final LangString WINDOW_MAIN_BUTTON_EXPORT = builder()
            .en("Export")
            .ru("Отправить архив в..")
            .build();

    public static final LangString WINDOW_MAIN_BUTTON_SYNC = builder()
            .en("Sync")
            .ru("Распаковать")
            .build();

    public static final LangString WINDOW_MAIN_ERROR_ADD_FILE = builder()
            .en("Failed to add file (violates hierarchy)")
            .ru("Не удалось добавить файл")
            .build();

    public static final LangString WINDOW_EXPORT_TITLE = builder()
            .en("Select a Service")
            .ru("Выбрать сервис")
            .build();

    public static final LangString WINDOW_EXPORT_OPTION_ZIP = builder()
            .en("Export as ZIP")
            .ru("Записать архив")
            .build();

    public static final LangString FILE_TYPE_ALL = builder()
            .en("All files")
            // TODO: ru
            .build();

    public static final LangString FILE_TYPE_ZIP = builder()
            .en("ZIP")
            .ru("ZIP")
            .build();

    public static final LangString FILE_TYPE_ZIP_ENCRYPTED = builder()
            .en("Encrypted ZIP")
            .ru("Шифрованный ZIP")
            .build();

    public static final LangString WINDOW_EXPORT_OPTION_YANDEX = builder()
            .en("Upload to Yandex Disk")
            .ru("Отправить на Yandex Disk")
            .build();

    public static final LangString WINDOW_EXPORT_OPTION_SFTP = builder()
            .en("Upload via SFTP")
            .ru("Отправить по SFTP")
            .build();

    public static final LangString WINDOW_CONNECT_SFTP_TITLE = builder()
            .en("Connect to SFTP")
            .ru("Поключение к SFTP")
            .build();

    public static final LangString WINDOW_CONNECT_SFTP_CONFIRM = builder()
            .en("Connect")
            .ru("Подключится")
            .build();

    public static final LangString WINDOW_CONNECT_SFTP_CANCEL = builder()
            .en("Cancel")
            // TODO: ru
            .build();

    public static final LangString WINDOW_CONNECT_SFTP_HOST = builder()
            .en("Host")
            .ru("Хост")
            .build();

    public static final LangString WINDOW_CONNECT_SFTP_PORT = builder()
            .en("Port")
            .ru("Порт")
            .build();

    public static final LangString WINDOW_CONNECT_SFTP_USERNAME = builder()
            .en("Username")
            .ru("Пользователь")
            .build();

    public static final LangString WINDOW_CONNECT_SFTP_AUTHENTICATION = builder()
            .en("Authentication")
            // TODO: ru
            .build();

    public static final LangString WINDOW_CONNECT_SFTP_NONE = builder()
            .en("None")
            // TODO: ru
            .build();

    public static final LangString WINDOW_CONNECT_SFTP_NONE_DESC = builder()
            .en("No authentication will be performed")
            // TODO: ru
            .build();

    public static final LangString WINDOW_CONNECT_SFTP_PASSWORD = builder()
            .en("Password")
            .ru("Пароль")
            .build();

    public static final LangString WINDOW_CONNECT_SFTP_PRIVATE_KEY = builder()
            .en("Private Key")
            // TODO: ru
            .build();

    public static final LangString WINDOW_CONNECT_SFTP_PRIVATE_KEY_DESC = builder()
            .en("System will be searched for keys")
            // TODO: ru
            .build();

    public static final LangString WINDOW_CONNECT_SFTP_ERROR_AUTH = builder()
            .en("Authentication failed. Provided password or key may be incorrect.")
            .ru("Логин и пароль не подошли под данный хост")
            .build();

    public static final LangString WINDOW_CONNECT_SFTP_ERROR_IO = builder()
            .en("Failed to connect to server. See log for more details.")
            .ru("Не удалось подключится к удалённому хосту")
            .build();

    public static final LangString WINDOW_IMPORT_TITLE = builder()
            .en("Import")
            .ru("Импортировать")
            .build();

    public static final LangString WINDOW_IMPORT_OPTION_ZIP = builder()
            .en("Import from Zip")
            .ru("Импортировать из Zip")
            .build();

    public static final LangString WINDOW_IMPORT_OPTION_YANDEX = builder()
            .en("Download from Yandex Disk")
            .ru("Загрузить с Yandex Disk")
            .build();

    public static final LangString WINDOW_IMPORT_OPTION_DRIVE = builder()
            .en("Download from Google Drive")
            // TOOD: ru
            .build();

    public static final LangString WINDOW_IMPORT_ERROR_PASSWORD = builder()
            .en("Incorrect encryption password")
            .ru("Пароль не подходит")
            .build();

    public static final LangString WINDOW_IMPORT_ERROR_FORMAT = builder()
            .en("Improper format (ZIP was not generated by this application)")
            .ru("Неправильный формат (ZIP не был создан)")
            .build();

    public static final LangString WINDOW_IMPORT_YANDEX_TITLE = builder()
            .en("Select an archive to download")
            .ru("Выберите архив для скачивания")
            .build();

    public static final LangString WINDOW_SYNC_TITLE = builder()
            .en("Sync")
            .ru("Распаковать архив")
            .build();

    public static final LangString WINDOW_SYNC_CONFIRM = builder()
            .en("Confirm")
            .ru("Подтвердить")
            .build();

    public static final LangString WINDOW_SYNC_CANCEL = builder()
            .en("Cancel")
            .ru("Отменить")
            .build();

    public static final LangString WINDOW_SYNC_ERROR = builder()
            .en("Extraction failed, see log for more details")
            .ru("Распаковка не удалась, посмотрите вывод в журнале")
            .build();

    public static final LangString WINDOW_AWAITING_TITLE = builder()
            .en("Processing...")
            .ru("Идёт обработка...")
            .build();

    public static final LangString WINDOW_AWAITING_TEXT = builder()
            .en("Please wait for the operation to complete...")
            .ru("Подождите завершения работы процедуры...")
            .build();

    public static final LangString WINDOW_AWAITING_COMPLETE = builder()
            .en("Done!")
            .ru("Готово!")
            .build();

    public static final LangString WINDOW_AWAITING_CONTINUE = builder()
            .en("Continue")
            .ru("Продолжить")
            .build();

    public static final LangString STAGE_PASSWORD_PROMPT_TITLE = builder()
            .en("Encryption")
            .ru("Выбор пароля")
            .build();

    public static final LangString STAGE_PASSWORD_PROMPT_CONFIRM = builder()
            .en("Encrypt the archive?")
            .ru("Добавить пароль для доступа к архиву?")
            .build();

    public static final LangString STAGE_PASSWORD_PROMPT_ENTER = builder()
            .en("Enter the password for the archive")
            .ru("Введите пароль для архива")
            .build();

    public static final LangString SEND_PICK_NAME = builder()
            .en("Enter a file name for the archive")
            .ru("Введите название файла для архива")
            .build();

    public static final LangString RECEIVE_CONFLICT = builder()
            .en("An archive with this name already exists. Replace?")
            .ru("Такой архив уже есть в данной директории, заменить?")
            .build();

    public static final LangString OP_WRITE_NEW_FILE = builder()
            .en("Write new file")
            .ru("Записать новый файл")
            .build();

    public static final LangString OP_OVERWRITE_FILE = builder()
            .en("Overwrite file")
            .ru("Перезаписать файл")
            .build();

    public static final LangString OP_CREATE_DIR = builder()
            .en("Create directory")
            .ru("Создать папку")
            .build();

    public static final LangString OP_DELETE_FILE = builder()
            .en("Delete file")
            .ru("Удалить файл")
            .build();

    public static final LangString OP_DELETE_DIR = builder()
            .en("Delete directory")
            .ru("Удалить папку")
            .build();

    public static final LangString PICKER_OPEN = builder()
            .en("Open")
            // TODO: ru
            .build();

    public static final LangString PICKER_HIDE_DOT_FILES = builder()
            .en("Hide dotfiles")
            // TODO: ru
            .build();

    public static final LangString PICKER_CONFLICT = builder()
            .en("File already exists. Replace?")
            // TODO: ru
            .build();

}
