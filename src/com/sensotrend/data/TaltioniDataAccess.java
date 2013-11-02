package com.sensotrend.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.CharBuffer;
import java.rmi.RemoteException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;
import java.util.UUID;

import javax.activation.DataHandler;

import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axis2.AxisFault;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import fi.taltioni._0._1.taltioniapi.TaltioniAPI_GetApplicationInfo_ReceiverFault_FaultMessage;
import fi.taltioni._0._1.taltioniapi.TaltioniAPI_GetApplicationInfo_SenderFault_FaultMessage;
import fi.taltioni._0._1.taltioniapi.TaltioniAPI_GetHealthRecordItems_ReceiverFault_FaultMessage;
import fi.taltioni._0._1.taltioniapi.TaltioniAPI_GetHealthRecordItems_SenderFault_FaultMessage;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceCallbackHandler;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.AccessToken;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.ApplicationId;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.ArrayOfObservation;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.ArrayOfstring;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.AuditInfo;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.AuthCode;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.DeviceInformation;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.GetApplicationInfoRequest;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.GetApplicationInfoResponse;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.GetHealthRecordItemsRequest;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.GetHealthRecordItemsResponse;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.Guid;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.HealthRecordData;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.Observation;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.ObservationItem;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.RequestId;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.StoreHealthRecordItemsRequest;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.StoreHealthRecordItemsResponse;
import fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.Timestamp;

public class TaltioniDataAccess {
	
	private static final TaltioniDataAccess instance = new TaltioniDataAccess();
		
	public final ApplicationId APPLICATION_ID = new ApplicationId();
	private final Properties props = new Properties();
	
	// Date formats used in parsers
	public final SimpleDateFormat TALTIONI_DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	public final SimpleDateFormat DEXCOM_DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	public final SimpleDateFormat ACCUCHEK_DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	public final SimpleDateFormat CSV_DATEFORMAT = new SimpleDateFormat("dd.MM.yyyy");
    public final SimpleDateFormat CSV_DATETIMEFORMAT = new SimpleDateFormat("dd.MM.yyyy-HH:mm:ss");
    public final SimpleDateFormat MOVES_DATEFORMAT = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");
	
    public enum SensotrendFileType {
        EXCEL_CSV, 
        MEDTRONIC_CSV,
        ACCUCHEK_PUMP_XML,
        ACCUCHEK_METER_XML,
        DEXCOM_XML,
        MOVES_JSON};

	private final TaltioniServiceStub TALTIONI;
	

	public static TaltioniDataAccess getInstance() {
		return instance;
	}
	
	private TaltioniDataAccess() {
		InputStream is = null;
        try {
        	String home = System.getProperty("user.home");
        	File propertiesFile = new File(home, "moves2taltioni.properties");
        	String location = propertiesFile.getAbsolutePath();
        	System.out.println(home + ", " + location);
        	if (propertiesFile.exists()) {
        		is = new FileInputStream(propertiesFile);
        	} else {
                is = Thread.currentThread().getContextClassLoader().getResourceAsStream("app.properties");
        	}
            props.load(is);
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } finally {
        	if (is != null) {
        		try {
        			is.close();
        		} catch (IOException e) {
        		}
        	}
        }
		APPLICATION_ID.setApplicationId(getProperty("APPLICATION_ID"));
		// for Taltioni and for JSON exchange, express time in UTC time
		// for log file parsing, use server time zone for now
		// TODO: the application sending the log file should indicate the correct timezone
		TALTIONI_DATEFORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
		try {
			TALTIONI = new TaltioniServiceStub(getProperty("TALTIONI_URL"));
		} catch (AxisFault e) {
			InstantiationError ie = new InstantiationError();
			ie.initCause(e);
			throw ie;
		}
	}
	
	public ApplicationId getApplicationId() {return APPLICATION_ID;}
	public SimpleDateFormat getCSVDateFormat() {return CSV_DATEFORMAT;}
	public SimpleDateFormat getCSVDateTimeFormat() {return CSV_DATETIMEFORMAT;}
	public String getProperty(String key) {return props.getProperty(key);}

