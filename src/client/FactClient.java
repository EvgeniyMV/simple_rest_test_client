package client;

import java.io.*;
import java.net.*;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

public class FactClient {
    private final static String BASE_URL = "http://localhost:8080/facts";
    private final String AUTHOR;

    public FactClient (String author) {
        if (author.isBlank() || author.isEmpty() || author == null) AUTHOR = "Неизвестный автор";
        else AUTHOR = author;
    }

    public String postFact (String content) {
        if (content.isEmpty() || content == null) return "Факт отсутствует, введите факт";
        StringBuilder result = new StringBuilder();
        try {
            String request = CustomJsonConverter.getJson(content, AUTHOR);
            URL url = new URI(BASE_URL).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
            dos.write(request.getBytes(StandardCharsets.UTF_8));
            dos.flush();
            BufferedReader dis = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while (dis.ready()) {
                result.append(dis.readLine());
            }
            dos.close();
            dis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    public String getFactById (int factId) {
        try {
            URI uri = new URI(BASE_URL + "/id=" + factId);
            HttpRequest httpRequest = HttpRequest.newBuilder(uri)
                    .timeout(Duration.ofSeconds(10))
                    .headers("Content-Type", "application/json")
                    .GET()
                    .build();
            HttpClient client = HttpClient.newHttpClient();
            HttpResponse<String> response = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) return "Факт с таким номером не найден";
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "1";
    }




    private static class CustomJsonConverter {
        private static String getJson (String content, String author) throws UnsupportedEncodingException {
            StringBuilder jBuilder = new StringBuilder();
            char c = '"';
            jBuilder.append("{");
            jBuilder.append(c + "content" + c + ":" + c + content + c);
            jBuilder.append(",");
            jBuilder.append(c + "author" + c + ":" + "{" + c + "name" + c + ":"  + c + author + c + "}");
            jBuilder.append(",");
            jBuilder.append(c + "id" + c + ":" + c + "0" + c);
            jBuilder.append("}");
            jBuilder.trimToSize();


            return jBuilder.toString();
        }
    }
}
