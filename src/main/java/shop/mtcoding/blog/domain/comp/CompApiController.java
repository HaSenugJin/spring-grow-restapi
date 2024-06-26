package shop.mtcoding.blog.domain.comp;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import shop.mtcoding.blog._core.util.ApiUtil;
import shop.mtcoding.blog._core.util.JwtUtil;
import shop.mtcoding.blog._core.util.JwtVO;
import shop.mtcoding.blog.domain.resume.ResumeResponse;
import shop.mtcoding.blog.domain.resume.ResumeService;
import shop.mtcoding.blog.domain.user.SessionUser;
import shop.mtcoding.blog.domain.user.User;
import shop.mtcoding.blog.domain.user.UserService;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class CompApiController {
    private final CompService compService;
    private final HttpSession session;
    private final UserService userService;
    private final ResumeService resumeService;

    // 기업 회원 가입
    @PostMapping("/comp/join")
    public ResponseEntity<?> compJoin(@Valid @RequestBody CompRequest.CompJoinDTO reqDTO, Errors errors) {
        User user = compService.join(reqDTO);
        String jwt = JwtUtil.create(user);
        return ResponseEntity.ok()
                .header(JwtVO.HEADER,JwtVO.PREFIX + jwt)
                .body(new ApiUtil<>(new CompResponse.CompJoinDTO(user)));
    }

    //기업 채용정보 (공고 뿌리기)
    @GetMapping("/api/comp/jobs-info")
    public ResponseEntity<?> jobsInfo() {
        List<CompResponse.CompJobsInfoDTO> respDTO = compService.jobsInfoList();
        return ResponseEntity.ok(new ApiUtil<>(respDTO));
    }

    //기업 메인 (메인이라 api 안 붙였는데 필요하면 붙이세용)
    @GetMapping("/comps/comp-index")
    public ResponseEntity<?> compIndex() {
        List<CompResponse.CompMainDTO> reqsDTO = compService.compMainList();
        return ResponseEntity.ok(new ApiUtil<>(reqsDTO));
    }

    // 기업이 지원받은 이력서 상세보기 페이지 요청
    @PostMapping("/api/comp/resume-detail/{resumeId}")
    public ResponseEntity<?> resumeDetail(@PathVariable Integer resumeId, @RequestBody CompRequest.JobsIdDTO reqDTO) {
        SessionUser sessionComp = (SessionUser) session.getAttribute("sessionComp");
        User user = userService.findById(sessionComp.getId());
        ResumeResponse.CompDetailDTO respDTO =
                resumeService.compResumeDetail(resumeId, reqDTO.getJobsId(), user);

        return ResponseEntity.ok(new ApiUtil(respDTO));
    }

    // 기업 사용자 공고 관리 페이지 요청
    @GetMapping("/api/comps/comp-home")
    public ResponseEntity<?> compHome() {
        SessionUser sessionUser = (SessionUser) session.getAttribute("sessionComp");
        System.out.println(sessionUser.getId());
        List<CompResponse.CompHomeDTO> respList = compService.compHomeDTOS(sessionUser.getId());

        return ResponseEntity.ok(new ApiUtil<>(respList));
    }

    // 기업 공개 이력서 열람 페이지 요청
    @GetMapping("/api/comps/read-resume")
    public ResponseEntity<?> readResume() {
        List<CompResponse.ResumeUserSkillDTO> rusList = compService.readResume();
        return ResponseEntity.ok(new ApiUtil<>(rusList));
    }

    // 기업 지원 내역 페이지 요청
    @GetMapping("/api/comps/comp-manage")
    public ResponseEntity<?> compManage() {
        SessionUser sessionUser = (SessionUser) session.getAttribute("sessionComp");
        CompResponse.CompManageDTO compManageDTO = compService.compManage(sessionUser.getId());
        return ResponseEntity.ok(new ApiUtil<>(compManageDTO));
    }

    // 기업의 모든 공고 조회 요청
    @PostMapping("/api/find-all-jobs")
    public ResponseEntity<?> compManageDTO(@RequestBody CompRequest.UserIdDTO reqDTO) {
        return ResponseEntity.ok(new ApiUtil<>(compService.compManage(reqDTO.getUserId())));
    }

    // 기업의 모든 공고에 지원한 모든 지원자 조회 요청
    @PostMapping("/api/find-all-applicants")
    public ResponseEntity<?> findAllApplicants(@RequestBody CompRequest.UserIdDTO reqDTO) {
        return ResponseEntity.ok(new ApiUtil<>(compService.findAllAppli(reqDTO.getUserId())));
    }

    // 기업 특정 공고에 지원한 지원자 조회 요청
    @PostMapping("/api/find-applicants")
    public ResponseEntity<?> findApplicants(@RequestBody CompRequest.JobsIdDTO reqDTO) {
        return ResponseEntity.ok(new ApiUtil<>(compService.findApplicants(reqDTO.getJobsId())));
    }

    // 기업 지원자 이력서에 합격/불합격 응답안한 이력서 조회
    @PostMapping("/api/find-no-resp")
    public ResponseEntity<?> findNoResp(@RequestBody CompRequest.UserIdDTO reqDTO) {
        return ResponseEntity.ok(new ApiUtil<>(compService.findNoResp(reqDTO.getUserId())));
    }
}

