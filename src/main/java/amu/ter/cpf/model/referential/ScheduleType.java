package amu.ter.cpf.model.referential;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ScheduleType {
    SCHEDULE_TYPE_1(1,"A horaires fixes"),
    SCHEDULE_TYPE_2(2, "Sans horaires fixes"),
    SCHEDULE_TYPE_3(3,"Mixte");
    private final int code;
    private final String description;
}
