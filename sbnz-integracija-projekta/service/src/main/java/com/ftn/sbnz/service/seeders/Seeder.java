package com.ftn.sbnz.service.seeders;

import com.ftn.sbnz.model.models.Post;
import com.ftn.sbnz.model.models.User;
import com.ftn.sbnz.service.repositories.PostRepository;
import com.ftn.sbnz.service.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
public class Seeder implements CommandLineRunner {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public Seeder(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
//        seedPosts();
    }

    private void seedTemplateBackward() {
        if (postRepository.count() > 0) {
            System.out.println("Posts already seeded.");
            return;
        }

        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            System.out.println("No users found. Seed users first!");
            return;
        }

        // Get specific users (assuming IDs 1 and 3 exist)
        User user1 = userRepository.findById(1L).orElse(users.get(0));
        User user3 = userRepository.findById(3L).orElse(users.size() > 1 ? users.get(1) : users.get(0));

        List<Post> posts = Arrays.asList(
                // Post 1
                new Post(user1,
                        "10 Tips for Better Photography",
                        "image",
                        "Photography",
                        Arrays.asList("#photography", "#tips", "#creativity"),
                        LocalDateTime.parse("2025-10-13T14:05:39.750940"),
                        120, 30, 45, 1000, 0.195),

                // Post 2
                new Post(user1,
                        "Top 5 Healthy Recipes for Busy People",
                        "video",
                        "Food",
                        Arrays.asList("#health", "#food", "#recipes"),
                        LocalDateTime.parse("2025-10-11T14:05:39.750940"),
                        200, 40, 70, 1500, 0.20666666666666667),

                // Post 3
                new Post(user1,
                        "Best Exercises for Core Strength",
                        "image",
                        "Fitness",
                        Arrays.asList("#fitness", "#workout", "#core"),
                        LocalDateTime.parse("2025-10-14T14:05:39.750940"),
                        250, 80, 100, 1800, 0.2388888888888889),

                // Post 4
                new Post(user1,
                        "Exploring the Streets of Paris – My Travel Diary",
                        "video",
                        "Travel",
                        Arrays.asList("#travel", "#paris", "#adventure"),
                        LocalDateTime.parse("2025-10-09T14:05:39.750940"),
                        320, 120, 150, 2500, 0.236),

                // Post 5
                new Post(user1,
                        "How AI Is Changing the World",
                        "text",
                        "Technology",
                        Arrays.asList("#AI", "#tech", "#innovation"),
                        LocalDateTime.parse("2025-10-12T14:05:39.750940"),
                        500, 100, 200, 4000, 0.2),

                // Post 6
                new Post(user1,
                        "Interior Design Trends for 2025",
                        "image",
                        "Design",
                        Arrays.asList("#design", "#home", "#interior"),
                        LocalDateTime.parse("2025-10-10T14:05:39.750940"),
                        180, 25, 65, 1300, 0.2076923076923077),

                // Post 7
                new Post(user1,
                        "Why You Should Read More Books",
                        "text",
                        "Education",
                        Arrays.asList("#reading", "#learning", "#motivation"),
                        LocalDateTime.parse("2025-10-07T14:05:39.750940"),
                        90, 10, 25, 900, 0.1388888888888889),

                // Post 8
                new Post(user1,
                        "The Future of Electric Cars",
                        "video",
                        "Automotive",
                        Arrays.asList("#EV", "#cars", "#future"),
                        LocalDateTime.parse("2025-10-08T14:05:39.750940"),
                        410, 75, 180, 3500, 0.19),

                // Post 9 - User 3
                new Post(user3,
                        "Top 10 AI breakthroughs in 2025",
                        "Article",
                        "Technology",
                        Arrays.asList("AI", "Innovation", "TechNews"),
                        LocalDateTime.parse("2025-10-18T14:55:11.727832"),
                        0, 0, 0, 0, 0),

                // Post 10 - User 3
                new Post(user3,
                        "Highlights from last night's football match",
                        "Video",
                        "Sports",
                        Arrays.asList("Football", "Highlights", "SportsUpdate"),
                        LocalDateTime.parse("2025-10-18T14:55:20.571926"),
                        0, 0, 0, 0, 0),

                // Post 11 - User 3 (duplicate)
                new Post(user3,
                        "Highlights from last night's football match",
                        "Video",
                        "Sports",
                        Arrays.asList("Football", "Highlights", "SportsUpdate"),
                        LocalDateTime.parse("2025-10-18T14:55:23.567069"),
                        0, 0, 0, 0, 0),

                // Post 12 - User 3
                new Post(user3,
                        "5 Easy Pasta Recipes for Beginners",
                        "Article",
                        "Cooking",
                        Arrays.asList("Cooking", "Recipes", "Foodie"),
                        LocalDateTime.parse("2025-10-18T14:55:37.592251"),
                        0, 0, 0, 0, 0)
        );

        postRepository.saveAll(posts);

        System.out.println("✅ Seeded " + posts.size() + " posts successfully.");
    }
}