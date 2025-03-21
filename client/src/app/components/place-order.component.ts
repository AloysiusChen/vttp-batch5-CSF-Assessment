import { Component, inject } from '@angular/core';
import { OrderService } from '../order.service';
import { MenuItem, OrderDetails, SimplifiedMenuItems } from '../models';
import { Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RestaurantService } from '../restaurant.service';
import { Subject } from 'rxjs';

@Component({
  selector: 'app-place-order',
  standalone: false,
  templateUrl: './place-order.component.html',
  styleUrl: './place-order.component.css'
})
export class PlaceOrderComponent {

  // TODO: Task 3
  private orderService = inject(OrderService)
  private router = inject(Router)
  private fb = inject(FormBuilder)
  private restaurantService = inject(RestaurantService)
  orderItems: MenuItem[] = [] 
  orderForm!: FormGroup
  simplifiedItems: SimplifiedMenuItems[] = []

  private orderResponseSubject = new Subject<any>()
  
  ngOnInit(): void {
    this.orderItems = this.orderService.getSelectedItems()

    this.orderForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    })

    this.createSimplifiedItems()
  }

  getTotalCost(): number {
    return this.orderItems.reduce((total, item) => 
      total + ((item.quantity || 0) * item.price), 0)
  }

  startOver(): void {
    this.orderService.clearOrder
    this.router.navigate(['/'])
  }

  createSimplifiedItems(): void {
    this.simplifiedItems = this.orderItems.map(item => ({
      id: item.id,
      price: item.price,
      quantity: item.quantity || 0
    }))
  }

  protected processForm(): void {
    const orderDetails: OrderDetails = {
      username: this.orderForm.value.username,
      password: this.orderForm.value.password,
      items: this.simplifiedItems
    }
    
    this.restaurantService.submitOrder(orderDetails)
    .then(response => {
      console.log('Order submitted successfully', response)
      this.router.navigate(['/confirmation'])
    })
    .catch(error => {      
      if (error.status === 401) {
        alert(error.error.message)
      } else {
        alert('Error submitting your order.')
      }
    });
  }
}

