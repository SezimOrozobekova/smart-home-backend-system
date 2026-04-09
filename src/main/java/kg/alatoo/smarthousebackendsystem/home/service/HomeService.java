package kg.alatoo.smarthousebackendsystem.home.service;

import kg.alatoo.smarthousebackendsystem.home.entity.Home;
import kg.alatoo.smarthousebackendsystem.home.mapper.HomeMapper;
import kg.alatoo.smarthousebackendsystem.home.payload.request.CreateHomeRequest;
import kg.alatoo.smarthousebackendsystem.home.payload.response.HomeResponse;
import kg.alatoo.smarthousebackendsystem.home.repository.HomeRepository;
import kg.alatoo.smarthousebackendsystem.user.entity.User;
import kg.alatoo.smarthousebackendsystem.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HomeService {

    private final HomeRepository homeRepository;
    private final UserRepository userRepository;
    private final HomeMapper homeMapper;

    public List<HomeResponse> getAllHomes() {
        return homeRepository.findAll()
                .stream()
                .map(homeMapper::toResponse)
                .toList();
    }

    public List<HomeResponse> getHomesByOwner(UUID ownerId) {
        return homeRepository.findAllByOwnerId(ownerId)
                .stream()
                .map(homeMapper::toResponse)
                .toList();
    }

    @Transactional
    public HomeResponse createHome(CreateHomeRequest request) {
        User owner = userRepository.findById(request.ownerId())
                .orElseThrow(() -> new RuntimeException("Owner not found"));

        Home home = new Home();
        home.setName(request.name());
        home.setAddress(request.address());
        home.setOwner(owner);

        Home savedHome = homeRepository.save(home);

        return homeMapper.toResponse(savedHome);
    }
}