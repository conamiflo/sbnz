import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CepService } from '../../core/services/cep.service'; // Ажурирано име сервиса
import { Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-nav-bar',
  standalone: true,
  templateUrl: './nav-bar.component.html',
  imports: [
    CommonModule,
    RouterLink,
    RouterLinkActive
  ],
  styleUrls: ['./nav-bar.component.css']
})
export class NavBarComponent implements OnInit, OnDestroy {

  isLoggedIn: boolean = false;

  trendSimLoading = false;
  saturationSimLoading = false;
  viralSimLoading = false;
  private simSubscription: Subscription = new Subscription();

  constructor(
    private router: Router,
    private authService: AuthService,
    private cepDemoService: CepService // Ажурирано име
  ) {}

  ngOnInit(): void {
    this.isLoggedIn = this.authService.isAuthenticated();
    // TODO: Додај праћење промена статуса аутентификације
  }

  ngOnDestroy(): void {
    this.simSubscription.unsubscribe();
  }

  logout(): void {
    this.authService.logout();
    this.isLoggedIn = false;
    this.router.navigate(['/login']);
  }

  runTrendSimulation(): void {
    const username = this.authService.getUsername(); // Дохвати username
    if (!username || !this.isLoggedIn) {
      alert("Морате бити улоговани.");
      return;
    }

    this.trendSimLoading = true;
    this.simSubscription.unsubscribe();
    this.simSubscription = this.cepDemoService.simulateRelevantTrend(username).subscribe({ // Шаљи username
      next: (res: any) => {
        this.trendSimLoading = false;
        console.log("Trend Simulation Result:", res);
        alert('Trend Simulation Result:\n' + JSON.stringify(res, null, 2));
      },
      error: (err: any) => {
        this.trendSimLoading = false;
        console.error("Trend Sim Error:", err); // Бољи лог
        alert('Trend Simulation Failed:\n' + (err.error?.message || err.message || JSON.stringify(err))); // Покушај да прикажеш бољу поруку
      }
    });
  }

  runSaturationSimulation(): void {
    if (!this.isLoggedIn) {
      alert("Морате бити улоговани.");
      return;
    }
    this.saturationSimLoading = true;
    this.simSubscription.unsubscribe();
    this.simSubscription = this.cepDemoService.simulateAudienceSaturation().subscribe({
      next: (res: any) => {
        this.saturationSimLoading = false;
        console.log("Saturation Simulation Result:", res);
        alert('Saturation Simulation Result:\n' + JSON.stringify(res, null, 2));
      },
      error: (err: any) => {
        this.saturationSimLoading = false;
        console.error("Saturation Sim Error:", err);
        alert('Saturation Simulation Failed:\n' + (err.error?.message || err.message || JSON.stringify(err)));
      }
    });
  }

  runViralMomentumSimulation(): void {
    const username = this.authService.getUsername(); // Дохвати username
    if (!username || !this.isLoggedIn) {
      alert("Морате бити улоговани.");
      return;
    }

    this.viralSimLoading = true;
    this.simSubscription.unsubscribe();
    this.simSubscription = this.cepDemoService.simulateViralMomentum(username).subscribe({
      next: (res: any) => {
        this.viralSimLoading = false;
        console.log("Viral Momentum Simulation Result:", res);
        alert('Viral Momentum Simulation Result:\n' + JSON.stringify(res, null, 2));
      },
      error: (err: any) => {
        this.viralSimLoading = false;
        console.error("Viral Momentum Sim Error:", err);
        alert('Viral Momentum Simulation Failed:\n' + (err.error?.message || err.message || JSON.stringify(err)));
      }
    });
  }
}
