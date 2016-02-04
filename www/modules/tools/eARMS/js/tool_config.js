var topicSubscribe = {
    initialize: function() 
    {
        this.bindEvents();
    },

    bindEvents: function() 
    {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },
 
    onDeviceReady: function() 
    {
        topicSubscribe.receivedEvent('deviceready');
        var pushNotification = window.plugins.pushNotification;
        console.log("userConfigJson Value " + JSON.stringify(userConfigJson));
        pushNotification.subscribeTopic(topicSubscribe.successHandlerForSubscribeTopic, topicSubscribe.errorHandlerForSubscribeTopic, userConfigJson); 
    },

    receivedEvent: function(id) 
    {
        console.log('Received Event for Tool Config: ' + id);
    },
       
    successHandlerForSubscribeTopic: function(result) 
    {
        console.log('Callback Success For Subscribe Topic! Result = '+result);      
    },
  
    errorHandlerForSubscribeTopic:function(error)
    {
        console.log(error);
    },
    
    onSubscribeNotificationGCM: function(e) 
    {
        switch(e.event)
        {
            case 'registered':
                if (e.regid.length > 0)
                {
                    console.log("Regid " + e.regid);
                    console.log('registration id = '+e.regid);
                }
                break;

            case 'message':
                console.log('message = '+e.message+' msgcnt = '+e.msgcnt + ' payload = '+JSON.stringify(e.payload));                            
                break;

            case 'error':
                console.log('GCM error = '+e.msg);
                break;

            default:
                console.log('An unknown GCM event has occurred');
                break;
        }
    }
};