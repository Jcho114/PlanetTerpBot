package gradle.planet.terp.scraper.bot.scraper;

import java.util.Collection;

public class ProfessorProfile {
	private String name;
	private double rating;
	private Collection<String> courses;
	
	public ProfessorProfile(String name, double rating, Collection<String> courses) {
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
	
	public Collection<String> getCourses() {
		return courses;
	}
	
	public String getCoursesAsString() {
		return getCourseList();
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
		if (!courses.isEmpty()) {
			int i = 1;
			for (Object course : courses) {
				result += course + " ";
				if (i++ % 5 == 0)
					result += "\n";
			}
		}
		return result;
	}
}
