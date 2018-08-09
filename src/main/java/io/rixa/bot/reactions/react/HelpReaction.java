package io.rixa.bot.reactions.react;

import io.rixa.bot.Rixa;
import io.rixa.bot.commands.handler.CommandType;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.reactions.React;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

public class HelpReaction extends React {

  public HelpReaction(String name) {
    super(name);
  }

  @SubscribeEvent
  public void onReact(MessageReactionAddEvent event) {
    if (event.getUser().isBot()) {
      return;
    }
    Message message = event.getChannel().getMessageById(event.getMessageId()).complete();
    this.execute(message, event.getReaction().getReactionEmote().getName(), event.getJDA());
  }

  private void execute(Message message, String emoteName, JDA jda) {
    MessageEmbed messageEmbed = message.getEmbeds().get(0);
    if (!messageEmbed.getTitle().contains(": ")) {
      return;
    }
    String title = messageEmbed.getTitle().split(": ")[1];
    RixaGuild rixaGuild = GuildManager.getInstance().getGuild(jda.getGuildById(title));
    if (rixaGuild == null) {
      return;
    }
    String prefix = rixaGuild.getSettings().getPrefix();
    EmbedBuilder embedBuilder;
    try {
      switch (emoteName) {
        case "\uD83D\uDDD1":// waste bin
          if (message.getAuthor().getId().equalsIgnoreCase(jda.getSelfUser().getId())) {
            message.delete().complete();
          }
          break;
        case "\u0031\u20E3":// one emoji
          embedBuilder = new EmbedBuilder();
          String stringBuilder = "\u2753" +
              " **General Commands Help**" +
              "\n" +
              "Click a number below for information about other commands.";
          embedBuilder.setTitle(String.format("Help: %s", title));
          embedBuilder.setDescription(stringBuilder);
          Rixa.getInstance().getCommandHandler().getAllCommands().values()
              .stream().filter(cmd -> cmd.getCommandType() == CommandType.USER)
              .forEach(cmd -> embedBuilder.addField(prefix + cmd.getCommand(),
                  cmd.getDescription(), false));
          message.editMessage(embedBuilder.build()).queue();
          break;
        case "\u0032\u20E3": // two emoji
          embedBuilder = new EmbedBuilder();
          stringBuilder = "\u2753" +
              " **Staff Commands Help**" +
              "\n" +
              "Click a number below for information about other commands.";
          embedBuilder.setTitle(String.format("Help: %s", title));
          embedBuilder.setDescription(stringBuilder);
          Rixa.getInstance().getCommandHandler().getAllCommands().values().stream()
              .filter(cmd -> cmd.getCommandType() == CommandType.STAFF)
              .forEach(cmd -> embedBuilder.addField(prefix + cmd.getCommand(),
                  cmd.getDescription(), false));
          message.editMessage(embedBuilder.build()).queue();
          break;
        case "\u0033\u20E3": // three emoji
          embedBuilder = new EmbedBuilder();
          stringBuilder = "\u2753" +
              " **Music Commands Help**" +
              "\n" +
              "Click a number below for information about other commands.";
          embedBuilder.setTitle(String.format("Help: %s", title));
          embedBuilder.setDescription(stringBuilder);
          embedBuilder.addField(prefix + "music join [name]",
              "Joins a voice channel that has the provided name", false)
              .addField(prefix + "music join [id]",
                  "Joins a voice channel based on the provided id.", false)
              .addField(prefix + "music leave",
                  "Leaves the voice channel that the bot is currently in.", false)
              .addField(prefix + "music play",
                  "Plays songs from the current queue. Starts playing again if it was previously paused",
                  false)
              .addField(prefix + "music play [url]",
                  "Adds a new song to the queue and starts playing if it wasn't playing already",
                  false)
              .addField(prefix + "music playlist",
                  "Adds a playlist to the queue and starts playing if not already playing", false)
              .addField(prefix + "music pause", "Pauses audio playback", false)
              .addField(prefix + "music stop",
                  "Completely stops audio playback, skipping the current song.", false)
              .addField(prefix + "music skip",
                  "Skips the current song, automatically starting the next", false)
              .addField(prefix + "music nowplaying",
                  "Prints information about the currently playing song (title, current time)",
                  false)
              .addField(prefix + "music np", "Alias for nowplaying", false)
              .addField(prefix + "music list", "Lists the songs in the queue", false)
              .addField(prefix + "music volume [vol]",
                  "Sets the volume of the MusicPlayer [10 - 100]", false)
              .addField(prefix + "music restart",
                  "Restarts the current song or restarts the previous song if there is no current song playing.",
                  false)
              .addField(prefix + "music repeat",
                  "Makes the player repeat the currently playing song", false)
              .addField(prefix + "music reset",
                  "Completely resets the player, fixing all errors and clearing the queue.", false)
              .addField(prefix + "music shuffle", "Shuffle current music queue.", false);
          message.editMessage(embedBuilder.build()).queue();
          break;
      }
    } catch (ErrorResponseException ignored) {
    }
  }
}
