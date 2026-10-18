package amu.ter.cpf.model.entity;

import amu.ter.cpf.model.referential.AdmissionRequirements;
import amu.ter.cpf.model.referential.TrainingPace;
import amu.ter.cpf.validation.*;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static java.math.RoundingMode.HALF_UP;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "action")
public class Action {
    /**
     * identification
     */
    @Id
    @Column(name = "code_action", length = 30)
    @NotBlank(
            message = "Le code d'action est obligatoire.",
            groups = {Step1Validation.class, PublicationValidation.class}
    )
    @Size(
            min = 1, max = 30,
            message = "Le code d'action doit être composé de 1 à 30 caractères.",
            groups = {Step1Validation.class, PublicationValidation.class}
    )
    private String codeAction;

    @Column(name = "modalites_enseignement")
    @NotNull(
            message = "Les modalités d'enseignement sont obligatoires.",
            groups = {Step1Validation.class, PublicationValidation.class}
    )
    @Min(value = 0, message = "Le champ modalités d'enseignement doit valoir 0, 1 ou 2.",
            groups = {Step1Validation.class, PublicationValidation.class})
    @Max(value = 2, message = "Le champ modalités d'enseignement doit valoir 0, 1 ou 2.",
            groups = {Step1Validation.class, PublicationValidation.class})
    private Integer modalitesEnseignement;

    @Column(name = "modalites_entrees_sorties")
    @NotNull(
            message = "Les modalités d'entrées/sorties sont obligatoires.",
            groups = {Step1Validation.class, PublicationValidation.class}
    )
    @Min(value = 0, message = "Le champ 'modalités d'entrées/sorties' doit valoir 0 ou 1.",
            groups = {Step1Validation.class, PublicationValidation.class})
    @Max(value = 1, message = "Le champ 'modalités d'entrées/sorties' doit valoir 0 ou 1.",
            groups = {Step1Validation.class, PublicationValidation.class})
    private Integer modalitesEntreesSorties;

    @Column(name = "niveau_entree_obligatoire")
    @NotNull(
            message = "Le niveau d'entrée obligatoire est requis.",
            groups = {Step1Validation.class, PublicationValidation.class}
    )
    @Min(value = 0, message = "Le niveau d'entrée doit valoir 0 ou 1.",
            groups = {Step1Validation.class, PublicationValidation.class})
    @Max(value = 1, message = "Le niveau d'entrée doit valoir 0 ou 1.",
            groups = {Step1Validation.class, PublicationValidation.class})
    private Integer niveauEntreeObligatoire;

    @Column(name = "langue_formation")
    @NotBlank(
            message = "La langue de formation est obligatoire.",
            groups = {Step1Validation.class, PublicationValidation.class}
    )
    @Pattern(
            regexp = "^(FR|EN|AR|DE|BN|ZH|DA|ES|EL|HE|HI|HU|ID|RU|IT|JA|JV|XX|LA|MS|NL|NO|PL|PA|RO|SV|CS|TR|UK|PT)$",
            message = "La langue utilisée n'existe pas dans le référentiel.",
            groups = {Step1Validation.class, PublicationValidation.class}
    )
    private String langueFormation;

    @Column(name = "url_action")
    @Size(max = 400, message = "L'URL de l'action ne doit pas dépasser 400 caractères.",
            groups = {Step1Validation.class, PublicationValidation.class})
    @Pattern(
            regexp = "^(https?://.+)?$",
            message = "L'URL de l'action doit commencer par http:// ou https://.",
            groups = {Step1Validation.class, PublicationValidation.class}
    )
    private String urlAction;

    /**
     * Contenu pédagogique
     */

    @Column(columnDefinition = "TEXT", name = "conditions_specifiques")
    @Size(max = 3000, message = "Les conditions spécifiques ne doivent pas dépasser 3000 caractères.",
            groups = {Step2Validation.class, PublicationValidation.class})
    private String conditionSpecifiques;

    @Column(name = "existence_prerequis")
    @NotNull(
            message = "L'indicateur existence-prérequis est obligatoire.",
            groups = {Step2Validation.class, PublicationValidation.class}
    )
    @Min(value = 0, message = "Le top prérequis doit valoir 0 ou 1.",
            groups = {Step2Validation.class, PublicationValidation.class})
    @Max(value = 1, message = "Le top prérequis doit valoir 0 ou 1.",
            groups = {Step2Validation.class, PublicationValidation.class})
    private Integer existencePrerequis;

