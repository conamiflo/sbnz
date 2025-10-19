import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment'; // Прилагоди путању ако треба
import { PostCreateRequest } from '../models/post-create-request.model';
import {PostResponse} from '../models/post-response.model';
import {Post} from '../models/post.model';

// Увези моделе које си дефинисао (прилагоди путање)

@Injectable({
  providedIn: 'root'
})
export class PostService {
  private readonly API_URL = `${environment.apiUrl}/posts`;

  constructor(private http: HttpClient) {}

  createPost(data: PostCreateRequest): Observable<PostResponse> {
    return this.http.post<PostResponse>(this.API_URL, data);
  }

  getAllPosts(): Observable<Post[]> {
    return this.http.get<Post[]>(this.API_URL);
  }

  getPostById(postId: number): Observable<PostResponse> {
    return this.http.get<PostResponse>(`${this.API_URL}/${postId}`);
  }

  getPostsByUsername(username: string): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.API_URL}/user/${username}`);
  }

  getPostsByUser(userId: number): Observable<Post[]> {
    return this.http.get<Post[]>(`${this.API_URL}/user/${userId}`);
  }

  likePost(postId: number): Observable<PostResponse> {
    return this.http.post<PostResponse>(`${this.API_URL}/${postId}/like`, {});
  }


  sharePost(postId: number): Observable<PostResponse> {
    return this.http.post<PostResponse>(`${this.API_URL}/${postId}/share`, {});
  }

  commentPost(postId: number): Observable<PostResponse> {
    return this.http.post<PostResponse>(`${this.API_URL}/${postId}/comment`, {});
  }
}
