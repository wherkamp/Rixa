package io.rixa.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.rixa.bot.commands.cmds.admin.ConfigCommand;
import io.rixa.bot.commands.cmds.admin.PMCommand;
import io.rixa.bot.commands.cmds.general.*;
import io.rixa.bot.commands.cmds.moderator.BanCommand;
import io.rixa.bot.commands.cmds.moderator.ClearCommand;
import io.rixa.bot.commands.cmds.moderator.MuteCommand;
import io.rixa.bot.commands.cmds.other.ShutdownCommand;
import io.rixa.bot.commands.handler.CommandHandler;
import io.rixa.bot.commands.handler.CommandType;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.data.config.Configuration;
import io.rixa.bot.data.storage.DatabaseAdapter;
import io.rixa.bot.data.storage.enums.DatabaseTables;
import io.rixa.bot.events.BotJoinListener;
import io.rixa.bot.events.MessageListener;
import io.rixa.bot.events.ReadyListener;
import io.rixa.bot.events.UserListener;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.reactions.ReactManager;
import io.rixa.bot.reactions.react.HelpReaction;
import io.rixa.bot.user.RixaUser;
import io.rixa.bot.user.manager.UserManager;
import io.rixa.bot.utils.FileUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import lombok.Getter;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Game.GameType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.hooks.AnnotatedEventManager;

public class Rixa {

  private static Rixa instance;
  private static long timeUp;
  @Getter
  private CommandHandler commandHandler;
  @Getter
  private ReactManager reactManager;
  @Getter
  private Configuration configuration;
  @Getter
  private ObjectMapper objectMapper;
  @Getter
  private List<JDA> shardList;
  @Getter
  private File defaultPath;
  @Getter
  private Logger logger;

  private Rixa() {
    instance = this;
    this.logger = Logger.getLogger(Rixa.class.getCanonicalName());
    this.objectMapper = new ObjectMapper(new YAMLFactory());
    this.defaultPath = new File("Rixa/");
    this.commandHandler = new CommandHandler();
    this.reactManager = new ReactManager();
    this.shardList = new ArrayList<>();
    this.defaultPath.mkdirs();
    this.loadConfiguration();
    this.registerCommands();
    this.registerReactions();
    this.loadJDA();
  }

  public static Rixa getInstance() {
    return (instance == null) ? new Rixa() : instance;
  }

  public long getTimeUp() {
    return timeUp;
  }

  private void loadJDA() {
    JDABuilder jda = new JDABuilder(AccountType.BOT)
        .setToken(configuration.getToken())
        .setGame(Game.of(GameType.DEFAULT, configuration.getBotGame()))
        .setEventManager(new AnnotatedEventManager())
        .addEventListener(new ReadyListener(), new BotJoinListener(), new MessageListener(), new UserListener())
        .setAutoReconnect(true)
        .setAudioEnabled(true)
        .setEnableShutdownHook(false)
        .setStatus(OnlineStatus.ONLINE);
    for (int i = 0; i < configuration.getShards(); i++) {
      try {
        getLogger().info("Loading Shard #" + (i + 1) + "!");
        getShardList().add(jda.useSharding(i, configuration.getShards()).buildBlocking());
        getLogger().info("Shard #" + (i + 1) + " has been loaded");
      } catch (InterruptedException | LoginException e) {
        e.printStackTrace();
      }
    }
    Runtime.getRuntime().addShutdownHook(new Thread(() ->
        getShardList().forEach(jdaInstance -> {
          UserManager.getInstance().getUserMap().values().forEach(RixaUser::save);
          jdaInstance.getGuilds().forEach((Guild guild) -> {
            System.out.println("Saving " + guild.getName() + ": " + guild.getId());
            RixaGuild rixaGuild = GuildManager.getInstance().getGuild(guild);
            rixaGuild.save();
          });
          jdaInstance.shutdown();
        })));
    timeUp = System.currentTimeMillis();
  }

  private void registerReactions() {
    this.reactManager.registerReact(new HelpReaction("Help"));
  }

