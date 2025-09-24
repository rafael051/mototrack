package br.com.fiap.mototrack.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.cache.SpringCacheBasedUserCache;

import java.time.Duration;

/**
 * ⚡ Configuração de Cache da aplicação (Caffeine).
 *
 * Objetivos:
 *  - Reduzir consultas repetidas ao banco (ex.: múltiplas buscas de usuário durante o login).
 *  - Oferecer um CacheManager reutilizável para qualquer @Cacheable da aplicação.
 *  - Expor um UserCache para o Spring Security (DaoAuthenticationProvider) reutilizar credenciais.
 *
 * Observações:
 *  - Requer a dependência no build.gradle:
 *        implementation 'com.github.ben-manes.caffeine:caffeine:3.1.8'
 *  - @EnableCaching ativa o suporte a anotações como @Cacheable/@CacheEvict.
 *  - O cache "usersByEmail" é pensado para UserDetails (login). Pode adicionar outros nomes conforme necessidade.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * ✅ CacheManager padrão da aplicação usando Caffeine.
     *
     * - Define o(s) nome(s) dos caches que usaremos (aqui: "usersByEmail").
     * - Configura política de expiração e tamanho máximo do cache.
     * - Esse CacheManager será utilizado por:
     *     a) @Cacheable/@CacheEvict na sua aplicação;
     *     b) O Spring Security, via bean UserCache (ver método abaixo).
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("usersByEmail");
        manager.setCaffeine(Caffeine.newBuilder()
                // Quantidade máxima de entradas em memória (ajuste conforme sua carga)
                .maximumSize(1_000)
                // Tempo de expiração após escrita (ex.: 10 minutos)
                .expireAfterWrite(Duration.ofMinutes(10))
                // (opcional) métricas internas; útil em diagnósticos
                .recordStats()
        );
        return manager;
    }

    /**
     * 🔐 UserCache para o Spring Security.
     *
     * - O DaoAuthenticationProvider pode reutilizar detalhes do usuário entre requisições,
     *   evitando bater no banco várias vezes.
     * - Aqui, conectamos o cache "usersByEmail" ao mecanismo de UserCache do Security.
     * - Para ativar no seu SecurityConfiguration, injete este bean no AuthenticationProvider:
     *
     *   var provider = new DaoAuthenticationProvider();
     *   provider.setUserDetailsService(userDetailsService);
     *   provider.setPasswordEncoder(passwordEncoder);
     *   provider.setUserCache(userCache); // <—— usar este bean
     *
     * Dica: se preferir, você também pode anotar o método loadUserByUsername com @Cacheable,
     * sem usar UserCache. As duas abordagens funcionam; não precisa das duas ao mesmo tempo.
     */
    @Bean
    public UserCache userCache(CacheManager cacheManager) {
        // Garante que o cache "usersByEmail" existe no CacheManager
        if (cacheManager.getCache("usersByEmail") == null) {
            throw new IllegalStateException("""
                Cache 'usersByEmail' não está configurado no CacheManager.
                Verifique o bean cacheManager() acima.
            """);
        }
        return new SpringCacheBasedUserCache(cacheManager.getCache("usersByEmail"));
    }
}
