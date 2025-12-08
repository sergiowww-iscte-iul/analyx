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

import java.util.function.Supplier;

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

	public UserDTO getUserByName(String username) {
		return userRepository.findByName(username)
				.map(u -> UserDTO.builder()
						.id(u.getId())
						.name(u.getName())
						.email(u.getEmail())
						.build())
				.orElseThrow(throwBusinessException(username));
	}

	private static Supplier<BusinessException> throwBusinessException(String username) {
		return () -> new BusinessException("User %s not found".formatted(username));
	}

	@Transactional
	public void updateUser(UserDTO userDTO, String username) {
		AppUser appUser = userRepository.findByName(username)
				.orElseThrow(throwBusinessException(username));

		appUser.setName(userDTO.getName());
		appUser.setEmail(userDTO.getEmail());
		appUser.setPassword(passwordEncoder.encode(userDTO.getPassword()));

		userRepository.save(appUser);
	}
}
