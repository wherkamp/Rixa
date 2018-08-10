package io.rixa.bot.events;

import com.mysql.jdbc.StringUtils;
import io.rixa.bot.Rixa;
import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.exceptions.CommandNotFoundException;
import io.rixa.bot.commands.exceptions.ReactNotFoundException;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.guild.modules.module.ConversationModule;
import io.rixa.bot.reactions.React;
import io.rixa.bot.reactions.react.HelpReaction;
import io.rixa.bot.user.RixaUser;
import io.rixa.bot.user.manager.UserManager;
import io.rixa.bot.utils.DiscordUtils;
import io.rixa.bot.utils.MessageFactory;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class MessageListener {

  @SubscribeEvent
  public void onReactionAdded(MessageReactionAddEvent event) {
    if (event.getUser().isBot()) {
      return;
    }
    event.getChannel().getMessageById(event.getMessageId()).queue(message -> {
      React react = this.check(message);
      if (react != null) {
        react.onReact(event);
      }
    });
  }

  private React check(Message message) {
      if (message == null || message.getEmbeds().isEmpty()) {
        return null;
      }
      MessageEmbed messageEmbed = message.getEmbeds().get(0);
      if (StringUtils.isNullOrEmpty(messageEmbed.getTitle())) {
        return null;
      }
      String[] titleSplit = messageEmbed.getTitle().split(": ");
      if (titleSplit[0].equalsIgnoreCase("Leaderboard")) {
        return null; // Not sure if this is required anymore.
      }
      try {
        React react = Rixa.getInstance().getReactManager().getReaction(titleSplit[0]);
        if (react != null) {
          return react;
        }
      } catch (ReactNotFoundException ignored) {
      }
      return null;
  }

  @SubscribeEvent
  public void onMessage(GuildMessageReceivedEvent event) {
    if (event.getAuthor().isBot()) {
      return;
    }
    String message = event.getMessage().getContentRaw().trim();
    RixaGuild rixaGuild = GuildManager.getInstance().getGuild(event.getGuild());
    if (event.getMessage().getContentDisplay()
        .startsWith("@" + event.getGuild().getSelfMember().getEffectiveName())) {
      String chat = event.getMessage().getContentDisplay();
      chatter(rixaGuild, event.getChannel(),
          chat.replace("@" + event.getGuild().getSelfMember().getEffectiveName(), ""));
      return;
    }
    String prefix = rixaGuild.getSettings().getPrefix();
    if (message.startsWith(prefix)) {
      String[] msgArgs = message.split(" ");
      String commandName = (message.contains(" ") ? msgArgs[0] : message);
      String[] args = new String[msgArgs.length - 1];
      System.arraycopy(msgArgs, 1, args, 0, msgArgs.length - 1);
      command(commandName, prefix, event, args);
      return;
    }
    RixaUser rixaUser = UserManager.getInstance().getUser(event.getAuthor());
    if (rixaUser.awardIfCan(event.getGuild())) {
      int level = DiscordUtils.getLevelFromExperience(rixaUser.getLevels(rixaGuild.getId()));
      MessageFactory
          .create(event.getAuthor().getAsMention() + " has leveled up to **level " + level + "**!")
          .setTimestamp()
          .setColor(event.getMember().getColor())
          .setAuthor("Leveled Up!", null, event.getAuthor().getAvatarUrl())
          .footer("Rixa Levels", event.getJDA().getSelfUser().getAvatarUrl())
          .queue(event.getChannel());
    }
  }

  private void command(String commandName, String prefix, GuildMessageReceivedEvent event, String[] args) {
    commandName = commandName.replaceFirst(prefix, "");
    try {
      Command command = Rixa.getInstance().getCommandHandler().getCommand(commandName);
      RixaGuild rixaGuild = GuildManager.getInstance().getGuild(event.getGuild());
      if (!event.getGuild().getOwner().getUser().getId().equalsIgnoreCase(event.getAuthor().getId())
          && command.getPermission() != null && command.getPermission() != RixaPermission.NONE &&
          (!rixaGuild.hasPermission(event.getMember().getUser(), command.getPermission()))
          && (!Rixa.getInstance().getConfiguration().isBotAdmin(event.getAuthor().getId()))) {
        MessageFactory.create("Sorry! You do not have permission for this command!")
            .setColor(event.getMember().getColor()).queue(event.getChannel());
        return;
      }
      command.execute(commandName, event.getGuild(), event.getMember(), event.getChannel(), args);
    } catch (CommandNotFoundException | IOException ignored) {
    }
  }

  private void chatter(RixaGuild rixaGuild, TextChannel channel, String message) {
    ConversationModule conversationModule = (ConversationModule) rixaGuild
        .getModule("Conversation");
    if (!conversationModule.isEnabled()) {
      return;
    }
    try {
      MessageFactory.sendPlainMessage(conversationModule.getChatBotSession().think(message), channel);
    } catch (Exception ignored) {
    }
  }
}
