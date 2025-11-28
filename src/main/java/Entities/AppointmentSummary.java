package Entities;

public record AppointmentSummary(
        Appointment appointment,
        Doctor doctor,
        Patient patient,
        Department department
) {

    @SuppressWarnings("NullableProblems")
    @Override
    public String toString() {
        return """
           Appointment Summary:
           ---------------------
           Appointment: %d at %s
           Department: (ID: %d) %s
           Doctor: Dr. %s %s (ID: %d)
           Patient: %s
           Reason: %s
           """.formatted(
                appointment.getAppointmentID(),
                appointment.getAppointmentDate(),

                department.getDepartmentId(),
                department.getName(),

                doctor.getFirstName(),
                doctor.getLastName(),
                doctor.getDoctorId(),

                patient.toString(),

                appointment.getReason()
                + "\n---------------------"
        );
    }
}
