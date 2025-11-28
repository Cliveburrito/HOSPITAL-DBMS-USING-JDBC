package Entities;

public record CurrentHospitalizationSummary(
        Patient patient,
        Department department,
        Hospitalization hospitalization
) {

    @Override
    public String toString() {
        return """
                ┌───────────────────────────────────────────┐
                │        CURRENT HOSPITALIZATION            │
                └───────────────────────────────────────────┘
                Patient:
                  - ID: %d
                  - Name: %s %s
                  - AMKA: %s
                  - Phone: %s
                  - Gender: %s
                  - Date of Birth: %s

                Department:
                  - ID: %d
                  - Name: %s
                  - Bed Number: %s

                Hospitalization:
                  - Hospitalization ID: %d
                  - Admit Date/Time: %s
                  - Reason: %s

                """.formatted(
                patient.getPatientId(),
                patient.getFirstName(),
                patient.getLastName(),
                patient.getAmka(),
                patient.getPhone(),
                patient.getGender(),
                patient.getDateOfBirth(),

                department.getDepartmentId(),
                department.getName(),
                hospitalization.getBedNumber(),

                hospitalization.getHospitalizationId(),
                hospitalization.getAdmitDateTime(),
                hospitalization.getReason()
        );
    }
}
