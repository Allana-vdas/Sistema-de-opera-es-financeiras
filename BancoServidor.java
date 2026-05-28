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

