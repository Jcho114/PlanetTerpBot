package gradle.planet.terp.scraper.bot.commands;

import java.time.Instant;

import discord4j.core.event.domain.interaction.ChatInputInteractionEvent;
import discord4j.core.object.command.ApplicationCommandInteractionOption;
import discord4j.core.object.command.ApplicationCommandInteractionOptionValue;
import discord4j.core.object.entity.Message;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.core.spec.InteractionReplyEditSpec;
import discord4j.rest.util.Color;
import gradle.planet.terp.scraper.bot.scraper.ProfessorProfile;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import gradle.planet.terp.scraper.bot.api.API;

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
		Mono<Message> result = event.deferReply().then(Mono.fromCallable(() -> {
			ProfessorProfile professorProfile;
			professorProfile = API.createProfessorProfile(professorName);
			return professorProfile;
		}))
		// Now each instance of the scraper creates a professor profile
		.flatMap(professorProfile -> {
			if (professorProfile.getName().equals("Invalid")) {
				EmbedCreateSpec errorEmbed = createErrorEmbed();
				InteractionReplyEditSpec errorEditSpec = InteractionReplyEditSpec.builder()
						.addEmbed(errorEmbed)
						.build();
				return event.editReply(errorEditSpec);
			}
			
			EmbedCreateSpec embed = createEmbed(professorProfile.getName(),
												professorProfile.getRating(),
												professorProfile.getCoursesAsString());
			InteractionReplyEditSpec editSpec = InteractionReplyEditSpec.builder()
					.addEmbed(embed)
					.build();
			
			// It then edits the original deferred reply to the
			// professorProfile
			return event.editReply(editSpec);
		})
		// subscribeOn is used in order to specify the scheduler
		// that the method runs on, which in this case is an
		// bounded elastic pool that avoids blocking the main
		// thread of the application
		.subscribeOn(Schedulers.boundedElastic());
		
		return result;
	}
	
	// Add url later
	private EmbedCreateSpec createEmbed(String professorName, double professorRating,
										String professorCourses) {
		EmbedCreateSpec embed = EmbedCreateSpec.builder()
				.color(Color.RED)
				.title(professorName)
				.addField("Rating", String.valueOf(professorRating) + "/5.00", false)
				.addField("Courses", professorCourses, false)
				.timestamp(Instant.now())
				.build();
		return embed;
	}
	
	private EmbedCreateSpec createErrorEmbed() {
		EmbedCreateSpec embed = EmbedCreateSpec.builder()
		.color(Color.RED)
		.title("Invalid Input")
		.timestamp(Instant.now())
		.build();
		return embed;
	}
}
