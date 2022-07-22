package org.task.repository;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.task.entity.db.UserName;

import java.util.Optional;

@EntityScan
public interface UserNameRepository extends JpaRepository<UserName, Long> {

    Optional<UserName> findByName(String name);

}
