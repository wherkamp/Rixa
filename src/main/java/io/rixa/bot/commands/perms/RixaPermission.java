package io.rixa.bot.commands.perms;

public enum RixaPermission {
    NONE,
    MUTE,
    ADD_ROLE,
    REMOVE_ROLE,
    CLEAR_CHAT,
    ACCESS_CONFIG,
    PM_MESSAGE,
    KICK_MEMBER,
    BAN_MEMBER,
    TOGGLE_RAIDMODE,
    UNMUTE;

    public static RixaPermission fromString(String string) {
        for (RixaPermission value : values()) {
            if (value.toString().equalsIgnoreCase(string)) {
                return value;
            }
        }
        return null;
    }
}
