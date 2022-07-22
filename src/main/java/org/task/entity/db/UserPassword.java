package org.task.entity.db;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_password")
public class UserPassword {

    @Id
    private int id;

    @Column(name = "id_user_name", nullable = false)
    private int idUserName;

    @Column(name = "passwd", nullable = false)
    private String passwd;
}
