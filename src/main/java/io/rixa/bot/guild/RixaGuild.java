package io.rixa.bot.guild;

import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.data.storage.DatabaseAdapter;
import io.rixa.bot.guild.manager.IGuild;
import io.rixa.bot.guild.modules.RixaModule;
import io.rixa.bot.guild.modules.module.ConversationModule;
import io.rixa.bot.guild.modules.module.LevelsModule;
import io.rixa.bot.guild.modules.module.MusicModule;
import io.rixa.bot.guild.settings.Settings;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;

import java.util.*;
import java.util.stream.Collectors;

public class RixaGuild implements IGuild {

    @Getter private final Map<String, RixaModule> modules;
    @Getter private List<String> confirmationUsers;
    @Getter @Setter private List<String> keywords;
    @Getter @Setter private String description;
    @Getter private Map<String, List<RixaPermission>> permissionMap;
    @Getter private final String id;
    @Getter private Guild guild;
    @Getter private Settings settings;

    public RixaGuild(Guild guild) {
        this.guild = guild;
        id = guild.getId();
        modules = new HashMap<>();
        keywords = new ArrayList<>();
        confirmationUsers = new ArrayList<>();
        permissionMap = new HashMap<>();
        load();
    }

    @Override
    public void load() {
        settings = new Settings(this);
        if (!(DatabaseAdapter.getInstance().exists("modules", "guild_id", guild.getId()))) {
            DatabaseAdapter.getInstance().get().update
                ("INSERT INTO `modules` (`guild_id`, `levels`, `conversation`) VALUES (?, ?, ?);",
                    guild.getId(), false, true);
        }
        registerModules(
                new ConversationModule("conversation", "Have a conversation with Rixa!", this),
                new MusicModule("music", "Listen to music from within discord!", this),
                new LevelsModule("levels", "Levels for your discord server", this)
        );
    }

    @Override
    public void save() {
        modules.values().forEach(RixaModule::save);
        settings.save();
        permissionMap.keySet().forEach(object -> {
            List<RixaPermission> permissions = permissionMap.get(object);
            List<String> permission = new ArrayList<>();
            Arrays.stream(RixaPermission.values()).forEach(stringPermission ->
                    permission.add("`" + stringPermission.toString() + "`=" +
                    (permissions.contains(stringPermission) ? "'1'" : "'0'")));
            DatabaseAdapter.getInstance().get().update(
                    "UPDATE `permissions` SET " + String.join(", " + permission) +
                            " WHERE `guild_id` = ? AND `role_id` = ?",
                    guild.getId(), object);
        });
    }

    @Override
    public RixaModule getModule(String id) {
        return modules.get(id.toLowerCase());
    }

    private void registerModules(RixaModule... modules) {
        for (RixaModule module : modules) {
            registerModule(module);
        }
    }


    @Override
    public RixaModule registerModule(RixaModule module) {
        if (!(isRegistered(module.getName()))) modules.put(module.getName(), module);
        return module;
    }

    @Override
    public boolean isRegistered(String id) {
        return modules.containsKey(id.toLowerCase());
    }

    @Override
    //may god help whoever needs to edit this.
    public boolean hasPermission(User user, RixaPermission permission) {
        Member member = guild.getMember(user);
        return member != null && (!member.getRoles().stream().filter(role -> (permissionMap.containsKey(role.getId()) && permissionMap.get(role.getId()).contains(permission))).collect(Collectors.toList()).isEmpty());
    }

    @Override
    public void addPermission(String roleId, RixaPermission rixaPermission) {
        if (!permissionMap.containsKey(roleId)) {
            permissionMap.put(roleId, Collections.singletonList(rixaPermission));
            return;
        }
        List<RixaPermission> permissionsList = permissionMap.get(roleId);
        if (permissionsList.contains(rixaPermission)) return;
        permissionsList.add(rixaPermission);
        permissionMap.replace(roleId, permissionsList);
    }

    @Override
    public void removePermission(String roleId, RixaPermission rixaPermission) {
        if (!permissionMap.containsKey(roleId)) return;
        List<RixaPermission> permissionsList = permissionMap.get(roleId);
        if (!permissionsList.contains(rixaPermission)) return;
        permissionsList.remove(rixaPermission);
        permissionMap.replace(roleId, permissionsList);
    }
}
