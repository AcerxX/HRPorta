package ro.appbranch.HRPortal.config;

import org.jtwig.environment.EnvironmentConfigurationBuilder;
import org.jtwig.hot.reloading.HotReloadingExtension;
import org.jtwig.spring.JtwigViewResolver;
import org.jtwig.spring.asset.SpringAssetExtension;
import org.jtwig.spring.asset.resolver.AssetResolver;
import org.jtwig.spring.asset.resolver.BaseAssetResolver;
import org.jtwig.spring.boot.config.JtwigViewResolverConfigurer;
import org.jtwig.spring.csrf.SpringCsrfExtension;
import org.jtwig.translate.spring.SpringTranslateExtension;
import org.jtwig.translate.spring.SpringTranslateExtensionConfiguration;
import org.jtwig.web.servlet.JtwigRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;

@Configuration
public class JtwigMvcConfiguration implements JtwigViewResolverConfigurer, WebMvcConfigurer {
    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;

    @Autowired
    public JtwigMvcConfiguration(MessageSource messageSource, LocaleResolver localeResolver) {
        this.messageSource = messageSource;
        this.localeResolver = localeResolver;
    }

    @Override
    public void configure(JtwigViewResolver viewResolver) {
        viewResolver.setSuffix(".twig");
        viewResolver.setRenderer(
                new JtwigRenderer(
                        EnvironmentConfigurationBuilder.configuration()
                                .extensions()
                                .add(
                                        new SpringTranslateExtension(
                                                SpringTranslateExtensionConfiguration
                                                        .builder(messageSource)
                                                        .withLocaleResolver(localeResolver)
                                                        .build()
                                        )
                                )
                                .add(new HotReloadingExtension())
                                .add(new SpringAssetExtension())
                                .add(new SpringCsrfExtension())
                                .and()
                                .build()
                )
        );
    }

    @Bean
    public AssetResolver assetResolver() {
        BaseAssetResolver assetResolver = new BaseAssetResolver();
        assetResolver.setPrefix("/assets");
        return assetResolver;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("assets/**")
                .addResourceLocations("classpath:assets/")
                .setCacheControl(CacheControl.maxAge(1, TimeUnit.DAYS));

        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/")
                .setCacheControl(CacheControl.maxAge(7, TimeUnit.DAYS));
    }
}
