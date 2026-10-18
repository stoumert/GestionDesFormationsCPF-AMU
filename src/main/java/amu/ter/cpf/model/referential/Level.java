package amu.ter.cpf.model.referential;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Level {
    LEVEL_2(2, "NIVEAU 2", "Sans diplôme ou diplôme national du Brevet"),
    LEVEL_3(3, "NIVEAU 3", "CAP, BEP..."),
    LEVEL_4(4, "NIVEAU 4", "BAC : BP, BT, bac pro ou techno"),
    LEVEL_5(5,"NIVEAU 5", "BAC+2 : DEUG, BT, DUT..."),
    LEVEL_6(6, "NIVEAU 6", "BAC+3 ou 4 : licence, master 1, maîtrise"),
    LEVEL_7(7, "NIVEAU 7", "BAC+5 : grade master, DEA, DESS, ingénieur..."),
    LEVEL_8(8, "NIVEAU 8", "BAC+8 : doctorat..."),
    WITHOUT_LEVEL(9,"", "Sans niveau spécifique");

    private final int code;
    private final String level;
    private final String description;

}
