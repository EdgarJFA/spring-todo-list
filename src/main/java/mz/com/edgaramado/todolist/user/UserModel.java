package mz.com.edgaramado.todolist.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity(name = "tb_users")
public class UserModel {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;
    private String name;
//    @Column(name = "user") Used  to rename the class attribute in database column name, in this case userName would be user.
    @Column(unique = true)
    private String userName;
    private String password;
    @CreationTimestamp
    private LocalDateTime createAt;
}