  private void registerCommands() {
    this.commandHandler.registerCommands(
            new AdviceCommand("advice", RixaPermission.NONE, "Receive advice from the great beyond...",
                CommandType.USER),

            new FeaturesCommand("features", RixaPermission.NONE, "List Rixa's official features!",
                CommandType.USER),

            new HelpCommand("help", RixaPermission.NONE, "Review commands and its usages!",
                CommandType.USER),

            new InfoCommand("info", RixaPermission.NONE, "Review information about a user or Rixa!",
                CommandType.USER),

            new MinecraftCommand("minecraft", RixaPermission.NONE, "See minecraft server info",
                CommandType.USER, Collections.singletonList("mc")),

            new ModulesCommand("modules", RixaPermission.NONE,
            "List both enabled & disabled features of Rixa for this server!", CommandType.USER),

            new MusicCommand("music", RixaPermission.NONE, "Listen to music right from discord!",
                CommandType.USER),

            new PingCommand("ping", RixaPermission.NONE, "Check Rixa's ping!",
                CommandType.USER),

            new MsbCommand("msb", RixaPermission.NONE, "Create a mocking Spongebob meme",
                    CommandType.USER),

            new NoworkCommand("nowork", RixaPermission.NONE, "For when you just have to bitch about Rixa",
                    CommandType.USER),

            new ServerInfoCommand("serverinfo", RixaPermission.NONE,
                    "Review information about the server!", CommandType.USER),

            new QuoteCommand("quote", RixaPermission.NONE,
                    "Receive a quote from some of the greatest authors!", CommandType.USER),

            new RankCommand("rank", RixaPermission.NONE, "Check your rank!", CommandType.USER),

            new LeaderboardsCommand("leaderboards", RixaPermission.NONE,
                    "Look at the levels leaderboards!", CommandType.USER),

            new YoutubeCommand("youtube", RixaPermission.NONE, "Search for music on youtube!",
                    CommandType.USER),

            new UrbanDictionaryCommand("ud", RixaPermission.NONE, "Look up urban definitions!",
                    CommandType.USER),

            new BanCommand("ban", RixaPermission.BAN_MEMBER, "Ban a player from your server.",
                    CommandType.STAFF),

            new ClearCommand("clear", RixaPermission.CLEAR_CHAT, "Clear Chat!", CommandType.STAFF,
                    Arrays.asList("deleemessages", "cmessages")),

            new ConfigCommand("config", RixaPermission.ACCESS_CONFIG, "Access the config menu",
                    CommandType.STAFF),

            new MuteCommand("mute", RixaPermission.MUTE, "Mute those pesky children!",
                    CommandType.STAFF),

            new PMCommand("pm", RixaPermission.PM_MESSAGE,
                    "Private Message all users with a specific role!", CommandType.STAFF),

            new RoleMemberList("listmembers", RixaPermission.NONE,
                    "List all users with a specific role!", CommandType.STAFF),

            new ShutdownCommand("shutdown", RixaPermission.NONE, "Shutdown Rixa!",
                    CommandType.OWNER));
  }

  private void loadConfiguration() {
    try {
      if (FileUtils.saveResource("config.yml", false)) {
        logger.info("Shutting down Rixa. Please edit configuration");
        System.exit(0);
      }
      File file = new File(defaultPath.getPath() + "/config.yml");
      configuration = objectMapper.readValue(file, Configuration.class);
      logger.info("Configuration successfully loaded.");
      DatabaseAdapter.getInstance().check();
      Arrays.stream(DatabaseTables.values()).forEach(databaseTables -> {
        System.out
            .println("Checking database table (creating if needed): " + databaseTables.toString());
        DatabaseAdapter.getInstance().get().update(databaseTables.getQuery());
        System.out.println("Done checking " + databaseTables.toString());
      });
    } catch (IOException e) {
      logger.severe("Could not properly load configuration file!.");
      e.printStackTrace();
    }
  }

  public Guild getGuildById(String id) {
    Guild guild = null;
    for (JDA jda : Rixa.getInstance().getShardList()) {
      if (jda.getGuilds().size() == 0 || jda.getGuildById(id) == null) {
        continue;
      }
      guild = jda.getGuildById(id);
      break;
    }
    if (guild != null) {
      return guild;
    }
    throw new NullPointerException("Guild not found.");
  }

  public void close() {
    shardList.forEach(JDA::shutdown);
  }
}