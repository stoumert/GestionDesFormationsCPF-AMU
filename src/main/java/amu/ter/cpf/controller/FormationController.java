package amu.ter.cpf.controller;

import amu.ter.cpf.model.entity.*;
import amu.ter.cpf.service.ComposanteService;
import amu.ter.cpf.service.ContactService;
import amu.ter.cpf.service.FormationService;
import amu.ter.cpf.service.UserService;
import amu.ter.cpf.validation.BrouillonValidation;
import amu.ter.cpf.validation.Step1Validation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/formation")
@RequiredArgsConstructor
public class FormationController {

    private final FormationService formationService;
    private final ComposanteService composanteService;
    private final UserService userService;
    private final ContactService contactService;
  /*  @GetMapping("")
    public ModelAndView getFormations(Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        List<Formation> formations;

        if (user.getRole() == Role.ADMIN) {
            formations = formationService.getAll();
        } else if (user.getRole() == Role.MANAGER && user.getComposante() != null) {
            formations = formationService.getAll().stream()
                    .filter(f -> f.getComposante().getCodeComposante()
                            .equals(user.getComposante().getCodeComposante()))
                    .toList();
        } else {
            formations = formationService.getAllActif();
        }

        ModelAndView mav = new ModelAndView("list_formations");
        mav.addObject("formations", formations);
        mav.addObject("currentUser", user);
        return mav;
    }*/
  @GetMapping("")
  public ModelAndView getFormations(
          Authentication authentication,
          @RequestParam(required = false) String intitule,
          @RequestParam(required = false) String code,
          @RequestParam(required = false) String codeComposante,
          @RequestParam(required = false) String typeCertification,
          @RequestParam(required = false) String codeCertification
  ) {
      User user = userService.getUserByEmail(authentication.getName());

      List<Formation> formations = formationService.search(
              intitule, code, codeComposante, typeCertification, codeCertification);

      List<Composante> composantes = composanteService.getAllComposantes();

      ModelAndView mav = new ModelAndView("list_formations");
      mav.addObject("formations", formations);
      mav.addObject("currentUser", user);
      mav.addObject("composantes", composantes);
      mav.addObject("intitule", intitule);
      mav.addObject("code", code);
      mav.addObject("codeComposante", codeComposante);
      mav.addObject("typeCertification", typeCertification);
      mav.addObject("codeCertification", codeCertification);
      return mav;
  }


    @GetMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ModelAndView showStep1() {
        List<Composante> composantes = getComposantesForUser();
        if (composantes.isEmpty()) {
            return noComposanteView("Votre compte n'est associé à aucune composante. Vous ne pouvez pas créer de formation.");
        }

        ModelAndView mav = new ModelAndView("formation_step1");
        mav.addObject("formation", new Formation());
        mav.addObject("composantes", composantes);
        return mav;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ModelAndView submitStep1(
            @Validated(BrouillonValidation.class) @ModelAttribute Formation formation,
            BindingResult bindingResult,
            @RequestParam String codeComposante,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("formation_step1");
            mav.addObject("formation", formation);
            mav.addObject("composantes", getComposantesForUser());
            return mav;
        }

        try {
            formation.setComposante(
                    composanteService.getComposanteByCodeComposante(codeComposante)
            );
          formation.setIntitule("Formation " + formation.getCodeFormation());
          Formation created = formationService.createFormationIdentity(formation);
          redirectAttributes.addFlashAttribute(
              "success",
              "Votre formation a été créée."
          );
            return new ModelAndView(
                    "redirect:/formation/" + created.getCodeFormation() + "/edit-content"
            );


        } catch (Exception e) {
            ModelAndView mav = new ModelAndView("formation_step1");
            mav.addObject("formation", formation);
            mav.addObject("composantes", getComposantesForUser());
            mav.addObject("error", e.getMessage());
            return mav;
        }
    }
    @GetMapping("/{codeFormation}/edit-content")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ModelAndView showStep2(@PathVariable String codeFormation) {
        Formation formation = formationService.getFormationByCode(codeFormation);
        List<Contact> contacts =  contactService.getContactsByComposante(formation.getComposante());
        if (formation == null) return new ModelAndView("redirect:/formation");

        ModelAndView mav = new ModelAndView("formation_form");
        mav.addObject("formation", formation);
        mav.addObject("contacts", contacts);
        mav.addObject("formAction", "/formation/" + codeFormation + "/edit-content");
        mav.addObject("isEdit", false);
        return mav;
    }

