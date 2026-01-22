package com.pediatric.application.usecase;

import com.pediatric.application.dto.RegisterMedicalRecordCommand;
import com.pediatric.application.port.input.RegisterMedicalRecordCommandPortIn;
import com.pediatric.application.port.output.*;
import com.pediatric.domain.exception.BusinessRuleException;
import com.pediatric.domain.exception.EntityNotFoundException;
import com.pediatric.domain.model.Consultation;
import com.pediatric.domain.model.MedicalRecord;
import com.pediatric.domain.model.Prescription;
import com.pediatric.domain.valueobject.CarePlan;
import com.pediatric.domain.valueobject.ClinicalNotes;
import com.pediatric.domain.valueobject.VitalSigns;

import java.util.UUID;

/**
 * Service focused on Medical Record Registration (Command).
 * Implements the RegisterMedicalRecordCommandInputPort (ISP).
 */
public class RegisterMedicalRecordUseCaseImpl implements RegisterMedicalRecordCommandPortIn {

    private final ConsultationPortOut consultationPortOut;
    private final PatientPortOut patientPortOut;
    private final MedicalRecordPortOut medicalRecordPortOut;
    private final MedicationPortOut medicationPortOut;
    private final ExamPortOut examPortOut;

    public RegisterMedicalRecordUseCaseImpl(
            ConsultationPortOut consultationPortOut,
            PatientPortOut patientPortOut,
            MedicalRecordPortOut medicalRecordPortOut,
            MedicationPortOut medicationPortOut,
            ExamPortOut examPortOut) {
        this.consultationPortOut = consultationPortOut;
        this.patientPortOut = patientPortOut;
        this.medicalRecordPortOut = medicalRecordPortOut;
        this.medicationPortOut = medicationPortOut;
        this.examPortOut = examPortOut;
    }

    @Override
    public MedicalRecord registerMedicalRecord(RegisterMedicalRecordCommand command) {
        // Step 1: Validate and get the consultation
        Consultation consultation = validateConsultation(command.getConsultationId());

        // Step 2: Validate patient exists
        UUID patientId = consultation.getPatientId();
        if (!patientPortOut.existsById(patientId)) {
            throw new EntityNotFoundException("Patient", patientId);
        }

        // Step 3 & 4: Validate medications and exams
        validateMedicationsInternal(command);
        validateExamsInternal(command);

        // Step 5: Start the consultation if not already in progress
        if (consultation.getStatus() == Consultation.ConsultationStatus.SCHEDULED) {
            consultation.startConsultation();
            consultationPortOut.save(consultation);
        }

        // Step 6: Map command to Entity using the Private Mapper
        MedicalRecord medicalRecord = mapToEntity(command, consultation);

        // Step 7: Create prescriptions and add to medical record
        // Note: Prescriptions are already added in mapToEntity via builder and manual check if needed, 
        // but let's see how our MedicalRecord works. The new builder creates the list.
        // We will do it nicely in the mapToEntity method.

        // Step 8: Save the medical record
        MedicalRecord savedRecord = medicalRecordPortOut.save(medicalRecord);

        // Step 9: Complete the consultation
        consultation.completeConsultation();
        consultationPortOut.save(consultation);

        return savedRecord;
    }

    private Consultation validateConsultation(UUID consultationId) {
        Consultation consultation = consultationPortOut.findById(consultationId)
                .orElseThrow(() -> new EntityNotFoundException("Consultation", consultationId));

        if (consultation.isCompleted()) {
            throw new BusinessRuleException("CONSULTATION_ALREADY_COMPLETED", "Cannot register record for completed consultation");
        }
        if (consultation.isCancelled()) {
            throw new BusinessRuleException("CONSULTATION_CANCELLED", "Cannot register record for cancelled consultation");
        }
        if (medicalRecordPortOut.existsByConsultationId(consultationId)) {
            throw new BusinessRuleException("RECORD_ALREADY_EXISTS", "Medical record already exists for this consultation");
        }
        return consultation;
    }

    private MedicalRecord mapToEntity(RegisterMedicalRecordCommand command, Consultation consultation) {
        // Create Value Objects first (They perform their own validations)
        VitalSigns vitalSigns = VitalSigns.builder()
                .weightKg(command.getWeight())
                .heightCm(command.getHeight())
                .temperatureC(command.getTemperature())
                .bloodPressure(command.getBloodPressure())
                .heartRateBpm(command.getHeartRate())
                .build();

        ClinicalNotes notes = ClinicalNotes.builder()
                .symptomDescription(command.getSymptomDescription())
                .clinicalObservation(command.getClinicalObservation())
                .build();

        CarePlan carePlan = CarePlan.builder()
                .diagnosis(command.getDiagnosis())
                .treatmentPlan(command.getTreatmentPlan())
                .build();

        // Use the new clean builder from MedicalRecord
        MedicalRecord.Builder builder = MedicalRecord.builder()
                .consultationId(consultation.getId())
                .patientId(consultation.getPatientId())
                .doctorId(consultation.getDoctorId())
                .vitalSigns(vitalSigns)
                .clinicalNotes(notes)
                .carePlan(carePlan)
                .requestedExamIds(command.getRequestedExamIds());

        // We build the record partially to get an ID if we wanted to use it for prescriptions
        // But the builder can take prescriptions list. However, Prescription entity needs medicalRecordId.
        // This is a circular dependency during creation if we demand ID for prescription creation.
        // In the previous code, 'medicalRecord' was built, then prescriptions added.
        
        MedicalRecord record = builder.build();

        // Add Prescriptions
        for (RegisterMedicalRecordCommand.PrescriptionData pData : command.getPrescriptions()) {
            Prescription prescription = Prescription.builder()
                    .medicalRecordId(record.getId()) // UUID is generated on build()
                    .medicationId(pData.getMedicationId())
                    .dosage(pData.getDosage())
                    .administrationRoute(pData.getAdministrationRoute())
                    .usageDuration(pData.getUsageDuration())
                    .frequency(pData.getFrequency())
                    .specialInstructions(pData.getSpecialInstructions())
                    .build();
            
            record.addPrescription(prescription);
        }

        return record;
    }

    private void validateMedicationsInternal(RegisterMedicalRecordCommand command) {
        for (RegisterMedicalRecordCommand.PrescriptionData prescription : command.getPrescriptions()) {
            UUID medicationId = prescription.getMedicationId();
            if (!medicationPortOut.existsById(medicationId)) {
                throw new EntityNotFoundException("Medication", medicationId);
            }
        }
    }

    private void validateExamsInternal(RegisterMedicalRecordCommand command) {
        for (UUID examId : command.getRequestedExamIds()) {
            if (!examPortOut.existsById(examId)) {
                throw new EntityNotFoundException("Exam", examId);
            }
        }
    }
}
