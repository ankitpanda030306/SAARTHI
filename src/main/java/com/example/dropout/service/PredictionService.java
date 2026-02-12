package com.example.dropout.service;

import org.springframework.stereotype.Service;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Service
public class PredictionService {

    public String getPrediction(double attendance, double mathScore, int income, int internet, int education) {
        try {
            // 1. Build JSON securely
            String jsonInput = String.format(
                "{\"Attendance\": %f, \"Math_Score\": %f, \"Parent_Income\": %d, \"Has_Internet\": %d, \"Parent_Education\": %d}",
                attendance, mathScore, income, internet, education
            );

            System.out.println("Sending to Python: " + jsonInput); // Debug print

            // 2. Setup Client with Timeout (prevents hanging)
            HttpClient client = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(3))
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("http://127.0.0.1:5000/predict"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                    .build();

            // 3. Send and wait for response
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Python Response: " + response.body()); // Debug print
            return response.body();

        } catch (Exception e) {
            // This prints the REAL error to your VS Code Terminal
            e.printStackTrace(); 
            // This sends the REAL error to the browser popup (instead of "null")
            return "Error: " + e.toString(); 
        }
    }
}