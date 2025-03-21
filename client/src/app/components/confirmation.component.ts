import { Component, inject } from '@angular/core';
import { Router } from '@angular/router';
import { RestaurantService } from '../restaurant.service';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-confirmation',
  standalone: false,
  templateUrl: './confirmation.component.html',
  styleUrl: './confirmation.component.css'
})
export class ConfirmationComponent {

  // TODO: Task 5
    private router = inject(Router)
    private restaurantService = inject(RestaurantService)
    private sub!: Subscription 

}
