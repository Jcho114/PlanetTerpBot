package gradle.planet.terp.scraper.bot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import reactor.core.publisher.Mono;

public class PingCommand implements SlashCommand{
	@Override
	public String getName() {
		return "ping";
	}
	
	@Override
	public Mono<Void> handle(ChatInputInteractionEvent event) {
		return event.reply()
			.withEphemeral(true)
			.withContent("Pong!");
	}
}
