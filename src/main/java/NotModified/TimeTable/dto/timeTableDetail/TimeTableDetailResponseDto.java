package NotModified.TimeTable.dto.timeTableDetail;

import NotModified.TimeTable.domain.Course;
import NotModified.TimeTable.domain.TimeTableDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeTableDetailResponseDto {
    private Long id;
    private int weekday;
    private String location;
    private LocalTime startTime;
    private LocalTime endTime;

    public TimeTableDetailResponseDto(TimeTableDetail ttd) {
        this.id = ttd.getId();
        this.weekday = ttd.getWeekday();
        this.location = ttd.getLocation();
        this.startTime = ttd.getStartTime();
        this.endTime = ttd.getEndTime();
    }
}
