package org.example.util;

import org.example.model.mongodb.Medicion;
import java.util.*;
import java.util.stream.*;

public final class ReportGenerator {
	private ReportGenerator() {}

	public static String measurementsToCsv(List<Medicion> datos) {
		StringBuilder sb = new StringBuilder();
		sb.append("sensorId,timestamp,temperature,humidity,city\n");
		for (Medicion m : datos) {
			String city = m.getLocationSnapshot() == null ? "" : m.getLocationSnapshot().getCity();
			sb.append(String.format("%s,%s,%.2f,%.2f,%s\n",
					m.getSensorId(), m.getTimestamp().toString(),
					m.getTemperature() == null ? Double.NaN : m.getTemperature(),
					m.getHumidity() == null ? Double.NaN : m.getHumidity(),
					city == null ? "" : city));
		}
		return sb.toString();
	}

	public static Map<String, Double> avgTemperatureByCity(List<Medicion> datos) {
		return datos.stream()
				.filter(d -> d.getLocationSnapshot() != null && d.getTemperature() != null)
				.collect(Collectors.groupingBy(d -> d.getLocationSnapshot().getCity(),
						Collectors.averagingDouble(Medicion::getTemperature)));
	}
}

