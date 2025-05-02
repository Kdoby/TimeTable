package NotModified.TimeTable.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.time.LocalTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@DynamicInsert
@Table(name = "time_table_detail")
public class TimeTableDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "time_table_id")
    private TimeTable timeTable;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "weekday")
    private int weekday;

    @Column(name = "location")
    private String location;

    @Column(name = "start_time")
    private LocalTime start_time;

    @Column(name = "end_time")
    private LocalTime end_time;
}
