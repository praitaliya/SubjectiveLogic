package com.Operators;

import java.text.DecimalFormat;
import java.util.ArrayList;
import com.Exceptions.OperatorExceptions;
import com.Model.OpinionModel;

public class SubjectiveOperators {
	public final OpinionModel add(ArrayList<OpinionModel> opinions) throws OperatorExceptions {
		if (opinions == null) {
			throw new NullPointerException("The Opinion must not be null");
		}
		return ProbabilityExpectation(sum(opinions.get(0),opinions.get(1)));
	}

	public final OpinionModel not(OpinionModel o) {
		return ProbabilityExpectation(complement(o));
	}

	public final OpinionModel and(ArrayList<OpinionModel> opinion) {
		if (opinion == null) {
			throw new NullPointerException("Opinion must not be null");
		}
		return ProbabilityExpectation(multiply(opinion.get(0), opinion.get(1)));
	}

	public final OpinionModel unAnd(ArrayList<OpinionModel> opinions) throws OperatorExceptions {
		if (opinions == null) {
			throw new NullPointerException();
		}
		return ProbabilityExpectation(division(opinions.get(0), opinions.get(1)));
	}

	public final OpinionModel subtract(ArrayList<OpinionModel> opinions) {
		if (opinions == null) {
			throw new NullPointerException();
		}
		return ProbabilityExpectation(subtraction(opinions.get(0), opinions.get(1)));
	}

	public final OpinionModel unOr(ArrayList<OpinionModel> opinion) {
		if (opinion == null) {
			throw new NullPointerException();
		}
		OpinionModel coDiv = coDivision(opinion, 0.0D);
		if (coDiv != null) {
			coDiv = ProbabilityExpectation(coDiv);
		} else {
			System.out.println("Co-Division is not possible");
		}
		return coDiv;
	}

	public final OpinionModel or(ArrayList<OpinionModel> opinion) {
		if (opinion == null) {
			throw new NullPointerException();
		}
		return ProbabilityExpectation(coMultiplication(opinion.get(0), opinion.get(1)));
	}

	private OpinionModel coMultiplication(OpinionModel x, OpinionModel y) {
		if ((x == null) || (y == null)) {
			throw new NullPointerException();
		}
		OpinionModel o = new OpinionModel();

		double r = 1.0D;

		x.setDependants();
		y.setDependants();

		o.belief = (x.belief + y.belief - x.belief * y.belief);
		o.baserate = (x.baserate + y.baserate - x.baserate * y.baserate);
		if (o.baserate != 0.0D) {
			o.uncertainty = (x.uncertainty * y.uncertainty
					+ (y.baserate * x.disbelief * y.uncertainty + x.baserate * x.uncertainty * y.disbelief)
							/ o.baserate);
		} else {
			o.uncertainty = (x.uncertainty * y.uncertainty
					+ (x.disbelief * y.uncertainty + r * x.uncertainty * y.disbelief) / (r + 1.0D));
		}
		o.disbelief = (1.0D - o.belief - o.uncertainty);

		o.checkConsistency();

		return o;
	}

	public final OpinionModel abduce(ArrayList<OpinionModel> opinions, double baseRateX) throws OperatorExceptions {
		if (opinions == null) {
			throw new NullPointerException();
		}
		return ProbabilityExpectation(abduction(opinions, baseRateX));
	}

	public final OpinionModel deduce(ArrayList<OpinionModel> opinions) throws OperatorExceptions {
		if (opinions == null) {
			throw new NullPointerException();
		}
		return ProbabilityExpectation(deduction(opinions.get(2), opinions.get(0), opinions.get(1)));
	}

	private OpinionModel abduction(ArrayList<OpinionModel> opinions, double baseRateX) {
		OpinionModel y = opinions.get(2);
		OpinionModel yTx = opinions.get(0);
		OpinionModel yFx = opinions.get(1);
		if ((baseRateX < 0.0D) || (baseRateX > 1.0D)) {
			throw new IllegalArgumentException("Base Rate x, must be: 0 <= x <= 1");
		}
		if ((y == null) || (yTx == null) || (yFx == null)) {
			throw new NullPointerException();
		}
		OpinionModel o = null;
		if (y.baserate == 0.0D) {
			o = new OpinionModel();
			o = createVacuousOpinion(baseRateX);
		} else {
			ArrayList<OpinionModel> conditionals = reverseConditionals(yTx, yFx, baseRateX);
			o = new OpinionModel();
			o = deduction(y, conditionals.get(0), conditionals.get(1));
		}
		return o;
	}

