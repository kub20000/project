package com.bproject.videoHistory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoHistory {
    private int id;
    private int user_id;
    private int video_id;
    private LocalDateTime watched_date;
    private int progress_rate;
}
