package com.attendance;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@SpringBootApplication
public class AttendanceApplication {
    public static void main(String[] args) {
        SpringApplication.run(AttendanceApplication.class, args);
    }
}

// Lecturer -> Course (Option B)
class Data {
    static Map<String, String> lecturerToCourse = new LinkedHashMap<>();
    static Map<String, String> lecturerPassword = new HashMap<>();
    static Map<String, List<String>> attendance = new HashMap<>();
    static String currentOtp = "7563";
    static String currentCourse = "";

    static {
        lecturerToCourse.put("lecturer1", "Programming with Java");
        lecturerToCourse.put("lecturer2", "Discrete Mathematics");
        lecturerToCourse.put("lecturer3", "Differential Equations");
        lecturerToCourse.put("lecturer4", "Computer Architecture");
        lecturerToCourse.put("lecturer5", "Digital Systems");
        lecturerToCourse.put("lecturer6", "Psychology");
        lecturerToCourse.put("lecturer7", "Database Systems");
        lecturerToCourse.put("lecturer8", "Computer Graphics");
        // For testing, you can also use mensah / 1234
        lecturerToCourse.put("mensah", "Programming with Java");
        lecturerPassword.put("lecturer1", "1234");
        lecturerPassword.put("lecturer2", "1234");
        lecturerPassword.put("lecturer3", "1234");
        lecturerPassword.put("lecturer4", "1234");
        lecturerPassword.put("lecturer5", "1234");
        lecturerPassword.put("lecturer6", "1234");
        lecturerPassword.put("lecturer7", "1234");
        lecturerPassword.put("lecturer8", "1234");
        lecturerPassword.put("mensah", "1234");
    }
}

@Controller
class HomeController {
    @GetMapping("/")
    public String home() { return "login"; }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, Model model) {
        if (Data.lecturerPassword.containsKey(username) && Data.lecturerPassword.get(username).equals(password)) {
            model.addAttribute("username", username);
            model.addAttribute("course", Data.lecturerToCourse.get(username));
            return "dashboard";
        }
        model.addAttribute("error", "Wrong username or password");
        return "login";
    }

    @PostMapping("/start")
    public String start(@RequestParam String username, Model model) {
        String course = Data.lecturerToCourse.get(username);
        Data.currentCourse = course;
        Data.currentOtp = String.valueOf(1000 + new Random().nextInt(9000));
        model.addAttribute("username", username);
        model.addAttribute("course", course);
        model.addAttribute("otp", Data.currentOtp);
        model.addAttribute("link", "https://your-render-link.onrender.com/student?course=" + course.replace(" ", "%20"));
        return "session";
    }

    @GetMapping("/student")
    public String student(@RequestParam(required = false) String course, Model model) {
        model.addAttribute("course", course == null ? Data.currentCourse : course);
        return "student";
    }

    @PostMapping("/mark")
    public String mark(@RequestParam String studentId, @RequestParam String otp, @RequestParam String course, Model model) {
        if (!otp.equals(Data.currentOtp)) {
            model.addAttribute("error", "Wrong OTP. Ask lecturer for correct OTP.");
            model.addAttribute("course", course);
            return "student";
        }
        Data.attendance.computeIfAbsent(course, k -> new ArrayList<>()).add(studentId + " - " + new Date());
        model.addAttribute("success", "Attendance marked for " + course);
        model.addAttribute("course", course);
        return "student";
    }

    @GetMapping("/attendance")
    @ResponseBody
    public Map<String, List<String>> allAttendance() { return Data.attendance; }
}
