package ch13;

import java.util.ArrayList;

//동기화 (synchronized만 사용하고 wait,notify 미사용시)
public class Ex13_14 {

	public static void main(String[] args) throws Exception {
		Table table = new Table(); // 여러 쓰레드가 공유하는 객체
		Runnable r = new Cook(table);
		Thread th1 = new Thread(r,"Cook");
		th1.start();
		
		Runnable r1 = new Customer(table,"donut");
		Thread th2 = new Thread(r1,"CUST1");
		th2.start();
//		new Thread(new Customer(table,"burger"),"CUST2").start();
		

		Runnable r2 = new Customer(table,"burger");
		Thread th3 = new Thread(r1,"CUST2");
		th3.start();
		
		Thread.sleep(5000);
		
		System.exit(0); //프로그램 종료 
		
		
	}

}
class Customer implements Runnable {
	
	private Table table;
	private String food;
	
	Customer(Table table, String food){
		this.table =table;
		this.food = food;
	}
	
	public void run() {
		while(true) {
			try {
				Thread.sleep(10);
			}catch(Exception e) {
				;
			}
			String name = Thread.currentThread().getName();
			if(eatFood()) {
				System.out.println(name + " ate a " + food);
			}else {
				System.out.println(name + " failed to eat. : (");
			}
		}
	}
	boolean eatFood() {
		return table.remove(food);
	}
}

class Table{ // Cook과 복수의 Customer가 공동으로 사용하는 자원 
	String[] dishNames = {"donut","donut","burger"};
	
	final int MAX_FOOD = 6;
	
	private ArrayList<String> dishes = new ArrayList<>();
	
	public synchronized void add(String dish) {
		if(dishes.size() >= MAX_FOOD) {
			return;
		}
		dishes.add(dish); //add는 ArrayList의 add로 원소 추가
		
		System.out.println("Dishes :" + dishes.toString());
		
	}
	public boolean remove(String dishName) {
		synchronized(this) {
			while(dishes.size()==0) {
				String name = Thread.currentThread().getName();
				System.out.println(name + "is waiting.");
				try {
					Thread.sleep(500);
				}catch(Exception e ) {
					e.printStackTrace();
				}
			}
			for (int i = 0; i < dishes.size(); i++) {
				if(dishName.equals(dishes.get(i))) {
					dishes.remove(i); //ArrayList의 remove 
					return true;
				}
			}
		}
		return false;
	}
	public int disNum() {
		return dishNames.length;
	}
}

class Cook implements Runnable {
	private Table table;
	Cook(Table table){ // 생성자를 선언하면 기본형은 사라져 버림 
		this.table = table;
	}
	
	public void run() {
		while(true) {
			int idx = (int)(Math.random()* table.disNum());
			table.add(table.dishNames[idx]);
			try {
				Thread.sleep(100);
			}
			catch(Exception e) {
				;
			}
		}
	}
}
