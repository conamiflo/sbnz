package com.ftn.sbnz.service.seeders;

import com.ftn.sbnz.model.models.Post;
import com.ftn.sbnz.model.models.User;
import com.ftn.sbnz.service.repositories.PostRepository;
import com.ftn.sbnz.service.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
//        seedRandomPosts();
    }

    private void seedRandomPosts() {
//        if (postRepository.count() > 0) {
//            System.out.println("Posts already seeded.");
//            return;
//        }

        List<User> users = userRepository.findAll();
        if (users.isEmpty()) {
            System.out.println("No users found. Seed users first!");
            return;
        }

        String[] hashtagsPool = {
                "trening","fitness","#photography","#tips","#creativity","#health","#food","#recipes",
                "#fitness","#workout","#core","#travel","#paris","#adventure","#AI","#tech","#innovation",
                "#design","#home","#interior","#reading","#learning","#motivation","#EV","#cars","#future",
                "AI","Innovation","TechNews","Football","Highlights","SportsUpdate","Football","Highlights",
                "SportsUpdate","Cooking","Recipes","Foodie"
        };

        String[] interestsPool = {
                "music","travel","sports","Technology","Sports","Technology","Sports","trening","fitness",
                "zdravlje","vežbe","tehnologija","programiranje","nauka","cooking"
        };

        List<Post> posts = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 40; i++) {
            User user = users.get(random.nextInt(users.size()));

            // Random title & category
            String category = interestsPool[random.nextInt(interestsPool.length)];
            String contentType = switch (random.nextInt(3)) {
                case 0 -> "text";
                case 1 -> "image";
                default -> "video";
            };
            String title = category + " post #" + (i + 1);

            // Random hashtags (1 to 4 per post)
            int numHashtags = 1 + random.nextInt(4);
            List<String> postHashtags = new ArrayList<>();
            for (int h = 0; h < numHashtags; h++) {
                postHashtags.add(hashtagsPool[random.nextInt(hashtagsPool.length)]);
            }

            // Random date within last 30 days
            LocalDateTime date = LocalDateTime.now().minusDays(random.nextInt(30)).minusHours(random.nextInt(24));

            // Random engagement numbers
            int likes = 50 + random.nextInt(500);
            int comments = 5 + random.nextInt(100);
            int shares = 0 + random.nextInt(50);
            int reach = 100 + random.nextInt(5000);
            double engagementRate = 0.01 + (random.nextDouble() * 0.25);

            Post post = new Post(user, title, contentType, category, postHashtags, date, likes, comments, shares, reach, engagementRate);
            posts.add(post);
        }

        postRepository.saveAll(posts);
        System.out.println("✅ Seeded " + posts.size() + " random posts successfully.");
    }
}