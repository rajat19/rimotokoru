# Rimotokoru (gRPC)

## gRPC vs REST
| gRPC             | REST                   |
|------------------|------------------------|
| Protocol Buffers | JSON                   |
| HTTP 2           | HTTP 1.1               |
| Streaming        | Unary                  |
| Bi directional   | Client -> Server       |
| Free design      | GET/POST/UPDATE/DELETE |
| More secure      | Less secure            |

## Protocol buffer vs Json

| Comparison  | Json               | Protobuf                   |
|-------------|--------------------|----------------------------|
| Storage     | Takes more storage | Less as compared to Json   |
| Efficiency  | Less CPU Intensive | More CPU Intensive, Faster |

```protobuf
syntax = "proto3";

message Person {
  uint32 age = 1;
  string first_name = 2;
  string last_name = 3;
}
```
Protobuf occupies 17 bytes

```json
{
  "age": 26,
  "first_name": "Clement",
  "last_name": "Jean"
}
```
Json occupies 52 bytes (compressed)

## Apis in gRPC

| API                      | Description                                            |
|--------------------------|--------------------------------------------------------|
| Unary                    | Similar to REST, one request and one response          |
| Server streaming         | Uses HTTP/2, one quest and multiple responses          |
| Client streaming         | client send multiple request, server send one response |
| Bi directional streaming | Multiple request/response from client and server       |

```protobuf
service GreetService {
  // Unary
  rpc Greet(GreetRequest) returns (GreetResponse) {};

  // Server streaming
  rpc GreetManyTimes(GreetRequest) returns (stream GreetResponse) {};

  // Client streaming
  rpc LongGreet(stream GreetRequest) returns (GreetResponse) {};

  // Bi directional streaming
  rpc GreetEveryone(stream GreetRequest) returns (stream GreetResponse) {};
}
```

## SSL

### Generating ssl keys
To generate ssl certificates, keys and permissions. Run
```shell
chmod +x ssl/ssl.sh
./ssl.sh
```

### Enable TLS in application

#### Server changes
In ApplicationServer, add below while starting server
```java
.useTransportSecurity(
        new File("ssl/server.crt"),
        new File("ssl/server.pem")
)
```

#### Client changes

```java
final ChannelCredentials credentials = TlsChannelCredentials.newBuilder()
        .trustManager(new File("ssl/ca.crt"))
        .build();
final ManagedChannel channel = Grpc
        .newChannelBuilderForAddress("localhost", 50051, credentials)
        .build();
```

instead of 

```java
final ManagedChannel channel = ManagedChannelBuilder
        .forAddress("localhost", 50051)
        .usePlaintext()
        .build();
```

## Reflection and Evans API

To enable reflection. Follow changes defined in [Enable Server Reflection](https://github.com/grpc/grpc-java/blob/master/documentation/server-reflection-tutorial.md#enable-server-reflection)

To manage inputs in application using CLI, Download [Evans API CLI](https://github.com/ktr0731/evans/releases)

```shell
evans --host localhost --port 50051 --reflection repl
```

### Evans Commands

1. show packages
    ```shell
    show package
    ```
    ```
    +-------------------------+
    |         PACKAGE         |
    +-------------------------+
    | calculator              |
    | greeting                |
    | grpc.reflection.v1alpha |
    +-------------------------+
    ```
2. select a package
    ```shell
    package calculator
    ```
3. show services
    ```shell
    show service
    ```
    ```
    +-------------------+---------+----------------+-----------------+
    |      SERVICE      |   RPC   |  REQUEST TYPE  |  RESPONSE TYPE  |
    +-------------------+---------+----------------+-----------------+
    | CalculatorService | sum     | SumRequest     | SumResponse     |
    | CalculatorService | prime   | PrimeRequest   | PrimeResponse   |
    | CalculatorService | average | AverageRequest | AverageResponse |
    | CalculatorService | max     | MaxRequest     | MaxResponse     |
    | CalculatorService | sqrt    | SqrtRequest    | SqrtResponse    |
    +-------------------+---------+----------------+-----------------+
    ```
4. select service
    ```shell
    service CalculatorService
    ```
5. show messages
    ```shell
    show message
    ```
    ```
    +-----------------+
    |     MESSAGE     |
    +-----------------+
    | AverageRequest  |
    | AverageResponse |
    | MaxRequest      |
    | MaxResponse     |
    | PrimeRequest    |
    | PrimeResponse   |
    | SqrtRequest     |
    | SqrtResponse    |
    | SumRequest      |
    | SumResponse     |
    +-----------------+
    ```
6. call function
    ```shell
    call sum
    ```
    ```
    first_number (TYPE_INT32) => 1
    second_number (TYPE_INT32) => 5
    {
       "result": 6
    }
    ```