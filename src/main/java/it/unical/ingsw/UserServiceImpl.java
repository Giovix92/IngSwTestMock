package it.unical.ingsw;

import java.util.Optional;

public class UserServiceImpl implements UserService {

    private UserDao userDao;
    private SecurityService securityService;

    public UserServiceImpl(UserDao userDao, SecurityService securityService) {
        this.userDao = userDao;
        this.securityService = securityService;
    }

    public User assignPassword(User user) throws Exception {
        String passwordMd5 = securityService.hash(user.getPassword());
        user.setPassword(passwordMd5);
        return userDao.updateUser(user);
    }

    public String findUserEmail(String username) {
        Optional<User> user = userDao.findByUsername(username);
        if (user.isPresent()) {
            return user.get().getEmail();
        }
        return null;
    }

    public Optional<User> findByUsername(String username) {
        return userDao.findByUsername(username);
    }
}
