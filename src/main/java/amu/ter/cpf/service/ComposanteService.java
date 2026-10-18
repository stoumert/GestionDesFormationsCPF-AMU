package amu.ter.cpf.service;

import amu.ter.cpf.dao.ComposanteDAO;
import amu.ter.cpf.dao.UserDAO;
import amu.ter.cpf.model.entity.Composante;
import amu.ter.cpf.model.entity.Role;
import amu.ter.cpf.model.entity.User;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ComposanteService {
    private final ComposanteDAO composanteDAO;
    private final UserDAO userDAO;

    @PostConstruct
    public void init() {
        Composante composante = new Composante();
        composante.setCodeComposante("SCI");
        composante.setName("Science");
        composanteDAO.save(composante);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public Composante create(Composante composante) {
        return composanteDAO.save(composante);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public void assignComposanteToManager(Composante composante, User manager) {
        if (manager == null || !manager.getRole().equals(Role.MANAGER)) return;
        manager.setComposante(composante);
        userDAO.save(manager);
    }
    public Composante getComposanteByName(String name) {
        return composanteDAO.findByName(name).orElse(null);
    }
    public Composante getComposanteByCodeComposante(String code) {
        return composanteDAO.findByCodeComposante(code).orElse(null);
    }
    public List<Composante> getAllComposantes() {
        return composanteDAO.findAll();
    }
    public Composante getComposanteByCodeWithUsers(String codeComposante){
        return composanteDAO.findByCodeWithUsers(codeComposante);
    }
    @PreAuthorize("hasRole('ADMIN')")
    public void assignComposanteToManagers(Composante composante, List<Integer> managerIds) {
        managerIds.forEach(managerId -> {
            User manager = userDAO.findById(managerId).orElse(null);
            assignComposanteToManager(composante, manager);
        });
    }

    @PreAuthorize("hasRole('ADMIN')")
    public void removeManager(Composante composante, User manager) {
        if (!manager.getComposante().getCodeComposante().equals(composante.getCodeComposante())) return;
        manager.setComposante(null);
        userDAO.save(manager);
    }

    public boolean existsByCodeComposante(String codeComposante) {
        return composanteDAO.existsById(codeComposante);
    }
}
