package com.hostel.model;

import java.io.Serializable;

public class SustainabilityUsage implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private String yearMonth; // "YYYY-MM" e.g. "2024-08"
    private String monthLabel; // "Aug 2024"
    private double electricityKwh; // kWh units
    private double waterKiloLitres; // kL (1000 Litres)
    private double prevMonthElectricity;
    private double prevMonthWater;
    private double electricityChangePct; // % change vs prev month
    private double waterChangePct;       // % change vs prev month
    private boolean electricityAnomaly;  // true if jump > 15%
    private boolean waterAnomaly;        // true if jump > 15%
    private String remarks;
    private String recordedDate;
    private String recordedBy;

    public SustainabilityUsage() {}

    public SustainabilityUsage(int id, String yearMonth, String monthLabel, double electricityKwh, 
                               double waterKiloLitres, double prevMonthElectricity, double prevMonthWater,
                               String remarks, String recordedDate, String recordedBy) {
        this.id = id;
        this.yearMonth = yearMonth;
        this.monthLabel = monthLabel;
        this.electricityKwh = electricityKwh;
        this.waterKiloLitres = waterKiloLitres;
        this.prevMonthElectricity = prevMonthElectricity;
        this.prevMonthWater = prevMonthWater;
        this.remarks = remarks;
        this.recordedDate = recordedDate;
        this.recordedBy = recordedBy;
        calculateVariances();
    }

    public void calculateVariances() {
        if (prevMonthElectricity > 0) {
            this.electricityChangePct = ((electricityKwh - prevMonthElectricity) / prevMonthElectricity) * 100.0;
            this.electricityAnomaly = this.electricityChangePct >= 15.0; // unusual spike
        } else {
            this.electricityChangePct = 0.0;
            this.electricityAnomaly = false;
        }

        if (prevMonthWater > 0) {
            this.waterChangePct = ((waterKiloLitres - prevMonthWater) / prevMonthWater) * 100.0;
            this.waterAnomaly = this.waterChangePct >= 15.0; // unusual spike
        } else {
            this.waterChangePct = 0.0;
            this.waterAnomaly = false;
        }
    }

    public boolean hasAnyAnomaly() {
        return electricityAnomaly || waterAnomaly;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getYearMonth() { return yearMonth; }
    public void setYearMonth(String yearMonth) { this.yearMonth = yearMonth; }

    public String getMonthLabel() { return monthLabel; }
    public void setMonthLabel(String monthLabel) { this.monthLabel = monthLabel; }

    public double getElectricityKwh() { return electricityKwh; }
    public void setElectricityKwh(double electricityKwh) { 
        this.electricityKwh = electricityKwh;
        calculateVariances();
    }

    public double getWaterKiloLitres() { return waterKiloLitres; }
    public void setWaterKiloLitres(double waterKiloLitres) { 
        this.waterKiloLitres = waterKiloLitres;
        calculateVariances();
    }

    public double getPrevMonthElectricity() { return prevMonthElectricity; }
    public void setPrevMonthElectricity(double prevMonthElectricity) { 
        this.prevMonthElectricity = prevMonthElectricity;
        calculateVariances();
    }

    public double getPrevMonthWater() { return prevMonthWater; }
    public void setPrevMonthWater(double prevMonthWater) { 
        this.prevMonthWater = prevMonthWater;
        calculateVariances();
    }

    public double getElectricityChangePct() { return electricityChangePct; }
    public double getWaterChangePct() { return waterChangePct; }

    public boolean isElectricityAnomaly() { return electricityAnomaly; }
    public boolean isWaterAnomaly() { return waterAnomaly; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getRecordedDate() { return recordedDate; }
    public void setRecordedDate(String recordedDate) { this.recordedDate = recordedDate; }

    public String getRecordedBy() { return recordedBy; }
    public void setRecordedBy(String recordedBy) { this.recordedBy = recordedBy; }
}