	/**
	 * Generates unique request ID for Taltioni OAuth request.
	 * @return
	 *     Unique request ID.
	 */
	protected RequestId getUniqueRequestID() {
		RequestId requestId = new RequestId();
		Guid id = new Guid();
		id.setGuid(UUID.randomUUID().toString());
		requestId.setRequestId(id);
		return requestId;
	}
	
	/**
	 * Makes Taltioni Timestamp from current time to use in SOAP requests.
	 * @return
	 *     Taltioni Timestamp.
	 */
	protected Timestamp getUTCTimestamp() {
		Timestamp timestamp = new Timestamp();
		timestamp.setTimestamp(TALTIONI_DATEFORMAT.format(new Date()));
		return timestamp;
	}
	
	/**
	 * Generates valid OAuth code to be used in SOAP requests.
	 * @param requestId
	 * @param timestamp
	 * @param token
	 * @return
	 */
	protected AuthCode generateAuthCode(RequestId requestId, Timestamp timestamp, AccessToken token) {
		String originalText =
				requestId.getRequestId().getGuid() + ";" +
				timestamp.getTimestamp() + ";" + 
				APPLICATION_ID.getApplicationId() + ";" +
				(token != null ? (token.getAccessToken() + ";") : "") + 
				getProperty("SHARED_SECRET");
		
		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-256");
			md.update(originalText.getBytes("UTF-8"));
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
		DataHandler dataHandler = new DataHandler(new ByteArrayDataSource(md.digest(), "application/octet-stream"));
		AuthCode authCode = new AuthCode();
		authCode.setAuthCode(dataHandler);
		return authCode;
	}