    @PostMapping("/{codeFormation}/edit-content")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ModelAndView submitStep2(
            @PathVariable String codeFormation,
            @Validated(Step1Validation.class) @ModelAttribute Formation formation,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("formation_form");
            mav.addObject("formation", formation);
            mav.addObject("formAction", "/formation/" + codeFormation + "/edit-content");
            mav.addObject("isEdit", false);
            return mav;
        }

        try {
            formationService.createFormationWithAllOtherFields(formation, codeFormation);
            redirectAttributes.addFlashAttribute("success",
                    "Formation enregistrée avec succès");
            return new ModelAndView("redirect:/formation");
        } catch (Exception e) {
            ModelAndView mav = new ModelAndView("formation_form");
            mav.addObject("formation", formation);
            mav.addObject("formAction", "/formation/" + codeFormation + "/edit-content");
            mav.addObject("isEdit", false);
            mav.addObject("error", e.getMessage());
            return mav;
        }
    }

    @GetMapping("/{code}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ModelAndView showEdit(
            @PathVariable String code,
            Authentication authentication
    ) {
        Formation formation = formationService.getFormationByCode(code);
        List<Contact> contacts =  contactService.getContactsByComposante(formation.getComposante());

        if (formation == null) return new ModelAndView("redirect:/formation");

        if (!hasAccessToFormation(formation, authentication)) {
            return noComposanteView("Cette formation n'appartient pas à votre composante.");
        }

        ModelAndView mav = new ModelAndView("formation_form");
        mav.addObject("contacts", contacts);
        mav.addObject("formation", formation);
        mav.addObject("formAction", "/formation/" + code + "/edit");
        mav.addObject("isEdit", true);
        return mav;
    }
    @GetMapping("/{codeFormation}/actions")
    public ModelAndView getActions(@PathVariable String codeFormation) {
        Formation formation = formationService.getFormationByCodeWithActions(codeFormation);
        if (formation == null) return new ModelAndView("redirect:/formation");
        ModelAndView mav = new ModelAndView("formation_actions");
        mav.addObject("formation", formation);
        return mav;
    }
    @PostMapping("/{code}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ModelAndView submitEdit(
            @PathVariable String code,
            @Validated(Step1Validation.class) @ModelAttribute Formation formation,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            ModelAndView mav = new ModelAndView("formation_form");
            mav.addObject("formation", formation);
            mav.addObject("formAction", "/formation/" + code + "/edit");
            mav.addObject("isEdit", true);
            return mav;
        }

        try {
            formationService.alterFormation(code, formation);
            redirectAttributes.addFlashAttribute("success", "Modification enregistrée.");
            return new ModelAndView("redirect:/formation/view/"+code);
        } catch (Exception e) {
            ModelAndView mav = new ModelAndView("formation_form");
            mav.addObject("formation", formation);
            mav.addObject("formAction", "/formation/" + code + "/edit");
            mav.addObject("isEdit", true);
            mav.addObject("error", e.getMessage());
            return mav;
        }
    }
    @PostMapping("/{codeFormation}/publish")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public String publishFormation(
            @PathVariable String codeFormation,
            RedirectAttributes redirectAttributes
    ) {
        try {
            formationService.publishFormation(codeFormation);
            redirectAttributes.addFlashAttribute("success", "Formation publiée avec succès");
        } catch (ConstraintViolationException e) {
            String erreurs = e.getConstraintViolations().stream()
                    .map(v -> v.getPropertyPath() + " : " + v.getMessage())
                    .collect(Collectors.joining(", "));
            redirectAttributes.addFlashAttribute("error",
                    "Publication impossible — champs invalides : " + erreurs);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/formation";
    }
    @PostMapping("/createDraft/{codeFormation}")
    public String createDraftFormation(@PathVariable String codeFormation, RedirectAttributes redirectAttributes) {
        Formation formation = formationService.getFormationByCode(codeFormation);

        if (formationService.existsFormationByFormationParente(formation)){
            redirectAttributes.addFlashAttribute("error", "Un brouillon de cette formation exite déja.");
            return "redirect:/formation";
        }
        Principal principal = SecurityContextHolder.getContext().getAuthentication();
        User user = userService.getUserByEmail(Objects.requireNonNull(principal).getName());
        if (formation.getStatus() == Status.ACTIF && user.getRole().equals(Role.ADMIN)) {
            formation = formationService.createDraftVersion(codeFormation);
            redirectAttributes.addFlashAttribute("success", "Brouillon créé.");
            return "redirect:/formation/view/"+formation.getCodeFormation();
        }
        if (user.getComposante() == null){
            redirectAttributes.addFlashAttribute("error", "Votre compte n’est associé à aucune composante. Vous ne pouvez pas créer de brouillon");
            return "redirect:/formation";
        }
        if (!formation.getComposante().getCodeComposante().equals(user.getComposante().getCodeComposante()) && user.getRole() != Role.ADMIN) {
            redirectAttributes.addFlashAttribute("error", "Impossible de créer un brouillon, vous n'avez pas l'autorisation");
            return "redirect:/formation";
        }
        if (formation.getStatus() == Status.ACTIF) {
            formation = formationService.createDraftVersion(codeFormation);
            redirectAttributes.addFlashAttribute("success", "Brouillon créé.");
            return "redirect:/formation/view/"+formation.getCodeFormation();
        }

        return "redirect:/formation";
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping("{codeFormation}/delete")
    public String deleteDraft(@PathVariable String codeFormation) {
        formationService.deleteFormation(codeFormation);
        return "redirect:/formation";
    }
    private boolean hasAccessToFormation(Formation formation, Authentication authentication) {
        User user = userService.getUserByEmail(authentication.getName());
        if (user.getRole() == Role.ADMIN) return true;
        if (user.getComposante() == null) return false;
        return user.getComposante().getCodeComposante()
                .equals(formation.getComposante().getCodeComposante());
    }

    private List<Composante> getComposantesForUser() {
        String email = Objects.requireNonNull(
                SecurityContextHolder.getContext().getAuthentication()
        ).getName();
        User user = userService.getUserByEmail(email);
        if (user.getRole() == Role.ADMIN) {
            return composanteService.getAllComposantes();
        }
        if (user.getComposante() != null) {
            return List.of(user.getComposante());
        }
        return List.of();
    }

    private ModelAndView noComposanteView(String message) {
        ModelAndView mav = new ModelAndView("no_composante");
        mav.addObject("message", message);
        return mav;
    }

    @GetMapping("/view/{codeFormation}")
    @Transactional
    public String viewFormation(
            @PathVariable String codeFormation,
            Model model) {

        Formation formation =
                formationService.getFormationByCodeWithActions(codeFormation);

//      List<Contact> contactsAssignes =
//          contactService.getContactsByComposante(formation.getComposante());

      List<Contact> contactsDisponibles =
          contactService.getContactsByComposante(formation.getComposante());
      Principal principal = SecurityContextHolder.getContext().getAuthentication();
      User user = userService.getUserByEmail(Objects.requireNonNull(principal).getName());
      model.addAttribute("currentUser", user);
      model.addAttribute("formation", formation);
      model.addAttribute("contactsAssignes", formation.getContact());
      model.addAttribute("contactsDisponibles", contactsDisponibles);

        return "formation_view";
    }

  @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
  @PostMapping("/{codeFormation}/assign-contact")
  public String assignContactToFormation(
      @PathVariable String codeFormation,
      @RequestParam String codeContact,
      Model model) {

    Formation formation =
        formationService.getFormationByCode(codeFormation);

    if (formation == null) {
      model.addAttribute("message", "Formation introuvable");
      return "error_page";
    }

    try {
      contactService.assignContactToFormation(codeContact, codeFormation);
    } catch (Exception e) {
      model.addAttribute("message", e.getMessage());
      return "error_page";
    }

    return "redirect:/formation/view/" + codeFormation;
  }
}