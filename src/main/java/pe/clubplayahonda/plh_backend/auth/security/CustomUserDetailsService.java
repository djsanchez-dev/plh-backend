package pe.clubplayahonda.plh_backend.auth.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pe.clubplayahonda.plh_backend.auth.model.User;
import pe.clubplayahonda.plh_backend.auth.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public CustomUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmailIgnoreCase(username)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
		// Un propietario de la directiva obtiene también los permisos de BOARD
		// (los Authorities se recalculan en cada petición desde la base).
		String[] roles = (user.isBoardMember() && user.getRole() != pe.clubplayahonda.plh_backend.auth.model.UserRole.BOARD)
				? new String[] { user.getRole().name(), pe.clubplayahonda.plh_backend.auth.model.UserRole.BOARD.name() }
				: new String[] { user.getRole().name() };
		return org.springframework.security.core.userdetails.User
				.withUsername(user.getEmail())
				.password(user.getPasswordHash())
				.roles(roles)
				.disabled(!user.isEnabled())
				.build();
	}
}
