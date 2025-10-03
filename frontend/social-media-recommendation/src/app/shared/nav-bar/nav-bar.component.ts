import { Component } from '@angular/core';
import {Router, RouterLink} from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-nav-bar',
  templateUrl: './nav-bar.component.html',
  imports: [
    RouterLink
  ],
  styleUrls: ['./nav-bar.component.css']
})
export class NavBarComponent {

   isLoggedIn: boolean

  constructor(private router: Router, private authService: AuthService) {

      this.isLoggedIn= this.authService.isAuthenticated();
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
    //window.location.reload();
  }
}
