package com.zalmuk.swwim.api.service.user;

import com.zalmuk.swwim.api.entity.user.BlockedUser;
import com.zalmuk.swwim.api.entity.user.User;
import com.zalmuk.swwim.api.repository.user.BlockedUserRepository;
import com.zalmuk.swwim.api.repository.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class BlockService {

    private final BlockedUserRepository blockedUserRepository;
    private final UserRepository userRepository;

    public BlockService(BlockedUserRepository blockedUserRepository, UserRepository userRepository) {
        this.blockedUserRepository = blockedUserRepository;
        this.userRepository = userRepository;
    }

    public boolean isBlocked(String userId, String blockedUserId) {
        return userRepository.findById(userId)
                .flatMap(user -> userRepository.findById(blockedUserId)
                        .map(blocked -> blockedUserRepository.existsByUserAndBlockedUser(user, blocked)))
                .orElse(false);
    }

    @Transactional
    public void blockUser(String userId, String blockedUserId, String reason) {
        if (userId.equals(blockedUserId)) {
            throw new IllegalArgumentException("자기 자신을 차단할 수 없습니다.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        User blockedUser = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new IllegalArgumentException("차단 대상을 찾을 수 없습니다."));

        if (blockedUserRepository.existsByUserAndBlockedUser(user, blockedUser)) {
            return; // 이미 차단됨
        }

        BlockedUser block = new BlockedUser(user, blockedUser);
        block.setReason(reason);
        blockedUserRepository.save(block);
    }

    @Transactional
    public void unblockUser(String userId, String blockedUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        User blockedUser = userRepository.findById(blockedUserId)
                .orElseThrow(() -> new IllegalArgumentException("차단 해제 대상을 찾을 수 없습니다."));

        blockedUserRepository.findByUserAndBlockedUser(user, blockedUser)
                .ifPresent(blockedUserRepository::delete);
    }

    public List<User> getBlockedUsers(String userId) {
        return blockedUserRepository.findBlockedUsersByUserId(userId);
    }

    public List<String> getBlockedUserIds(String userId) {
        return blockedUserRepository.findBlockedUserIdsByUserId(userId);
    }
}
