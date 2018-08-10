package io.rixa.bot.commands.cmds.admin;

import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.handler.CommandType;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.guild.modules.module.MusicModule;
import io.rixa.bot.pagination.Pagination;
import io.rixa.bot.utils.DiscordUtils;
import io.rixa.bot.utils.MessageFactory;
import java.util.Arrays;
import java.util.List;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import org.apache.commons.lang3.StringUtils;

public class ConfigCommand extends Command {

  private Pagination pagination;

  public ConfigCommand(String command, RixaPermission rixaPermission, String description, CommandType commandType) {
    super(command, rixaPermission, description, commandType);

    pagination = new Pagination(Arrays.asList(
            "%pconfig set greetings ; Set channel where greeting messages are announced!",
            "%pconfig set farewell ; Set channel where farewell messages are announced!",
            "%pconfig set prefix <prefix> ; Set Rixa's command prefix!",
            "%pconfig set defaultRole <role> ; Set role to be assigned when a user joins the server!",
            "%pconfig set muteRole <role> ; Set role to be assigned when a user is muted!",
            "%pconfig set musicRole <musicRole> ; Set role required to use the music functions! (Not required)",/*
            "%pconfig set twitterCKey <key> ; Set Twitter Consumer Key!",
            "%pconfig set twitterCSecret <key> ; Set Twitter Consumer Secret!",
            "%pconfig set twitterAToken <key> ; Set Twitter Access Key!",
            "%pconfig set twitterASecret <key> ; Set Twitter Access Secret!",
            "%config set twitterChannel ; Set the channel for Twitter feed updates!",*/
            "%pconfig set joinMessage <joinMessage> ; Set the greetings message for when a user joins the server!",
            "%pconfig set quitMessage <quitMessage> ; Set the quit message for when a user leaves the server!",
            "%pconfig set joinPm <joinPm> ; Set the message to be private messaged when a user joins!",
            "%pconfig set description <description> ; Set your server description!",
            "%pconfig addPerm <role> <permission> ; Give a role permission to access a command!",
            "%pconfig removePerm <role> <permission> ; Remove a role's permission to access a command!",
            "%pconfig enable <module> ; Enabled a Rixa Module!",
            "%pconfig disable <module> ; Disable a Rixa Module!"
    ), 6);
  }

