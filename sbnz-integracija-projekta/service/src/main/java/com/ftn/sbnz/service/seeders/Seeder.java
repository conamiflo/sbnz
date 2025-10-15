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
import java.util.Random;

@Component
public class Seeder implements CommandLineRunner  {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    public Seeder(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
//        seedPosts();
    }

    private void seedPosts() {
        if (postRepository.count() > 0) {
            System.out.println("Posts already seeded.");
            return;
        }

        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            System.out.println("No users found. Seed users first!");
            return;
        }

        List<Post> posts = Arrays.asList(
                new Post(randomUser(users),
                        "10 Tips for Better Photography",
                        "image",
                        "Photography",
                        Arrays.asList("#photography", "#tips", "#creativity"),
                        LocalDateTime.now().minusDays(2),
                        120, 45, 30, 1000, 0),

                new Post(randomUser(users),
                        "Top 5 Healthy Recipes for Busy People",
                        "video",
                        "Food",
                        Arrays.asList("#health", "#food", "#recipes"),
                        LocalDateTime.now().minusDays(4),
                        200, 70, 40, 1500, 0),

                new Post(randomUser(users),
                        "Best Exercises for Core Strength",
                        "image",
                        "Fitness",
                        Arrays.asList("#fitness", "#workout", "#core"),
                        LocalDateTime.now().minusDays(1),
                        250, 100, 80, 1800, 0),

                new Post(randomUser(users),
                        "Exploring the Streets of Paris – My Travel Diary",
                        "video",
                        "Travel",
                        Arrays.asList("#travel", "#paris", "#adventure"),
                        LocalDateTime.now().minusDays(6),
                        320, 150, 120, 2500, 0),

                new Post(randomUser(users),
                        "How AI Is Changing the World",
                        "text",
                        "Technology",
                        Arrays.asList("#AI", "#tech", "#innovation"),
                        LocalDateTime.now().minusDays(3),
                        500, 200, 100, 4000, 0),

                new Post(randomUser(users),
                        "Interior Design Trends for 2025",
                        "image",
                        "Design",
                        Arrays.asList("#design", "#home", "#interior"),
                        LocalDateTime.now().minusDays(5),
                        180, 65, 25, 1300, 0),

                new Post(randomUser(users),
                        "Why You Should Read More Books",
                        "text",
                        "Education",
                        Arrays.asList("#reading", "#learning", "#motivation"),
                        LocalDateTime.now().minusDays(8),
                        90, 25, 10, 900, 0),

                new Post(randomUser(users),
                        "The Future of Electric Cars",
                        "video",
                        "Automotive",
                        Arrays.asList("#EV", "#cars", "#future"),
                        LocalDateTime.now().minusDays(7),
                        410, 180, 75, 3500, 0)
        );

        posts.forEach(Post::calculateEngagementRate);
        postRepository.saveAll(posts);

        System.out.println("✅ Seeded " + posts.size() + " posts successfully.");
    }

    private User randomUser(List<User> users) {
        return users.get(random.nextInt(users.size()));
    }
}
