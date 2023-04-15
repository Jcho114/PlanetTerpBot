package gradle.planet.terp.scraper.bot;

import java.util.List;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import gradle.planet.terp.scraper.bot.listeners.SlashCommandListener;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import io.github.cdimascio.dotenv.Dotenv;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyBot {
	private static final Logger LOGGER = LoggerFactory.getLogger(MyBot.class);
	
	public static void main(String[] args) {
		// Accesses the discord token environment variable
		Dotenv dotenv = Dotenv.load();
		String discordToken = dotenv.get("DISCORD_TOKEN");
		
		// Creates gateway client and connects to the gateway
		final GatewayDiscordClient client = DiscordClientBuilder.create(discordToken).build()
			.login()
			.block();
		
		// Creates list of json command files
		List<String> commands = List.of("greet.json", "ping.json", "getprofessorprofile.json");
		
		// Registers commands
		try {
			new GlobalCommandRegistrar(client.getRestClient()).registerCommands(commands);
		} catch (Exception e) {
			LOGGER.error("Error trying to register commands", e);
		}
		
		// Registers slash command listener
		client.on(ChatInputInteractionEvent.class, SlashCommandListener::handle)
			.then(client.onDisconnect())
			.block(); // Program terminates if we do not use this method
	}
}
