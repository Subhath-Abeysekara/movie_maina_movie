package com.movieMania.backend.Repository;


import com.movieMania.backend.Entity.roles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface rolesRepository extends JpaRepository<roles, Long> {
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM roles v WHERE v.id = :id")
    void deleteByRoleId(Long id);
}
