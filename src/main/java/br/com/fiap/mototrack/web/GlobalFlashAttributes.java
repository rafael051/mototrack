// v// pacote: br.com.fiap.mototrack.web
package br.com.fiap.mototrack.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

/**
 * Expõe mensagens "flash" da sessão para todas as views:
 * - FLASH_MSG_ERRO    -> msgErro
 * - FLASH_MSG_OK      -> msgSucesso
 * Remove da sessão após expor (one-shot).
 */
@ControllerAdvice
@Component
public class GlobalFlashAttributes {

    private static final String FLASH_ERR = "FLASH_MSG_ERRO";
    private static final String FLASH_OK  = "FLASH_MSG_OK";

    @ModelAttribute
    public void exposeFlash(HttpServletRequest req, Model model) {
        HttpSession session = req.getSession(false);
        if (session == null) return;

        popAttr(session, FLASH_ERR, "msgErro", model);
        popAttr(session, FLASH_OK,  "msgSucesso", model);
    }

    // Move um atributo da sessão para o Model e remove da sessão.
    private void popAttr(HttpSession session, String fromKey, String toKey, Model model) {
        Object val = session.getAttribute(fromKey);
        if (val != null) {
            model.addAttribute(toKey, val.toString());
            session.removeAttribute(fromKey);
        }
    }
}
