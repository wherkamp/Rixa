package io.rixa.bot.commands.cmds.general;

import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.handler.CommandType;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.data.storage.DatabaseAdapter;
import io.rixa.bot.data.storage.enums.Statements;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.user.RixaUser;
import io.rixa.bot.user.manager.UserManager;
import io.rixa.bot.utils.DiscordUtils;
import io.rixa.bot.utils.MessageFactory;

import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

public class RankCommand extends Command {

    public RankCommand(String command, RixaPermission rixaPermission, String description, CommandType commandType) {
        super(command, rixaPermission, description, commandType);
    }

    @Override
    public void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) {
        RixaGuild rixaGuild = GuildManager.getInstance().getGuild(guild);
        if (!rixaGuild.getModule("Levels").isEnabled()) {
            MessageFactory.create("Levels are not enabled on this server!")
                    .setColor(member.getColor()).queue(channel);
            return;
        }
        if (args.length == 0) {
            getInfo(rixaGuild, member).queue(channel);
            return;
        }
        List<Member> members = DiscordUtils.memberSearch(guild, String.join(" ", args), false);
        if (members.isEmpty()) {
            MessageFactory.create("Could not find valid member! Please try again!").setColor(member.getColor()).queue(channel);
            return;
        }
        getInfo(rixaGuild, members.get(0)).queue(channel);
    }

    private MessageFactory getInfo(RixaGuild rixaGuild, Member member) {
        User author = member.getUser();
        int rank = 1;
        int count = DatabaseAdapter.getInstance().get().queryForObject("SELECT COUNT(*) FROM `levels`", Integer.class);
        if (count > 0) {
            rank = DatabaseAdapter.getInstance().get().queryForObject(
                    "SELECT FIND_IN_SET(`experience`, (SELECT GROUP_CONCAT(`experience` ORDER BY `experience` DESC) FROM `levels`)) AS `rank` FROM `levels` WHERE `guild_id` = ? AND `user_id` = ?",
                    new Object[]{member.getGuild().getId(), member.getUser().getId()}, Integer.class);
        }
        RixaUser rixaUser = UserManager.getInstance().getUser(member.getUser());
        int levels = rixaUser.getLevels(rixaGuild.getGuild().getId());
        return MessageFactory.create()
                .setAuthor(author.getName(), author.getEffectiveAvatarUrl(), author.getEffectiveAvatarUrl())
                .setTitle(author.getName() + "'s level")
                .setColor(member.getColor())
                .addField("Rank", String.valueOf(rank), true)
                .addField("Level", String.valueOf(DiscordUtils.getLevelFromExperience(rixaUser.getLevels(rixaGuild.getId()))), false)
                .addField("Exp Needed",
                        DiscordUtils.getRemainingExperience(levels) + "/" + DiscordUtils.getNeededXP
                                (DiscordUtils.getLevelFromExperience(levels)).intValue(), true)
                .addField("Total Exp", String.valueOf(levels), true);
    }
}
