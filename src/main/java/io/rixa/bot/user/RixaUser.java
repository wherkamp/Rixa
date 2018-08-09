package io.rixa.bot.user;

import io.rixa.bot.data.storage.DatabaseAdapter;
import io.rixa.bot.utils.DiscordUtils;
import lombok.Getter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class RixaUser {

    @Getter
    private User user;
    @Getter
    private Map<String, Integer> levels;
    @Getter
    private long last_awarded;

    public RixaUser(User user) {
        this.user = user;
        levels = new HashMap<>();
        last_awarded = (System.currentTimeMillis() - 60000);
        load();
    }

    private void load() {
        int count = DatabaseAdapter.getInstance().get().queryForObject
                ("SELECT COUNT(*) FROM `levels` WHERE `user_id` = ?", new Object[] { user.getId() },  Integer.class);
        if (count > 0) {
            DatabaseAdapter.getInstance().get().queryForObject("SELECT * FROM `levels` WHERE `user_id` = ?",
                    new Object[]{user.getId()},
                    (resultSet, i) -> {
                        resultSet.beforeFirst();
                        while (resultSet.next()) {
                            levels.put(resultSet.getString("guild_id"), resultSet.getInt("experience"));
                        }
                        return 0;
                    });
        }
    }

    public void save() {
        levels.forEach((guildId, integer) -> {
            int i = DatabaseAdapter.getInstance().get().queryForObject
                    ("SELECT COUNT(*) FROM `levels` WHERE `guild_id` = ? AND `user_id` = ?", new Object[]{
                            guildId, user.getId()
                    }, Integer.class);
            if (i > 0) {
                DatabaseAdapter.getInstance().get().update(
                        "UPDATE `levels` SET `experience` = ? WHERE `guild_id` = ? AND `user_id` = ?", integer, guildId,
                        user.getId());
                return;
            }
            DatabaseAdapter.getInstance().get().update
                    ("INSERT INTO `levels` (guild_id, user_id, experience) VALUES (?, ?, ?);", guildId, user.getId(), integer);
        });
    }

    public boolean awardIfCan(Guild guild) {
        long b = ((System.currentTimeMillis() - last_awarded) / 1000);
        if (b < 60) {
            return false;
        }
        int amountAdding = ThreadLocalRandom.current().nextInt(15, 25);
        int exp = levels.getOrDefault(guild.getId(), 0);
        int currentLevel = DiscordUtils.getLevelFromExperience(exp);
        if (levels.containsKey(guild.getId())) {
            levels.replace(guild.getId(), exp + amountAdding);
        } else {
            levels.put(guild.getId(), exp + amountAdding);
        }
        this.last_awarded = System.currentTimeMillis();
        return currentLevel < DiscordUtils.getLevelFromExperience(levels.get(guild.getId()));
    }

    public int getLevels(String id) {
        return levels.getOrDefault(id, 0);
    }
}
