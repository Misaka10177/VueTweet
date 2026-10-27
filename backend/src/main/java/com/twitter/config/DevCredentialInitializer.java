package com.twitter.config;

import com.twitter.entity.User;
import com.twitter.entity.UserCredential;
import com.twitter.repo.UserCredentialRepo;
import com.twitter.repo.UserRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Local/demo bootstrap only: gives existing users a BCrypt credential so the
 * new login flow can be exercised without a registration screen. Disable with
 * app.auth.seed-default-password=false once real accounts exist.
 */
@Component
@ConditionalOnProperty(name = "app.auth.seed-default-password", havingValue = "true")
public class DevCredentialInitializer implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(DevCredentialInitializer.class);
    private static final String DEFAULT_PASSWORD = "123456";
    private static final String TEST_USER_ID = "deepseek";
    private static final String TEST_USER_NAME = "DeepSeek";
    private static final String TEST_PASSWORD = "deepseek123";

    private final UserRepo userRepo;
    private final UserCredentialRepo credentialRepo;
    private final PasswordEncoder passwordEncoder;

    public DevCredentialInitializer(UserRepo userRepo, UserCredentialRepo credentialRepo,
                                    PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.credentialRepo = credentialRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        String hash = passwordEncoder.encode(DEFAULT_PASSWORD);
        int created = 0;
        for (User user : userRepo.findAll()) {
            if (credentialRepo.existsById(user.getId())) {
                continue;
            }
            saveCredential(user.getId(), hash);
            created++;
        }
        if (created > 0) {
            log.warn("Seeded {} demo credentials with the default password '{}'. "
                    + "Set app.auth.seed-default-password=false in production.", created, DEFAULT_PASSWORD);
        }

        ensureTestAccount();
    }

    private void ensureTestAccount() {
        User testUser = userRepo.findById(TEST_USER_ID).orElse(null);
        if (testUser == null) {
            testUser = new User();
            testUser.setId(TEST_USER_ID);
            testUser.setName(TEST_USER_NAME);
            testUser.setProfilePhoto("/images/img.png");
            userRepo.save(testUser);
            log.warn("Created test account '{}'.", TEST_USER_ID);
        }
        if (!credentialRepo.existsById(TEST_USER_ID)) {
            saveCredential(TEST_USER_ID, passwordEncoder.encode(TEST_PASSWORD));
            log.warn("Test account ready: username='{}', password='{}'.", TEST_USER_ID, TEST_PASSWORD);
        }
    }

    private void saveCredential(String userId, String passwordHash) {
        UserCredential credential = new UserCredential();
        credential.setUserId(userId);
        credential.setPasswordHash(passwordHash);
        credential.setPasswordUpdatedAt(LocalDateTime.now());
        credential.setFailedAttempts(0);
        credentialRepo.save(credential);
    }
}
