import { Component, Input, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import {PostResponse} from '../../../core/models/post-response.model';
import {PostService} from '../../../core/services/post.service';
import {Post} from '../../../core/models/post.model';

@Component({
  selector: 'app-post-card',
  standalone: true, // Постави на true ако је standalone
  imports: [
    CommonModule // Додај CommonModule
  ],
  templateUrl: './post-card.component.html',
  styleUrls: ['./post-card.component.css'] // Исправљено име својства
})
export class PostCardComponent implements OnInit {
  // 1. Прима податке о посту споља
  @Input() post: Post | any; // Иницијализујемо као null

  likeCount: number = 0;
  commentCount: number = 0;
  shareCount: number = 0;

  isLoadingLike = false;
  isLoadingComment = false;
  isLoadingShare = false;
  error: string | null = null;

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    if (this.post) {
      this.likeCount = this.post.likes;
      this.commentCount = this.post.comments;
      this.shareCount = this.post.shares;
    }
  }

  like(): void {
    if (!this.post || this.isLoadingLike) return;

    this.isLoadingLike = true;
    this.error = null;
    this.postService.likePost(this.post.id).subscribe({
      next: (updatedPost: PostResponse) => {
        // Ажурирај бројач одмах (претпостављајући да сервер враћа ажуриран пост)
        // Идеално би било да backend врати ажуриран број лајкова
        this.likeCount++; // Оптимистично ажурирање
        // Ако желиш да будеш сигуран, користи updatedPost.likes (ако га враћаш)
        // if (this.post) this.post.likes++; // Можеш ажурирати и оригинални објекат
        this.isLoadingLike = false;
      },
      error: (err: any) => {
        console.error("Error liking post:", err);
        this.error = "Failed to like post.";
        this.isLoadingLike = false;
      }
    });
  }

  comment(): void {
    if (!this.post || this.isLoadingComment) return;

    this.isLoadingComment = true;
    this.error = null;
    this.postService.commentPost(this.post.id).subscribe({
      next: (updatedPost: PostResponse) => {
        this.commentCount++; // Оптимистично ажурирање
        // if (this.post) this.post.comments++;
        this.isLoadingComment = false;
        // Овде би иначе отворио модал/поље за унос коментара
        console.log("Comment action triggered for post:", this.post.id);
      },
      error: (err: any) => {
        console.error("Error commenting on post:", err);
        this.error = "Failed to comment.";
        this.isLoadingComment = false;
      }
    });
  }

  // 6. Метода за дељење
  share(): void {
    if (!this.post || this.isLoadingShare) return;

    this.isLoadingShare = true;
    this.error = null;
    this.postService.sharePost(this.post.id).subscribe({
      next: (updatedPost: PostResponse) => {
        this.shareCount++; // Оптимистично ажурирање
        // if (this.post) this.post.shares++;
        this.isLoadingShare = false;
        // Овде би иначе приказао опције за дељење
        console.log("Share action triggered for post:", this.post.id);
      },
      error: (err: any) => {
        console.error("Error sharing post:", err);
        this.error = "Failed to share post.";
        this.isLoadingShare = false;
      }
    });
  }
}
