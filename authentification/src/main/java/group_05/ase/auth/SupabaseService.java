package group_05.ase.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.json.JSONObject;

import java.util.UUID;

@Service
public class SupabaseService {

    private final RestTemplate restTemplate;
    private final String supabaseProjectUrl;
    private final String supabaseServiceRoleKey;

    public SupabaseService(
            @Value("${supabase.project-url}") String supabaseProjectUrl,
            @Value("${supabase.service-role-key}") String supabaseServiceRoleKey
    ) {
        this.restTemplate = new RestTemplate();
        this.supabaseProjectUrl = supabaseProjectUrl;
        this.supabaseServiceRoleKey = supabaseServiceRoleKey;
    }

    public void deleteUser(UUID supabaseId) {
        String url = supabaseProjectUrl + "/auth/v1/admin/users/" + supabaseId;

        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseServiceRoleKey);
        headers.set("Authorization", "Bearer " + supabaseServiceRoleKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                entity,
                String.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to delete user from Supabase: " + response.getBody());
        }
    }

    public String createUser(String email, String password) {
        String url = supabaseProjectUrl + "/auth/v1/admin/users";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseServiceRoleKey);
        headers.set("Authorization", "Bearer " + supabaseServiceRoleKey);

        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);

        HttpEntity<String> request = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            JSONObject body = new JSONObject(response.getBody());
            // supabaseId ist "id" im Body-JSON
            return body.getString("id");
        } else {
            throw new RuntimeException("Supabase User konnte nicht erstellt werden: " + response.getBody());
        }
    }

    public String loginUser(String email, String password) {
        String url = supabaseProjectUrl + "/auth/v1/token?grant_type=password";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseServiceRoleKey);

        JSONObject json = new JSONObject();
        json.put("email", email);
        json.put("password", password);

        HttpEntity<String> request = new HttpEntity<>(json.toString(), headers);

        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            JSONObject body = new JSONObject(response.getBody());
            return body.getString("access_token");
        } else {
            throw new RuntimeException("Login fehlgeschlagen: " + response.getBody());
        }
    }

    public void forgotPassword(String email) {
        String url = supabaseProjectUrl + "/auth/v1/recover";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("apikey", supabaseServiceRoleKey);
        headers.set("Authorization", "Bearer " + supabaseServiceRoleKey);

        JSONObject json = new JSONObject();
        json.put("email", email);

        HttpEntity<String> request = new HttpEntity<>(json.toString(), headers);

        restTemplate.postForEntity(url, request, String.class);
    }

}
