package com.mikalauskas.ricardas.springbatch.database.reader.SpringBatch.database.reader.model.migration.mysql.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "student")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Student {
    @Id
    private Long id;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    private String email;
    @Column(name = "dept_id")
    private Long deptId;
    @Column(name = "is_active")
    private Boolean isActive;
}
