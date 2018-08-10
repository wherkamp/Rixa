package io.rixa.bot.commands;

import io.rixa.bot.commands.handler.CommandType;
import io.rixa.bot.commands.perms.RixaPermission;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;

public abstract class Command {

  @Getter @Setter private String command, description;
  @Getter @Setter private RixaPermission permission;
  @Getter @Setter private List<String> aliases;
  @Getter @Setter private CommandType commandType;

  public Command(String command) {
    this(command, RixaPermission.NONE, "Undefined", CommandType.USER, Collections.emptyList());
  }

  public Command(String command, RixaPermission rixaPermission) {
    this(command, rixaPermission, "Undefined", CommandType.USER, Collections.emptyList());
  }

  public Command(String command, RixaPermission rixaPermission, String description) {
    this(command, rixaPermission, description, CommandType.USER, Collections.emptyList());
  }

  public Command(String command, RixaPermission rixaPermission, String description, CommandType commandType) {
    this(command, rixaPermission, description, commandType, Collections.emptyList());
  }

  public Command(String command, RixaPermission rixaPermission, String description,
      CommandType commandType, List<String> aliases) {
    this.setCommand(command);
    this.setPermission(rixaPermission);
    this.setDescription(description);
    this.setAliases(aliases);
    this.setCommandType(commandType);
  }

  //   public abstract void execute(GuildMessageReceivedEvent event);

  public abstract void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) throws IOException;
}