	private ArrayList<OpinionModel> reverseConditionals(OpinionModel yTx, OpinionModel yFx, double baseRateX) {
		ArrayList<OpinionModel> opinions = new ArrayList<OpinionModel>();
		if ((baseRateX < 0.0D) || (baseRateX > 1.0D)) {
			throw new IllegalArgumentException("Base Rate x, must be: 0 <= x <= 1");
		}
		if ((yTx == null) || (yFx == null)) {
			throw new NullPointerException();
		}
		OpinionModel x_br = new OpinionModel();
		x_br = createVacuousOpinion(baseRateX);

		double atom_y = yTx.baserate;
		OpinionModel xFy = new OpinionModel();
		OpinionModel xTy = new OpinionModel();
		if (baseRateX == 0.0D) {
			xTy = createDogmaticOpinion(0.0D, 0.0D);
			xFy = createDogmaticOpinion(0.0D, 0.0D);
		} else {
			if (baseRateX == 1.0D) {
				xTy = createDogmaticOpinion(1.0D, 1.0D);
				xFy = createDogmaticOpinion(1.0D, 1.0D);
			} else {
				if ((atom_y == 0.0D) || (atom_y == 1.0D)) {
					xTy = new OpinionModel(0.0D, 0.0D, 1.0D, baseRateX);
					xFy = new OpinionModel(0.0D, 0.0D, 1.0D, baseRateX);
				} else {
					OpinionModel not_yTx = complement(yTx);
					OpinionModel y_br = deduction(x_br, yTx, yFx);
					OpinionModel not_y_br = complement(y_br);
					OpinionModel y_and_x = multiply(x_br, yTx);
					OpinionModel not_y_and_x = multiply(x_br, not_yTx);
					xTy = division(y_and_x, y_br);
					xFy = division(not_y_and_x, not_y_br);
					opinions.add(xTy);
					opinions.add(xFy);
				}
			}
		}
		return opinions;
	}

	private OpinionModel createDogmaticOpinion(double expectation, double baserate) {
		if ((expectation < 0.0D) || (expectation > 1.0D)) {
			throw new IllegalArgumentException("Expectation e, must be 0 <= e <= 1");
		}
		return new OpinionModel(expectation, 1.0D - expectation, 0.0D, baserate);
	}

	private OpinionModel createVacuousOpinion(double baserate) {
		if ((baserate < 0.0D) || (baserate > 1.0D)) {
			throw new IllegalArgumentException("Expectation e, must be 0 <= e <= 1");
		}
		return new OpinionModel(0.0D, 0.0D, 1.0D, baserate);
	}

	public OpinionModel sum(OpinionModel x, OpinionModel y) {
		OpinionModel sum = new OpinionModel();
		if (x == null || y==null) {
			throw new NullPointerException();
		} 
		double b = 0.0D;
		double u = 0.0D;
		double a = 0.0D;

		b = x.belief + y.belief;
		u = (x.baserate * x.uncertainty + y.baserate * y.uncertainty) / (x.baserate + y.baserate);
		a = x.baserate + y.baserate;
		if (a > 1.0D) {
			a = 1.0D;
			// throw new OperatorExceptions("Illegal operation, Sum of atomicities is
			// greater than 1.0");
		}
		if (a > 0.0D) {
			u /= a;
		}
		sum = SubjectiveConditions.clippedOpinion(b, u, a);
		return sum;
	}