	/**
	 * Returns application name associated to token in Taltioni.
	 * @param token
	 *     OAuth token received from Taltioni.
	 * @return
	 *     Application name stored in Taltioni with token.
	 * @throws RemoteException
	 */
	public String getAppName(AccessToken token) throws RemoteException {
		RequestId requestId = getUniqueRequestID();
		Timestamp timestamp = getUTCTimestamp();
		AuthCode authCode = generateAuthCode(requestId, timestamp, null);
		
		try {
			GetApplicationInfoRequest request = new GetApplicationInfoRequest();
			GetApplicationInfoResponse response = TALTIONI.getApplicationInfo(request, APPLICATION_ID, authCode, requestId, timestamp);
			return response.getApplicationInfo().getName();
		} catch (TaltioniAPI_GetApplicationInfo_ReceiverFault_FaultMessage | 
				 TaltioniAPI_GetApplicationInfo_SenderFault_FaultMessage e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Gets all specific Observations at given time to JSON String.
	 * @param observationTypeName
	 *     ObservationTypeName, for example BloodGlucose
	 * @param startTime
	 *     Oldest Observation to be retrieved.
	 * @param endTime
	 *     Newest Observation.
	 * @param token
	 * @return
	 *     All Observations in JSON format.
	 * @throws RemoteException
	 */
	public String getObservationsJSON(String observationTypeName, Calendar startTime, Calendar endTime, AccessToken token) throws RemoteException {
        ArrayOfstring types = new ArrayOfstring();
        types.addString(observationTypeName);
        return getObservationsJSON(types, startTime, endTime, token);
    }
    
	/**
	 * Gets many types of Observations.
	 * @param observationTypeNames
	 *     Array of ObservationTypeNames
	 * @param startTime
	 *     @see TaltioniDataAccess#getObservationsJSON(String, Calendar, Calendar, AccessToken)
	 * @param endTime
	 * @param token
	 * @return
	 * @throws RemoteException
	 */
	public String getObservationsJSON(ArrayOfstring observationTypeNames, Calendar startTime, Calendar endTime, AccessToken token) throws RemoteException {
	    Observation[] observations = null;
	    try {
	        observations = getHealthRecordObservations(token, startTime, endTime, observationTypeNames);
	    } catch (TaltioniAPI_GetHealthRecordItems_ReceiverFault_FaultMessage | 
	            TaltioniAPI_GetHealthRecordItems_SenderFault_FaultMessage e) {
	        // TODO Error handling
	        e.printStackTrace();
	    }
        JSONArray jsonObservations = new JSONArray();           
        if (observations != null) {
            for (Observation o : observations) {
                JSONObject jsonObservation = new JSONObject();
                try {
                    jsonObservation.put("time", TALTIONI_DATEFORMAT.format(o.getEffectiveDateTime().getTime()));
                } catch (JSONException e) {
                	throw new RuntimeException(e);
                } 
                
                ObservationItem[] items = o.getObservationItems().getObservationItem();
                for (ObservationItem item : items) {
                    if (item.getValueAsString() != null) {
                        try {
                        	// Add to JSON as number if value is numeric, as string otherwise
                        	// Taltioni stub returns item.isNumberValueSpecified() == true also in cases it cannot be used, so comparing the other way around... :(
//                            jsonObservation.put(item.getTypeId(), item.isTextValueSpecified() ? item.getValueAsString() : item.getNumberValue());
                            jsonObservation.put(item.getTypeId(), getJSONValue(item));
                        } catch (JSONException e) {
                        	throw new RuntimeException("Number value: '" + item.getNumberValue() + "'", e);
                        }
                    }
                }  // for observation items
                
//                try {
//                    jsonObservation.put("notes", o.getNotes());
//                } catch (JSONException e) {
//                	throw new RuntimeException(e);
//                }
                
                // Audit info
                AuditInfo ai = o.getAuditInfo();
                if (ai != null) {
                    DeviceInformation di = ai.getDeviceInformation();
                    if (di != null) {
                        try {
                            jsonObservation.put("DeviceType", "Sensor");
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                }
                jsonObservations.put(jsonObservation);
            }  // for observations
        }
        return jsonObservations.toString();         
	}
	
	private Object getJSONValue(ObservationItem item) {
		if (item.isNumberValueSpecified()) {
			double value = item.getNumberValue();
			if (!Double.isNaN(value)) {
				return Double.valueOf(value);
			}
		}
		return item.getValueAsString();
	}
	
	
	/*
	 * Do we want to keep the months from 0 to 11, or use months from 1 to 12?
	 * Keep the Java standard, but document it well?
	 */
	/**
	 * Return Calendar at specific date.
	 */
	public Calendar getCalendar(int year, int month, int day) {
		return getCalendar(year, month, day, 0, 0);
	}

	/**
	 * Return Calendar at specific time.
	 */
	public Calendar getCalendar(int year, int month, int day, int hour, int minute) {
		Calendar cal = getCalendar();
		cal.set(Calendar.YEAR, year);
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, day);
		cal.set(Calendar.HOUR_OF_DAY, hour);
		cal.set(Calendar.MINUTE, minute);
		return cal;
	}

	/*
	 * This allows us to adjust the time zone if required, etc.
	 */
	/**
	 * Returns Calendar now.
	 */
	public Calendar getCalendar() {
		return Calendar.getInstance();
	}
	
	public Calendar getCalendar(Date time) {
		Calendar cal = getCalendar();
		cal.setTime(time);
		return cal;
	}
	
	/**
	 * Compares two Observations and returns true if first one occurs before the second.  
	 */
	public static Comparator<Observation> ObservationComparator = new Comparator<Observation>() {
	    public int compare(Observation o1, Observation o2) {
	        return o1.getEffectiveDateTime().compareTo(o2.getEffectiveDateTime());
	    }
	};
	
	/**
     * Gets Observations of given type and sort them by time.
	 * @param token
	 * @param start
	 * @param stop
	 * @param types
	 * @return
	 * @throws RemoteException
	 * @throws TaltioniAPI_GetHealthRecordItems_ReceiverFault_FaultMessage
	 * @throws TaltioniAPI_GetHealthRecordItems_SenderFault_FaultMessage
	 */
	private Observation[] getHealthRecordObservations(AccessToken token, Calendar start, Calendar stop, ArrayOfstring types) throws RemoteException, TaltioniAPI_GetHealthRecordItems_ReceiverFault_FaultMessage, TaltioniAPI_GetHealthRecordItems_SenderFault_FaultMessage {
        RequestId requestId = getUniqueRequestID();
        Timestamp timestamp = getUTCTimestamp();
        AuthCode authCode = generateAuthCode(requestId, timestamp, token);
        GetHealthRecordItemsRequest request = new GetHealthRecordItemsRequest();
        request.setStartDateTime(start);
        request.setEndDateTime(stop);
        request.setItemTypes(types);

        GetHealthRecordItemsResponse response = TALTIONI.getHealthRecordItems(request, token, APPLICATION_ID, authCode, requestId, timestamp);
        HealthRecordData recordData = response.getHealthRecordData();
        ArrayOfObservation observationArray = recordData.getObservations();
        if (observationArray == null) return null;
        if (observationArray.getObservation() == null) return null;
        if (observationArray.getObservation().length == 0) return null;
        Arrays.sort(observationArray.getObservation(), ObservationComparator);
        return observationArray.getObservation();	    
	}

    /**
     * Imports all data from one file to Taltioni.
     * @param fileType
     *      Type of the file.<p>
     *      Currently Supported file types are:
     *      <li>Medtronic Sensor CSV file
     *      <li>Diabetesliitto Excel CSV
     *      <li>AccuChek Combo Insulin pump XML
     *      <li>AccuChek Aviva Combo meter XML
     *      <li>Dexcom Sensor XML
     *             
     * @param fileName
     *      Name of the file. This is not used to manipulate the file. Only stored to taltioni. 
     * @param reader
     *      File reader. This is used to read the contents of the file.
     * @param token
     * @return
     */
    public int storeFileValues(SensotrendFileType fileType, String fileName, BufferedReader reader, AccessToken token) {
        HealthRecordData record = null;
        switch (fileType) {
        case MOVES_JSON:
            record = MovesJSONParser.getInstance().parse(reader);
            break;
        default:
            
            break;
        }
        if (record == null) return 0;

        StringBuffer sb = new StringBuffer();
        try {
            reader.reset();
            reader.read(CharBuffer.wrap(sb));
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        StoreHealthRecordItemsRequest request = new StoreHealthRecordItemsRequest();
        request.setHealthRecordData(record);
        request.setAbortOnError(false); // at least for debugging!

        RequestId requestId = getUniqueRequestID();
        Timestamp timestamp = getUTCTimestamp();
        AuthCode authCode = generateAuthCode(requestId, timestamp, token);
        MyHandler cbHandler = new MyHandler();
        try {
            TALTIONI.startstoreHealthRecordItems(  // TODO callback handling
                    request, token, APPLICATION_ID, authCode, requestId, timestamp, cbHandler);
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
        return record.getObservations().getObservation().length;
    }
}  // class TaltioniDataAccess


class MyHandler extends TaltioniServiceCallbackHandler {
    @Override
    public void receiveErrorstoreHealthRecordItems(Exception e) {
        // TODO Auto-generated method stub
        super.receiveErrorstoreHealthRecordItems(e);
    }
    @Override
    public void receiveResultstoreHealthRecordItems(
            StoreHealthRecordItemsResponse result) {
        // TODO Auto-generated method stub
        super.receiveResultstoreHealthRecordItems(result);
    }        
}

class TypeIdMap {  // TODO these are actually ObservationTypeId's and ObervationItemTypeId's
    public static final TypeIdMap BLOOD_GLUCOSE = new TypeIdMap("BloodGlucose", null);
    public static final TypeIdMap GLUCOSE = new TypeIdMap("Glucose", "mmol/l");  // TODO This should be read from XML
    public static final TypeIdMap WEIGHT = new TypeIdMap("Weight", "kg");
    public static final TypeIdMap INSULIN_INJECTION = new TypeIdMap("InsulinInjection", null);
    public static final TypeIdMap INSULIN_INJECTION_TYPE_SLOW = new TypeIdMap("Type", "slow effect");
    public static final TypeIdMap INSULIN_INJECTION_TYPE_FAST = new TypeIdMap("Type", "fast effect");
    public static final TypeIdMap INSULIN_INJECTION_AMOUNT = new TypeIdMap("Amount", "IE");
    public static final TypeIdMap STEPS = new TypeIdMap("Steps", "");
    public static final TypeIdMap EXERCISE_STEPS = new TypeIdMap("ExerciseSteps", "");
    
    public final String name;
    public final String unit;
    
    TypeIdMap(String name, String unit) {
        this.name = name;
        this.unit = unit;
    }
}
