package amu.ter.cpf.model.entity;

import amu.ter.cpf.validation.PublicationValidation;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "session")
public class Session {
    @Id
    @NotBlank(message = "Le code session est obligatoire")
    @Size(min = 1, max = 50, message = "Le code session doit contenir entre 1 et 30 caractères")
    @Column(name = "code_session", nullable = false, length = 30)
    private String codeSession;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_debut")
    private LocalDate dateDebut;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date_fin")
    private LocalDate dateFin;

    @NotNull(message = "L’état du recrutement est obligatoire")
    @Column(name = "etat_recrutement",nullable = false)
    private Integer etatRecrutement;

    @NotNull(message = "La garantie est obligatoire")
    @Column(name = "garantie",nullable = false)
    private Integer garantie;

    @Size(max = 3000, message = "Les modalités particulières ne doivent pas dépasser 3000 caractères")
    @Column(name = "modalites_particulieres", columnDefinition = "TEXT")
    private String modalitesParticulieres;

    @Column(name = "date_creation")
    private LocalDate dateCreation;

    @Column(name = "date_maj")
    private LocalDate dateMaj;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_action", nullable = false)
    @ToString.Exclude
    private Action action;

    @Column(name = "ouverture")
    private Boolean ouverture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_session_parente")
    @ToString.Exclude
    private Session sessionParente;

    @AssertTrue(message = "Vous devez renseigner les deux dates, ou laisser les deux dates vides. La date de début doit être inférieure ou égale à la date de fin.")
    public boolean isPeriodeValide() {

        if (dateDebut == null && dateFin == null) {
            return true;
        }
        if (dateDebut == null || dateFin == null) {
            return false;
        }
        return !dateDebut.isAfter(dateFin);
    }
    @AssertTrue(message = "Le code de la session doit obligatoirement commencer par le code de l'action",
            groups = PublicationValidation.class)
    public boolean isCodeSessionStartWithCodeAction() {
        if (codeSession == null || action == null
                || action.getCodeAction() == null) {
            return true;
        }
        String codeActionPropre  = action.getCodeAction().replace("_*", "");
        String codeSessionPropre = codeSession.replace("_*", "");
        return codeSessionPropre.startsWith(codeActionPropre);
    }

}
