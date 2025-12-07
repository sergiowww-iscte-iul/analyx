package pt.iscteiul.analyx.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.iscteiul.analyx.dto.UserDTO;
import pt.iscteiul.analyx.entity.AppUser;
import pt.iscteiul.analyx.exception.BusinessException;
import pt.iscteiul.analyx.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	public static final String DEFAULT_ROLE = "DEV-USER";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

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

	@Transactional
	public void createUser(UserDTO userDTO) {
		if (userRepository.existsByEmailOrName(userDTO.getEmail(), userDTO.getName())) {
			throw new BusinessException("User already exists");
		}
		AppUser appUser = new AppUser();
		appUser.setName(userDTO.getName());
		appUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));
		appUser.setEmail(userDTO.getEmail());
		userRepository.save(appUser);
	}
}
