package io.rixa.bot.commands.cmds.general;

import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.handler.CommandType;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.time.format.DateTimeFormatter;

public class ServerInfoCommand extends Command {

    public ServerInfoCommand(String command, RixaPermission rixaPermission, String description, CommandType commandType) {
        super(command, rixaPermission, description, commandType);
    }

    @Override
    public void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) {
        RixaGuild rixaGuild = GuildManager.getInstance().getGuild(guild);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        User owner = guild.getOwner().getUser();
        MessageFactory
                .create(rixaGuild.getDescription())
                .setTitle(guild.getName())
                .addField("Created", guild.getCreationTime().format(formatter), true)
                .addField("Region", guild.getRegion().toString(), true)
                .addField("Users", String.valueOf(guild.getMembers().size()), true)
                .addField("Channel Categories", String.valueOf(guild.getCategories().size()), true)
                .addField("Text Channels", String.valueOf(guild.getTextChannels().size()), true)
                .addField("Voice Channels", String.valueOf(guild.getVoiceChannels().size()), true)
                .addField("Verification Level", guild.getVerificationLevel().toString(), true)
                .addField("Roles", String.valueOf(guild.getRoles().size()), true)
                .addField("Owner", owner.getName() + "#" + owner.getDiscriminator(), true)
                .addField("Enlisted", String.valueOf(true), true)
                .setThumbnail(guild.getIconUrl())
                .footer("Server Id: " + guild.getId(), guild.getIconUrl())
                .queue(channel);
    }
}