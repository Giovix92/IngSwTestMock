package it.unical.ingsw;

import okhttp3.OkHttpClient;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Optional;

public class Main {

    public static void main(String[] args) {

        UserDao userDao = new UserDao() {
            @Override
            public User updateUser(User user) {
                System.out.println("Fake user dao");
                return user;
            }

            @Override
            public Optional<User> findByUsername(String username) {
                return Optional.empty();
            }
        };
        SecurityService secService = new SecurityService() {
            @Override
            public String hash(String password) throws Exception {
                String md5Hex = DigestUtils
                        .md5Hex(password).toUpperCase();
                System.out.println("Hashed password: " + md5Hex);
                return md5Hex;
            }
        };
        UserService userService = new UserServiceImpl(userDao, secService);
        User user = new User("Name", "password", "user@unical.it");
        try {
            userService.assignPassword(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
