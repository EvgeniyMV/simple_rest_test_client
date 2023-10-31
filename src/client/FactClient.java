package client;

import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class FactClient {
    private final static String BASE_URL = "http://94.41.20.1:8080/facts";

    public String postFact(Fact fact) {
        if (fact.getContent().isEmpty() || fact.getContent() == null || fact == null)
            return "Факт отсутствует, введите факт";
        try {
            URI uri = new URI(BASE_URL);
            HttpRequest httpRequest = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(10))
                    .headers("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(CustomJsonConverter.getJson(fact), StandardCharsets.UTF_8))
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 201) return "Не удалось опубликовать факт";
            return "Факт опубликован!";

        } catch (Exception e) {
            return e.toString();
        }
    }

    public Fact getFactById(int factId) {
        try {
            URI uri = new URI(BASE_URL + "/id=" + factId);
            HttpRequest httpRequest = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(10))
                    .headers("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return null;
            return CustomJsonConverter.fromJson(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Fact> getAllFacts() {
        try {
            URI uri = new URI(BASE_URL);
            HttpRequest httpRequest = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(10))
                    .headers("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return null;
            return CustomJsonConverter.listFromJson(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Fact> getByAuthor(String author) {
        if (author.isBlank() || author.isEmpty() || author == null) return null;
        try {
            URI uri = new URI(BASE_URL + "/author=" + author);
            HttpRequest httpRequest = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(10))
                    .headers("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return null;
            return CustomJsonConverter.listFromJson(response.body());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String deleteFact(int id) {
        try {
            URI uri = new URI(BASE_URL + "/id=" + id);
            HttpRequest httpRequest = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(5))
                    .headers("Content-Type", "application/json")
                    .DELETE()
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return "Не удалось удалить факт с таким номером";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Факт удален";
    }

    public String updateFact(int id, Fact fact) {
        if (fact == null || fact.getContent().isEmpty() || fact.getContent().isBlank() || fact.getContent() == null)
            return "Факт отсутствует, введите факт";
        try {
            URI uri = new URI(BASE_URL + "/id=" + id);
            HttpRequest httpRequest = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(5))
                    .headers("Content-Type", "application/json")
                    .PUT(HttpRequest.BodyPublishers.ofString(CustomJsonConverter.getJson(fact), StandardCharsets.UTF_8))
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.statusCode());
            if (response.statusCode() != 200) return "Не удалось изменить факт c таким номером";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Факт изменен!";
    }

    private static class CustomJsonConverter {
        private static String getJson(Fact fact) {
            StringBuilder jBuilder = new StringBuilder();
            char c = '"';
            jBuilder.append("{");
            jBuilder.append(c + "content" + c + ":" + c + fact.getContent() + c);
            jBuilder.append(",");
            jBuilder.append(c + "author" + c + ":" + "{" + c + "name" + c + ":" + c + fact.getAuthor() + c + "}");
            jBuilder.append(",");
            jBuilder.append(c + "id" + c + ":" + c + "0" + c);
            jBuilder.append("}");
            jBuilder.trimToSize();
            return jBuilder.toString();
        }

        private static Fact fromJson(String json) {
            json = json.replaceAll("}]", "}");
            json = json.replaceAll("\\[", "");
            String content = json.substring(12, json.indexOf("\",\""));
            json = json.substring(json.indexOf("\",\"") + 2);
            String author = json.substring(json.indexOf("name") + 7, json.indexOf("\"},\""));
            json = json.substring(json.indexOf("\"},\"") + 3);
            int idStartIndex = json.indexOf("id") + 4;
            int idEndIndex = idStartIndex;
            while (Character.isDigit(json.charAt(idEndIndex))) idEndIndex++;
            int id = Integer.parseInt(json.substring(idStartIndex, idEndIndex));
            return new Fact(content, author, id);
        }

        private static List<Fact> listFromJson(String json) {
            List<Fact> facts = new ArrayList<>();
            while (!json.isEmpty()) {
                String fact;
                if (json.contains("},{")) {
                    fact = json.substring(0, (json.indexOf("},{") + 2));
                    json = json.substring((json.indexOf("},{") + 2));
                } else {
                    fact = json;
                    json = "";
                }
                facts.add(fromJson(fact));
            }
            return facts;
        }
    }
}
