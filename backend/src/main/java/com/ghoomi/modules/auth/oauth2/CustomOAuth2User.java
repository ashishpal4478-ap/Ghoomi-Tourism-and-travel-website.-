package com.ghoomi.modules.auth.oauth2;

import com.ghoomi.modules.user.User;
import com.ghoomi.modules.user.UserProfile;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Custom OAuth2User implementation wrapping Ghoomi's User and UserProfile entities
 * alongside OAuth2 attributes supplied by Google.
 */
public class CustomOAuth2User implements OAuth2User {

    private final User user;
    private final UserProfile profile;
    private final Map<String, Object> attributes;

    public CustomOAuth2User(User user, UserProfile profile, Map<String, Object> attributes) {
        this.user = user;
        this.profile = profile;
        this.attributes = attributes;
    }

    public User getUser() {
        return user;
    }

    public UserProfile getProfile() {
        return profile;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getName() {
        return user.getEmail();
    }
}

