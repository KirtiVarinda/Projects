package com.tiffindaddy.app.tiffindaddy.Manager;

/**
 * Created by Avnish on 2/17/2016.
 */
public class TimeSlotManager {

    public static String[] calculateSlots(String[] slotsInMillli) {
        long current = TimeManager.currentTimedateToString();


        int count = 0;
        for (int l = 0; l < slotsInMillli.length; l++) {

            System.out.println("mili   =  " + slotsInMillli[l]);
            String Slot[] = slotsInMillli[l].split("-l-");

            long start = Long.parseLong(Slot[0]);
            long end = Long.parseLong(Slot[1]);


            if (current >= end) {


            } else {
                count++;
            }


        }



        String actualSlots[] = new String[count];

        int index = 0;
        for (int l = 0; l < slotsInMillli.length; l++) {


            String Slot1[] = slotsInMillli[l].split("-l-");

            long start1 = Long.parseLong(Slot1[0]);
            long end1 = Long.parseLong(Slot1[1]);

            if (current >= end1) {


            } else {
                actualSlots[index] = TimeManager.milisecToTime(start1) + " - " + TimeManager.milisecToTime(end1);
                index++;
            }


        }

        return actualSlots;
    }


    public static String[] calculateSlotsForTomorrow(String[] slotsInMillli) {
        long current = TimeManager.currentTimedateToString();


        int count = 0;
        for (int l = 0; l < slotsInMillli.length; l++) {

            System.out.println("mili   =  " + slotsInMillli[l]);
            String Slot[] = slotsInMillli[l].split("-l-");

            long start = Long.parseLong(Slot[0]);
            long end = Long.parseLong(Slot[1]);


            count++;


        }


        System.out.println("counbt   =  " + count);

        String actualSlots[] = new String[count];

        int index = 0;
        for (int l = 0; l < slotsInMillli.length; l++) {


            String Slot1[] = slotsInMillli[l].split("-l-");

            long start1 = Long.parseLong(Slot1[0]);
            long end1 = Long.parseLong(Slot1[1]);


            actualSlots[index] = TimeManager.milisecToTime(start1) + " - " + TimeManager.milisecToTime(end1);
            index++;


        }

        return actualSlots;
    }

    public static String[] calculateSlotsinMiliseconds(int noOfSlots, String startTime) {

        long slotStart = 0;
        String slots[] = new String[noOfSlots];

        String startArray[] = startTime.split(":");

        slotStart = TimeManager.dateToString(Integer.parseInt(startArray[0]), Integer.parseInt(startArray[1]));


        for (int hourNo = 0; hourNo < noOfSlots; hourNo++) {

            long slotEnd = TimeManager.timeAfterHourinMilli(slotStart);

            slots[hourNo] = slotStart + "-l-" + slotEnd;
            slotStart = slotEnd;


        }

        return slots;
    }


}
