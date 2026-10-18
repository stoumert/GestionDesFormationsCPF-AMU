package amu.ter.cpf.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "export_xml")
public class ExportXML {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "date_export")
    private LocalDate dateExport;


    @Column(name = "contenu_xml", columnDefinition = "MEDIUMTEXT")
    private String contenuXml;

    @ManyToOne
    @JoinColumn(name = "id_utilisateur")
    @ToString.Exclude
    private User user;
}
