package amu.ter.cpf.model.referential;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TrainingPace {
    TRAINING_PACE_1(1,"En journée"),
    TRAINING_PACE_2(2,"En soirée"),
    TRAINING_PACE_3(3,"En semaine"),
    TRAINING_PACE_4(4,"Le week-end"),
    TRAINING_PACE_5(5,"Temps plein"),
    TRAINING_PACE_6(6,"Temps partiel"),
    TRAINING_PACE_7(7,"Plusieurs rythmes");

    private final int code;
    private final String libelle;

    public static boolean isValidCode(int code) {
        for (TrainingPace t : TrainingPace.values()) {
            if (t.code == code) {
                return true;
            }
        }
        return false;
    }
}