  @Override
  public void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) {
    RixaGuild rixaGuild = GuildManager.getInstance().getGuild(guild);
    if (args.length == 2) {
      switch (args[0].toLowerCase()) {
        case "set":
          if (args[1].equalsIgnoreCase("greetings")) {
            rixaGuild.getSettings().setGreetings(channel);
            MessageFactory.create(channel.getAsMention()).setAuthor("Updated Greetings Channel",
                guild.getIconUrl()).setColor(member.getColor()).queue(channel);
            return;
          } else if (args[1].equalsIgnoreCase("farewell")) {
            rixaGuild.getSettings().setFarewell(channel);
            MessageFactory.create(channel.getAsMention()).setAuthor("Updated Farewell Channel",
                guild.getIconUrl()).setColor(member.getColor()).queue(channel);
            return;
          }
      }
    }
    if (args.length < 2) {
      sendHelp(member, 1, rixaGuild.getSettings().getPrefix());
      return;
    }
    String string = join(args, 2, args.length);
    Role role;
    switch (args[0].toLowerCase()) {
      case "set":
        if (args[1].equalsIgnoreCase("muteRole") ||
            args[1].equalsIgnoreCase("musicRole") ||
            args[1].equalsIgnoreCase("defaultRole")) {
          role = DiscordUtils.searchFirstRole(guild, string);
          if (role == null) {
            MessageFactory.create("Sorry I could not find that role!")
                .setColor(member.getColor()).setTimestamp().queue(channel);
            // Role not found
            return;
          }
          switch (args[1].toLowerCase()) {
            case "muterole":
              rixaGuild.getSettings().setMuteRole(role);
              break;
            case "musicrole":
              ((MusicModule) rixaGuild.getModule("Music")).setMusicRole(role);
              break;
            case "defaultrole":
              rixaGuild.getSettings().setDefaultRole(role);
              break;
          }
          // Role set
          MessageFactory.create(role.getAsMention()).setAuthor("Updated Role",
              guild.getIconUrl()).setColor(member.getColor()).queue(channel);
        } else if (args[1].equalsIgnoreCase("joinMessage")) {
          rixaGuild.getSettings().setJoinMessage(string);
          MessageFactory.create(string).setAuthor("Updated Join Message",
              guild.getIconUrl()).setColor(member.getColor()).queue(channel);
        } else if (args[1].equalsIgnoreCase("quitMessage")) {
          rixaGuild.getSettings().setQuitMessage(string);
          MessageFactory.create(string).setAuthor("Updated Quit Message",
              guild.getIconUrl()).setColor(member.getColor()).queue(channel);
        } else if (args[1].equalsIgnoreCase("joinPm")) {
          rixaGuild.getSettings().setJoinPrivateMessage(string);
          MessageFactory.create(string).setAuthor("Updated Join Private Message",
              guild.getIconUrl()).setColor(member.getColor()).queue(channel);
        } else if (args[1].equalsIgnoreCase("description")) {
          rixaGuild.setDescription(string);
          MessageFactory.create(string).setAuthor("Updated Guild Server Description",
              guild.getIconUrl()).setColor(member.getColor()).queue(channel);
        } else if (args[1].equalsIgnoreCase("prefix")) {
          rixaGuild.getSettings().setPrefix(string);
          MessageFactory.create(string).setAuthor("Updated Command Prefix",
              guild.getIconUrl()).setColor(member.getColor()).queue(channel);
        } else {
          sendHelp(member, 1, rixaGuild.getSettings().getPrefix());
          return;
        }
        break;
      case "enable":
        if (rixaGuild.isRegistered(args[1].toLowerCase())) {
          rixaGuild.getModule(args[1].toLowerCase()).setEnabled(true);
          MessageFactory.create(args[1].toLowerCase()).setAuthor("Module Enabled",
              guild.getIconUrl()).setColor(member.getColor()).queue(channel);
        } else if (args[1].equalsIgnoreCase("joinVerification")) {
          rixaGuild.getSettings().setJoinVerification(true);
          MessageFactory.create(args[1].toLowerCase()).setAuthor("Module Enabled",
              guild.getIconUrl()).setColor(member.getColor()).queue(channel);
        } else {
          MessageFactory.create(args[1].toLowerCase()).setAuthor("Module Not Found",
              guild.getIconUrl()).setColor(member.getColor()).queue(channel);
        }
        break;
      case "disable":
        if (rixaGuild.isRegistered(args[1])) {
          rixaGuild.getModule(args[1]).setEnabled(false);
          MessageFactory.create(args[1]).setAuthor("Module Disabled",
              guild.getIconUrl()).setColor(member.getColor()).queue(channel);
        } else if (args[1].equalsIgnoreCase("joinMessage")) {
          rixaGuild.getSettings().setJoinMessage("default_value");
          MessageFactory.create(args[1]).setAuthor("Module Disabled",
              guild.getIconUrl()).setColor(member.getColor()).queue(channel);
        } else if (args[1].equalsIgnoreCase("quitMessage")) {
          rixaGuild.getSettings().setQuitMessage("default_value");
          MessageFactory.create(args[1]).setAuthor("Module Disabled",
              guild.getIconUrl()).setColor(member.getColor()).queue(channel);
        } else if (args[1].equalsIgnoreCase("joinPrivateMessage")) {
          rixaGuild.getSettings().setJoinPrivateMessage("default");
          MessageFactory.create(args[1]).setAuthor("Module Disabled",
              guild.getIconUrl()).setColor(member.getColor()).queue(channel);
        } else if (args[1].equalsIgnoreCase("joinVerification")) {
          rixaGuild.getSettings().setJoinVerification(false);
          MessageFactory.create(args[1].toLowerCase()).setAuthor("Module Disabled",
              guild.getIconUrl()).setColor(member.getColor()).queue(channel);
        } else {
          MessageFactory.create(args[1].toLowerCase()).setAuthor("Module Not Found",
              guild.getIconUrl()).setColor(member.getColor()).queue(channel);
        }
        break;
      case "addperm":
      case "aperm":
      case "addpermission":
      case "addperms":
      case "addpermissions":
        RixaPermission permission = searchPerms(args);
        if (permission == null) {
          MessageFactory.create("That permission does not exist!").setColor(member.getColor())
              .queue(channel);
          return;
        }
        if (args[1].equalsIgnoreCase("role")) {
          role = DiscordUtils.searchFirstRole(guild, string);
          if (role == null) {
            MessageFactory.create("That role does not exist!").setColor(member.getColor())
                .queue(channel);
            return;
          }
          rixaGuild.addPermission(role.getId(), permission);
          MessageFactory.create("Role: " + role.getAsMention() + " | Permission: " +
              permission.toString()).setAuthor("Permission Given",
              guild.getIconUrl()).setColor(member.getColor()).queue(channel);
          return;
        }
        MessageFactory
            .create("Incorrect Usage! Try " + commandLabel + " addPerm <role> <permission>!")
            .setColor(member.getColor())
            .queue(channel);
        break;
      case "removeperm":
      case "rperm":
      case "removepermissions":
      case "removeperms":
        permission = searchPerms(args);
        if (permission == null) {
          MessageFactory.create("That permission does not exist!").setColor(member.getColor())
              .queue(channel);
          return;
        }
        if (args[1].equalsIgnoreCase("role")) {
          role = DiscordUtils.searchFirstRole(guild, string);
          if (role == null) {
            MessageFactory.create("That role does not exist!").setColor(member.getColor())
                .queue(channel);
            return;
          }
          rixaGuild.removePermission(role.getId(), permission);
          MessageFactory.create("Role: " + role.getAsMention() + " | Permission: " +
              permission.toString()).setAuthor("Permission Revoked",
              guild.getIconUrl()).setColor(member.getColor()).queue(channel);
          return;
        }
        MessageFactory
            .create("Incorrect Usage! Try " + commandLabel + " addPerm <role> <permission>!")
            .setColor(member.getColor())
            .queue(channel);
        break;
      default:
        sendHelp(member, 1, rixaGuild.getSettings().getPrefix());
        break;
    }
  }

  private void sendHelp(Member member, int page, String prefix) {
    List<Object> objects = pagination.getPage(page);
    MessageFactory messageFactory = MessageFactory.create("\u2699" + " **Config**" +
        "\nClick the back or forward reactions to switch between pages.")
        .setTitle(String.format("Config: %s", member.getGuild().getId()));
    objects.forEach(obj -> {
      String object = obj.toString();
      messageFactory.addField(object.split(" ; ")[0].replace("%p", prefix),
          object.split(" ; ")[1], false);
    });
    messageFactory.footer("Page: (" + page + " / " + (pagination.getMaxPage()) + ")",
        member.getGuild().getIconUrl())
        .setColor(member.getColor()).selfDestruct(0).send(member.getUser(), success ->
        success.addReaction("\u2B05").queue(v -> success.addReaction("\u27A1").queue()));
  }

  private RixaPermission searchPerms(String[] args) {
    for (String stringInArgs : args) {
      for (RixaPermission rixaPermission : RixaPermission.values()) {
        if (stringInArgs.equalsIgnoreCase(rixaPermission.toString())) {
          return rixaPermission;
        }
      }
    }
    return null;
  }

  private String join(Object[] obj, int startIndex, int endIndex) {
    return StringUtils.join(obj, " ", startIndex, endIndex);
  }
}
