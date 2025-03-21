package vttp.batch5.csf.assessment.server.model;

public class Menu {
    private String _id;
    private String name;
    private double price;
    private String description;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Menu() {
    }

    public Menu(String _id, String name, double price, String description) {
        this._id = _id;
        this.name = name;
        this.price = price;
        this.description = description;
    }

}
