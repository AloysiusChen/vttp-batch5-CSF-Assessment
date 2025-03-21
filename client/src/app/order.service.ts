import { MenuItem } from "./models"

export class OrderService {
    private selectedItems: MenuItem[] = []

    setSelectedItems(items: MenuItem[]): void {
        this.selectedItems = items
    }

    getSelectedItems(): MenuItem[] {
        return this.selectedItems
    }

    clearOrder(): void {
        this.selectedItems = []
    }
}
