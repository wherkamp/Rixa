package io.rixa.bot.guild.modules.module;

import io.rixa.bot.data.storage.DatabaseAdapter;
import io.rixa.bot.data.storage.enums.Statements;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.modules.RixaModule;
import io.rixa.bot.pagination.ObjectPagination;
import io.rixa.bot.user.RixaUser;
import io.rixa.bot.user.manager.UserManager;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LevelsModule implements RixaModule {

    @Getter
    private String name, description;
    @Getter
    @Setter
    boolean enabled;
    @Getter
    private RixaGuild guild;
    @Getter
    private ObjectPagination objectPagination;

    public LevelsModule(String name, String description, RixaGuild rixaGuild) {
        this.name = name;
        this.description = description;
        this.guild = rixaGuild;
        load();
    }

    @Override
    public void load() {
        // SELECT * FROM `levels` ORDER BY `experience` DESC LIMIT 100, 50;
        setEnabled(DatabaseAdapter.getInstance().get().queryForObject
                (Statements.SELECT_MODULE_STATUS.getStatement("{module_name}", getName()),
                        new Object[]{guild.getId()}, (resultSet, i) -> resultSet.getBoolean(getName())));
        reload();
    }

    @Override
    public void save() {
        DatabaseAdapter.getInstance().get().update("UPDATE `modules` SET `" + name + "` = ? WHERE `guild_id` = ?;", enabled, guild.getId());
    }

    @Override
    public void reload() {
        if (!isEnabled()) return;
        UserManager.getInstance().getUserMap().values().forEach(RixaUser::save);
        int count = DatabaseAdapter.getInstance().get().queryForObject
                ("SELECT COUNT(*) FROM `levels`", Integer.class);
        if (count == 0) {
            objectPagination = new ObjectPagination(Collections.emptyList(), 10);
            return;
        }
        List<Object> expList = DatabaseAdapter.getInstance().get().queryForObject
                (Statements.SELECT_ALL_FROM_TABLE.getStatement("{table_name}", "levels")+" ORDER BY `experience` DESC",
                        new Object[]{guild.getId()}, (resultSet, i) -> {
                            List<Object> list = new ArrayList<>();
                            resultSet.beforeFirst();
                            while (resultSet.next()) {
                                list.add(resultSet.getString("user_id") + ":" +
                                        resultSet.getInt("experience"));
                            }
                            return list;
                        });
        if (objectPagination == null)
            objectPagination = new ObjectPagination(expList, 10);
        else
            objectPagination.updateList(expList);
    }
}