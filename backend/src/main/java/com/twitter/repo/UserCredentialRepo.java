package com.twitter.repo;

import com.twitter.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCredentialRepo extends JpaRepository<UserCredential, String> {
}
