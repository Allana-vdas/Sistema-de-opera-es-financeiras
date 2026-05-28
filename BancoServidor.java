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
                while ((dados = entrada.readLine()) != null) {

                    String[] partes = dados.split(";");
                    int opcao = Integer.parseInt(partes[0]);
                    String conta = partes.length > 1 ? partes[1] : "";
                    double valor = partes.length > 2 ? Double.parseDouble(partes[2]) : 0.0;

                    String resposta = "erro;Ação inválida";
                    if (!contas.containsKey(conta)) {
                        resposta = "erro;Conta não encontrada";
                    } else {
                        if (opcao==1) {
                            resposta = "sucesso;" + contas.get(conta);
                        } else if (opcao==2) {
                            if (valor > 0) {
                                contas.put(conta, contas.get(conta) + valor);
                                resposta = "sucesso;" + contas.get(conta);
                            } else {
                                resposta = "erro;Valor inválido";
                            }
                        } else if (opcao==3) {
                            if (valor > 0 && contas.get(conta) >= valor) {
                                contas.put(conta, contas.get(conta) - valor);
                                resposta = "sucesso;" + contas.get(conta);
                            } else {
                                resposta = "erro;Saldo insuficiente ou valor inválido";
                            }
                        }
                    }
                    saida.println(resposta);
                }
            } catch (Exception e) {
                System.out.println("Erro na conexão: " + e.getMessage());
            } finally {
                try {
                    System.out.println("Conexão encerrada com " + conexao.getInetAddress().getHostAddress());
                    conexao.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


