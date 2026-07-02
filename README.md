# Distributed Systems in Java — Client/Server & Publish/Subscribe

Two-phase distributed system built with core Java concurrency and networking (no frameworks).

## Phase 1 — Multithreaded Client/Server (`phase1-client-server/`)

- **Three Swing GUI clients** send product requests to an intermediate server over **TCP sockets** using Java object serialization.
- Server routes requests through a **synchronized BlockingQueue** processed by a **fixed pool of 5 worker threads**, with a live server log GUI.

## Phase 2 — Publish/Subscribe Notifications (`phase2-pubsub/`)

- Extends the system into a **pub/sub notification service**: clients subscribe to topics and receive real-time broadcast events.
- Dedicated publisher, subscriber, and server GUIs demonstrating concurrent event delivery.

## Screenshot

![Client-server GUIs](docs/client_server_gui.png)

## Stack

Java, Swing, TCP sockets, object serialization, `ExecutorService` thread pools, `BlockingQueue`, Maven.

## Running

```bash
cd phase1-client-server && mvn package   # then run server, then the clients
cd phase2-pubsub && mvn package          # then run server, subscribers, publisher
```
