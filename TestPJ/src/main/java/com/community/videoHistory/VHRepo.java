package com.bproject.videoHistory;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class VHRepo {

    private final JdbcTemplate jdbc;

    private RowMapper<VideoHistory> videoHistoryRowMapper = new RowMapper<VideoHistory>() {
        @Override
        public VideoHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
            return new VideoHistory(
                    rs.getInt("id"),
                    rs.getInt("user_id"),
                    rs.getInt("video_id"),
                    rs.getTimestamp("watched_date").toLocalDateTime(),
                    rs.getInt("progress_rate")
            );
        }
    };

    public Page<VideoHistory> findRecentWatchedVideos(int userId, Pageable pageable) {

        // 1. 총 개수 쿼리 (페이지네이션을 위해 전체 개수가 필요합니다.)
        String countSql = "SELECT COUNT(id) FROM video_history WHERE user_id = ?";
        Integer totalCount = jdbc.queryForObject(countSql, Integer.class, userId);

        if (totalCount == null || totalCount == 0) {
            return Page.empty(pageable);
        }

        // 2. 데이터 조회 쿼리 (watched_date 기준 최신순 정렬 및 LIMIT/OFFSET 적용)
        String dataSql = "SELECT * FROM video_history " +
                "WHERE user_id = ? " +
                "ORDER BY watched_date DESC " + // 최신순 정렬 (필수)
                "LIMIT ? OFFSET ?";

        int pageSize = pageable.getPageSize();
        long offset = pageable.getOffset();

        List<VideoHistory> content = jdbc.query(
                dataSql,
                videoHistoryRowMapper,
                userId, pageSize, offset
        );

        // 3. Page 객체로 반환
        return new PageImpl<>(content, pageable, totalCount);
    }

    public void saveOrUpdateHistory(int userId, int videoId, int progressRate) {

        String sql = "INSERT INTO video_history (user_id, video_id, progress_rate, watched_date) " +
                "VALUES (?, ?, ?, CURRENT_TIMESTAMP()) " + //  watched_date에 DB 현재 시간 기록
                "ON DUPLICATE KEY UPDATE " +
                "progress_rate = VALUES(progress_rate), " + // 진도율 갱신
                "watched_date = CURRENT_TIMESTAMP()";       //  시청 기록 시간을 현재 시간으로 갱신

        jdbc.update(sql, userId, videoId, progressRate);
    }

}
