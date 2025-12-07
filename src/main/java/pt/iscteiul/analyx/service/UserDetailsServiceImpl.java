package pt.iscteiul.analyx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pt.iscteiul.analyx.entity.AppUser;
import pt.iscteiul.analyx.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	public static final String DEFAULT_ROLE = "DEV-USER";
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		AppUser user = userRepository.findByName(username)
				.orElseThrow(() -> new UsernameNotFoundException(username));
		return User.builder()
				.username(username)
				.password(user.getPassword())
				.disabled(false)
				.roles(DEFAULT_ROLE)
				.build();
	}
}
