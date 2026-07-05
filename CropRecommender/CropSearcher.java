package CropRecommender;

import java.util.*;
import java.nio.file.*;
import org.json.*;

public class CropSearcher {
    private String cropType, month, district;

    private final List<String> seasonOne = Arrays.asList("January", "February");
    private final List<String> seasonTwo = Arrays.asList("March", "April", "May");
    private final List<String> seasonThree = Arrays.asList("June", "July", "August", "September");
    private final List<String> seasonFour = Arrays.asList("October", "November", "December");

    public CropSearcher(String district, String cropType, String month) {
        this.cropType = cropType;
        this.district = district;

        if (seasonOne.contains(month)) this.month = "1";
        else if (seasonTwo.contains(month)) this.month = "2";
        else if (seasonThree.contains(month)) this.month = "3";
        else this.month = "4";
    }

    public String display() {
        try {
            // Read JSON file
            String path = "E:\\MiniProject\\src\\CropRecommender\\tn_districts_seasons_crops.json";
            String content = Files.readString(Paths.get(path));

            JSONObject object = new JSONObject(content);

            if (!object.has(district)) {
                return "District not found in data.";
            }

            JSONObject monthObj = object.getJSONObject(district).getJSONObject(month);

            if (!monthObj.has(cropType)) {
                return "Crop type not found for selected month.";
            }

            JSONArray crops = monthObj.getJSONArray(cropType);
            if (crops.length() == 0) {
                return "No crop recommendations found.";
            }

            // Format suggestions neatly
            StringBuilder suggestionBuilder = new StringBuilder("<html><center>Recommended crops:<br>");
            for (int i = 0; i < crops.length(); i++) {
                suggestionBuilder.append("• ").append(crops.getString(i)).append("<br>");
            }
            suggestionBuilder.append("</center></html>");

            return suggestionBuilder.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error reading crop data. Please check JSON file path or structure.";
        }
    }
}
