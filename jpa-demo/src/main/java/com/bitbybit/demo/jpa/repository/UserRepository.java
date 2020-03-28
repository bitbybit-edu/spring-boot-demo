package com.bitbybit.demo.jpa.repository;

import com.bitbybit.demo.jpa.entity.JpaUser;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * @author liulin
 */
public interface UserRepository extends CrudRepository<JpaUser, Long> {

    /**
     * findByName
     *
     * @param name name
     * @return List<JpaUser>
     */
    List<JpaUser> findByName(String name);
}
