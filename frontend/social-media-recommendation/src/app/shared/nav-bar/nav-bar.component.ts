import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router, RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { CepService } from '../../core/services/cep.service'; // Ажурирано име сервиса
import { Subscription } from 'rxjs';
import { CommonModule } from '@angular/common';
import { TemplateService } from '../../core/services/template.service';
import { BackwardService } from '../../core/services/backward.service';

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
  templateLoading = false;
  backwardLoading = false;
  connectedLoading = false;
  private simSubscription: Subscription = new Subscription();

  constructor(
    private router: Router,
    private authService: AuthService,
    private cepDemoService: CepService,
    private templateService: TemplateService,
    private backwardService: BackwardService
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
  
  runTemplateExcel(): void {
    if (!this.isLoggedIn) {
      alert("You must be logged in to run this.");
      return;
    }

    this.templateLoading = true;
    this.templateService.generateFromExcel().subscribe({
      next: res => {
        this.templateLoading = false;
        console.log("Template result:", res);
        alert('Template result:\n' + res);
      },
      error: err => {
        this.templateLoading = false;
        console.error("Template error:", err);
        alert('Template error:\n' + (err.error?.message || err.message));
      }
    });
  }

  runBackwardRecommendations(): void {
    if (!this.isLoggedIn) {
      alert("You must be logged in to run this.");
      return;
    }

    const userId = 1; // or get from authService if available
    this.backwardLoading = true;
    this.backwardService.getRecommendations(userId).subscribe({
      next: res => {
        this.backwardLoading = false;
        console.log("Backward recommendations:", res);
        alert('Backward Recommendations:\n' + JSON.stringify(res, null, 2));
      },
      error: err => {
        this.backwardLoading = false;
        console.error("Backward error:", err);
        alert('Backward error:\n' + (err.error?.message || err.message));
      }
    });
  }

  runConnectedContent(): void {
    if (!this.isLoggedIn) {
      alert("You must be logged in to run this.");
      return;
    }

    const userId = 1; // or dynamic
    this.connectedLoading = true;
    this.backwardService.getConnectedContent(userId).subscribe({
      next: res => {
        this.connectedLoading = false;
        console.log("Connected content:", res);
        alert('Connected content:\n' + JSON.stringify(res, null, 2));
      },
      error: err => {
        this.connectedLoading = false;
        console.error("Connected content error:", err);
        alert('Connected content error:\n' + (err.error?.message || err.message));
      }
    });
  }

}