	public OpinionModel subtraction(OpinionModel x, OpinionModel y) {
		OpinionModel sub = new OpinionModel();
		if (x == null && y == null) {
			throw new NullPointerException();
		}
		if (x.getBaserate() - y.getBaserate() < 0.0D) {

			// throw new OperatorExceptions("Illegal operation, Difference of atomicities is
			// less than 0.0");
		}
		double b = 0.0D;
		double u = 0.0D;
		double a = 0.0D;
		x.toSubjectiveOpinion();
		y.toSubjectiveOpinion();

		b = x.belief - y.belief;
		u = (x.baserate * x.uncertainty - y.baserate * y.uncertainty) / (x.baserate - y.baserate);
		;
		a = SubjectiveConditions.constrain(x.baserate - y.baserate);
		if (a != 0.0D) {
			u /= a;
		}
		sub = SubjectiveConditions.clippedOpinion(b, u, a);
		return sub;
	}

	public final OpinionModel division(OpinionModel x, OpinionModel y) throws OperatorExceptions {
		x.setDependants();
		y.setDependants();
		if ((x == null) || (y == null)) {
			throw new NullPointerException();
		}
		if (y.baserate == 0.0D) {
			throw new OperatorExceptions("Atomicity of divisor is zero");
		}
		x.setDependants();
		y.setDependants();
		if (y.probability - x.probability < -1.0E-10D) {
			throw new OperatorExceptions("Expectation of divisor cannot be less than of numerator");
		}
		try {
			double a = x.baserate / y.baserate;
			OpinionModel div;
			if (x.probability == 0.0D) {
				div = new OpinionModel(0.0D, 1.0D, 0.0D, a);
			} else {
				if (a == 1.0D) {
					div = new OpinionModel(1.0D, 0.0D, 0.0D, a);
				} else {
					double e = x.probability / y.probability;

					double d = SubjectiveConditions.constrain((x.disbelief - y.disbelief) / (1.0D - y.disbelief));
					double u = ((y.baserate * (1 - x.disbelief)) / ((y.baserate - x.baserate) * (1 - y.disbelief)))
							- ((y.baserate * e) / (y.baserate - x.baserate));
					double b = 1.0D - d - u;

					div = SubjectiveConditions.clippedOpinion(b, u, a);
				}
			}
			div.checkConsistency();
			return div;
		} catch (ArithmeticException ae) {
			throw new OperatorExceptions(ae.getMessage());
		}
	}

	private final OpinionModel coDivision(ArrayList<OpinionModel> opinions, double r) {
		OpinionModel x = opinions.get(0);
		OpinionModel y = opinions.get(1);
		if ((x == null) || (y == null)) {
			throw new NullPointerException();
		}
		if ((r < 0.0D) || (r > 1.0D)) {
			throw new IllegalArgumentException("Limiting value, r, must be: 0<= r <=1");
		}
		OpinionModel coDiv = new OpinionModel();

		x.setDependants();
		y.setDependants();
		try {
			coDiv.belief = ((x.belief - y.belief) / (1.0D - y.belief));
			coDiv.baserate = ((x.baserate - y.baserate) / (1.0D - y.baserate));
			if (x.baserate > y.baserate) {
				coDiv.uncertainty = (((1.0D - x.belief) / (1.0D - y.belief)
						- (x.disbelief + (1.0D - x.baserate) * x.uncertainty)
								/ (y.disbelief + (1.0D - y.baserate) * y.uncertainty))
						* (1.0D - y.baserate) / (x.baserate - y.baserate));

				coDiv.disbelief = (((1.0D - y.baserate) * (x.disbelief + (1.0D - x.baserate) * x.uncertainty)
						/ (y.disbelief + (1.0D - y.baserate) * y.uncertainty)
						- (1.0D - x.baserate) * (1.0D - x.belief) / (1.0D - y.baserate)) / (x.baserate - y.baserate));
			} else {
				coDiv.disbelief = (r * (1.0D - x.belief) / (1.0D - y.belief));
				coDiv.uncertainty = ((1.0D - r) * (1.0D - x.belief) / (1.0D - y.belief));
			}
			coDiv.checkConsistency();
			return coDiv;
		} catch (OperatorExceptions e) {
			return null;
		} catch (ArithmeticException ae) {
			return null;
		}
	}

