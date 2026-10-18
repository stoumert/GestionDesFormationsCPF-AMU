package amu.ter.cpf.model.referential;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {
    FR("Francais"),
    EN("Anglais"),
    AR("Arabe"),
    DE("Allemand"),
    BN("Bengali"),
    ZH("Chinois"),
    DA("Danois"),
    ES("Espagnol"),
    EL("Grec moderne"),
    HE("Hébreu"),
    HI("Hindi"),
    HU("Hongrois"),
    ID("Indonésien"),
    RU("Russe"),
    IT("Italien"),
    JA("Japonais"),
    JV("Javanais"),
    XX("Langue des signes"),
    LA("Latin"),
    MS ("Malais"),
    NL("Néerlandais"),
    NO("Norvégien"),
    PL("Polonais"),
    PA("Punjabi"),
    RO("Roumain"),
    SV("Suédois"),
    CS("Tchèque"),
    TR("Turc"),
    UK("Ukrainien"),
    PT("Portugais");

    private final String language;
}
