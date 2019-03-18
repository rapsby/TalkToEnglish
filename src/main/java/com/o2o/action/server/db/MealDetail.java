package com.o2o.action.server.db;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class MealDetail {
	public enum MealType {
		LAUNCH_1, LAUNCH_2, DINNER
	};

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	long id;

	@ManyToOne(optional = false)
	@JoinColumn
	@JsonIgnore
	MealMenu mealMenu;

	@Column(nullable = false)
	MealType mealType;

	String menu1;
	String menu2;
	String menu3;
	String menu4;
	String menu5;
	String menu6;
	String menu7;

	public MealDetail() {
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public MealMenu getMealMenu() {
		return mealMenu;
	}

	public void setMealMenu(MealMenu mealMenu) {
		this.mealMenu = mealMenu;
	}

	public MealType getMealType() {
		return mealType;
	}

	public void setMealType(MealType mealType) {
		this.mealType = mealType;
	}

	public String getMenu1() {
		return menu1;
	}

	public void setMenu1(String menu1) {
		this.menu1 = menu1;
	}

	public String getMenu2() {
		return menu2;
	}

	public void setMenu2(String menu2) {
		this.menu2 = menu2;
	}

	public String getMenu3() {
		return menu3;
	}

	public void setMenu3(String menu3) {
		this.menu3 = menu3;
	}

	public String getMenu4() {
		return menu4;
	}

	public void setMenu4(String menu4) {
		this.menu4 = menu4;
	}

	public String getMenu5() {
		return menu5;
	}

	public void setMenu5(String menu5) {
		this.menu5 = menu5;
	}

	public String getMenu6() {
		return menu6;
	}

	public void setMenu6(String menu6) {
		this.menu6 = menu6;
	}

	public String getMenu7() {
		return menu7;
	}

	public void setMenu7(String menu7) {
		this.menu7 = menu7;
	}

}
