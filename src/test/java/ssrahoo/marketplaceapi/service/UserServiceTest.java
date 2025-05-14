package ssrahoo.marketplaceapi.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ssrahoo.marketplaceapi.dto.UserRegistrationDto;
import ssrahoo.marketplaceapi.dto.UserUpdateDto;
import ssrahoo.marketplaceapi.entity.User;
import ssrahoo.marketplaceapi.repository.BuyerRepository;
import ssrahoo.marketplaceapi.repository.SellerRepository;
import ssrahoo.marketplaceapi.repository.UserRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    UserRepository userRepository;

    @Mock
    BuyerRepository buyerRepository;

    @Mock
    SellerRepository sellerRepository;

    @InjectMocks
    UserService userService;

    @Captor
    ArgumentCaptor<UUID> uuidArgumentCaptor;

    @Captor
    ArgumentCaptor<User> userArgumentCaptor;

    // naming convention:
    // class <method> {
    //     void should<expected>_when<condition>(){
    //     ...
    // }

    @Nested
    class save{
        @Test
        @DisplayName("METHOD save SHOULD save user WHEN required data is present.")
        void shouldSaveUser_whenRequiredDataIsPresent(){
            // arrange
            var user = new User(
                    "x",
                    "email",
                    "password",
                    0.0,
                    Instant.now(),
                    null
            );

            var userRegistrationDto = new UserRegistrationDto(
                    "y",
                    "email",
                    "password",
                    0.0
            );

            UUID uuid = UUID.randomUUID();
            user.setUserId(uuid);

            doReturn(user).when(userRepository).save(userArgumentCaptor.capture());

            // act
            var savedId = userService.save(userRegistrationDto);
            var capturedUser = userArgumentCaptor.getValue();

            // assert
            assertNotNull(savedId);
            assertEquals(uuid, savedId);
            assertEquals(userRegistrationDto.username(), capturedUser.getUsername());
            assertEquals(userRegistrationDto.email(), capturedUser.getEmail());
            assertEquals(userRegistrationDto.password(), capturedUser.getPassword());
            assertEquals(userRegistrationDto.wallet(), capturedUser.getWallet());
            verify(userRepository, times(1)).save(capturedUser);

        }

        @Test
        @DisplayName("METHOD save SHOULD throw exception WHEN exception occurs.")
        void shouldThrowException_whenExceptionOccurs(){
            // arrange
            var userRegistrationDto = new UserRegistrationDto(
                    "y",
                    "email",
                    "password",
                    0.0
            );

            doThrow(new RuntimeException()).when(userRepository).save(userArgumentCaptor.capture());

            // act & assert
            assertThrows(RuntimeException.class, () -> userService.save(userRegistrationDto));
        }
    }

    @Nested
    class findAll{
        @Test
        @DisplayName("METHOD findAll SHOULD return all saved users")
        void shouldReturnAllSavedUsers() {
            // arrange
            var users = List.of(new User());
            doReturn(users).when(userRepository).findAll();

            // act
            var result = userService.findAll();

            // assert
            assertNotNull(result);
            assertTrue(!result.isEmpty());
            assertEquals(users.size(), result.size());
        }

        @Test
        @DisplayName("METHOD findAll SHOULD throw exception WHEN exception occurs.")
        void shouldThrowException_whenExceptionOccurs(){
            // arrange
            doThrow(new RuntimeException()).when(userRepository).findAll();

            // act & assert
            assertThrows(RuntimeException.class, () -> userService.findAll());
        }
    }

    @Nested
    class findById{
        @DisplayName("METHOD findById SHOULD return user WHEN optional is present")
        @Test
        void shouldReturnUser_whenOptionalIsPresent() {
            // arrange
            var id = UUID.randomUUID();
            var user = new User(
                    "x",
                    "email",
                    "password",
                    0.0,
                    Instant.now(),
                    null
            );

            doReturn(Optional.of(user)).when(userRepository).findById(uuidArgumentCaptor.capture());

            // act
            var result = userService.findById(id);

            // assert
            assertTrue(result.isPresent());
            assertEquals(id, uuidArgumentCaptor.getValue());
            verify(userRepository, times(1)).findById(uuidArgumentCaptor.getValue());
        }

        @DisplayName("METHOD findById SHOULD return user WHEN optional is empty")
        @Test
        void shouldReturnUser_whenOptionalIsEmpty() {
            // arrange
            var id = UUID.randomUUID();
            doReturn(Optional.empty()).when(userRepository).findById(uuidArgumentCaptor.capture());

            // act
            var result = userService.findById(id);

            // assert
            assertTrue(result.isEmpty());
            assertEquals(id, uuidArgumentCaptor.getValue());
            verify(userRepository, times(1)).findById(uuidArgumentCaptor.getValue());
        }

        @Test
        @DisplayName("METHOD findById SHOULD throw exception WHEN exception occurs.")
        void shouldThrowException_whenExceptionOccurs(){
            // arrange
            var id = UUID.randomUUID();
            doThrow(new RuntimeException()).when(userRepository).findById(uuidArgumentCaptor.capture());

            // act & assert
            assertThrows(RuntimeException.class, () -> userService.findById(id));
        }
    }

    @Nested
    class updateById{
        @DisplayName("METHOD updateById SHOULD update user WHEN it exists and required data is present")
        @Test
        void shouldUpdateUser_whenItExistsAndRequiredDataIsPresent() {
            // arrange
            UUID id = UUID.randomUUID();
            User user = new User(
                    "x",
                    "email",
                    "password",
                    0.0,
                    Instant.now(),
                    null
            );

            UserUpdateDto userUpdateDto = new UserUpdateDto(
                    "y",
                    "password",
                    0.0
            );

            doReturn(Optional.of(user)).when(userRepository).findById(uuidArgumentCaptor.capture());
            doReturn(user).when(userRepository).save(userArgumentCaptor.capture());

            // act
            userService.updateById(id, userUpdateDto);

            // assert
            assertEquals(id, uuidArgumentCaptor.getValue());
            assertEquals(userUpdateDto.username(), userArgumentCaptor.getValue().getUsername());
            assertEquals(userUpdateDto.password(), userArgumentCaptor.getValue().getPassword());
            assertEquals(userUpdateDto.wallet(), userArgumentCaptor.getValue().getWallet());

            verify(userRepository, times(1)).findById(uuidArgumentCaptor.getValue());
            verify(userRepository, times(1)).save(userArgumentCaptor.getValue());

        }

        @DisplayName("METHOD updateById SHOULD not update user WHEN it does not exist")
        @Test
        void shouldNotUpdateUser_whenItDoesNotExist() {
            // arrange
            UUID id = UUID.randomUUID();
            UserUpdateDto userUpdateDto = new UserUpdateDto(
                    "y",
                    "password",
                    0.0
            );

            doReturn(Optional.empty()).when(userRepository).findById(uuidArgumentCaptor.capture());

            // act
            userService.updateById(id, userUpdateDto);

            // assert
            assertEquals(id, uuidArgumentCaptor.getValue());

            verify(userRepository, times(1)).findById(uuidArgumentCaptor.getValue());
            verify(userRepository, times(0)).save(any());

        }
    }

    @Nested
    class deleteById{

        @DisplayName("METHOD deleteById SHOULD delete user WHEN it exists")
        @Test
        void shouldDeleteUser_whenItExists() {
            // arrange
            var id = UUID.randomUUID();

            doReturn(true).when(userRepository).existsById(uuidArgumentCaptor.capture());
            doNothing().when(userRepository).deleteById(uuidArgumentCaptor.capture());

            // act
            userService.deleteById(id);
            var capturedValues = uuidArgumentCaptor.getAllValues();

            // assert
            assertEquals(id, capturedValues.get(0));
            assertEquals(id, capturedValues.get(1));

            verify(userRepository, times(1)).existsById(capturedValues.get(0));
            verify(userRepository, times(1)).deleteById(capturedValues.get(1));
        }

        @DisplayName("METHOD deleteById SHOULD not delete user WHEN it does not exist")
        @Test
        void shouldNotDeleteUser_whenItDoesNotExist() {
            // arrange
            var id = UUID.randomUUID();

            doReturn(false).when(userRepository).existsById(uuidArgumentCaptor.capture());

            // act
            userService.deleteById(id);
            var capturedValues = uuidArgumentCaptor.getAllValues();

            // assert
            assertEquals(id, capturedValues.get(0));

            verify(userRepository, times(1)).existsById(capturedValues.get(0));
            verify(userRepository, times(0)).deleteById(any());
        }

        @Test
        @DisplayName("METHOD deleteById SHOULD throw exception WHEN exception occurs.")
        void shouldThrowException_whenExceptionOccurs(){
            // arrange
            var id = UUID.randomUUID();
            doReturn(true).when(userRepository).existsById(uuidArgumentCaptor.capture());
            doThrow(new RuntimeException()).when(userRepository).deleteById(uuidArgumentCaptor.capture());

            // act & assert
            assertThrows(RuntimeException.class, () -> userService.deleteById(id));
        }

    }

}
