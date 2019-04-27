package com.Model;

import com.Exceptions.OperatorExceptions;
import com.Operators.SubjectiveConditions;

public class OpinionModel {
	public double belief = 0.0D;
	public double disbelief = 0.0D;
	public double uncertainty = 0.0D;
	public double baserate = 0.5D;
	public double probability = 0.0D;

	public OpinionModel() {

	}

	public OpinionModel(double b, double d, double u, double a) {
		this.belief = b;
		this.disbelief = d;
		this.baserate = a;
		this.uncertainty = u;
	}

	public OpinionModel(double b, double d, double a) {
		this.belief = b;
		this.disbelief = d;
		this.uncertainty = (1.0D - (b + d));
		this.baserate = a;
	}

	public double getBelief() {
		return belief;
	}

	public void setBelief(double belief) {
		this.belief = belief;
	}

	public double getDisbelied() {
		return disbelief;
	}

	public void setDisbelied(double disbelief) {
		this.disbelief = disbelief;
	}

	public double getUncertainty() {
		return uncertainty;
	}

	public void setUncertainty(double uncertainty) {
		this.uncertainty = uncertainty;
	}

	public double getBaserate() {
		return baserate;
	}

	public void setBaserate(double baserate) {
		this.baserate = baserate;
	}

	public double getProbability() {
		return probability;
	}

	public void setProbability(double probability) {
		this.probability = probability;
	}

	public OpinionModel toSubjectiveOpinion() {
		return this;
	}

	public void checkConsistency() {
		synchronized (this) {
			if ((this.baserate < 0.0D) || (this.baserate > 1.0D)) {
				throw new OperatorExceptions("Atomicity out of range, a: 0 <= a <= 1");
			}
			this.belief = SubjectiveConditions.constrain(SubjectiveConditions.adjust(this.belief));
			this.disbelief = SubjectiveConditions.constrain(SubjectiveConditions.adjust(this.disbelief));
			this.uncertainty = SubjectiveConditions.constrain(SubjectiveConditions.adjust(this.uncertainty));
			if (Math.abs(this.belief + this.disbelief + this.uncertainty - 1.0D) > 1.0E-10D) {
				double bdu = this.belief + this.disbelief + this.uncertainty;
				this.belief = SubjectiveConditions.constrain(SubjectiveConditions.adjust(this.belief / bdu));
				this.uncertainty = SubjectiveConditions.constrain(SubjectiveConditions.adjust(this.uncertainty / bdu));
				this.disbelief = (1.0D - (this.belief + this.uncertainty));
			}
			if ((this.baserate < 0.0D) || (this.baserate > 1.0D)) {
				throw new OperatorExceptions("Atomicity out of range, a: 0 <= a <= 1");
			}
			if ((this.belief < 0.0D) || (this.belief > 1.0D)) {
				throw new OperatorExceptions("Belief out of range, b: 0 <= b <= 1");
			}
			if ((this.disbelief < 0.0D) || (this.disbelief > 1.0D)) {
				throw new OperatorExceptions("Disbelief out of range, d: 0 <= d <= 1");
			}
			if ((this.uncertainty < 0.0D) || (this.uncertainty > 1.0D)) {
				throw new OperatorExceptions("Uncertainty out of range, u: 0 <= u <= 1");
			}
			if (Math.abs(this.belief + this.disbelief + this.uncertainty - 1.0D) > 1.0E-10D) {
				throw new OperatorExceptions("Belief, disbelief and uncertainty do not add up to 1: b + d + u != 1");
			}
		}
	}

	public final void adjust() {
		this.belief = SubjectiveConditions.constrain(SubjectiveConditions.adjust(this.belief));
		this.disbelief = SubjectiveConditions.constrain(SubjectiveConditions.adjust(this.disbelief));
		this.uncertainty = SubjectiveConditions.constrain(SubjectiveConditions.adjust(this.uncertainty));
	}

	public synchronized void setDependants() {
		this.belief = SubjectiveConditions.constrain(SubjectiveConditions.adjust(this.belief));
		this.disbelief = SubjectiveConditions.constrain(SubjectiveConditions.adjust(this.disbelief));
		this.uncertainty = SubjectiveConditions.constrain(SubjectiveConditions.adjust(this.uncertainty));
		this.baserate = SubjectiveConditions.constrain(SubjectiveConditions.adjust(this.baserate));
		this.probability = SubjectiveConditions
				.constrain(SubjectiveConditions.adjust(this.belief + this.baserate * this.uncertainty));
	}

}
