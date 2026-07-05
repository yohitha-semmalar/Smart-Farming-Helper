package CropRecommender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Arrays;
import LoginPage.*;

public class CropRecommender extends JFrame {
    private JPanel mainPanel, resultPanel;
    private JLabel result;
    private String username;

    private static final String[] districts = {
        "Ariyalur","Chengalpattu","Chennai","Coimbatore","Cuddalore","Dharmapuri",
        "Dindigul","Erode","Kallakurichi","Kancheepuram","Kanyakumari","Karur",
        "Krishnagiri","Madurai","Mayiladuthurai","Nagapattinam","Namakkal","Nilgiris",
        "Perambalur","Pudukottai","Ramanathapuram","Ranipet","Salem","Sivaganga",
        "Tenkasi","Thanjavur","Theni","Thoothukudi","Tiruchirappalli","Tirunelveli",
        "Tirupathur","Tiruppur","Tiruvallur","Tiruvannamalai","Tiruvarur","Vellore",
        "Viluppuram","Virudhunagar"
    };

    private static final String[] cropTypes = {"cereal","fruit","vegetable","cash","ornamental"};
    private static final String[] months = {
        "January","February","March","April","May","June","July","August",
        "September","October","November","December"
    };

    public CropRecommender(String uname) {
        setTitle("Crop Recommender");
        setSize(360, 580); // adjusted for your small screen
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        username = uname;

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(7, 2, 10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));

        JComboBox<String> districtDD = new JComboBox<>(districts);
        JComboBox<String> cropTypeDD = new JComboBox<>(cropTypes);
        JComboBox<String> monthDD = new JComboBox<>(months);

        JButton submitButton = new JButton("Get Recommendation");
        submitButton.setBackground(new Color(34, 139, 34));
        submitButton.setForeground(Color.WHITE);
        
        JButton backButton = new JButton("Back");

        resultPanel = new JPanel(new BorderLayout());
        resultPanel.setBorder(BorderFactory.createTitledBorder("Suggestions"));
        resultPanel.setPreferredSize(new Dimension(300, 120));

        result = new JLabel("<html><center>Fill the fields and click SUBMIT</center></html>", SwingConstants.CENTER);
        result.setFont(new Font("Segoe UI", Font.BOLD, 14));
        resultPanel.add(result, BorderLayout.CENTER);

        // Add components
        mainPanel.add(new JLabel("District:"));
        mainPanel.add(districtDD);
        mainPanel.add(new JLabel("Crop Type:"));
        mainPanel.add(cropTypeDD);
        mainPanel.add(new JLabel("Month:"));
        mainPanel.add(monthDD);
        mainPanel.add(new JLabel("")); // empty label for spacing
        mainPanel.add(submitButton);
        mainPanel.add(backButton); 

        add(mainPanel, BorderLayout.NORTH);
        add(resultPanel, BorderLayout.CENTER);

        submitButton.addActionListener(e -> {
            String districtSelected = (String) districtDD.getSelectedItem();
            String cropTypeSelected = (String) cropTypeDD.getSelectedItem();
            String monthSelected = (String) monthDD.getSelectedItem();

            try {
                String suggestions = new CropSearcher(districtSelected, cropTypeSelected, monthSelected).display();
                result.setText("<html><center>" + suggestions.replaceAll(" ", "<br>") + "</center></html>");
            } catch (Exception ex) {
                result.setText("<html><center>Error fetching crop data.<br>Please check file path or input.</center></html>");
            }

            resultPanel.revalidate();
            resultPanel.repaint();
        });
        
        backButton.addActionListener(e -> {
            this.dispose();  
            HomePage hp = new HomePage(uname);  
            hp.setVisible(true);
        });

        setVisible(true);
    }

  
}
