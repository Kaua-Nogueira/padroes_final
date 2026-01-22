package com.pediatric.infrastructure;

import com.pediatric.application.dto.PatientHistoryDTO;
import com.pediatric.application.dto.RegisterMedicalRecordCommand;
import com.pediatric.application.usecase.ConsultationQueryService;
import com.pediatric.application.usecase.PatientHistoryQueryService;
import com.pediatric.application.usecase.RegisterMedicalRecordService;
import com.pediatric.domain.model.*;
import com.pediatric.domain.valueobject.*;
import com.pediatric.infrastructure.adapter.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Demonstration of the Medical Consultation System.
 * This serves as an Inbound Adapter (entry point) for testing the services.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("  Medical Consultation System - Pediatric Focus");
        System.out.println("  Hexagonal Architecture Demo");
        System.out.println("==============================================\n");

        // 1. Initialize repositories (Outbound Adapters)
        var patientRepository = new InMemoryPatientRepository();
        var consultationRepository = new InMemoryConsultationRepository();
        var medicalRecordRepository = new InMemoryMedicalRecordRepository();
        var doctorRepository = new InMemoryDoctorRepository();
        var medicationRepository = new InMemoryMedicationRepository();
        var examRepository = new InMemoryExamRepository();

        // 2. Initialize Services (Use Cases) - Segregated by Responsibility (SRP/ISP)
        
        // Service for looking up consultations
        ConsultationQueryService consultationQueryService = new ConsultationQueryService(
                consultationRepository,
                medicalRecordRepository
        );

        // Service for viewing patient history
        PatientHistoryQueryService patientHistoryService = new PatientHistoryQueryService(
                patientRepository,
                medicalRecordRepository
        );

        // Service for performing the action of registering a record (Command)
        RegisterMedicalRecordService registerRecordService = new RegisterMedicalRecordService(
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
        System.out.println("Created Doctor: " + doctor.getName());

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
                .addPhone(phone)
                .build();
        patientRepository.save(patient);
        System.out.println("Created Patient: " + patient.getChildName());

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
        System.out.println("Created Consultation ID: " + consultation.getId());

        System.out.println("\n--- Starting Use Case Flow (Simulating Frontend) ---\n");

        // === Use Case Flow ===

        // Step 1: Retrieve Scheduled Consultation (Using Query Service)
        System.out.println("Step 1: Doctor opens the consultation screen...");
        Consultation retrievedConsultation = consultationQueryService.getScheduledConsultation(consultation.getId());
        System.out.println("  ✓ Consultation loaded. Status: " + retrievedConsultation.getStatus());

        // Step 2: Get Patient History (Using History Service)
        System.out.println("\nStep 2: Doctor checks patient history...");
        PatientHistoryDTO history = patientHistoryService.getPatientHistory(patient.getId());
        System.out.println("  ✓ Patient History loaded for: " + history.getPatient().getChildName());
        System.out.println("  ✓ Previous Consultations: " + history.getTotalConsultations());
        System.out.println("  ✓ Has History: " + history.hasHistory());

        // Step 3: Register Medical Record (Using Command Service)
        System.out.println("\nStep 3: Doctor fills form and clicks 'Save'...");
        RegisterMedicalRecordCommand command = RegisterMedicalRecordCommand.builder()
                .consultationId(consultation.getId())
                .weight(18.5)
                .height(110.0)
                .temperature(Double.valueOf(37.8))
                .bloodPressure("100/60")
                .heartRate(Integer.valueOf(95))
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

        MedicalRecord medicalRecord = registerRecordService.registerMedicalRecord(command);

        System.out.println("\n=== Medical Record Created Successfully ===");
        System.out.println("  Record ID: " + medicalRecord.getId());
        // Using Value Objects accessors properly
        System.out.println("  Weight: " + medicalRecord.getVitalSigns().getWeightKg() + " kg");
        System.out.println("  Height: " + medicalRecord.getVitalSigns().getHeightCm() + " cm");
        System.out.println("  BMI: " + String.format("%.2f", Double.valueOf(medicalRecord.getVitalSigns().calculateBMI())) + " (" + medicalRecord.getVitalSigns().getBMIClassification() + ")");
        System.out.println("  Symptoms: " + medicalRecord.getClinicalNotes().getSymptomDescription());
        System.out.println("  Diagnosis: " + (medicalRecord.getCarePlan().getDiagnosis() != null ? medicalRecord.getCarePlan().getDiagnosis() : "N/A"));
        System.out.println("  Prescriptions: " + medicalRecord.getPrescriptionCount());
        System.out.println("  Exams Requested: " + medicalRecord.getRequestedExamCount());

        // Verify consultation was completed
        Consultation completedConsultation = consultationRepository.findById(consultation.getId()).orElseThrow();
        System.out.println("\n  Consultation Final Status: " + completedConsultation.getStatus());

        System.out.println("\n==============================================");
        System.out.println("  Demo completed successfully!");
        System.out.println("==============================================");
    }
}
