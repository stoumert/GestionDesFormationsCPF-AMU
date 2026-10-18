package amu.ter.cpf.model.entity;

import amu.ter.cpf.model.referential.IndiceRepetition;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "contact")
public class Contact {

    @Id
    @NotBlank(message = "L'identificateur du contact est obligatoire")
    @Size(min = 1, max = 30, message = "Le code contact doit contenir entre 1 et 30 caractères")
    @Column(name = "code_contact", length = 30,nullable = false)
    private String codeContact;

    @Column(name = "numero_coord")
    private Integer numeroCoord;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(min = 1, max = 50, message = "Le nom doit contenir entre 1 et 50 caractères")
    @Column(name = "nom", length = 50,nullable=false)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(min = 1, max = 50, message = "Le prénom doit contenir entre 1 et 50 caractères")
    @Column(name = "prenom", length = 50 , nullable = false)
    private String prenom;

    @NotBlank(message = "Le numéro est obligatoire")
    @Size(min = 10, max = 15, message = "Le numéro doit contenir entre 1 et 15 caractères")
    @Pattern(regexp = "^\\+?\\d{1,15}$", message = "Le téléphone doit contenir uniquement des chiffres, avec éventuellement + au début")
    @Column(name = "telephone", length = 15,nullable = false)
    private String telephone;


    @Column(name = "fonction")
    @Size(max = 75)
    private String fonction;

    @Size(max = 50, message = "La ligne 1 ne doit pas dépasser 50 caractères")
    @Column(name = "adresse_ligne1", length = 50)
    @NotBlank(message = "L'adresse est obligatoire")
    private String adresseLigne1;

    @Size(max = 50, message = "La ligne 2 ne doit pas dépasser 50 caractères")
    @Column(name = "adresse_ligne2", length = 50)
    private String adresseLigne2;

    // uniquement pour pays france
    @Pattern(    regexp = "^$|^\\d{5}$", message = "Le code postal doit contenir exactement 5 chiffres")
    @Column(name = "code_postal", length = 5)
    @NotBlank(message = "Le code postal est obligatoire")
    private String codePostal;

    @Size(max = 50, message = "La ville ne doit pas dépasser 50 caractères")
    @Column(name = "ville", length = 50)
    @NotBlank(message = "La ville est obligatoire")
    private String ville;

    @DecimalMin(value = "-90.0",
            message = "La latitude doit être comprise entre -90 et 90")
    @DecimalMax(value = "90.0",
            message = "La latitude doit être comprise entre -90 et 90")
    private BigDecimal latitude;

    @DecimalMin(value = "-180.0",
            message = "La longitude doit être comprise entre -180 et 180")
    @DecimalMax(value = "180.0",
            message = "La longitude doit être comprise entre -180 et 180")
    private BigDecimal longitude;

    @Size( max = 4, message = "Le numéro de voie ne doit pas dépasser 4 caractères")
    @Column(name = "numero_voie", length = 4)
    private String numeroVoie;

    @Column(name = "code_nature_voie", length = 4)
    private String codeNatureVoie;

    @Size(max = 32, message = "Le libellé de voie ne doit pas dépasser 32 caractères")
    @Column(name = "libelle_voie", length = 32)
    private String libelleVoie;

    @Min(value = 0)
    @Max(value = 1)
    @NotNull(message = "La conformité réglementaire est obligatoire")
    private Integer conformiteReglementaire;

    @NotBlank(message =("ce champ est obligatoire"))
    @Column(name="courriel",nullable = false,length=160)
    @Size(min=3,max=160,message="La longueur de la rubrique doit être comprise entre 3 et 160 caractères")
    @Email(message = "Le courriel doit être valide")
    private String courriel;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_parent")
    @ToString.Exclude
    private Contact contactParent;


    @Column(name = "code_indice_repetition", length = 1)
    private String codeIndiceRepetition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_composante", nullable = false)
    @ToString.Exclude
    private Composante composante;


    @AssertTrue(message = "L'indice de répétition n'est pas valide")
    public boolean isIndiceValid() {
        if (codeIndiceRepetition!= null && !codeIndiceRepetition.isBlank()) {
            return IndiceRepetition.isValidIndex(codeIndiceRepetition);
        }
        return true;
    }

    @AssertTrue(message = "Le code nature de voie et le libellé de voie doivent être alimentés si le numéro dans la voie l'est")
    public boolean isNumeroVoieValid() {
        if (numeroVoie != null && !numeroVoie.isBlank()) {
            return !codeNatureVoie.isBlank() && !libelleVoie.isBlank();
        }
        return true;
    }
    @AssertTrue(message = "Le numéro dans la voie, le code nature de voie et le libellé de voie doivent être alimentés si l'indice de répétition l'est")
    public boolean isIndiceRepetitionValid() {
        if (codeIndiceRepetition != null && !codeIndiceRepetition.isBlank()) {
            return !numeroVoie.isBlank() && !codeNatureVoie.isBlank() && !libelleVoie.isBlank();
        }
        return true;
    }

}
