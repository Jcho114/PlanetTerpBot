package gradle.planet.terp.scraper.bot.commands;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import gradle.planet.terp.scraper.bot.scraper.PlanetTerpWebScraper;
import gradle.planet.terp.scraper.bot.scraper.ProfessorProfile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

// Fix to allow it to work with multiple calls at once
public class GetProfessorProfileCommand implements SlashCommand {
	@Override
	public String getName() {
		return "getprofessorprofile";
	}
	
	@Override
	public Mono<?> handle(ChatInputInteractionEvent event) {
		// The program first gets the professor name
		String professorName = event.getOption("professorname")
				.flatMap(ApplicationCommandInteractionOption::getValue)
				.map(ApplicationCommandInteractionOptionValue::asString)
				.get();
		
		// The function then defers the reply in order to extend the
		// response window of the bot
		return event.deferReply().then(Mono.fromCallable(() -> {
			// Mono.fromCallable is used to create instances of scraper
			// asynchronously
			System.out.println("Starting scraper");
			PlanetTerpWebScraper scraper;
			scraper = new PlanetTerpWebScraper(professorName);
			return scraper;
		}))
		// Now each instance of the scraper creates a professor profile
		.flatMap(scraper -> {
			System.out.println("Starting professor profile creation");
			if (scraper.hasError()) {
				System.out.println("Returning null checked-response");
				return event.editReply("Invalid input from user...");
			}
			System.out.println("Creating profile");
			ProfessorProfile professorProfile = scraper.createProfile();
			scraper.close();
			System.out.println("Returning response");
			// It then edits the original deferred reply to the
			// professorProfile
			return event.editReply(professorProfile.toString());
		})
		// subscribeOn is used in order to specify the scheduler
		// that the method runs on, which in this case is an
		// bounded elastic pool that avoids blocking the main
		// thread of the application
		.subscribeOn(Schedulers.boundedElastic());
	}
}
