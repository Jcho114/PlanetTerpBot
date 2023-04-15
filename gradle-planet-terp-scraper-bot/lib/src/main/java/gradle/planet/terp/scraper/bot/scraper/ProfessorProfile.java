package gradle.planet.terp.scraper.bot.scraper;

import java.util.Set;

public class ProfessorProfile {
	private String name;
	private double rating;
	private Set<String> courses;
	
	public ProfessorProfile(String name, double rating, Set<String> courses) {
		this.name = name;
		this.rating = rating;
		this.courses = courses;
	}
	
	public String getName() {
		return name;
	}
	
	public double getRating() {
		return rating;
	}
	
	public Set<String> getCourses() {
		return courses;
	}
	
	@Override
	public String toString() {
		return "==============================================\n" +
			   "" + name + " Professor Profile:\n" +
			   "==============================================\n" +
			   "Rating: " + rating + "/5.00\n" +
			   "==============================================\n" +
			   "Course List:\n" + getCourseList() + "\n" +
			   "==============================================\n";
	}
	
	private String getCourseList() {
		String result = "";
		int i = 1;
		for (String course : courses) {
			result += course + " ";
			if (i++ % 5 == 0) {
				result += "\n";
			}
		}
		return result;
	}
}
