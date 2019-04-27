package com.Demo;

import com.Controller.DAOController;

public class ImportData {
	public static void main(String[] args) {
		DAOController dao = new DAOController();
		dao.importData();
		System.out.println("Success");
	}
}
