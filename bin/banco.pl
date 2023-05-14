% EfolioB - Linguagens de Programação, 2022/2023, UAb 

%Predicados dinamicos para armazenar os dados dos clientes
:- dynamic balanco_total/2, balanco_credito/2, movimento/3.

% Informações de cliente
cliente(123, 'Alice', 'NYC-123', 'New York', '01-01-2020').
cliente(456, 'Bob', 'CHI-456', 'Chicago', '02-02-2020').
cliente(789, 'Charlie', 'LA-789', 'Los Angeles', '03-03-2020').
cliente(752, 'John', 'CHI-456', 'Chicago', '03-03-2020').

% Balanço total da conta (valor corrente + valor disponível de crédito)
balanco_total(123, 2500).
balanco_total(456, 500).
balanco_total(789, 50).
balanco_total(752, 5000).

% Balanço de crédito
balanco_credito(123, 0).
balanco_credito(456, 5000).
balanco_credito(789, 2000).

% Movimentos (valor e data do movimento)
movimento(123, -100, '01-01-2020').
movimento(456, 200, '02-02-2020').
movimento(789, -10, '03-03-2020').
movimento(789, -20, '04-03-2020').

% Predicados para obter os dados dos clientes

% 1.1 - Predicado que obtém todos os dados dos clientes
todos_clientes(NumCliente, Nome, Agencia, Cidade, DataAbertura) :-
    cliente(NumCliente, Nome, Agencia, Cidade, DataAbertura).

% 2.1 - Predicado que obtém o número de cliente e nome de cliente com base numa cidade
clientes_por_cidade(Cidade, NumCliente, Nome) :-
    cliente(NumCliente, Nome, _, Cidade, _).

% 3.1 - Predicado que retorna todos os clientes elegíveis a crédito
clientes_elegiveis_credito(NumeroCliente, Nome, Agencia, Cidade, DataAbertura) :-
    cliente(NumeroCliente, Nome, Agencia, Cidade, DataAbertura),
    saldo_real(NumeroCliente, SaldoReal),
    (balanco_credito(NumeroCliente, 0) ; \+balanco_credito(NumeroCliente, _)),
    SaldoReal > 100.

% 4.1 - Predicado que obtém o saldo real de um determinado cliente
saldo_real(NumCliente, SaldoReal) :-
    balanco_total(NumCliente, BalancoTotal),
    (balanco_credito(NumCliente, Credito) -> true; Credito is 0),
    SaldoReal is BalancoTotal - Credito.

% 5.1 - Predicado que obtém o balanço de crédito de um determinado cliente
saldo_credito(NumCliente, Credito) :-
    balanco_credito(NumCliente, Credito).

% 6.1 - Predicado que obtém os movimentos de determinado cliente
movimentos_cliente(NumCliente, Valor, DataMovimento) :-
    movimento(NumCliente, Valor, DataMovimento).

% 6.2 - Predicado que verifica se um cliente não possui movimentos
sem_movimentos(NumCliente) :-
    \+ movimento(NumCliente, _, _).

% Função auxiliar para obter a data atual
getCurrentDate(Date) :-
    get_time(Timestamp),
    stamp_date_time(Timestamp, DateTime, 'UTC'),
    date_time_value(year, DateTime, Year),
    date_time_value(month, DateTime, Month),
    date_time_value(day, DateTime, Day),
    format(atom(Date), '~|~`0t~d~2+~|~`0t~d~2+~|~d', [Day, Month, Year]).

% 7.1 - Predicado para efetuar um depósito em determinado cliente
deposito(NumCliente, Valor) :-
    balanco_total(NumCliente, BalancoTotal),
    getCurrentDate(DataMovimento),
    NewBalancoTotal is BalancoTotal + Valor,
    assertz(movimento(NumCliente, Valor, DataMovimento)),
    retract(balanco_total(NumCliente, BalancoTotal)),
    assertz(balanco_total(NumCliente, NewBalancoTotal)).

% 7.2 - Predicado para efetuar um levantamento em determinado cliente
levantamento(NumCliente, Valor) :-
    balanco_total(NumCliente, BalancoTotal),
    SaldoSuficiente is BalancoTotal - Valor,
    SaldoSuficiente >= 0,
    getCurrentDate(DataMovimento),
    NewBalancoTotal is BalancoTotal - Valor,
    assertz(movimento(NumCliente, -Valor, DataMovimento)),
    retract(balanco_total(NumCliente, BalancoTotal)),
    assertz(balanco_total(NumCliente, NewBalancoTotal)).

% 8.1 - Predicado que verifica se um determinado cliente é elegível a crédito
elegivel_credito(NumCliente) :-
    clientes_elegiveis_credito(NumCliente, _, _, _, _).

% 8.2 - Predicado que concede crédito de um determinado valor a um determinado cliente
conceder_credito(NumeroCliente, ValorCredito) :-
    elegivel_credito(NumeroCliente),
    balanco_total(NumeroCliente, BalancoTotal),
    NewBalancoTotal is BalancoTotal + ValorCredito,
    retract(balanco_total(NumeroCliente, BalancoTotal)),
    assertz(balanco_total(NumeroCliente, NewBalancoTotal)),
    (balanco_credito(NumeroCliente, 0) ->
        assertz(balanco_credito(NumeroCliente, ValorCredito));
        retract(balanco_credito(NumeroCliente, _)),
        assertz(balanco_credito(NumeroCliente, ValorCredito))
    ).


