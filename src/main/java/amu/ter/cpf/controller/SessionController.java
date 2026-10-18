package amu.ter.cpf.controller;

import amu.ter.cpf.model.entity.Action;
import amu.ter.cpf.model.entity.Session;
import amu.ter.cpf.model.referential.Guarantee;
import amu.ter.cpf.model.referential.RecruitmentStatus;
import amu.ter.cpf.service.SessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/session")
@RequiredArgsConstructor
public class SessionController {

    private final SessionService sessionService;

    private void addReferential(Model model) {
      model.addAttribute("recruitmentStatuses", RecruitmentStatus.values());
      model.addAttribute("guarantees", Guarantee.values());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/action/{codeAction}")
    public String listSessions(@PathVariable String codeAction, Model model) {
      try {
        model.addAttribute("action", sessionService.getAction(codeAction));
        model.addAttribute("sessions", sessionService.getSessionsByAction(codeAction));
      } catch (IllegalArgumentException e) {
        return "redirect:/formation";
      }
      return "session_list";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/create")
    public String showCreateForm(@RequestParam String codeAction, Model model) {
      Session session;

      try {
        session = sessionService.prepareNewSession(codeAction);
      } catch (IllegalArgumentException e) {
        return "redirect:/formation";
      }

      model.addAttribute("session", session);
      model.addAttribute("action", session.getAction());
      model.addAttribute("isEdit", false);

      addReferential(model);

      return "session_form";
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/create")
    public String createSession(@RequestParam String codeAction,
                                @Valid @ModelAttribute Session session,
                                BindingResult result,
                                Model model) {

      Action action = sessionService.getAction(codeAction);

      if (result.hasErrors()) {
        model.addAttribute("action", action);
        model.addAttribute("isEdit", false);
        addReferential(model);
        return "session_form";
      }

      sessionService.createSession(codeAction, session);

      return "redirect:/session/action/" + codeAction;
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @GetMapping("/{codeSession}/edit")
    public String showEditForm(@PathVariable String codeSession,
                               @RequestParam String codeAction,
                               Model model) {
      Session session = sessionService.getByCode(codeSession);
      model.addAttribute("session", session);
      model.addAttribute("action", sessionService.getAction(codeAction));
      model.addAttribute("isEdit", true);

        addReferential(model);
      return "session_form";
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    @PostMapping("/{codeSession}/edit")
    public String saveEdit(@PathVariable String codeSession,
                           @RequestParam String codeAction,
                           @Valid @ModelAttribute Session session,
                           BindingResult result,
                           Model model) {
      if (result.hasErrors()) {
        model.addAttribute("action", sessionService.getAction(codeAction));
        model.addAttribute("isEdit", true);
        addReferential(model);
        return "session_form";
      }
      sessionService.saveSession(codeSession, session);
      return "redirect:/session/action/" + codeAction;
    }
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @PostMapping("/{codeSession}/delete")
    public String delete(@PathVariable String codeSession){
      sessionService.deleteSession(codeSession);
      return "redirect:/session/action/" + codeSession;
    }

}






