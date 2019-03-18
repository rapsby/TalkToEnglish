package com.o2o.action.server.db;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class MealMenu {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;

	@Temporal(TemporalType.DATE)
	@Column(unique = true, nullable = false)
	Date serviceDate;

	String salad1;
	String salad2;
	String mrice;
	String dessert;

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "mealMenu")
	List<MealDetail> meals;

	public MealMenu() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getServiceDate() {
		return serviceDate;
	}

	public void setServiceDate(Date serviceDate) {
		this.serviceDate = serviceDate;
	}

	public String getSalad1() {
		return salad1;
	}

	public void setSalad1(String salad1) {
		this.salad1 = salad1;
	}

	public String getSalad2() {
		return salad2;
	}

	public void setSalad2(String salad2) {
		this.salad2 = salad2;
	}

	public String getMrice() {
		return mrice;
	}

	public void setMrice(String mrice) {
		this.mrice = mrice;
	}

	public String getDessert() {
		return dessert;
	}

	public void setDessert(String dessert) {
		this.dessert = dessert;
	}

	public List<MealDetail> getMeals() {
		return meals;
	}

	public void setMeals(List<MealDetail> meals) {
		this.meals = meals;
	}

}