    @Column(name = "info_admission", columnDefinition = "TEXT")
    @Size(max = 250, message = "L'information sur l'admission ne doit pas dépasser 250 caractères.",
            groups = {Step2Validation.class, PublicationValidation.class})
    private String infoAdmission;

    @Column(name = "modalites_pedagogiques", columnDefinition = "TEXT")
    @Size(max = 200, message = "Les modalités pédagogiques ne doivent pas dépasser 200 caractères.",
            groups = {Step2Validation.class, PublicationValidation.class})
    private String modalitesPedagogiques;

    @Column(name = "modalites_handicap", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Les modalités handicap ne doivent pas dépasser 3000 caractères.",
            groups = {Step2Validation.class, PublicationValidation.class})
    private String modalitesHandicap;

    @Column(name = "modalites_recrutement", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Les modalités de recrutement ne doivent pas dépasser 3000 caractères.",
            groups = {Step2Validation.class, PublicationValidation.class})
    private String modalitesRecrutement;

    /**
     * horaires et durée
     */

    @Column(name = "code_type_horaires")
    @Min(value = 1, message = "Le code type d'horaires doit être compris entre 1 et 3.",
            groups = {Step3Validation.class, PublicationValidation.class})
    @Max(value = 3, message = "Le code type d'horaires doit être compris entre 1 et 3.",
            groups = {Step3Validation.class, PublicationValidation.class})
    private Integer codeTypeHoraires;

    @Column(name = "nombre_heures_centre")
    @Min(value = 0, message = "Le nombre d'heures en centre doit être compris entre 0 et 99999.",
            groups = {Step3Validation.class, PublicationValidation.class})
    @Max(value = 99999, message = "Le nombre d'heures en centre doit être compris entre 0 et 99999.",
            groups = {Step3Validation.class, PublicationValidation.class})
    private Integer nombreHeuresCentre;

    @Column(name = "nombre_heures_entreprise")
    @Min(value = 0, message = "Le nombre d'heures en entreprise doit être compris entre 0 et 99999.",
            groups = {Step3Validation.class, PublicationValidation.class})
    @Max(value = 99999, message = "Le nombre d'heures en entreprise doit être compris entre 0 et 99999.",
            groups = {Step3Validation.class, PublicationValidation.class})
    private Integer nombreHeuresEntreprise;

    @Column(name = "duree_apprentissage")
    @Min(value = 0, message = "La durée d'apprentissage doit être comprise entre 0 et 99999.",
            groups = {Step3Validation.class, PublicationValidation.class})
    @Max(value = 99999, message = "La durée d'apprentissage doit être comprise entre 0 et 99999.",
            groups = {Step3Validation.class, PublicationValidation.class})
    private Integer dureeApprentissage;

    @Column(name = "code_gfe")
    @Min(value = 1, message = "Le code GFE doit être compris entre 1 et 24.",
            groups = {Step3Validation.class, PublicationValidation.class})
    @Max(value = 24, message = "Le code GFE doit être compris entre 1 et 24.",
            groups = {Step3Validation.class, PublicationValidation.class})
    private Integer codeGfe;

    @Column(name = "code_perimetre_recrutement")
    @Min(value = 1, message = "Le code périmètre de recrutement doit être compris entre 1 et 7.",
            groups = {Step3Validation.class, PublicationValidation.class})
    @Max(value = 7, message = "Le code périmètre de recrutement doit être compris entre 1 et 7.",
            groups = {Step3Validation.class, PublicationValidation.class})
    private Integer codePerimetreRecrutement;

    @Column(name = "infos_perimetre_recrutement")
    @Size(max = 50, message = "Les infos périmètre de recrutement ne doivent pas dépasser 50 caractères.",
            groups = {Step3Validation.class, PublicationValidation.class})
    private String infosPerimetreRecrutement;

    /**
     * frais
     */

