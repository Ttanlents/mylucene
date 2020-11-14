package com.yjf.mylucene.entity;

import javax.persistence.*;
import java.util.Objects;

/**
 * @author 余俊锋
 * @date 2020/11/13 10:51
 * @Description
 */
@Entity
@Table(name = "goods", schema = "test", catalog = "")
public class Goods {
    private int id;
    private String name;
    private String title;
    private Double price;
    private String pic;

    @Id
    @Column(name = "id", nullable = false)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Basic
    @Column(name = "name", nullable = true, length = 255)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Basic
    @Column(name = "title", nullable = true, length = 255)
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Basic
    @Column(name = "price", nullable = true, precision = 2)
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Basic
    @Column(name = "pic", nullable = true, length = 255)
    public String getPic() {
        return pic;
    }



    public void setPic(String pic) {
        this.pic = pic;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goods that = (Goods) o;
        return id == that.id &&
                Objects.equals(name, that.name) &&
                Objects.equals(title, that.title) &&
                Objects.equals(price, that.price) &&
                Objects.equals(pic, that.pic);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, title, price, pic);
    }
}
