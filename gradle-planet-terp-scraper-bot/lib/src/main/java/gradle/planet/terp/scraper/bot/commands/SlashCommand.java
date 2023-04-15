package gradle.planet.terp.scraper.bot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public interface SlashCommand {
	String getName();
	
	public Mono<?> handle(ChatInputInteractionEvent event);
}
