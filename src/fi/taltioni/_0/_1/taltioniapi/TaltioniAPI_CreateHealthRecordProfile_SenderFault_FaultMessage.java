
/**
 * TaltioniAPI_CreateHealthRecordProfile_SenderFault_FaultMessage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

package fi.taltioni._0._1.taltioniapi;

public class TaltioniAPI_CreateHealthRecordProfile_SenderFault_FaultMessage extends java.lang.Exception{

    private static final long serialVersionUID = 1375440886613L;
    
    private fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.SenderFault0 faultMessage;

    
        public TaltioniAPI_CreateHealthRecordProfile_SenderFault_FaultMessage() {
            super("TaltioniAPI_CreateHealthRecordProfile_SenderFault_FaultMessage");
        }

        public TaltioniAPI_CreateHealthRecordProfile_SenderFault_FaultMessage(java.lang.String s) {
           super(s);
        }

        public TaltioniAPI_CreateHealthRecordProfile_SenderFault_FaultMessage(java.lang.String s, java.lang.Throwable ex) {
          super(s, ex);
        }

        public TaltioniAPI_CreateHealthRecordProfile_SenderFault_FaultMessage(java.lang.Throwable cause) {
            super(cause);
        }
    

    public void setFaultMessage(fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.SenderFault0 msg){
       faultMessage = msg;
    }
    
    public fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.SenderFault0 getFaultMessage(){
       return faultMessage;
    }
}
    