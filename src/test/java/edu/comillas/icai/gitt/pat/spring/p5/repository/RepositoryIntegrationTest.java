package edu.comillas.icai.gitt.pat.spring.p5.repository;

import edu.comillas.icai.gitt.pat.spring.p5.entity.AppUser;
import edu.comillas.icai.gitt.pat.spring.p5.entity.Token;
import edu.comillas.icai.gitt.pat.spring.p5.model.Role;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
class RepositoryIntegrationTest {
    @Autowired TokenRepository tokenRepository;
    @Autowired AppUserRepository appUserRepository;

    /**
     * TODO#9
     * Completa este test de integración para que verifique
     * que los repositorios TokenRepository y AppUserRepository guardan
     * los datos correctamente, y las consultas por AppToken y por email
     * definidas respectivamente en ellos retornan el token y usuario guardados.
     */
    @Test void saveTest() {
        AppUser user = new AppUser();
        user.name = "Test";
        user.email = "test@email.com";
        user.password = "pass123";
        user.role = Role.USER;
        appUserRepository.save(user);

        Token token = new Token();
        token.appUser = user;
        tokenRepository.save(token);

        AppUser userByEmail = appUserRepository.findByEmail("test@email.com").orElse(null);
        Token tokenByUser = tokenRepository.findByAppUser(user).orElse(null);

        Assertions.assertNotNull(userByEmail);
        Assertions.assertNotNull(tokenByUser);
        Assertions.assertEquals(userByEmail.id, user.id);
        Assertions.assertEquals(tokenByUser.appUser.id, user.id);
    }


    /**
     * TODO#10
     * Completa este test de integración para que verifique que
     * cuando se borra un usuario, automáticamente se borran sus tokens asociados.
     */
    @Test void deleteCascadeTest() {
        AppUser user = new AppUser();
        user.name = "Test";
        user.email = "test@email.com";
        user.password = "pass123";
        user.role = Role.USER;
        appUserRepository.save(user);

        Token token = new Token();
        token.appUser = user;
        tokenRepository.save(token);

        appUserRepository.delete(user);

        Assertions.assertEquals(0, appUserRepository.count());
        Assertions.assertEquals(0, tokenRepository.count());
    }

}