package cc.blynk.integration.https.reporting;

import java.util.StringJoiner;

/**
 * The Blynk Project.
 * Created by Dmitriy Dumanskiy.
 * Created on 12.10.17.
 */
class KnightData {

    private int typeOfRecord;
    private int washerId;
    private String startDate;
    private String startTime;
    private String finishTime;
    private String cycleTime;
    private int formulaNumber;
    private String loadWeight;
    private int pumpId;
    private int volume;
    private int runTime;
    private int pulseCount;

    private KnightData(int typeOfRecord, int washerId, String startDate,
                      String startTime, String finishTime, String cycleTime,
                       int formulaNumber, String loadWeight, int pumpId,
                       int volume, int runTime, int pulseCount) {
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
        int typeOfRecord = Integer.parseInt(oldData[4]);
        int washerId = Integer.parseInt(oldData[5]);
        String startDate = oldData[0];
        String startTime = oldData[1];
        String cycleTime = oldData[7];
        String finishTime = oldData[3];
        int formulaNumber = Integer.parseInt(oldData[6]);
        String loadWeight = oldData[8];

        int pumpId1 = Integer.parseInt(oldData[9]);
        int pumpId2 = Integer.parseInt(oldData[10]);
        int pumpId3 = Integer.parseInt(oldData[11]);
        int pumpId4 = Integer.parseInt(oldData[12]);
        int pumpId5 = Integer.parseInt(oldData[13]);
        int pumpId6 = Integer.parseInt(oldData[14]);
        int pumpId7 = Integer.parseInt(oldData[15]);
        int pumpId8 = Integer.parseInt(oldData[16]);

        return new KnightData[] {
                new KnightData(typeOfRecord, washerId, startDate,
                        startTime, finishTime, cycleTime,
                        formulaNumber, loadWeight, 1, pumpId1, 0, 0),
                new KnightData(typeOfRecord, washerId, startDate,
                        startTime, finishTime, cycleTime,
                        formulaNumber, loadWeight, 2, pumpId2, 0, 0),
                new KnightData(typeOfRecord, washerId, startDate,
                        startTime, finishTime, cycleTime,
                        formulaNumber, loadWeight, 3, pumpId3, 0, 0),
                new KnightData(typeOfRecord, washerId, startDate,
                        startTime, finishTime, cycleTime,
                        formulaNumber, loadWeight, 4, pumpId4, 0, 0),
                new KnightData(typeOfRecord, washerId, startDate,
                        startTime, finishTime, cycleTime,
                        formulaNumber, loadWeight, 5, pumpId5, 0, 0),
                new KnightData(typeOfRecord, washerId, startDate,
                        startTime, finishTime, cycleTime,
                        formulaNumber, loadWeight, 6, pumpId6, 0, 0),
                new KnightData(typeOfRecord, washerId, startDate,
                        startTime, finishTime, cycleTime,
                        formulaNumber, loadWeight, 7, pumpId7, 0, 0),
                new KnightData(typeOfRecord, washerId, startDate,
                        startTime, finishTime, cycleTime,
                        formulaNumber, loadWeight, 8, pumpId8, 0, 0),
        };
    }

    public String toString() {
        StringJoiner sj = new StringJoiner(",", "[", "]");
        sj.add(String.valueOf(typeOfRecord));
        sj.add(String.valueOf(washerId));
        sj.add(escape(startDate));
        sj.add(escape(startTime));
        sj.add(escape(finishTime));
        sj.add(escape(cycleTime));
        sj.add(String.valueOf(formulaNumber));
        sj.add(escape(loadWeight));
        sj.add(String.valueOf(pumpId));
        sj.add(String.valueOf(volume));
        sj.add(String.valueOf(runTime));
        sj.add(String.valueOf(pulseCount));
        return sj.toString();
    }

    Object[] toSplit() {
        return new Object[] {
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
