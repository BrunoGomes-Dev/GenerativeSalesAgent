import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class GenerativeSalesAgent {
    private static final String OPENAI_API_KEY = "SUA_API_KEY_AQUI";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    public static String gerarResposta(String mensagem) throws IOException {
        String prompt = "Você é um agente de vendas digital especializado em marketing. Responda de forma persuasiva e profissional: " + mensagem;
        String body = "{"
            + "\"model\": \"gpt-3.5-turbo\","
            + "\"messages\": [{\"role\": \"user\", \"content\": \"" + prompt + "\"}]"
            + "}";

        URL url = new URL(OPENAI_API_URL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        try (OutputStream os = con.getOutputStream()) {
            byte[] input = body.getBytes("utf-8");
            os.write(input, 0, input.length);
        }

        StringBuilder response = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(con.getInputStream(), "utf-8"))) {
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
        }

        // Aqui você pode usar uma biblioteca JSON para extrair a resposta gerada
        return response.toString(); // (Ajuste para extrair apenas o texto da resposta)
    }

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        System.out.println("Agente de vendas IA pronto! Digite sua dúvida ou solicitação:");
        String pergunta = reader.readLine();
        String resposta = gerarResposta(pergunta);
        System.out.println("IA: " + resposta);
    }
}
