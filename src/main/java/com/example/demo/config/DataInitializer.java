package com.example.demo.config;

import com.example.demo.entity.Course;
import com.example.demo.repository.CourseRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Seeds sample courses on first startup if the table is empty.
 */
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner seedCourses(CourseRepository courseRepository) {
        return args -> {
            if (courseRepository.countByDeletedFalse() > 0) {
                return; // Already have data, skip
            }

            List<Course> samples = List.of(

                createCourse(
                    "HTML5 Toàn Tập – Nền Tảng Web",
                    "Cấu trúc trang, semantic tags, forms, SEO cơ bản. Lý tưởng cho người mới bắt đầu hoàn toàn. Bạn sẽ xây dựng được trang web tĩnh hoàn chỉnh sau khóa học.",
                    "Nguyễn Minh Tuấn",
                    60,
                    4,
                    LocalDate.now().plusDays(7),
                    null   // Miễn phí
                ),

                createCourse(
                    "CSS3 & Flexbox / Grid Mastery",
                    "Layout hiện đại, responsive design, animations và CSS Variables nâng cao. Nắm vững mọi kỹ thuật dàn trang chuyên nghiệp.",
                    "Trần Thu Hà",
                    50,
                    8,
                    LocalDate.now().plusDays(14),
                    new BigDecimal("499000")
                ),

                createCourse(
                    "JavaScript ES6+ Thực Chiến",
                    "DOM, async/await, Fetch API, module, closure — xây dựng mini-projects thực tế. Từ cơ bản đến nâng cao trong một lộ trình bài bản.",
                    "Lê Văn Hùng",
                    45,
                    12,
                    LocalDate.now().plusDays(10),
                    new BigDecimal("799000")
                ),

                createCourse(
                    "React 18 – Xây Dựng SPA Hiện Đại",
                    "Hooks, Context, React Router, tích hợp API và deploy lên Vercel. Học cách xây dựng Single Page Application từ đầu.",
                    "Phạm Quốc Bảo",
                    40,
                    10,
                    LocalDate.now().plusDays(21),
                    new BigDecimal("1200000")
                ),

                createCourse(
                    "Node.js & Express – Backend Từ Đầu",
                    "REST API, JWT, MongoDB, tối ưu hiệu suất và deploy lên Railway/Render. Làm chủ backend Node.js theo chuẩn thực tế doanh nghiệp.",
                    "Đỗ Thành Nam",
                    35,
                    9,
                    LocalDate.now().plusDays(28),
                    new BigDecimal("1100000")
                ),

                createCourse(
                    "Full-Stack Web Developer Bootcamp",
                    "HTML → CSS → JS → React → Node trong một lộ trình tiết kiệm nhất. Từ zero đến full-stack developer sẵn sàng đi làm.",
                    "Team DevAcademy",
                    30,
                    24,
                    LocalDate.now().plusDays(3),
                    new BigDecimal("2500000")
                )
            );

            courseRepository.saveAll(samples);
            System.out.println("[DataInitializer] ✅ Seeded " + samples.size() + " sample courses.");
        };
    }

    private Course createCourse(String title, String description, String instructor,
                                 int maxStudents, int durationWeeks,
                                 LocalDate startDate, BigDecimal price) {
        Course c = new Course();
        c.setTitle(title);
        c.setDescription(description);
        c.setInstructor(instructor);
        c.setMaxStudents(maxStudents);
        c.setDurationWeeks(durationWeeks);
        c.setStartDate(startDate);
        c.setPrice(price);
        c.setDeleted(false);
        return c;
    }
}
