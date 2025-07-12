package store.shportfolio.user.usecase.ports.output.security;

import store.shportfolio.common.domain.valueobject.Token;

public interface JwtPort {
    Token createSignupTemporaryToken(String email);

    Token createLoginToken(String email, String username, String userId);

    String getEmailFromToken(Token token);

    String getUserIdByToken(Token token);

    String getUserIdFromToken(String userId, String token);

    String extractClaimFromToken(Token token, String claimType);
}
