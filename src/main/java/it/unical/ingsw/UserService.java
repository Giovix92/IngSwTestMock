package it.unical.ingsw;

import java.util.Optional;

public interface UserService {

    User assignPassword(User user) throws Exception;

    String findUserEmail(String username);

    Optional<User> findByUsername(String username);
}
