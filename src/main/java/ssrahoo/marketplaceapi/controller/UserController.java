package ssrahoo.marketplaceapi.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ssrahoo.marketplaceapi.dto.UserRegistrationDto;
import ssrahoo.marketplaceapi.dto.UserUpdateDto;
import ssrahoo.marketplaceapi.entity.User;
import ssrahoo.marketplaceapi.service.UserService;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // create
    @PostMapping
    public ResponseEntity<User> save(@RequestBody UserRegistrationDto userRegistrationDto) {
        var id = userService.save(userRegistrationDto);
        return ResponseEntity.created(URI.create("/user/" + id.toString())).build();
    }

    // read all
    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        var users = (userService.findAll());
        return ResponseEntity.ok(users);
    }

    // read by id
    @GetMapping("/{id}")
    public ResponseEntity<User> findById(@PathVariable("id") UUID id) {
        var user = userService.findById(id);
        return user.isPresent() ? ResponseEntity.ok(user.get()) : ResponseEntity.notFound().build();
    }

    // update
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateById(@PathVariable("id") UUID id, @RequestBody UserUpdateDto userUpdateDto){
        userService.updateById(id, userUpdateDto);
        return ResponseEntity.noContent().build();
    }

    // delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable("id") UUID id){
        userService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


}
