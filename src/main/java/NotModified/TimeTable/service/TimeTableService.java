package NotModified.TimeTable.service;

import NotModified.TimeTable.domain.Course;
import NotModified.TimeTable.domain.TimeTable;
import NotModified.TimeTable.domain.TimeTableDetail;
import NotModified.TimeTable.dto.timeTable.TimeTableRequestDto;
import NotModified.TimeTable.dto.timeTable.TimeTableResponseDto;
import NotModified.TimeTable.dto.timeTable.TimeTableUpdateDto;
import NotModified.TimeTable.repository.interfaces.CourseRepository;
import NotModified.TimeTable.repository.interfaces.TimeTableDetailRepository;
import NotModified.TimeTable.repository.interfaces.TimeTableRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class TimeTableService {
    private final TimeTableRepository timeTableRepository;
    private final TimeTableDetailRepository timeTableDetailRepository;
    private final CourseRepository courseRepository;

    // 특정 id에 해당하는 시간표 리턴
    public TimeTable findTimeTable(Long id) {
        return timeTableRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 시간표입니다.")
        );
    }

    // 새 시간표 등록
    public void saveTimeTable(TimeTableRequestDto dto) {
        TimeTable timeTable = TimeTable.builder()
                .userId(dto.getUserId())
                .name(dto.getName())
                .build();

        // isMain이 존재하는지 먼저 확인하고, 없으면 지금 추가하는 걸 isMain으로 설정
        boolean exists = timeTableRepository.findIsMain(true, dto.getUserId()).isPresent();
        timeTable.setIsMain(!exists);

        timeTableRepository.save(timeTable);
    }
    
    // 특정 유저가 등록한 전체 시간표(학기별) 목록 조회
    public List<TimeTableResponseDto> findTimeTableList(String userId) {
        // TimeTable domain -> TimeTableResponseDto
        return timeTableRepository.findAll(userId)
                .stream()
                .map(TimeTableResponseDto::new)
                .collect(Collectors.toList());
    }

    // 시간표 정보 수정
    public void updateTimeTable(Long id, TimeTableUpdateDto dto) {
        TimeTable timeTable = findTimeTable(id);

        if(dto.getName() != null) timeTable.setName(dto.getName());

        // is_main 시간표를 수정하는 경우: 이미 존재하는 main 이 있으면 걔를 취소하고 업데이트
        if(dto.getIsMain() != null && Boolean.TRUE.equals(dto.getIsMain())) {
            Optional<TimeTable> currentMain = timeTableRepository.findIsMain(true, timeTable.getUserId());
            currentMain.ifPresent(main -> main.setIsMain(false));
            timeTable.setIsMain(true);
        }
    }

    // 시간표 삭제
    // fix: 해당 시간표 밑에 있던 모든 세부시간표 및 강좌(cascade x)를 삭제 해야함
    // fix: 삭제 후에 main 인 애가 없으면 얘로 업데이트
    public void deleteTimeTable(Long id) {
        TimeTable timeTable = findTimeTable(id);
        String userId = timeTable.getUserId();
        boolean wasMain = Boolean.TRUE.equals(timeTable.getIsMain());

        // 해당 시간표에 속한 세부 시간표 목록 가져오기
        List<TimeTableDetail> details = timeTableDetailRepository.findAll(id);
        
        // 세부 시간표가 속한 courses 가져오기
        // set: distinct 하게 가져옴
        Set<Long> courseIds = details.stream()
                .map(detail -> detail.getCourse().getId())
                .collect(Collectors.toSet());

        timeTableRepository.delete(timeTable);

        if(wasMain) {
            List<TimeTable> remaining = timeTableRepository.findAllOrderByCreatedAt(userId);
            if(!remaining.isEmpty()){
                TimeTable newest = remaining.get(0);
                newest.setIsMain(true);
            }
        }

        for(Long courseId : courseIds) {
            Course course = courseRepository.findById(courseId)
                    .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강좌입니다."));
            courseRepository.delete(course);
        }
    }
}
