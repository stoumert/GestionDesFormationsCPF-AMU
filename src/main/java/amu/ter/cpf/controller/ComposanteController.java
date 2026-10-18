package amu.ter.cpf.controller;

import amu.ter.cpf.model.entity.Composante;
import amu.ter.cpf.model.entity.User;
import amu.ter.cpf.service.ComposanteService;
import amu.ter.cpf.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import java.util.List;

@Controller()
@RequestMapping("/composante")
@RequiredArgsConstructor
public class ComposanteController {

    private final ComposanteService composanteService;
    private final UserService userService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/create")
    public ModelAndView createComposante() {
        Composante composante = new Composante();
        List<User> managers = userService.getManagersWithoutAssignComposante();
        ModelAndView modelAndView = new ModelAndView("composante_form");
        modelAndView.addObject("composante", composante);
        modelAndView.addObject("managers", managers);
        return modelAndView;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createComposante(@Valid @ModelAttribute Composante composante,
                                    BindingResult bindingResult,
                                    @RequestParam(required = false) List<Integer> managerIds,
                                    Model model) {
        if (composanteService.getComposanteByCodeComposante(composante.getCodeComposante()) != null) {
            bindingResult.rejectValue("codeComposante", "codeComposante.duplicate", "Ce code est déjà utilisé.");
        }
        if (composanteService.getComposanteByName(composante.getName()) != null) {
            bindingResult.rejectValue("name", "name.duplicate", "Ce nom est déjà utilisé.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("managers", userService.getManagersWithoutAssignComposante());
            return "composante_form";
        }
        composanteService.create(composante);
        if (managerIds != null) {
            composanteService.assignComposanteToManagers(composante, managerIds);
        }
        return "redirect:/";
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{codeComposante}/assign")
    public ModelAndView assignComposante(@PathVariable String codeComposante) {
        Composante composante = composanteService.getComposanteByCodeWithUsers(codeComposante);
        List<Integer> composanteManagersIds = composante.getUsers()
                .stream()
                .map(User::getId)
                .toList();
        List<User> managers = userService.getManagersWithoutAssignComposante();
        managers.addAll(composante.getUsers());

        ModelAndView modelAndView = new ModelAndView("assign_composante");
        modelAndView.addObject("composante", composante);
        modelAndView.addObject("managers", managers);
        modelAndView.addObject("composanteManagersIds", composanteManagersIds);
        return modelAndView;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{codeComposante}/assign/add")
    public String addManager(@PathVariable String codeComposante,
                             @RequestParam Integer managerId) {
        Composante composante = composanteService.getComposanteByCodeComposante(codeComposante);
        User manager = userService.getUserById(managerId);
        composanteService.assignComposanteToManager(composante, manager);
        return "redirect:/composante/" + codeComposante + "/assign";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/{codeComposante}/assign/remove")
    public String removeManager(@PathVariable String codeComposante,
                                @RequestParam Integer managerId) {
        Composante composante = composanteService.getComposanteByCodeComposante(codeComposante);
        User manager = userService.getUserById(managerId);
        composanteService.removeManager(composante, manager);
        return "redirect:/composante/" + codeComposante + "/assign";
    }
}
