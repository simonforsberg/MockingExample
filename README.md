# PaymentProcessor
## Refactoring Decisions

The `PaymentProcessor` class was refactored by extracting external dependencies into separate interfaces:
- `PaymentService`
- `PaymentRepository`
- `NotificationService`

These interfaces represent clear responsibilities and abstract away the implementations of external systems.

`PaymentApiResponse` was implemented as a `record` to represent a simple, immutable value object that is easy to use and test.

Dependency Injection was applied using constructor injection, where `PaymentProcessor` receives its dependencies through the constructor. This makes it possible to replace real implementations with test doubles (mocks) during unit testing, without modifying production code.

Unit tests were written using Mockito following the Test-Driven Development (TDD) cycle, where tests define the expected behavior before or alongside the implementation. The tests verify both successful and failed payment scenarios while keeping the business logic isolated from external side effects.