    @Column(name = "taux_tva", precision = 5, scale = 1)
    @NotNull(
            message = "Le taux de TVA est obligatoire.",
            groups = {Step4Validation.class, PublicationValidation.class}
    )
    private BigDecimal tauxTva;

    @Column(name = "frais_ht", precision = 10, scale = 2)
    @NotNull(
            message = "Les frais HT sont obligatoires.",
            groups = {Step4Validation.class, PublicationValidation.class}
    )
    @DecimalMin(value = "0.00", message = "Les frais HT doivent être compris entre 0 et 9999999999,99.",
            groups = {Step4Validation.class, PublicationValidation.class})
    @DecimalMax(value = "9999999999.99", message = "Les frais HT doivent être compris entre 0 et 9999999999,99.",
            groups = {Step4Validation.class, PublicationValidation.class})
    private BigDecimal fraisHt;

    @Column(name = "frais_ttc", precision = 10, scale = 2)
    @NotNull(
            message = "Les frais TTC sont obligatoires.",
            groups = {Step4Validation.class, PublicationValidation.class}
    )
    @DecimalMin(value = "0.00", message = "Les frais TTC doivent être compris entre 0 et 9999999999,99.",
            groups = {Step4Validation.class, PublicationValidation.class})
    @DecimalMax(value = "9999999999.99", message = "Les frais TTC doivent être compris entre 0 et 9999999999,99.",
            groups = {Step4Validation.class, PublicationValidation.class})
    private BigDecimal fraisTtc;

    @Column(name = "code_modele_economique")
    @Min(value = 1, message = "Le code modèle économique doit être 1 ou 2.",
            groups = {Step4Validation.class, PublicationValidation.class})
    @Max(value = 2, message = "Le code modèle économique doit être 1 ou 2.",
            groups = {Step4Validation.class, PublicationValidation.class})
    private Integer codeModeleEconomique;

    @Column(name = "frais_certif_inclus_frais_anpec")
    @NotNull(
            message = "L'indicateur frais-certif-inclus est obligatoire.",
            groups = {Step4Validation.class, PublicationValidation.class}
    )
    @Min(value = 0, message = "Le champ 'frais-certif-inclus' doit valoir 0 ou 1.",
            groups = {Step4Validation.class, PublicationValidation.class})
    @Max(value = 1, message = "Le champ 'frais-certif-inclus' doit valoir 0 ou 1.",
            groups = {Step4Validation.class, PublicationValidation.class})
    private Integer fraisCertifInclusFraisAnpec;

    @Column(name = "frais_anpec", precision = 10, scale = 2)
    @DecimalMin(value = "0.00", message = "Les frais ANPEC doivent être compris entre 0 et 9999999999,99.",
            groups = {Step4Validation.class, PublicationValidation.class})
    @DecimalMax(value = "9999999999.99", message = "Les frais ANPEC doivent être compris entre 0 et 9999999999,99.",
            groups = {Step4Validation.class, PublicationValidation.class})
    private BigDecimal fraisAnpec;

    @Column(name = "detail_frais_anpec", columnDefinition = "TEXT")
    @Size(max = 3000, message = "Le détail des frais ANPEC ne doit pas dépasser 3000 caractères.",
            groups = {Step4Validation.class, PublicationValidation.class})
    private String detailFraisAnpec;

    /**
     * logistique
     */

    @Column(name = "acces_handicapes")
    @NotBlank(
            message = "Le champ 'Accès handicapés' est obligatoire.",
            groups = {Step5Validation.class, PublicationValidation.class}
    )
    @Size(min = 1, max = 250, message = "Le champ 'Accès handicapés' doit être composé de 1 à 250 caractères.",
            groups = {Step5Validation.class, PublicationValidation.class})
    private String accesHandicapes;

    @Size(max = 250, message = "La restauration ne doit pas dépasser 250 caractères.",
            groups = {Step5Validation.class, PublicationValidation.class})
    private String restauration;

    @Size(max = 250, message = "L'hébergement ne doit pas dépasser 250 caractères.",
            groups = {Step5Validation.class, PublicationValidation.class})
    private String hebergement;

    @Size(max = 250, message = "Le transport ne doit pas dépasser 250 caractères.",
            groups = {Step5Validation.class, PublicationValidation.class})
    private String transport;

