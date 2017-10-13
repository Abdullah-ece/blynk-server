package cc.blynk.integration.https.reporting;

import java.util.StringJoiner;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.10.17.
 */
class KnightData {
    private String typeOfRecord;
    private String washerId;
    private String startDate;
    private String startTime;
    private String finishTime;
    private String cycleTime;
    private String formulaNumber;
    private String loadWeight;
    private String pumpId;
    private String volume;
    private String runTime;
    private String pulseCount;

    private KnightData(String typeOfRecord, String washerId, String startDate,
                      String startTime, String finishTime, String cycleTime,
                      String formulaNumber, String loadWeight, String pumpId,
                      String volume, String runTime, String pulseCount) {
        this.typeOfRecord = typeOfRecord;
        this.washerId = washerId;
        this.startDate = startDate;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.cycleTime = cycleTime;
        this.formulaNumber = formulaNumber;
        this.loadWeight = loadWeight;
        this.pumpId = pumpId;
        this.volume = volume;
        this.runTime = runTime;
        this.pulseCount = pulseCount;
    }

    static KnightData[] makeNewDataFromOldData(String[] oldData) {
        String typeOfRecord = oldData[4];
        String washerId = oldData[5];
        String startDate = oldData[0];
        String startTime = oldData[1];
        String cycleTime = oldData[7];
        String finishTime = oldData[3];
        String formulaNumber = oldData[6];
        String loadWeight = oldData[8];

        String pumpId1 = oldData[9];
        String pumpId2 = oldData[10];
        String pumpId3 = oldData[11];
        String pumpId4 = oldData[12];
        String pumpId5 = oldData[13];
        String pumpId6 = oldData[14];
        String pumpId7 = oldData[15];
        String pumpId8 = oldData[16];

        return new KnightData[] {
                new KnightData(typeOfRecord, washerId, startDate,
                        startTime, finishTime, cycleTime,
                        formulaNumber, loadWeight, "1", pumpId1, "0", "0"),
                new KnightData(typeOfRecord, washerId, startDate,
                        startTime, finishTime, cycleTime,
                        formulaNumber, loadWeight, "2", pumpId2, "0", "0"),
                new KnightData(typeOfRecord, washerId, startDate,
                        startTime, finishTime, cycleTime,
                        formulaNumber, loadWeight, "3", pumpId3, "0", "0"),
                new KnightData(typeOfRecord, washerId, startDate,
                        startTime, finishTime, cycleTime,
                        formulaNumber, loadWeight, "4", pumpId4, "0", "0"),
                new KnightData(typeOfRecord, washerId, startDate,
                        startTime, finishTime, cycleTime,
                        formulaNumber, loadWeight, "5", pumpId5, "0", "0"),
                new KnightData(typeOfRecord, washerId, startDate,
                        startTime, finishTime, cycleTime,
                        formulaNumber, loadWeight, "6", pumpId6, "0", "0"),
                new KnightData(typeOfRecord, washerId, startDate,
                        startTime, finishTime, cycleTime,
                        formulaNumber, loadWeight, "7", pumpId7, "0", "0"),
                new KnightData(typeOfRecord, washerId, startDate,
                        startTime, finishTime, cycleTime,
                        formulaNumber, loadWeight, "8", pumpId8, "0", "0"),
        };
    }

    public String toString() {
        StringJoiner sj = new StringJoiner(",", "[", "]");
        sj.add(escape(typeOfRecord));
        sj.add(escape(washerId));
        sj.add(escape(startDate));
        sj.add(escape(startTime));
        sj.add(escape(finishTime));
        sj.add(escape(cycleTime));
        sj.add(escape(formulaNumber));
        sj.add(escape(loadWeight));
        sj.add(escape(pumpId));
        sj.add(escape(volume));
        sj.add(escape(runTime));
        sj.add(escape(pulseCount));
        return sj.toString();
    }
    
    String[] toSplit() {
        return new String[] {
                typeOfRecord,
                washerId,
                startDate,
                startTime,
                finishTime,
                cycleTime,
                formulaNumber,
                loadWeight,
                pumpId,
                volume,
                runTime,
                pulseCount
        };
    }

    private static String escape(String field) {
        return "\"" + field + "\"";
    }
}
