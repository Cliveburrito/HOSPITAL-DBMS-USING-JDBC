package Entities;

import java.time.LocalDateTime;

public class MedicalRecord {

    private int recordId;
    private int patientId;
    private Integer doctorId; // nullable in DB
    private LocalDateTime recordDateTime;
    private String diagnosis;
    private String treatment;
    private String notes;

    public MedicalRecord() {
    }

    public MedicalRecord(int recordId,
                         int patientId,
                         Integer doctorId,
                         LocalDateTime recordDateTime,
                         String diagnosis,
                         String treatment,
                         String notes) {
        this.recordId = recordId;
        this.patientId = patientId;
        this.doctorId = doctorId;
        this.recordDateTime = recordDateTime;
        this.diagnosis = diagnosis;
        this.treatment = treatment;
        this.notes = notes;
    }

    public int getRecordId() {
        return recordId;
    }

    public void setRecordId(int recordId) {
        this.recordId = recordId;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public Integer getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    public LocalDateTime getRecordDateTime() {
        return recordDateTime;
    }

    public void setRecordDateTime(LocalDateTime recordDateTime) {
        if(recordDateTime == null) {
            throw new IllegalArgumentException("Please enter a date!");
        }
        if(recordDateTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Date cannot be in the future");
        }
        this.recordDateTime = recordDateTime;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        if(diagnosis == null || diagnosis.isBlank()) {
            throw new IllegalArgumentException("Please enter a diagnosis");
        }
        this.diagnosis = diagnosis;
    }

    public String getTreatment() {
        return treatment;
    }

    public void setTreatment(String treatment) {
        if(treatment == null || treatment.isBlank()) {
            throw new IllegalArgumentException("Please enter a treatment");
        }
        this.treatment = treatment;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    @Override
    public String toString() {
        String docStr = (doctorId == null) ? "N/A" : doctorId.toString();
        return String.format("%-4d %-4d %-4s %-20s %-20s %-20s %-30s",
                recordId, patientId, docStr, recordDateTime,
                diagnosis, treatment, notes);
    }
}
