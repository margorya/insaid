package org.task.repository;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.task.entity.db.UserPassword;

import java.util.Optional;

@EntityScan
public interface UserPasswordRepository extends JpaRepository<UserPassword, Long> {

    Optional<UserPassword> findByIdUserNameAndPasswd(int idUserName, String passwd);

}
