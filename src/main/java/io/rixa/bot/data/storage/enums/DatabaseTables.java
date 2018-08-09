package io.rixa.bot.data.storage.enums;

public enum DatabaseTables {

    CORE("CREATE TABLE IF NOT EXISTS `core` ( `guild_id` varchar(255) NOT NULL PRIMARY KEY, `guild_name` varchar(255) NOT NULL," +
            " `description` text, `enlisted` tinyint(1) NOT NULL DEFAULT '0', `icon` varchar(255) DEFAULT NULL, `link` varchar(255) DEFAULT NULL," +
            " `keywords` text, `creation_date` varchar(255) NOT NULL DEFAULT 'N/A', `guild_region` varchar(255) NOT NULL DEFAULT 'N/A'," +
            " `guild_owner` varchar(255) NOT NULL DEFAULT 'N/A'\n" +
            ");"),

    LEVELS("CREATE TABLE IF NOT EXISTS `levels` ( `id` int(11) NOT NULL AUTO_INCREMENT PRIMARY KEY, `guild_id` varchar(255) NOT NULL, `user_id` varchar(255) NOT NULL," +
            " `experience` int(90) NOT NULL\n" +
            ");"),

    MODULES("CREATE TABLE IF NOT EXISTS `modules` (`guild_id` varchar(255) DEFAULT NULL, `levels` tinyint(1) NOT NULL DEFAULT '1', `conversation` tinyint(1) NOT NULL DEFAULT '1');"),

    MUSIC("CREATE TABLE IF NOT EXISTS `music` ( `guild_id` varchar(255) NOT NULL UNIQUE PRIMARY KEY, `music_role` varchar(255) NOT NULL DEFAULT 'default_value'," +
            " `skip_amount` int(5) NOT NULL DEFAULT '0', `max_playlist_amount` int(5) NOT NULL DEFAULT '100', `enabled` tinyint(1) NOT NULL DEFAULT '0'\n" +
            ");"),

    PERMISSIONS("CREATE TABLE IF NOT EXISTS `permissions` ( `user_id` varchar(255) NOT NULL, `guild_id` varchar(255) NOT NULL, `MUTE` tinyint(1) NOT NULL DEFAULT '0'," +
            " `ADD_ROLE` tinyint(1) NOT NULL DEFAULT '0', `REMOVE_ROLE` tinyint(1) NOT NULL DEFAULT '0', `CLEAR_CHAT` tinyint(1) NOT NULL DEFAULT '0'," +
            " `ACCESS_CONFIG` tinyint(1) NOT NULL DEFAULT '0', `PM_MESSAGE` tinyint(1) NOT NULL DEFAULT '0', `KICK_MEMBER` tinyint(1) NOT NULL DEFAULT '0'," +
            " `BAN_MEMBER` tinyint(1) NOT NULL DEFAULT '0', `TOGGLE_RAIDMODE` tinyint(4) NOT NULL DEFAULT '0', `UNMUTE` tinyint(1) NOT NULL DEFAULT '0');"),

    POLLS("CREATE TABLE IF NOT EXISTS `polls` ( `id` int(9) NOT NULL AUTO_INCREMENT PRIMARY KEY, `name` varchar(255) NOT NULL, `description` text," +
            " `option_1` varchar(255) DEFAULT NULL, `option_2` varchar(255) DEFAULT NULL, `option_3` varchar(255) DEFAULT NULL," +
            " `option_4` varchar(255) DEFAULT NULL, `option_5` varchar(255) DEFAULT NULL, `option_6` varchar(255) DEFAULT NULL," +
            " `option_7` varchar(255) DEFAULT NULL, `option_8` varchar(255) DEFAULT NULL, `option_9` varchar(255) DEFAULT NULL," +
            " `option_10` varchar(255) DEFAULT NULL);"),

    ROLE_REWARDS("CREATE TABLE IF NOT EXISTS `role_rewards` ( `guild_id` varchar(255) NOT NULL PRIMARY KEY, `level` int(9) NOT NULL, `role_id` varchar(255) NOT NULL\n" +
            ");"),

    SETTINGS("CREATE TABLE IF NOT EXISTS `settings` ( `guild_id` varchar(255) NOT NULL, `log_enabled` tinyint(1) NOT NULL, `log_channel` varchar(255) NOT NULL," +
            " `joinMessage` text NOT NULL, `quitMessage` text NOT NULL, `greetings` varchar(255) NOT NULL, `farewell` varchar(255) NOT NULL," +
            " `prefix` varchar(5) NOT NULL, `joinPm` text NOT NULL, `joinVerification` tinyint(1) NOT NULL, `defaultRole` varchar(255) NOT NULL, `muteRole` varchar(255) NOT NULL\n" +
            ")"),

    TWITTER("CREATE TABLE IF NOT EXISTS `twitter` (`guild_id` varchar(255) NOT NULL, `consumer_key` varchar(255) NOT NULL, `consumer_secret` varchar(255) NOT NULL, " +
            "`access_key` varchar(255) NOT NULL, `access_secret` varchar(255) NOT NULL, `tweet_channel` varchar(255) NOT NULL, `updates_channel` varchar(255) NOT NULL)"),

    USER("CREATE TABLE IF NOT EXISTS `user` (`user_id` varchar(255) NOT NULL UNIQUE, `user_name` varchar(255) NOT NULL, `avatar_hash` varchar(255) DEFAULT 'N/A');"); // USER is mostly used for http://rixa.io.

    private String query;

    DatabaseTables(String s) {
        query = s;
    }

    public String toString() {
        return this.name();
    }

    public String getQuery() {
        return this.query;
    }
}
