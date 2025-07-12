package store.shportfolio.user.infrastructure.database.jpa.adapter;

import org.springframework.stereotype.Repository;
import store.shportfolio.user.domain.entity.User;
import store.shportfolio.user.infrastructure.database.jpa.entity.UserEntity;
import store.shportfolio.user.infrastructure.database.jpa.mapper.UserEntityDataAccessMapper;
import store.shportfolio.user.infrastructure.database.jpa.repository.UserJpaRepository;
import store.shportfolio.user.usecase.ports.output.repository.UserRepository;

import java.util.Optional;

@Repository
public class UserRepositoryImpl implements UserRepository {


    private final UserJpaRepository userJpaRepository;
    private final UserEntityDataAccessMapper userEntityDataAccessMapper;

    public UserRepositoryImpl(UserJpaRepository userJpaRepository, UserEntityDataAccessMapper userEntityDataAccessMapper) {
        this.userJpaRepository = userJpaRepository;
        this.userEntityDataAccessMapper = userEntityDataAccessMapper;
    }

    @Override
    public User save(User user) {
        UserEntity userEntity = userEntityDataAccessMapper.userToUserEntity(user);
        UserEntity savedUserEntity = userJpaRepository.save(userEntity);
        return userEntityDataAccessMapper.userEntityToUser(savedUserEntity);
    }

    @Override
    public Optional<User> findById(String userId) {
        return userJpaRepository.findById(userId.toString())
                .map(userEntityDataAccessMapper::userEntityToUser);
    }


    @Override
    public Optional<User> findByEmail(String email) {
        return userJpaRepository.findByEmail(email).map(userEntityDataAccessMapper::userEntityToUser);
    }

    @Override
    public void remove(String userId) {
        userJpaRepository.deleteByUserId(userId);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userJpaRepository.findByUsername(username)
                .map(userEntityDataAccessMapper::userEntityToUser);
    }
}
