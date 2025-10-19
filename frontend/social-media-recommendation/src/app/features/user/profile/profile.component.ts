import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router } from '@angular/router';
import {AuthService} from '../../../core/services/auth.service';
import {UserService} from '../../../core/services/user.service';
import {PostService} from '../../../core/services/post.service';
import {PostCardComponent} from '../../post/post-card/post-card.component';
import {UserResponse} from '../../../core/models/user-response.model';
import {Post} from '../../../core/models/post.model'; // За читање username-а из руте

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, PostCardComponent],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit {

  userProfile: UserResponse | null = null;
  userPosts: Post[] = [];
  isLoadingProfile = true;
  isLoadingPosts = true;
  profileError: string | null = null;
  postsError: string | null = null;
  isOwnProfile = false;
  usernameToLoad: string | null = null; // Username чији профил учитавамо

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private authService: AuthService,
    private userService: UserService,
    private postService: PostService
  ) {}

  ngOnInit(): void {
    // Слушај промене у параметрима руте (ако корисник иде са једног профила на други)
    this.route.paramMap.subscribe(params => {
      const routeUsername = params.get('username'); // Прочитај username из руте
      const loggedInUsername = this.authService.getUsername(); // Дохвати username улогованог

      if (routeUsername) {
        // Приказујемо профил из руте
        this.usernameToLoad = routeUsername;
        this.isOwnProfile = (loggedInUsername === this.usernameToLoad);
      } else if (loggedInUsername) {
        // Нема username-а у рути, приказујемо профил улогованог корисника
        this.usernameToLoad = loggedInUsername;
        this.isOwnProfile = true;
      } else {
        // Нема username-а ни у рути, нити је корисник улогован
        this.profileError = "User not specified or not logged in.";
        this.isLoadingProfile = false;
        this.isLoadingPosts = false;
        // Можда преусмерити на логин
        // this.router.navigate(['/login']);
        return; // Прекини даље извршавање
      }

      // Учитај податке за одређени username
      this.loadUserProfile(this.usernameToLoad);
      this.loadUserPosts(this.usernameToLoad);
    });
  }

  loadUserProfile(username: string): void {
    this.isLoadingProfile = true;
    this.profileError = null;
    // ✅ Позивамо нову методу сервиса
    this.userService.getUserProfileByUsername(username).subscribe({
      next: (profile) => {
        this.userProfile = profile;
        this.isLoadingProfile = false;
      },
      error: (err) => {
        console.error('Error loading user profile:', err);
        this.profileError = 'Failed to load user profile.';
        this.isLoadingProfile = false;
        if (err.status === 404) {
          this.profileError = 'User not found.';
        }
      }
    });
  }

  loadUserPosts(username: string): void {
    this.isLoadingPosts = true;
    this.postsError = null;
    this.postService.getPostsByUsername(username).subscribe({
      next: (posts) => {
        this.userPosts = posts;
        this.isLoadingPosts = false;
      },
      error: (err) => {
        console.error('Error loading user posts:', err);
        this.postsError = 'Failed to load posts.';
        this.isLoadingPosts = false;
      }
    });
  }
}
