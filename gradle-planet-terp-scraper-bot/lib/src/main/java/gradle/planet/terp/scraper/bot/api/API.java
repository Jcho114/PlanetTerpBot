package gradle.planet.terp.scraper.bot.api;

import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Scanner;
import java.net.URL;
import java.net.HttpURLConnection;
import org.json.JSONArray;
import org.json.JSONObject;
import gradle.planet.terp.scraper.bot.scraper.ProfessorProfile;

public class API {
	public static ProfessorProfile createProfessorProfile(String professorName) throws Exception {
		ProfessorProfile professorProfile = null;
		
		try {
			professorName = professorName.replace(" ", "%20");
			URL url = new URL("https://planetterp.com/api/v1/professor?name=" + professorName);

			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.connect();

			int responseCode = conn.getResponseCode();

			if (responseCode != 200) {
				throw new RuntimeException("HttpResponseCode: " + responseCode);
			} else {
				StringBuilder informationString = new StringBuilder();
				Scanner scan = new Scanner(url.openStream());

				while (scan.hasNext()) {
					informationString.append(scan.nextLine());
				}

				scan.close();

				String toParse = new String(informationString);
				JSONObject professorData = new JSONObject(toParse);
				
				Collection<String> courses = new ArrayList<>();
				Iterator<Object> iterator = ((JSONArray) professorData.get("courses")).iterator();
				
				while(iterator.hasNext()) {
					courses.add(String.valueOf(iterator.next()));
				}
				
				professorProfile = new ProfessorProfile(professorData.getString("name"),
														professorData.getDouble("average_rating"),
														courses);
			}
		} catch (Exception e) {
			professorProfile = new ProfessorProfile("Invalid", 0.0, null);
		}
		
		return professorProfile;
	}
}
