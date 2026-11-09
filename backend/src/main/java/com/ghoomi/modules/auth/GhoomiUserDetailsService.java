package com.ghoomi.modules.auth;

import com.ghoomi.modules.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Loads user details from the database for Spring Security authentication.
 * The "username" passed to this service is the user's email address.
 */
@Service
public class GhoomiUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public GhoomiUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email.trim().toLowerCase())
                .map(GhoomiUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("No account found for: " + email));
    }
}

