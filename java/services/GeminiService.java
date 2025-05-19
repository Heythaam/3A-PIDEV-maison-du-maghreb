package tn.esprit.services;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GeminiService {
    public static String getGeminiResponse(String prompt) {
        try {
            ProcessBuilder builder = new ProcessBuilder(
                    prompt
            );
            builder.redirectErrorStream(true);
            Process process = builder.start();

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream())
            );
            StringBuilder output = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            process.waitFor();
            return output.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: Could not get response from Gemini.";
        }
    }
}
