import { Component, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { CommonModule } from '@angular/common';
import { UserIdService } from './core/user-id.service';
import { UserIdModalComponent } from './core/user-id-modal.component';
import { ToastsComponent } from './core/toasts.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, CommonModule, UserIdModalComponent, ToastsComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  private readonly svc = inject(UserIdService);
  readonly userId = this.svc.userId;
  title = 'ecommerce-frontend';
  openModal() { this.svc.openModal(); }
}
