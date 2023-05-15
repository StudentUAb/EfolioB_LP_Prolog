import org.jpl7.*;
import java.util.Map;
import java.util.Scanner;

// Classe Cliente e suas funções
public class Cliente {
    private int numeroCliente;
    private String nome;
    private String agencia;
    private String cidade;
    private String dataAbertura;

    // Construtor
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

    // Função para mostrar o saldo real do cliente
    public void printRealBalance() {
        String queryString = String.format("saldo_real(%d, SaldoReal).", this.numeroCliente);
        Query query = new Query(queryString);
        Term saldoRealTerm = query.oneSolution().get("SaldoReal");
        int saldoReal = saldoRealTerm.intValue();

        System.out.printf("O saldo real do cliente %d é %d%n", this.numeroCliente, saldoReal);
    }


    //Função para mostrar o saldo de crédito do cliente
    public void printCreditBalance() {
        String queryString = String.format("saldo_credito_existente(%d, Credito).", this.numeroCliente);
        Query query = new Query(queryString);
        Map<String, Term> solution = query.oneSolution();
    
        if (solution != null) {
            Term creditoTerm = solution.get("Credito");
            int credito = creditoTerm.intValue();
            System.out.printf("O balanço de crédito do cliente %d é %d%n", this.numeroCliente, credito);
        } else {
            System.out.println("Não foi possível obter o balanço de crédito do cliente.");
        }
    }
    
    
    //Função para mostrar os movimentos do cliente
    private void printMovements() {
        System.out.println("Movimentos do Cliente:");
        Query query = new Query("movimentos_cliente(" + this.numeroCliente + ", Valor, DataMovimento)");
        Map<String, Term>[] solutions = query.allSolutions();
    
        for (Map<String, Term> solution : solutions) {
            Term valorTerm = solution.get("Valor");
            Term dataTerm = solution.get("DataMovimento");
    
            if (valorTerm.isInteger()) {
                int valor = ((org.jpl7.Integer) valorTerm).intValue();
                String data = dataTerm.toString();
    
                if (valor > 0) {
                    System.out.println("Valor depositado: " + valor + " | Data: " + data);
                } else if (valor < 0) {
                    System.out.println("Valor retirado: " + (valor) + " | Data: " + data);
                } else {
                    System.out.println("Movimento sem valor | Data: " + data);
                }
            }
        }
    }
    
    //Função para depositar dinheiro na conta do cliente
    public boolean deposit(int amount) {
        if (amount <= 0) {
            return false;
        }
        String queryString = String.format("deposito(%d, %d).", this.numeroCliente, amount);
        Query query = new Query(queryString);
        return query.hasSolution();
    }
    
    //Função para levantar dinheiro da conta do cliente
    public boolean withdraw(int amount) {
        if (amount <= 0) {
            return false;
        }
        String queryString = String.format("levantamento(%d, %d).", this.numeroCliente, amount);
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
    public boolean isEligibleForCredit() {
        String queryString = String.format("elegivel_credito(%d).", this.numeroCliente);
        Query query = new Query(queryString);
        return query.hasSolution();
    }
    
    
    //Função para conceder crédito ao cliente
    public void grantCredit(int creditValue) {
        if (isEligibleForCredit()) {
            Query query = new Query(String.format("conceder_credito(%d, %d, Result)", numeroCliente, creditValue));
            java.util.Map<String, Term> solution = query.oneSolution();
    
            if (solution != null) {
                Term resultTerm = solution.get("Result");
                if (resultTerm != null && resultTerm.toString().equals("true")) {
                    System.out.println("Crédito concedido com sucesso.");
                } else {
                    System.out.println("Não foi possível conceder crédito ao cliente.");
                }
            } else {
                System.out.println("Não foi possível conceder crédito ao cliente.");
            }
        } else {
            System.out.println("O cliente não é elegível para crédito.");
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


    // Função para exibir o saldo total de um cliente
public void printTotalBalance() {
    String queryString = String.format("balanco_total(%d, BalancoTotal).", this.numeroCliente);
    Query query = new Query(queryString);
    Term balancoTotalTerm = query.oneSolution().get("BalancoTotal");
    int balancoTotal = balancoTotalTerm.intValue();

    System.out.printf("O saldo total do cliente %d é %d%n", this.numeroCliente, balancoTotal);
}

// Menu do cliente
public void exibirMenuCliente() {
    Scanner scanner = new Scanner(System.in);
    int opcao = 0;
    while (opcao != 9) {
        System.out.println("\n--** Menu do Cliente **--");
        System.out.println("1 - Ver saldo total (real + crédito))");
        System.out.println("2 - Ver saldo real (sem crédito)");
        System.out.println("3 - Ver saldo de crédito existente");
        System.out.println("4 - Ver movimentos do cliente");
        System.out.println("5 - Fazer depósito na conta");
        System.out.println("6 - Fazer levantamento da conta");
        System.out.println("7 - Verificar elegibilidade de crédito do cliente");
        System.out.println("8 - Conceder crédito ao cliente");
        System.out.println("9 - Voltar ao menu anterior");
        System.out.print("\nEscolha uma opção: ");
        opcao = scanner.nextInt();
        switch (opcao) {
            case 1:
                printTotalBalance();
                break;
            case 2:
                printRealBalance();
                break;
            case 3:
                printCreditBalance();
                break;
            case 4:
                printMovements();
                break;
            case 5:
                System.out.print("Insira a quantia a depositar: ");
                int depositAmount = scanner.nextInt();
                if (deposit(depositAmount)) {
                    System.out.println("Depósito realizado com sucesso.");
                } else {
                    System.out.println("Não foi possível realizar o depósito.");
                }
                break;
            case 6:
                System.out.print("Insira a quantia a levantar: ");
                int withdrawAmount = scanner.nextInt();
                if (withdraw(withdrawAmount)) {
                    System.out.println("Levantamento realizado com sucesso.");
                } else {
                    System.out.println("Não foi possível realizar o levantamento.");
                }
                break;
            case 7:
                checkCreditEligibility();
                break;
            case 8:
                System.out.print("Insira o valor do crédito a conceder: ");
                int creditValue = scanner.nextInt();
                grantCredit(creditValue);
                break;
            case 9:
                System.out.println("Voltando ao menu anterior...");
                break;
            default:
                System.out.println("Opção inválida. Tente novamente.");
                break;
        }
    }
}


} // Fim da classe Cliente
