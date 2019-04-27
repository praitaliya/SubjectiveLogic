package com.Operators;

import com.Exceptions.OperatorExceptions;
import com.Model.OpinionModel;

public class SubjectiveConditions {
	public static final OpinionModel clippedOpinion(double b, double u, double a) throws OperatorExceptions {
		if ((a < 0.0D) || (a > 1.0D)) {
			throw new OperatorExceptions("Atomicity out of range, a: 0 <= a <= 1");
		}
		OpinionModel o = new OpinionModel();
		o.baserate = a;
		double e = constrain(b + a * u);
		double sum = u + b;
		if (u < 0.0D) {
			o.uncertainty = 0.0D;
			o.belief = e;
			o.disbelief = (1.0D - o.belief);
		} else if (b < 0.0D) {
			o.belief = 0.0D;
			o.uncertainty = (e / o.baserate);
			o.disbelief = (1.0D - o.uncertainty);
		} else if (sum > 1.0D) {
			if (a == 1.0D) {
				o.disbelief = 0.0D;
				o.belief = (b / sum);
				o.uncertainty = (u / sum);
			} else {
				o.disbelief = 0.0D;
				o.belief = (a < 1.0D ? (e - a) / (1.0D - a) : e);
				o.uncertainty = (1.0D - o.belief);
			}
		} else {
			o.belief = b;
			o.uncertainty = u;
			o.disbelief = (1.0D - b - u);
		}
		o.adjust();
		o.checkConsistency();

		return o;
	}

	public static double constrain(double x) {
		return Math.min(1.0D, Math.max(0.0D, x));
	}

	public static double adjust(double x) {
		return x >= 1.7976931348623158E297D ? 1.7976931348623158E297D
				: x == Double.NaN ? Double.NaN : Math.round(x * 1.0E11D) / 1.0E11D;
	}

}
