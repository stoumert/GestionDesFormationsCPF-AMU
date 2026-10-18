package amu.ter.cpf.model.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "composante")
public class Composante {
    @Id
    @Column(name = "code_composante", length = 36, unique = true, nullable = false, updatable = false)
    @NotBlank(message = "Le code est obligatoire.")
    @Pattern(
            regexp = "^\\p{L}+$",
            message = "Le code de la composante ne doit contenir que des lettres (aucun chiffre ni caractère spécial)."
    )
    private String codeComposante;

    @Column(name = "nom", nullable = false, unique = true, updatable = false)
    @NotBlank(message = "Le nom est obligatoire.")
    @Pattern(
            regexp = "^\\p{L}+( \\p{L}+)*$",
            message = "Le nom ne doit contenir que des lettres et des espaces simples (aucun chiffre ni caractère spécial)."
    )
    private String name;

    @OneToMany(mappedBy = "composante", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @OneToMany(mappedBy = "composante",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Formation> formations = new ArrayList<>();

    @OneToMany(mappedBy = "composante",
            cascade = {CascadeType.MERGE,CascadeType.PERSIST},
            fetch = FetchType.LAZY)
    private List<Contact> contacts = new ArrayList<>();

    public void addContact(Contact contact) {
        contacts.add(contact);
        contact.setComposante(this);
    }

    public void removeContact(Contact contact) {
        contacts.remove(contact);
        contact.setComposante(null);
    }
}
