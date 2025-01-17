package com.ks1dotnet.jewelrystore.repository;

import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.ks1dotnet.jewelrystore.entity.UserInfo;

@Repository
public interface IUserInfoRepository extends JpaRepository<UserInfo, Integer> {
        @Query("SELECT u FROM UserInfo u WHERE u.role.id = 4 AND u.fullName LIKE %:name%")
        public Page<UserInfo> findCustomersByNameContainingIgnoreCase(@Param("name") String name,
                        Pageable pageable);

        @Query("SELECT u FROM UserInfo u WHERE u.role.id = 4 AND u.phoneNumber LIKE %:phoneNumber%")
        public Page<UserInfo> findCustomersByPhoneNumberContaining(
                        @Param("phoneNumber") String phoneNumber, Pageable pageable);

        @Query("SELECT u FROM UserInfo u WHERE u.role.id = 4 AND u.email LIKE %:email%")
        public Page<UserInfo> findCustomersByEmailContainingIgnoreCase(@Param("email") String email,
                        Pageable pageable);

        @Query("SELECT u FROM UserInfo u WHERE u.role.id = 5 AND u.fullName LIKE %:name%")
        public Page<UserInfo> findSuppliersByNameContainingIgnoreCase(@Param("name") String name,
                        Pageable pageable);

        @Query("SELECT u FROM UserInfo u WHERE u.role.id = 5 AND u.phoneNumber LIKE %:phoneNumber%")
        public Page<UserInfo> findSuppliersByPhoneNumberContaining(
                        @Param("phoneNumber") String phoneNumber, Pageable pageable);

        @Query("SELECT u FROM UserInfo u WHERE u.role.id = 5 AND u.email LIKE %:email%")
        public Page<UserInfo> findSuppliersByEmailContainingIgnoreCase(@Param("email") String email,
                        Pageable pageable);

        @Query(value = "SELECT * FROM user_info u WHERE u.id_role = 4",
                        countQuery = "SELECT count(*) FROM user_info u WHERE u.id_role = 4",
                        nativeQuery = true)
        Page<UserInfo> findCustomersByRoleId(Pageable pageable);

        @Query(value = "SELECT * FROM user_info u WHERE u.id_role = 5",
                        countQuery = "SELECT count(*) FROM user_info u WHERE u.id_role = 5",
                        nativeQuery = true)
        Page<UserInfo> findSuppliersByRoleId(Pageable pageable);

        public boolean existsByEmail(String email);

        public boolean existsByPhoneNumber(String phoneNumber);

        public boolean existsByEmailAndIdNot(String email, int id);

        public boolean existsByPhoneNumberAndIdNot(String phoneNumber, int id);

        @Query("SELECT u FROM UserInfo u WHERE u.role.id = 5 AND u.id = :id")
        public UserInfo findSupplierById(@Param("id") int id);

        @Query("SELECT u FROM UserInfo u WHERE u.role.id = 4 AND u.id = :id")
        public UserInfo findCustomerById(@Param("id") int id);

        @Query("SELECT u FROM UserInfo u WHERE u.role.id = 4 AND u.phoneNumber LIKE %:phoneNumber%")
        public Optional<UserInfo> findByPhoneNumber(@Param("phoneNumber") String phoneNumber);

        @Query("SELECT u FROM UserInfo u WHERE u.role.id = 4 AND u.email LIKE %:email%")
        public Optional<UserInfo> findByEmail(@Param("email") String email);

        @Query("SELECT u FROM UserInfo u Where u.role.id = 5 AND u.phoneNumber LIKE %:phoneNumber%")
        public Optional<UserInfo> findSupplierByPhoneNumber(@Param("phoneNumber") String phoneNumber);

        @Query("SELECT u FROM UserInfo u Where u.role.id = 5 AND u.email LIKE %:email%")
        public Optional<UserInfo> findSupplierByEmail(@Param("email") String email);

}
