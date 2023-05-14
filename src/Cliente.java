import org.jpl7.*;
import java.util.Map;
import java.util.Scanner;
 
//Classe Cliente e suas funções
public class Cliente {
    private int numeroCliente;
    private String nome;
    private String agencia;
    private String cidade;
    private String dataAbertura;

    //Construtor
    public Cliente(int numeroCliente) {
        this.numeroCliente = numeroCliente;
        Query query = new Query("cliente(" + numeroCliente + ", Nome, Agencia, Cidade, DataAbertura)");
        if (query.hasSolution()) {
            nome = query.oneSolution().get("Nome").toString();
            agencia = query.oneSolution().get("Agencia").toString();
            cidade = query.oneSolution().get("Cidade").toString();
            dataAbertura = query.oneSolution().get("DataAbertura").toString();
        }
    }

    //Função para mostrar o saldo real do cliente
    public void printRealBalance() {
        String queryString = String.format("saldo_real(%d, SaldoReal).", this.numeroCliente);
        Query query = new Query(queryString);
        Term saldoRealTerm = query.oneSolution().get("SaldoReal");
        double saldoReal = saldoRealTerm.doubleValue();
    
        System.out.printf("O saldo real do cliente %d é %.2f%n", this.numeroCliente, saldoReal);
    }

    //Função para mostrar o saldo de crédito do cliente
    public void printCreditBalance() {
        String queryString = String.format("saldo_credito(%d, Credito).", this.numeroCliente);
        Query query = new Query(queryString);
        Map<String, Term> solution = query.oneSolution();
    
        if (solution != null) {
            Term creditoTerm = solution.get("Credito");
            double credito = creditoTerm.doubleValue();
            System.out.printf("O balanço de crédito do cliente %d é %.2f%n", this.numeroCliente, credito);
        } else {
            System.out.println("Não foi possível obter o balanço de crédito do cliente.");
        }
    }
    
    //Função para mostrar os movimentos do cliente
    public void printMovements() {
        String noMovementsQuery = String.format("sem_movimentos(%d).", this.numeroCliente);
        Query checkNoMovements = new Query(noMovementsQuery);
        
        System.out.printf("\nMovimentos do cliente %d:\n", this.numeroCliente);
        System.out.println("--------------------------------------");
    
        if (checkNoMovements.hasSolution()) {
            System.out.println("Nenhum movimento encontrado para o cliente.");
        } else {
            String queryString = String.format("movimentos_cliente(%d, Valor, DataMovimento).", this.numeroCliente);
            Query query = new Query(queryString);
    
            while (query.hasMoreSolutions()) {
                Map<String, Term> solution = query.nextSolution();
                double valor = solution.get("Valor").doubleValue();
                String dataMovimento = solution.get("DataMovimento").toString();
    
                System.out.printf("Valor: %.2f | Data: %s\n", valor, dataMovimento);
            }
        }
    }

    //Função para depositar dinheiro na conta do cliente
    public boolean deposit(double amount) {
        if (amount <= 0) {
            return false;
        }
        String queryString = String.format("deposito(%d, %.2f).", this.numeroCliente, amount);
        Query query = new Query(queryString);
        return query.hasSolution();
    }
    
    //Função para levantar dinheiro da conta do cliente
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }
        String queryString = String.format("levantamento(%d, %.2f).", this.numeroCliente, amount);
        Query query = new Query(queryString);
        return query.hasSolution();
    }
    
    //Função para verificar se o cliente é elegível para crédito
    public void checkCreditEligibility() {
        Query query = new Query("elegivel_credito(" + numeroCliente + ")");
        if (query.hasSolution()) {
            System.out.println("O cliente é elegível para crédito.");
        } else {
            System.out.println("O cliente não é elegível para crédito.");
        }
    }
    
    //Função para conceder crédito ao cliente
    public void grantCredit(double creditValue) {
        Query query = new Query(String.format("conceder_credito(%d, %.2f)", numeroCliente, creditValue));
        if (query.hasSolution()) {
            System.out.println("Crédito concedido com sucesso.");
        } else {
            System.out.println("Não foi possível conceder crédito ao cliente.");
        }
    }
    
    //Getters para os atributos da classe
    public int getNumeroCliente() {
        return numeroCliente;
    }

    public String getNome() {
        return nome;
    }

    public String getAgencia() {
        return agencia;
    }

    public String getCidade() {
        return cidade;
    }

    public String getDataAbertura() {
        return dataAbertura;
    }

    //Menu do cliente
    public void exibirMenuCliente() {
        Scanner scanner = new Scanner(System.in);
        int opcao = 0;
    
        while (opcao != 8) {
            System.out.println("\n--** Menu do Cliente **--");
            System.out.println("1 - Ver saldo real");
            System.out.println("2 - Ver saldo de crédito");
            System.out.println("3 - Ver movimentos");
            System.out.println("4 - Fazer depósito");
            System.out.println("5 - Fazer levantamento");
            System.out.println("6 - Verificar elegibilidade de crédito");
            System.out.println("7 - Conceder crédito");
            System.out.println("8 - Voltar ao menu anterior");
            System.out.print("\nEscolha uma opção: ");
            opcao = scanner.nextInt();
    
            switch (opcao) {
                case 1:
                    printRealBalance();
                    break;
                case 2:
                    printCreditBalance();
                    break;
                case 3:
                    printMovements();
                    break;
                case 4:
                    System.out.print("Insira a quantia a depositar: ");
                    double depositAmount = scanner.nextDouble();
                    if (deposit(depositAmount)) {
                        System.out.println("Depósito realizado com sucesso.");
                    } else {
                        System.out.println("Não foi possível realizar o depósito.");
                    }
                    break;
                case 5:
                    System.out.print("Insira a quantia a levantar: ");
                    double withdrawAmount = scanner.nextDouble();
                    if (withdraw(withdrawAmount)) {
                        System.out.println("Levantamento realizado com sucesso.");
                    } else {
                        System.out.println("Não foi possível realizar o levantamento.");
                    }
                    break;
                case 6:
                    checkCreditEligibility();
                    break;
                case 7:
                    System.out.print("Insira o valor do crédito a conceder: ");
                    double creditValue = scanner.nextDouble();
                    grantCredit(creditValue);
                    break;
                case 8:
                    System.out.println("Voltando ao menu anterior...");
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }
        }
    }
    
}//Fim da classe Cliente