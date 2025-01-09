/*
    Luminance
    Contributor(s): dannytaylor
    Github: https://github.com/MCLegoMan/Luminance
    Licence: GNU LGPLv3
*/

package com.mclegoman.luminance.client.update;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mclegoman.luminance.client.translation.Translation;
import com.mclegoman.luminance.common.data.Data;
import com.mclegoman.luminance.common.util.LogType;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

public class Update {
	public static JsonElement getModrinthData(String project_id, String request) {
		try {
			Data.version.sendToLog(LogType.INFO, "Getting data from Modrinth API...");
			URL url = Objects.equals(request, "") ? new URI("https://api.modrinth.com/v2/project/" + project_id).toURL() : new URI("https://api.modrinth.com/v2/project/" + project_id + "/" + request).toURL();
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			try {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				StringBuilder jsonContent = new StringBuilder();
				String line;
				while ((line = reader.readLine()) != null) {
					jsonContent.append(line);
				}
				return JsonParser.parseString(jsonContent.toString());
			} catch (Exception error) {
				Data.version.sendToLog(LogType.INFO, Translation.getString("Failed to read data from Modrinth API: {}", error));
			}
		} catch (Exception error) {
			Data.version.sendToLog(LogType.INFO, Translation.getString("Failed to get data from Modrinth API: {}", error));
		}
		return null;
	}
}
