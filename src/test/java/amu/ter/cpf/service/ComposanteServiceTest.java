package amu.ter.cpf.service;

import amu.ter.cpf.dao.ComposanteDAO;
import amu.ter.cpf.dao.UserDAO;
import amu.ter.cpf.model.entity.Composante;
import amu.ter.cpf.model.entity.Role;
import amu.ter.cpf.model.entity.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ComposanteServiceTest {

    @Mock
    private ComposanteDAO composanteDAO;

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private ComposanteService composanteService;



    @Test
    @WithMockUser(roles = "ADMIN")
    void create_shouldSaveComposante() {

        Composante c = new Composante();
        c.setCodeComposante("SCI");

        when(composanteDAO.save(c)).thenReturn(c);

        Composante result = composanteService.create(c);

        assertNotNull(result);
        verify(composanteDAO).save(c);
    }



    @Test
    void assignManager_shouldAssignWhenRoleIsManager() {

        Composante c = new Composante();
        User manager = new User();
        manager.setRole(Role.MANAGER);

        composanteService.assignComposanteToManager(c, manager);

        assertEquals(c, manager.getComposante());
        verify(userDAO).save(manager);
    }



    @Test
    void assignManager_shouldDoNothingIfNotManagerRole() {

        Composante c = new Composante();
        User user = new User();
        user.setRole(Role.USER);

        composanteService.assignComposanteToManager(c, user);

        assertNull(user.getComposante());
        verify(userDAO, never()).save(any());
    }



    @Test
    void assignManager_shouldDoNothingIfUserIsNull() {

        Composante c = new Composante();

        composanteService.assignComposanteToManager(c, null);

        verify(userDAO, never()).save(any());
    }



    @Test
    void assignManagers_shouldAssignOnlyValidManagers() {

        Composante c = new Composante();

        User manager1 = new User();
        manager1.setRole(Role.MANAGER);

        User manager2 = new User();
        manager2.setRole(Role.USER);

        when(userDAO.findById(1)).thenReturn(Optional.of(manager1));
        when(userDAO.findById(2)).thenReturn(Optional.of(manager2));

        composanteService.assignComposanteToManagers(c, List.of(1, 2));

        assertEquals(c, manager1.getComposante());
        assertNull(manager2.getComposante());

        verify(userDAO).save(manager1);
        verify(userDAO, never()).save(manager2);
    }



    @Test
    void removeManager_shouldRemoveIfSameComposante() {

        Composante c = new Composante();
        c.setCodeComposante("SCI");

        User manager = new User();
        manager.setRole(Role.MANAGER);
        manager.setComposante(c);

        composanteService.removeManager(c, manager);

        assertNull(manager.getComposante());
        verify(userDAO).save(manager);
    }


    @Test
    void removeManager_shouldDoNothingIfDifferentComposante() {

        Composante c1 = new Composante();
        c1.setCodeComposante("SCI");

        Composante c2 = new Composante();
        c2.setCodeComposante("LAW");

        User manager = new User();
        manager.setComposante(c2);

        composanteService.removeManager(c1, manager);

        assertEquals(c2, manager.getComposante());
        verify(userDAO, never()).save(any());
    }



    @Test
    void getByName_shouldReturnComposante() {

        Composante c = new Composante();
        c.setName("Science");

        when(composanteDAO.findByName("Science"))
                .thenReturn(Optional.of(c));

        assertEquals(c, composanteService.getComposanteByName("Science"));
    }

    @Test
    void getByCode_shouldReturnComposante() {

        Composante c = new Composante();
        c.setCodeComposante("SCI");

        when(composanteDAO.findByCodeComposante("SCI"))
                .thenReturn(Optional.of(c));

        assertEquals(c, composanteService.getComposanteByCodeComposante("SCI"));
    }

    @Test
    void getAll_shouldReturnList() {

        when(composanteDAO.findAll())
                .thenReturn(List.of(new Composante(), new Composante()));

        assertEquals(2, composanteService.getAllComposantes().size());
    }

    @Test
    void exists_shouldReturnBoolean() {

        when(composanteDAO.existsById("SCI"))
                .thenReturn(true);

        assertTrue(composanteService.existsByCodeComposante("SCI"));
    }

    @Test
    void shouldReturnComposanteWithUsersByCode() {

        Composante composante = new Composante();
        composante.setCodeComposante("SCI");

        when(composanteDAO.findByCodeWithUsers("SCI"))
                .thenReturn(composante);

        Composante result =
                composanteService.getComposanteByCodeWithUsers("SCI");

        assertNotNull(result);
        assertEquals("SCI", result.getCodeComposante());

        verify(composanteDAO).findByCodeWithUsers("SCI");
    }
}