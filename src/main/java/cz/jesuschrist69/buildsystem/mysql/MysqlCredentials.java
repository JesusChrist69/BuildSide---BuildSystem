package cz.jesuschrist69.buildsystem.mysql;

import lombok.Data;

@Data
public class MysqlCredentials {

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final String database;
    private final String tablePrefix;
    private final boolean autoReconnect;

}
