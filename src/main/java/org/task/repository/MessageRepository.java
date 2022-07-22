package org.task.repository;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.task.entity.db.Message;

import java.util.List;

@EntityScan
public interface MessageRepository extends JpaRepository<Message, Long> {

    @Query(nativeQuery = true, value = "select * from insaid.message where id_user_name = ?1 order by date_time desc limit ?2")
    List<Message> findAllByIdUserNameWithLimit(Integer idUserName, Long limit);
}
