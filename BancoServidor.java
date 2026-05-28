import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

public class BancoServidor {
    private static Map<String, Double> contas = new HashMap<>();

    public static void main(String[] args) {
        contas.put("123", 1000.0);
        contas.put("456", 500.0);
        int porta = 5000;
        try (ServerSocket servidor = new ServerSocket(porta)) {
            System.out.println("Servidor do Banco iniciado. Escutando na porta " + porta);
            while (true) {
                Socket conexao = servidor.accept();
                System.out.println("Nova conexão de " + conexao.getInetAddress().getHostAddress());
                new Thread(new GerenciadorCliente(conexao)).start();
            }
        } catch (IOException e) {
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }
    // Classe que gerencia a comunicação com um cliente específico
    private static class GerenciadorCliente implements Runnable {
        private Socket conexao;

        public GerenciadorCliente(Socket conexao) {
            this.conexao = conexao;
        }

        @Override
        public void run() {
            try (
                    BufferedReader entrada = new BufferedReader(new InputStreamReader(conexao.getInputStream()));
                    PrintWriter saida = new PrintWriter(conexao.getOutputStream(), true)
            ) {
                String dados;
                // Lê as mensagens enviadas pelo cliente
                while ((dados = entrada.readLine()) != null) {
                    // Separa a mensagem recebida: ACAO;CONTA;VALOR
                    String[] partes = dados.split(";");
                    int opcao = Integer.parseInt(partes[0]);
                    String conta = partes.length > 1 ? partes[1] : "";
                    double valor = partes.length > 2 ? Double.parseDouble(partes[2]) : 0.0;

                    String resposta = "erro;Ação inválida";