	private static final OpinionModel complement(OpinionModel x) {
		if (x == null) {
			throw new NullPointerException();
		}
		synchronized (x) {
			OpinionModel o = new OpinionModel();

			o.belief = x.disbelief;
			o.disbelief = x.belief;
			o.uncertainty = x.uncertainty;
			o.baserate = (1.0D - x.baserate);

			o.checkConsistency();
			return o;
		}
	}

	public final OpinionModel multiply(OpinionModel x, OpinionModel y) {
		if ((x == null) || (y == null)) {
			throw new NullPointerException();
		}
		OpinionModel o = new OpinionModel();

		x.setDependants();
		y.setDependants();

		double divisor = 1.0D;
		double r = 1.0D;
		double expec = 0.0D;

		o.disbelief = (x.disbelief + y.disbelief - x.disbelief * y.disbelief);
		o.baserate = x.baserate * y.baserate;
		expec = x.probability * (y.belief + y.baserate * y.uncertainty);
		divisor = 1.0D - o.baserate;
		if (divisor != 0.0D) {
			o.belief = (((o.disbelief - 1.0D) * o.baserate + expec) / divisor);
			o.uncertainty = (-(o.disbelief - 1.0D + expec) / divisor);
		} else {
			o.belief = (x.belief * y.belief + (r * x.belief * y.uncertainty + x.uncertainty * y.belief) / (r + 1.0D));
			o.uncertainty = ((x.belief * y.uncertainty + r * y.belief * x.uncertainty) / (r + 1.0D)
					+ x.uncertainty * y.uncertainty);
		}
		o.adjust();
		o.checkConsistency();

		return o;
	}

