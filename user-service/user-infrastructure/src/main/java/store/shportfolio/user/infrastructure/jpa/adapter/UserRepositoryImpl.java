package store.shportfolio.user.infrastructure.jpa.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import store.shportfolio.user.application.ports.output.repository.UserRepository;
import store.shportfolio.user.domain.entity.User;
import store.shportfolio.user.infrastructure.jpa.entity.UserEntity;
import store.shportfolio.user.infrastructure.jpa.mapper.UserEntityDataMapper;
import store.shportfolio.user.infrastructure.jpa.repository.UserJpaRepository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class UserRepositoryImpl implements UserRepository {


    private final UserJpaRepository userJpaRepository;
    private final UserEntityDataMapper userEntityDataMapper;

    public UserRepositoryImpl(UserJpaRepository userJpaRepository, UserEntityDataMapper userEntityDataMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userEntityDataMapper = userEntityDataMapper;
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = userEntityDataMapper.userToUserEntity(user);
        UserEntity savedUserEntity = userJpaRepository.save(userEntity);
        return userEntityDataMapper.userEntityToUser(savedUserEntity);
    }

    @Override
    public Optional<User> findById(UUID userId) {
        return userJpaRepository.findById(userId.toString())
                .map(userEntityDataMapper::userEntityToUser);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(userEntityDataMapper::userEntityToUser);
    }

    @Override
    public void remove(UUID userId) {
        userJpaRepository.deleteByUserId(userId.toString());
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .map(userEntityDataMapper::userEntityToUser);
    }
}
