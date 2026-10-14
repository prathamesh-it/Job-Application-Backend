package com.JobApplication.JobApplication.Repository;

import com.JobApplication.JobApplication.Entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long>
{
    //user delete vel companies cha owner null karayla:
    @Modifying(clearAutomatically = true , flushAutomatically = true)
    @Query("UPDATE Company c SET c.createdBy = null WHERE c.createdBy.id = :userId")
    void clearOwner(@Param("userId") Long userId);
}
