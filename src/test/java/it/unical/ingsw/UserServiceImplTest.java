package it.unical.ingsw;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {


    private UserService userService;
    @Mock
    private SecurityService securityService;

    @Mock
    private UserDao userDao;
    @Mock
    private User user;

    @BeforeEach
    public void setUp() {
        userService = new UserServiceImpl(userDao, securityService);
    }



    @Test
    public void userShouldGetNewPassword() throws Exception {
        String fakePassword = "Password1234";
        String hashedFakePassword = "Hashed Password1234";
        when(user.getPassword()).thenReturn(fakePassword);
        when(securityService.hash(fakePassword)).thenReturn(hashedFakePassword);
        when(userDao.updateUser(any())).thenReturn(user);
        User userReturn = userService.assignPassword(user);

        verify(securityService).hash(fakePassword);
        verify(user).setPassword(hashedFakePassword);
        verify(userDao).updateUser(user);

        assertEquals(user, userReturn);
    }

    @Test
    public void shouldNotProceedFurtherWhenUsersGetPasswordThrowsException() {
        when(user.getPassword()).thenThrow(RuntimeException.class);

        Exception ex = assertThrows(RuntimeException.class, () -> {
            userService.assignPassword(user);
        });

        assertNotNull(ex);
        verify(user, times(1)).getPassword();
        verifyNoMoreInteractions(user);
        verifyNoInteractions(userDao);
    }

    @Test
    public void shouldNotProceedFurtherWhenSecurityServiceThrowsException() throws Exception {
        when(securityService.hash(anyString())).thenThrow(Exception.class);

        Exception ex = assertThrows(Exception.class, () -> {
            securityService.hash(anyString());
        });

        assertNotNull(ex);
        verifyNoInteractions(user);
        verifyNoInteractions(userDao);
    }

    @Test
    public void shouldNotProceedFurtherWhenUsersSetPasswordThrowsException() throws Exception {
        when(user.getPassword()).thenReturn("password");
        when(securityService.hash("password")).thenReturn("hashed password");
        doThrow(IllegalArgumentException.class).when(user).setPassword("hashed password");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.assignPassword(user);
        });

        assertNotNull(ex);
        verify(user).getPassword();
        verify(securityService).hash(anyString());
        verify(user).setPassword(anyString());
        verifyNoInteractions(userDao);
    }

    @Test
    public void shouldNotUpdateUserDaoWhenExceptionIsThrownOnUpdateUser() throws Exception {

        doThrow(RuntimeException.class).when(userDao).updateUser(any());

        Exception ex = assertThrows(RuntimeException.class, () -> {
            userDao.updateUser(any());
        });

        assertNotNull(ex);
        verify(userDao, times(1)).updateUser(any());
    }

    @Test
    public void shouldThrowNullPointerExceptionWhenNullIsPassedAsParameter() {

        Exception ex = assertThrows(NullPointerException.class, () -> {
            userService.assignPassword(null);
        });

        assertTrue(ex instanceof NullPointerException);
        verifyNoInteractions(user);
        verifyNoInteractions(securityService);
        verifyNoInteractions(userDao);
    }

    // --- POSSIBLE OTHER TESTS ---
    @Test
    public void shouldHandleExceptionInSetPassword() throws Exception {
        when(user.getPassword()).thenReturn("password");
        when(securityService.hash("password")).thenReturn("hashed password");
        doThrow(IllegalArgumentException.class).when(user).setPassword("hashed password");

        Exception ex = assertThrows(IllegalArgumentException.class, () -> {
            userService.assignPassword(user);
        });

        assertNotNull(ex);
        verify(user).getPassword();
        verify(securityService).hash(anyString());
        verify(user).setPassword(anyString());
        verifyNoInteractions(userDao);
    }

    @Test
    public void shouldHandleExceptionInUpdateUser() throws Exception {
        doThrow(RuntimeException.class).when(userDao).updateUser(any());

        Exception ex = assertThrows(RuntimeException.class, () -> {
            userService.assignPassword(user);
        });

        assertNotNull(ex);
        verify(userDao, times(1)).updateUser(user);
    }

    @Test
    public void shouldHandleNullUserInAssignPassword() {
        Exception ex = assertThrows(NullPointerException.class, () -> {
            userService.assignPassword(null);
        });

        assertTrue(ex instanceof NullPointerException);
        verifyNoInteractions(user);
        verifyNoInteractions(securityService);
        verifyNoInteractions(userDao);
    }

    @Test
    public void shouldReturnUserEmailIfExists() {
        String username = "existingUser";
        when(userDao.findByUsername(username)).thenReturn(Optional.of(user));
        String userEmail = userService.findUserEmail(username);

        assertEquals(user.getEmail(), userEmail);
        verify(userDao).findByUsername(username);
    }

    @Test
    public void shouldReturnNullForNonexistentUser() {
        String username = "nonexistentUser";
        when(userDao.findByUsername(username)).thenReturn(Optional.empty());
        String userEmail = userService.findUserEmail(username);

        assertNull(userEmail);
        verify(userDao).findByUsername(username);
    }

    @Test
    public void shouldReturnUserByUsernameIfExists() {
        String username = "existingUser";
        when(userDao.findByUsername(username)).thenReturn(Optional.of(user));
        Optional<User> retrievedUser = userService.findByUsername(username);

        assertTrue(retrievedUser.isPresent());
        assertEquals(user, retrievedUser.get());
        verify(userDao).findByUsername(username);
    }

    @Test
    public void shouldReturnEmptyOptionalForNonexistentUser() {
        String username = "nonexistentUser";
        when(userDao.findByUsername(username)).thenReturn(Optional.empty());
        Optional<User> retrievedUser = userService.findByUsername(username);

        assertTrue(retrievedUser.isEmpty());
        verify(userDao).findByUsername(username);
    }
}
