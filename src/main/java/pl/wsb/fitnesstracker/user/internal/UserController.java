package pl.wsb.fitnesstracker.user.internal;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import pl.wsb.fitnesstracker.user.api.UserDto;
import org.springframework.http.HttpStatus;
import pl.wsb.fitnesstracker.user.api.User;
import pl.wsb.fitnesstracker.user.api.UserSummaryDto;

import java.time.LocalDate;
import java.util.List;

/**
 * UserController is responsible for handling HTTP requests related to user operations.
 * It provides endpoints for retrieving and creating users.
 */
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
class UserController {

    private final UserServiceImpl userService;

    private final UserMapper userMapper;

   /* @PostMapping
    public UserDto addUser(@RequestBody UserDto userDto) throws InterruptedException {

        // TODO: Implement the method to add a new user.
        //  You can use the @RequestBody annotation to map the request body to the UserDto object.

        return null;
    }*/
/////////////////

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto addUser(@RequestBody UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        User saved = userService.createUser(user);
        return userMapper.toDto(saved);
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @GetMapping("/simple")
    public List<UserSummaryDto> getSimpleUsers() {
        return userService.findAllUsers()
                .stream()
                .map(userMapper::toSummaryDto)
                .toList();
    }


    @GetMapping("/{id}")
    public UserDto getById(@PathVariable Long id) {
        User user = userService.getUser(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return userMapper.toDto(user);
    }


    @GetMapping("/email")
    public List<UserDto> getByEmail(@RequestParam String email) {
        return List.of(
                userService.getUserByEmail(email)
                        .map(userMapper::toDto)
                        .orElseThrow(() -> new RuntimeException("User not found"))
        );
    }


    @GetMapping("/older/{time}")
    public List<UserDto> getOlderThan(@PathVariable LocalDate time) {
        return userService.findAllUsers()
                .stream()
                .filter(u -> u.getBirthdate().isBefore(time))
                .map(userMapper::toDto)
                .toList();
    }


    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long userId) {
        userService.deleteUser(userId);
    }

    @PutMapping("/{userId}")
    public void update(@PathVariable Long userId,
                       @RequestBody UserDto dto) {

        User user = userService.getUser(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(dto.firstName());
        user.setLastName(dto.lastName());
        user.setBirthdate(dto.birthdate());
        user.setEmail(dto.email());

        userService.createUser(user);
    }


}