package com.JobApplication.JobApplication.Repository;

import com.JobApplication.JobApplication.Entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>
{
    boolean existsByEmail(String email);
}
