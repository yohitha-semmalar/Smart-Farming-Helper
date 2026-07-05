package WeatherReport;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WeatherRetriever {

    // Fetch weather data for a district/location
    public static JSONObject getWeatherData(String locationName) {
        try {
            JSONArray locationData = getLocationData(locationName);
            if (locationData == null || locationData.isEmpty()) return null;

            JSONObject location = (JSONObject) locationData.get(0);
            double latitude = (double) location.get("latitude");
            double longitude = (double) location.get("longitude");

            String urlString = "https://api.open-meteo.com/v1/forecast?" +
                    "latitude=" + latitude +
                    "&longitude=" + longitude +
                    "&hourly=temperature_2m,relativehumidity_2m,weathercode,windspeed_10m" +
                    "&timezone=Asia/Kolkata";

            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn.getResponseCode() != 200) return null;

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder resultJson = new StringBuilder();
            while (scanner.hasNext()) resultJson.append(scanner.nextLine());
            scanner.close();
            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(resultJson.toString());
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            JSONArray tempData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) tempData.get(index);

            JSONArray weatherCodeData = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long) weatherCodeData.get(index));

            JSONArray humidityData = (JSONArray) hourly.get("relativehumidity_2m");
            long humidity = (long) humidityData.get(index);

            JSONArray windData = (JSONArray) hourly.get("windspeed_10m");
            double windspeed = (double) windData.get(index);

            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("humidity", humidity);
            weatherData.put("windspeed", windspeed);

            return weatherData;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Get location latitude/longitude
    private static JSONArray getLocationData(String locationName) {
        try {
            locationName = locationName.replaceAll(" ", "+");
            String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
                    locationName + "&count=1&format=json";

            HttpURLConnection conn = fetchApiResponse(urlString);
            if (conn.getResponseCode() != 200) return null;

            Scanner scanner = new Scanner(conn.getInputStream());
            StringBuilder resultJson = new StringBuilder();
            while (scanner.hasNext()) resultJson.append(scanner.nextLine());
            scanner.close();
            conn.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultObj = (JSONObject) parser.parse(resultJson.toString());
            JSONArray locationData = (JSONArray) resultObj.get("results");

            return locationData;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper: connect to API
    private static HttpURLConnection fetchApiResponse(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();
        return conn;
    }

    // Get index of current hour
    private static int findIndexOfCurrentTime(JSONArray timeList) {
        String currentTime = getCurrentTime();
        for (int i = 0; i < timeList.size(); i++) {
            if (timeList.get(i).toString().equalsIgnoreCase(currentTime)) return i;
        }
        return 0; // fallback
    }

    private static String getCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");
        return now.format(formatter);
    }

    // Convert weather code to Sunny/Cloudy/Rain
    private static String convertWeatherCode(long code) {
        if (code == 0L) return "Sunny";
        else if (code > 0L && code <= 3L) return "Cloudy";
        else if ((code >= 51L && code <= 67L) || (code >= 80L && code <= 99L)) return "Rain";
        else return "Sunny"; // fallback
    }
}
