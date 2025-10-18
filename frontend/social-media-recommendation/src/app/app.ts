import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import {NavBarComponent} from './shared/nav-bar/nav-bar.component';
import {AuthService} from './core/services/auth.service';
import {NgIf} from '@angular/common';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavBarComponent, NgIf],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('social-media-recommendation');
  constructor(private authService: AuthService) {}

  get isAuthenticated() {
    return this.authService.isAuthenticated();
  }
}
