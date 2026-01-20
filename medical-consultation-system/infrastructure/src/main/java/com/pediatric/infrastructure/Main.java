package com.pediatric.infrastructure;

import com.pediatric.application.dto.PatientHistoryDTO;
import com.pediatric.application.dto.RegisterMedicalRecordCommand;
import com.pediatric.application.port.input.RegisterMedicalRecordUseCase;
import com.pediatric.application.usecase.RegisterMedicalRecordUseCaseImpl;
import com.pediatric.domain.entity.*;
import com.pediatric.domain.valueobject.Address;
import com.pediatric.domain.valueobject.Gender;
import com.pediatric.domain.valueobject.Phone;
import com.pediatric.infrastructure.adapter.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Demonstration of the Medical Consultation System.
 * This serves as an Inbound Adapter (entry point) for testing the use case.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  Medical Consultation System - Pediatric Focus");
        System.out.println("  Hexagonal Architecture Demo");
        System.out.println("==============================================\n");

        // Initialize repositories (Outbound Adapters)
        var patientRepository = new InMemoryPatientRepository();
        var consultationRepository = new InMemoryConsultationRepository();
        var medicalRecordRepository = new InMemoryMedicalRecordRepository();
        var doctorRepository = new InMemoryDoctorRepository();
        var medicationRepository = new InMemoryMedicationRepository();
        var examRepository = new InMemoryExamRepository();

        // Create the Use Case with dependencies injected
        RegisterMedicalRecordUseCase useCase = new RegisterMedicalRecordUseCaseImpl(
                consultationRepository,
                patientRepository,
                medicalRecordRepository,
                medicationRepository,
                examRepository
        );

        // === Setup Test Data ===
        System.out.println("Setting up test data...\n");

        // Create a Doctor
        Doctor doctor = Doctor.builder()
                .name("Maria Santos")
                .crm("12345/SP")
                .specialty("Pediatrics")
                .active(true)
                .build();
        doctorRepository.save(doctor);
        System.out.println("Created Doctor: " + doctor);

        // Create a Patient (Child)
        Address address = Address.builder()
                .street("Rua das Flores")
                .number("123")
                .complement("Apt 45")
                .neighborhood("Centro")
                .city("São Paulo")
                .state("SP")
                .zipCode("01234-567")
                .build();

        Phone phone = Phone.mobile("55", "11", "987654321");

        Patient patient = Patient.builder()
                .childName("Pedro Silva")
                .guardianName("Ana Silva")
                .birthDate(LocalDate.now().minusYears(5).minusMonths(3))
                .gender(Gender.MALE)
                .address(address)
                .phone(phone)
                .build();
        patientRepository.save(patient);
        System.out.println("Created Patient: " + patient);

        // Create Medications
        Medication amoxicillin = Medication.builder()
                .name("Amoxicillin")
                .activeIngredient("Amoxicillin Trihydrate")
                .concentration("250mg/5ml")
                .form(Medication.MedicationForm.SUSPENSION)
                .manufacturer("Generic Labs")
                .requiresPrescription(true)
                .build();
        medicationRepository.save(amoxicillin);

        Medication ibuprofen = Medication.builder()
                .name("Ibuprofen")
                .activeIngredient("Ibuprofen")
                .concentration("100mg/5ml")
                .form(Medication.MedicationForm.SUSPENSION)
                .manufacturer("PharmaCo")
                .requiresPrescription(false)
                .build();
        medicationRepository.save(ibuprofen);
        System.out.println("Created Medications: Amoxicillin, Ibuprofen");

        // Create Exams
        Exam bloodTest = Exam.builder()
                .name("Complete Blood Count")
                .code("CBC001")
                .category(Exam.ExamCategory.LABORATORY)
                .description("Full blood panel analysis")
                .requiresFasting(true)
                .preparationInstructions("8-hour fasting required")
                .build();
        examRepository.save(bloodTest);

        Exam chestXray = Exam.builder()
                .name("Chest X-Ray")
                .code("XRAY001")
                .category(Exam.ExamCategory.IMAGING)
                .description("Frontal chest radiograph")
                .requiresFasting(false)
                .build();
        examRepository.save(chestXray);
        System.out.println("Created Exams: Blood Count, Chest X-Ray");

        // Create a Consultation (Scheduled)
        Consultation consultation = Consultation.builder()
                .patientId(patient.getId())
                .doctorId(doctor.getId())
                .dateTime(LocalDateTime.now())
                .isNewPatient(false)
                .isScheduled(true)
                .build();
        consultationRepository.save(consultation);
        System.out.println("Created Consultation: " + consultation);

        System.out.println("\n--- Starting Use Case Flow ---\n");

        // === Use Case Flow ===

        // Step 1: Retrieve Scheduled Consultation
        System.out.println("Step 1: Retrieving scheduled consultation...");
        Consultation retrievedConsultation = useCase.getScheduledConsultation(consultation.getId());
        System.out.println("  Retrieved: " + retrievedConsultation.getStatus());

        // Step 2: Get Patient History
        System.out.println("\nStep 2: Getting patient history...");
        PatientHistoryDTO history = useCase.getPatientHistory(patient.getId());
        System.out.println("  Patient: " + history.getPatient().getChildName());
        System.out.println("  Age: " + history.getPatient().getAgeDescription());
        System.out.println("  Previous Consultations: " + history.getTotalConsultations());
        System.out.println("  Has History: " + history.hasHistory());

        // Step 3: Register Medical Record
        System.out.println("\nStep 3: Registering medical record...");
        RegisterMedicalRecordCommand command = RegisterMedicalRecordCommand.builder()
                .consultationId(consultation.getId())
                .weight(18.5)
                .height(110.0)
                .temperature(37.8)
                .heartRate(95)
                .symptomDescription("Patient presents with fever (37.8°C), cough, and mild throat pain for 2 days.")
                .clinicalObservation("Throat shows mild inflammation. Lungs clear on auscultation. No signs of respiratory distress.")
                .diagnosis("Upper Respiratory Tract Infection (URI)")
                .treatmentPlan("Antibiotics for 7 days, antipyretic as needed, rest and hydration.")
                .addPrescription(new RegisterMedicalRecordCommand.PrescriptionData(
                        amoxicillin.getId(),
                        "5ml",
                        Prescription.AdministrationRoute.ORAL,
                        "7 days",
                        "Every 8 hours",
                        "Take with food"
                ))
                .addPrescription(new RegisterMedicalRecordCommand.PrescriptionData(
                        ibuprofen.getId(),
                        "5ml",
                        Prescription.AdministrationRoute.ORAL,
                        "As needed",
                        "Every 6-8 hours if fever > 38°C",
                        "Maximum 4 doses in 24 hours"
                ))
                .addRequestedExamId(bloodTest.getId())
                .build();

        MedicalRecord medicalRecord = useCase.registerMedicalRecord(command);

        System.out.println("\n=== Medical Record Created Successfully ===");
        System.out.println("  Record ID: " + medicalRecord.getId());
        System.out.println("  Weight: " + medicalRecord.getWeight() + " kg");
        System.out.println("  Height: " + medicalRecord.getHeight() + " cm");
        System.out.println("  BMI: " + String.format("%.2f", medicalRecord.calculateBMI()) + " (" + medicalRecord.getBMIClassification() + ")");
        System.out.println("  Symptoms: " + medicalRecord.getSymptomDescription());
        System.out.println("  Diagnosis: " + medicalRecord.getDiagnosis().orElse("N/A"));
        System.out.println("  Prescriptions: " + medicalRecord.getPrescriptionCount());
        System.out.println("  Exams Requested: " + medicalRecord.getRequestedExamCount());

        // Verify consultation was completed
        Consultation completedConsultation = consultationRepository.findById(consultation.getId()).orElseThrow();
        System.out.println("\n  Consultation Status: " + completedConsultation.getStatus());

        System.out.println("\n==============================================");
        System.out.println("  Demo completed successfully!");
        System.out.println("==============================================");
    }
}
