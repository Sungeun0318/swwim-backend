package com.zalmuk.swwim.api.repository.user;

import com.zalmuk.swwim.api.entity.user.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserSettingsRepository extends JpaRepository<UserSettings, String> {
}
