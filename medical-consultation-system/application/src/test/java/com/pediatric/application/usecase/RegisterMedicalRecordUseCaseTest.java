package com.pediatric.application.usecase;

import com.pediatric.application.dto.PatientHistoryDTO;
import com.pediatric.application.dto.RegisterMedicalRecordCommand;
import com.pediatric.application.port.input.RegisterMedicalRecordUseCase;
import com.pediatric.application.port.output.*;
import com.pediatric.domain.model.*;
import com.pediatric.domain.exception.BusinessRuleException;
import com.pediatric.domain.exception.EntityNotFoundException;
import com.pediatric.domain.valueobject.Address;
import com.pediatric.domain.valueobject.Gender;
import com.pediatric.domain.valueobject.Phone;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for RegisterMedicalRecordUseCaseImpl.
 * Uses simple in-memory mock repositories for isolation.
 */
class RegisterMedicalRecordUseCaseTest {

    private RegisterMedicalRecordUseCase useCase;
    private MockPatientRepository patientRepository;
    private MockConsultationRepository consultationRepository;
    private MockMedicalRecordRepository medicalRecordRepository;
    private MockMedicationRepository medicationRepository;
    private MockExamRepository examRepository;

    // Test fixtures
    private Patient testPatient;
    private Doctor testDoctor;
    private Consultation testConsultation;
    private Medication testMedication;
    private Exam testExam;

    @BeforeEach
    void setUp() {
        // Initialize mock repositories
        patientRepository = new MockPatientRepository();
        consultationRepository = new MockConsultationRepository();
        medicalRecordRepository = new MockMedicalRecordRepository();
        medicationRepository = new MockMedicationRepository();
        examRepository = new MockExamRepository();

        // Create use case with dependencies
        useCase = new RegisterMedicalRecordUseCaseImpl(
                consultationRepository,
                patientRepository,
                medicalRecordRepository,
                medicationRepository,
                examRepository
        );

        // Setup test fixtures
        setupTestData();
    }

    private void setupTestData() {
        // Create test patient
        testPatient = Patient.builder()
                .childName("Test Child")
                .guardianName("Test Guardian")
                .birthDate(LocalDate.now().minusYears(5))
                .gender(Gender.MALE)
                .address(Address.builder()
                        .street("Test Street")
                        .number("1")
                        .neighborhood("Test")
                        .city("Test City")
                        .state("TS")
                        .zipCode("12345-678")
                        .build())
                .phone(Phone.mobile("55", "11", "999999999"))
                .build();
        patientRepository.save(testPatient);

        // Create test doctor (we don't need a repository for this use case)
        testDoctor = Doctor.builder()
                .name("Test Doctor")
                .crm("99999/SP")
                .specialty("Pediatrics")
                .build();

        // Create test consultation
        testConsultation = Consultation.builder()
                .patientId(testPatient.getId())
                .doctorId(testDoctor.getId())
                .dateTime(LocalDateTime.now())
                .isNewPatient(true)
                .isScheduled(true)
                .build();
        consultationRepository.save(testConsultation);

        // Create test medication
        testMedication = Medication.builder()
                .name("Test Medication")
                .activeIngredient("Test Ingredient")
                .concentration("10mg")
                .form(Medication.MedicationForm.SYRUP)
                .build();
        medicationRepository.save(testMedication);

        // Create test exam
        testExam = Exam.builder()
                .name("Test Exam")
                .code("TEST001")
                .category(Exam.ExamCategory.LABORATORY)
                .build();
        examRepository.save(testExam);
    }

    @Nested
    @DisplayName("getScheduledConsultation tests")
    class GetScheduledConsultationTests {

