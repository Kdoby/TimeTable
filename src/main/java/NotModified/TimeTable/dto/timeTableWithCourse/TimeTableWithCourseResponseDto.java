package NotModified.TimeTable.dto.timeTableWithCourse;

import NotModified.TimeTable.domain.Course;
import NotModified.TimeTable.domain.TimeTableDetail;
import NotModified.TimeTable.dto.timeTableDetail.TimeTableDetailResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.util.stream.Collectors;
import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TimeTableWithCourseResponseDto {
    private Long courseId;
    private String title;
    private String instructor;
    private String color;
    private List<TimeTableDetailResponseDto> details;

    public TimeTableWithCourseResponseDto (Course course, List<TimeTableDetail> details) {
        this.courseId = course.getId();
        this.title = course.getTitle();
        this.instructor = course.getInstructor();
        this.color = course.getColor();
        this.details = details.stream()
                .map(TimeTableDetailResponseDto::new)
                .collect(Collectors.toList());
    }
}
