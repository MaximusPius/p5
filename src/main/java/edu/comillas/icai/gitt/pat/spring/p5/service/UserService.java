package edu.comillas.icai.gitt.pat.spring.p5.service;

import edu.comillas.icai.gitt.pat.spring.p5.entity.AppUser;
import edu.comillas.icai.gitt.pat.spring.p5.entity.Token;
import edu.comillas.icai.gitt.pat.spring.p5.model.ProfileRequest;
import edu.comillas.icai.gitt.pat.spring.p5.model.ProfileResponse;
import edu.comillas.icai.gitt.pat.spring.p5.model.RegisterRequest;
import edu.comillas.icai.gitt.pat.spring.p5.repository.TokenRepository;
import edu.comillas.icai.gitt.pat.spring.p5.repository.AppUserRepository;
import edu.comillas.icai.gitt.pat.spring.p5.util.Hashing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements UserServiceInterface {

    @Autowired
    AppUserRepository appUserRepository;

    @Autowired
    TokenRepository tokenRepository;

    @Autowired
    Hashing hashing;

    @Override
    public Token login(String email, String password) {
        Optional<AppUser> optionalUser = appUserRepository.findByEmail(email);
        if (optionalUser.isEmpty()) return null;

        AppUser appUser = optionalUser.get();
        if (!hashing.compare(appUser.password, password)) return null;

        Optional<Token> existingToken = tokenRepository.findByAppUser(appUser);
        if (existingToken.isPresent()) return existingToken.get();

        Token token = new Token();
        token.appUser = appUser;
        return tokenRepository.save(token);
    }

    @Override
    public AppUser authentication(String tokenId) {
        return tokenRepository.findById(tokenId)
                .map(token -> token.appUser)
                .orElse(null);
    }

    @Override
    public ProfileResponse profile(AppUser appUser) {
        return new ProfileResponse(appUser.email, appUser.name, appUser.role);
    }

    @Override
    public ProfileResponse profile(AppUser appUser, ProfileRequest profile) {
        if (profile.name() != null) appUser.name = profile.name();
        if (profile.role() != null) appUser.role = profile.role();
        appUserRepository.save(appUser);
        return profile(appUser);
    }

    @Override
    public ProfileResponse profile(RegisterRequest register) {
        AppUser appUser = new AppUser();
        appUser.name = register.name();
        appUser.email = register.email();
        appUser.password = hashing.hash(register.password());
        appUser.role = register.role();
        appUser = appUserRepository.save(appUser);
        return profile(appUser);
    }

    @Override
    public void logout(String tokenId) {
        tokenRepository.deleteById(tokenId);
    }

    @Override
    public void delete(AppUser appUser) {
        appUserRepository.delete(appUser);
    }
}
