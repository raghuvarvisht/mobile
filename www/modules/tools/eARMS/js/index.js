var app = {
    // Application Constructor
    initialize: function() 
    {
        this.bindEvents();
    },
    // Bind Event Listeners
    //
    // Bind any events that are required on startup. Common events are:
    // 'load', 'deviceready', 'offline', and 'online'.
    bindEvents: function() 
    {
        document.addEventListener('deviceready', this.onDeviceReady, false);
    },
    // deviceready Event Handler
    //
    // The scope of 'this' is the event. In order to call the 'receivedEvent'
    // function, we must explicity call 'app.receivedEvent(...);'
    onDeviceReady: function() 
    {
        app.receivedEvent('deviceready');
        var pushNotification = window.plugins.pushNotification;
        pushNotification.register(app.successHandler, app.errorHandler, {"senderID":"819109776891","ecb":"app.onNotificationGCM"});
    },
    // Update DOM on a Received Event
    receivedEvent: function(id) 
    {
        /*var parentElement = document.getElementById(id);
        var listeningElement = parentElement.querySelector('.listening');
        var receivedElement = parentElement.querySelector('.received');

        listeningElement.setAttribute('style', 'display:none;');
        receivedElement.setAttribute('style', 'display:block;');*/

        console.log('Received Event: ' + id);
    },
    // result contains any message sent from the plugin call
    successHandler: function(result)
    {
        console.log('Callback Success! Result = '+result);
        var pushNotification = window.plugins.pushNotification;
        pushNotification.sendMessage(app.successHandlerForSendMessage, app.errorHandler, {"senderID":"819109776891","ecb":"app.onNotificationGCM"});
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
                // this is the actual push notification. its format depends on the data model from the push server
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