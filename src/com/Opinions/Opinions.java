package com.Opinions;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import com.Model.OpinionModel;
import com.Operators.SubjectiveOperators;
import com.Util.DBConnection;

public class Opinions {
	SubjectiveOperators subOperator = null;
	static final int W = 2;
	DBConnection db = null;
	Connection conn = null;
	double belief = 0;
	double disbelief = 0;
	double uncertainty = 0;
	double baserate = 0.5;

	public OpinionModel getOpinions(int choice, String text) {
		subOperator = new SubjectiveOperators();
		// HashMap<String, Double> opinion = new HashMap<String, Double>();
		OpinionModel opinion = new OpinionModel();
		ArrayList<Double> data = new ArrayList<Double>();
		try {
			if (text.trim() != null) {
				if (choice == 3) {
					int agegroup = Integer.parseInt(text);
					System.out.println("Age Group : " + agegroup);
					if (agegroup == 1) {
						text = "Children";
					} else if (agegroup == 2) {
						text = "Youth";
					} else if (agegroup == 3) {
						text = "Adults";
					} else if (agegroup == 4) {
						text = "Seniors";
					}
				}
				System.out.println("Text : " + text);
				data = getBelief(choice, text);
			}

			opinion.setBelief(data.get(0));
			opinion.setDisbelied(data.get(1));
			opinion.setUncertainty((1 - (data.get(0) + data.get(1))));
			opinion.setBaserate(0.5D);
			opinion = subOperator.ProbabilityExpectation(opinion);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return opinion;
	}

	public ArrayList<Double> getBelief(int choice, String text) {
		ArrayList<Double> data = new ArrayList<Double>();
		try {
			String sql = "";
			double count = 0, totalCount = 0;
			double belief = 0, disbelief = 0;
			db = new DBConnection();
			conn = db.getJDBCConnection();
			if (choice == 1) {
				sql = "SELECT (SELECT COUNT(*) FROM disease WHERE primarydisease LIKE ? AND primarydisease!=\"\") AS cnt, (SELECT COUNT(*) FROM disease WHERE primarydisease!=\"\") AS totalcount";
			} else if (choice == 2) {
				sql = "SELECT (SELECT COUNT(*) FROM disease WHERE secondarydisease LIKE ? AND secondarydisease!=\"\") AS cnt, (SELECT COUNT(*) FROM disease WHERE secondarydisease!=\"\") AS totalcount";
			} else if (choice == 3) {
				sql = "SELECT (SELECT COUNT(*) FROM disease WHERE agegroup LIKE ? AND agegroup!=\"\") AS cnt, (SELECT COUNT(*) FROM disease WHERE agegroup!=\"\") AS totalcount";
			} else if (choice == 4) {
				sql = "SELECT (SELECT COUNT(*) FROM disease WHERE gender LIKE ? AND gender!=\"\") AS cnt, (SELECT COUNT(*) FROM disease WHERE gender!=\"\") AS totalcount";
			}
			PreparedStatement pst = conn.prepareStatement(sql);
			pst.setString(1, "%" + text + "%");
			ResultSet rs = pst.executeQuery();
			if (rs.next()) {
				count = rs.getInt("cnt");
				totalCount = rs.getInt("totalCount");
			}
			belief = (count) / (W + count + (totalCount - count));
			if (belief == 0) {
				disbelief = 1;
			} else {
				disbelief = (totalCount - count) / (W + count + (totalCount - count));
			}
			data.add(belief);
			data.add(disbelief);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}
}