	public final OpinionModel deduction(OpinionModel x, OpinionModel yTx, OpinionModel yFx) throws OperatorExceptions {
		if ((x == null) || (yTx == null) || (yFx == null)) {
			throw new NullPointerException();
		}
		if (Math.abs(yTx.baserate - yFx.baserate) > 1.0E-10D) {
			throw new OperatorExceptions("The atomicities of both sub-conditionals must be equal");
		}
		x.setDependants();

		OpinionModel I = new OpinionModel();

		I.baserate = yTx.baserate;
		I.belief = (x.belief * yTx.belief + x.disbelief * yFx.belief
				+ x.uncertainty * (yTx.belief * x.baserate + yFx.belief * (1.0D - x.baserate)));
		I.disbelief = (x.belief * yTx.disbelief + x.disbelief * yFx.disbelief
				+ x.uncertainty * (yTx.disbelief * x.baserate + yFx.disbelief * (1.0D - x.baserate)));
		I.uncertainty = (x.belief * yTx.uncertainty + x.disbelief * yFx.uncertainty
				+ x.uncertainty * (yTx.uncertainty * x.baserate + yFx.uncertainty * (1.0D - x.baserate)));

		I.setDependants();
		OpinionModel y = new OpinionModel();
		if (((yTx.belief >= yFx.belief) && (yTx.disbelief >= yFx.disbelief))
				|| ((yTx.belief <= yFx.belief) && (yTx.disbelief <= yFx.disbelief))) {
			y = I;
		} else {
			double expec = yTx.belief * x.baserate + yFx.belief * (1.0D - x.baserate)
					+ yTx.baserate * (yTx.uncertainty * x.baserate + yFx.uncertainty * (1.0D - x.baserate));

			boolean case_II = (yTx.belief > yFx.belief) && (yTx.disbelief < yFx.disbelief);

			boolean case_1 = x.probability <= x.baserate;
			double k;
			if (case_II) {
				boolean case_A = expec <= yFx.belief + yTx.baserate * (1.0D - yFx.belief - yTx.disbelief);
				if (case_A) {
					if (case_1) {
						double divisor;
						if ((divisor = x.probability * yTx.baserate) > 0.0D) {
							k = x.baserate * x.uncertainty * (I.belief - yFx.belief) / divisor;
						} else {
							k = I.belief - yFx.belief;
						}
					} else {
						double divisor;
						if ((divisor = (x.disbelief + (1.0D - x.baserate) * x.uncertainty) * yTx.baserate
								* (yFx.disbelief - yTx.disbelief)) > 0.0D) {
							k = x.baserate * x.uncertainty * (I.disbelief - yTx.disbelief) * (yTx.belief - yFx.belief)
									/ divisor;
						} else {
							k = (I.disbelief - yTx.disbelief) * (yTx.belief - yFx.belief);
						}
					}
				} else {
					if (case_1) {
						double divisor;
						if ((divisor = x.baserate * (1.0D - yTx.baserate) * (yTx.belief - yFx.belief)) > 0.0D) {
							k = (1.0D - x.baserate) * x.uncertainty * (I.belief - yFx.belief)
									* (yFx.disbelief - yTx.disbelief) / divisor;
						} else {
							k = (I.belief - yFx.belief) * (yFx.disbelief - yTx.disbelief);
						}
					} else {
						double divisor;
						if ((divisor = (x.disbelief + (1.0D - x.baserate) * x.uncertainty)
								* (1.0D - yTx.baserate)) > 0.0D) {
							k = (1.0D - x.baserate) * x.uncertainty * (I.disbelief - yTx.disbelief) / divisor;
						} else {
							k = I.disbelief - yTx.disbelief;
						}
					}
				}
			} else {
				boolean case_A = expec <= yTx.belief + yTx.baserate * (1.0D - yTx.belief - yFx.disbelief);
				if (case_A) {
					if (case_1) {
						double divisor;
						if ((divisor = x.probability * yTx.baserate * (yTx.disbelief - yFx.disbelief)) > 0.0D) {
							k = (1.0D - x.baserate) * x.uncertainty * (I.disbelief - yFx.disbelief)
									* (yFx.belief - yTx.belief) / divisor;
						} else {
							k = (I.disbelief - yFx.disbelief) * (yFx.belief - yTx.belief);
						}
					} else {
						double divisor;
						if ((divisor = (x.disbelief + (1.0D - x.baserate) * x.uncertainty) * yTx.baserate) > 0.0D) {
							k = (1.0D - x.baserate) * x.uncertainty * (I.belief - yTx.belief) / divisor;
						} else {
							k = I.belief - yTx.belief;
						}
					}
				} else {
					if (case_1) {
						double divisor;
						if ((divisor = x.probability * (1.0D - yTx.baserate)) > 0.0D) {
							k = x.baserate * x.uncertainty * (I.disbelief - yFx.disbelief) / divisor;
						} else {
							k = I.disbelief - yFx.disbelief;
						}
					} else {
						double divisor;
						if ((divisor = (x.disbelief + (1.0D - x.baserate) * x.uncertainty) * (1.0D - yTx.baserate)
								* (yFx.belief - yTx.belief)) > 0.0D) {
							k = x.baserate * x.uncertainty * (I.belief - yTx.belief) * (yTx.disbelief - yFx.disbelief)
									/ divisor;
						} else {
							k = (I.belief - yTx.belief) * (yTx.disbelief - yFx.disbelief);
						}
					}
				}
			}
			y = new OpinionModel();
			y.baserate = yTx.baserate;

			y.belief = SubjectiveConditions.adjust(I.belief - k * y.baserate);
			y.disbelief = SubjectiveConditions.adjust(I.disbelief - k * (1.0D - y.baserate));
			y.uncertainty = SubjectiveConditions.adjust(I.uncertainty + k);

			y.checkConsistency();
		}

		return y;
	}

	public OpinionModel ProbabilityExpectation(OpinionModel opinion) {
		double probability = 0.0D;
		probability = opinion.getBelief() + (opinion.getBaserate() * opinion.getUncertainty());
		opinion.setProbability(probability);
		return opinion;
	}

	public String getStringOpinion(OpinionModel opinion) {
		DecimalFormat df = new DecimalFormat("#.#####");
		df.setMaximumFractionDigits(5);
		String str = "";
		if (opinion != null) {
			str = "[Belief : " + df.format(opinion.getBelief()) + " Disbelief : " + df.format(opinion.getDisbelied())
					+ " Uncertainty : " + df.format(opinion.getUncertainty()) + " Baserate : "
					+ df.format(opinion.getBaserate()) + " Probability : " + df.format(opinion.getProbability()) + " ]";
		}
		return str;
	}
}
