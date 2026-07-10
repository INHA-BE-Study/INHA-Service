package org.study.inhamatch.domain.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.study.inhamatch.domain.chat.entity.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomJpaRepository extends JpaRepository<ChatRoom, Long> {

    Optional<ChatRoom> findByMatchId(Long matchId);

    // chat_room_members 테이블 조인 → 해당 유저가 속한 채팅방 전체 조회
    @Query("SELECT cr FROM ChatRoom cr JOIN cr.members m WHERE m.userId = :userId")
    List<ChatRoom> findAllByUserId(@Param("userId") Long userId);

    // 해당 유저가 속한 채팅방 존재 여부
    @Query("SELECT COUNT(cr) > 0 FROM ChatRoom cr JOIN cr.members m WHERE m.userId = :userId")
    boolean existsByUserId(@Param("userId") Long userId);
}
