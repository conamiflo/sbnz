import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import {PostCreateRequest} from '../../../core/models/post-create-request.model';
import {PostService} from '../../../core/services/post.service';

@Component({
  selector: 'app-create-post',
  standalone: true,
  imports: [
    FormsModule,
    CommonModule
  ],
  templateUrl: './create-post.component.html',
  styleUrls: ['./create-post.component.css']
})
export class CreatePostComponent {
  formData: {
    content: string | null;
    contentType: string | null;
    category: string | null;
    hashtags: string | null;
  } = {
    content: null,
    contentType: null,
    category: null,
    hashtags: null
  };

  isLoading = false;
  successMessage: string | null = null;
  errorMessage: string | null = null;

  constructor(private postService: PostService, private router: Router) {}

  onSubmit(): void {
    this.isLoading = true;
    this.successMessage = null;
    this.errorMessage = null;

    const dtoToSend: PostCreateRequest = {
      content: this.formData.content ?? '',
      contentType: this.formData.contentType ?? '',
      category: this.formData.category ?? '',
      hashtags: this.formData.hashtags
          ?.split(',')
          ?.map(tag => tag.trim())
          ?.filter(tag => tag !== '')
        ?? []
    };

    this.postService.createPost(dtoToSend).subscribe({
      next: (response: any) => {
        this.isLoading = false;
        this.successMessage = 'Post successfully created!';
        console.log('Post created:', response);
        this.resetForm();
      },
      error: (error: { error: { message: string; }; }) => {
        this.isLoading = false;
        this.errorMessage = error.error?.message || 'Failed to create post. Please try again.';
        console.error('Error creating post:', error);
      }
    });
  }

  resetForm(): void {
    this.formData = { content: null, contentType: null, category: null, hashtags: null };
  }
}
