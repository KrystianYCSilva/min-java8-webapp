package br.gov.inep.censo.web.zk;

import br.gov.inep.censo.util.ValidationUtils;
import org.zkoss.zk.ui.Execution;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.select.SelectorComposer;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.sql.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Base de composers ZK com utilitarios comuns para navegacao, flash e parsing.
 */
public abstract class AbstractBaseComposer extends SelectorComposer<Window> {

    private static final long serialVersionUID = 1L;

    protected HttpServletRequest currentRequest() {
        Execution execution = Executions.getCurrent();
        if (execution == null) {
            return null;
        }
        Object nativeRequest = execution.getNativeRequest();
        if (!(nativeRequest instanceof HttpServletRequest)) {
            return null;
        }
        return (HttpServletRequest) nativeRequest;
    }

    protected HttpSession currentSession(boolean create) {
        HttpServletRequest request = currentRequest();
        if (request == null) {
            return null;
        }
        return request.getSession(create);
    }

    protected void redirect(String relativePath) {
        Executions.sendRedirect(relativePath);
    }

    protected void goShell(String view) {
        redirect("/app/menu.zul?view=" + view);
    }

    protected void goShell(String view, int pagina) {
        redirect("/app/menu.zul?view=" + view + "&pagina=" + pagina);
    }

    protected void openSub(String view, String sub) {
        redirect("/app/menu.zul?view=" + view + "&sub=" + sub);
    }

    protected void openSub(String view, String sub, Long id) {
        if (id == null) {
            openSub(view, sub);
            return;
        }
        redirect("/app/menu.zul?view=" + view + "&sub=" + sub + "&id=" + id);
    }

    protected void putFlash(String key, String value) {
        HttpSession session = currentSession(true);
        if (session != null) {
            session.setAttribute(key, value);
        }
    }

    protected String consumeFlash(String key) {
        HttpSession session = currentSession(false);
        if (session == null) {
            return null;
        }
        Object value = session.getAttribute(key);
        if (value == null) {
            return null;
        }
        session.removeAttribute(key);
        return String.valueOf(value);
    }

    protected String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String cleaned = value.trim();
        return cleaned.length() == 0 ? null : cleaned;
    }

    protected String trimToEmpty(String value) {
        if (value == null) {
            return "";
        }
        return value.trim();
    }

    protected Long parseLongOrNull(String value) {
        String cleaned = trimToEmpty(value);
        if (!ValidationUtils.isNumeric(cleaned)) {
            return null;
        }
        return Long.valueOf(Long.parseLong(cleaned));
    }

    protected Integer parseIntegerOrNull(String value) {
        String cleaned = trimToEmpty(value);
        if (!ValidationUtils.isNumeric(cleaned)) {
            return null;
        }
        return Integer.valueOf(Integer.parseInt(cleaned));
    }

    protected int parseIntOrDefault(String value, int defaultValue) {
        String cleaned = trimToEmpty(value);
        if (!ValidationUtils.isNumeric(cleaned)) {
            return defaultValue;
        }
        return Integer.parseInt(cleaned);
    }

    protected Date toSqlDate(java.util.Date value) {
        if (value == null) {
            return null;
        }
        return new Date(value.getTime());
    }

    protected Map<Long, String> mapCamposComplementares(Map<Long, Textbox> campos) {
        Map<Long, String> valores = new LinkedHashMap<Long, String>();
        if (campos == null) {
            return valores;
        }
        for (Map.Entry<Long, Textbox> entry : campos.entrySet()) {
            Textbox textbox = entry.getValue();
            if (textbox == null) {
                continue;
            }
            String value = trimToEmpty(textbox.getValue());
            if (value.length() == 0) {
                continue;
            }
            valores.put(entry.getKey(), value);
        }
        return valores;
    }

    protected long[] mapSelectedIds(Map<Long, Checkbox> checks) {
        if (checks == null || checks.isEmpty()) {
            return new long[0];
        }
        java.util.ArrayList<Long> ids = new java.util.ArrayList<Long>();
        for (Map.Entry<Long, Checkbox> entry : checks.entrySet()) {
            Checkbox checkbox = entry.getValue();
            if (checkbox != null && checkbox.isChecked()) {
                ids.add(entry.getKey());
            }
        }
        long[] result = new long[ids.size()];
        for (int i = 0; i < ids.size(); i++) {
            result[i] = ids.get(i).longValue();
        }
        return result;
    }
}
