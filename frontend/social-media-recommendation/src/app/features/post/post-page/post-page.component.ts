import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PostCardComponent } from '../post-card/post-card.component';
import {Post} from '../../../core/models/post.model';
import {PostService} from '../../../core/services/post.service';

@Component({
  selector: 'app-post-page',
  standalone: true,
  imports: [
    CommonModule,
    PostCardComponent
  ],
  templateUrl: './post-page.component.html',
  styleUrls: ['./post-page.component.css']
})
export class PostPageComponent implements OnInit {

  posts: Post[] = [];
  isLoading = true;
  error: string | null = null;

  constructor(private postService: PostService) {}

  ngOnInit(): void {
    this.loadPosts();
  }

  loadPosts(): void {
    this.isLoading = true;
    this.error = null;
    this.postService.getAllPosts().subscribe({
      next: (data: Post[]) => {
        this.posts = data;
        console.log("DATA", data);
        this.isLoading = false;
        console.log('Posts loaded:', this.posts);
      },
      error: (err) => {
        console.error('Error loading posts:', err);
        this.error = 'Failed to load posts. Please try again later.';
        this.isLoading = false;
      }
    });
  }
}
