package br.com.fiap.mototrack.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE)           // ⬅️ garante prioridade
@ControllerAdvice(annotations = Controller.class)
public class UiExceptionAdvice {

    @ExceptionHandler({ AuthorizationDeniedException.class, AccessDeniedException.class })
    public Object handleAccessDenied(Exception ex,
                                     HttpServletRequest req,
                                     HttpServletResponse res) throws Exception {
        String accept = String.valueOf(req.getHeader("Accept")).toLowerCase();
        boolean wantsHtml = accept.contains("text/html");
        res.setStatus(HttpStatus.FORBIDDEN.value());
        if (wantsHtml) {
            req.getSession().setAttribute("FLASH_MSG_ERRO","Você não tem permissão para realizar esta ação.");
            return "erro/acesso-negado"; // templates/erro/acesso-negado.html
        }
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write("{\"message\":\"Access Denied\"}");
        return null;
    }
}
