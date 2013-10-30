
/**
 * TaltioniServiceCallbackHandler.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.2  Built on : Apr 17, 2012 (05:33:49 IST)
 */

    package fi.taltioni._0._1.taltioniapi;

    /**
     *  TaltioniServiceCallbackHandler Callback class, Users can extend this class and implement
     *  their own receiveResult and receiveError methods.
     */
    public abstract class TaltioniServiceCallbackHandler{



    protected Object clientData;

    /**
    * User can pass in any object that needs to be accessed once the NonBlocking
    * Web service call is finished and appropriate method of this CallBack is called.
    * @param clientData Object mechanism by which the user can pass in user data
    * that will be avilable at the time this callback is called.
    */
    public TaltioniServiceCallbackHandler(Object clientData){
        this.clientData = clientData;
    }

    /**
    * Please use this constructor if you don't want to set any clientData
    */
    public TaltioniServiceCallbackHandler(){
        this.clientData = null;
    }

    /**
     * Get the client data
     */

     public Object getClientData() {
        return clientData;
     }

        
           /**
            * auto generated Axis2 call back method for getAuthorizationInfo method
            * override this method for handling normal response from getAuthorizationInfo operation
            */
           public void receiveResultgetAuthorizationInfo(
                    fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.GetAuthorizationInfoResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getAuthorizationInfo operation
           */
            public void receiveErrorgetAuthorizationInfo(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for about method
            * override this method for handling normal response from about operation
            */
           public void receiveResultabout(
                    fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.AboutResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from about operation
           */
            public void receiveErrorabout(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for storeHealthRecordItems method
            * override this method for handling normal response from storeHealthRecordItems operation
            */
           public void receiveResultstoreHealthRecordItems(
                    fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.StoreHealthRecordItemsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from storeHealthRecordItems operation
           */
            public void receiveErrorstoreHealthRecordItems(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for updateHealthRecordProfile method
            * override this method for handling normal response from updateHealthRecordProfile operation
            */
           public void receiveResultupdateHealthRecordProfile(
                    fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.UpdateHealthRecordProfileResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from updateHealthRecordProfile operation
           */
            public void receiveErrorupdateHealthRecordProfile(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for removeApplicationAuthorization method
            * override this method for handling normal response from removeApplicationAuthorization operation
            */
           public void receiveResultremoveApplicationAuthorization(
                    fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.RemoveApplicationAuthorizationResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from removeApplicationAuthorization operation
           */
            public void receiveErrorremoveApplicationAuthorization(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getUserProfile method
            * override this method for handling normal response from getUserProfile operation
            */
           public void receiveResultgetUserProfile(
                    fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.GetUserProfileResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getUserProfile operation
           */
            public void receiveErrorgetUserProfile(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getCodes method
            * override this method for handling normal response from getCodes operation
            */
           public void receiveResultgetCodes(
                    fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.GetCodesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getCodes operation
           */
            public void receiveErrorgetCodes(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getHealthRecordProfile method
            * override this method for handling normal response from getHealthRecordProfile operation
            */
           public void receiveResultgetHealthRecordProfile(
                    fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.GetHealthRecordProfileResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getHealthRecordProfile operation
           */
            public void receiveErrorgetHealthRecordProfile(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getHealthRecordItem method
            * override this method for handling normal response from getHealthRecordItem operation
            */
           public void receiveResultgetHealthRecordItem(
                    fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.GetHealthRecordItemResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getHealthRecordItem operation
           */
            public void receiveErrorgetHealthRecordItem(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getHealthRecordItemsPaged method
            * override this method for handling normal response from getHealthRecordItemsPaged operation
            */
           public void receiveResultgetHealthRecordItemsPaged(
                    fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.GetHealthRecordItemsPagedResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getHealthRecordItemsPaged operation
           */
            public void receiveErrorgetHealthRecordItemsPaged(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getApplicationInfo method
            * override this method for handling normal response from getApplicationInfo operation
            */
           public void receiveResultgetApplicationInfo(
                    fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.GetApplicationInfoResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getApplicationInfo operation
           */
            public void receiveErrorgetApplicationInfo(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for deleteHealthRecordItems method
            * override this method for handling normal response from deleteHealthRecordItems operation
            */
           public void receiveResultdeleteHealthRecordItems(
                    fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.DeleteHealthRecordItemsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from deleteHealthRecordItems operation
           */
            public void receiveErrordeleteHealthRecordItems(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getHealthRecordItems method
            * override this method for handling normal response from getHealthRecordItems operation
            */
           public void receiveResultgetHealthRecordItems(
                    fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.GetHealthRecordItemsResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getHealthRecordItems operation
           */
            public void receiveErrorgetHealthRecordItems(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getLatestHealthRecordItem method
            * override this method for handling normal response from getLatestHealthRecordItem operation
            */
           public void receiveResultgetLatestHealthRecordItem(
                    fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.GetHealthRecordItemResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getLatestHealthRecordItem operation
           */
            public void receiveErrorgetLatestHealthRecordItem(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for createHealthRecordProfile method
            * override this method for handling normal response from createHealthRecordProfile operation
            */
           public void receiveResultcreateHealthRecordProfile(
                    fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.CreateHealthRecordProfileResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from createHealthRecordProfile operation
           */
            public void receiveErrorcreateHealthRecordProfile(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for checkPersonalIdentifier method
            * override this method for handling normal response from checkPersonalIdentifier operation
            */
           public void receiveResultcheckPersonalIdentifier(
                    fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.CheckPersonalIdentifierResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from checkPersonalIdentifier operation
           */
            public void receiveErrorcheckPersonalIdentifier(java.lang.Exception e) {
            }
                
           /**
            * auto generated Axis2 call back method for getHealthRecordItemTypes method
            * override this method for handling normal response from getHealthRecordItemTypes operation
            */
           public void receiveResultgetHealthRecordItemTypes(
                    fi.taltioni._0._1.taltioniapi.TaltioniServiceStub.GetHealthRecordItemTypesResponse result
                        ) {
           }

          /**
           * auto generated Axis2 Error handler
           * override this method for handling error response from getHealthRecordItemTypes operation
           */
            public void receiveErrorgetHealthRecordItemTypes(java.lang.Exception e) {
            }
                


    }
    