    @Column(name = "autres_services", columnDefinition = "TEXT")
    @Size(max = 255, message = "Les autres services ne doivent pas dépasser 255 caractères.",
            groups = {Step5Validation.class, PublicationValidation.class})
    private String autresServices;

    /**
     * champs téchnique
     */

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDate dateCreation;

    @Column(name = "date_maj", nullable = false)
    private LocalDate dateMaj;

    @OneToMany(mappedBy = "action",
            //cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            fetch = FetchType.LAZY)
    private List<Session> sessions;


    private List<Integer> codeModaliteAdmissions;


    private List<Integer> codeRythmeFormations;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_formation", nullable = false)
    @ToString.Exclude
    private Formation formation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_action_parente")
    private Action actionParente;

    @AssertTrue(
            message = "L'URL de l'action doit être composée de 3 à 400 caractères.",
            groups = {Step1Validation.class, PublicationValidation.class}
    )
    @Transient
    public boolean isUrlActionValide() {
        if (urlAction == null || urlAction.isBlank()) return true;
        return urlAction.length() >= 3 && urlAction.length() <= 400;
    }

    @AssertTrue(
            message = "Le taux de TVA doit exister dans le référentiel : 0.0, 5.5 ou 20.0.",
            groups = {Step4Validation.class, PublicationValidation.class}
    )
    @Transient
    public boolean isTauxTvaValide() {
        if (tauxTva == null) return true;
        return tauxTva.compareTo(new BigDecimal("0.0")) == 0
                || tauxTva.compareTo(new BigDecimal("5.5")) == 0
                || tauxTva.compareTo(new BigDecimal("20.0")) == 0;
    }

    @AssertTrue(
            message = "Les frais HT et TTC doivent être renseignés lorsque le taux TVA l'est.",
            groups = {Step4Validation.class, PublicationValidation.class}
    )
    @Transient
    public boolean isFraisComplets() {
        if (tauxTva == null) return true;
        return fraisHt != null && fraisTtc != null;
    }

    @AssertTrue(
            message = "L'écart entre les frais TTC et les frais TTC calculés doit être inférieur à 1 centime.",
            groups = {Step4Validation.class, PublicationValidation.class}
    )
    @Transient
    public boolean isFraisTtcCoherent() {
        if (fraisHt == null || fraisTtc == null || tauxTva == null) return true;
        BigDecimal coeff = BigDecimal.ONE.add(tauxTva.divide(new BigDecimal("100")));
        BigDecimal fraisTtcCalcule = fraisHt.multiply(coeff).setScale(2, HALF_UP);
        BigDecimal ecart = fraisTtc.subtract(fraisTtcCalcule).abs();
        return ecart.compareTo(new BigDecimal("0.01")) < 0;
    }

    @AssertTrue(message = "Le code de l'action doit obligatoirement commencer par le code de la formation",
            groups = {Step1Validation.class, PublicationValidation.class})
    @Transient
    public boolean isCodeActionStartWithCodeFormation() {
        if (codeAction == null || formation == null
                || formation.getCodeFormation() == null) {
            return true;
        }
        String codeFormationPropre = formation.getCodeFormation().replace("_*", "");
        String codeActionPropre    = codeAction.replace("_*", "");
        return codeActionPropre.startsWith(codeFormationPropre);
    }
    @AssertTrue(
            message = "Au moins une modalité d'admission valide doit être sélectionnée.",
            groups = {Step1Validation.class, PublicationValidation.class}
    )
    public boolean isValideCodesModaliteAdmissions() {
        if (codeModaliteAdmissions == null || codeModaliteAdmissions.isEmpty()) return false;
        for(Integer code : codeModaliteAdmissions) {
            if (!AdmissionRequirements.isValidCode(code)) return false;
        }
        return true;
    }
    @AssertTrue(
            message = "Au moins un rythme de formation valide doit être sélectionné.",
            groups = {Step1Validation.class, PublicationValidation.class}

    )
    public boolean isValideCodesRythmeFormations() {
        if (codeRythmeFormations == null || codeRythmeFormations.isEmpty()) return false;
        for(Integer code : codeRythmeFormations) {
            if (!TrainingPace.isValidCode(code)) return false;
        }
        return true;
    }
}