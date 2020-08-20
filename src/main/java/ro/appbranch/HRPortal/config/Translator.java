package ro.appbranch.HRPortal.config;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

@Component
public class Translator {
    private final MessageSourceAccessor accessor;

    public Translator(MessageSource messageSource) {
        this.accessor = new MessageSourceAccessor(messageSource);
    }

    public String translate(String code) {
        try {
            return accessor.getMessage(code, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException ignored) {
        }

        return code;
    }
}