# Evolution Strategy - Phase 2 (Theoretical)

This document outlines how the Medical Consultation System can be evolved to support additional features while maintaining the Hexagonal Architecture principles.

---

## 1. Online Scheduling & Payments

### Recommended Pattern: **Strategy Pattern**

The Strategy Pattern is ideal for payment processing because:

1. **Multiple Payment Methods**: Different payment providers (Credit Card, PIX, Bank Transfer, Health Insurance) can be encapsulated as interchangeable strategies.

2. **Open/Closed Principle**: New payment methods can be added without modifying existing code.

### Implementation Approach:

```java
// Port Definition (Output)
public interface PaymentGateway {
    PaymentResult processPayment(PaymentRequest request);
    PaymentResult refund(String transactionId, BigDecimal amount);
}

// Strategy Interface
public interface PaymentStrategy {
    PaymentResult execute(PaymentDetails details);
    boolean supports(PaymentMethod method);
}

// Concrete Strategies
public class CreditCardPaymentStrategy implements PaymentStrategy { ... }
public class PixPaymentStrategy implements PaymentStrategy { ... }
public class HealthInsurancePaymentStrategy implements PaymentStrategy { ... }

// Context
public class PaymentProcessor {
    private final List<PaymentStrategy> strategies;
    
    public PaymentResult process(PaymentDetails details) {
        return strategies.stream()
            .filter(s -> s.supports(details.getMethod()))
            .findFirst()
            .orElseThrow()
            .execute(details);
    }
}
```

### For Online Scheduling:
- Create `ScheduleConsultationUseCase` (Input Port)
- Add `AvailabilityService` (Output Port) to check doctor availability
- Implement calendar adapters (Google Calendar, internal calendar)

---

## 2. Notifications (Reminders)

### Recommended Pattern: **Observer Pattern**

The Observer Pattern is perfect for notifications because:

1. **Loose Coupling**: The domain events don't need to know about notification channels.

2. **Multiple Observers**: Email, SMS, Push notifications can all observe the same events.

3. **Extensibility**: New notification channels can be added without modifying the event source.

### Implementation Approach:

```java
// Domain Events
public record ConsultationScheduledEvent(UUID consultationId, UUID patientId, LocalDateTime dateTime) {}
public record ConsultationReminderEvent(UUID consultationId, int hoursUntil) {}

// Observer Interface (Output Port)
public interface NotificationObserver {
    void onConsultationScheduled(ConsultationScheduledEvent event);
    void onConsultationReminder(ConsultationReminderEvent event);
}

// Concrete Observers (Adapters)
public class EmailNotificationAdapter implements NotificationObserver {
    private final EmailService emailService;
    
    @Override
    public void onConsultationScheduled(ConsultationScheduledEvent event) {
        // Send confirmation email
    }
}

public class SmsNotificationAdapter implements NotificationObserver { ... }
public class PushNotificationAdapter implements NotificationObserver { ... }

// Event Publisher (in Application Layer)
public class DomainEventPublisher {
    private final List<NotificationObserver> observers;
    
    public void publish(Object event) {
        observers.forEach(observer -> {
            if (event instanceof ConsultationScheduledEvent e) {
                observer.onConsultationScheduled(e);
            }
            // ... other event types
        });
    }
}
```

### Alternative: Event-Driven Architecture
For larger systems, consider using a message broker (RabbitMQ, Kafka) as an adapter to decouple event production from consumption completely.

---

## 3. Multiple Clinics / Scalability

### Recommended Pattern: **Abstract Factory Pattern**

The Abstract Factory Pattern is suitable for multi-clinic support because:

1. **Consistent Object Families**: Each clinic may have its own set of configurations, repositories, and services.

2. **Isolation**: Different clinics can have different database schemas, notification preferences, and business rules.

3. **Scalability**: New clinics can be onboarded without modifying core business logic.

### Implementation Approach:

```java
// Abstract Factory
public interface ClinicFactory {
    PatientRepository createPatientRepository();
    ConsultationRepository createConsultationRepository();
    MedicalRecordRepository createMedicalRecordRepository();
    NotificationService createNotificationService();
    ClinicConfiguration getConfiguration();
}

// Concrete Factories
public class ClinicSaoPauloFactory implements ClinicFactory {
    @Override
    public PatientRepository createPatientRepository() {
        return new PostgresPatientRepository(saoPauloDataSource);
    }
    // ... other factory methods
}

public class ClinicRioFactory implements ClinicFactory {
    @Override
    public PatientRepository createPatientRepository() {
        return new PostgresPatientRepository(rioDataSource);
    }
    // ... other factory methods
}

// Clinic Context (manages which factory to use)
public class ClinicContext {
    private final Map<String, ClinicFactory> factories;
    
    public ClinicFactory getFactory(String clinicId) {
        return factories.get(clinicId);
    }
}

// Use Case Factory
public class UseCaseFactory {
    private final ClinicContext clinicContext;
    
    public RegisterMedicalRecordUseCase createRegisterMedicalRecordUseCase(String clinicId) {
        ClinicFactory factory = clinicContext.getFactory(clinicId);
        return new RegisterMedicalRecordUseCaseImpl(
            factory.createConsultationRepository(),
            factory.createPatientRepository(),
            factory.createMedicalRecordRepository(),
            // ... other dependencies
        );
    }
}
```

### Multi-Tenancy Considerations:

1. **Database Strategy**:
   - **Separate Databases**: Each clinic has its own database (highest isolation)
   - **Shared Database, Separate Schemas**: One database, multiple schemas
   - **Shared Schema with Tenant ID**: Single schema with `clinic_id` column (most cost-effective)

2. **Additional Patterns**:
   - **Tenant Context**: Use ThreadLocal or request-scoped beans to maintain current clinic context
   - **Repository Decorator**: Add tenant filtering transparently

```java
public class TenantAwarePatientRepository implements PatientRepository {
    private final PatientRepository delegate;
    private final TenantContext tenantContext;
    
    @Override
    public List<Patient> findAll() {
        return delegate.findByClinicId(tenantContext.getCurrentClinicId());
    }
}
```

---

## Summary Table

| Feature | Pattern | Key Benefit |
|---------|---------|-------------|
| Payments | Strategy | Interchangeable payment methods |
| Notifications | Observer | Loose coupling, multiple channels |
| Multi-Clinic | Abstract Factory | Consistent object families per clinic |

---

## Architecture Principles Maintained

Throughout all evolutions, we maintain:

1. **Domain Isolation**: Core business logic remains framework-agnostic
2. **Dependency Inversion**: High-level modules depend on abstractions
3. **Single Responsibility**: Each class has one reason to change
4. **Open/Closed**: Open for extension, closed for modification
5. **Testability**: All components can be tested in isolation

The Hexagonal Architecture ensures that these new features can be added as new adapters and ports without modifying the existing domain logic.
