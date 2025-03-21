// You may use this file to create any models
export interface MenuItem {
  id: number,
  name: string,
  description: string,
  price: number
  quantity?: number
}

export interface SimplifiedMenuItems {
  id: number,
  price: number
  quantity: number
}

export interface OrderDetails {
  username:string
  password:string
  items: SimplifiedMenuItems[]
}

