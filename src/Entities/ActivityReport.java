package Entities;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;


/**
 * This Class contains all the needed data for the activity report
 * 
 * @author Bshara
 * */
public class ActivityReport implements Serializable {

	public long id;
	public String name;
	public Timestamp date;

	public ArrayList<Integer> active;
	public ArrayList<Integer> frozen;
	public ArrayList<Integer> closed;
	public ArrayList<Integer> rejected;
	public ArrayList<Integer> numOfWorkDays;

	public int totalActive;
	public int totalFrozen;
	public int totalClosed;
	public int totalRejected;
	public int totalNumOfWorkDays;

	public ActivityReport(long id, String name, Timestamp date, ArrayList<Integer> active, ArrayList<Integer> frozen,
			ArrayList<Integer> closed, ArrayList<Integer> rejected, ArrayList<Integer> numOfWorkDays, int totalActive,
			int totalFrozen, int totalClosed, int totalRejected, int totalNumOfWorkDays) {
		super();
		this.id = id;
		this.name = name;
		this.date = date;
		this.active = active;
		this.frozen = frozen;
		this.closed = closed;
		this.rejected = rejected;
		this.numOfWorkDays = numOfWorkDays;
		this.totalActive = totalActive;
		this.totalFrozen = totalFrozen;
		this.totalClosed = totalClosed;
		this.totalRejected = totalRejected;
		this.totalNumOfWorkDays = totalNumOfWorkDays;
	}

	public ArrayList<Double> getMedian() {

		ArrayList<Double> medians = new ArrayList<Double>();

		medians.add(CalcMedian(getActive()));

		medians.add(CalcMedian(getFrozen()));

		medians.add(CalcMedian(getClosed()));

		medians.add(CalcMedian(getRejected()));

		medians.add(CalcMedian(getNumOfWorkDays()));

		return medians;
	}

	public ArrayList<Double> getSTD() {

		ArrayList<Double> std = new ArrayList<Double>();

		ArrayList<Double> avgs = getAverages();

		std.add(CalcStandardDeviation(getActive(), avgs.get(0)));
		std.add(CalcStandardDeviation(getFrozen(), avgs.get(1)));
		std.add(CalcStandardDeviation(getClosed(), avgs.get(2)));
		std.add(CalcStandardDeviation(getRejected(), avgs.get(3)));
		std.add(CalcStandardDeviation(getNumOfWorkDays(), avgs.get(4)));

		return std;
	}

	public ArrayList<Double> getAverages() {

		ArrayList<Double> avgs = new ArrayList<Double>();

		avgs.add(CalcAvg(getActive()));
		avgs.add(CalcAvg(getFrozen()));
		avgs.add(CalcAvg(getClosed()));
		avgs.add(CalcAvg(getRejected()));
		avgs.add(CalcAvg(getNumOfWorkDays()));

		return avgs;
	}

	private double CalcMedian(ArrayList<Integer> arrList) {
		ArrayList<Integer> arr = (ArrayList<Integer>) arrList.clone();
		Collections.sort(arr);
		int median;
		int a, b;
		if (arrList.size() == 0) {
			return 0;
		}
		if (arrList.size() % 2 == 0) {
			median = arrList.size() / 2;
		} else {
			median = (arrList.size() + 1) / 2;
		}
		a = arrList.get(median);
		b = arrList.get(median + 1);
		return (a + b) / 2.0;
	}

	private double CalcAvg(ArrayList<Integer> arrList) {
		Integer sum = 0;

		if (arrList.size() == 0) {
			return 0;
		}

		for (Integer i : arrList) {
			sum += i;
		}
		return (double) sum / arrList.size();
	}

	private double CalcStandardDeviation(ArrayList<Integer> arrList, double avg) {
		double sum = 0;
		if (arrList.size() == 0)
			return 0;
		for (Integer i : arrList) {
			sum += Math.pow((double) i - avg, 2);
		}
		return Math.sqrt(sum / arrList.size());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Timestamp getDate() {
		return date;
	}

	public void setDate(Timestamp date) {
		this.date = date;
	}

	public ArrayList<Integer> getActive() {
		return active;
	}

	public void setActive(ArrayList<Integer> active) {
		this.active = active;
	}

	public ArrayList<Integer> getFrozen() {
		return frozen;
	}

	public void setFrozen(ArrayList<Integer> frozen) {
		this.frozen = frozen;
	}

	public ArrayList<Integer> getClosed() {
		return closed;
	}

	public void setClosed(ArrayList<Integer> closed) {
		this.closed = closed;
	}

	public ArrayList<Integer> getRejected() {
		return rejected;
	}

	public void setRejected(ArrayList<Integer> rejected) {
		this.rejected = rejected;
	}

	public ArrayList<Integer> getNumOfWorkDays() {
		return numOfWorkDays;
	}

	public void setNumOfWorkDays(ArrayList<Integer> numOfWorkDays) {
		this.numOfWorkDays = numOfWorkDays;
	}

	public int getTotalActive() {
		return totalActive;
	}

	public void setTotalActive(int totalActive) {
		this.totalActive = totalActive;
	}

	public int getTotalFrozen() {
		return totalFrozen;
	}

	public void setTotalFrozen(int totalFrozen) {
		this.totalFrozen = totalFrozen;
	}

	public int getTotalClosed() {
		return totalClosed;
	}

	public void setTotalClosed(int totalClosed) {
		this.totalClosed = totalClosed;
	}

	public int getTotalRejected() {
		return totalRejected;
	}

	public void setTotalRejected(int totalRejected) {
		this.totalRejected = totalRejected;
	}

	public int getTotalNumOfWorkDays() {
		return totalNumOfWorkDays;
	}

	public void setTotalNumOfWorkDays(int totalNumOfWorkDays) {
		this.totalNumOfWorkDays = totalNumOfWorkDays;
	}

	@Override
	public String toString() {
		return "ActivityReport [id=" + id + ", name=" + name + ", date=" + date + ", active=" + active + ", frozen="
				+ frozen + ", closed=" + closed + ", rejected=" + rejected + ", numOfWorkDays=" + numOfWorkDays
				+ ", totalActive=" + totalActive + ", totalFrozen=" + totalFrozen + ", totalClosed=" + totalClosed
				+ ", totalRejected=" + totalRejected + ", totalNumOfWorkDays=" + totalNumOfWorkDays + "]";
	}

	
}
