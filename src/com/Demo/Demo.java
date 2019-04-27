package com.Demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.Controller.DAOController;
import com.Model.OpinionModel;
import com.Operators.SubjectiveOperators;
import com.Opinions.Opinions;

public class Demo {
	static SubjectiveOperators op = null;
	static Scanner sc = new Scanner(System.in);

	public static void main(String[] args) {
		op = new SubjectiveOperators();
		// DAOController dao = new DAOController();
		// dao.importData();
		Opinions o = new Opinions();
		boolean mainloop = true;
		ArrayList<OpinionModel> opinions = new ArrayList<OpinionModel>();
		try {
			while (mainloop) {
				System.out.println("Select from Below : ");
				System.out.println("1 : Enter Manual Opinions");
				System.out.println("2 : Select from Dataset");
				System.out.println("3 : Deducation");
				System.out.println("4 : Abducation");
				System.out.println("5 : Complex Binomial Expressions");
				System.out.println("6 : Exit");
				int ch = 0;
				ch = Integer.parseInt(sc.nextLine());
				OpinionModel opinion;
				switch (ch) {
				case 1:
					opinions = getManualOpinions(2);
					calculateOpinions(opinions);
					break;
				case 2:
					opinions = getDatasetOpinions();
					calculateOpinions(opinions);
					break;
				case 3:
					opinions = getDeductionOpinions();
					System.out.println("Opinion about y given x is true : "
							+ op.getStringOpinion(op.ProbabilityExpectation(opinions.get(0))));
					System.out.println("opinion about y given x is false : "
							+ op.getStringOpinion(op.ProbabilityExpectation(opinions.get(1))));
					System.out.println(
							"Opinion about x : " + op.getStringOpinion(op.ProbabilityExpectation(opinions.get(2))));
					opinion = op.deduce(opinions);
					System.out.println("Deduction : " + op.getStringOpinion(opinion));
					break;
				case 4:
					opinions = getAbductionOpinions();
					System.out.println("Enter Baserate : ");
					double baserate = Double.parseDouble(sc.nextLine());
					System.out.println("Positive Opinion of y given x is true : "
							+ op.getStringOpinion(op.ProbabilityExpectation(opinions.get(0))));
					System.out.println("Negative Opinion of y given x is false : "
							+ op.getStringOpinion(op.ProbabilityExpectation(opinions.get(1))));
					System.out.println(
							"Opinion about y : " + op.getStringOpinion(op.ProbabilityExpectation(opinions.get(2))));
					opinion = op.abduce(opinions, baserate);
					System.out.println("Abduction : " + op.getStringOpinion(opinion));
					break;
				case 5:
					opinions = getManualOpinions(3);
					opinion = new OpinionModel();
					String str = "";
					int operator = 0;
					for (int i = 1; i <= 2; i++) {
						System.out.println("Select " + i + " Operator : ");
						System.out.println("1 : Addition.");
						System.out.println("2 : Subtraction.");
						System.out.println("3 : Multiplication.");
						System.out.println("4 : Division.");
						operator = Integer.parseInt(sc.nextLine());
						if (i == 1) {
							switch (operator) {
							case 1:
								opinion = op.sum(opinions.get(0), opinions.get(1));
								str = "((Opinion 1 ADD Opinion 2)";
								break;
							case 2:
								opinion = op.subtraction(opinions.get(0), opinions.get(1));
								str = "((Opinion 1 SUB Opinion 2)";
								break;
							case 3:
								opinion = op.multiply(opinions.get(0), opinions.get(1));
								str = "((Opinion 1 AND Opinion 2)";
								break;
							case 4:
								opinion = op.division(opinions.get(0), opinions.get(1));
								str = "((Opinion 1 OR Opinion 2)";
								break;
							default:
								System.out.println("Invalid Operator : ");
								break;
							}
						}else {
							switch (operator) {
							case 1:
								opinion = op.sum(opinion, opinions.get(2));
								str += " SUM Opinion 3)";
								break;
							case 2:
								opinion = op.subtraction(opinion, opinions.get(2));
								str += " SUB Opinion 3)";
								break;
							case 3:
								opinion = op.multiply(opinion, opinions.get(2));
								str += " AND Opinion 3)";
								break;
							case 4:
								opinion = op.division(opinion, opinions.get(2));
								str += " OR Opinion 3)";
								break;
							default:
								System.out.println("Invalid Operator : ");
								break;
							}
						}
					}
					System.out.println(str + " : " + op.getStringOpinion(op.ProbabilityExpectation(opinion)));
					break;
				default:
					break;
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static ArrayList<OpinionModel> getAbductionOpinions() {
		op = new SubjectiveOperators();
		ArrayList<OpinionModel> opinions = new ArrayList<OpinionModel>();
		for (int i = 1; i <= 3; i++) {
			if (i == 1) {
				System.out.println("Enter Positive Opinion of y given x is true : ");
			} else if (i == 2) {
				System.out.println("Enter Negative Opinion of y given x is false : ");
			} else if (i == 3) {
				System.out.println("Enter Opinion about y : ");
			} else {

			}
			System.out.println("");
			System.out.println("Belief : ");
			double b = Double.parseDouble(sc.nextLine());
			System.out.println("Disbelief : ");
			double d = Double.parseDouble(sc.nextLine());
			System.out.println("Baserate : ");
			double a = Double.parseDouble(sc.nextLine());
			OpinionModel opinion = new OpinionModel(b, d, a);
			// System.out.println("Opinion for operator : " +
			// op.getStringOpinion(op.ProbabilityExpectation(opinion)));
			opinions.add(opinion);
		}
		return opinions;
	}

	private static ArrayList<OpinionModel> getDeductionOpinions() {
		op = new SubjectiveOperators();
		ArrayList<OpinionModel> opinions = new ArrayList<OpinionModel>();
		for (int i = 1; i <= 3; i++) {
			if (i == 1) {
				System.out.println("Enter opinion about y given x is true : ");
			} else if (i == 2) {
				System.out.println("Enter opinion about y given x is false : ");
			} else {
				System.out.println("Enter Opinion about x : ");
			}

			System.out.println("");
			System.out.println("Belief : ");
			double b = Double.parseDouble(sc.nextLine());
			System.out.println("Disbelief : ");
			double d = Double.parseDouble(sc.nextLine());
			System.out.println("Baserate : ");
			double a = Double.parseDouble(sc.nextLine());
			OpinionModel opinion = new OpinionModel(b, d, a);
			// System.out.println("Opinion for operator : " +
			// op.getStringOpinion(op.ProbabilityExpectation(opinion)));
			opinions.add(opinion);
		}
		return opinions;
	}

	public static void calculateOpinions(ArrayList<OpinionModel> opinions) {
		op = new SubjectiveOperators();
		if (opinions.size() == 2) {
			System.out.println("First Opinion : " + op.getStringOpinion(op.ProbabilityExpectation(opinions.get(0))));
			System.out.println("Second Opinion : " + op.getStringOpinion(op.ProbabilityExpectation(opinions.get(1))));
			System.out.println("Sum of opinions : " + op.getStringOpinion(op.add(opinions)));
			System.out.println("Subtraction of opinions : " + op.getStringOpinion(op.subtract(opinions)));
			System.out.println("Division of opinions : " + op.getStringOpinion(op.unAnd(opinions)));
			System.out.println("Co-Division of opinions : " + op.getStringOpinion(op.unOr(opinions)));
			System.out.println("Multiplication of opinions : " + op.getStringOpinion(op.and(opinions)));
			System.out.println("Co-Multiplication of opinions : " + op.getStringOpinion(op.or(opinions)));
		}
	}

	private static ArrayList<OpinionModel> getDatasetOpinions() {
		op = new SubjectiveOperators();
		ArrayList<OpinionModel> opinions = new ArrayList<OpinionModel>();
		Opinions o = new Opinions();
		for (int i = 1; i <= 2; i++) {
			System.out.println("Select " + i + " Opinion : ");
			int choice = 0;
			System.out.println("");
			System.out.println("1 :-> Primary Disease");
			System.out.println("2 :-> Secondary Disease");
			System.out.println("3 :-> Age Group");
			System.out.println("4 :-> Gender");
			System.out.println("Select Column");
			choice = Integer.parseInt(sc.nextLine());
			OpinionModel opinion = new OpinionModel();
			switch (choice) {
			case 1:
				System.out.println("Get Opinion for Primary Disease : ");
				String primaryDisease = sc.nextLine();
				opinion = o.getOpinions(choice, primaryDisease);
				System.out.println(
						"Opinion for Primary Disease : " + primaryDisease + " : " + op.getStringOpinion(opinion));
				opinions.add(opinion);
				break;
			case 2:
				System.out.println("Get Opinion for Secondary Disease : ");
				String secondaryDisease = sc.nextLine();
				opinion = o.getOpinions(choice, secondaryDisease);
				System.out.println(
						"Opinion for Secondary Disease : " + secondaryDisease + " : " + op.getStringOpinion(opinion));
				opinions.add(opinion);
				break;
			case 3:
				System.out.println("Get Opinion for Age Group : ");
				System.out.println("Select from below : ");
				System.out.println("1 : Children (00 - 14 yrs) ");
				System.out.println("2 : Youth (15 - 24 yrs) ");
				System.out.println("3 : Adult (25 - 64 yrs) ");
				System.out.println("4 : Seniors (65 and more yrs) ");
				String agegroup = sc.nextLine();
				opinion = o.getOpinions(choice, agegroup);
				System.out.println("Opinion for Agegroup : " + agegroup + " : " + op.getStringOpinion(opinion));
				opinions.add(opinion);
				break;
			case 4:
				System.out.println("Get Opinion for Gender : ");
				String gender = sc.nextLine();
				opinion = o.getOpinions(choice, gender);
				System.out.println("Opinion for gender : " + gender + " : " + op.getStringOpinion(opinion));
				opinions.add(opinion);
				break;
			default:
				break;
			}
		}
		return opinions;
	}

	private static ArrayList<OpinionModel> getManualOpinions(int n) {
		op = new SubjectiveOperators();
		ArrayList<OpinionModel> opinions = new ArrayList<OpinionModel>();
		for (int i = 1; i <= n; i++) {
			System.out.println("Enter " + i + " Opinion : ");
			System.out.println("");
			System.out.println("Belief : ");
			double b = Double.parseDouble(sc.nextLine());
			System.out.println("Disbelief : ");
			double d = Double.parseDouble(sc.nextLine());
			System.out.println("Baserate : ");
			double a = Double.parseDouble(sc.nextLine());
			OpinionModel opinion = new OpinionModel(b, d, a);
			// System.out.println("Opinion for operator : " +
			// op.getStringOpinion(op.ProbabilityExpectation(opinion)));
			opinions.add(opinion);
		}
		return opinions;
	}
}
