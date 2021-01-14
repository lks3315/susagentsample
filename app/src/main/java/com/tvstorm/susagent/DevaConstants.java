package com.tvstorm.susagent;

import android.net.Uri;

public class DevaConstants {

    public static final String AUTHORITY = "com.tvstorm.deva.provider";

    public static final String NOTICE_MESSAGE_TABLE = "notice_message_table";
    public static final String CONFIG_TABLE = "config_table";

    public static final Uri URI_DEVA = Uri.parse("content://" + AUTHORITY + "/" + NOTICE_MESSAGE_TABLE);
    public static final Uri URI_CONFIG = Uri.parse("content://" + AUTHORITY + "/" + CONFIG_TABLE);

    public static final String BIND_SERVICE_ACTION = "deva.client.service";
    public static final String BIND_SERVICE_PKG = "com.tvstorm.deva";

    public static final String RECONNECT_TAG = "retry.connect";

    // NoticeMessageDTO
    public static final String NOTICE_ID = "id";
    public static final String NOTICE_DATE = "date";
    public static final String NOTICE_TITLE = "title";
    public static final String NOTICE_TYPE = "type";
    public static final String NOTICE_MESSAGE = "message";
    public static final String NOTICE_READ = "read";
    public static final String NOTICE_PARAMS = "optionalParams";


    // Config
    public static final String CONFIG_NAME = "name";
    public static final String CONFIG_VALUE = "value";
}
