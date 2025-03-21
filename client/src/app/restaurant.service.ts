import { HttpClient } from "@angular/common/http";
import { inject } from "@angular/core";
import { catchError, firstValueFrom, Observable, of, Subject, tap } from "rxjs";
import { MenuItem, OrderDetails } from "./models";

export class RestaurantService {

  private http = inject(HttpClient)
  menuItems = new Subject<MenuItem[]>()

  // TODO: Task 2.2
  // You change the method's signature but not the name
  getMenuItems(): Observable<MenuItem[]> {
    return this.http.get<MenuItem[]>('/api/menu').pipe(
      catchError(error => {
        console.error('Error fetching menu items:', error)
        return of([])
      })
    )
  }

  // TODO: Task 3.2
  submitOrder(orderDetails: OrderDetails): Promise<any> {
    return firstValueFrom(
      this.http.post<any>('/api/food_order', orderDetails)
    )
  }
}

