package kg.alatoo.smarthousebackendsystem.user.service;

import kg.alatoo.smarthousebackendsystem.user.mapper.UserMapper;
import kg.alatoo.smarthousebackendsystem.user.payload.response.UserResponse;
import kg.alatoo.smarthousebackendsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

}