        @Test
        @DisplayName("Should return consultation when it exists and is scheduled")
        void shouldReturnScheduledConsultation() {
            Consultation result = useCase.getScheduledConsultation(testConsultation.getId());

            assertNotNull(result);
            assertEquals(testConsultation.getId(), result.getId());
            assertEquals(Consultation.ConsultationStatus.SCHEDULED, result.getStatus());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when consultation doesn't exist")
        void shouldThrowWhenConsultationNotFound() {
            UUID nonExistentId = UUID.randomUUID();

            EntityNotFoundException exception = assertThrows(
                    EntityNotFoundException.class,
                    () -> useCase.getScheduledConsultation(nonExistentId)
            );

            assertTrue(exception.getMessage().contains("Consultation"));
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when consultation is completed")
        void shouldThrowWhenConsultationCompleted() {
            testConsultation.startConsultation();
            testConsultation.completeConsultation();
            consultationRepository.save(testConsultation);

            BusinessRuleException exception = assertThrows(
                    BusinessRuleException.class,
                    () -> useCase.getScheduledConsultation(testConsultation.getId())
            );

            assertEquals("CONSULTATION_ALREADY_COMPLETED", exception.getRuleCode());
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when consultation is cancelled")
        void shouldThrowWhenConsultationCancelled() {
            testConsultation.cancelConsultation();
            consultationRepository.save(testConsultation);

            BusinessRuleException exception = assertThrows(
                    BusinessRuleException.class,
                    () -> useCase.getScheduledConsultation(testConsultation.getId())
            );

            assertEquals("CONSULTATION_CANCELLED", exception.getRuleCode());
        }

        @Test
        @DisplayName("Should throw BusinessRuleException when medical record already exists")
        void shouldThrowWhenRecordAlreadyExists() {
            // Create an existing medical record for the consultation
            MedicalRecord existingRecord = MedicalRecord.builder()
                    .consultationId(testConsultation.getId())
                    .patientId(testPatient.getId())
                    .doctorId(testDoctor.getId())
                    .weight(15.0)
                    .height(100.0)
                    .symptomDescription("Test symptoms")
                    .build();
            medicalRecordRepository.save(existingRecord);

            BusinessRuleException exception = assertThrows(
                    BusinessRuleException.class,
                    () -> useCase.getScheduledConsultation(testConsultation.getId())
            );

            assertEquals("RECORD_ALREADY_EXISTS", exception.getRuleCode());
        }
    }

    @Nested
    @DisplayName("getPatientHistory tests")
    class GetPatientHistoryTests {

        @Test
        @DisplayName("Should return patient history with no previous records")
        void shouldReturnEmptyHistory() {
            PatientHistoryDTO history = useCase.getPatientHistory(testPatient.getId());

            assertNotNull(history);
            assertEquals(testPatient.getId(), history.getPatient().getId());
            assertEquals(0, history.getTotalConsultations());
            assertFalse(history.hasHistory());
        }

        @Test
        @DisplayName("Should return patient history with previous records")
        void shouldReturnHistoryWithRecords() {
            // Create a completed consultation and medical record
            Consultation completedConsultation = Consultation.builder()
                    .patientId(testPatient.getId())
                    .doctorId(testDoctor.getId())
                    .dateTime(LocalDateTime.now().minusDays(30))
                    .status(Consultation.ConsultationStatus.COMPLETED)
                    .build();
            consultationRepository.save(completedConsultation);

            MedicalRecord previousRecord = MedicalRecord.builder()
                    .consultationId(completedConsultation.getId())
                    .patientId(testPatient.getId())
                    .doctorId(testDoctor.getId())
                    .weight(14.5)
                    .height(98.0)
                    .symptomDescription("Previous symptoms")
                    .build();
            medicalRecordRepository.save(previousRecord);

            PatientHistoryDTO history = useCase.getPatientHistory(testPatient.getId());

            assertNotNull(history);
            assertEquals(1, history.getTotalConsultations());
            assertTrue(history.hasHistory());
            assertEquals(14.5, history.getLastKnownWeight());
            assertEquals(98.0, history.getLastKnownHeight());
        }

        @Test
        @DisplayName("Should throw EntityNotFoundException when patient doesn't exist")
        void shouldThrowWhenPatientNotFound() {
            UUID nonExistentId = UUID.randomUUID();

            assertThrows(
                    EntityNotFoundException.class,
                    () -> useCase.getPatientHistory(nonExistentId)
            );
        }
    }

    @Nested
    @DisplayName("registerMedicalRecord tests")
    class RegisterMedicalRecordTests {

        @Test
        @DisplayName("Should successfully register medical record")
        void shouldRegisterMedicalRecord() {
            RegisterMedicalRecordCommand command = RegisterMedicalRecordCommand.builder()
                    .consultationId(testConsultation.getId())
                    .weight(15.0)
                    .height(105.0)
                    .symptomDescription("Fever and cough")
                    .clinicalObservation("Mild inflammation")
                    .diagnosis("Common cold")
                    .addPrescription(new RegisterMedicalRecordCommand.PrescriptionData(
                            testMedication.getId(),
                            "5ml",
                            Prescription.AdministrationRoute.ORAL,
                            "5 days",
                            "3 times daily",
                            null
                    ))
                    .addRequestedExamId(testExam.getId())
                    .build();

            MedicalRecord result = useCase.registerMedicalRecord(command);

            assertNotNull(result);
            assertEquals(15.0, result.getWeight());
            assertEquals(105.0, result.getHeight());
            assertEquals("Fever and cough", result.getSymptomDescription());
            assertEquals(1, result.getPrescriptionCount());
            assertEquals(1, result.getRequestedExamCount());

            // Verify consultation is completed
            Consultation updatedConsultation = consultationRepository.findById(testConsultation.getId()).orElseThrow();
            assertEquals(Consultation.ConsultationStatus.COMPLETED, updatedConsultation.getStatus());
        }

        @Test
        @DisplayName("Should throw when medication doesn't exist")
        void shouldThrowWhenMedicationNotFound() {
            UUID nonExistentMedicationId = UUID.randomUUID();

            RegisterMedicalRecordCommand command = RegisterMedicalRecordCommand.builder()
                    .consultationId(testConsultation.getId())
                    .weight(15.0)
                    .height(105.0)
                    .symptomDescription("Test symptoms")
                    .addPrescription(new RegisterMedicalRecordCommand.PrescriptionData(
                            nonExistentMedicationId,
                            "5ml",
                            Prescription.AdministrationRoute.ORAL,
                            "5 days",
                            "3 times daily",
                            null
                    ))
                    .build();

            assertThrows(
                    EntityNotFoundException.class,
                    () -> useCase.registerMedicalRecord(command)
            );
        }

        @Test
        @DisplayName("Should throw when exam doesn't exist")
        void shouldThrowWhenExamNotFound() {
            UUID nonExistentExamId = UUID.randomUUID();

            RegisterMedicalRecordCommand command = RegisterMedicalRecordCommand.builder()
                    .consultationId(testConsultation.getId())
                    .weight(15.0)
                    .height(105.0)
                    .symptomDescription("Test symptoms")
                    .addRequestedExamId(nonExistentExamId)
                    .build();

            assertThrows(
                    EntityNotFoundException.class,
                    () -> useCase.registerMedicalRecord(command)
            );
        }
    }

    // Simple mock repository implementations for testing
    
    static class MockPatientRepository implements PatientPortOut {
        private final Map<UUID, Patient> storage = new HashMap<>();

        @Override
        public Patient save(Patient patient) {
            storage.put(patient.getId(), patient);
            return patient;
        }

        @Override
        public Optional<Patient> findById(UUID id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<Patient> findByChildName(String childName) {
            return List.of();
        }

        @Override
        public List<Patient> findByGuardianName(String guardianName) {
            return List.of();
        }

        @Override
        public List<Patient> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public void deleteById(UUID id) {
            storage.remove(id);
        }

        @Override
        public boolean existsById(UUID id) {
            return storage.containsKey(id);
        }

        @Override
        public long count() {
            return storage.size();
        }
    }

    static class MockConsultationRepository implements ConsultationPortOut {
        private final Map<UUID, Consultation> storage = new HashMap<>();

        @Override
        public Consultation save(Consultation consultation) {
            storage.put(consultation.getId(), consultation);
            return consultation;
        }

        @Override
        public Optional<Consultation> findById(UUID id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<Consultation> findByPatientId(UUID patientId) {
            return storage.values().stream()
                    .filter(c -> c.getPatientId().equals(patientId))
                    .toList();
        }

        @Override
        public List<Consultation> findByDoctorId(UUID doctorId) {
            return List.of();
        }

        @Override
        public List<Consultation> findByDate(java.time.LocalDate date) {
            return List.of();
        }

        @Override
        public List<Consultation> findByDateTimeBetween(LocalDateTime start, LocalDateTime end) {
            return List.of();
        }

        @Override
        public List<Consultation> findByStatus(Consultation.ConsultationStatus status) {
            return List.of();
        }

        @Override
        public List<Consultation> findScheduledByDoctorAndDate(UUID doctorId, java.time.LocalDate date) {
            return List.of();
        }

        @Override
        public void deleteById(UUID id) {
            storage.remove(id);
        }

        @Override
        public boolean existsById(UUID id) {
            return storage.containsKey(id);
        }
    }

    static class MockMedicalRecordRepository implements MedicalRecordPortOut {
        private final Map<UUID, MedicalRecord> storage = new HashMap<>();

        @Override
        public MedicalRecord save(MedicalRecord medicalRecord) {
            storage.put(medicalRecord.getId(), medicalRecord);
            return medicalRecord;
        }

        @Override
        public Optional<MedicalRecord> findById(UUID id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public Optional<MedicalRecord> findByConsultationId(UUID consultationId) {
            return storage.values().stream()
                    .filter(r -> r.getConsultationId().equals(consultationId))
                    .findFirst();
        }

        @Override
        public List<MedicalRecord> findByPatientId(UUID patientId) {
            return storage.values().stream()
                    .filter(r -> r.getPatientId().equals(patientId))
                    .toList();
        }

        @Override
        public List<MedicalRecord> findByDoctorId(UUID doctorId) {
            return List.of();
        }

        @Override
        public List<MedicalRecord> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
            return List.of();
        }

        @Override
        public List<MedicalRecord> findRecentByPatientId(UUID patientId, int limit) {
            return storage.values().stream()
                    .filter(r -> r.getPatientId().equals(patientId))
                    .limit(limit)
                    .toList();
        }

        @Override
        public boolean existsByConsultationId(UUID consultationId) {
            return storage.values().stream()
                    .anyMatch(r -> r.getConsultationId().equals(consultationId));
        }

        @Override
        public void deleteById(UUID id) {
            storage.remove(id);
        }

        @Override
        public long countByPatientId(UUID patientId) {
            return storage.values().stream()
                    .filter(r -> r.getPatientId().equals(patientId))
                    .count();
        }
    }

    static class MockMedicationRepository implements MedicationPortOut {
        private final Map<UUID, Medication> storage = new HashMap<>();

        @Override
        public Medication save(Medication medication) {
            storage.put(medication.getId(), medication);
            return medication;
        }

        @Override
        public Optional<Medication> findById(UUID id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public List<Medication> findByName(String name) {
            return List.of();
        }

        @Override
        public List<Medication> findByActiveIngredient(String activeIngredient) {
            return List.of();
        }

        @Override
        public List<Medication> findAllActive() {
            return List.of();
        }

        @Override
        public List<Medication> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public void deleteById(UUID id) {
            storage.remove(id);
        }

        @Override
        public boolean existsById(UUID id) {
            return storage.containsKey(id);
        }
    }

    static class MockExamRepository implements ExamPortOut {
        private final Map<UUID, Exam> storage = new HashMap<>();

        @Override
        public Exam save(Exam exam) {
            storage.put(exam.getId(), exam);
            return exam;
        }

        @Override
        public Optional<Exam> findById(UUID id) {
            return Optional.ofNullable(storage.get(id));
        }

        @Override
        public Optional<Exam> findByCode(String code) {
            return Optional.empty();
        }

        @Override
        public List<Exam> findByName(String name) {
            return List.of();
        }

        @Override
        public List<Exam> findByCategory(Exam.ExamCategory category) {
            return List.of();
        }

        @Override
        public List<Exam> findAllActive() {
            return List.of();
        }

        @Override
        public List<Exam> findAll() {
            return new ArrayList<>(storage.values());
        }

        @Override
        public void deleteById(UUID id) {
            storage.remove(id);
        }

        @Override
        public boolean existsById(UUID id) {
            return storage.containsKey(id);
        }
    }
}
