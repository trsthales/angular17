import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-checkout',
  standalone: true,
  imports: [CommonModule],
  template: `<p>Componente de checkout carregado com &#64;defer.</p>`,
})
export class CheckoutComponent {}
