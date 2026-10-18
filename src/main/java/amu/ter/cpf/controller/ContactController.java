package amu.ter.cpf.controller;


import amu.ter.cpf.model.entity.*;
import amu.ter.cpf.model.referential.IndiceRepetition;
import amu.ter.cpf.model.referential.NatureVoie;
import amu.ter.cpf.service.ContactService;
import amu.ter.cpf.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/contact")
@RequiredArgsConstructor
public class ContactController {

  private final  UserService userService;
  private final ContactService contactService;

  @PreAuthorize("hasRole('MANAGER')")
  @GetMapping("/create")
  public ModelAndView createContact() {


    List<Composante> composantes = getComposantesForUser();

    if (composantes.isEmpty()) {
      ModelAndView mav = new ModelAndView("no_composante");
      mav.addObject("message",
          "Votre compte n’est associé à aucune composante. Vous ne pouvez pas créer de contact.");
      return mav;
    }
    Contact contact = new Contact();
    ModelAndView modelAndView = new ModelAndView("contact_form");
    modelAndView.addObject("contact", contact);
    modelAndView.addObject("indicesRepetition", IndiceRepetition.values());
    modelAndView.addObject("naturesVoie", NatureVoie.values());
    modelAndView.addObject("formAction", "/contact/create");
    modelAndView.addObject("isEdit", false);
    return modelAndView;

  }

  private List<Composante> getComposantesForUser() {
    String currentUserEmail = Objects.requireNonNull(SecurityContextHolder.getContext().getAuthentication()).getName();
    User user = userService.getUserByEmail(currentUserEmail);
    if (user.getComposante() != null) {
      return List.of(user.getComposante());
    }
    return List.of();
  }

  @PreAuthorize("hasRole('MANAGER')")
  @PostMapping("/create")
  public String createContact(@Valid @ModelAttribute Contact contact,
                                 BindingResult bindingResult,
                                 Model model) {
    if (contactService.getContactByCodeContact(contact.getCodeContact()) != null) {
      bindingResult.rejectValue("codeContact", "codeContact.duplicate", "Ce code est déjà utilisé.");
    }

    if (bindingResult.hasErrors()) {
      model.addAttribute("indicesRepetition", IndiceRepetition.values());
      model.addAttribute("naturesVoie", NatureVoie.values());
      model.addAttribute("formAction", "/contact/create");
      model.addAttribute("isEdit", false);
      return "contact_form";
    }
    User manager = getCurrentUser();
    contactService.create(contact,manager);

    return "redirect:/contact";
  }

  private User getCurrentUser() {

    String currentUserEmail = Objects.requireNonNull(
        SecurityContextHolder.getContext().getAuthentication()
    ).getName();
    return userService.getUserByEmail(currentUserEmail);
  }

  @PreAuthorize("hasRole('MANAGER')")
  @GetMapping
  public ModelAndView listContacts() {
    User manager = getCurrentUser();
    ModelAndView mav = new ModelAndView("list_contact");
    mav.addObject("contacts", contactService.getContactsByComposante(manager.getComposante()));

    return mav;
  }



  @PreAuthorize("hasRole('MANAGER')")
  @GetMapping("/edit/{code}")
  public String editContact(
          @PathVariable String code,
          Model model) {

    Contact contact = contactService.getContactByCodeContact(code);

    if (contact == null) {
      model.addAttribute("message", "Contact introuvable");
      return "error_page";
    }

    User manager = getCurrentUser();

    if (!contact.getComposante().getCodeComposante()
            .equals(manager.getComposante().getCodeComposante())) {

      model.addAttribute("message",
              "Vous ne pouvez pas modifier ce contact");

      return "error_page";
    }

    model.addAttribute("contact", contact);
    model.addAttribute("isEdit", true);
    model.addAttribute("formAction",
            "/contact/edit/" + code);

    model.addAttribute("naturesVoie",
            NatureVoie.values());
    model.addAttribute("indicesRepetition", IndiceRepetition.values());


    return "contact_form";
  }


  @PostMapping("/edit/{code}")
  @PreAuthorize("hasRole('MANAGER')")
  public String updateContact(
          @PathVariable String code,
          @Valid @ModelAttribute Contact contact,
          BindingResult bindingResult,
          Model model) {
    if (bindingResult.hasErrors()) {

      model.addAttribute("indicesRepetition",
              IndiceRepetition.values());

      model.addAttribute("naturesVoie",
              NatureVoie.values());

      model.addAttribute("formAction",
              "/contact/edit/" + code);

      model.addAttribute("isEdit", true);

      return "contact_form";
    }

    contactService.update(code, contact);

    return "redirect:/contact";
  }

  @PreAuthorize("hasRole('MANAGER')")
  @PostMapping("/delete/{code}")
  public String deleteContact(
          @PathVariable String code,
          Model model) {

    Contact contact =
            contactService.getContactByCodeContact(code);

    if (contact == null) {
      model.addAttribute("message",
              "Contact introuvable");
      return "error_page";
    }

    User manager = getCurrentUser();

    if (!contact.getComposante().getCodeComposante()
            .equals(manager.getComposante().getCodeComposante())) {

      model.addAttribute("message",
              "Suppression interdite");

      return "error_page";
    }

    contactService.delete(code);

    return "redirect:/contact";
  }

  @GetMapping("/view/{code}")
  public String viewContact(
          @PathVariable String code,
          Model model) {

    Contact contact =
            contactService.getContactByCodeContact(code);

    if (contact == null) {

      model.addAttribute(
              "message",
              "Contact introuvable");

      return "error";
    }

    model.addAttribute("contact", contact);

    return "contact_view";
  }


}
