import { Routes } from '@angular/router';
import {LoginComponent} from './features/auth/login/login.component';
import {RegisterComponent} from './features/auth/register/register.component';
import {CreatePostComponent} from './features/post/create-post/create-post.component';
import {AuthGuard} from './core/guards/auth.guard';
import {PostPageComponent} from './features/post/post-page/post-page.component';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'register',
    component: RegisterComponent,
  },
  {
    path: 'register',
    component: RegisterComponent,
  },
  {
    path: 'create-post',
    component: CreatePostComponent,
    canActivate: [AuthGuard]
  }
  ,
  {
    path: 'posts',
    component: PostPageComponent,
    canActivate: [AuthGuard]
  }
];
