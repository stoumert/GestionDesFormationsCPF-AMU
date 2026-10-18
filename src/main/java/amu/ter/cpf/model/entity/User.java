package amu.ter.cpf.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "utilisateur")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Integer id;

    @Column(nullable = false)
    private String loginCAS;

    @Column(nullable = false, length = 100, updatable = false)
    private String lastName;

    @Column(nullable = false, length = 100, updatable = false)
    private String firstName;

    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "code_composante")
    private Composante composante;

    @OneToMany(mappedBy = "user",
            cascade = {CascadeType.MERGE, CascadeType.PERSIST},
            fetch = FetchType.LAZY)
    @ToString.Exclude
    private List<ExportXML> exportXML;


}
