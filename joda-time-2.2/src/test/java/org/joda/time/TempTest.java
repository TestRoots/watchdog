/*
 *  Copyright 2001-2012 Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.time;

/**
 * Test testing.
 */
public class TempTest {

//    private static final DateTimeZone PARIS = DateTimeZone.forID("Europe/Paris");

    public static void main(String[] args) {
        DateTime dateTimeBefore = new DateTime(2012, 10, 28, 2, 59, 0, 0, DateTimeZone.forID("+02:00"));
        System.out.println(dateTimeBefore);
        DateTime dateTimeAfter = dateTimeBefore.withSecondOfMinute(0);
        System.out.println(dateTimeAfter);
        System.out.println(dateTimeBefore.equals(dateTimeAfter));

        DateTime dateTimeBefore2 = new DateTime(2012, 10, 28, 2, 59, 0, 0, DateTimeZone.forID("Europe/Berlin"));
        System.out.println(dateTimeBefore2);
        DateTime dateTimeAfter2 = dateTimeBefore2.withSecondOfMinute(0);
        System.out.println(dateTimeAfter2);
        System.out.println(dateTimeBefore2.equals(dateTimeAfter2));

        DateTime dateTimeBefore3 = new DateTime(2012, 10, 28, 1, 59, 0, 0, DateTimeZone.forID("Europe/Berlin"));
        dateTimeBefore3 = dateTimeBefore3.plusMinutes(60); // 2:59 +02:00 with Europe/Berlin
        System.out.println(dateTimeBefore3);
        DateTime dateTimeAfter3 = dateTimeBefore2.withSecondOfMinute(0);
        System.out.println(dateTimeAfter3);
        // DateTimeZone changed from +02:00 to +01:00
        System.out.println(dateTimeBefore3.equals(dateTimeAfter3));


//        final DateTime start = new LocalDateTime(2012, 11, 4, 1, 30).toDateTime(DateTimeZone.forID("America/New_York"));
//        final DateTime end = new LocalDateTime(2012, 11, 4, 2, 0).toDateTime(DateTimeZone.forID("America/New_York"));
//        System.out.println(start);
//        System.out.println(end);
//
//        final MutableInterval interval = new MutableInterval(start, end);
//        System.out.println(interval);
//        System.out.println(new Period(interval));
//        System.out.println(Hours.hoursIn(interval));
//        System.out.println(Hours.hoursIn(interval).getHours());
//        
//        // Period is correctly PT1H30, 1 == 1
//        Assert.assertEquals(interval.toPeriod().getHours(), Hours.hoursIn(interval).getHours());
//
//        interval.setStart(interval.getStart().plusHours(1));
//        System.out.println("-----");
//        System.out.println(interval);
//        System.out.println(new Period(interval));
//        System.out.println(Hours.hoursIn(interval));
//        System.out.println(Hours.hoursIn(interval).getHours());
//        // Period is incorrectly PT1H30, 1 == 0
//        Assert.assertEquals(Hours.hoursIn(interval).getHours(), interval.toPeriod().getHours());
        
//        DateTimeFormat.forPattern("dd.MM.yyyy").parseDateTime("00.10.2010");
        
//        DateTime dateTime = new DateTime("2010-10-10T04:00:00",
//        DateTimeZone.forID("America/Caracas"));
//        // time zone is -04:30 -- UTC date time is 2010-10-09T23:30
//
//        System.out.println(dateTime + " " + dateTime.getChronology());
//        MutableDateTime mutableDateTime = dateTime.toMutableDateTime();
//        mutableDateTime.setDate(dateTime); // is essentially a no-op
//        System.out.println(mutableDateTime + " " + mutableDateTime.getChronology());

//        Expected is: 2010-10-10T04:00:00.000-04:30
//        Actual result is: 2010-10-09T04:00:00.000-04:30        
        
        
//      DateTime dt = new DateTime(1, 1, 1, 0, 0, 0, ISOChronology.getInstanceUTC());
//      System.out.println(dt + " " + dt.toString("yyyy YYYY GG"));
//      dt = dt.minusDays(1);
//      System.out.println(dt + " " + dt.toString("yyyy YYYY GG"));
//      
//      dt = new DateTime(1, 1, 1, 0, 0, 0, GregorianChronology.getInstanceUTC());
//      System.out.println(dt + " " + dt.toString("yyyy YYYY GG"));
//      dt = dt.minusDays(1);
//      System.out.println(dt + " " + dt.toString("yyyy YYYY GG"));
//      
//      dt = new DateTime(100000, 1, 1, 0, 0, 0, GregorianChronology.getInstanceUTC());
//      System.out.println(dt + " " + dt.toString("yyyy YYYY GG"));
//      
//      dt = new DateTime(-100000, 1, 1, 0, 0, 0, GregorianChronology.getInstanceUTC());
//      System.out.println(dt + " " + dt.toString("yyyy YYYY GG"));
      
//      DateTime birth = new DateTime(2012, 03, 31, 02, 28, 0, 0).minus(Years.years(27));
//      DateTime now = new DateTime(2012, 03, 31, 02, 28, 33, 0);
//      System.out.println(birth);
//      System.out.println(now);
//      System.out.println(new Period(birth, now));
      
//      // From the first test 
//      // Comment out (in) the following call in order to make the test pass (fail). 
//      DateTimeZone p1 = DateTimeZone.forID("Europe/Paris");
//      System.out.println(p1 + " " + System.identityHashCode(p1));
//      DateTimeZone.setDefault(p1); 
//      new DateMidnight(2004, 6, 9); 
//      // From the first test 
//      
//      // From the second test 
//      DateTimeZone.setProvider(null); 
//      // From the second test 
//      
//      // From the third test 
//      DateTimeZone p2 = DateTimeZone.forID("Europe/Paris");
//      System.out.println(p2 + " " + System.identityHashCode(p2));
//      DateTimeZone.setDefault(p2); 
//      
//      DateTime test = new DateTime(0); 
//      DateTime result = test.withZoneRetainFields(p2); 
//      if (test != result) {
//        throw new IllegalArgumentException();
//      }
    }

}
