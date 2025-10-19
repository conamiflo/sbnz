import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostCardComponent } from '../post-card/post-card.component'; // Компонента за приказ поста
import { Post } from '../../../core/models/post.model';
import { PostService } from '../../../core/services/post.service';
import { RecommendationService } from '../../../core/services/recommendation.service';
import { RecommendationDTO } from '../../../core/models/recommendation-dto.model';
import { RecommendationResponse } from '../../../core/models/recommendation-response.model';

@Component({
  selector: 'app-post-page',
  standalone: true,
  imports: [
    CommonModule,
    PostCardComponent // Користимо PostCard за приказ обичних постова
    // RecommendationCardComponent // Додај ако направиш компоненту за препоруке
  ],
  templateUrl: './post-page.component.html',
  styleUrls: ['./post-page.component.css']
})
export class PostPageComponent implements OnInit {

  posts: Post[] = []; // Низ за обичне постове
  recommendations: RecommendationDTO[] = []; // ✅ Низ за препоруке

  isLoadingPosts = true; // Одвојено праћење учитавања
  isLoadingRecs = true;  // Одвојено праћење учитавања
  errorPosts: string | null = null;
  errorRecs: string | null = null;

  // ✅ Инјектујемо ОБА сервиса
  constructor(
    private postService: PostService,
    private recommendationService: RecommendationService
  ) {}

  ngOnInit(): void {
    // Позови обе методе за учитавање
    this.loadPosts();
    this.loadRecommendations();
  }

  // Метода за учитавање постова (остаје иста)
  loadPosts(): void {
    this.isLoadingPosts = true;
    this.errorPosts = null;
    this.postService.getAllPosts().subscribe({
      next: (data: Post[]) => {
        this.posts = data;
        this.isLoadingPosts = false;
        console.log('Posts loaded:', this.posts);
      },
      error: (err) => {
        console.error('Error loading posts:', err);
        this.errorPosts = 'Failed to load posts.';
        this.isLoadingPosts = false;
      }
    });
  }

  // ✅ Метода за учитавање препорука (додата)
  loadRecommendations(): void {
    this.isLoadingRecs = true;
    this.errorRecs = null;
    this.recommendationService.getRecommendations().subscribe({
      next: (response: RecommendationResponse) => {
        if (response.success && response.recommendations) {
          this.recommendations = response.recommendations;
          console.log('Recommendations loaded:', this.recommendations);
        } else {
          this.errorRecs = response.message || 'Failed to load recommendations (API Error).';
          this.recommendations = [];
        }
        this.isLoadingRecs = false;
      },
      error: (err) => {
        console.error('Error loading recommendations:', err);
        this.errorRecs = 'Failed to load recommendations (HTTP Error).';
        this.isLoadingRecs = false;
        this.recommendations = [];
      }
    });
  }
}
