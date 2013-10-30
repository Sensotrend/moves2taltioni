package com.sensotrend.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import org.apache.commons.io.IOUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.ArrayOfObservation;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.ArrayOfObservationItem;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.AuditInfo;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.DeviceInformation;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.HealthRecordData;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.Observation;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.ObservationItem;

public class MovesJSONParser {

    public static MovesJSONParser getInstance() {return new MovesJSONParser();}
    
    /**
     *          Parsing result is stored in HealthRecordData which is sent to Taltioni with SOAP request.
     *          HealthRecordData stores data in ArrayOfObservation: <p>
     *          <pre>
     *          HealthRecordData
     *              |
     *              ArrayOfObservation
     *                  |
     *                  Observation 1 (TypeId="ExerciseSteps")
     *                  |   |
     *                  |   EffectiveDateTime
     *                  |   |
     *                  |   ArrayOfObservationItem
     *                  |       |
     *                  |       ObservationItem 1 (TypeId="Steps", Unit="", NumberValue=xxx)
     *                  |       ObservationItem 2 (TypeId="Duration", Unit="min", NumberValue=xxx)
     *                  |       ObservationItem 3 (TypeId="Distance", Unit="m", NumberValue=xxx)
     *                  |       ObservationItem 4 (TypeId="EnergyConsumption", Unit="kcal", NumberValue=xxx)
     *                  |        
     *                  Observation 2
     *                  ...
     *                  </pre>

     * @param reader Data to be parsed. 
     * @return Taltioni HealthRecordData.
     */
    public HealthRecordData parse(BufferedReader reader) {

        // Build simple Auditinfo for storing data source information.
        AuditInfo auditInfo = new AuditInfo();
        auditInfo.setCreatedBy("Sensotrend");
        auditInfo.setSourceSystem("Moves");
        ArrayOfObservation observationArray = new ArrayOfObservation();

        JSONArray jarr = null;
        JSONObject segments = null;
        try {
            jarr = new JSONArray(IOUtils.toString(reader));
            segments = jarr.getJSONObject(0);
            jarr = segments.getJSONArray("segments");
            for (int seg=0; seg<jarr.length(); ++seg) {
                JSONObject segment = jarr.getJSONObject(seg);
                JSONArray activities = segment.getJSONArray("activities");
                JSONObject activity = activities.getJSONObject(0); 
                if (activity.getString("activity").equals("wlk")) {  // Only walk activity is currently supported
                    
                    // Get data from JSON
                    String start = activity.getString("startTime");
                    double duration = activity.getDouble("duration");
                    double distance = activity.getDouble("distance");
                    int steps = activity.getInt("steps");
                    int calories = activity.getInt("calories");
                    
                    // Put data to Taltioni Observation
                    Observation o = new Observation(); 
                    o.setApplicationId(TaltioniDataAccess.getInstance().APPLICATION_ID.getApplicationId());
                    Date time;
                    try {
                        time = TaltioniDataAccess.getInstance().MOVES_DATEFORMAT.parse(start);
                    } catch (java.text.ParseException e) {
                        throw new RuntimeException(e.getMessage(), e);
                    }
                    Calendar date = TaltioniDataAccess.getInstance().getCalendar(time);
                    o.setEffectiveDateTime(date);
                    o.setTypeId(TypeIdMap.EXERCISE_STEPS.name);

                    ArrayOfObservationItem itemArray = new ArrayOfObservationItem();
                    ObservationItem stepItem = new ObservationItem();
                    stepItem.setTypeId("Steps");
                    stepItem.setNumberValue(steps);
                    stepItem.setUnit("");
                    itemArray.addObservationItem(stepItem);
                    
                    ObservationItem durationItem = new ObservationItem();
                    durationItem.setTypeId("Duration");
                    durationItem.setUnit("min");
                    durationItem.setNumberValue(duration / 60);
                    itemArray.addObservationItem(durationItem);
                    
                    ObservationItem distanceItem = new ObservationItem();
                    distanceItem.setTypeId("Distance");
                    distanceItem.setUnit("m");
                    distanceItem.setNumberValue(distance);
                    itemArray.addObservationItem(distanceItem);
                    
                    ObservationItem calorItem = new ObservationItem();
                    calorItem.setTypeId("EnergyConsumption");
                    calorItem.setUnit("kcal");
                    calorItem.setNumberValue(calories);
                    itemArray.addObservationItem(calorItem);
                    
                    o.setObservationItems(itemArray);
                    o.setAuditInfo(auditInfo);
                    observationArray.addObservation(o);
                }  // if walking
            }  // for segments
        } catch (JSONException | IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if ((observationArray.getObservation() == null) || (observationArray.getObservation().length == 0) ) return null;
        HealthRecordData record = new HealthRecordData();
        record.setObservations(observationArray);
        return record;
    }
}
