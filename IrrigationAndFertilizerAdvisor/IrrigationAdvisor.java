package IrrigationAndFertilizerAdvisor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.json.JSONObject;
import LoginPage.*;

public class IrrigationAdvisor extends JFrame {

    private JComboBox<String> cropDropdown;
    private JTextArea adviceArea;
    private JSONObject cropsData;
    private String username;

    public IrrigationAdvisor(String uname) {
        super("Irrigation Advisory");
        setSize(520, 480);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null); // Center window
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        username = uname;

        // Load JSON file
        try {
            String content = new String(Files.readAllBytes(Paths.get("E:\\MiniProject\\src\\IrrigationAndFertilizerAdvisor\\adviceSet.json")));
            cropsData = new JSONObject(content);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error loading JSON: " + e.getMessage());
            e.printStackTrace();
        }

        // Title
        JLabel title = new JLabel("Irrigation Advisory");
        title.setFont(new Font("Dialog", Font.BOLD, 22));
        title.setBounds(150, 10, 300, 40);
        add(title);

        // Crop label and dropdown
        JLabel cropLabel = new JLabel("Select Crop:");
        cropLabel.setBounds(20, 60, 100, 25);
        add(cropLabel);

        cropDropdown = new JComboBox<>();
        cropDropdown.setBounds(120, 60, 250, 30);
        add(cropDropdown);

        // Back button
        JButton backButton = new JButton("Back");
        backButton.setBounds(400, 60, 80, 30);
        add(backButton);

        // Fill dropdown with crops
        if (cropsData != null) {
            for (String category : cropsData.keySet()) {
                JSONObject categoryObj = cropsData.getJSONObject(category);
                for (String crop : categoryObj.keySet()) {
                    cropDropdown.addItem(category + " -> " + crop);
                }
            }
        }

        // Advice area
        adviceArea = new JTextArea();
        adviceArea.setBounds(20, 110, 460, 300);
        adviceArea.setLineWrap(true);
        adviceArea.setWrapStyleWord(true);
        adviceArea.setEditable(false);
        adviceArea.setFont(new Font("Dialog", Font.PLAIN, 14));

        JScrollPane scrollPane = new JScrollPane(adviceArea);
        scrollPane.setBounds(20, 110, 460, 300);
        add(scrollPane);

        // Dropdown action
        cropDropdown.addActionListener(e -> showAdvice());

        // Show first crop advice by default
        if (cropDropdown.getItemCount() > 0)
            cropDropdown.setSelectedIndex(0);

        // Back button action
        backButton.addActionListener(e -> {
            this.dispose();
            HomePage hp = new HomePage(username);
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
        JSONObject irrigation = cropObj.getJSONObject("irrigation");

        StringBuilder sb = new StringBuilder();
        sb.append("Crop: ").append(crop).append("\n\n");
        sb.append("Irrigation Method: ").append(irrigation.getString("method")).append("\n");
        sb.append("Frequency: ").append(irrigation.getString("frequency")).append("\n");
        sb.append("Water Requirement: ").append(irrigation.getString("water_requirement")).append("\n");
        sb.append("Critical Stages: ").append(irrigation.getString("critical_stages")).append("\n");
        sb.append("Tips: ").append(irrigation.getString("tips")).append("\n");

        adviceArea.setText(sb.toString());
    }
}
