package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import java.util.List;
import java.util.stream.Collectors;


@AllArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll()
                .stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) {
        return UserMapper.toUserDto(userService.get(userId));
    }

    @PostMapping
    public UserDto add(@RequestBody UserDto userDto) {
        return UserMapper.toUserDto(userService.add(UserMapper.toUser(userDto)));
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @RequestBody UserDto userDto) {
        userDto.setId(userId);
        return UserMapper.toUserDto(userService.update(userId, UserMapper.toUser(userDto)));
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        userService.delete(userId);
    }
}
