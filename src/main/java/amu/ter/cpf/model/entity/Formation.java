
package amu.ter.cpf.model.entity;
import amu.ter.cpf.validation.BrouillonValidation;
import amu.ter.cpf.validation.PublicationValidation;
import amu.ter.cpf.validation.Step1Validation;
import amu.ter.cpf.validation.TextAreaNotBlank;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "formation")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Formation {


    @Id
    @NotBlank(message = "Ce champ est obligatoire", groups = {BrouillonValidation.class,PublicationValidation.class})
    @Column(name = "code_formation", length = 36, updatable = false, nullable = false)
    @Size(max = 36, message = "La longueur doit être comprise entre 1 et 36 caractères", groups = {BrouillonValidation.class,PublicationValidation.class})
    @Pattern(
            regexp = "^[A-Z0-9]{1,30}(_[*])?$",
            message = "Le code formation doit contenir uniquement des lettres majuscules et des chiffres, avec éventuellement le suffixe _DRAFT",
            groups = {BrouillonValidation.class,PublicationValidation.class}
    )
    private String codeFormation;



    @NotBlank(message = "Le type de certification est obligatoire",groups = {BrouillonValidation.class,PublicationValidation.class})
    @Pattern(
            regexp = "^(RNCP|RS|CPF)$",
            message = "Le type de certification doit être RNCP, RS ou CPF",
            groups = {BrouillonValidation.class,PublicationValidation.class}
    )
    @Column(name = "type_certification", length = 4, updatable = false, nullable = false)

    private String typeCertification;

    @NotBlank(message = "Le code certification est obligatoire", groups = {BrouillonValidation.class,PublicationValidation.class})
    @Pattern(
            regexp = "^(RNCP[A-Z0-9]{2,7}|RS[A-Z0-9]{1,8}|CPF[A-Z0-9]{2,7})$",
            message = "Le code doit être au format RNCP##### (5-10 car), RS### (3-10 car) ou CPF### (4-10 car)",
            groups = {BrouillonValidation.class,PublicationValidation.class}
    )
    @Column(name = "code_certification", updatable = false, nullable = false)
    private String codeCertification;

    @Column(name = "intitule")
    @NotBlank(message="ce champ est obligatoire", groups = PublicationValidation.class)
    @Size(
            max=255,
            message="La longueur de la rubrique doit être comprise entre 1 et 255 caractères",
            groups = {Step1Validation.class,PublicationValidation.class})
    private String intitule;

    @Column(columnDefinition = "TEXT", length = 3000)
    //@NotBlank(message="ce champs est obligatoire", groups = PublicationValidation.class)
    @TextAreaNotBlank(groups = PublicationValidation.class)
    @Size(
            max=3000,
            message="La longueur de la rubrique doit être comprise entre 1 et 3000 caractères",
            groups = {Step1Validation.class,PublicationValidation.class})
    private String objectifFormation;



    @Column(columnDefinition = "TEXT", length = 3000)
    //@NotBlank(message="ce champs est obligatoire", groups = PublicationValidation.class)
    @TextAreaNotBlank(groups = PublicationValidation.class)
    @Size(
            max=3000,
            message="La longueur de la rubrique doit être comprise entre 1 et 3000 caractères",
            groups = {Step1Validation.class,PublicationValidation.class})
    private String resultatsAttendus;

    @Column(columnDefinition = "TEXT", length = 3000)
    //@NotBlank(message="ce champs est obligatoire", groups = PublicationValidation.class)
    @TextAreaNotBlank(groups = PublicationValidation.class)
    @Size(
            max=3000,
            message="La longueur de la rubrique doit être comprise entre 1 et 3000 caractères",
            groups = {Step1Validation.class,PublicationValidation.class})
    private String contenuFormation;

    @NotNull(groups = PublicationValidation.class)
    @Column(name = "parcours_de_formation", length = 10)
    private Integer parcoursDeFormation;

    @Column(name = "objectif_general", length = 10)
    private Integer objectifGeneral;


    //@NotBlank(message = "Le résumé du contenu est obligatoire", groups = PublicationValidation.class)
    @Size(min = 1, max = 255, message = "Le résumé du contenu doit être compris entre 1 et 255 caractères", groups = PublicationValidation.class)
    @TextAreaNotBlank(groups = PublicationValidation.class)
    @Column(name = "resume_contenu")
    private String resumeContenu;


    @Size( max = 3, message = "La categorisation de l'offre doit être entre 0 et 3 car", groups = PublicationValidation.class)
    @Column(name = "categorisation-offre")
    private String categorisationOffre;


    @Min(value = 0, message = "La valeur doit être 0 ou 1", groups = PublicationValidation.class)
    @Max(value = 1, message = "La valeur doit être 0 ou 1", groups = PublicationValidation.class)
    @Column(name = "blocs_competences")
    private Integer blocsCompetences;

    @Column(name = "code-bloc")
    private List<String> codeBloc;

    @Min(value = 0, message = "Les blocs modifiables doivent valoir 0 ou 1", groups = PublicationValidation.class)
    @Max(value = 1, message = "Les blocs modifiables doivent valoir 0 ou 1", groups = PublicationValidation.class)
    @Column(name = "blocs_modifiables")
    private Integer blocsModifiables;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;


    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_creation", nullable = false)
    private LocalDate dateCreation;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_maj", nullable = false)
    private LocalDate dateMaj;


    @OneToMany(mappedBy = "formation",
            //cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            fetch = FetchType.LAZY)
    private List<Action> actions;

//    @OneToMany(mappedBy = "formation",
//            cascade = {CascadeType.MERGE,CascadeType.PERSIST},
//            fetch = FetchType.LAZY)
//    private List<Contact> contacts;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_contact")
    @ToString.Exclude
    private Contact contact;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "formation_parente_id")
    @ToString.Exclude
    private Formation formationParente;

    @OneToMany(mappedBy = "formationParente", fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<Formation> versionsEnCours;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_composante", nullable = false)
    @ToString.Exclude
    private Composante composante;


    @AssertTrue(message = "Le type de certification doit être cohérent avec son code", groups = {BrouillonValidation.class,PublicationValidation.class})
    private boolean isCoherentCert(){
        if (typeCertification.equalsIgnoreCase("RNCP")){
            return codeCertification.startsWith("RNCP") ;
        }
        else if (typeCertification.equalsIgnoreCase("RS")){
            return codeCertification.startsWith("RS") ;
        }
        else {
            return codeCertification.startsWith("CPF") ;
        }
    }
    @AssertTrue(message = "La longueur doit être entre 5 et 10", groups = {BrouillonValidation.class,PublicationValidation.class})
    private boolean isHisSizeRncp(){
        if (codeCertification.startsWith("RNCP")){
            return codeCertification.length()>=5 &&  codeCertification.length()<=10;
        }

        return true;

    }

    @AssertTrue(message = "La longueur doit être entre 3 et 10 ", groups = {BrouillonValidation.class,PublicationValidation.class})
    private boolean isHisSizeRs(){
        if (codeCertification.startsWith("RS")){
            return codeCertification.length()>=3 &&  codeCertification.length()<=10;
        }
        return true;

    }

    @AssertTrue(message = "La longueur doit être entre 4 et 10 ", groups = {BrouillonValidation.class,PublicationValidation.class})
    private boolean isHisSizeCpf(){
        if (codeCertification.startsWith("CPF")){
            return codeCertification.length()>=4 &&  codeCertification.length()<=10;
        }
        return true;

    }


    @AssertTrue(message = "Ne peux être présent que pour un code certification CPF200.", groups = {BrouillonValidation.class,PublicationValidation.class})
    private boolean categorieCpf(){
        if (codeCertification.startsWith("CPF200")){
            return  categorisationOffre != null && !categorisationOffre.isBlank();
        }
        return true;

    }


    @AssertTrue(message = "Au moins un code bloc doit être renseigné lorsque les blocs de compétences sont activés", groups = PublicationValidation.class)
    public boolean isCodeBlocValid() {

        if (blocsCompetences == null || blocsCompetences == 0) {
            return true;
        }

        return codeBloc != null
                && !codeBloc.isEmpty()
                && codeBloc.stream().anyMatch(s -> s != null && !s.isBlank());
    }

    @AssertTrue(message = "Les blocs de compétences ne sont autorisés que pour une certification RNCP", groups = PublicationValidation.class)
    public boolean isBlocsCompetencesCompatibleWithRncp() {

        if (blocsCompetences == null || blocsCompetences == 0) {
            return true;
        }

        return codeCertification != null
                && codeCertification.startsWith("RNCP");
    }

    @AssertTrue(message = "La formation n'a pas de contact", groups = PublicationValidation.class)
    private boolean isContact(){
        return contact != null;
    }


}
