package com.ai.emailgenerator.service;

import com.ai.emailgenerator.dto.EmailRequest;
import com.ai.emailgenerator.dto.EmailResponse;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
public class EmailService {

    @Value("${openrouter.api.key}")
    private String apiKey;

    // 🎯 MAIN FLOW (clean & readable)
    public EmailResponse generateReply(EmailRequest request) {

        String prompt = buildPrompt(request);

        String rawResponse = callOpenRouter(prompt);

        String finalReply = extractReply(rawResponse);

        return new EmailResponse(finalReply);
    }

    // 🧠 PROMPT BUILDER (your improved logic)
    private String buildPrompt(EmailRequest request) {

        String email = request.getEmailContent();
        String tone = request.getTone();

        if ("rewrite".equalsIgnoreCase(tone)) {

            return """
Rewrite the following email reply in a better, clearer, and more professional way.

Rules:
- Keep same meaning
- Do NOT include subject
- Improve clarity and tone
- Keep it 80–120 words
- Maintain proper greeting and closing
- Maintain clean paragraph spacing (VERY IMPORTANT)
- Do NOT use placeholders like [Name]

Email:
%s
""".formatted(email);

        } else {

            return """
Generate a %s  email reply based on given tone.

Rules:
- Generate ONLY one reply
- Do NOT include subject
- Keep it 80–120 words
- Use natural human vocabulary
- Include proper greeting
- Start body in new paragraph after greeting
- Maintain paragraph spacing (VERY IMPORTANT)
- End with closing like:
  Best regards,
  Sai Nagalakshmi

Email:
%s
""".formatted(tone, email);
        }
    }

    // 🌐 API CALL
    private String callOpenRouter(String prompt) {

        try {
            URL url = new URL("https://openrouter.ai/api/v1/chat/completions");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + apiKey);
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            // Escape quotes
            String safePrompt = prompt.replace("\"", "\\\"");

            String requestBody = """
            {
              "model": "openai/gpt-4o-mini",
              "messages": [
                {
                  "role": "user",
                  "content": "%s"
                }
              ]
            }
            """.formatted(safePrompt);

            OutputStream os = conn.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();

            BufferedReader br;

            if (conn.getResponseCode() >= 200 && conn.getResponseCode() < 300) {
                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            } else {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            }

            StringBuilder response = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                response.append(line);
            }

            return response.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 🧩 RESPONSE EXTRACTOR
    private String extractReply(String response) {

        try {

            if (response == null) {
                return "Error: No response from AI";
            }

            JSONObject json = new JSONObject(response);

            if (json.has("error")) {
                return "AI service unavailable. Try again.";
            }

            return json
                    .getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                    .trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "Error extracting AI response";
        }
    }
}