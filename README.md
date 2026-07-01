# Real-Time E-Commerce Order Processing Engine

A robust, multi-threaded Java backend simulator built from scratch over **10 days (1 hour daily)**. This project serves as a comprehensive portfolio piece demonstrating structural mastery of Core Java, production-grade exception handling, functional data pipelines, and thread-safe concurrent systems.

---

## 🚀 Project Overview

The engine simulates a high-traffic e-commerce microservice. Multiple automated customer threads concurrently drop dynamic orders into a central queue, while a managed pool of background worker threads processes them, updates an in-memory inventory pool without data corruption, handles out-of-stock bottlenecks, and runs instantaneous functional analytics.

```
[Customer Thread 1] ──┐
[Customer Thread 2] ──┼─> [LinkedBlockingQueue] ─> [ExecutorService Pool] ─> [Synchronized Stock Update]
[Customer Thread 3] ──┘                             (Worker 1 & Worker 2)                 │
                                                                                           ▼
                                                                                   [Java 8 Stream Report]

```

---

## 📅 10-Day Curriculum & Technical Milestone Breakdown

### Phase 1: Core Architecture & OOPs (Days 1–3)

* **Day 1: Project Setup, Modern Basics, & Wrapper Classes**
* *Topics Covered:* Core data models, JVM memory stack allocation, Enums, and the modern `java.time` API.
* *Implementation:* Established the `Order` structure using wrapper objects (`Integer`, `Double`) instead of primitive variants to support null-safety bounds. Integrated `LocalDateTime`.


* **Day 2: Domain Modelling via Encapsulation & Inheritance**
* *Topics Covered:* Access modifiers (`private`/`public`), data hiding, constructors, and the `super` keyword.
* *Implementation:* Designed an abstract `Product` base class containing encapsulated metrics, extending it into concrete `Electronics` and `Clothing` domains.


* **Day 3: Abstraction & Runtime Polymorphism**
* *Topics Covered:* Interfaces vs. Abstract Classes, overriding contracts, loose coupling ("coding to an interface").
* *Implementation:* Created the decoupled `OrderProcessor` interface implemented by `DefaultOrderProcessor`, hiding execution internals from the runtime application.



### Phase 2: Data Management & Stability (Days 4–6)

* **Day 4: In-Memory Storage via the Collections Framework**
* *Topics Covered:* Collection hierarchies, hashing performance, $O(1)$ lookups, and resizable linear arrays.
* *Implementation:* Modeled a centralized warehouse database using a `HashMap<Integer, Product>` alongside a `List<Order>` history ledger.


* **Day 5: Production-Grade Exception Handling**
* *Topics Covered:* Checked vs. Unchecked exceptions, `Throwable` architecture, and `try-catch-finally` boundaries.
* *Implementation:* Custom-engineered `InsufficientStockException` (checked) and `InvalidOrderException` (runtime) to act as absolute transaction boundaries during low-stock states.


* **Day 6: Null Safety with Java 8 Optionals**
* *Topics Covered:* Defensive programming, container types, and functional alternatives to dangerous `null` returns.
* *Implementation:* Re-architected catalog scanning utilizing `Optional<Product>`, replacing rigid `if (x != null)` statements with the fluent `.orElseThrow()` pattern.



### Phase 3: High-Performance Concurrency & Streaming (Days 7–10)

* **Day 7: Functional Programming & Basic Streams**
* *Topics Covered:* Functional Interfaces, lambda expressions (`->`), method references (`::`), intermediate vs. terminal stream execution.
* *Implementation:* Built a revenue calculation engine leveraging aggregate `.filter()`, `.mapToDouble()`, and `.sum()` operators.


* **Day 8: Advanced Stream Analytics & Data Grouping**
* *Topics Covered:* Data fragmentation reduction, `Collectors.groupingBy()`, downstream counters, and stream flattening via `flatMap()`.
* *Implementation:* Implemented real-time dashboard analytics categorizing transaction success indices and total aggregate item unit performance.


* **Day 9: Multithreading & The Producer Pattern**
* *Topics Covered:* Thread lifecycles, the `Runnable` interface, task scheduling, and thread-safe data pipelines.
* *Implementation:* Formulated an asynchronous `OrderPlacer` engine utilizing a thread-safe `LinkedBlockingQueue<Order>` to prevent item collision.


* **Day 10: Managed Concurrency & Race Condition Elimination**
* *Topics Covered:* Thread Pools, `ExecutorService`, critical section blocks, object level monitors (`synchronized`), and clean execution termination.
* *Implementation:* Deployed a thread pool allocating concurrent fulfillment consumers. Thread safety was maintained by applying atomic object-level locks over checking and decrementing routines to guarantee complete state consistency.



---

## 🛠️ Core Concepts Mastered

1. **Object-Oriented Design:** Enforced strict abstraction boundaries ensuring processing business layers do not directly depend on raw item subclasses.
2. **State Protection:** Applied structural encapsulation across mutable attributes (`id`, `amount`, `stock`), allowing modification only via secure getters/setters.
3. **Transactional Resilience:** Configured strict `try-catch-finally` parameters preventing corrupt or failed orders from inserting records into historical ledgers.
4. **Functional Declarative Programming:** Streamlined complex statistical reporting calculations down into unified multi-line stream chains.
5. **Concurrent Coordination:** Resolved multi-core read/write race conditions using synchronized monitor lock boundaries across mutating inventory allocations.

---

## 🖥️ How To Run the Application

### Prerequisites

* Java Development Kit (JDK) 8 or higher installed.

### Execution

1. Clone this repository or copy the code file.
2. Compile the program using your terminal:
```bash
javac Main.java

```


3. Run the application:
```bash
java Main

```



### Sample Output

```text
--- Booting Backend Processing Engine ---
👤 Customer Thread #1 started browsing...
👤 Customer Thread #2 started browsing...
👤 Customer Thread #3 started browsing...
🛒 Customer #2 placed Order #8491 into the queue.
🛒 Customer #1 placed Order #2048 into the queue.

[⚙️ Worker Thread pool-1-thread-1] Processing Order #8491
[📦 Stock Updated] Acer Laptop remaining: 1
✅ Order #8491 complete! Status: PROCESSED

[⚙️ Worker Thread pool-1-thread-2] Processing Order #2048
[📦 Stock Updated] Acer Laptop remaining: 0
✅ Order #2048 complete! Status: PROCESSED

🛒 Customer #3 placed Order #1105 into the queue.
[⚙️ Worker Thread pool-1-thread-1] Processing Order #1105
❌ Order #1105 Processing Blocked: Stock error: 'Acer Laptop' is out of stock!

--- All orders handled. Workers shutting down cleanly. ---

========================================
     FINAL STORE ANALYTICS REPORT       
========================================
Total Realized Revenue: $1000.00

--- Order Status Summary ---
 * PROCESSED: 2
 * FAILED: 1
========================================

```
