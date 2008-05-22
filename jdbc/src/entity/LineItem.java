package entity;

import java.math.*;

public class LineItem
{
    private BigDecimal quantity;
    private Product product;
    private BigDecimal price;
    private Invoice invoice;

    public LineItem(BigDecimal quantity, Product product, BigDecimal price, Invoice invoice)
    {
        this.quantity = quantity;
        this.product = product;
        this.price = price;
        this.invoice = invoice;
        invoice.updateSubtotal();
    }

    public BigDecimal getQuantity()
    {
        return quantity;
    }

    public void setQuantity(BigDecimal quantity)
    {
        this.quantity = quantity;
        invoice.updateSubtotal();
    }

    public Product getProduct()
    {
        return product;
    }

    public void setProduct(Product product)
    {
        this.product = product;
    }

    public BigDecimal getPrice()
    {
        return price;
    }

    public void setPrice(BigDecimal price)
    {
        this.price = price;
        invoice.updateSubtotal();
    }
}
