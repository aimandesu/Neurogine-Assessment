package org.example.namia.supportdeskspringboot.service;

import lombok.RequiredArgsConstructor;
import org.example.namia.supportdeskspringboot.model.entity.Agent;
import org.example.namia.supportdeskspringboot.model.entity.User;
import org.example.namia.supportdeskspringboot.model.enums.Roles;
import org.example.namia.supportdeskspringboot.repository.AgentRepository;
import org.example.namia.supportdeskspringboot.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    private final UserRepository userRepository;
    private final AgentRepository agentRepository;

    @Override
    public User Create(User entity) {

        User user = userRepository.save(entity);

        if (user.role == Roles.Agent)
        {
            Agent agent = new Agent();
            agent.setEmail(user.getEmail());
            agent.setUser(user);

            agentRepository.save(agent);
        }

        return user;
    }

    @Override
    public Optional<User> Get(UUID Id) {
        return Optional.empty();
    }

    @Override
    public List<User> GetAll() {
        return List.of();
    }

    @Override
    public User Update(User entity) {
        return null;
    }

    @Override
    public void Delete(UUID Id) {

    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

}
