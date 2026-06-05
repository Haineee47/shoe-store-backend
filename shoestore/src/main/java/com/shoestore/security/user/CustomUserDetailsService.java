package com.shoestore.security.user;

import com.shoestore.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 🌟 TỐI ƯU THEO ĐỀ XUẤT CỦA BẠN: Nạp sẵn đồ thị quyền để Filter dùng mượt mà, không bị Lazy Load
        return userRepository.findWithRolesAndPermissionsByEmail(email)
                .map(UserPrincipal::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
    }
}