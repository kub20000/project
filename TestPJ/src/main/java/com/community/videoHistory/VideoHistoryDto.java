package com.bproject.videoHistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoHistoryDto {
    private int videoId;
    private String videoTitle;
    private String thumbnail_url;
    private LocalDateTime watched_date;
    private int progress_rate;
}
