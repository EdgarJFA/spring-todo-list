package mz.com.edgaramado.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import jakarta.servlet.http.HttpServletRequest;
import mz.com.edgaramado.todolist.utils.Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @PostMapping()
    public ResponseEntity create(@RequestBody UserModel user) {
        var respUser =  this.userRepository.findByUserName(user.getUserName());
        if(respUser != null)  {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exist!");
        }
        var passwordEncrypted = BCrypt.withDefaults().hashToString(12, user.getPassword().toCharArray());
        user.setPassword(passwordEncrypted);
        var response = this.userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    @GetMapping
    public List<UserModel> list() {
        return this.userRepository.findAll();
    }
    @PutMapping("/{id}")
    public UserModel update(@RequestBody UserModel userModel, HttpServletRequest request, @PathVariable UUID id) {
        var user = this.userRepository.findById(id).orElse(null);
        Utils.copyNonNullProperties(userModel, user);
        return this.userRepository.save(user);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        var user = this.userRepository.findById(id).orElse(null);
        this.userRepository.delete(user);
    }
}
