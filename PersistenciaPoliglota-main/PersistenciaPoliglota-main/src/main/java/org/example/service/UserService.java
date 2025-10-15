package org.example.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.example.repository.postgres.UserRepository;
import org.example.model.postgres.User;
import org.example.util.EncryptionUtil;
import org.example.util.DateUtil;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public List<User> listAll() {
		return userRepository.findAll();
	}

	public Optional<User> findById(Integer id) {
		return userRepository.findById(id);
	}

	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}

	@Transactional
	public User register(String fullName, String email, String plainPassword) {
		if (email == null || plainPassword == null) throw new IllegalArgumentException("email and password required");
		if (userRepository.findByEmail(email).isPresent()) {
			throw new IllegalArgumentException("email already registered");
		}
		String hash = EncryptionUtil.hashPassword(plainPassword);
		User u = new User();
		u.setFullName(fullName);
		u.setEmail(email);
		u.setPasswordHash(hash);
		u.setStatus("activo");
		u.setRegisteredAt(DateUtil.nowOffset());
		return userRepository.save(u);
	}

	public boolean authenticate(String email, String plainPassword) {
		return userRepository.findByEmail(email)
				.map(u -> EncryptionUtil.verify(plainPassword, u.getPasswordHash()))
				.orElse(false);
	}

	@Transactional
	public User changePassword(Integer userId, String newPlainPassword) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		user.setPasswordHash(EncryptionUtil.hashPassword(newPlainPassword));
		return userRepository.save(user);
	}

	@Transactional
	public User activate(Integer userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		user.setStatus("activo");
		return userRepository.save(user);
	}

	@Transactional
	public User deactivate(Integer userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
		user.setStatus("inactivo");
		return userRepository.save(user);
	}
}
