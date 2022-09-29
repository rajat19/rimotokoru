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