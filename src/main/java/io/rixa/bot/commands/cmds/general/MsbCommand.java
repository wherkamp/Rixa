package io.rixa.bot.commands.cmds.general;

import io.rixa.bot.commands.Command;
import io.rixa.bot.commands.handler.CommandType;
import io.rixa.bot.commands.perms.RixaPermission;
import io.rixa.bot.user.RixaUser;
import io.rixa.bot.utils.MessageFactory;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MsbCommand extends Command {
    private Map<Member, Long> userMap = new HashMap<>();

    public MsbCommand(String command, RixaPermission rixaPermission, String description, CommandType commandType) {
        super(command, rixaPermission, description, commandType);
    }

    @Override
    public void execute(String commandLabel, Guild guild, Member member, TextChannel channel, String[] args) {
        if(!userMap.containsKey(member) || (System.currentTimeMillis() - userMap.get(member)) > 30000) {
            String str = String.join("_", args);
            MessageFactory.create("Mocking Spongebob").setImage("https://mockingspongebob.org/" + str + ".jpg").queue(channel);
            userMap.put(member, System.currentTimeMillis());
        }
        else{
            MessageFactory.create("The msb command is on a 30 second cooldown because you guys have no mercy.").selfDestruct(3).queue(channel);
        }
    }
}
