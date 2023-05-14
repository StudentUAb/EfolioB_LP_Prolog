import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import org.jpl7.Query;
import org.jpl7.Term;
import java.lang.Integer;

//Classe Sistema Bancário
public class SistemaBancario {
    private List<Cliente> clientes;

    //Construtor
    public SistemaBancario() {
        clientes = new ArrayList<>();
        obterClientesDoProlog();
        ordenarClientesPorNumero();
    }

    //Função para obter os clientes do Prolog
    private void obterClientesDoProlog() {
        Query query = new Query("consult('src/banco.pl')");
        System.out.println("accesso ao ficheiro banco.pl: " + (query.hasSolution() ? "com successo" : "falhou ligação"));
        Query queryClientes = new Query("cliente(NumeroCliente, _, _, _, _)");
        while (queryClientes.hasMoreSolutions()) {
            int numeroCliente = Integer.parseInt(queryClientes.nextSolution().get("NumeroCliente").toString());
            clientes.add(new Cliente(numeroCliente));
        }
    }
    
    //Função para ordenar os clientes por número
    private void ordenarClientesPorNumero() {
        Collections.sort(clientes, (cliente1, cliente2) -> {
            if (cliente1.getNumeroCliente() < cliente2.getNumeroCliente()) {
                return -1;
            } else if (cliente1.getNumeroCliente() > cliente2.getNumeroCliente()) {
                return 1;
            } else {
                return 0;
            }
        });
    }

    //Função para imprimir a lista de clientes
    public void imprimirListaClientes() {
        System.out.println("Lista de Clientes:");
        for (Cliente cliente : clientes) {
            System.out.println("Número de Cliente: " + cliente.getNumeroCliente());
            System.out.println("Nome: " + cliente.getNome());
            System.out.println("Agência: " + cliente.getAgencia());
            System.out.println("Cidade: " + cliente.getCidade());
            System.out.println("Data de Abertura: " + cliente.getDataAbertura());
            System.out.println("------------------------");
        }
    }

    //Função para mostrar os clientes por cidade
    private void mostrarClientesPorCidade() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite a cidade: ");
        String cidade = scanner.nextLine();
        //System.out.println("Cidade: " + cidade);
        System.out.println("Clientes na cidade " + cidade + ":");
        Query query = new Query("clientes_por_cidade('" + cidade + "', NumeroCliente, Nome)");
        while (query.hasMoreSolutions()) {
            java.util.Map<String, Term> solution = query.nextSolution();
            int numeroCliente = Integer.parseInt(solution.get("NumeroCliente").toString());
            String nome = solution.get("Nome").toString();
            System.out.println("Número de Cliente: " + numeroCliente);
            System.out.println("Nome: " + nome);
            System.out.println("------------------------");
        }
    }
    
    //Função para mostrar o saldo real do cliente
    private void mostrarSaldoRealCliente() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o número do cliente: ");
        int numeroCliente = scanner.nextInt();
        //System.out.println("Número de Cliente: " + numeroCliente);
        Cliente cliente = buscarCliente(numeroCliente);
        if (cliente != null) {
            System.out.println("Saldo Real do Cliente " + cliente.getNome() + ":");
            Query query = new Query("saldo_real(" + numeroCliente + ", RealBalance)"); // Update this line
            if (query.hasSolution()) {
                int saldoReal = Integer.parseInt(query.oneSolution().get("RealBalance").toString());
                System.out.println("Saldo Real: " + saldoReal);
            } else {
                System.out.println("Não foi possível obter o saldo real do cliente.");
            }
        } else {
            System.out.println("Cliente não encontrado.");
        }
    }
    
    //Função para mostrar os clientes elegíveis a crédito
    private void mostrarClientesElegiveisCredito() {
        System.out.println("Clientes elegíveis a crédito:");
        Query query = new Query("clientes_elegiveis_credito(NumeroCliente, Nome, Agencia, Cidade, DataAbertura)");
        while (query.hasMoreSolutions()) {
            java.util.Map<String, Term> solution = query.nextSolution();
            int numeroCliente = Integer.parseInt(solution.get("NumeroCliente").toString());
            String nome = solution.get("Nome").toString();
            String agencia = solution.get("Agencia").toString();
            String cidade = solution.get("Cidade").toString();
            String dataAbertura = solution.get("DataAbertura").toString();
            System.out.println("Número de Cliente: " + numeroCliente);
            System.out.println("Nome: " + nome);
            System.out.println("Agência: " + agencia);
            System.out.println("Cidade: " + cidade);
            System.out.println("Data de Abertura: " + dataAbertura);
            System.out.println("------------------------");
        }
    }
    
    //Função para selecionar o cliente pelo número
    private void selecionarCliente() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o número do cliente: ");
        int numeroCliente = scanner.nextInt();
        //
        Cliente cliente = buscarCliente(numeroCliente);
        if (cliente != null) {
            cliente.exibirMenuCliente();
        } else {
            System.out.println("Cliente não encontrado.");
        }
    }

    //Menu do Sistema Bancário
    public void exibirMenu() {
        Scanner scanner = new Scanner(System.in);
        int opcao = 0;

        while (opcao != 7) {
            System.out.println("\n--** Menu do Sistema Bancário **--");
            System.out.println("1 - Mostrar Lista de Clientes");
            System.out.println("2 - Mostrar Saldo Real de um Cliente");
            System.out.println("3 - Mostrar Balanço de Crédito de um Cliente");
            System.out.println("4 - Mostrar clientes por cidade");
            System.out.println("5 - Verificar clientes elegíveis a crédito");
            System.out.println("6 - Selecionar cliente pelo número");
            System.out.println("7 - Sair");
            System.out.print("\nEscolha uma opção: ");
            opcao = scanner.nextInt();

            switch (opcao) {
                case 1:
                    imprimirListaClientes();
                    break;
                case 2:
                    mostrarSaldoRealCliente();
                    break;
                case 3:
                    mostrarBalancoCreditoCliente();
                    break;
                case 4:
                    mostrarClientesPorCidade();
                    break;
                case 5:
                    mostrarClientesElegiveisCredito();
                    break;
                case 6:
                    selecionarCliente();
                    break;
                case 7:
                    System.out.println("Saindo do programa, Obrigado! - Criado por: Ivo Baptista,");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }
        }
    }

    //Função para mostrar o balanço de crédito do cliente
    private void mostrarBalancoCreditoCliente() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Digite o número do cliente: ");
        int numeroCliente = scanner.nextInt();
        //
        Cliente cliente = buscarCliente(numeroCliente);
        if (cliente != null) {
            System.out.println("Balanço de Crédito do Cliente " + cliente.getNome() + ":");
            Query query = new Query("saldo_credito(" + numeroCliente + ", CreditBalance)"); // Use saldo_credito instead of credit_balance
            if (query.hasSolution()) {
                Term creditBalanceTerm = query.oneSolution().get("CreditBalance");
                if (creditBalanceTerm.isInteger()) {
                    int balancoCredito = ((org.jpl7.Integer) creditBalanceTerm).intValue();
                    System.out.println("Balanço de Crédito: " + balancoCredito);
                }
            } else {
                System.out.println("Não foi possível obter o balanço de crédito do cliente.");
            }
        } else {
            System.out.println("Cliente não encontrado.");
        }
    }
    
    //Função para buscar o cliente pelo número
    private Cliente buscarCliente(int numeroCliente) {
        for (Cliente cliente : clientes) {
            if (cliente.getNumeroCliente() == numeroCliente) {
                return cliente;
            }
        }
        return null;
    }
}