import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import org.json.JSONArray;
import org.json.JSONObject;

public class GenerativeSalesAgent {

    private static final String OPENAI_API_KEY = "SUA_API_KEY_AQUI";
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    private static final List<JSONObject> historicoMensagens = new ArrayList<>();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Agente de vendas digital pronto! Digite sua dúvida ou solicitação (ou 'sair' para encerrar):");

        while (true) {
            System.out.print("Você: ");
            String entrada = scanner.nextLine();

            if (entrada.equalsIgnoreCase("sair")) {
                System.out.println("Encerrando agente de vendas...");
                break;
            }

            // Verifica se o usuário quer avaliar uma rede social
            if (entrada.toLowerCase().startsWith("avaliar ")) {
                String link = entrada.substring(8).trim();
                avaliarRedeSocial(link);
                continue;
            }

            // Gera resposta da IA
            String resposta = gerarResposta(entrada);
            System.out.println("IA: " + resposta);
        }
    }

    public static String gerarResposta(String mensagem) throws InterruptedException {
        String prompt = "Você é um consultor digital especializado em marketing. "
                + "Forneça respostas persuasivas, detalhadas e práticas, sempre com soluções passo a passo, "
                + "sugestões de melhoria e estratégias aplicáveis. Mensagem do usuário: " + mensagem;

        JSONObject novaMensagem = new JSONObject();
        novaMensagem.put("role", "user");
        novaMensagem.put("content", prompt);
        historicoMensagens.add(novaMensagem);

        int tentativas = 0;
        int maxTentativas = 5;
        long espera = 1000;

        while (tentativas < maxTentativas) {
            try {
                JSONObject body = new JSONObject();
                body.put("model", "gpt-3.5-turbo");
                body.put("messages", new JSONArray(historicoMensagens));

                URL url = new URL(OPENAI_API_URL);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Authorization", "Bearer " + OPENAI_API_KEY);
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);

                try (OutputStream os = con.getOutputStream()) {
                    os.write(body.toString().getBytes("utf-8"));
                }

                int responseCode = con.getResponseCode();
                if (responseCode == 429) {
                    System.out.println("Limite de requisições atingido. Aguardando " + espera + "ms...");
                    Thread.sleep(espera);
                    espera *= 2;
                    tentativas++;
                    continue;
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line.trim());
                }

                JSONObject jsonResponse = new JSONObject(response.toString());
                String conteudoResposta = jsonResponse
                        .getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                JSONObject respostaMensagem = new JSONObject();
                respostaMensagem.put("role", "assistant");
                respostaMensagem.put("content", conteudoResposta);
                historicoMensagens.add(respostaMensagem);

                return conteudoResposta;

            } catch (IOException e) {
                System.out.println("Erro na requisição: " + e.getMessage() + ". Tentando novamente...");
                Thread.sleep(espera);
                espera *= 2;
                tentativas++;
            }
        }

        return "Não foi possível obter resposta da API após várias tentativas.";
    }

    // Método de avaliação automática de redes sociais
    public static void avaliarRedeSocial(String link) throws InterruptedException {
        String prompt = "Você é um consultor digital especialista em marketing de redes sociais. "
                + "Analise o perfil ou link fornecido e forneça: "
                + "1) Pontos fortes, 2) Pontos a melhorar, 3) Sugestões de crescimento e engajamento.\n"
                + "Link: " + link;

        System.out.println("IA (avaliando rede social): " + gerarResposta(prompt));
    }
}

 
