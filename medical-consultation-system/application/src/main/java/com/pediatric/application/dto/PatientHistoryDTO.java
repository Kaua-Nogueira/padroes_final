package com.pediatric.application.dto;

import com.pediatric.domain.entity.MedicalRecord;
import com.pediatric.domain.entity.Patient;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DTO representing a patient's medical history summary.
 * Used for viewing previous records during a consultation.
 */
public final class PatientHistoryDTO {

    private final Patient patient;
    private final List<RecordSummary> previousRecords;
    private final int totalConsultations;

    public PatientHistoryDTO(Patient patient, List<MedicalRecord> records) {
        this.patient = patient;
        this.previousRecords = records.stream()
                .map(RecordSummary::fromMedicalRecord)
                .toList();
        this.totalConsultations = records.size();
    }

    public Patient getPatient() {
        return patient;
    }

    public List<RecordSummary> getPreviousRecords() {
        return Collections.unmodifiableList(previousRecords);
    }

    public int getTotalConsultations() {
        return totalConsultations;
    }

    public boolean hasHistory() {
        return !previousRecords.isEmpty();
    }

    /**
     * Gets the most recent weight from history.
     * @return the most recent weight, or 0 if no history
     */
    public double getLastKnownWeight() {
        return previousRecords.isEmpty() ? 0 : previousRecords.get(0).weight();
    }

    /**
     * Gets the most recent height from history.
     * @return the most recent height, or 0 if no history
     */
    public double getLastKnownHeight() {
        return previousRecords.isEmpty() ? 0 : previousRecords.get(0).height();
    }

    /**
     * Calculates weight gain since last visit.
     * @param currentWeight the current weight
     * @return weight difference (positive = gain, negative = loss)
     */
    public double calculateWeightChange(double currentWeight) {
        double lastWeight = getLastKnownWeight();
        return lastWeight > 0 ? currentWeight - lastWeight : 0;
    }

    /**
     * Calculates height change since last visit.
     * @param currentHeight the current height
     * @return height difference (should normally be positive for growing children)
     */
    public double calculateHeightChange(double currentHeight) {
        double lastHeight = getLastKnownHeight();
        return lastHeight > 0 ? currentHeight - lastHeight : 0;
    }

    /**
     * Summary record of previous consultations.
     */
    public record RecordSummary(
            LocalDateTime date,
            double weight,
            double height,
            double bmi,
            String symptomSummary,
            int prescriptionCount,
            int examCount
    ) {
        public static RecordSummary fromMedicalRecord(MedicalRecord record) {
            return new RecordSummary(
                    record.getCreatedAt(),
                    record.getWeight(),
                    record.getHeight(),
                    record.calculateBMI(),
                    truncate(record.getSymptomDescription(), 100),
                    record.getPrescriptionCount(),
                    record.getRequestedExamCount()
            );
        }

        private static String truncate(String text, int maxLength) {
            if (text == null) return "";
            return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
        }
    }
}
