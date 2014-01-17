package com.gmail.fantasticskythrow.configuration;

public class TimeNames {

	private String second, seconds, minute, minutes, hour, hours, day, days, month, months, noLastLogin;

	public TimeNames(String second, String seconds, String minute, String minutes, String hour, String hours, String day, String days, String month,
			String months, String noLastLogin) {
		this.second = second;
		this.seconds = seconds;
		this.minute = minute;
		this.minutes = minutes;
		this.hour = hours;
		this.hours = hours;
		this.day = day;
		this.days = days;
		this.month = month;
		this.months = months;
		this.noLastLogin = noLastLogin;
	}

	public String getSecond() {
		return second;
	}

	public String getSeconds() {
		return seconds;
	}

	public String getMinute() {
		return minute;
	}

	public String getMinutes() {
		return minutes;
	}

	public String getHour() {
		return hour;
	}

	public String getHours() {
		return hours;
	}

	public String getDay() {
		return day;
	}

	public String getDays() {
		return days;
	}

	public String getMonth() {
		return month;
	}

	public String getMonths() {
		return months;
	}

	public String getNoLastLogin() {
		return noLastLogin;
	}
}
