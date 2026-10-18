package amu.ter.cpf.controller;

import amu.ter.cpf.model.entity.Composante;
import amu.ter.cpf.model.entity.Contact;
import amu.ter.cpf.model.entity.Role;
import amu.ter.cpf.model.entity.User;
import amu.ter.cpf.service.ContactService;
import amu.ter.cpf.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@WebAppConfiguration
public class ContactControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @MockitoBean
    private ContactService contactService;

    @MockitoBean
    private UserService userService;

    private MockMvc mockMvc;
    private User manager;
    private Composante composante;
    private Contact contact;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(webApplicationContext)
                .build();

        composante = new Composante();
        composante.setCodeComposante("SCI");
        composante.setName("Sciences");

        manager = new User();
        manager.setEmail("manager@test.com");
        manager.setRole(Role.MANAGER);
        manager.setComposante(composante);

        contact = new Contact();
        contact.setCodeContact("C001");
        contact.setNom("Dupont");
        contact.setPrenom("Jean");
        contact.setComposante(composante);
    }


    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void listContactsShouldReturnContactListPage() throws Exception {
        when(userService.getUserByEmail("manager@test.com"))
                .thenReturn(manager);
        when(contactService.getContactsByComposante(composante))
                .thenReturn(List.of(contact));

        mockMvc.perform(get("/contact"))
                .andExpect(status().isOk())
                .andExpect(view().name("list_contact"))
                .andExpect(model().attributeExists("contacts"));
    }


    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void showCreateFormShouldReturnContactForm() throws Exception {
        when(userService.getUserByEmail("manager@test.com"))
                .thenReturn(manager);

        mockMvc.perform(get("/contact/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("contact_form"))
                .andExpect(model().attributeExists("contact"))
                .andExpect(model().attribute("isEdit", false))
                .andExpect(model().attributeExists("indicesRepetition"))
                .andExpect(model().attributeExists("naturesVoie"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void showCreateFormShouldReturnNoComposantePageWhenManagerHasNoComposante()
            throws Exception {
        manager.setComposante(null);
        when(userService.getUserByEmail("manager@test.com"))
                .thenReturn(manager);

        mockMvc.perform(get("/contact/create"))
                .andExpect(status().isOk())
                .andExpect(view().name("no_composante"))
                .andExpect(model().attributeExists("message"));
    }




    @Test
    @WithMockUser(username = "manager@test.com", roles = "MANAGER")
    void createContactShouldRedirectOnSuccess() throws Exception {

        when(userService.getUserByEmail("manager@test.com"))
                .thenReturn(manager);

        when(contactService.getContactByCodeContact("C001"))
                .thenReturn(null);

        mockMvc.perform(post("/contact/create")
                        .param("codeContact", "C001")
                        .param("nom", "Dupont")
                        .param("prenom", "Jean")
                        .param("telephone", "+33123456789")
                        .param("adresseLigne1", "Rue test")
                        .param("codePostal", "75000")
                        .param("ville", "Paris")
                        .param("courriel", "test@mail.com")
                        .param("conformiteReglementaire", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contact"));

        verify(contactService).create(any(Contact.class), eq(manager));
    }
    @Test
    @WithMockUser(username = "manager@test.com", roles = "MANAGER")
    void createContactShouldReturnFormWhenCodeAlreadyExists() throws Exception {

        when(userService.getUserByEmail("manager@test.com"))
                .thenReturn(manager);

        when(contactService.getContactByCodeContact("C001"))
                .thenReturn(contact);

        mockMvc.perform(post("/contact/create")
                        .param("codeContact", "C001")
                        .param("nom", "Dupont")
                        .param("prenom", "Jean")
                        .param("telephone", "+33123456789")
                        .param("adresseLigne1", "Rue test")
                        .param("codePostal", "75000")
                        .param("ville", "Paris")
                        .param("courriel", "test@mail.com")
                        .param("conformiteReglementaire", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("contact_form"));
    }



    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void showEditFormShouldReturnContactForm() throws Exception {
        when(userService.getUserByEmail("manager@test.com"))
                .thenReturn(manager);
        when(contactService.getContactByCodeContact("C001"))
                .thenReturn(contact);

        mockMvc.perform(get("/contact/edit/C001"))
                .andExpect(status().isOk())
                .andExpect(view().name("contact_form"))
                .andExpect(model().attributeExists("contact"))
                .andExpect(model().attribute("isEdit", true));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void showEditFormShouldReturnErrorPageWhenNotFound() throws Exception {
        when(userService.getUserByEmail("manager@test.com"))
                .thenReturn(manager);
        when(contactService.getContactByCodeContact("UNKNOWN"))
                .thenReturn(null);

        mockMvc.perform(get("/contact/edit/UNKNOWN"))
                .andExpect(status().isOk())
                .andExpect(view().name("error_page"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void showEditFormShouldReturnErrorPageWhenNotSameComposante() throws Exception {
        Composante autre = new Composante();
        autre.setCodeComposante("DROIT");
        contact.setComposante(autre);

        when(userService.getUserByEmail("manager@test.com"))
                .thenReturn(manager);
        when(contactService.getContactByCodeContact("C001"))
                .thenReturn(contact);

        mockMvc.perform(get("/contact/edit/C001"))
                .andExpect(status().isOk())
                .andExpect(view().name("error_page"));
    }



    @Test
    @WithMockUser(username = "manager@test.com", roles = "MANAGER")
    void updateContactShouldRedirectOnSuccess() throws Exception {

        when(userService.getUserByEmail("manager@test.com"))
                .thenReturn(manager);

        mockMvc.perform(post("/contact/edit/C001")
                        .param("codeContact", "C001")
                        .param("nom", "Dupont")
                        .param("prenom", "Jean")
                        .param("telephone", "+33123456789")
                        .param("adresseLigne1", "Rue test")
                        .param("codePostal", "75000")
                        .param("ville", "Paris")
                        .param("courriel", "test@mail.com")
                        .param("conformiteReglementaire", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contact"));

        verify(contactService).update(eq("C001"), any(Contact.class));
    }

    @Test
    @WithMockUser(username = "manager@test.com", roles = "MANAGER")
    void updateContactShouldReturnFormOnValidationErrors() throws Exception {

        mockMvc.perform(post("/contact/edit/C001")
                        .param("codeContact", "")
                        .param("nom", "")
                        .param("prenom", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("contact_form"));
    }



    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void deleteContactShouldRedirectOnSuccess() throws Exception {
        when(userService.getUserByEmail("manager@test.com"))
                .thenReturn(manager);
        when(contactService.getContactByCodeContact("C001"))
                .thenReturn(contact);

        mockMvc.perform(post("/contact/delete/C001"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contact"));

        verify(contactService).delete("C001");
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void deleteContactShouldReturnErrorPageWhenNotFound() throws Exception {
        when(userService.getUserByEmail("manager@test.com"))
                .thenReturn(manager);
        when(contactService.getContactByCodeContact("UNKNOWN"))
                .thenReturn(null);

        mockMvc.perform(post("/contact/delete/UNKNOWN"))
                .andExpect(status().isOk())
                .andExpect(view().name("error_page"));
    }

    @Test
    @WithMockUser(username = "manager@test.com", authorities = "ROLE_MANAGER")
    void deleteContactShouldReturnErrorPageWhenNotSameComposante() throws Exception {
        Composante autre = new Composante();
        autre.setCodeComposante("DROIT");
        contact.setComposante(autre);

        when(userService.getUserByEmail("manager@test.com"))
                .thenReturn(manager);
        when(contactService.getContactByCodeContact("C001"))
                .thenReturn(contact);

        mockMvc.perform(post("/contact/delete/C001"))
                .andExpect(status().isOk())
                .andExpect(view().name("error_page"));
    }



    @Test
    @WithMockUser
    void viewContactShouldReturnContactView() throws Exception {
        when(contactService.getContactByCodeContact("C001"))
                .thenReturn(contact);

        mockMvc.perform(get("/contact/view/C001"))
                .andExpect(status().isOk())
                .andExpect(view().name("contact_view"))
                .andExpect(model().attributeExists("contact"));
    }

    @Test
    @WithMockUser
    void viewContactShouldReturnErrorPageWhenNotFound() throws Exception {
        when(contactService.getContactByCodeContact("UNKNOWN"))
                .thenReturn(null);

        mockMvc.perform(get("/contact/view/UNKNOWN"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }
}