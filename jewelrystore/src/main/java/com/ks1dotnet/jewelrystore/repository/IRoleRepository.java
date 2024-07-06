package com.ks1dotnet.jewelrystore.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.ks1dotnet.jewelrystore.entity.Role;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Integer> {

    @Query("SELECT u FROM Role u WHERE u.id != :excludedRole")
    List<Role> findAllExceptRole(@Param("excludedRole") int excludedRole);

}
