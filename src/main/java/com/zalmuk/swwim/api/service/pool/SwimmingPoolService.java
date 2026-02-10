package com.zalmuk.swwim.api.service.pool;

import com.zalmuk.swwim.api.entity.pool.PoolReview;
import com.zalmuk.swwim.api.entity.pool.SavedPool;
import com.zalmuk.swwim.api.entity.pool.SwimmingPool;
import com.zalmuk.swwim.api.entity.user.User;
import com.zalmuk.swwim.api.repository.pool.PoolReviewRepository;
import com.zalmuk.swwim.api.repository.pool.SavedPoolRepository;
import com.zalmuk.swwim.api.repository.pool.SwimmingPoolRepository;
import com.zalmuk.swwim.api.repository.user.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class SwimmingPoolService {

    private final SwimmingPoolRepository poolRepository;
    private final PoolReviewRepository reviewRepository;
    private final SavedPoolRepository savedPoolRepository;
    private final UserRepository userRepository;

    public SwimmingPoolService(SwimmingPoolRepository poolRepository,
                                PoolReviewRepository reviewRepository,
                                SavedPoolRepository savedPoolRepository,
                                UserRepository userRepository) {
        this.poolRepository = poolRepository;
        this.reviewRepository = reviewRepository;
        this.savedPoolRepository = savedPoolRepository;
        this.userRepository = userRepository;
    }

    public Optional<SwimmingPool> findById(Integer id) {
        return poolRepository.findById(id);
    }

    public Optional<SwimmingPool> findByPlaceId(String placeId) {
        return poolRepository.findByPlaceId(placeId);
    }

    public Page<SwimmingPool> searchByName(String keyword, Pageable pageable) {
        return poolRepository.searchByName(keyword, pageable);
    }

    public List<SwimmingPool> findNearbyPools(BigDecimal latitude, BigDecimal longitude, double radiusKm) {
        return poolRepository.findNearbyPools(latitude, longitude, radiusKm);
    }

    public List<SwimmingPool> findAllSorted() {
        return poolRepository.findAllByOrderByDisplayOrderAsc();
    }

    @Transactional
    public SwimmingPool createOrUpdatePool(SwimmingPool pool) {
        return poolRepository.save(pool);
    }

    // 리뷰 관련
    public Page<PoolReview> getReviews(Integer poolId, Pageable pageable) {
        return poolRepository.findById(poolId)
                .map(pool -> reviewRepository.findByPoolOrderByCreatedAtDesc(pool, pageable))
                .orElse(Page.empty());
    }

    @Transactional
    public PoolReview createReview(Integer poolId, String userId, BigDecimal rating, String comment) {
        SwimmingPool pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new IllegalArgumentException("수영장을 찾을 수 없습니다."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        if (reviewRepository.existsByPoolAndUser(pool, user)) {
            throw new IllegalStateException("이미 리뷰를 작성했습니다.");
        }

        PoolReview review = new PoolReview(pool, user, rating, comment);
        review = reviewRepository.save(review);

        // 수영장 평점 업데이트
        updatePoolRating(pool);

        return review;
    }

    @Transactional
    public void deleteReview(java.util.UUID reviewId, String userId) {
        reviewRepository.findById(reviewId).ifPresent(review -> {
            if (!review.getUser().getId().equals(userId)) {
                throw new IllegalArgumentException("본인의 리뷰만 삭제할 수 있습니다.");
            }
            SwimmingPool pool = review.getPool();
            reviewRepository.delete(review);
            updatePoolRating(pool);
        });
    }

    private void updatePoolRating(SwimmingPool pool) {
        BigDecimal avgRating = reviewRepository.calculateAverageRating(pool.getId());
        long count = reviewRepository.countByPoolId(pool.getId());
        pool.setRating(avgRating);
        pool.setUserRatingsTotal((int) count);
        poolRepository.save(pool);
    }

    // 저장(즐겨찾기) 관련
    public boolean isSaved(String userId, Integer poolId) {
        return userRepository.findById(userId)
                .flatMap(user -> poolRepository.findById(poolId)
                        .map(pool -> savedPoolRepository.existsByUserAndPool(user, pool)))
                .orElse(false);
    }

    @Transactional
    public void savePool(String userId, Integer poolId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        SwimmingPool pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new IllegalArgumentException("수영장을 찾을 수 없습니다."));

        if (!savedPoolRepository.existsByUserAndPool(user, pool)) {
            SavedPool savedPool = new SavedPool(user, pool);
            savedPoolRepository.save(savedPool);
        }
    }

    /**
     * placeId로 수영장을 찾거나 새로 생성하여 즐겨찾기에 추가
     */
    @Transactional
    public SwimmingPool savePoolByPlaceId(String userId, String placeId, String name, String address,
                                           java.math.BigDecimal latitude, java.math.BigDecimal longitude,
                                           java.math.BigDecimal rating, Integer userRatingsTotal,
                                           String photoUrl, String phoneNumber, String website) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // placeId로 수영장 찾기, 없으면 생성
        SwimmingPool pool = poolRepository.findByPlaceId(placeId)
                .orElseGet(() -> {
                    SwimmingPool newPool = new SwimmingPool(placeId, name, address, latitude, longitude);
                    newPool.setRating(rating);
                    newPool.setUserRatingsTotal(userRatingsTotal);
                    newPool.setPhotoUrl(photoUrl);
                    newPool.setPhoneNumber(phoneNumber);
                    newPool.setWebsite(website);
                    return poolRepository.save(newPool);
                });

        // 즐겨찾기에 추가 (이미 있으면 무시)
        if (!savedPoolRepository.existsByUserAndPool(user, pool)) {
            SavedPool savedPool = new SavedPool(user, pool);
            savedPoolRepository.save(savedPool);
        }

        return pool;
    }

    /**
     * placeId로 즐겨찾기 제거
     */
    @Transactional
    public void unsavePoolByPlaceId(String userId, String placeId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        poolRepository.findByPlaceId(placeId).ifPresent(pool -> {
            savedPoolRepository.findByUserAndPool(user, pool)
                    .ifPresent(savedPoolRepository::delete);
        });
    }

    @Transactional
    public void unsavePool(String userId, Integer poolId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        SwimmingPool pool = poolRepository.findById(poolId)
                .orElseThrow(() -> new IllegalArgumentException("수영장을 찾을 수 없습니다."));

        savedPoolRepository.findByUserAndPool(user, pool)
                .ifPresent(savedPoolRepository::delete);
    }

    public Page<SwimmingPool> getSavedPools(String userId, Pageable pageable) {
        return savedPoolRepository.findSavedPoolsByUserId(userId, pageable);
    }
}
