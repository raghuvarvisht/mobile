var msgRegister = {
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
        msgRegister.receivedEvent('deviceready');
        var pushNotification = window.plugins.pushNotification;
        pushNotification.register(msgRegister.successHandler, msgRegister.errorHandler, {"senderID":"819109776891","ecb":"msgRegister.onNotificationGCM"});
    },
 
    receivedEvent: function(id) 
    {
        console.log('Received Event: ' + id);
    },
 
    successHandler: function(result)
    {
        console.log('Callback Success! Result = '+result);
    },
    
    successHandlerForSendMessage: function(result) 
    {
        console.log('Callback Success For Send Message! Result = '+result);       
    },
    
    errorHandler:function(error)
    {
        console.log(error);
    },
    onNotificationGCM: function(e) 
    {
        switch( e.event )
        {
            case 'registered':
                if ( e.regid.length > 0 )
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