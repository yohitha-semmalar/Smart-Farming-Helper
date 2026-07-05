package IrrigationAndFertilizerAdvisor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;
import LoginPage.*;

public class FertilizerAdvisor extends JFrame {

    private JComboBox<String> cropDropdown;
    private JTextArea adviceArea;
    private JSONObject cropsData;
    private String username;

    public FertilizerAdvisor(String uname) {
        super("Fertilizer Advisory");
        setSize(500, 450);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        username = uname;

        // Load JSON
        try {
            String content = new String(Files.readAllBytes(Paths.get("E:\\MiniProject\\src\\IrrigationAndFertilizerAdvisor\\adviceSet.json")));
            cropsData = new JSONObject(content);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading JSON: " + e.getMessage());
            e.printStackTrace();
        }

        // Dropdown
        cropDropdown = new JComboBox<>();
        cropDropdown.setBounds(20, 20, 200, 30);
        add(cropDropdown);

        // Back button
        JButton backButton = new JButton("Back");
        backButton.setBounds(370, 20, 90, 30);
        add(backButton);

        // Fill dropdown
        for (String category : cropsData.keySet()) {
            JSONObject categoryObj = cropsData.getJSONObject(category);
            for (String crop : categoryObj.keySet()) {
                cropDropdown.addItem(category + " -> " + crop);
            }
        }

        // Advice area
        adviceArea = new JTextArea();
        adviceArea.setBounds(20, 70, 440, 300);
        adviceArea.setLineWrap(true);
        adviceArea.setWrapStyleWord(true);
        adviceArea.setEditable(false);
        adviceArea.setFont(new Font("Dialog", Font.PLAIN, 14));
        add(adviceArea);

        // Show advice
        cropDropdown.addActionListener(e -> showAdvice());
        if (cropDropdown.getItemCount() > 0)
            cropDropdown.setSelectedIndex(0);

        // Back action
        backButton.addActionListener(e -> {
            this.dispose();
            HomePage hp = new HomePage(uname);
            hp.setVisible(true);
        });
    }

    private void showAdvice() {
    String selected = (String) cropDropdown.getSelectedItem();
    if (selected == null) return;

    String[] parts = selected.split(" -> ");
    String category = parts[0];
    String crop = parts[1];

    JSONObject cropObj = cropsData.getJSONObject(category).getJSONObject(crop);
    JSONObject fertilizer = cropObj.getJSONObject("fertilizer");

    StringBuilder sb = new StringBuilder();
    sb.append("Crop: ").append(crop).append("\n\n");

    sb.append("Nitrogen: ").append(fertilizer.optString("nitrogen", "N/A")).append("\n");
    sb.append("Phosphorus: ").append(fertilizer.optString("phosphorus", "N/A")).append("\n");
    sb.append("Potassium: ").append(fertilizer.optString("potassium", "N/A")).append("\n");
    sb.append("Organic: ").append(fertilizer.optString("organic", "N/A")).append("\n\n");

    sb.append("Application Schedule:\n");
    if (fertilizer.has("application_schedule")) {
        for (int i = 0; i < fertilizer.getJSONArray("application_schedule").length(); i++) {
            sb.append(" - ").append(fertilizer.getJSONArray("application_schedule").getString(i)).append("\n");
        }
    }

    adviceArea.setText(sb.toString());
}

}
