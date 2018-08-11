package io.rixa.bot.reactions.react;

import io.rixa.bot.commands.cmds.general.LeaderboardsCommand;
import io.rixa.bot.guild.RixaGuild;
import io.rixa.bot.guild.manager.GuildManager;
import io.rixa.bot.guild.modules.module.LevelsModule;
import io.rixa.bot.reactions.React;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.core.exceptions.ErrorResponseException;
import net.dv8tion.jda.core.hooks.SubscribeEvent;

import java.util.List;

public class LeaderboardsReaction extends React {

    public LeaderboardsReaction(String name) {
        super(name);
    }

    @SubscribeEvent
    public void onReact(MessageReactionAddEvent event) {
        if (event.getUser().isBot()) {
            return;
        }
        Message message = event.getChannel().getMessageById(event.getMessageId()).complete();
        this.execute(message, event.getReaction().getReactionEmote().getName(), event.getJDA(), event.getGuild(), event.getMember());
    }

    private void execute(Message message, String emoteName, JDA jda, Guild guild, Member member) {
        MessageEmbed messageEmbed = message.getEmbeds().get(0);
        if (!messageEmbed.getTitle().contains(": ")) {
            return;
        }
        String title = messageEmbed.getTitle().split(": ")[1];
        List<Guild> g = jda.getGuildsByName(title, false);
        RixaGuild rixaGuild = GuildManager.getInstance().getGuild(g.get(0));
        if (rixaGuild == null) {
            return;
        }
        EmbedBuilder embedBuilder;
        LevelsModule levelsModule = (LevelsModule) rixaGuild.getModule("Levels");
        String str = messageEmbed.getFooter().getText();
        String[] temp = str.substring(str.indexOf("(")+1,str.indexOf(")")).split(" / ");
        int page = Integer.valueOf(temp[0]);
        int max = levelsModule.getObjectPagination().getMaxPage();
        try {
            switch (emoteName) {
                case "\u2B05": //Arrow left
                    //Previous page
                    embedBuilder = new EmbedBuilder();
                    if(page > 1){
                        page = page - 1;
                    }
                    List<String> l1 = LeaderboardsCommand.getLeaderboard(rixaGuild, page);
                    embedBuilder.setTitle("Leaderboard: " + guild.getName(), guild.getIconUrl());
                    embedBuilder.setDescription(l1.isEmpty() ? "No users found!" : (String.join("\n", l1)));
                    embedBuilder.setColor(member.getColor());
                    embedBuilder.setFooter("Page: (" + page + " / " + levelsModule.getObjectPagination().getMaxPage() + ")", member.getGuild().getIconUrl());
                    message.editMessage(embedBuilder.build()).queue();
                    break;
                case "\u27A1": //Arrow right
                    //Next page
                    embedBuilder = new EmbedBuilder();
                    if(page < max){
                        page = page + 1;
                    }
                    List<String> l2 = LeaderboardsCommand.getLeaderboard(rixaGuild, page);
                    embedBuilder.setTitle("Leaderboard: " + guild.getName(), guild.getIconUrl());
                    embedBuilder.setDescription(l2.isEmpty() ? "No users found!" : (String.join("\n", l2)));
                    embedBuilder.setColor(member.getColor());
                    embedBuilder.setFooter("Page: (" + page + " / " + levelsModule.getObjectPagination().getMaxPage() + ")", member.getGuild().getIconUrl());
                    message.editMessage(embedBuilder.build()).queue();
                    break;
            }
        } catch (ErrorResponseException ignored) {
        }
    }
}
