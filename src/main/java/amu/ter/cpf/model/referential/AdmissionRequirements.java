package amu.ter.cpf.model.referential;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AdmissionRequirements {
    ADMISSION_REQUIREMENTS_1(92002,"Admission sur dossier"),
    ADMISSION_REQUIREMENTS_2(92003,"Admission sur concours"),
    ADMISSION_REQUIREMENTS_3(92005,"Admission après entretien"),
    ADMISSION_REQUIREMENTS_4(92007,"Admission après test"),
    ADMISSION_REQUIREMENTS_5(92009,"Admission après visite médicale"),
    ADMISSION_REQUIREMENTS_6(99998, "Plusieurs modalités possibles"),
    ADMISSION_REQUIREMENTS_7(99999,"Admission sans disposition particulière");

    private final int code;
    private final String description;

    public static boolean isValidCode(int code) {
        for (AdmissionRequirements requirement : values()) {
            if (requirement.code == code) {
                return true;
            }
        }
        return false;
    }
}
