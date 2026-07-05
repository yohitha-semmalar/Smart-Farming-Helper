package WeatherReport;

import java.io.*;
import javax.swing.*;
import java.awt.image.*;
import java.awt.*;
import javax.imageio.ImageIO;
import org.json.simple.JSONObject;
import LoginPage.*;

public class WeatherInfo extends JFrame {

    String[] districts = {
        "Ariyalur","Chengalpattu","Chennai","Coimbatore","Cuddalore","Dharmapuri",
        "Dindigul","Erode","Kallakurichi","Kancheepuram","Kanyakumari","Karur",
        "Krishnagiri","Madurai","Mayiladuthurai","Nagapattinam","Namakkal","Nilgiris",
        "Perambalur","Pudukottai","Ramanathapuram","Ranipet","Salem","Sivaganga",
        "Tenkasi","Thanjavur","Theni","Thoothukudi","Tiruchirappalli","Tirunelveli",
        "Tirupathur","Tiruppur","Tiruvallur","Tiruvannamalai","Tiruvarur","Vellore",
        "Viluppuram","Virudhunagar"
    };
    
    private JComboBox<String> districtDD;
    private JButton btnSubmit;
    private JButton backButton;
    private JLabel weatherConditionImage, temperatureText, weatherConditionDesc, humidityImage, humidityText, windImage, windText, jLabel1;
    private String username;

    public WeatherInfo(String uname) {
        username = uname;
        
        setTitle("Weather Info");
        setSize(350, 550);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initComponents();
        populateDistricts();
        addWeatherComponents();
    }

    private void initComponents() {
        // District label
        jLabel1 = new JLabel("District:");
        jLabel1.setBounds(30, 10, 70, 25);
        add(jLabel1);

        // District combo box
        districtDD = new JComboBox<>();
        districtDD.setBounds(100, 10, 180, 25);
        add(districtDD);

        // Submit button
        btnSubmit = new JButton("Submit");
        btnSubmit.setBounds(120, 40, 100, 30);
        btnSubmit.addActionListener(evt -> onSubmit());
        add(btnSubmit);

        // Back button
        backButton = new JButton("Back");
        backButton.setBounds(40, 450, 100, 30);
        backButton.addActionListener(e -> {
            this.dispose();
            HomePage hp = new HomePage(username);
            hp.setVisible(true);
        });
        add(backButton);
    }

    private void populateDistricts() {
        for (String district : districts) {
            districtDD.addItem(district);
        }
        districtDD.setSelectedIndex(0);
    }

    private void addWeatherComponents() {
        // Weather condition image
        weatherConditionImage = new JLabel(loadImage("E:\\MiniProject\\src\\WeatherReport\\assets\\cloudy.png"));
        weatherConditionImage.setBounds(75, 80, 200, 150);
        add(weatherConditionImage);

        // Temperature
        temperatureText = new JLabel("10°C", SwingConstants.CENTER);
        temperatureText.setBounds(0, 230, 350, 40);
        temperatureText.setFont(new Font("CooperBlack", Font.PLAIN, 36));
        add(temperatureText);

        // Weather description
        weatherConditionDesc = new JLabel("Cloudy", SwingConstants.CENTER);
        weatherConditionDesc.setBounds(0, 270, 350, 30);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(weatherConditionDesc);

        // Humidity
        humidityImage = new JLabel(loadImage("E:\\MiniProject\\src\\WeatherReport\\assets\\humidity.png"));
        humidityImage.setBounds(70, 310, 40, 40);
        add(humidityImage);

        humidityText = new JLabel("Humidity");
        humidityText.setBounds(120, 310, 200, 40);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 14));
        add(humidityText);

        // Wind
        windImage = new JLabel(loadImage("E:\\MiniProject\\src\\WeatherReport\\assets\\windy.png"));
        windImage.setBounds(70, 360, 40, 40);
        add(windImage);

        windText = new JLabel("Wind speed 15 km/h");
        windText.setBounds(120, 360, 200, 40);
        windText.setFont(new Font("Dialog", Font.PLAIN, 14));
        add(windText);
    }

    private ImageIcon loadImage(String path) {
        try {
            BufferedImage img = ImageIO.read(new File(path));
            return new ImageIcon(img);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void onSubmit() {
        String district = (String) districtDD.getSelectedItem();
        if (district == null) return;

        // Fetch weather data
        JSONObject weatherData = WeatherRetriever.getWeatherData(district);

        if (weatherData != null) {
            double temperature = ((Number) weatherData.getOrDefault("temperature", 0.0)).doubleValue();
            String condition = (String) weatherData.getOrDefault("weather_condition", "Unknown");
            long humidity = ((Number) weatherData.getOrDefault("humidity", 0)).longValue();
            double windspeed = ((Number) weatherData.getOrDefault("windspeed", 0.0)).doubleValue();

            temperatureText.setText(String.format("%.1f°C", temperature));
            weatherConditionDesc.setText(condition);

            // Update weather image
            switch (condition) {
                case "Cloudy":
                    weatherConditionImage.setIcon(loadImage("E:\\MiniProject\\src\\WeatherReport\\assets\\cloudy.png"));
                    break;
                case "Rain":
                    weatherConditionImage.setIcon(loadImage("E:\\MiniProject\\src\\WeatherReport\\assets\\rainy.png"));
                    break;
                case "Sunny":
                    weatherConditionImage.setIcon(loadImage("E:\\MiniProject\\src\\WeatherReport\\assets\\sunny.png"));
                    break;
                default:
                    weatherConditionImage.setIcon(loadImage("E:\\MiniProject\\src\\WeatherReport\\assets\\unknown.png"));
            }

            humidityText.setText("Humidity : " + humidity + "%");
            windText.setText("Wind speed : " + windspeed + " km/h");
        } else {
            JOptionPane.showMessageDialog(this, "Could not fetch weather for " + district);
        }
    }
}
