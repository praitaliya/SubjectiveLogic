package com.Controller;

import java.util.ArrayList;

import org.hibernate.Session;
import org.hibernate.Transaction;

import com.Model.DiseaseModel;
import com.Util.DBConnection;

public class DAOController {
	ExcelReader reader = null;
	DBConnection db = null;
	Session session = null;
	public void importData() {
		try {
			reader = new ExcelReader();
			db = new DBConnection();
			session = db.getConnection();
			ArrayList<DiseaseModel> data = reader.importData();
			Transaction tr = session.beginTransaction();
			for (DiseaseModel d : data) {
				session.save(d);
			}
			tr.commit();
			session.close();
			System.out.println("Data Imported Successfully");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
