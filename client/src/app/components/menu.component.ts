import { Component, inject, OnInit } from '@angular/core';
import { RestaurantService } from '../restaurant.service';
import { Observable, Subscription } from 'rxjs';
import { MenuItem } from '../models';
import { OrderService } from '../order.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-menu',
  standalone: false,
  templateUrl: './menu.component.html',
  styleUrl: './menu.component.css'
})
export class MenuComponent implements OnInit{
  // TODO: Task 2
  menuItems$!: Observable<MenuItem[]>
  menuItems: MenuItem[] = []

  private restaurantService = inject(RestaurantService)
  private orderService = inject(OrderService)  
  private router = inject(Router)

  ngOnInit() {
    this.menuItems$ = this.restaurantService.getMenuItems()

    this.menuItems$.subscribe(items => {
      this.menuItems = items.map(item => ({
        ...item,
        quantity: 0
      }))
    })
  }

  addItem(item: MenuItem): void {
    const storedItem = this.menuItems.find(i => i.id === item.id)
    if (storedItem) {
      storedItem.quantity = (storedItem.quantity || 0) + 1
    }
  }

  removeItem(item: MenuItem): void {
    const storedItem = this.menuItems.find(i => i.id === item.id)
    if (storedItem && storedItem.quantity && storedItem.quantity > 0) {
      storedItem.quantity--
    }
  }

  getQuantity(itemId: number): number {
    const item = this.menuItems.find(i => i.id === itemId)
    return item?.quantity || 0
  }

  getTotalItems(): number {
    return this.menuItems.reduce((total, item) => total + (item.quantity || 0), 0)
  }

  emptyCart(): boolean {
    if(this.getTotalItems() > 0)
      return false

    return true
  }

  getTotalCost(): number {
    return this.menuItems.reduce((total, item) =>
      total + ((item.quantity || 0) * item.price), 0)
  }

  getSelectedItems(): MenuItem[] {
    return this.menuItems.filter(item => item.quantity && item.quantity > 0)
  }

  placeOrder(): void {
    console.log('Place order clicked')
    const selectedItems = this.getSelectedItems()  
    console.log('Selected items:', selectedItems)
    this.orderService.setSelectedItems(selectedItems)
    console.log('Navigating to place-order')
    this.router.navigate(['/place-order'])
  }
}



